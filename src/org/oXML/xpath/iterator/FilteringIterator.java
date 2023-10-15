package org.oXML.xpath.iterator;

import org.oXML.xpath.XPathException;
import org.oXML.xpath.filter.NodeFilter;
import org.oXML.type.Node;
import org.oXML.type.NodeIterator;
import org.oXML.util.Log;

/**
 */
public class FilteringIterator implements NodeIterator{
    private int position = 0;
    private NodeIterator parent;
    private NodeFilter filter;
    private static final int whatToShow = NodeFilter.SHOW_ALL;

    public FilteringIterator(NodeIterator parent, NodeFilter filter){
        this.parent = parent;
        this.filter = filter;
    }

//      /**
//       * copy ctor
//       */
//      public FilteringIterator(FilteringIterator other)
//      {
//          super(other);
//          this.parent = other.parent;
//          this.filter = other.filter;
//      }

    public int position(){
        return position;
    }

    public void setWhatToShow(int nodetype){
        throw new RuntimeException(getClass()+".setWhatToShow(int nodeType): operation not supported");  
    }

    public int getWhatToShow(){
        return whatToShow;
    }

    public NodeFilter getFilter(){
        return filter;
    }

    public void setFilter(NodeFilter filter){
        this.filter = filter;
    }

    public Node nextNode(){
        Node next = parent.nextNode();
        if(next == null)
            return null;
        try{
            if(filter.acceptNode(next)){
                ++position;
                return next;
            }
        }catch(XPathException exc){
            Log.exception(exc);
        }
        return nextNode();
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
