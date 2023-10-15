package org.oXML.extras.java.function;

import java.util.List;
import java.util.Arrays;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.type.NodesetNode;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.type.Function;
import org.oXML.extras.java.ReflectionNode;
import org.oXML.extras.java.ReflectionType;
import org.oXML.extras.java.ReflectionTypeResolver;
import org.oXML.util.Log;

/**
 * creates an object representing a Java array.
 * first argument must be the array type, eg char, byte, short etc.
 * second argument is the array length.
 */
public class ArrayFunction extends Function {

    private ReflectionTypeResolver resolver;

    public ArrayFunction(Name name, Type[] sig, ReflectionTypeResolver resolver){
        super(name, sig);
	this.resolver = resolver;
    }

    public Node invoke(Node node, Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{
        return invoke(args, ctxt);
    }

    public Node invoke(Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{
	if(args.length != 2)
	    throw new ObjectBoxException(getName()+" takes exactly two arguments");
	String name = args[0].stringValue();
	Class klass;
	if(name.equals("char"))
	    klass = Character.TYPE;
	else if(name.equals("byte"))
	    klass = Byte.TYPE;
	else if(name.equals("short"))
	    klass = Short.TYPE;
	else if(name.equals("int"))
	    klass = Integer.TYPE;
	else if(name.equals("long"))
	    klass = Long.TYPE;
	else if(name.equals("float"))
	    klass = Float.TYPE;
	else if(name.equals("double"))
	    klass = Double.TYPE;
	else{
	    try{
		klass = Class.forName(name);
	    }catch(ClassNotFoundException exc){
		throw new ObjectBoxException(exc);
	    }
	}
	int length = (int)args[1].numberValue();
	Object array = java.lang.reflect.Array.newInstance(klass, length);
	Type type = resolver.resolve(array.getClass()).getType();
	return new ReflectionNode(type, array);
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
