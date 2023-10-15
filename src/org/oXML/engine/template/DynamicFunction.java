package org.oXML.engine.template;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.engine.ReturnException;
import org.oXML.type.TypeFunction;
import org.oXML.type.Function;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodesetNode;
import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.util.Log;

public class DynamicFunction extends Function {

    private Parameter[] params;
    private Template body;

    public DynamicFunction(Name name, Parameter[] params, Template body){
	super(name, getSignature(params));
        this.params = params;
        this.body = body;
    }

    protected static final Type[] getSignature(Parameter[] params){
        // create signature from params
        Type[] signature = new Type[params.length];
        for(int i=0; i<params.length; ++i)
            signature[i] = params[i].getType();
        return signature;
    }

    public Node invoke(Node target, Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{
	throw new ObjectBoxException("invalid invokation of function "+print());
    }

    public Node invoke(Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{        
        // hide all existing variables
        ctxt.hide();
        try{
            return evaluate(args, ctxt);
        }finally{
            // discard new variables
            ctxt.unhide();
        }
    }

    public Node process(Node args[], RuntimeContext env)
        throws ObjectBoxException{
        // create temporary result handler
	Nodeset result = env.pushOutputNodeset();
        try{
            // process
            body.process(env);
            // the result of executing a function with no return statement
            // is the nodeset it produced
        }catch(ReturnException exc){
            return exc.getResult();
        }finally{
	    // reset handler
	    env.popOutputNodeset();
        }
        return new NodesetNode(result);
    }

    public Node evaluate(Node args[], RuntimeContext env)
        throws ObjectBoxException{
        // set parameters as variables
        for(int i=0; i<params.length; i++)
            env.setVariable(params[i].getName(), args[i]);
        return process(args, env);
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
