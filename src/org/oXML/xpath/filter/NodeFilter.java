package org.oXML.xpath.filter;

import org.oXML.type.Node;
import org.oXML.xpath.XPathException;

public interface NodeFilter{

    public static final int SHOW_DOCUMENT = 0x1;
    public static final int SHOW_ELEMENT = 0x2;
    public static final int SHOW_ATTRIBUTE = 0x4;
    public static final int SHOW_TEXT = 0x8;
    public static final int SHOW_CDATA_SECTION = 0x10;
    public static final int SHOW_ENTITY = 0x20;
    public static final int SHOW_ENTITY_REFERENCE = 0x40;
    public static final int SHOW_PROCESSING_INSTRUCTION = 0x80;
    public static final int SHOW_COMMENT = 0x100;
    public static final int SHOW_DOCUMENT_TYPE = 0x200;
    public static final int SHOW_NOTATION = 0x400;
    public static final int SHOW_ALL = 0x7ff;

    public boolean acceptNode(Node p)
        throws XPathException;
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
