package org.oXML.type;

import org.oXML.util.Log;

import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;

public abstract class Function {

    private Name name;
    private Type[] signature;

    /**
     * @param signature the function signature, or null
     */
    public Function(Name name, Type[] signature){
        this.name = name;
        this.signature = signature == null ? new Type[0] : signature;
    }

    public Name getName(){
        return name;
    }

    public Type[] getSignature(){
        return signature;
    }

    public Type getDeclaringType(){
	return null;
    }

    public FunctionKey getKey(){
        return new FunctionKey(name, signature.length);
    }

    /**
     * get the total type distance between parameter Types and signature.
     * assumptions:
     *  names match
     *  params.length == sig.length
     */
    int distance(Type[] params, int max){
        int distance = 0;
        for(int i=0; distance < max && i < signature.length; ++i)
            distance += params[i].distance(signature[i], max - distance);

        return distance < max ? distance : max;
    }

    public abstract Node invoke(Node[] args, RuntimeContext rc)
	throws ObjectBoxException;

    public abstract Node invoke(Node target, Node[] args, RuntimeContext rc)
	throws ObjectBoxException;

    protected String printSignature(){
	if(signature.length == 0)
	    return "";
	StringBuffer buf = new StringBuffer(signature[0].getName().toString());
	for(int i=1; i<signature.length; ++i){
	    buf.append(", ");
	    buf.append(signature[i].getName());
	}
	return buf.toString();
    }

    public String print(){
        StringBuffer buf = new StringBuffer(name.toString());
	buf.append('(')
	    .append(printSignature())
	    .append(')');
	return buf.toString();	
    }

    public static String print(Name name, Type[] sig){
        StringBuffer buf = new StringBuffer(name.toString());
	buf.append('(');
	if(sig.length > 0){
	    buf.append(sig[0].getName());
	    for(int i=1; i<sig.length; ++i){
		buf.append(", ").append(sig[i].getName());
	    }
	}
	buf.append(')');
	return buf.toString();	
    }

    public String toString(){
        StringBuffer buf = new StringBuffer(getClass().getName());
        buf.append('<')
	    .append(print())
	    .append('>');
        return buf.toString();
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
