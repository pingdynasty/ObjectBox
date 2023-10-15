package org.oXML.xpath.iterator;

import org.oXML.type.Node;
import org.oXML.type.NodeIterator;
import org.oXML.xpath.filter.NodeFilter;

/**
 * wraps a Node in a NodeIterator.
 * can be extended by simply overloading getNode().
 */
public class SingleNodeIterator implements NodeIterator {
    private Node root;
    private boolean seen;
    private static final int whatToShow = NodeFilter.SHOW_ALL;

    public SingleNodeIterator(Node root){
        this.root = root;
        seen = false;
    }

//      public SingleNodeIterator(SingleNodeIterator other){
//          root = other.root;
//          seen = other.seen;
//      }

    public Node nextNode(){
        if(seen)
            return null;
        seen = true;
        return root;
    }

    public void setWhatToShow(int nodetype){
        throw new RuntimeException(getClass()+".setWhatToShow(int nodeType): operation not supported");  
    }

    public int getWhatToShow(){
        return whatToShow;
    }

    public void setFilter(NodeFilter filter){
        throw new RuntimeException(getClass()+".setFilter(NodeFilter filter): operation not supported");  
    }

    public NodeFilter getFilter(){
        return null;
    }

    public int position(){
        return seen ? 1 : 0;
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
