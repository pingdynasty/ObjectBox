package org.oXML.xpath.step;

import org.oXML.type.Node;
import org.oXML.type.NodesetNode;
import org.oXML.type.Nodeset;
import org.oXML.type.NodeIterator;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.iterator.IteratedNodeset;
import org.oXML.xpath.iterator.SingleNodeIterator;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

public class AbsoluteLocationPath implements LocationPath{
    RelativeLocationPath path;

    public AbsoluteLocationPath(){
        path = null;
    }

    public AbsoluteLocationPath(RelativeLocationPath path){
        this.path = path;
    }

    public void bind(Resolver ctxt)
	throws ObjectBoxException{
	path.bind(ctxt);
    }

    public Step[] getSteps(){
        if(path == null)
            return null;
        else
            return path.getSteps();
    }

    public Node evaluate(RuntimeContext ctxt)
        throws XPathException{
        // create the initial nodeset
        NodeIterator nit = new SingleNodeIterator(ctxt.getContextNode().getDocument());
        // evaluate steps
        nit = eval(nit, ctxt);
        Nodeset set = new IteratedNodeset(nit);
        // return result as primitive
        return new NodesetNode(set);
    }

    public NodeIterator eval(NodeIterator nit, RuntimeContext ctxt)
        throws XPathException{
        if(path == null)
            return nit;
        else
            return path.eval(nit, ctxt);
    }

    public String toString(){
        return getClass().getName()+"["+path+"]";
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
 	atts.addAttribute("", "name", "name", "CDATA", "absolute-location-path");
	handler.startElement("", "operation", "operation", atts);
	atts = new org.xml.sax.helpers.AttributesImpl();
	handler.startElement("", "param", "param", atts);
	path.write(handler);
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
