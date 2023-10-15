package org.oXML.engine.mapping.dom;

import java.util.List;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.xpath.Resolver;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.CatchTemplate;
import org.oXML.engine.CompilationContext;
import org.oXML.ObjectBoxException;
import org.w3c.dom.Element;
import org.oXML.util.Log;
import org.oXML.xpath.Expression;

public class CatchMapping implements TemplateMapping{

    public static final String DEFAULT_HANDLER = "ExceptionHandler()";

    public Template map(Element e, CompilationContext ctxt)
        throws ObjectBoxException{

	String expr = e.getAttribute("exceptions");

	Resolver resolver = ctxt.getResolver(e);
	List exceptions = new ArrayList();
	StringTokenizer tok = new StringTokenizer(expr, ", \t\n");
	while(tok.hasMoreTokens()){
	    String nm = (String)tok.nextToken();
	    Name name = resolver.getName(nm);
	    Type exception = ctxt.getType(name);
	    exceptions.add(exception);
	}

	Expression handler;
        expr = e.getAttribute("handler");
	if(expr.equals("")){
	    handler = ctxt.parse(DEFAULT_HANDLER);
	}else{
	    handler = ctxt.parse(expr);
	}
	handler.bind(resolver);
	Template content = ctxt.getBody(e);
	return new CatchTemplate(content, exceptions, handler);
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
