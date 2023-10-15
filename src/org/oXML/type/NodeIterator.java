package org.oXML.type;

/**
 * abstracts a nodeset iterator
 */
public interface NodeIterator {
    /**
     * get the position of the current node within this nodeset
     */
    public int position();

    // hasNext complicates implementation of chained iterators, as they have to look ahead
//      public boolean hasNext();

    /**
     * advance the current node in this nodeset.
     * @return the new current node or null.
     */
    public Node nextNode();

//      /**
//       * revert the current node in this nodeset.
//       * @return the new current node or null.
//       */
//      public Node previousNode();

//      /**
//       * reset the iterator to point to the first node in the set
//       */
//      public void reset();
    
//      /**
//       * reset the iterator and return the first node in the Nodeset
//       */
//      public Node firstNode();

//      public void setWhatToShow(int nodetype);

//      public int getWhatToShow();
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
