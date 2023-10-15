package org.oXML.extras.db;

import java.util.List;
import java.util.ArrayList;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Variable;
import org.oXML.engine.mapping.dom.TemplateMapping;
import org.oXML.engine.mapping.dom.ParameterMapping;
import org.oXML.engine.CompilationContext;
import org.oXML.engine.mapping.dom.MappingException;
import org.oXML.ObjectBoxException;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.NullTemplate;
import org.oXML.engine.template.Parameter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.NamedNodeMap;
import org.oXML.xpath.Resolver;
import org.tagbox.xml.NodeFinder;
import org.oXML.util.Log;

public class QueryFunctionMapping implements TemplateMapping {

    public static final Name NAME = 
	new Name(DatabaseExtensions.DB_NS, "function");

    private ParameterMapping paraMapping;

    public QueryFunctionMapping(){
        paraMapping = new ParameterMapping();
    }

    private ConstructorFactory factory = new ConstructorFactory();

    public Template map(Element e, CompilationContext ctxt)
        throws ObjectBoxException{

	Resolver resolver = ctxt.getResolver(e);
        String name = e.getAttribute("name");
        if(name.equals(""))
            throw new MappingException
                (e, "missing required attribute: name");

	Name nm = resolver.getName(name);

	Name connectionName;
	name = e.getAttribute("connection");
        if(name.equals(""))
	    connectionName = Connector.DEFAULT_NAME;
	else
	    connectionName = resolver.getName(name);

	List params = new ArrayList();
        NodeFinder finder = new NodeFinder(e.getNamespaceURI());
        NodeIterator it = finder.getElements(e, "param");
        for(Element p = (Element)it.nextNode(); p != null;
            p = (Element)it.nextNode()){
	    params.add(paraMapping.map(p, ctxt));
	}
	Variable[] vars = new Variable[params.size()];
	for(int i=0; i<vars.length; ++i){
	    Parameter p = (Parameter)params.get(i);
	    vars[i] = new Variable(p.getName(), null, p.getType());
	}

	StatementTemplate stmt = factory.buildStatementTemplate(e, ctxt);
	Query query;
	Element result = finder.getElement(e, "result");
	if(result == null){
	    query = new Query(nm, stmt, connectionName, vars);
	}else{
	    boolean s_text = ctxt.textSubstitution();
	    boolean s_attr = ctxt.attributeSubstitution();
	    ctxt.textSubstitution(true);
	    ctxt.attributeSubstitution(true);
	    Template target = ctxt.getBody(result);
	    query = new Query(nm, stmt, target, connectionName, vars);
	    ctxt.textSubstitution(s_text);
	    ctxt.attributeSubstitution(s_attr);
	}

        name = ((Element)e.getParentNode()).getAttribute("name");
        if(name.equals("")){
            ctxt.addFunction(new QueryFunction(nm, vars, query));
        }else{
            Type type = ctxt.getType(resolver.getName(name));
            type.addFunction(new QueryTypeFunction(type, nm, vars, query));
        }
	return new NullTemplate();
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
