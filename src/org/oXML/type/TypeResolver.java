package org.oXML.type;

import java.util.Map;
import java.util.HashMap;
import org.oXML.ObjectBoxException;

/**
 * dynamically resolves types and functions.
 */
public class TypeResolver {

    public Map types;
    public Functions functions;

    private static final Module core = new CoreModule();

    public TypeResolver(){
        types = new HashMap();
	types.putAll(core.getTypes());
        functions = new Functions(null);
	functions.addFunctions(core.getFunctions());
    }

    public boolean hasType(Name name){
        return types.containsKey(name);
    }

    public Type getType(Name name)
	throws ObjectBoxException {
	Type type = (Type)types.get(name);
	if(type == null)
	    throw new UnknownTypeException("no such type: "+name);
	return type;
    }

    public Function getFunction(Name name, Type[] args)
	throws ObjectBoxException {
	return functions.getFunction(name, args);
    }

    public void addFunction(Function fun)
	throws ObjectBoxException {
	functions.addFunction(fun);
    }

    public void addType(Type type)
	throws ObjectBoxException {
        if(types.containsKey(type.getName()))
            throw new ObjectBoxException("type already defined: "+type.getName());
	types.put(type.getName(), type);
    }

    public void addModule(Module module)
	throws ObjectBoxException {
	functions.addFunctions(module.getFunctions());
	types.putAll(module.getTypes());
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
