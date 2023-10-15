package org.oXML.extras.java;

import org.oXML.util.Log;
import org.oXML.type.Node;
import org.oXML.type.NumberNode;

public class NumberType extends ReflectionType{

    public static final int INTEGER = 0;
    public static final int LONG = 1;
    public static final int SHORT = 2;
    public static final int FLOAT = 3;
    public static final int DOUBLE = 4;

    private int type;

    public NumberType(ReflectionTypeResolver resolver, int type){
        super(resolver, NumberNode.TYPE);
        this.type = type;
    }

    public Node primitive(Object object){
        return primitive((Number)object);
    }

    public Node primitive(Number number){
        return new NumberNode(number);
    }

    public Object object(Node primitive){
        switch(type){
            case INTEGER:
                return new Integer((int)primitive.numberValue());
            case LONG:
                return new Long((long)primitive.numberValue());
            case SHORT:
                return new Short((short)primitive.numberValue());
            case FLOAT:
                return new Float((float)primitive.numberValue());
            case DOUBLE:
                return new Double(primitive.numberValue());
        }
        throw new RuntimeException("invalid number type: "+type);
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
