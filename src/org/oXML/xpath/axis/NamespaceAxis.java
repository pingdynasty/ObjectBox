package org.oXML.xpath.axis;

import org.oXML.xpath.filter.NodeFilter;
import org.oXML.xpath.step.NodeTest;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.type.NodeIterator;
import org.oXML.xpath.iterator.NamespaceIterator;

public class NamespaceAxis extends Axis
{
    public NamespaceAxis()
    {
        super(NAMESPACE);
    }

    public NodeIterator eval(NodeIterator parent, NodeTest test, RuntimeContext context)
        throws XPathException
    {
        NamespaceIterator nit = 
            new NamespaceIterator(parent, test.eval(context));
        return nit;
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
