package org.oXML.extras.java;

import org.oXML.type.Name;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.type.Node;
import org.oXML.type.NodesetNode;
import org.oXML.util.Log;

public class ConvertingMethod extends ConvertingFunction{

    private Method method;

    public ConvertingMethod(Name name, ReflectionType type, 
                            ReflectionType[] sig, Method method){
        super(name, type, sig);
        this.method = method;
    }

    public Node invoke(Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{
        return invoke(null, args, ctxt);
    }

    public Node invoke(Node node, Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{
        Object instance = node == null ? null : ((ReflectionNode)node).getInstance();
        Object result;
        Object[] params = convert(args);
        try{
            result = method.invoke(instance, params);
        }catch(InvocationTargetException exc){
	    // unwrap exception
	    Throwable cause = exc.getTargetException();
	    if(cause == null)
		throw new ObjectBoxException
		    ("error invoking function: "+getName(), exc);
	    else if(cause instanceof ObjectBoxException)
		throw (ObjectBoxException)cause;
	    throw new ObjectBoxException("error invoking function "+getName()
					 +": "+cause.getMessage(), cause);
        }catch(IllegalAccessException exc){
	    throw new ObjectBoxException("illegal access invoking function "
					 +getName()+": "+exc.getMessage(), exc);
        }
        if(result == null){
            return NodesetNode.EMPTY_SET;
        }
        return convert(result);
    }

    public String toString(){
        return super.toString()+'<'+method+'>';
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
