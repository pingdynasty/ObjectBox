package org.oXML.engine;

import java.util.Map;
import java.util.Iterator;
import java.util.Collection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.oXML.type.Name;
import org.oXML.util.Log;
import org.oXML.type.AttributeNode;
import org.oXML.type.DocumentNode;

public class DOMResultHandler implements ResultHandler{

    private Document doc;
    private Node current;
    private NamespaceHandler nsHandler;

    public static Document newDocument(){
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setNamespaceAware(true);
	try{
	    return factory.newDocumentBuilder().newDocument();
	}catch(ParserConfigurationException exc){
	    throw new RuntimeException(exc.toString());
	}
    }

    public DOMResultHandler(){
        this(newDocument());
    }

    public DOMResultHandler(Document doc){
        this.doc = doc;
        current = doc;
	nsHandler = new NamespaceHandler();
    }

    public DOMResultHandler(Node node){
        doc = node.getOwnerDocument();
        if(doc == null)
            doc = (Document)node;
        current = node;
	nsHandler = new NamespaceHandler();
    }

    public Object getResult(){
        return current;
    }

    public void text(String text){
        Node result = doc.createTextNode(text);
        current.appendChild(result);
    }

    public void comment(String text){
        Node result = doc.createComment(text);
        current.appendChild(result);
    }

    public void processingInstruction(String target, String data){
        current.appendChild(doc.createProcessingInstruction(target, data));
    }

    public String print(Name name){
	String ns = name.getNamespaceURI();
	if(ns.equals(Name.EMPTY_NAMESPACE))
	    return name.getLocalName();
	String prefix = nsHandler.getPrefix(ns);
        if(prefix.equals(Name.NO_PREFIX) || 
           name.getLocalName().equals(NamespaceHandler.XMLNS_PREFIX)){
	    return name.getLocalName();
	}
	return prefix+":"+name.getLocalName();
    }

    public void startElement(Name name, Collection attributes){
	nsHandler.startElement(name, attributes);
        Element result;
	String ns = name.getNamespaceURI();
        String prefix = nsHandler.getPrefix(ns);
	if(ns.equals(Name.EMPTY_NAMESPACE)){
	    result = doc.createElement(name.getLocalName());
        }else if(prefix == null){
            // create a new prefix
            prefix = nsHandler.createNamespaceDeclaration(name);
            result = doc.createElementNS(ns, prefix+":"+name.getLocalName());
            // print the new namespace declaration
            result.setAttributeNS(NamespaceHandler.XMLNS_NAMESPACE, NamespaceHandler.XMLNS_PREFIX+":"+prefix, ns);
        }else{
	    result = doc.createElementNS(ns, print(name));
        }
        Iterator it = attributes.iterator();
        while(it.hasNext()){
            AttributeNode attr = (AttributeNode)it.next();
	    name = attr.getName();
	    ns = name.getNamespaceURI();
            prefix = nsHandler.getPrefix(ns);
	    if(ns.equals(Name.EMPTY_NAMESPACE)){
		result.setAttribute(name.getLocalName(), attr.getValue());
            }else if(prefix == null){
		// create a new prefix
		prefix = nsHandler.createNamespaceDeclaration(name);
		// print the new namespace declaration
                result.setAttributeNS(NamespaceHandler.XMLNS_NAMESPACE, NamespaceHandler.XMLNS_PREFIX+":"+prefix, ns);
                result.setAttributeNS(ns, prefix+":"+name.getLocalName(), attr.getValue());
            }else{
                result.setAttributeNS(ns, print(name), attr.getValue());
            }
        }
        current.appendChild(result);
        current = result;
    }

    public void endElement(Name name){
        current = current.getParentNode();
	nsHandler.endElement();
    }

    // to be done! ->
    public void start(String contenttype){}
    public void end(){}
//     public void startDocument() {}
//     public void endDocument() {}
    public void startDocumentType(String name, String publicId, String systemId,
				  String internalSubset) {}
    public void endDocumentType() {}
    public void notationDeclaration(String name, String publicId, 
				    String systemId) {}
    public void entityDeclaration(String name, String publicId, 
				  String systemId, String notationName) {}

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
