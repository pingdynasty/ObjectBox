package org.oXML.extras.db;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.engine.ReturnException;
import org.oXML.type.TypeFunction;
import org.oXML.type.Variable;
import org.oXML.type.Function;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodesetNode;
import org.oXML.type.BooleanNode;
import org.oXML.type.StringNode;
import org.oXML.type.NumberNode;
import org.oXML.type.BytesNode;
import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.util.Log;

public class QueryFunction extends Function {

    private Variable[] params;
    private Query query;

    public QueryFunction(Name name, Variable[] params, Query query){
	super(name, getSignature(params));
        this.params = params;
        this.query = query;
    }

    protected static final Type[] getSignature(Variable[] params){
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

    public Node process(Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{
        // create temporary result handler
	Nodeset result = ctxt.pushOutputNodeset();
        try{
            // process
            query.execute(ctxt);
            // the result of executing a function with no return statement
            // is the nodeset it produced
        }catch(ReturnException exc){
            return exc.getResult();
        }finally{
	    // reset handler
	    ctxt.popOutputNodeset();
        }
        return new NodesetNode(result);
    }

    public Node evaluate(Node args[], RuntimeContext ctxt)
        throws ObjectBoxException{
        // set parameters as variables
        for(int i=0; i<params.length; i++){
            Node value = args[i];
            Type type = params[i].getType();
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
            ctxt.setVariable(params[i].getName(), value);
//             ctxt.setVariable(new Variable(params[i].getName(), args[i], params[i].getType()));
//             ctxt.setVariable(params[i].getName(), args[i]);
        }
        return process(args, ctxt);
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
