package org.oXML.engine.mapping.dom;

import org.oXML.engine.template.Template;
import org.oXML.engine.template.ReturnTemplate;
import org.oXML.engine.CompilationContext;
import org.oXML.ObjectBoxException;
import org.oXML.xpath.Expression;
import org.w3c.dom.Element;

public class ReturnMapping implements TemplateMapping{

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

        String expr = e.getAttribute("select");
        if(expr.equals("")){
            Template body = env.getBody(e);
	    return new ReturnTemplate(body);
        }else{
            Expression select = env.parse(expr);
	    select.bind(env.getResolver(e));
            return new ReturnTemplate(select);
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
