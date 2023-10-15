package org.oXML.extras.db;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Stack;
import java.util.StringTokenizer;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.engine.mapping.dom.TemplateMapping;
import org.oXML.engine.CompilationContext;
import org.oXML.engine.mapping.dom.MappingException;
import org.oXML.ObjectBoxException;
import org.oXML.engine.mapping.dom.MappingException;
import org.oXML.engine.template.CompoundTemplate;
import org.oXML.engine.template.NullTemplate;
import org.oXML.engine.template.Template;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.NamedNodeMap;
import org.tagbox.xml.NodeFinder;
import org.oXML.util.Log;

public class ConstructorFactory {

    private static final char LEFT_DELIM = '{';
    private static final char RIGHT_DELIM = '}';

    /**
     * construct a StatementTemplate from a given node
     * @see StatementTemplate
     */
    public StatementTemplate buildStatementTemplate(Node n, CompilationContext ctxt)
 	throws ObjectBoxException {
        NodeFinder finder = new NodeFinder(n.getNamespaceURI());
        NodeIterator it = finder.getElements(n, "sql");
        MultiStatementTemplate multi = new MultiStatementTemplate();
        StatementTemplate last = null;
        int count = 0;
        for(Element e = (Element)it.nextNode(); e != null;
            e = (Element)it.nextNode()){
            String dialect = e.getAttribute("dialect");
            if(dialect.equals(""))
                dialect = StatementTemplate.DEFAULT_DIALECT_NAME;
            last = buildSimpleStatementTemplate(e, ctxt);
            multi.addTemplate(dialect, last);
            ++count;
        }
        if(count == 0)
	    throw new MappingException(n, "missing required element: sql");
        if(count == 1)
            return last;
        return multi;
    }

    public StatementTemplate buildSimpleStatementTemplate(Element e, CompilationContext ctxt)
 	throws ObjectBoxException {
        NodeFinder finder = new NodeFinder(e.getNamespaceURI());
        String query = finder.getValue(e);
	if(query == null || query.equals(""))
	    throw new MappingException(e, "empty or missing element: sql");
        List expressions = new ArrayList();
        Chunks chunks = tokenizeQuery(query, expressions, ctxt.getResolver(e), ctxt);
	return new SimpleStatementTemplate(chunks, expressions);
    }

    /**
     * break a query into chunks of Strings and ExpressionParts
     */
    protected Chunks tokenizeQuery(String query, List expressions, 
				   Resolver resolver, CompilationContext ctxt)
 	throws ObjectBoxException {

	Chunks chunks = new Chunks();
        if(query.indexOf(LEFT_DELIM) < 0){
            chunks.add(query);
            return chunks;
        }

        int from = query.indexOf(LEFT_DELIM);
        int to = query.indexOf(RIGHT_DELIM);
        chunks.add(query.substring(0, from));
        while(to > from){
            String expr = query.substring(from+1, to);
	    Expression e = ctxt.parse(expr);
	    e.bind(resolver);
	    ExpressionPart part = new ExpressionPart(e);
	    expressions.add(part);
            chunks.add(part);
            from = query.indexOf(LEFT_DELIM, to);
            if(from < 0)
                break;
            chunks.add(query.substring(to+1, from));
            to = query.indexOf(RIGHT_DELIM, from);
        }
        chunks.add(query.substring(to+1));
	return chunks;
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
