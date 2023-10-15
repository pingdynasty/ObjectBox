package org.oXML.xpath.step;

import org.oXML.xpath.Resolver;
import org.oXML.xpath.filter.NodeFilter;
import org.oXML.xpath.filter.NodeTypeFilter;
import org.oXML.engine.RuntimeContext;
import org.oXML.util.Log;

public class NodeType implements NodeTest
{
    public static final int COMMENT = 0;
    public static final int TEXT = 1;
    public static final int PROCESSING_INSTRUCTION = 2;
    public static final int NODE = 3;

    private static final String[] NODE_TYPES =
    {"COMMENT", "TEXT", "PROCESSING_INSTRUCTION", "NODE"};

    private int type;

    public NodeType(int type)
    {
        this.type = type;
    }

    public void bind(Resolver ctxt) {}

    public NodeFilter eval(RuntimeContext context)
    {
        return new NodeTypeFilter(type);
    }

    public boolean principal()
    {
        return false;
    }

    public static String typeName(int type){
        return type < NODE_TYPES.length ? NODE_TYPES[type] : null;
    }

    public String toString()
    {
        return getClass().getName()+"["+NODE_TYPES[type]+"]";
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
	atts.addAttribute("", "type", "type", "CDATA", NODE_TYPES[type]);
	handler.startElement(OUTPUT_NS, "node-type", "o:node-type", atts);
	handler.endElement(OUTPUT_NS, "node-type", "o:node-type");
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
