package org.oXML.type;

import org.oXML.util.Log;
import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;

public abstract class TypeFunction extends Function {

    private Type type;

    /**
     * @param signature the function signature, or null
     */
    public TypeFunction(Type type, Name name, Type[] signature){
	super(name, signature);
        this.type = type;
    }

    public Type getDeclaringType(){
	return type;
    }

    public Node invoke(Node[] args, RuntimeContext rc)
	throws ObjectBoxException{
	throw new ObjectBoxException("invalid invokation of type function: "+print());
    }

    public String print(){
        StringBuffer buf = new StringBuffer(getDeclaringType().getName().toString());
	buf.append('.')
	    .append(getName())
	    .append('(')
	    .append(printSignature())
	    .append(')');
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
