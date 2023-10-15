package org.oXML.extras.java.function;

import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.type.NodesetNode;
import org.oXML.type.NodeIterator;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.function.XPathFunction;
import org.oXML.util.Log;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.oXML.ObjectBoxException;

public class ParseFunction extends XPathFunction{

    private DocumentBuilder builder;
    private Converter converter;
    private static final String XMLDECL = "<?xml";

    public ParseFunction(Name name, Type[] sig)
	throws ObjectBoxException{
        super(name, sig);
	try{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    builder = factory.newDocumentBuilder();
	}catch(ParserConfigurationException exc){
	    throw new ObjectBoxException(exc);
	}
	converter = new Converter();
    }

    public Node eval(Node args[], RuntimeContext ctxt)
        throws XPathException{
	try{
	    String xml;
	    if(args[0].getType().equals(NodesetNode.TYPE)){
		xml = "";
		// get the string values of all child nodes, not just the first one
		NodeIterator it = args[0].getChildNodes().getIterator();
		for(Node kid = it.nextNode(); kid != null; kid = it.nextNode())
		    xml += kid.stringValue(); 
	    }else{
		xml = args[0].stringValue();
	    }
	    boolean hasRoot = xml.length() >= XMLDECL.length() &&
		xml.substring(0, XMLDECL.length()).equalsIgnoreCase(XMLDECL);
	    if(!hasRoot)
		xml = "<root>"+xml+"</root>";

	    org.w3c.dom.Document doc = builder.parse(new InputSource(new StringReader(xml)));
	    org.w3c.dom.Node result;
	    if(hasRoot){
		result = doc;
	    }else{
		// loose the <root> element
		result = doc.createDocumentFragment();
		org.w3c.dom.Node node = doc.getDocumentElement();
		while(node.hasChildNodes()) {
		    result.appendChild(node.removeChild(node.getFirstChild()));
		}
	    }
	    return converter.toNative(result);
	}catch(SAXException exc){
	    throw new XPathException(exc);
	}catch(IOException exc){
	    throw new XPathException(exc);
	}	
  }

    public Node eval(Node node, Node args[], RuntimeContext ctxt)
        throws XPathException{
        return eval(args, ctxt);
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
