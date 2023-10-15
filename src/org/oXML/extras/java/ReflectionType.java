package org.oXML.extras.java;

import java.util.List;
import java.util.ArrayList;
import org.oXML.util.Log;
import org.oXML.type.Type;
import org.oXML.type.Node;
import org.oXML.type.Variable;
import org.oXML.type.Function;

public class ReflectionType{

    private ReflectionTypeResolver resolver;
    private Type type;
    private List ctors;
    private List globals;
    private List fields;

    public ReflectionType(ReflectionTypeResolver resolver, Type type){
        this.resolver = resolver;
        this.type = type;
        ctors = new ArrayList();
        globals = new ArrayList();
        fields = new ArrayList();
    }

    public Type getType(){
        return type;
    }

    public void addConstructor(Function ctor){
        ctors.add(ctor);
    }

    public Function[] getConstructors(){
        Function[] constructors = new Function[ctors.size()];
        ctors.toArray(constructors);
        return constructors;
    }

    public void addStaticFunction(Function fun){
        globals.add(fun);
    }

    public Function[] getStaticFunctions(){
        Function[] funs = new Function[globals.size()];
        globals.toArray(funs);
        return funs;
    }

    public void addField(Variable field){
        fields.add(field);
    }

    public Variable[] getFields(){
        Variable[] vars = new Variable[fields.size()];
        fields.toArray(vars);
        return vars;
    }

    public Node primitive(Object object){
        if(object instanceof Node)
            return (Node)object;
        ReflectionType rtype = resolver.resolve(object.getClass());
        return new ReflectionNode(rtype.getType(), object);
    }

    public Object object(Node primitive){
        ReflectionNode p = (ReflectionNode)primitive;
        return p.getInstance();
    }

    public String toString(){
        return getClass().getName()+'<'+type+'>';
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
