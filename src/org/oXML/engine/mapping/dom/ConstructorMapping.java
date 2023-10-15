package org.oXML.engine.mapping.dom;

import java.util.List;
import java.util.ArrayList;
import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.oXML.xpath.function.FunctionCall;
import org.oXML.xpath.function.ConstructorCall;
import org.oXML.engine.CompilationContext;
import org.oXML.engine.template.Parameter;
import org.oXML.ObjectBoxException;
import org.tagbox.xml.NodeFinder;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Element;
import org.oXML.util.Log;

public class ConstructorMapping extends FunctionMapping{

    public Parameter[] getParents(NodeFinder finder, Element e, 
				  CompilationContext env)
        throws ObjectBoxException{
        List list = new ArrayList();
        NodeIterator it = finder.getElements(e, "parent");
        for(Element parent = (Element)it.nextNode(); parent != null;
            parent = (Element)it.nextNode()){
	    Resolver resolver = env.getResolver(parent);

            // get name
            String nm = parent.getAttribute("name");
            if(nm.equals(""))
                throw new MappingException
                    (parent, "missing required attribute: name");
            Name name = resolver.getName(nm);

            // get expression
            String expr = parent.getAttribute("select");
            if(expr.equals(""))
                throw new MappingException
		    (parent, "missing required attribute: select");
            Expression select = env.parse(expr);
	    if(!(select instanceof FunctionCall))
		throw new MappingException
		    (parent, "invalid parent initialiser expression: "+expr);
	    select = new ConstructorCall((FunctionCall)select);
	    select.bind(resolver);

	    // create Parameter with these values
            list.add(new Parameter(name, select));
        }
        Parameter[] parents = new Parameter[list.size()];
        list.toArray(parents);
        return parents;
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
