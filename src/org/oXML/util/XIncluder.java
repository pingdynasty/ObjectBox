package org.oXML.util;

import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.oXML.util.Log;
import org.tagbox.xpath.XPathProcessor;
import org.tagbox.xpath.XPathException;

/**
 * Partial implementation of w3c <a href="http://www.w3.org/TR/xinclude/">XML Inclusions (XInclude)</a> Version 1.0.
 */
public class XIncluder
{
    private static final String namespaceURI = 
    "http://www.w3.org/2001/XInclude";
    private String basedir;

    public XIncluder() {
        this(null);
    }

    public XIncluder(String basedir) {
	if(basedir == null)
	    basedir = System.getProperty("user.dir");
	if(!basedir.endsWith(File.separator))
	    basedir += File.separator;
	this.basedir = basedir;
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

    /**
     * naive XPointer that will simply execute an xpath contained within
     * parenthesis  - xpointer(xpath).
     */
    private class XPointer {
        String xpath;

        XPointer(String xpointer) {
            xpath = xpointer.substring(xpointer.indexOf('(')+1, 
                                       xpointer.lastIndexOf(')'));
            Log.trace("xpointer <"+xpointer+"> xpath <"+xpath+">");
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

    public void process(Node source)
        throws SAXException, IOException, XPathException {
        Node next = source.getFirstChild();
        while(next != null){
            Node current = next;
            next = next.getNextSibling();
            String ns = current.getNamespaceURI();
            if(ns != null && ns.equals(namespaceURI)
               && "include".equals(current.getLocalName())){
                Element e = (Element)current;
                String href = e.getAttribute("href");
                include(e, href);
            }
            if(current.hasChildNodes())
                process(current);
        }
    }

    protected void include(Element e, String href)
        throws SAXException, IOException, XPathException {
        XPointer xpointer = null;
        int pos = href.indexOf("#");
        if(pos > 0){
            xpointer = new XPointer(href.substring(pos+1));
            href = href.substring(0, pos);
        }
        href = basedir+href;
        Document doc = getDocument(href);        
        Node imported;
        if(xpointer == null)
            imported = doc.getDocumentElement();
        else
            imported = xpointer.resolve(doc);

        if(imported == null){
            Log.warning("included infoset is empty: "+href);
            e.getParentNode().removeChild(e);
        }else{
            imported = e.getOwnerDocument().importNode(imported, true);
            process(imported);
            e.getParentNode().replaceChild(imported, e);
        }
    }
}
/*
    ObjectBox - o:XML compiler and interpretor
    for more information see http://www.o-xml.org/objectbox
    Copyright (C) 2002/2003 Martin Klang, Alpha Plus Technology Ltd
    email: martin at hack.org

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
