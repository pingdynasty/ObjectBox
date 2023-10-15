package org.oXML.engine.mapping.dom;

import java.util.List;
import java.util.ArrayList;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.Parameter;
import org.oXML.engine.template.ProgramTemplate;
import org.oXML.engine.CompilationContext;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.NodeIterator;
import org.tagbox.xml.NodeFinder;

public class ProgramMapping implements TemplateMapping {

    private ParameterMapping paraMapping = new ParameterMapping();
    private static final String DEFAULT_CONTENT_TYPE = "text/xml";

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

	String value = e.getAttribute("comments");
	if(value.equalsIgnoreCase("ignore"))
	    env.ignoreComments(true);
	else if(value.equalsIgnoreCase("preserve"))
	    env.ignoreComments(false);
	else if(value.equals(""))
	    env.ignoreComments(true);
	else
	    throw new MappingException(e, "invalid program attribute 'comments': "+value);
	value = e.getAttribute("space");
	if(value.equalsIgnoreCase("ignore"))
	    env.ignoreWhitespace(true);
	else if(value.equalsIgnoreCase("preserve"))
	    env.ignoreWhitespace(false);
	else if(value.equals(""))
	    env.ignoreWhitespace(true);
	else
	    throw new MappingException(e, "invalid program attribute 'space': "+value);
	value = e.getAttribute("content-type");
	if(value.equals(""))
	    value = DEFAULT_CONTENT_TYPE;

        List list = new ArrayList();
        NodeFinder finder = new NodeFinder(e.getNamespaceURI());
        NodeIterator it = finder.getElements(e, "param");
        for(Element param = (Element)it.nextNode(); param != null;
            param = (Element)it.nextNode()){
            list.add(paraMapping.map(param, env));
 	    e.removeChild(param);
	}
        Parameter[] params = new Parameter[list.size()];
        list.toArray(params);

        Template body = env.getBody(e);
	ProgramTemplate program = new ProgramTemplate(params, body, value);
	env.setProgramTemplate(program);
	return program;
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
