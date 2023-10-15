package org.oXML.extras.db;

import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.ReturnException;
import org.oXML.xpath.XPathException;
import org.oXML.engine.template.DynamicNode;
import org.oXML.type.Variable;
import org.oXML.type.Function;
import org.oXML.type.TypeFunction;
import org.oXML.type.NodesetNode;
import org.oXML.type.BooleanNode;
import org.oXML.type.StringNode;
import org.oXML.type.NumberNode;
import org.oXML.type.BytesNode;
import org.oXML.type.Nodeset;
import org.oXML.type.Node;
import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.util.Log;

public class QueryTypeFunction extends TypeFunction {

    private Variable[] params;
    private Query query;

    static private final Name THIS_NAME = new Name("this");

    /**
     * @param type the Type that defines this function
     */
    public QueryTypeFunction(Type type, Name name, 
                             Variable[] params, Query query){
	super(type, name, QueryFunction.getSignature(params));
        this.params = params;
        this.query = query;
    }

    public Node invoke(Node node, Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{
	// 'downcast'
	node = node.cast(getDeclaringType());
	return invoke((DynamicNode)node, args, ctxt);
    }

    public Node invoke(DynamicNode node, Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{
        // hide all existing variables
        ctxt.hide();

        Variable[] variables = getDeclaringType().getVariables();
        try{
            // push variables
            ctxt.setVariable(THIS_NAME, node);
            for(int i=0; i<variables.length; ++i){
		Variable var = node.getVariable(variables[i].getName());
		assert var != null : "missing variable: "+node.getType().getName()+'.'+variables[i];
                Node value = var.getValue();
                Type type = variables[i].getType();
                if(type != null){
                    if(type.instanceOf(StringNode.TYPE))
                        value = QuoteFunction.quote(value); // quote strings
                    // cast to ensure that values are of the right type
                    else if(type.instanceOf(NumberNode.TYPE))
                        value = value.toNumberNode();
                    else if(type.instanceOf(BooleanNode.TYPE))
                        value = value.toBooleanNode();
                    else if(type.instanceOf(BytesNode.TYPE))
                        value = value.toBytesNode();
                }
		ctxt.setVariable(var.getName(), value);
	    }
            return evaluate(node, args, ctxt);
        }finally{
            // discard new variables
            ctxt.unhide();
        }
    }

    public Node evaluate(DynamicNode instance, Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{
        // set parameters as variables
        for(int i=0; i<params.length; i++){
            Node value = query.castParameter(params[i].getName(), args[i]);
            ctxt.setVariable(params[i].getName(), value);
        }
        // create temporary result handler
	// this will hold our return value 
	Nodeset result = ctxt.pushOutputNodeset();
        try{
            // process
            query.execute(ctxt);
            // the result of executing a function with no return statement
            // is the nodeset it produced
        }catch(ReturnException exc){
            return exc.getResult();
        }finally{
	    ctxt.popOutputNodeset();
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
