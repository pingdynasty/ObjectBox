package org.oXML.xpath.step;

import java.util.List;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.axis.Axis;
import org.oXML.xpath.XPathException;
import org.oXML.engine.RuntimeContext;
import org.oXML.type.Nodeset;
import org.oXML.type.NodeIterator;
import org.oXML.xpath.iterator.IteratedNodeset;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

/**
 * represents an XPath Step with one or more Predicates
 */
public class PredicatedStep extends Step{
    private Predicate[] predicates;

    public PredicatedStep(Axis axis, NodeTest test, List predicates){
        super(axis, test);
        this.predicates = new Predicate[predicates.size()];
        predicates.toArray(this.predicates);
    }

    public PredicatedStep(NodeTest test, List predicates){
        super(test);
        this.predicates = new Predicate[predicates.size()];
        predicates.toArray(this.predicates);
    }

    public void bind(Resolver ctxt)
	throws ObjectBoxException {
	super.bind(ctxt);
        for(int i=0; i<predicates.length; ++i)
            predicates[i].bind(ctxt);
    }

    public NodeIterator eval(NodeIterator parent, RuntimeContext ctxt)
        throws XPathException{
        NodeIterator p = super.eval(parent, ctxt);
        Nodeset set = new IteratedNodeset(p);
        for(int i=0; i<predicates.length; ++i)
            set = predicates[i].eval(set, ctxt);

        return set.getIterator();
    }

    public String toString(){
        return super.toString()+"["+predicates+"]";
    }


    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = new org.xml.sax.helpers.AttributesImpl();
	atts.addAttribute("", "axis", "axis", "CDATA", getAxis().getType());
	handler.startElement(OUTPUT_NS, "step", "o:step", atts);
	getNodeTest().write(handler);
        for(int i=0; i<predicates.length; ++i)
            predicates[i].write(handler);
	handler.endElement(OUTPUT_NS, "step", "o:step");
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
