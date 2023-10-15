package org.oXML.extras.java.function;

import java.util.Collection;
import java.util.Collections;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.oXML.type.*;
import org.oXML.xpath.iterator.DynamicNodeset;
import org.oXML.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Text;

import org.oXML.engine.ResultHandler;
import org.oXML.ObjectBoxException;
import org.oXML.engine.DOMResultHandler;
import org.oXML.engine.ResultDirector;

public class Converter{

    private Document doc = newDocument();

    private static Document newDocument(){
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	try{
	    return factory.newDocumentBuilder().newDocument();
	}catch(ParserConfigurationException exc){
	    throw new RuntimeException(exc.toString());
	}
    }

    public DocumentFragment fromNative(Node node)
        throws ObjectBoxException{
        DocumentFragment frag = doc.createDocumentFragment();
        ResultHandler handler = new DOMResultHandler(frag);
        ResultDirector dir = new ResultDirector(handler);
        output(dir, node);
        return frag;
    }

//      public static org.w3c.dom.Node fromNative(Nodeset set)
//          throws ObjectBoxException{
//          org.w3c.dom.Node frag = doc.createDocumentFragment();
//          ResultHandler handler = new DOMResultHandler(frag);
//          ResultDirector dir = new ResultDirector(handler);
//          NodeIterator it = set.getIterator();
//          for(Node n = it.nextNode(); n != null; n = it.nextNode())
//              output(dir, n);
//          return frag;
//      }

    protected void output(ResultDirector dir, Node node)
        throws ObjectBoxException{
        dir.output(node);
        NodeIterator it = node.getChildNodes().getIterator();
        for(Node n = it.nextNode(); n != null; n = it.nextNode())
            output(dir, n);
        if(node.getType().instanceOf(ElementNode.TYPE)){
            ElementNode element = (ElementNode)node.cast(ElementNode.TYPE);
            dir.pop(element.getName());
        }
    }

    public Node toNative(org.w3c.dom.Node dom){
        switch(dom.getNodeType()){
            case org.w3c.dom.Node.TEXT_NODE:
            case org.w3c.dom.Node.CDATA_SECTION_NODE:
                return toString(dom);
            case org.w3c.dom.Node.ELEMENT_NODE:
                return toElement((Element)dom);
            case org.w3c.dom.Node.COMMENT_NODE:
                return toComment(dom);
            case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
                return toProcessingInstruction((ProcessingInstruction)dom);
            case org.w3c.dom.Node.DOCUMENT_NODE:
                return toDocument((Document)dom);
            case org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE:
                Nodeset result = new DynamicNodeset();
                addKids(result, dom);
                return new NodesetNode(result);
            case org.w3c.dom.Node.DOCUMENT_TYPE_NODE:
                return null;
            default:
                Log.warning("not implemented: "+dom);
                return null;
        }
    }

    protected void addKids(Nodeset parent, org.w3c.dom.Node dom){
        for(org.w3c.dom.Node kid = dom.getFirstChild(); 
            kid != null; kid = kid.getNextSibling()){
            Node baby = toNative(kid);
            if(baby != null)
                parent.addNode(baby);
        }
    }

    public DocumentNode toDocument(Document dom){
        DocumentNode doc = new DocumentNode();
        addKids(doc.getChildNodes(), dom);
        return doc;
    }

    public ElementNode toElement(Element dom){
        String ns = dom.getNamespaceURI();
	Name name = ns == null ? new Name(dom.getNodeName()) 
	    : new Name(ns, dom.getLocalName(), dom.getPrefix());
        ElementNode result = new ElementNode(name);

        // add the attributes
        org.w3c.dom.NamedNodeMap map = dom.getAttributes();
        for(int i = 0; i < map.getLength(); i++){
            org.w3c.dom.Node attr = map.item(i);
	    ns = attr.getNamespaceURI();
	    name = ns == null ? new Name(attr.getNodeName()) 
		: new Name(ns, attr.getLocalName(), attr.getPrefix());
            result.setAttribute(name, attr.getNodeValue());
        }

        // add the kids
        addKids(result.getChildNodes(), dom);
        return result;
    }

    public StringNode toString(org.w3c.dom.Node dom){
        return new StringNode(dom.getNodeValue());
    }

    public CommentNode toComment(org.w3c.dom.Node dom){
        return new CommentNode(dom.getNodeValue());
    }

    public ProcessingInstructionNode toProcessingInstruction(ProcessingInstruction dom){
        return new ProcessingInstructionNode(dom.getTarget(), dom.getData());
    }

    public Node toComment(String data){
        return new CommentNode(data);
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
