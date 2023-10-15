package org.oXML.engine.mapping.dom;

import java.util.List;
import java.util.ArrayList;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.WhenTemplate;
import org.oXML.engine.template.ChooseTemplate;
import org.oXML.engine.CompilationContext;
import org.oXML.ObjectBoxException;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.NodeIterator;
import org.oXML.xpath.Expression;
import org.tagbox.xml.NodeFinder;

public class ChooseMapping implements TemplateMapping{

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

        List whens = new ArrayList();
        NodeFinder finder = new NodeFinder(e.getNamespaceURI());
        NodeIterator it = finder.getElements(e, "when");
        for(Element when = (Element)it.nextNode(); when != null;
            when = (Element)it.nextNode()){
            String test = when.getAttribute("test");
            if(test.equals(""))
                throw new MappingException
                    (when, "missing required attribute: test");
	    Expression cond = env.parse(test);
	    cond.bind(env.getResolver(when));
	    Template body = env.getBody(when);
	    whens.add(new WhenTemplate(cond, body));
        }
        Node other = finder.getElement(e, "otherwise");
        if(other == null)
            return new ChooseTemplate(whens);
        Template otherwise = env.getBody(other);
        return new ChooseTemplate(whens, otherwise);
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
