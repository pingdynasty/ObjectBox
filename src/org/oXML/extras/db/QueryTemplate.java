package org.oXML.extras.db;

import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.engine.template.Template;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.engine.mapping.dom.MappingException;
import org.oXML.xpath.Expression;
import org.oXML.util.Log;

public class QueryTemplate implements Template{

    private Name name;
    private Query query;

    public QueryTemplate(Name name, Query query) {
	this.name = name;
	this.query = query;
    }

    public void process(RuntimeContext env)
        throws ObjectBoxException{

        // moved to QueryMapping
// 	ConnectionDirector dir = ConnectionDirector.getInstance();
// 	if(dir.hasQuery(name)){
// 	    Query other = dir.getQuery(name);
// 	    if(other != query)
// 		Log.warning("multiple definition of query: "+name);
// 	}
// 	dir.setQuery(name, query);
    }

    public String toString(){
        return getClass().getName()+'<'+name+'>';
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
