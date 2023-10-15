package org.oXML.xpath.step;

import org.oXML.xpath.Resolver;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.Expression;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.iterator.IteratedNodeset;
import org.oXML.xpath.iterator.PredicateIterator;
import org.oXML.type.Nodeset;
import org.oXML.ObjectBoxException;

public class Predicate{
    private Expression expr;

    public Predicate(Expression expr){
        this.expr = expr;
    }

    public void bind(Resolver ctxt)
        throws ObjectBoxException{
	expr.bind(ctxt);
    }

    public Nodeset eval(Nodeset parent, RuntimeContext ctxt)
        throws XPathException{
        // tbd take a copy of the context to evaluate the expression
        // in the current state ??
        Nodeset value = new IteratedNodeset(new PredicateIterator(parent, expr, ctxt));
	value.size(); // iterate to the end _immediately_ to ensure the expression is evaluated now
	return value;
   }

    public String toString(){
        return getClass().getName()+"["+expr+"]";
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = new org.xml.sax.helpers.AttributesImpl();
	handler.startElement(Expression.OUTPUT_NS, "predicate", "o:predicate", atts);
	expr.write(handler);
	handler.endElement(Expression.OUTPUT_NS, "predicate", "o:predicate");
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
