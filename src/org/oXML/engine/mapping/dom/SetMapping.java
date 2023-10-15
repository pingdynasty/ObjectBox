package org.oXML.engine.mapping.dom;

import java.util.List;
import java.util.ArrayList;
import org.oXML.type.Name;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.SetTemplate;
import org.oXML.engine.CompilationContext;
import org.oXML.ObjectBoxException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.oXML.util.Log;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;

import org.w3c.dom.NamedNodeMap;
import org.oXML.engine.template.Parameter;

/**
 */
public class SetMapping implements TemplateMapping {

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

	Resolver resolver = env.getResolver(e);
	List assignments = new ArrayList();
        NamedNodeMap nnm = e.getAttributes();
        for(int i = 0; i < nnm.getLength(); i++){
            Node attr = nnm.item(i);
	    String ns = attr.getNamespaceURI();
	    if(!"http://www.w3.org/2000/xmlns/".equals(ns)){
		Name name = new Name(ns, attr.getLocalName());
		String value = attr.getNodeValue();
		Expression expr = env.parse(value);
		expr.bind(resolver);
		assignments.add(new Parameter(name, expr));
	    }
	}

	return new SetTemplate(assignments);
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
