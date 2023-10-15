package org.oXML.type;

import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.util.Log;

public class Variable {
    private Name name;
    private Node value;
    private Type type;

    public Variable(Name name, Type type){
        this.name = name;
	this.type = type;
    }

    public Variable(Name name, Node value){
        this.name = name;
        this.value = value;
        this.type = Node.TYPE;
    }

    public Variable(Name name, Node value, Type type){
        this.name = name;
        this.value = value;
	this.type = type;
    }

    public Variable(Variable copy, boolean deep){
        name = copy.name;
	if(deep)
	    value = copy.value.copy(deep);
	else
	    value = copy.value;
	this.type = copy.type;
    }

    public Name getName(){
        return name;
    }

    public Node getValue(){
        return value;
    }

    public Type getType(){
        return type;
    }

    public void setValue(Node value){
        this.value = value;
    }

    public String toString(){
        return (type == null ? "<" : "<"+type.getName()+" ")+name+" = "+value+">";
    }

    public Variable copy(boolean deep){
	return new Variable(this, deep);
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
