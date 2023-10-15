package org.oXML.engine.template;

import org.oXML.ObjectBoxException;
import org.oXML.type.Variable;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.engine.ReturnException;
import org.oXML.xpath.Expression;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.function.FunctionCall;
import org.oXML.xpath.function.ConstructorCall;
import org.oXML.type.Function;
import org.oXML.type.ConstructorFunction;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.ParentNode;
import org.oXML.type.NodesetNode;
import org.oXML.type.AbstractNode;
import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.util.Log;

public class DynamicConstructor extends ConstructorFunction {

    protected Parameter[] variables;
    protected Parameter[] parentConstructors;

    static private final Type[] defaultSig = new Type[]{};
    static private final Node[] defaultArgs = new Node[]{};
    static private final Name THIS_NAME = new Name("this");

    private Parameter[] params;
    private Template body;


    public DynamicConstructor(Type type, Parameter[] params, Template body, 
                              Parameter[] variables, Parameter[] parentConstructors){
	super(type, DynamicFunction.getSignature(params));
        this.params = params;
        this.body = body;
        this.variables = variables;
        this.parentConstructors = parentConstructors;
    }

    /**
     * create a Generated Default Constructor
     */
    public DynamicConstructor(Type type, Parameter[] variables){
        this(type, new Parameter[0], new NullTemplate(), variables, new Parameter[0]);
    }

    public Node invoke(Node[] args, RuntimeContext context)
        throws ObjectBoxException {
	DynamicNode instance = new DynamicNode(getDeclaringType());
	initialise(instance, args, context);
	return instance;
    }

    public Node invoke(Node target, Node[] args, RuntimeContext context)
	throws ObjectBoxException {
	// we are being called as a parent initialiser
	// 'target' is a node that is derived from this type
	return invoke((AbstractNode)target, args, context);
    }

    public Node invoke(AbstractNode target, Node[] args, RuntimeContext context)
	throws ObjectBoxException {
	assert !target.hasInstance(getName()) : getName();
        DynamicNode instance = new DynamicNode(target, target.getType());
	initialise(instance, args, context);
// 	if(!target.hasInstance(getName())){ // if we haven't already been instantiated
// 	}
	return instance;
    }

    public void initialise(DynamicNode instance, Node[] args, RuntimeContext env)
        throws ObjectBoxException {
        // hide all existing variables
        env.hide();
        try{
	    // initialise variables
            env.setVariable(THIS_NAME, instance);
	    for(int i=0; i<variables.length; ++i){
		Variable variable = new Variable(variables[i].getName(), null, variables[i].getType());
// 		Log.trace("create variable: "+getName()+"."+variable.getName());
		instance.setVariable(variable);
		Expression expr = variables[i].getDefaultExpression();
		if(expr == null){
		    variable.setValue(NodesetNode.EMPTY_SET);
		}else{
		    variable.setValue(expr.evaluate(env));
		}
		// push variable
		env.setVariable(variable);
	    }
            Node content = evaluate(instance, args, env);
	    try{
                if(instance.getType().instanceOf(ParentNode.TYPE))
                    instance.addChildNode(content);
	    }catch(Exception exc){
		Log.warning("Unable to add node content: "+exc.getMessage());
	    }
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

	// initialise parents
	Type type = getDeclaringType();

        // call all explicit parent constructors
        for(int i=0; i<parentConstructors.length; ++i){
            Name name = parentConstructors[i].getName();
            if(!instance.hasInstance(name)){
//   		Log.trace("explicit initialisation of parent: "+name);
                // unless this parent has already been initialised
		ConstructorCall ctor = 
		    (ConstructorCall)parentConstructors[i].getDefaultExpression();
		AbstractNode parent = (AbstractNode)ctor.evaluate(instance, env);
		instance.setInstance(name, parent);
            }
        }

        // initialise remaining parent instances by 
	// calling default parent constructors
        Type[] parents = type.getParentTypes();
        for(int i=0; i<parents.length; ++i){
            Name name = parents[i].getName();
            if(!instance.hasInstance(name)){
//   		Log.trace("default initialisation of parent: "+name);
                // if this parent has not already been initialised
                // try for default constructor
                Function ctor = env.getFunction(name, defaultSig);
                if(ctor == null)
                    throw new XPathException
			("invalid constructor, parent type: "+name+
			 " has no default and no explicit constructor");
                AbstractNode parent = (AbstractNode)ctor.invoke(instance, defaultArgs, env);
		instance.setInstance(name, parent);
            }
        }

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
