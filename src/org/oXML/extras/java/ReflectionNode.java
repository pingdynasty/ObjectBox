package org.oXML.extras.java;

import java.util.Iterator;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.type.SimpleNode;
import org.oXML.util.Log;

public class ReflectionNode extends SimpleNode {

    private Object instance;

    public ReflectionNode(Type type, Object instance){
	super(type);
        this.instance = instance;
    }

    public Object getInstance(){
        return instance;
    }

    public Node copy(boolean deep){
        return this; // immutable
    }

    // Node i/f required functions

//     public Node getPreviousSibling(){
// 	return null;
//     }
//     public Node getNextSibling(){
// 	return null;
//     }
//     public Nodeset getChildNodes(){
// 	return null;
//     }
//     public void addChildNode(Node child){}
//     public void removeChild(Node child){}

//     public double numberValue(){
// 	try{
// 	    return Double.parseDouble(stringValue());
// 	}catch(NumberFormatException exc){
// 	    return Double.NaN;
// 	}
//     }

    public String stringValue(){
        return instance.toString();
    }

    public boolean booleanValue(){
	return true; // because I am
    }

//     public byte[] byteValue(){
// 	return stringValue().getBytes();
//     }

    public String toString(){
        return super.toString()+'<'+instance.getClass().getName()+'>'+'<'+instance+'>';
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
