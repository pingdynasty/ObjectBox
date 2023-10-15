package org.oXML.engine.template;

import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.xpath.Expression;
import org.oXML.type.Node;

public class Parameter{
    private Name name;
    private Type type;
    private Expression select;
    private Node value;

    public Parameter(Name name, Type type){
        this.name = name;
        this.type = type;
    }

    public Parameter(Name name, Type type, Expression select){
        this(name, type);
        this.select = select;
    }

    public Parameter(Name name, Expression select){
        this.name = name;
        this.select = select;
    }

    public Name getName(){
        return name;
    }

    public Type getType(){
        return type;
    }

    public Expression getDefaultExpression(){
        return select;
    }

    public void setDefaultValue(Node value){
        this.value = value;
    }
    
    public Node getDefaultValue(){
        return value;
    }

    public String toString(){
        return "<"+name+"="+value+">";
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
