package org.oXML.extras.java;

import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.engine.mapping.dom.TemplateMapping;
import org.oXML.engine.CompilationContext;
import org.oXML.engine.mapping.dom.MappingException;
import org.oXML.ObjectBoxException;
import org.oXML.engine.template.Template;
import org.w3c.dom.Element;
import org.oXML.xpath.Expression;
import org.oXML.xpath.Resolver;
import org.oXML.util.Log;

public class WriteMapping implements TemplateMapping{

    public static final Name NAME = 
    new Name(ReflectionTypeResolver.JAVA_NS, "write");

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

        String name = e.getAttribute("filename");
        if(name.equals(""))
            throw new MappingException
                (e, "missing required attribute: filename");

	Expression filename = env.evaluate(name);
	filename.bind(env.getResolver(e));
	name = e.getAttribute("select");
        if(name.equals("")){
            Template body = env.getBody(e);
            return new WriteTemplate(filename, body);
        }else{
            Expression select = env.parse(name);
	    select.bind(env.getResolver(e));
            return new WriteTemplate(filename, select);
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
