package org.oXML.xpath;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.*;

/**
 * test harness for ParserFilter
 */
public class Filterer {

    public static void main(String[] args)
	throws Exception {

	Result result;
	if(args.length == 1)
	    result = new StreamResult(System.out);
	else if(args.length == 2)
		result = new StreamResult(args[1]);
	else {
	    System.err.println("usage: java "+
			       Filterer.class.getName()+
			       " in-file [out-file]");
	    return;
	}
	String in = args[0];

	try{
	    SAXParserFactory sfactory = SAXParserFactory.newInstance();
	    sfactory.setNamespaceAware(true);
	    SAXParser parser = sfactory.newSAXParser();

	    XMLReader reader = parser.getXMLReader();
// 	XMLReader reader = XMLReaderFactory.createXMLReader();
	    reader = new ParserFilter(reader);
	    reader.setFeature("http://xml.org/sax/features/namespaces", true);
	    reader.setFeature("http://xml.org/sax/features/namespace-prefixes", false);
// 	    SAXTransformerFactory factory =
// 		(SAXTransformerFactory)SAXTransformerFactory.newInstance();
// 	    TransformerHandler handler = factory.newTransformerHandler();
// 	    handler.setResult(new StreamResult(out));
// 	    reader.setContentHandler(handler);
// 	    reader.parse(in);
	    SAXSource source = new SAXSource(reader, new InputSource(in));
	    Transformer transformer = 
		TransformerFactory.newInstance().newTransformer();
	    transformer.transform(source, result);
	}catch(SAXException exc){
	    exc.printStackTrace();
	    Exception nested = exc.getException();
	    if(nested != null)
		nested.printStackTrace();
	}
    }
}