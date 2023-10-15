package org.oXML.xpath.step;

import org.oXML.type.Node;
import org.oXML.type.NumberNode;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.iterator.SingleNodeset;
import org.oXML.type.Nodeset;

public class NumberExpression implements Expression
{
    private double value;

    public NumberExpression(Double value)
    {
        this.value = value.doubleValue();
    }

//      public Nodeset eval(RuntimeContext ctxt)
//          throws XPathException
//      {
//          return new SingleNodeset(new NumberNode(value));
//      }

    public void bind(Resolver ctxt)
        throws XPathException {}

    public Node evaluate(RuntimeContext ctxt)
        throws XPathException {
        return new NumberNode(value);
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
	handler.startElement("", "constant", "constant", atts);
	// output type
	atts = new org.xml.sax.helpers.AttributesImpl();
	handler.startElement("", "type", "type", atts);
 	atts.addAttribute("", "ref", "ref", "CDATA", "Number");
	handler.endElement("", "type", "type");
	// output value
	atts = new org.xml.sax.helpers.AttributesImpl();
	handler.startElement("", "value", "value", atts);
	char[] chars = Double.toString(value).toCharArray();
	handler.characters(chars, 0, chars.length);
	// close
	handler.endElement("", "value", "value");
	handler.endElement("", "constant", "constant");
    }

    public String toString()
    {
        return getClass().getName()+"["+value+"]";
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
