package org.oXML.extras.java;

import org.oXML.util.Log;
import org.oXML.type.Node;
import org.oXML.type.Type;
import org.oXML.type.StringNode;
import org.oXML.type.NumberNode;
import org.oXML.type.BooleanNode;

public class NodeType extends ReflectionType{

    public NodeType(ReflectionTypeResolver resolver, Type type){
        super(resolver, type);
    }

    public Node primitive(Object object){
        // downcast to Node
        if(object instanceof Node)
            return (Node)object;
        else
            return super.primitive(object);
    }

    public Object object(Node primitive){
//          // convert from basic o:XML types or unwrap ReflectionNode
//          if(primitive instanceof ReflectionNode){
//              ReflectionNode p = (ReflectionNode)primitive;
//              return p.getInstance();
//          }else if(primitive.getType().equals(StringNode.TYPE)){
//              return primitive.stringValue();
//          }else if(primitive.getType().equals(NumberNode.TYPE)){
//              return new Double(primitive.numberValue());
//          }else if(primitive.getType().equals(BooleanNode.TYPE)){
//              return new Boolean(primitive.booleanValue());
//          }
//          // oh i give up.
        return primitive;
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
