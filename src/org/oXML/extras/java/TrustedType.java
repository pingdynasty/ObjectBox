package org.oXML.extras.java;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import org.oXML.type.*;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.type.Function;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

/** wrapper that wraps all functions to use a specific RuntimeContext */
public class TrustedType extends Type {

    private RuntimeContext ctxt;
    private Node trusted;

    private static final Set trustedTypes = new HashSet();

    static {
	trustedTypes.add(Node.TYPE.getName());
	trustedTypes.add(StringNode.TYPE.getName());
	trustedTypes.add(NumberNode.TYPE.getName());
	trustedTypes.add(BooleanNode.TYPE.getName());
	trustedTypes.add(NodesetNode.TYPE.getName());
	trustedTypes.add(ElementNode.TYPE.getName());
	trustedTypes.add(AttributeNode.TYPE.getName());
	trustedTypes.add(ProcessingInstructionNode.TYPE.getName());
	trustedTypes.add(CommentNode.TYPE.getName());
    }


    /** wrapper that invokes a function with our RuntimeContext */
    class TrustedFunction extends Function {
	private Function fun;

	public TrustedFunction(Function fun) {
	    super(fun.getName(), fun.getSignature());
	    this.fun = fun;
	}

	public Node invoke(Node args[], RuntimeContext other)
	    throws ObjectBoxException{
	    return entrust(fun.invoke(args, ctxt));
	}

	private Node entrust(Node node){
	    Name name = node.getType().getName();
	    if(trustedTypes.contains(name))
		return node;
	    return new TrustedNode(node, ctxt);
	}

	public Node invoke(Node node, Node args[], RuntimeContext other)
	    throws ObjectBoxException{
	    return entrust(fun.invoke(trusted, args, ctxt));
	}
    }

    public TrustedType(Node trusted, RuntimeContext ctxt){
	super(trusted.getType().getName(), 
	      trusted.getType().getParentTypes());
	java.util.List funs = trusted.getType().getFunctions().getFunctions();
	for(int i=0; i<funs.size(); ++i)
	    super.addFunction((Function)funs.get(i));
	Function[] ctors = trusted.getType().getConstructors();
	for(int i=0; i<ctors.length; ++i)
	    super.addConstructor(ctors[i]);
	this.ctxt = ctxt;
	this.trusted = trusted;
    }

    public final Function getFunction(Name name, Type[] params){
	Function function = super.getFunction(name, params);
	if(function == null)
	    return null;
	return new TrustedFunction(function);
    }
}
