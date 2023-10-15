package org.oXML.engine.mapping.dom;

import java.util.List;
import java.util.ArrayList;
import org.oXML.engine.template.Parameter;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.TypeTemplate;
import org.oXML.engine.template.DynamicConstructor;
import org.oXML.engine.template.DynamicTypeFunction;
import org.oXML.engine.template.DynamicFunction;
import org.oXML.engine.template.DefaultNodesFunction;
import org.oXML.engine.template.ElementNodesFunction;
import org.oXML.engine.CompilationContext;
import org.oXML.ObjectBoxException;
import org.w3c.dom.Element;
import org.w3c.dom.traversal.NodeIterator;
import org.oXML.type.Node;
import org.oXML.type.ParentNode;
import org.oXML.type.ElementNode;
// import org.oXML.type.NodesetNode;
import org.oXML.type.Function;
import org.tagbox.xml.NodeFinder;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Function;
import org.oXML.xpath.Resolver;
import org.oXML.util.Log;

public class TypeMapping implements TemplateMapping{

    private ParameterMapping varMapping;
    private ConstructorMapping functionMapping;

    public TypeMapping(){
        varMapping = new ParameterMapping();
        functionMapping = new ConstructorMapping();
    }

    public Type getType(NodeFinder finder, Element e, CompilationContext env)
        throws ObjectBoxException{

        // get name
        String nm = e.getAttribute("name");
        if(nm.equals(""))
            throw new MappingException
                (e, "missing required attribute: name");
        Name name = env.getResolver(e).getName(nm);

        // get parents
        List list = new ArrayList();
        NodeIterator it = finder.getElements(e, "parent");
        for(Element parent = (Element)it.nextNode(); parent != null;
            parent = (Element)it.nextNode()){
            nm = parent.getAttribute("name");
            if(nm.equals(""))
                throw new MappingException
                    (parent, "missing required attribute: name");
	    Resolver resolver = env.getResolver(parent);
            Type type = env.getType(resolver.getName(nm));
            if(type == null)
                throw new MappingException
                    (parent, "unknown type: "+resolver.getName(nm));
            list.add(type);
        }
        Type[] parents;
        if(list.isEmpty()){
            // default parent types are Node and Parent
            parents = new Type[]{ParentNode.TYPE};
        }else{
            parents = new Type[list.size()];
            list.toArray(parents);
        }

        // add this type to the environment for recursive type references
        // in variables and functions
        return new Type(name, parents);
    }

//      public Parameter[] getVariables(NodeFinder finder, Element e, CompilationContext env)
//          throws ObjectBoxException{
//          // get variables
//          List list = new ArrayList();
//          NodeIterator it = finder.getElements(e, "variable");
//          for(Element var = (Element)it.nextNode(); var != null; var = (Element)it.nextNode()){
//              Parameter variable = varMapping.map(var, env);
//              type.addVariable(variable.getName());
//              list.add(variable);
//          }
//          Parameter[] variables = new Parameter[list.size()];
//          list.toArray(variables);
//          return variables;
//      }

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

        NodeFinder finder = new NodeFinder(e.getNamespaceURI());

        // create the type from the name and parent type definitions
        Type type = getType(finder, e, env);
        env.addType(type);

//          // get the variable definitions
//          Parameter[] variables = getVariables(finder, e, env);
        // get variables
        List list = new ArrayList();
        NodeIterator it = finder.getElements(e, "variable");
        for(Element var = (Element)it.nextNode(); var != null; var = (Element)it.nextNode()){
            Parameter variable = varMapping.map(var, env);
            type.addVariable(variable.getName(), variable.getType());
            list.add(variable);
        }
        Parameter[] variables = new Parameter[list.size()];
        list.toArray(variables);

        // get constructors
        it = finder.getElements(e, "constructor");
        for(Element fun = (Element)it.nextNode(); fun != null;
            fun = (Element)it.nextNode()){
            Parameter[] params = functionMapping.getParameters(finder, fun, env);

	    Element content = finder.getElement(fun, "do");
	    if(content == null)
		throw new MappingException
		    (fun, "missing required element: do");
	    Template body = env.getBody(content);

            Parameter[] parents = functionMapping.getParents(finder, fun, env);
            Function function = new DynamicConstructor(type, params, body, variables, parents);
            type.addConstructor(function);
            // add it to type
            type.addFunction(function);
        }

        // get functions
        boolean generateNodesFunction = true;
        it = finder.getElements(e, "function");
        for(Element fun = (Element)it.nextNode(); fun != null;
            fun = (Element)it.nextNode()){

	    String nm = fun.getAttribute("name");
	    if(nm.equals(""))
		throw new MappingException
		    (fun, "missing required attribute: name");
            if(nm.equals("nodes"))
                generateNodesFunction = false;
	    Name name = env.getResolver(fun).getName(nm);
            Parameter[] params = functionMapping.getParameters(finder, fun, env);

	    Element content = finder.getElement(fun, "do");
	    if(content == null)
		throw new MappingException
		    (fun, "missing required element: do");
	    Template body = env.getBody(content);

            Function function;
            if(name.equals(type.getName())){
                // it's a constructor
                Log.trace("use of o:function for constructors will be deprecated, please use o:constructor in type "+name);
                Parameter[] parents = functionMapping.getParents(finder, fun, env);
                function = new DynamicConstructor(type, params, body, variables, parents);
                // add it to type
                type.addConstructor(function);
            }else{
                function = new DynamicTypeFunction(type, name, params, body);
                // add it to type
                type.addFunction(function);
            }
        }

        if(generateNodesFunction){
            // no nodes() function declared, add generated function
            // there are two types of generated nodes() functions:
            // 1) element subtypes output simple values wrapped in 
            // subelements, if there are more than one type variable.
            // 2) other types output each public variable value as it is.
            if(type.instanceOf(ElementNode.TYPE))
                type.addFunction(new ElementNodesFunction(type));
// todo: add access specifiers to Variable to only output public variables
//             else
//                 type.addFunction(new DefaultNodesFunction(type));
        }

        if(type.getConstructors().length == 0){
            // no ctors declared, add generated default constructor
            type.addConstructor(new DynamicConstructor(type, variables));
        }

        // add in constructors as global functions
	Function[] ctors = type.getConstructors();
	for(int i=0; i<ctors.length; ++i)
	    env.addFunction(ctors[i]);

        return new TypeTemplate(type);
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
