package org.oXML.xpath.step;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import org.oXML.type.Nodeset;
import org.oXML.type.NodeIterator;
import org.oXML.type.Node;
import org.oXML.type.NodesetNode;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.XPathException;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.iterator.IteratedNodeset;
import org.oXML.xpath.iterator.SingleNodeIterator;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

public class RelativeLocationPath implements LocationPath {

    private List steps;

    public RelativeLocationPath(Step step){
        steps = new ArrayList();
        steps.add(step);
    }

    public void bind(Resolver ctxt)
	throws ObjectBoxException{
        Iterator it = steps.iterator();
        while(it.hasNext()){
            Step step = (Step)it.next();
	    step.bind(ctxt);
	}
    }

    public void insert(Step step){
        steps.add(0, step);
    }

    public void add(Step step){
        steps.add(step);
    }

    public Step[] getSteps(){
        Step[] s = new Step[steps.size()];
        steps.toArray(s);
        return s;
    }

    public Node evaluate(RuntimeContext context)
        throws XPathException{
        // create the initial nodeset
        NodeIterator nit = new SingleNodeIterator(context.getContextNode());
        // evaluate steps
        nit = eval(nit, context);
        Nodeset set = new IteratedNodeset(nit);
        // return result as primitive
        return new NodesetNode(set);
    }

    protected NodeIterator eval(NodeIterator nit, RuntimeContext context)
        throws XPathException{
        Iterator it = steps.iterator();
        while(it.hasNext()){
            Step step = (Step)it.next();
            nit = step.eval(nit, context);
        }
        return nit;
    }

    public String toString() {
        return getClass().getName()+'['+steps+']';
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
 	atts.addAttribute("", "name", "name", "CDATA", "relative-location-path");
	handler.startElement("", "operation", "operation", atts);
	atts = new org.xml.sax.helpers.AttributesImpl();
        Iterator it = steps.iterator();
        while(it.hasNext()){
	    handler.startElement("", "param", "param", atts);
            Step step = (Step)it.next();
	    step.write(handler);
	    handler.endElement("", "param", "param");
	}
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
