package org.oXML.extras.java;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.xpath.function.XPathFunction;
import org.oXML.util.Log;
import org.oXML.extras.java.function.*;
import org.oXML.engine.mapping.dom.TemplateMapping;
import org.oXML.ObjectBoxException;
import org.oXML.engine.LanguageExtension;

/**
 * self-configured one-stop-shop for java extensions
 */
public class JavaExtensions implements LanguageExtension{

    private ReflectionTypeResolver resolver;
    private Map mappings; // Map<Name, TemplateMapping>
    private List functions; // List<Function>

    public JavaExtensions()
	throws ObjectBoxException{
        resolver = ReflectionTypeResolver.getInstance();

        mappings = new HashMap();
        TemplateMapping mapping = new ResolveMapping(resolver);
        mappings.put(ResolveMapping.NAME, mapping);
	mapping = new WriteMapping();
        mappings.put(WriteMapping.NAME, mapping);

        // add some java: namespace functions
        functions = new ArrayList();
        Type domNode = resolver.resolve(org.w3c.dom.Node.class).getType();
        functions.add(new ToNativeFunction(new Name(resolver.JAVA_NS, "toNative"), 
                                           new Type[]{domNode}));
        Type domFrag = resolver.resolve(org.w3c.dom.DocumentFragment.class).getType();
        functions.add(new ToDOMFunction(new Name(resolver.JAVA_NS, "toDOM"), 
                                             new Type[]{org.oXML.type.Node.TYPE}, domFrag));
        Type list = resolver.resolve(java.util.List.class).getType();
        functions.add(new ToListFunction(new Name(resolver.JAVA_NS, "toList"), 
                                         new Type[]{ObjectType.TYPE}, list));

        functions.add(new ParseFunction(new Name(resolver.JAVA_NS, "parse"), 
					new Type[]{org.oXML.type.Node.TYPE}));

        functions.add(new SerializeFunction(new Name(resolver.JAVA_NS, "serialize"), 
					    new Type[]{org.oXML.type.Node.TYPE}));

        functions.add(new TrustedFunction(new Name(resolver.JAVA_NS, "trusted"), 
					  new Type[]{org.oXML.type.Node.TYPE}));

        functions.add(new EvaluateFunction(new Name(resolver.JAVA_NS, "evaluate")));
        functions.add(new ArrayFunction(new Name(resolver.JAVA_NS, "array"),
					new Type[]{org.oXML.type.Node.TYPE, 
						   org.oXML.type.Node.TYPE}, resolver));
    }

    public Map getMappings(){
        return mappings;
    }

    public List getFunctions(){
        return functions;
    }

    public ReflectionType resolve(Class javaClass){
        return resolver.resolve(javaClass);
    }

    public Node makeNode(Object obj){
        return resolver.resolve(obj.getClass()).primitive(obj);
    }

    public ReflectionTypeResolver getResolver(){
        return resolver;
    }

    public String toString(){
	return getClass().getName();
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
