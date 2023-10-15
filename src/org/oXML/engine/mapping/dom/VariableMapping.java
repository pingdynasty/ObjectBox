package org.oXML.engine.mapping.dom;

import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.xpath.Resolver;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.VariableTemplate;
import org.oXML.engine.CompilationContext;
import org.oXML.ObjectBoxException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.oXML.util.Log;
import org.oXML.xpath.Expression;

/**
 */
public class VariableMapping implements TemplateMapping {

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

	Resolver resolver = env.getResolver(e);

	// get name
	String nm = e.getAttribute("name");
	if(nm.equals(""))
	    throw new MappingException
		(e, "missing required attribute: name");
	Name name = resolver.getName(nm);

        // get type
        String typename = e.getAttribute("type");
        Type type;
        if(typename.equals("")){
            type = org.oXML.type.Node.TYPE;
        }else{
            type = env.getType(resolver.getName(typename));
            if(type == null)
                throw new MappingException(e, "unknown type: "+typename);
        }

	String expr = e.getAttribute("select");
	if(expr.equals("")){
	    Template body = env.getBody(e);
	    return new VariableTemplate(name, type, body);
	}else{
	    Expression select = env.parse(expr);
	    select.bind(resolver);
	    return new VariableTemplate(name, type, select);
	}
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
