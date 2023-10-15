package org.oXML.xpath.axis;

import org.oXML.xpath.step.NodeTest;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.type.NodeIterator;

public abstract class Axis
{
    public static final int ANCESTOR           =  0;
    public static final int ANCESTOR_OR_SELF   =  1;
    public static final int ATTRIBUTE          =  2;
    public static final int CHILD              =  3;
    public static final int DESCENDANT         =  4;
    public static final int DESCENDANT_OR_SELF =  5;
    public static final int FOLLOWING          =  6;
    public static final int FOLLOWING_SIBLING  =  7;
    public static final int NAMESPACE          =  8;
    public static final int PARENT             =  9;
    public static final int PRECEDING          = 10;
    public static final int PRECEDING_SIBLING  = 11;
    public static final int SELF               = 12;

    private static final String[] AXIS_NAMES = {
	"ancestor",
	"ancestor-or-self",
	"attribute",
	"child",
	"descendant",
	"descendant-or-self",
	"following",
	"following-sibling",
	"namespace",
	"parent",
	"preceding",
	"preceding-sibling",
	"self"
    };

    private int type;

    protected Axis(int type)
    {
        this.type = type;
    }

    public int getType()
    {
        return type;
    }
    
    public String getTypeName()
    {
        return AXIS_NAMES[type];
    }

    public abstract NodeIterator eval(NodeIterator parent, NodeTest test,
                                 RuntimeContext context)
        throws XPathException;

    public String toString()
    {
        return getClass().getName()+'['+getType()+']';
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
