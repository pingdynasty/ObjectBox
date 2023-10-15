package org.oXML.xpath.variable;

import java.util.List;
import org.oXML.type.Node;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.parser.QName;
import org.oXML.type.Name;
import org.oXML.util.Log;

public class VariableReference implements Expression {

    private QName qname;
    private Name name;

    public VariableReference(QName qname) {
        this.qname = qname;
    }

    public void bind(Resolver ctxt)
        throws XPathException {
	name = ctxt.getName(qname);
	qname = null;
    }

    public Node evaluate(RuntimeContext ctxt)
        throws XPathException{
	assert name != null : qname;
        return ctxt.getVariable(name);
    }
    
    public String toString(){
        return getClass().getName()+'['+name != null ? name.toString() : qname.toString() +']';
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
	atts.addAttribute("", "name", "name", "CDATA", qname.toString());
	handler.startElement("", "reference", "reference", atts);
	handler.endElement("", "reference", "reference");
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
