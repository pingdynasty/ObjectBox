package org.oXML.extras.db;

import java.util.Map;
import java.util.HashMap;
import org.oXML.type.Name;

public class ConnectionDirector
{
    private static ConnectionDirector me;
    private Map connections;
    private Map queries;

    private ConnectionDirector(){
	connections = new HashMap();
	queries = new HashMap();
    }

    public static ConnectionDirector getInstance(){
	if(me == null)
	    me = new ConnectionDirector();
	return me;
    }

    public Connector getConnector(Name name){
	return (Connector)connections.get(name);
    }

     public void setConnector(Name name, Connector connection){
 	connections.put(name, connection);
     }

    public boolean hasConnector(Name name){
	return connections.containsKey(name);
    }

    public Query getQuery(Name name){
	return (Query)queries.get(name);
    }

    public void setQuery(Name name, Query query){
	queries.put(name, query);
    }    

    public boolean hasQuery(Name name){
	return queries.containsKey(name);
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
