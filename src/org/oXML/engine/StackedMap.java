package org.oXML.engine;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.oXML.type.Name;
import org.oXML.type.Variable;
import org.oXML.xpath.XPathException;
import org.oXML.util.Log;

/**
 * n.b. not thread safe!
 */
public class StackedMap{
    private Map vars;
    private List levels; // contains a list of vars for each level
    private List currentItems;
    private int currentLevel;

    public StackedMap(){
        vars = new HashMap();
        levels = new ArrayList();
        currentItems = new ArrayList();
        currentLevel = 0;
    }

    /**
     * copy constructor
     */
    protected StackedMap(StackedMap other){
	this();

	Iterator it = other.vars.values().iterator();
	while(it.hasNext()){
	    Variable var = (Variable)it.next();
	    vars.put(var.getName(), var.copy(false)); // add a shallow copy
	}
	currentLevel = other.currentLevel;
	levels.addAll(other.levels);
	currentItems.addAll(other.currentItems);
    }

// 	for(int i=0; i<other.currentLevel; ++i){
// 	    List level = (List)other.levels.get(i);
// 	    for(int j=0; j<level.size(); ++j){
// 		Name name = (Name)level.get(j);
// 		Variable var = (Variable)other.vars.get(name);
// 		vars.put(name, var.copy(false)); // add a shallow copy
// 		currentItems.add(name);
// 	    }
// 	    increment();
// 	}
//     }

    public StackedMap copy(){
        return new StackedMap(this);
    }

    public Variable get(Name name){
        return (Variable)vars.get(name);
    }

    public boolean containsKey(Name name){
        return vars.containsKey(name);
    }

    public void set(Name name, Variable value){
	vars.put(name, value);
	currentItems.add(name);
    }

    public void increment(){
        ++currentLevel;
        levels.add(0, currentItems);
        currentItems = new ArrayList();
    }

    public void decrement(){
        if(--currentLevel < 0)
            throw new RuntimeException("stack underflow exception");
        Iterator it = currentItems.iterator();
        while(it.hasNext()){
            Name name = (Name)it.next();
            vars.remove(name);
        }
        currentItems = (List)levels.remove(0);
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
