package org.oXML.xpath;

import java.util.Map;
import java.util.HashMap;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ContentHandler;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.AttributesImpl;
import org.oXML.type.Name;
import org.oXML.xpath.parser.MixedExpressionParser;
import org.oXML.xpath.parser.ExpressionParser;
import org.oXML.xpath.parser.Parser;
import org.oXML.xpath.parser.ParserException;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

public class ParserFilter extends XMLFilterImpl {
    private static final String OXML_NS = "http://www.o-xml.org/lang/";
    private static final String XMLNS_NS = "http://www.w3.org/2000/xmlns/";
    private static final Attributes EMPTY_ATTS = new AttributesImpl();

    private Parser eparser = new ExpressionParser();
    private Parser mparser = new MixedExpressionParser();
    private Map names;

    // state keeping fields
    private SimpleResolver resolver; // this is where we keep track of ns uris


    public ParserFilter(){
	init();
    }

    public ParserFilter(XMLReader parent){
	super(parent);
	init();
    }

    class WildCard {
	public boolean equals(Object o){
	    return true;
	}
    }

    private void init(){
	resolver = new SimpleResolver();
	names = new HashMap();

	// set up element/attribute -> parser mappings

	// for-each
	Map map = new HashMap();
	map.put("select", eparser);
	map.put("from", eparser);
	map.put("to", eparser);
	map.put("step", eparser);
	names.put(new Name(OXML_NS, "for-each"), map);

	// log
	map = new HashMap();
	map.put("select", eparser);
	map.put("msg", mparser);
	names.put(new Name(OXML_NS, "log"), map);

	// variable
	map = new HashMap();
	map.put("select", eparser);
	names.put(new Name(OXML_NS, "variable"), map);

	// while, if, when
	map = new HashMap();
	map.put("test", eparser);
	names.put(new Name(OXML_NS, "while"), map);
	names.put(new Name(OXML_NS, "if"), map);
	names.put(new Name(OXML_NS, "when"), map);

	// do, eval, return
	map = new HashMap();
	map.put("select", eparser);
	names.put(new Name(OXML_NS, "do"), map);
	names.put(new Name(OXML_NS, "eval"), map);
	names.put(new Name(OXML_NS, "return"), map);

	// element
	map = new HashMap();
	map.put("name", mparser);
	names.put(new Name(OXML_NS, "element"), map);

	// attribute
	map = new HashMap();
	map.put("name", mparser);
	map.put("select", eparser);
	names.put(new Name(OXML_NS, "attribute"), map);

	// processing-instruction
	map = new HashMap();
	map.put("target", mparser);
	map.put("select", eparser);
	names.put(new Name(OXML_NS, "processing-instruction"), map);

	// comment
	map = new HashMap();
	map.put("select", eparser);
	names.put(new Name(OXML_NS, "comment"), map);

	// throw
	map = new HashMap();
	map.put("select", eparser);
	names.put(new Name(OXML_NS, "throw"), map);

	// assert
	map = new HashMap();
	map.put("test", eparser);
	map.put("msg", mparser);
	names.put(new Name(OXML_NS, "assert"), map);
    }

    public void startPrefixMapping(String prefix,
				   String uri)
	throws SAXException{
	resolver.addNamespace(prefix, uri);
	super.startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix)
	throws SAXException{
	resolver.removeNamespace(prefix);
	super.endPrefixMapping(prefix);
    }

    /**
     * if there is a mapping associated with the element name,
     * check if any attributes are matched with a parser
     */
    public void startElement(String uri, String local, 
			     String qname, Attributes atts)
	throws SAXException{
	// handle o:set as a special case
	if(OXML_NS.equals(uri) && "set".equals(local)){
	    // <o:set name="xx"> <!-- repeated for each variable -->
	    //    ... expression ...
	    // </o:set>
	    for(int i=0; i<atts.getLength(); ++i){
		if(!XMLNS_NS.equals(atts.getURI(i))){
		    // create a pruned copy of the attributes list
		    AttributesImpl pruned = new AttributesImpl();
		    for(int j=0; j<atts.getLength(); ++j){
			if(XMLNS_NS.equals(atts.getURI(j)))
			    // copy over ns declarations
			    pruned.addAttribute(atts.getURI(j), atts.getLocalName(j),
						atts.getQName(j), atts.getType(j), 
						atts.getValue(j));
		    }
		    // add in this name
		    pruned.addAttribute(null, "name", "name", null, atts.getQName(i));
		    super.startElement(uri, local, qname, pruned);
		    ContentHandler handler = getParent().getContentHandler();
		    String unparsed = atts.getValue(i);
		    Expression expr;
		    try{
			expr = eparser.parse(unparsed);
			expr.bind(resolver);
		    }catch(ParserException exc){
			throw new SAXException(exc);
		    }catch(ObjectBoxException exc){
			throw new SAXException(exc);
		    }
		    expr.write(handler);
		}
	    }
	}else{
	    Name name = new Name(uri, local);
	    Map map = (Map)names.get(name);
	    if(map == null){
		super.startElement(uri, local, qname, atts);
	    }else{
		// create a pruned copy of the attributes list
		AttributesImpl pruned = new AttributesImpl();
		for(int i=0; i<atts.getLength(); ++i){
		    if(!map.containsKey(atts.getLocalName(i)))
			pruned.addAttribute(atts.getURI(i), atts.getLocalName(i),
					    atts.getQName(i), atts.getType(i), 
					    atts.getValue(i));
		}
		// propagate this element without the matching attributes
		super.startElement(uri, local, qname, pruned);
		for(int i=0; i<atts.getLength(); ++i){
		    String nm = atts.getLocalName(i);
		    Parser parser = (Parser)map.get(nm);
		    if(parser != null){
			ContentHandler handler = getParent().getContentHandler();
			String unparsed = atts.getValue(i);
			Expression expr;
			try{
			    expr = parser.parse(unparsed);
			    expr.bind(resolver);
			}catch(ParserException exc){
			    throw new SAXException(exc);
			}catch(ObjectBoxException exc){
			    throw new SAXException(exc);
			}
			handler.startElement(OXML_NS, nm, "o:"+nm, EMPTY_ATTS);
			expr.write(handler);
			handler.endElement(OXML_NS, nm, "o:"+nm);
		    }
		}
	    }
	}
    }
}