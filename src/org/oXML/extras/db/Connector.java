package org.oXML.extras.db;

import org.oXML.type.Name;
import org.oXML.util.Log;

import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.template.Template;

/**
 * A Connector represents a datasource that can execute queries
 */
public interface Connector {

    public static final Name DEFAULT_NAME = new Name(DatabaseExtensions.DB_NS, "connection");

    /**
     * get the name of the SQL dialect preferred by this Connector
     */
    public String getDialect();

    /**
     * execute the statement
     * @param statement the statement to be executed
     * @param target the template of what the result should look like
     */
    public void execute(String statement, Template target, RuntimeContext context)
        throws ObjectBoxException;

    public void execute(String statement)
        throws DatabaseException;

//     public void connect()
//         throws DatabaseException;

//      public void close()
//          throws DatabaseException;
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
