package org.oXML.engine.mapping.dom;

import org.w3c.dom.*;
import org.oXML.type.Name;
import org.oXML.type.*;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.AttributeTemplate;
import org.oXML.engine.CompilationContext;
import org.oXML.ObjectBoxException;
import org.w3c.dom.Element;

public class AttributeMapping implements TemplateMapping{

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{
        String attr = e.getAttribute("name");
        if(attr.equals(""))
            throw new MappingException(e, "element declaration missing required attribute: name");

	Resolver resolver = env.getResolver(e);
        Expression name = env.evaluate(attr);
	name.bind(resolver);

        attr = e.getAttribute("namespace");
	Expression namespace = null;
        if(!attr.equals("")){
	    namespace = env.evaluate(attr);
	    namespace.bind(resolver);
	}

	Expression value;
        attr = e.getAttribute("select");
        if(attr.equals("")){
	    attr = e.getAttribute("value");
	    if(attr.equals("")){
		Template body = env.getBody(e);
		return new AttributeTemplate(namespace, name, body);
	    }else{
		value = env.evaluate(attr);
	    }
	}else{
	    value = env.parse(attr);
	}
	value.bind(resolver);
	return new AttributeTemplate(namespace, name, value);
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
