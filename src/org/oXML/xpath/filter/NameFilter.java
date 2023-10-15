package org.oXML.xpath.filter;

import org.oXML.type.Name;
import org.oXML.xpath.XPathContext;
import org.oXML.util.Log;
import org.oXML.type.Node;
import org.oXML.type.ElementNode;
import org.oXML.xpath.XPathException;

/**
 * implementation of NodeFilter that filters out (Element) nodes
 * with matching name and namespace
 */
public class NameFilter implements NodeFilter{
    private Name name;

    public NameFilter(Name name){
        this.name = name;
    }

    public boolean acceptNode(Node p){
        Name other = p.getName();
        return other != null && other.matches(name);
    }

    public String toString(){
        return getClass().getName()+"["+name+"]";
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
