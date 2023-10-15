package org.oXML.extras.db;

import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.engine.mapping.dom.TemplateMapping;
import org.oXML.engine.CompilationContext;
import org.oXML.engine.mapping.dom.MappingException;
import org.oXML.ObjectBoxException;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.Parameter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.tagbox.xml.NodeFinder;
import org.oXML.util.Log;

public class ConnectionMapping implements TemplateMapping{

    public static final Name NAME = 
	new Name(DatabaseExtensions.DB_NS, "connection");

    private static final int DEFAULT_POOL_MIN = 0;
    private static final int DEFAULT_POOL_MAX = 10;
    private static final int DEFAULT_RETRY = 0;

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

	Name connectionName;
        String name = e.getAttribute("name");
	Resolver resolver = env.getResolver(e);
        if(name.equals(""))
	    connectionName = Connector.DEFAULT_NAME;
	else
	    connectionName = resolver.getName(name);

        NodeFinder finder = new NodeFinder(e.getNamespaceURI());

	String driver = finder.getElementValue(e, "driver");
        if(driver == null)
            throw new MappingException(e, "missing element: driver");
	String url = finder.getElementValue(e, "url");
        if(url == null)
            throw new MappingException(e, "missing element: url");
	String user = finder.getElementValue(e, "username");
        if(user == null)
	    user = "";
	String passwd = finder.getElementValue(e, "password");
        if(passwd == null)
	    passwd = "";
	String log = e.getAttribute("log");
	String dialect = e.getAttribute("dialect");

	int min = DEFAULT_POOL_MIN;
	int max = DEFAULT_POOL_MAX;
	Element pool = finder.getElement(e, "pool");
	if(pool != null){
	    name = pool.getAttribute("minFree");
	    min = Integer.parseInt(name);
	    name = pool.getAttribute("maxFree");
	    max = Integer.parseInt(name);
	}

	int retry = DEFAULT_RETRY;
	Element transaction = finder.getElement(e, "transaction");
	if(transaction != null){
	    name = transaction.getAttribute("retry");
	    retry = Integer.parseInt(name);
	}

	Expression[] args = new Expression[]{
	    env.evaluate(driver),
	    env.evaluate(url), 
	    env.evaluate(user), 
	    env.evaluate(passwd),
	    env.evaluate(log),
	    env.evaluate(dialect)
	};
	for(int i=0; i<args.length; ++i)
	    args[i].bind(resolver);
	return new ConnectionTemplate(connectionName, args, min, max, retry);
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
