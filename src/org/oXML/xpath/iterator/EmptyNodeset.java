package org.oXML.xpath.iterator;

import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodeIterator;

/**
 */
public class EmptyNodeset implements Nodeset {

    public EmptyNodeset(){
    }

    public Node getNode(int index){
        return null;
    }

    public void addNode(Node node){
        throw new RuntimeException(getClass()+".addNode(Node node): operation not supported");  
    }

    public int size(){
        return 0;
    }

    public boolean isEmpty(){
        return true;
    }

    public int indexOf(Node node){
        return -1;
    }

    public NodeIterator getIterator(){
        return new SingleNodeIterator(null);
    }

    public void removeNode(int pos){
	throw new RuntimeException(getClass()+".removeNode(int pos): operation not supported");  
    }

    public void insertNode(int pos, Node node){
	throw new RuntimeException(getClass()+".insertNode(int pos, Node node): operation not supported");  
    }

    public String toString(){
        return getClass().getName();
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
