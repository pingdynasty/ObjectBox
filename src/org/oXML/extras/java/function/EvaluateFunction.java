package org.oXML.extras.java.function;

import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.function.XPathFunction;
import org.oXML.xpath.parser.Parser;
import org.oXML.xpath.parser.ExpressionParser;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.NodePrefixResolver;
import org.oXML.xpath.Expression;
import org.oXML.xpath.Context;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

import org.oXML.ObjectBoxException;

/**
 * java:evaluate(String expression, Node context)
 * Evaluates o:Path expression dynamically.
 */
public class EvaluateFunction extends XPathFunction{

    public Parser parser;

    public EvaluateFunction(Name name)
	throws ObjectBoxException{
        super(name, new Type[]{Node.TYPE, Node.TYPE});
	parser = new ExpressionParser();
    }

    public Node eval(Node args[], RuntimeContext context)
        throws ObjectBoxException {
	String expr = args[0].stringValue();
	Expression e = parser.parse(expr);
        // note: binding the expression with the evaluation node
        // means that only namespaces accessible from that node are in scope.
        // furthermore the namespace declarations themselves are not available.
        Resolver resolver = new NodePrefixResolver(args[1]);
        e.bind(resolver);
	Node saved = context.getContextNode();
	Node result;
	try{
	    context.setContextNode(args[1]);
	    result = e.evaluate(context);
	}finally{
	    context.setContextNode(saved);
	}
	return result;
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
