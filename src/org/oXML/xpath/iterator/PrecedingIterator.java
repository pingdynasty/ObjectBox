package org.oXML.xpath.iterator;

import java.util.List;
import java.util.ArrayList;
import org.oXML.xpath.filter.NodeFilter;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodeIterator;

/**
 * overloads getNextNode() to return the preceding nodes of the parent iterator
 */
public class PrecedingIterator extends SubNodeIterator{
    private Node ancestor_or_self;
    private Node preceding_sibling;
    private Node descendant_or_self;
    private List saved;

    public PrecedingIterator(NodeIterator root, NodeFilter filter){
        super(root, filter);
	saved = new ArrayList();
    }

    // ancestor-or-self::*/preceding-sibling::*/descendant-or-self::*

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

    protected Node getPrecedingSibling(){
	if(preceding_sibling == null){
	    preceding_sibling = getAncestorOrSelf();
	    if(preceding_sibling == null)
		return null;
	}
	preceding_sibling = preceding_sibling.getPreviousSibling();
	if(preceding_sibling == null)
	    return getPrecedingSibling();
	return preceding_sibling;
    }

    protected Node getDescendantOrSelf(){
	if(descendant_or_self == null){
	    descendant_or_self = getPrecedingSibling();
	    return descendant_or_self;
	}
	// descend
	Nodeset kids = descendant_or_self.getChildNodes();
	if(kids.size() == 0){
	    // try saved ones
	    if(!saved.isEmpty()){
		descendant_or_self = (Node)saved.remove(0);
		// get a sibling
		Node sibling = descendant_or_self.getPreviousSibling();
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
	    descendant_or_self = kids.getNode(kids.size()-1);
	    // get a sibling
	    Node sibling = descendant_or_self.getPreviousSibling();
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
     return super.toString()+'['+ancestor_or_self+','+preceding_sibling+','+descendant_or_self+','+saved+']';
    }

}
/*
 * The Pingdynasty Software License, Version 1
 *
 * Copyright (c) 2000-2002 Pingdynasty / Alpha Plus Technology Ltd.  
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:  
 *       "This product includes software developed by 
 *        Pingdynasty (http://www.pingdynasty.com/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "ObjectBox" and "Pingdynasty" must not be used to endorse 
 *    or promote products derived from this software without prior 
 *    written permission.
 *    For written permission, please contact mars@pingdynasty.org.
 *
 * 5. Products derived from this software may not be called "ObjectBox",
 *    nor may "ObjectBox" appear in their name, without prior written
 *    permission.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
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
