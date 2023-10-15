package org.oXML.xpath.function;

import java.util.List;
import org.oXML.type.Node;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.parser.QName;
import org.oXML.type.Name;
import org.oXML.type.Function;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

/**
 * ConstructorCall is used by constructors calling parent initialisers
 */
public class ConstructorCall extends FunctionCall {

    public ConstructorCall(FunctionCall call){
	super(call.qname, call.args);
	this.name = call.name;
    }

    public Node evaluate(Node target, RuntimeContext ctxt)
	throws ObjectBoxException {
	Node[] parts = new Node[args.length];
	for(int i=0; i<parts.length; ++i)
	    parts[i] = args[i].evaluate(ctxt);

	Function func = ctxt.getFunction(name, parts);

	if(func == null)
	    throw new XPathException("no matching function: "+
				     printFunction(name, parts));

	StackFrame frame = new StackFrame(func.print(), location);
	ctxt.pushStackFrame(frame);
	try{
	    return func.invoke(target, parts, ctxt);
	}catch(FunctionException exc){
	    exc.addStackFrame(frame);
	    throw exc;
	}catch(Throwable exc){
	    throw new FunctionException(frame, exc);
	}finally{
	    ctxt.popStackFrame();	
	}
    }
}
