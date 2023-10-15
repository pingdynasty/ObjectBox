package org.oXML.engine.mapping.dom;

import org.w3c.dom.Node;
import org.oXML.ObjectBoxException;

public class MappingException extends ObjectBoxException {
    Node context;

    public MappingException(Node context, Throwable cause)
    {
        super(cause);
        this.context = context;
    }

    public MappingException(Node context, String msg, Throwable cause)
    {
        super(msg, cause);
        this.context = context;
    }

    public MappingException(Node context, String msg)
    {
        super(msg);
        this.context = context;
    }

    public Node getContextNode()
    {
        return context;
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
