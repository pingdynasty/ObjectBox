package org.oXML.extras.db;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.engine.mapping.dom.TemplateMapping;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;
import org.oXML.engine.LanguageExtension;

/**
 * self-configured one-stop-shop for db extensions
 */
public class DatabaseExtensions implements LanguageExtension{

    private Map mappings;
    private List functions;
    public static final String DB_NS  ="http://www.o-xml.com/db/";
//     public static final String DB_NS  ="http://www.o-xml.org/namespace/db/";

    public DatabaseExtensions()
	throws ObjectBoxException{

        mappings = new HashMap();

        // db:query
	TemplateMapping mapping = new QueryMapping();
	mappings.put(QueryMapping.NAME, mapping);

        // db:connection
	mapping = new ConnectionMapping();
	mappings.put(ConnectionMapping.NAME, mapping);

        // db:execute
	mapping = new ExecuteMapping();
	mappings.put(ExecuteMapping.NAME, mapping);

        // db:call
	mapping = new CallMapping();
	mappings.put(CallMapping.NAME, mapping);

        // db:function
	mapping = new QueryFunctionMapping();
	mappings.put(QueryFunctionMapping.NAME, mapping);

        // o:type/db:function
        mapping = new DatabaseTypeMapping();
	mappings.put(DatabaseTypeMapping.NAME, mapping);

        // add some db: namespace functions
        functions = new ArrayList();

        functions.add(new QuoteFunction());
    }

    public Map getMappings(){
        return mappings;
    }

    public List getFunctions(){
        return functions;
    }

    public String toString(){
	return getClass().getName();
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
