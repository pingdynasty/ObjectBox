package org.oXML.engine.mapping.dom;

import org.w3c.dom.Element;
import org.oXML.type.Node;
import org.oXML.type.Name;
import org.oXML.ObjectBoxException;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.ForEachTemplate;
import org.oXML.engine.CompilationContext;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;

public class ForEachMapping implements TemplateMapping {

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

	Resolver resolver = env.getResolver(e);
	Name name;
        String expr = e.getAttribute("name");
        if(expr.equals("")){
	    name = null;
	}else{
	    name = resolver.getName(expr);
	}

	Template body = env.getBody(e);
	expr = e.getAttribute("select");
	if(expr.equals("")){
	    expr = e.getAttribute("to");
	    if(expr.equals("")){
		expr = e.getAttribute("in");
		if(expr.equals(""))
		    throw new MappingException
			(e, "missing required attribute: need one of 'select', 'in' or 'to'");
		// get a 'in' / 'delim' iteration
		Expression in = env.evaluate(expr);
		in.bind(resolver);
		Expression delim = null;
		expr = e.getAttribute("delim");
		if(!expr.equals("")){
		    delim = env.evaluate(expr);
		    delim.bind(resolver);
		}
		return new ForEachTemplate(name, in, delim, body);
	    }else{
		// get a 'from' / 'to' / 'step' iteration
		Expression to = env.parse(expr);
		to.bind(resolver);
		Expression from = null;
		expr = e.getAttribute("from");
		if(!expr.equals("")){
		    from = env.parse(expr);
		    from.bind(resolver);
		}
		Expression step = null;
		expr = e.getAttribute("step");
		if(!expr.equals("")){
		    step = env.parse(expr);
		    step.bind(resolver);
		}
		return new ForEachTemplate(name, from, to, step, body);
	    }
	}else{
	    // get a 'select' iteration
	    Expression select = env.parse(expr);
	    select.bind(resolver);
	    return new ForEachTemplate(name, select, body);
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
