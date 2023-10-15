package org.oXML.xpath.step;

import org.oXML.type.Node;
import org.oXML.type.NumberNode;
import org.oXML.xpath.Expression;
import org.oXML.type.Nodeset;
import org.oXML.xpath.Resolver;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;

/**
 * tbd - how compare a nodeset and boolean ???
 * eg test="/foo/bar < true" ???
 * or even - how compare two booleans? or two strings - lexicographically??
 * xalan gave me false on 'true <= false', 'true > false' and '"abc" <= "abc"'.
 */
public class MathExpression implements Expression
{
    public static final int PLUS = 0;
    public static final int MINUS = 1;
    public static final int DIVISION = 2;
    public static final int MODULO = 3;
    public static final int MULTIPLICATION = 4;
    private static final String OPERATIONS[] = {"plus", "minus", "div", "mod", "mul"};

    private Expression lhs;
    private Expression rhs;
    private int op;

    public MathExpression(Expression lhs, Expression rhs, 
                          int operation)
    {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = operation;
    }


    public void bind(Resolver ctxt)
        throws ObjectBoxException{
	lhs.bind(ctxt);
	rhs.bind(ctxt);
    }

    public int operation()
    {
        return op;
    }

    public String getOperation()
    {
        return OPERATIONS[op];
    }

    public Node evaluate(RuntimeContext ctxt)
        throws ObjectBoxException{
        Node left = lhs.evaluate(ctxt);
        Node right = rhs.evaluate(ctxt);

        double result = compute(left.numberValue(), right.numberValue());
        return new NumberNode(result);
    }

    private double compute(double left, double right)
        throws XPathException
    {
        switch(op){
        case PLUS:
            return left + right;
        case MINUS:
            return left - right;
        case DIVISION:
            return left / right;
        case MODULO:
            return left % right;
        case MULTIPLICATION:
            return left * right;
        default:
            throw new XPathException("unknown operation type!");
        }
    }

    public String toString()
    {
        return getClass().getName()+"["+lhs+" "+getOperation()+" "+rhs+"]";
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
 	atts.addAttribute("", "name", "name", "CDATA", getOperation());
	handler.startElement("", "operation", "operation", atts);
	atts = new org.xml.sax.helpers.AttributesImpl();
	handler.startElement("", "param", "param", atts);
	lhs.write(handler);
	handler.endElement("", "param", "param");
	handler.startElement("", "param", "param", atts);
	rhs.write(handler);
	handler.endElement("", "param", "param");
	handler.endElement("", "operation", "operation");
// 	atts = new org.xml.sax.helpers.AttributesImpl();
// 	atts.addAttribute("", "ref", "ref", "CDATA", "Node");
// 	handler.startElement("", "type", "type", atts);
// 	handler.endElement("", "type", "type");
	
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
