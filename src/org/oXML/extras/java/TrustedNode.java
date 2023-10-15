package org.oXML.extras.java;

import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.type.Type;
import org.oXML.type.NodeIterator;
import org.oXML.type.Function;
import org.oXML.type.SimpleNode;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.function.XPathFunction;
import org.oXML.util.Log;

public class TrustedNode extends SimpleNode {

    private Node trusted;

    public TrustedNode(Node trusted, RuntimeContext ctxt) {
        super(new TrustedType(trusted, ctxt));
	this.trusted = trusted;
	setParent(trusted.getParent());
    }

    public TrustedNode(TrustedNode other, boolean deep) {
	super(other, deep);
	trusted = other.trusted.copy(deep);
	setParent(trusted.getParent());
    }

    public String stringValue(){
        return trusted.stringValue();
    }

    public double numberValue(){
        return trusted.numberValue();
    }

    public boolean booleanValue(){
        return trusted.booleanValue();
    }

    public Node copy(boolean deep){
        return new TrustedNode(this, deep);
    }

    public String toString(){
        return super.toString()+'<'+trusted+'>';
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
