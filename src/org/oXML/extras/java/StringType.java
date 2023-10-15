package org.oXML.extras.java;

import org.oXML.util.Log;
import org.oXML.type.Node;
import org.oXML.type.StringNode;

public class StringType extends ReflectionType{

    public static final int STRING = 0;
    public static final int CHARACTER = 1;
    public static final int CHARARRAY = 2;

    private final int type;

    public StringType(ReflectionTypeResolver resolver){
        super(resolver, StringNode.TYPE);
        type = STRING;
    }

    public StringType(ReflectionTypeResolver resolver, int type){
        super(resolver, StringNode.TYPE);
        this.type = type;
    }

//      public Node primitive(Object object){
//          return new ReflectionNode(getType(), object.toString());
//      }

     public Node primitive(Object object){
         return new StringNode(object.toString());
     }

    public Object object(Node primitive){
        switch(type){
            case STRING :
                return primitive.stringValue();
            case CHARACTER :
                return new Character(primitive.stringValue().charAt(0));
            case CHARARRAY :
                return primitive.stringValue().toCharArray();
        }
        throw new RuntimeException("invalid string type: "+type);
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
