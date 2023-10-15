package org.oXML.engine.mapping.dom;

import java.util.List;
import java.util.ArrayList;
import org.oXML.xpath.Expression;
import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.engine.template.Parameter;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.FunctionTemplate;
import org.oXML.engine.template.DynamicFunction;
import org.oXML.engine.CompilationContext;
import org.oXML.ObjectBoxException;
import org.tagbox.xml.NodeFinder;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Element;

public class FunctionMapping implements TemplateMapping{

    private ParameterMapping paraMapping;

    public FunctionMapping(){
        paraMapping = new ParameterMapping();
    }

    public Parameter[] getParameters(NodeFinder finder, Element e, CompilationContext env)
        throws ObjectBoxException{
        List list = new ArrayList();
        NodeIterator it = finder.getElements(e, "param");
        for(Element param = (Element)it.nextNode(); param != null;
            param = (Element)it.nextNode())
            list.add(paraMapping.map(param, env));
        Parameter[] parameters = new Parameter[list.size()];
        list.toArray(parameters);
        return parameters;
    }

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

        String nm = e.getAttribute("name");
        if(nm.equals(""))
            throw new MappingException
		(e, "missing required attribute: name");                
        Name name = env.getResolver(e).getName(nm);

        NodeFinder finder = new NodeFinder(e.getNamespaceURI());

        Parameter[] params = getParameters(finder, e, env);

        Element content = finder.getElement(e, "do");
        if(content == null)
            throw new MappingException
		(e, "missing required element: do");                
	Template body = env.getBody(content);

        env.addFunction(new DynamicFunction(name, params, body));

        return new FunctionTemplate(name, body, params);
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
