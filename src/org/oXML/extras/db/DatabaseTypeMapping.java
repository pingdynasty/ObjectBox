package org.oXML.extras.db;

import java.util.List;
import java.util.ArrayList;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Variable;
import org.oXML.engine.mapping.dom.TypeMapping;
import org.oXML.engine.mapping.dom.TemplateMapping;
import org.oXML.engine.CompilationContext;
import org.oXML.engine.mapping.dom.MappingException;
import org.oXML.ObjectBoxException;
import org.oXML.engine.template.Template;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.tagbox.xml.NodeFinder;
import org.oXML.util.Log;

public class DatabaseTypeMapping implements TemplateMapping {

    public static final Name NAME = 
	new Name(CompilationContext.OXML_NS, "type");

    private TemplateMapping typeMapping;
    private QueryFunctionMapping queryFunctionMapping;

    public DatabaseTypeMapping(){
        typeMapping = new TypeMapping();
        queryFunctionMapping = new QueryFunctionMapping();
    }

    public Template map(Element e, CompilationContext ctxt)
        throws ObjectBoxException{

        // get hold of and invoke the core o:XML type mapping
//         TemplateMapping typeMapping = ctxt.getMapping(NAME);
        // invoke the core o:XML type mapping
        Template result = typeMapping.map(e, ctxt);

        // add in any db:function declarations
        NodeFinder finder = new NodeFinder(DatabaseExtensions.DB_NS);
        NodeIterator it = finder.getElements(e, "function");
        for(Element f = (Element)it.nextNode(); f != null;
            f = (Element)it.nextNode()){
            queryFunctionMapping.map(f, ctxt);
	}
	return result;
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
