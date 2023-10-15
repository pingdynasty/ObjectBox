package org.oXML.engine.template;

import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.ReturnException;
import org.oXML.xpath.XPathException;
import org.oXML.type.Variable;
import org.oXML.type.Function;
import org.oXML.type.TypeFunction;
import org.oXML.type.NodesetNode;
import org.oXML.type.Nodeset;
import org.oXML.type.Node;
import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.util.Log;

public class DynamicTypeFunction extends TypeFunction {

    private Parameter[] params;
    private Template body;

    static private final Name THIS_NAME = new Name("this");

    /**
     * @param type the Type that defines this function
     */
    public DynamicTypeFunction(Type type, Name name, 
			       Parameter[] params, Template body){
	super(type, name, DynamicFunction.getSignature(params));
        this.params = params;
        this.body = body;
    }

    public Node invoke(Node node, Node args[], RuntimeContext env)
        throws ObjectBoxException{
	// 'downcast'
	node = node.cast(getDeclaringType());
	return invoke((DynamicNode)node, args, env);
    }

    public Node invoke(DynamicNode node, Node args[], RuntimeContext env)
        throws ObjectBoxException{
        // hide all existing variables
        env.hide();

        Variable[] variables = getDeclaringType().getVariables();
	// should the context node be set to 'this'?
        try{
            // push variables
            env.setVariable(THIS_NAME, node);
            for(int i=0; i<variables.length; ++i){
		Variable var = node.getVariable(variables[i].getName());
		assert var != null : "missing variable: "+node.getType().getName()+'.'+variables[i].getName();
		env.setVariable(var);
// 		env.setVariable(node.getVariable(variables[i]));
	    }
            return evaluate(node, args, env);
        }finally{
            // discard new variables
            env.unhide();
        }
    }

    public Node evaluate(DynamicNode instance, Node args[], RuntimeContext env)
        throws ObjectBoxException{
        // set parameters as variables
        for(int i=0; i<params.length; i++)
            env.setVariable(params[i].getName(), args[i]);
        // create temporary result handler
	// this will hold our return value 
	Nodeset result = env.pushOutputNodeset();
        try{
            // process
            body.process(env);
            // the result of executing a function with no return statement
            // is the nodeset it produced
        }catch(ReturnException exc){
            return exc.getResult();
        }finally{
	    env.popOutputNodeset();
        }
        return new NodesetNode(result);
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
