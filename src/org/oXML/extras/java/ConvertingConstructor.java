package org.oXML.extras.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.ObjectBoxException;
import org.oXML.type.Node;
import org.oXML.util.Log;

public class ConvertingConstructor extends ConvertingFunction{

    private Constructor ctor;

    public ConvertingConstructor(ReflectionType type, ReflectionType[] sig, 
                                 Constructor ctor){
        super(type.getType().getName(), type, sig);
        this.ctor = ctor;
    }

    public Node invoke(Node target, Node args[], RuntimeContext ctxt)
	throws ObjectBoxException{
	// we are being called as a parent initialiser
	// 'target' is a node that is derived from this type
        return invoke(args, ctxt);
    }

    public Node invoke(Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{
        Object result;
        Object[] params = convert(args);
        try{
            result = ctor.newInstance(params);
        }catch(InvocationTargetException exc){
	    // unwrap exception
	    Throwable cause = exc.getTargetException();
	    if(cause == null)
		throw new XPathException
		    ("error invoking constructor: "+getName(), exc);
	    else if(cause instanceof ObjectBoxException)
		throw (ObjectBoxException)cause;
	    throw new ObjectBoxException("error invoking constructor "+getName()
					 +": "+cause.getMessage(), cause);
        }catch(IllegalAccessException exc){
	    throw new ObjectBoxException("illegal access invoking constructor "
					 +getName()+": "+exc.getMessage(), exc);
        }catch(InstantiationException exc){
            throw new XPathException
		("instantiation exception invoking constructor "+getName()
		 +": "+exc.getMessage(), exc);
        }
        return convert(result);
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
