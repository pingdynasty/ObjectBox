package org.oXML.xpath;

import java.util.Map;
import java.util.HashMap;
import org.oXML.type.*;
import org.oXML.xpath.parser.QName;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;

public abstract class Context implements XPathContext{
    private Node contextNode;
    private int position; // XPath context position (within context nodeset)
    private int size; // XPath context size (of context nodeset)
    private TypeResolver resolver;

    protected Context(Context other){
	resolver = other.resolver;
        contextNode = other.contextNode;
    }

    public Context(TypeResolver resolver, Node contextNode){
	this.resolver = resolver;
        this.contextNode = contextNode;
    }

    public Function getFunction(Name name, Node[] args)
        throws ObjectBoxException{
        Type[] params = new Type[args.length];
        for(int i=0; i<params.length; ++i)
            params[i] = args[i].getType();
        return getFunction(name, params);
    }

    public Function getFunction(Name name, Type[] params)
        throws ObjectBoxException{
        Function function = 
	    resolver.getFunction(name, params);
        if(function == null){
            StringBuffer msg = new StringBuffer("no such function: ");
            msg.append(name)
                .append('(');
            for(int i=0; i<params.length; ++i){
                msg.append(params[i].getName());
		if(i+1<params.length)
		    msg.append(',');
	    }
            msg.append(')');
            throw new XPathException(msg.toString());
        }
        return function;
    }

    public abstract Node getVariable(Name name)
        throws XPathException;

    public abstract void setVariable(Name name, Node value);

    public Node getContextNode(){
        return contextNode;
    }

    public String toString(){
        return getClass().getName()+"["+contextNode+"]";
    }

    public void setContextNode(Node contextNode){
        this.contextNode = contextNode;
    }

    public int getContextPosition(){
        return position;
    }

    public void setContextPosition(int position){
        this.position = position;
    }

    public int getContextSize(){
        return size;
    }

    public void setContextSize(int size){
        this.size = size;
    }
    
    public abstract XPathContext copy();
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
