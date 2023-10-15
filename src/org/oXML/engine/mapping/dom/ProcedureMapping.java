package org.oXML.engine.mapping.dom;

import java.util.List;
import java.util.ArrayList;
import org.oXML.xpath.Expression;
import org.oXML.xpath.Resolver;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.type.NodesetNode;
import org.oXML.engine.template.Parameter;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.ProcedureTemplate;
import org.oXML.engine.template.DynamicTemplate;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.CompilationContext;
import org.oXML.ObjectBoxException;
import org.tagbox.xml.NodeFinder;
import org.oXML.util.Log;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.Element;

/**
   <o:procedure name="nm">
     <o:param name="p1"/>
     <o:param name="p2" select="default"/>
     <o:do>
     </o:do>
   </o:procedure>
 */
public class ProcedureMapping implements TemplateMapping{

    private ParameterMapping paraMapping;

    /** represents a mapping to a user-defined procedure */
    public class DynamicMapping implements TemplateMapping {
	private List templates;
        private Parameter[] params;

        public DynamicMapping(Parameter[] params, List templates){
            this.params = params;
// 	    templates = new ArrayList();
	    this.templates = templates;
        }

        public Template map(Element e, CompilationContext env)
            throws ObjectBoxException{

            // create parameters
	    Resolver resolver = env.getResolver(e);
            Expression[] exps = new Expression[params.length];
            for(int i=0; i<params.length; ++i){
                Name name = params[i].getName();
                String uri = name.getNamespaceURI();
                String expr = uri.equals("") ?
                    e.getAttribute(name.getLocalName()) :
                    e.getAttributeNS(uri, name.getLocalName());
                if(expr.equals(""))
                    exps[i] = null;
                else{
                    exps[i] = env.parse(expr);
		    exps[i].bind(resolver);
		}
            }
	    Template body = env.getBody(e);
	    // the dynamic template represents a call to this user-defined procedure
            Template call = new DynamicTemplate(params, exps, body);
	    // we keep a list of all calls that we create so that we can set the procedure
	    // body at a later stage
	    templates.add(call);
	    return call;
        }
    }

    public ProcedureMapping(){
        paraMapping = new ParameterMapping();
    }

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

        String nm = e.getAttribute("name");
        if(nm.equals(""))
            throw new MappingException
                (e, "missing required attribute: name");

        Name name = env.getResolver(e).getName(nm);

        NodeFinder finder = new NodeFinder(e.getNamespaceURI());
        Element content = finder.getElement(e, "do");
        if(content == null)
            throw new MappingException
                (e, "missing required element: do");

        List list = new ArrayList();
        NodeIterator it = finder.getElements(e, "param");
        for(Element param = (Element)it.nextNode(); param != null;
            param = (Element)it.nextNode())
            list.add(paraMapping.map(param, env));

        Parameter[] params = new Parameter[list.size()];
        list.toArray(params);

	List calls = new ArrayList(); // this will be built up to contain all procedure calls
        // first set the new mapping
        DynamicMapping mapping = new DynamicMapping(params, calls);
        env.setMapping(name, mapping);
        // now get the procedure body (to facilitate recursive procedures)
        Template body = env.getBody(content);

        return new ProcedureTemplate(body, calls, params);
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
