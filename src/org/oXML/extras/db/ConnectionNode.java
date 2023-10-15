package org.oXML.extras.db;

import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Function;
import org.oXML.type.Node;
import org.oXML.type.AbstractNode;
import org.oXML.util.Log;

public class ConnectionNode extends AbstractNode{

    public static final Type TYPE = 
	new Type(new Name(DatabaseExtensions.DB_NS, "connection"),
		 new Type[]{Node.TYPE});

    private Connector connector;

    public ConnectionNode(Connector connector){
        super(TYPE);
	this.connector = connector;
    }

    public ConnectionNode(AbstractNode me, Type type, ConnectionNode other){
	super(me, type);
        connector = other.connector;
    }

    public ConnectionNode(ConnectionNode other, boolean deep){
	super(other.getType());
	connector = other.connector;
    }

    public String toString(){
        return super.toString()+'<'+connector+'>';
    }

    public Node copy(boolean deep){
	return new ConnectionNode(this, deep);
    }

    public AbstractNode copy(AbstractNode me, Type type, boolean deep){
        return new ConnectionNode(me, type, this);
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
