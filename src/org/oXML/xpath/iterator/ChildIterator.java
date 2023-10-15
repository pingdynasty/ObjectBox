package org.oXML.xpath.iterator;

import org.oXML.type.Node;
import org.oXML.type.NodeIterator;
import org.oXML.xpath.filter.NodeFilter;
import org.oXML.util.Log;

/**
 *  contains all child nodes of all nodes returned by parent nodeset
 */
public class ChildIterator extends SubNodeIterator
{
    private NodeIterator set; // the current set of child nodes

    public ChildIterator(NodeIterator parent, NodeFilter filter){
        super(parent, filter);
    }

//      /**
//       * copy ctor
//       */
//      public ChildIterator(ChildIterator other){
//          super(other);
//          set = other.set;
//      }

    protected Node getNextNode(){
        if(set == null){
            Node mom = parent.nextNode();
            if(mom == null){
                // we've reached the end of the line
                return null;
            }else{
                set = mom.getChildNodes().getIterator();
                if(set == null)
                    // can't use this node as parent, try next one
                    return getNextNode();
            }
        }
        Node result = set.nextNode();
        if(result == null){
            // this set is empty, try next one
            set = null;
            return getNextNode();
        }
        return result;
    }
        
    public String toString()
    {
        return super.toString()+"["+set+"]";
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
