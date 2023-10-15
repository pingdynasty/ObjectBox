package org.oXML.xpath;

import java.util.Map;
import java.util.HashMap;
import org.oXML.type.Node;
import org.oXML.type.Name;
import org.oXML.xpath.parser.QName;

public class SimpleResolver extends PrefixResolver implements Resolver {

    private Map namespaces;

    public SimpleResolver(){
	namespaces = new HashMap();
    }

    /**
     * create a new resolver using an array of String[2] prefix/namespace pairs
     */
    public SimpleResolver(String[][] namespaces){
	this();
	for(int i=0; i<namespaces.length; ++i)
	    addNamespace(namespaces[i][0], namespaces[i][1]);
    }

    public void addNamespace(String prefix, String uri){
	namespaces.put(prefix, uri);
    }

    public void removeNamespace(String prefix){
	namespaces.remove(prefix);
    }

    public String getNamespaceURI(String prefix){
	return (String)namespaces.get(prefix);
    }

    public SourceLocator getLocation(){
	return null;
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
