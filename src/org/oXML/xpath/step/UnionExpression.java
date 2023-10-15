package org.oXML.xpath.step;

import org.oXML.type.Node;
import org.oXML.type.NodesetNode;
import org.oXML.type.NodeIterator;
import org.oXML.xpath.Resolver;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.Expression;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.iterator.UnionIterator;
import org.oXML.xpath.iterator.IteratedNodeset;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

public class UnionExpression implements Expression
{
    private Expression lhs;
    private Expression rhs;

    public UnionExpression(Expression lhs, Expression rhs)
    {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void bind(Resolver ctxt)
        throws ObjectBoxException{
	lhs.bind(ctxt);
	rhs.bind(ctxt);
    }

    public Node evaluate(RuntimeContext context)
        throws ObjectBoxException{
        return new NodesetNode(new IteratedNodeset(eval(context)));
    }

    public NodeIterator eval(RuntimeContext ctxt)
        throws ObjectBoxException{
        Node left = lhs.evaluate(ctxt);
        if(!left.getType().instanceOf(NodesetNode.TYPE))
            throw new XPathException("union operand not a nodeset: "+lhs);

        // tbd should the ctxt be reset between lhs/rhs evaluate() ??
//          ctxt.getContextNode().getChildNodes().reset();

        Node right = rhs.evaluate(ctxt);
        if(!right.getType().instanceOf(NodesetNode.TYPE))
            throw new XPathException("union operand not a nodeset: "+rhs);

        return new UnionIterator(left.getChildNodes().getIterator(), right.getChildNodes().getIterator());
    }

    public String toString()
    {
        return getClass().getName()+"["+lhs+" | "+rhs+"]";
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
	handler.startElement(OUTPUT_NS, "union-expression", "o:union-expression", atts);
	lhs.write(handler);	
	rhs.write(handler);	
	handler.endElement(OUTPUT_NS, "union-expression", "o:union-expression");
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
