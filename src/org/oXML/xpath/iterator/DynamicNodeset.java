package org.oXML.xpath.iterator;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import org.oXML.xpath.filter.NodeFilter;
import org.oXML.type.Nodeset;
import org.oXML.type.NodeIterator;
import org.oXML.type.Node;
import org.oXML.util.Log;

/**
 * Nodeset where nodes can be dynamically added.
 */
public class DynamicNodeset extends ArrayList implements Nodeset{

    /**
     * does not honour filters or whatToShow
     */
    public class DynamicNodesetIterator implements NodeIterator{
        private int position;

        public DynamicNodesetIterator(){
            position = 0;
        }

        public int position(){
            return position;
        }

        public boolean hasNext(){
            return size() > position;
        }

        public Node nextNode(){
            return hasNext() ? (Node)get(position++) : null;
        }

        public NodeFilter getFilter(){
            return null;
        }

        public void setFilter(NodeFilter filter){
            throw new RuntimeException(this.getClass()+".setFilter(NodeFilter filter): operation not supported");
        }

        public void setWhatToShow(int nodetype){
            throw new RuntimeException(this.getClass()+".setWhatToShow(int nodeType): operation not supported");  
        }
        
        public int getWhatToShow(){
            return NodeFilter.SHOW_ALL;
        }
    }

    /**
     * creates empty nodeset. add nodes using appendChild() 
     * and/or appendChildset().
     */
    public DynamicNodeset(){
    }

    /**
     * creates a nodeset and adds the nodes in the specified collection.
     */
    public DynamicNodeset(Collection nodes){
        addAll(nodes);
    }

    public NodeIterator getIterator(){
        return new DynamicNodesetIterator();
    }

    public Node getNode(int index){
        return size() > index ? (Node)get(index) : null;
    }

    public void addNodeset(Nodeset nodes){
        for(int i=0; i<nodes.size(); ++i)
            add(nodes.getNode(i));
    }

    public void addNode(Node node){
	if(node.getType().instanceOf(org.oXML.type.NodesetNode.TYPE))
	    addNodeset(node.getChildNodes());
	else
            add(node);
    }

    public int indexOf(Node node){
        return super.indexOf(node);
    }

    public void removeNode(int pos){
	remove(pos);
    }

    public void insertNode(int pos, Node node){
	add(pos, node);
    }

    public String toString(){
        return getClass().getName()+'<'+size()+'>';
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
