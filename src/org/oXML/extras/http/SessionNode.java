package org.oXML.extras.http;

import java.util.Enumeration;
import javax.servlet.http.HttpSession;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.MapNode;
import org.oXML.type.StringNode;
import org.oXML.type.NumberNode;
import org.oXML.type.NodesetNode;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;
import org.oXML.engine.LanguageExtension;
import org.oXML.extras.http.function.*;

/**
 * Extension of MapNode (o:XML Map type) that overloads get, put and remove

 */
public class SessionNode extends MapNode {

    private HttpSession session;

    public SessionNode(HttpSession session){
	this.session = session;
    }

    public Node put(Node key, Node value){
	String strkey = key.stringValue();
	session.setAttribute(strkey, value);
        return this;
    }

    public Node get(Node key){
	String strkey = key.stringValue();
	Node value = (Node)session.getAttribute(strkey);
	return value == null ? NodesetNode.EMPTY_SET : value;
    }
  
    public Node remove(Node key){
	String strkey = key.stringValue();
	Node value = (Node)session.getAttribute(strkey);
	session.removeAttribute(strkey);
	return value == null ? NodesetNode.EMPTY_SET : value;
    }
  
    public Node size(){
	int size = 0;
	Enumeration anames = session.getAttributeNames();
	while(anames.hasMoreElements()){
	    size++;
	    anames.nextElement();
	}
	return new NumberNode(size);
    }
  
    public Node keys(){
	Nodeset keys = new org.oXML.xpath.iterator.DynamicNodeset();
	Enumeration anames = session.getAttributeNames();
	while(anames.hasMoreElements()){
	    String key = (String)anames.nextElement();
	    keys.addNode(new StringNode(key));
	}
	return new NodesetNode(keys);
    }
  
    public Node values(){
	Nodeset values = new org.oXML.xpath.iterator.DynamicNodeset();
	Enumeration anames = session.getAttributeNames();
	while(anames.hasMoreElements()){
	    String key = (String)anames.nextElement();
	    Node value = (Node)session.getAttribute(key);
	    values.addNode(value);
	}
	return new NodesetNode(values);
    }
  
    public Node nodes(){
	return values();
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
