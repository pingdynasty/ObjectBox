package org.oXML.xpath.iterator;

import java.util.Set;
import java.util.HashSet;
import org.oXML.type.Node;
import org.oXML.type.NodeIterator;
import org.oXML.xpath.filter.NodeFilter;
import org.oXML.util.Log;

public class UnionIterator implements NodeIterator{
    private NodeIterator first;
    private NodeIterator second;
    private Set visited;
    private boolean switched;
    private int position = 0;

    private static final int whatToShow = NodeFilter.SHOW_ALL;

    /**
     * construct a NodeIterator that is the union of two other nodesets
     */
    public UnionIterator(NodeIterator first, NodeIterator second){
        this.first = first;
        this.second = second;
        visited = new HashSet();
//          Log.trace(second.size()+" union "+first.size());
//          Log.trace(first.size()+" union "+second.size());
        switched = false;
    }

    public int position(){
        return position;
    }

    public NodeFilter getFilter(){
        return null;
    }

    public void setFilter(NodeFilter filter){
        throw new RuntimeException(getClass()+".setFilter(NodeFilter filter): operation not supported");
    }

    public void setWhatToShow(int nodetype){
        throw new RuntimeException(getClass()+".setWhatToShow(int nodeType): operation not supported");  
    }

    public int getWhatToShow(){
        return whatToShow;
    }

//      public Node getNextNode(){
//          Node result = first.nextNode();
//          if(result == null && second != null){
//              // switch over to the next nodeset
//              first = second;
//              second = null;
//              result = first.nextNode();
//          }
//          return result;
//      }

    public Node nextNode(){
        Node result;
        if(switched){
            result = second.nextNode();
            if(result != null && visited.contains(result))
                // this node is a duplicate from the first nodeset
                return nextNode();
        }else{
            result = first.nextNode();
            if(result == null){
                // switch over to the next nodeset
                switched = true;
//                  result = second.firstNode();
                return nextNode(); // filter duplicates
            }
        }
        ++position;
        visited.add(result);
        return result;
    }

    public String toString()
    {
        return super.toString()+"["+first+'|'+second+"]";
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
