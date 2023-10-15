package org.oXML.xpath;

import java.util.List;
import java.util.ArrayList;
import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;
import org.oXML.type.Node;
import org.oXML.type.StringNode;
import org.oXML.util.Log;

public class MixedExpression implements Expression{
    private Expression[] expressions;

    public MixedExpression(Expression[] expressions){
	this.expressions = expressions;
    }

    public MixedExpression(List list){
        expressions = new Expression[list.size()];
        list.toArray(expressions);
    }

    public void bind(Resolver ctxt) 
        throws ObjectBoxException {
        for(int i=0; i<expressions.length; ++i)
	    expressions[i].bind(ctxt);
    }

    public Node evaluate(RuntimeContext ctxt)
        throws ObjectBoxException{
        StringBuffer buf = new StringBuffer();
        for(int i=0; i<expressions.length; ++i)
            buf.append(expressions[i].evaluate(ctxt).stringValue());
        return new StringNode(buf.toString());
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
	handler.startElement(OUTPUT_NS, "mixed-expression", "o:mixed-expression", atts);
        for(int i=0; i<expressions.length; ++i)
            expressions[i].write(handler);
	handler.endElement(OUTPUT_NS, "mixed-expression", "o:mixed-expression");
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
