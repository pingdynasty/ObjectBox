package org.oXML.xpath.iterator;

import java.util.List;
import java.util.ArrayList;
import org.oXML.util.Log;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodeIterator;
import org.oXML.xpath.filter.NodeFilter;

/**
 */
public abstract class AbstractNodeset implements Nodeset {
    private List nodes;
    private NodeIterator iterator;

    public class AbstractNodesetIterator implements NodeIterator {
        private int position;

        public AbstractNodesetIterator(){
            position = 0;
        }

        public int position(){
            return position;
        }

        public boolean hasNext(){
            if(nodes.size() > position)
		return true;
	    Node next = iterator.nextNode();
	    if(next == null)
		return false;
	    nodes.add(next);
	    return true;
        }

        public Node nextNode(){
            return hasNext() ? getNode(position++) : null;
        }
    }

    /**
     * create a nodeset driven by this iterator
     */
    public AbstractNodeset(NodeIterator iterator){
        this.iterator = iterator;
        nodes = new ArrayList();
    }

    /**
     * step forward using the iterator and save the result node
     */
    protected Node nextNode(){
        Node node = iterator.nextNode();
	if(node != null)
            nodes.add(node);
	return node;
    }

    public NodeIterator getIterator(){
        return new AbstractNodesetIterator();
    }

    public int size(){
        // fast forward until end of iterator
	while(nextNode() != null);
        return nodes.size();
    }

    /**
     * position the nodeset at the given index and return the indexed node
     * @param index position of node in nodeset, starting at 0
     */
    public Node getNode(int index){
	assert index >= 0 : index;
        if(index < nodes.size()){
            // the node has been visited and is cached
            return (Node)nodes.get(index);
        }
        // the node asked for has not been visited yet
 	while(nodes.size() < index)
	    if(nextNode() == null)
		return null;
	return nextNode();
    }

    public boolean isEmpty(){
	if(nodes.isEmpty())
	    return nextNode() == null;
	return false;
    }

    public boolean contains(Node node){
        if(nodes.contains(node))
            return true;
        // the node asked may not have been visited yet
        for(Node next = iterator.nextNode(); next != null; next = iterator.nextNode()){
            nodes.add(next);
            if(next.equals(node))
                return true;
        }
        return false;
    }

    public int indexOf(Node node){
        int pos = nodes.indexOf(node);
        if(pos != -1)
            return pos;
        // the node asked may not have been visited yet
        for(Node next = iterator.nextNode(); next != null; next = iterator.nextNode()){
            nodes.add(next);
            if(next.equals(node))
                return iterator.position() - 1;
        }
        return -1;
    }

//     public boolean contains(Node node){
//         return contains((Object)node);
//     }

//     public int indexOf(Node node){
//         return indexOf((Object)node);
//     }

    public void addNode(Node node){
        throw new RuntimeException("operation not supported: "+getClass()+".addNode(Node node)");
    }

    public void removeNode(int pos){
        throw new RuntimeException("operation not supported: "+getClass()+".removeNode(int pos)");
// 	nodes.remove(pos);
    }

    public void insertNode(int pos, Node node){
        throw new RuntimeException("operation not supported: "+getClass()+".insertNode(int pos, Node node)");
// 	nodes.add(pos, node);
    }

    public String toString(){
        return getClass().getName()+'<'+iterator+'>'+'['+nodes+']';
    }

//      public Object remove(int index){
//          throw new NotImplementedException(getClass().getName()+".remove(int index)");
//      }

//      public boolean remove(Object o){
//          throw new NotImplementedException(getClass().getName()+".remove(Object o)");
//      }

//      public boolean removeAll(Collection c){
//          throw new NotImplementedException(getClass().getName()+".removeAll(Collection c)");
//      }

//      public boolean retainAll(Collection c){
//          throw new NotImplementedException(getClass().getName()+".retainAll(Collection c)");
//      }

//      public Object set(int index, Object element){
//          throw new NotImplementedException(getClass().getName()+".set(int index, Object element)");
//      }

//      public List subList(int fromIndex, int toIndex){
//          throw new NotImplementedException(getClass().getName()+".subList(int fromIndex, int toIndex)");
//      }

//      public Object[] toArray(){
//          throw new NotImplementedException(getClass().getName()+".toArray()");
//      }

//      public Object[] toArray(Object[] array){
//          throw new NotImplementedException(getClass().getName()+".toArray()");
//      }
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
