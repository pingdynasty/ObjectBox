package org.oXML.type;

import org.oXML.xpath.iterator.EmptyNodeset;

/**
 * abstracts a nodeset (such as the 'current nodeset' in xpath evaluation).
 */
public interface Nodeset{

    public static Nodeset EMPTY_SET = new EmptyNodeset();
    /**
     * get the cardinality of this nodeset
     */
    public int size();

    public Node getNode(int pos);

    public boolean isEmpty();

    public void addNode(Node node);

    public void insertNode(int pos, Node node);

    public void removeNode(int pos);

    public int indexOf(Node node);

    public NodeIterator getIterator();
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
