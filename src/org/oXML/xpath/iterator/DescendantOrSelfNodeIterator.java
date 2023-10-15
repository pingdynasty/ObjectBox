package org.oXML.xpath.iterator;

import java.util.List;
import java.util.ArrayList;
import org.oXML.xpath.filter.NodeFilter;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodeIterator;
import org.oXML.util.Log;

/**
 * overloads getNextNode() to return the descendants of the nodes of the parent iterator
 */
public class DescendantOrSelfNodeIterator extends SubNodeIterator {
    protected Node last;
    protected Node top;

    public DescendantOrSelfNodeIterator(NodeIterator root, NodeFilter filter){
        super(root, filter);
    }

    protected Node getNextNode(){
        if(last == null){
	    // get next from parent nodeset
	    last = parent.nextNode();
	    if(last == null)
		// end of the line
		return null;
	    top = last;
	    return last;
	}

	// get descendant
	Node next = last.getChildNodes().getNode(0);
	if(next != null){
	    last = next;
	    return last;
	}

	do{
	    // if no descendant, get next sibling
	    next = last.getNextSibling();
	    if(next != null){
		last = next;
		return last;
	    }
	    // else get parent sibling, while parent is not the start-level parent
	    last = last.getParent();
	}while(last != null && last != top);

	last = null;
	return getNextNode();
    }

    public String toString(){
        return super.toString()+'['+last+','+top+']';
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
