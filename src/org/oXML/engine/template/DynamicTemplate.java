package org.oXML.engine.template;

import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodesetNode;
import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.xpath.Expression;
import org.oXML.util.Log;

/** represents a call to a user-defined procedure */
public class DynamicTemplate implements Template {
    private Template procedure;
    private Parameter[] params;
    private Template body;
    private Expression[] exps;

    public DynamicTemplate(Parameter[] params, Expression[] exps, Template body){
	this.params = params;
	this.exps = exps;
	this.body = body;
    }

    public void setProcedureBody(Template procedure){
	this.procedure = procedure;
    }

    public void process(RuntimeContext env)
	throws ObjectBoxException{
	assert procedure != null;

	// evaluate arguments
	Node[] args = new Node[params.length];
	for(int i=0; i<params.length; i++){
	    if(exps[i] == null){
		args[i] = params[i].getDefaultValue();
		if(args[i] == null)
		    throw new ObjectBoxException("missing required attribute: "+params[i].getName());
	    }else{
		args[i] = exps[i].evaluate(env);
	    }
	}

	// evaluate the content of the procedure call
	Node content;
	try{
	    // create result nodeset
	    content = new NodesetNode(env.pushOutputNodeset());
	    // process
	    body.process(env);
	}finally{
	    // reset handler
	    env.popOutputNodeset();
	}

	// hide all existing variables
	env.hide();
	// save the current context node
	Node saved = env.getContextNode();
	// set the procedure call body as context node
	env.setContextNode(content);
	try{
	    // set parameters as XPath variables
	    for(int i=0; i<params.length; i++)
		env.setVariable(params[i].getName(), args[i]);

	    // process the procedure body
	    procedure.process(env);
	}finally{
	    // discard new variables
	    env.unhide();
	    // reset context node
	    env.setContextNode(saved);
	}
    }
}
