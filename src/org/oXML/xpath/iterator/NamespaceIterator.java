package org.oXML.xpath.iterator;

import org.oXML.xpath.filter.NodeFilter;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodeIterator;

/**
 */
public class NamespaceIterator extends SubNodeIterator
{
    public NamespaceIterator(NodeIterator root, NodeFilter filter)
    {
        super(root, filter);
        throw new RuntimeException(toString()+" not implemented yet!");
    }

//      /**
//       * copy ctor
//       */
//      public NamespaceIterator(NamespaceIterator other)
//      {
//          super(other);
//      }

    protected Node getNextNode(){
        // see XSLT 1.0 5.4 Namespace Nodes
        Node next = parent.nextNode();
        if(next == null)
            return null;
        throw new RuntimeException("not implemented: "+getClass()+"getNextNode()");
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
