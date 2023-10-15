package org.oXML.type;

import org.oXML.ObjectBoxException;

public class Name{

    private String localname;
    private String uri;
    private String prefix;
    public static final String EMPTY_NAMESPACE = "";
    public static final String NO_PREFIX = "";
    public static final String WILDCARD = "*";

    public Name(String name){
	localname = name;
	uri = EMPTY_NAMESPACE;
        prefix = NO_PREFIX;
    }

    public Name(String uri, String localname){
        this.localname = localname;
        this.uri = uri == null ? EMPTY_NAMESPACE : uri;
        prefix = NO_PREFIX;
    }

    public Name(String uri, String localname, String prefix){
        this.localname = localname;
        this.uri = uri == null ? EMPTY_NAMESPACE : uri;
	this.prefix = prefix == null ? NO_PREFIX : prefix;
    }

    /**
     * Turns a string, possibly containing a namespace within curly braces, 
     * into a valid name.
     */
    public static Name makeName(String name)
	throws ObjectBoxException {
	int pos = name.indexOf('}');
	if(pos == -1){
// 	    pos = name.indexOf(':');
// 	    if(pos == -1){
	    return new Name(name);
// 	    }else{
// 		String prefix = name.substring(1, pos);
// 		String local = name.substring(pos + 1);
// 		return new Name(EMPTY_NAMESPACE, prefix, name);
// 	    }
	}else{
	    if(name.indexOf('{') != 0)
		throw new ObjectBoxException("imbalanced or missing '{' in name: "+name);
	    String uri = name.substring(1, pos);
	    String local = name.substring(pos + 1);
	    return new Name(uri, local);
	}
    }

    public String getLocalName(){
        return localname;
    }

    public void setLocalName(String localname){
        this.localname = localname;
    }

    public String getNamespaceURI(){
        return uri;
    }

    public void setNamespaceURI(String uri){
        if(uri == null)
            this.uri = EMPTY_NAMESPACE;
        else
            this.uri = uri;
    }

    public String getPrefix(){
        return prefix;
    }

    public void setPrefix(String prefix){
        if(prefix == null)
            this.prefix = NO_PREFIX;
        else
            this.prefix = prefix;
    }

    public boolean matches(Name other){
	if(other == null)
	    return false;
        return (localname.equals(other.localname) || 
                localname.equals(WILDCARD) ||
                other.localname.equals(WILDCARD)) &&
            (uri.equals(other.uri) ||
             uri.equals(WILDCARD) || 
             other.uri.equals(WILDCARD));
    }

    /**
     * get a String representation of this Name with qualifying namespace prefix, if known.
     */
    public String print(){
        return prefix.equals(NO_PREFIX) ? localname : prefix+':'+localname;
    }

    public boolean equals(Object other){
        if(other instanceof Name)
            return equals((Name)other);
        return false;
    }

    public boolean equals(Name other){
        return localname.equals(other.localname) && uri.equals(other.uri);
    }

    public int hashCode(){
        return localname.hashCode() ^ uri.hashCode();
    }

    public String toString(){
        return uri.equals(EMPTY_NAMESPACE) ? localname : '{'+uri+'}'+localname;
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
