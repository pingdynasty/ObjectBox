package org.oXML.extras.db;

import java.util.List;
import java.util.ArrayList;
import org.oXML.type.Name;
import org.oXML.engine.mapping.dom.TemplateMapping;
import org.oXML.engine.CompilationContext;
import org.oXML.engine.mapping.dom.MappingException;
import org.oXML.ObjectBoxException;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.Parameter;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.oXML.util.Log;

public class ExecuteMapping implements TemplateMapping{

    public static final Name NAME = 
	new Name(DatabaseExtensions.DB_NS, "execute");

    public Template map(Element e, CompilationContext ctxt)
        throws ObjectBoxException{

	Resolver resolver = ctxt.getResolver(e);
        String name = e.getAttribute("connection");
	Name con;
        if(name.equals(""))
	    con = Connector.DEFAULT_NAME;
	else
	    con = resolver.getName(name);

	name = e.getAttribute("sql");
	if(name.equals(""))
	    throw new MappingException(e, "missing required attribute: sql");
	Expression stmt = ctxt.evaluate(name);
	stmt.bind(resolver);
	if(!e.hasChildNodes())
	    // no result nodes, simple execute
	    return new ExecuteTemplate(resolver.getLocation(), con, stmt);

        boolean s_text = ctxt.textSubstitution();
        boolean s_attr = ctxt.attributeSubstitution();
        ctxt.textSubstitution(true);
        ctxt.attributeSubstitution(true);

	Template target = ctxt.getBody(e);
        ctxt.textSubstitution(s_text);
        ctxt.attributeSubstitution(s_attr);
	return new ExecuteTemplate(resolver.getLocation(), con, stmt, target);
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
