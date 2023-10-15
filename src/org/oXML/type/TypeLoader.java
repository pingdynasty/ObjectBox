package org.oXML.type;

import java.util.Map;
import java.util.HashMap;
import org.oXML.util.Log;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Enumeration;
import java.util.MissingResourceException;
import java.util.NoSuchElementException;

public class TypeLoader {

    private static TypeLoader me = new TypeLoader();

    private Map types;

    private TypeLoader(){
	types = new HashMap();
    }

    public static Type getType(Name name){
	return me.get(name);
   }

    public Type get(Name name) {
	Type type;
	synchronized(types){
	    type = (Type)types.get(name);
	    if(type == null){
		type = loadType(name);
	    }
	} 
	return type;
   }

    protected Type loadType(Name name) {
	List pl = new ArrayList();
	List fl = new ArrayList();
	try {
	    ResourceBundle rb =
		ResourceBundle.getBundle("org.oXML.type."+name);
	    Enumeration keys = rb.getKeys();
	    while (keys.hasMoreElements()) {
		String key = (String)keys.nextElement();
		String value = rb.getString(key);
		if(key.equals("parent"))
		    pl.add(getName(value));
		else
		    fl.add(key);
	    }
	}catch(NoSuchElementException exc) {
	    Log.exception(exc);
            System.err.println("Syntax error in resource bundle, loading type: "+name);
        }catch(MissingResourceException exc) {
            exc.printStackTrace();
            System.err.println("Initialization error: " + exc.toString()
			       +", loading type: "+name);
        }

	Type[] parents = new Type[pl.size()];
	for(int i=0; i<parents.length; ++i){
	    Name pn = (Name)pl.get(i);
	    parents[i] = get(pn);
	}

	Type type = new Type(name, parents);
	types.put(name, type);

	for(int i=0; i<fl.size(); ++i){
	    String classname = (String)fl.get(i);
	    try {
		Function fun = (Function)Class.forName(classname).newInstance();
		type.addFunction(fun);
	    }catch(ClassNotFoundException exc) {
		Log.exception(exc);
	    }catch(IllegalAccessException exc) {
		Log.exception(exc);
	    }catch(InstantiationException exc){
		Log.exception(exc);
	    }
	}
	Log.trace("loaded type: "+name);
	return type;
    }

    protected static Name getName(String name){
	int pos = name.indexOf('}');
	if(pos < 0)
	    return new Name(name);
	String ns = name.substring(1, pos);
	String local = name.substring(pos+1);
	return new Name(name);
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
