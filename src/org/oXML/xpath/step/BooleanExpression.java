package org.oXML.xpath.step;

import org.oXML.type.Node;
import org.oXML.type.BooleanNode;
import org.oXML.xpath.Expression;
import org.oXML.xpath.Resolver;
import org.oXML.type.Nodeset;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.iterator.SingleNodeset;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;

/**
 */
public class BooleanExpression implements Expression
{
    public static final int AND = 0;
    public static final int OR = 1;
    public static final int NOT = 2;
    private static final String OPERATIONS[] = {"and", "or", "not"};

    private Expression lhs;
    private Expression rhs;
    private int op;

    public BooleanExpression(Expression lhs, Expression rhs, 
                             int operation) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.op = operation;
    }

    public BooleanExpression(Expression lhs) {
        this(lhs, null, NOT);
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
        boolean left = lhs.evaluate(ctxt).booleanValue();
        boolean result;
        switch(op){
        case NOT:
            result = !left;
            break;
        case AND:
            result = left && rhs.evaluate(ctxt).booleanValue();
            break;
        case OR:
            result = left || rhs.evaluate(ctxt).booleanValue();
            break;
        default:
            throw new XPathException("operation type not supported: "+getOperation());
        }
        
        return BooleanNode.booleanNode(result);
    }

    public String toString()
    {
        return getClass().getName()+"["+lhs+" "+getOperation()+" "+rhs+"]";
    }
    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
	String name  = getOperation();
	handler.startElement(OUTPUT_NS, name, "o:"+name, atts);
	lhs.write(handler);
	if(rhs != null)
	    rhs.write(handler);
	handler.endElement(OUTPUT_NS, name, "o:"+name);
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
