package org.oXML.extras.java.function;

import org.w3c.dom.DocumentFragment;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.function.XPathFunction;
import org.oXML.ObjectBoxException;
import org.oXML.extras.java.ReflectionNode;
import org.oXML.util.Log;

public class ToDOMFunction extends XPathFunction{

    private Converter converter;
    private Type type;

    public ToDOMFunction(Name name, Type[] sig, Type type){
        super(name, sig);
        this.type = type;
        converter = new Converter();
    }

    public Node eval(Node args[], RuntimeContext ctxt)
        throws XPathException {
	try{
	    DocumentFragment dom = converter.fromNative(args[0]);
	    if(dom.getChildNodes().getLength() == 1)
		return new ReflectionNode(type, dom.getChildNodes().item(0));
	    else
		return new ReflectionNode(type, dom);
	}catch(ObjectBoxException exc){
	    throw new XPathException(exc);
	}
    }

    public Node eval(Node node, Node args[], RuntimeContext ctxt)
        throws XPathException {
        return eval(args, ctxt);
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
