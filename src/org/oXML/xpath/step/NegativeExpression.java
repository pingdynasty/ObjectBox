package org.oXML.xpath.step;

import org.oXML.type.Node;
import org.oXML.type.NumberNode;
import org.oXML.xpath.Expression;
import org.oXML.type.Nodeset;
import org.oXML.xpath.Resolver;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.iterator.SingleNodeset;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;

public class NegativeExpression implements Expression
{
    private Expression expr;

    public NegativeExpression(Expression expr)
    {
        this.expr = expr;
    }

    public void bind(Resolver ctxt)
        throws ObjectBoxException{
	expr.bind(ctxt);
    }

    public Node evaluate(RuntimeContext ctxt)
        throws ObjectBoxException{
        Node node = expr.evaluate(ctxt);
        double result = node.numberValue();
        return new NumberNode(-result);
    }

    public String toString()
    {
        return getClass().getName()+"[-"+expr+"]";
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
 	atts.addAttribute("", "name", "name", "CDATA", "not");
	handler.startElement("", "operation", "operation", atts);
	atts = new org.xml.sax.helpers.AttributesImpl();
	handler.startElement("", "param", "param", atts);
	expr.write(handler);	
	handler.endElement("", "param", "param");
	handler.endElement("", "operation", "operation");
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
