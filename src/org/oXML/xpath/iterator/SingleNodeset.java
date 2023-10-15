package org.oXML.xpath.iterator;

import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodeIterator;

/**
 * wraps a Node in a NodeIterator.
 * can be extended by simply overloading getNode().
 */
public class SingleNodeset implements Nodeset {
    private Node root;
    private boolean seen;

    public SingleNodeset(Node root){
        this.root = root;
        seen = false;
    }

    public Node getNode(int index){
        return index == 0 ? root : null;
    }

    public void addNode(Node node){
        throw new RuntimeException(getClass()+".addNode(Node node): operation not supported");  
    }

    public int size(){
        return 1;
    }

    public boolean isEmpty(){
        return false;
    }

    public int indexOf(Node node){
        if(root.equals(node))
            return 0;
        return -1;
    }

    public NodeIterator getIterator(){
        return new SingleNodeIterator(root);
    }

    public void removeNode(int pos){
	if(pos == 0)
	    root = null;
	else
	    throw new RuntimeException(getClass()+".removeNode(int pos): operation not supported");  
    }

    public void insertNode(int pos, Node node){
	if(pos == 0)
	    root = node;
	else
	    throw new RuntimeException(getClass()+".insertNode(int pos, Node node): operation not supported");  
    }

    public String toString(){
        return getClass().getName()+"["+root+","+seen+"]";
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
