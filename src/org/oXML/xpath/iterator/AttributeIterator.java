package org.oXML.xpath.iterator;

import java.util.Iterator;
import org.oXML.type.Node;
import org.oXML.type.ElementNode;
import org.oXML.type.AttributeNode;
import org.oXML.type.Nodeset;
import org.oXML.type.NodeIterator;
import org.oXML.xpath.filter.NodeFilter;
import org.oXML.util.Log;

/**
 * contains all the attributes of all Elements in the parent nodeset
 */
public class AttributeIterator extends SubNodeIterator
{
    private Iterator current;

    public AttributeIterator(NodeIterator parent, NodeFilter filter){
        super(parent, filter);
    }

    protected ElementNode nextElement(){
        Node next = parent.nextNode();
        while(next != null && !next.getType().instanceOf(ElementNode.TYPE))
            next = parent.nextNode();
        if(next != null)
            next = next.cast(ElementNode.TYPE);
        return (ElementNode)next;
    }

    protected Node getNextNode(){
        if(current == null){
            // first invocation or no more attributes from this element
            ElementNode next = nextElement();
            if(next == null)
                // end of the line
                return null;
            current = next.getAttributes().iterator();
//              return getNextNode(); // in case it's empty
        }
        // current is not null
        if(!current.hasNext()){
            current = null;
            return getNextNode();
        }
        // current is not empty
        return (AttributeNode)current.next();
    }

    public String toString(){
        return super.toString()+'<'+current+'>';
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
