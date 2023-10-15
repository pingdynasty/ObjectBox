package org.oXML.xpath.iterator;

import java.util.List;
import java.util.ArrayList;
import org.oXML.xpath.filter.NodeFilter;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodeIterator;

/**
 * overloads getNextNode() to return the following nodes of the parent iterator
 */
public class FollowingIterator extends SubNodeIterator{
    private Node ancestor_or_self;
    private Node following_sibling;
    private Node descendant_or_self;
    private List saved;

    public FollowingIterator(NodeIterator root, NodeFilter filter){
        super(root, filter);
	saved = new ArrayList();
    }

    // ancestor-or-self::*/following-sibling::*/descendant-or-self::*

    protected Node getAncestorOrSelf(){
        if(ancestor_or_self == null){
            ancestor_or_self = parent.nextNode();
	    return ancestor_or_self;
        }
	ancestor_or_self = ancestor_or_self.getParent();
	if(ancestor_or_self == null)
            ancestor_or_self = parent.nextNode();
	return ancestor_or_self;
    }

    protected Node getFollowingSibling(){
	if(following_sibling == null){
	    following_sibling = getAncestorOrSelf();
	    if(following_sibling == null)
		return null;
	}
	following_sibling = following_sibling.getNextSibling();
	if(following_sibling == null)
	    return getFollowingSibling();
	return following_sibling;
    }

    protected Node getDescendantOrSelf(){
	if(descendant_or_self == null){
	    descendant_or_self = getFollowingSibling();
	    return descendant_or_self;
	}
	// descend
	Nodeset kids = descendant_or_self.getChildNodes();
	if(kids.size() == 0){
	    // try saved ones
	    if(!saved.isEmpty()){
		descendant_or_self = (Node)saved.remove(0);
		// get a sibling
		Node sibling = descendant_or_self.getNextSibling();
		if(sibling != null)
		    // save that
		    saved.add(0, sibling);
		// and return
		return descendant_or_self;
	    }else{
		descendant_or_self = null;
		return getDescendantOrSelf();
	    }
	}else{
	    descendant_or_self = kids.getNode(0);
	    // get a sibling
	    Node sibling = descendant_or_self.getNextSibling();
	    if(sibling != null)
		// save that
		saved.add(0, sibling);
	    // and return
	    return descendant_or_self;
	}
    }

    protected Node getNextNode(){
	return getDescendantOrSelf();
    }

    public String toString(){
     return super.toString()+'['+ancestor_or_self+','+following_sibling+','+descendant_or_self+','+saved+']';
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
