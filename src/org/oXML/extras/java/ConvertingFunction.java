package org.oXML.extras.java;

import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.type.TypeFunction;
import org.oXML.type.Node;
import org.oXML.util.Log;

public abstract class ConvertingFunction extends TypeFunction{

    private ReflectionType type;
    private ReflectionType[] sig;

    public ConvertingFunction(Name name, ReflectionType type, 
                              ReflectionType[] sig){
        super(type.getType(), name, getSignature(sig));
        this.type = type;
        this.sig = sig;
    }

    public ConvertingFunction(ConvertingFunction other){
        super(null, other.getName(), other.getSignature());
        this.type = other.type;
        this.sig = other.sig;
    }

    private static Type[] getSignature(ReflectionType[] sig){
        Type[] signature = new Type[sig.length];
        for(int i=0; i<sig.length; ++i)
            signature[i] = sig[i].getType();
        return signature;
    }

    protected Object[] convert(Node[] args){
        // turn args into java objects using type mappings
        Object[] params = new Object[sig.length];
        for(int i=0; i<sig.length; ++i)
            params[i] = sig[i].object(args[i]);
        return params;
    }

    protected Node convert(Object object){
        return type.primitive(object);
    }

    public Type getType(){
        return type.getType();
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
