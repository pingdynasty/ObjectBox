package org.oXML.type;

import java.util.Collection;
import org.oXML.util.Log;

public class Type{

    private Name name;
    private Type[] parents;
    private Functions functions;
    private Variable[] variables;
    private Function[] ctors;
    private Type[] descendants;

    public static final int MAX_TYPE_DISTANCE = Integer.MAX_VALUE;

    public Type(Name name){
        this.name = name;
	parents = new Type[0];
        functions = new Functions(this);
        variables = new Variable[0];
        ctors = new Function[0];
	descendants = new Type[0];
    }

    public Type(Name name, 
		Type[] parents) {
        this.name = name;
        this.parents = parents;
        functions = new Functions(this);
        ctors = new Function[0];
        variables = new Variable[0];
	descendants = new Type[0];
	for(int i=0; i<parents.length; ++i){
	    functions.addFunctions(parents[i].functions);
	    parents[i].addDescendant(this);
	}
    }

//     public Type(Name name, Type[] parents, Functions funs,
// 		Function[] ctors) {
//         this(name, parents, ctors);
// 	functions.addFunctions(funs);
//     }

    public Function[] getConstructors(){
	return ctors;
    }

    public void addConstructor(Function ctor){
	Function[] ctors = new Function[this.ctors.length + 1];
	for(int i=0; i<this.ctors.length; ++i){
	    if(this.ctors[i] == ctor)
		return;
	    ctors[i] = this.ctors[i];
	}
	ctors[this.ctors.length] = ctor;
	this.ctors = ctors;
    }

    public void addParent(Type parent){
	Type[] parents = new Type[this.parents.length + 1];
	for(int i=0; i<this.parents.length; ++i){
	    if(this.parents[i] == parent)
		return;
	    parents[i] = this.parents[i];
	}
	parents[this.parents.length] = parent;
	this.parents = parents;
	functions.addFunctions(parent.functions);
	parent.addDescendant(this);
    }


    private synchronized void addDescendant(Type type){
	Type[] desc = new Type[descendants.length + 1];
	for(int i=0; i<descendants.length; ++i){
	    if(desc[i] == type)
		return;
	    desc[i] = descendants[i];
	}
	desc[descendants.length] = type;
	descendants = desc;
    }

    public void addVariable(Name name, Type type){
        Variable[] vars = new Variable[variables.length+1];
        for(int i=0; i<variables.length; ++i)
            vars[i] = variables[i];
        vars[variables.length] = new Variable(name, type);
        variables = vars;
    }

    public Variable[] getVariables(){
        return variables;
    }

    public Name[] getVariableNames(){
        Name[] names = new Name[variables.length];
        for(int i=0; i<variables.length; ++i)
            names[i] = variables[i].getName();
        return names;
    }

    public void addFunction(Function function){
// 	throws FunctionException{
	if(function.getDeclaringType() == null)
	    throw new RuntimeException("no declaring type: "+function);
        functions.addFunction(function);
	// add function to all descendants' repertoirs
	for(int i=0; i<descendants.length; ++i)
	    descendants[i].addFunction(function);	
    }

    public boolean isFinal(){
        return false;
    }

    public Name getName(){
        return name;
    }

    public Type[] getParentTypes(){
        return parents;
    }

    public Type getParentType(Name name){
        // try immediate parents
        for(int i=0; i<parents.length; ++i)
            if(parents[i].getName().matches(name))
                return parents[i];
        // recurse upwards
        Type match = null;
        for(int i=0; match == null && i<parents.length; ++i){
            match = parents[i].getParentType(name);
        }
        return match;
    }

    public boolean instanceOf(Type other){
        return distance(other, MAX_TYPE_DISTANCE) < MAX_TYPE_DISTANCE;
    }

    public int distance(Type other, int max){

        if(name.equals(other.name))
            // it's a match
            return 0;

        // note:
        // this implementation has a limitation in the _total_ number of
        // parents a Type can have, and also in the number of sibling parents

        // distance to first parent
        int delta = 0x10000;
        int min = max;
        // find the parent that is closest
        for(int i=0; delta < max && i<parents.length; ++i){
            int calc = parents[i].distance(other, max - delta) + delta;
            if(calc < min)
                min = calc;
            // distance increases by 1 for each parent
            ++delta;
        }
        
        return min;
    }

    public Function getFunction(Name name, Type[] params){
	return functions.getFunction(name, params);
    }

    public Functions getFunctions(){
        return functions;
    }

    public boolean equals(Object other){
        if(other instanceof Type)
            return equals((Type)other);
        return false;
    }

    /**
     * two types are equal iff they have the same name
     */
    public boolean equals(Type other){
        return name.equals(other.name);
    }

    public String toString(){
        return getClass().getName()+"["+name+"]";
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
