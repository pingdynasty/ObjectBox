package org.oXML.extras.xinclude;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.oXML.type.Name;
import org.oXML.ObjectBoxException;
import org.oXML.engine.CompilationContext;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.NullTemplate;
import org.oXML.engine.mapping.dom.TemplateMapping;
import org.oXML.util.Log;
import org.tagbox.xpath.XPathProcessor;
import org.tagbox.xpath.XPathException;

public class XIncludeMapping implements TemplateMapping {

    public static final Name NAME = 
	new Name(XIncludeExtensions.XINCLUDE_NS, "include");

    private String basedir;

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

	String href = e.getAttribute("href");
	basedir = env.getSystemId();
	basedir = basedir == null ? null : new File(basedir).getParent();
	if(basedir == null)
	    basedir = System.getProperty("user.dir");
	if(!basedir.endsWith(File.separator))
	    basedir += File.separator;

	Node included;
	try{
	    included = include(e, href);
	}catch(Exception exc){
	    throw new ObjectBoxException("error following xinclude: "+href, exc);
	}

	if(included == null){
            Log.warning("XInclude: included infoset is empty: "+href);
	    return new NullTemplate();
	}

	try{
	    return env.compile(included);
	}catch(ObjectBoxException exc){
	    Log.error("in included file: "+href);
	    throw exc;
	}
    }

    protected Node include(Element e, String href)
        throws SAXException, IOException, XPathException {
        XPointer xpointer = null;
        int pos = href.indexOf("#");
        if(pos > 0){
            xpointer = new XPointer(href.substring(pos+1));
            href = href.substring(0, pos);
        }
        href = basedir+href;
        Document doc = getDocument(href);
        if(xpointer == null)
            return doc.getDocumentElement();
        else
            return xpointer.resolve(doc);
    }

    /**
     * naive XPointer that will simply execute an xpath contained within
     * parenthesis  - xpointer(xpath).
     */
    private class XPointer {
        String xpath;

        XPointer(String xpointer) {
            xpath = xpointer.substring(xpointer.indexOf('(')+1, 
                                       xpointer.lastIndexOf(')'));
        }

        /**
         * @return DocumentFragment holding all matching nodes, or null.
         */
        public Node resolve(Node in)
            throws XPathException {
            NodeList list = 
                XPathProcessor.getInstance().selectNodeList(in, xpath);

            if(list.getLength() < 1)
                return null;
            if(list.getLength() == 1)
                return list.item(0);
                
            Document doc = in.getOwnerDocument();
            if(doc == null)
                doc = (Document)in;
            Node result = doc.createDocumentFragment();
            for(int i=0; i<list.getLength(); ++i)
                result.appendChild(list.item(i));
                
            return result;
        }

        public String toString() {
            return xpath;
        }
    }

    private Document getDocument(String href)
        throws SAXException, IOException {
	URL url;
	Document doc;
	try{
	    try{
		// try absolute URL
		url = new URL(href);
	    }catch(MalformedURLException exc){
		// try relative (of program system id) URL
		url = new URL(basedir);
		url = new URL(url, href);
	    }
	    doc = getDocument(url);
	}catch(Exception exc){
	    // try for system resource
	    url = ClassLoader.getSystemResource(href);
	    if(url == null){
		// not in system classpath, try context classloader
		ClassLoader loader = 
		    Thread.currentThread().getContextClassLoader();
		url = loader.getResource(href);
	    }
	    if(url == null)
		throw new SAXException
		    ("missing or invalid include file: "+href, exc);
	    try{
		doc = getDocument(url);
	    }catch(SAXException nexc){
		throw new SAXException
		    ("missing or invalid include file: "+href, nexc);
	    }catch(IOException nexc){
		throw new SAXException
		    ("missing or invalid imported file: "+href, nexc);
	    }
	}
	Log.trace("xincluding: "+url);
	return doc;
    }

    private Document getDocument(URL url)
        throws SAXException, IOException {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setNamespaceAware(true);
	Document xml;
	try {
	    xml = factory.newDocumentBuilder().parse(url.toString());
	}catch(ParserConfigurationException exc){
	    throw new SAXException(exc);
	}
	return xml;
    }
}
