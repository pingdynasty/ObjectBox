package org.oXML.xpath;

import org.oXML.type.Name;
import org.oXML.xpath.parser.QName;

public abstract class PrefixResolver {
    public static final String XMLNS_PREFIX = "xmlns";
    public static final String XMLNS_NAMESPACE = "http://www.w3.org/2000/xmlns/";

    public Name getName(QName qname) 
	throws NamespaceException {
        String prefix = qname.getPrefix();
        if(prefix == null)
            return new Name(qname.getLocalName());
        else if(prefix.equals(QName.WILDCARD))
            return new Name(Name.WILDCARD, qname.getLocalName());

	String uri = getNamespaceURI(prefix);
	if(uri == null)
	    throw new NamespaceException("unresolved name: "+qname);

	return new Name(uri, qname.getLocalName());
    }    

    public Name getName(String name)
        throws NamespaceException{
        int pos = name.indexOf(':');
        if(pos < 0)
            return new Name(name);
        String prefix = name.substring(0, pos);
        String uri = getNamespaceURI(prefix);
	if(uri == null)
	    throw new NamespaceException("unresolved name: "+name);
        name = name.substring(pos+1);
        return new Name(uri, name);
    }

    public abstract String getNamespaceURI(String prefix);
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
