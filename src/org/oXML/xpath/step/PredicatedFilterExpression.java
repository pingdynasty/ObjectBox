package org.oXML.xpath.step;

import java.util.List;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.Expression;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.iterator.IteratedNodeset;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodesetNode;
import org.oXML.type.NodeIterator;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;

public class PredicatedFilterExpression implements Expression {
    private Expression fexp;
    private Predicate[] predicates;

    public PredicatedFilterExpression(Expression filterExpr, List predicates){
        this.fexp = filterExpr;
        this.predicates = new Predicate[predicates.size()];
        predicates.toArray(this.predicates);
    }

    public void bind(Resolver ctxt)
        throws ObjectBoxException{
	fexp.bind(ctxt);
        for(int i=0; i<predicates.length; ++i)
	    predicates[i].bind(ctxt);
    }

    public Node evaluate(RuntimeContext context)
        throws ObjectBoxException{

        Node result = fexp.evaluate(context);

//         if(!result.getType().instanceOf(NodesetNode.TYPE))
//             throw new XPathException("filter expression not a nodeset: "+fexp);

        context.setContextNode(result);
//          NodeIterator filtered = result.getChildNodes().getIterator();
//          Nodeset set = new IteratedNodeset(filtered);
        Nodeset set = result.getChildNodes();
        for(int i=0; i<predicates.length; ++i)
            set = predicates[i].eval(set, context);

        return new NodesetNode(set);
    }

    public String toString(){
        return getClass().getName()+"["+fexp+","+predicates+"]";
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = new org.xml.sax.helpers.AttributesImpl();
	handler.startElement(OUTPUT_NS, "filter-expression", "o:filter-expression", atts);
	fexp.write(handler);
        for(int i=0; i<predicates.length; ++i)
            predicates[i].write(handler);
	handler.endElement(OUTPUT_NS, "filter-expression", "o:filter-expression");
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
