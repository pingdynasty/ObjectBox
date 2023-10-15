package org.oXML.extras.java;

import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Function;
import org.oXML.engine.mapping.dom.TemplateMapping;
import org.oXML.engine.CompilationContext;
import org.oXML.engine.mapping.dom.MappingException;
import org.oXML.ObjectBoxException;
import org.oXML.engine.template.Template;
import org.w3c.dom.Element;
import org.oXML.xpath.Expression;
import org.oXML.xpath.XPathException;
import org.oXML.util.Log;

public class ResolveMapping implements TemplateMapping{

    public static final Name NAME = 
    new Name(ReflectionTypeResolver.JAVA_NS, "resolve");

    private ReflectionTypeResolver resolver;

    public ResolveMapping(ReflectionTypeResolver resolver){
        this.resolver = resolver;
    }

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

        String classname = e.getAttribute("classname");
        if(classname.equals(""))
            throw new MappingException
                (e, "missing required attribute: classname");

        try{
            Class javaclass = Class.forName(classname);
            ReflectionType type = resolver.resolve(javaclass);
            if(!env.hasType(type.getType().getName())){// assume it has already been resolved
               env.addType(type.getType());
               // add ctors
               Function[] funs = type.getConstructors();
               for(int i=0; i<funs.length; ++i){
                   env.addFunction(funs[i]);
               //             Log.trace("constructor: "+funs[i]);
               }
               // add static functions
               funs = type.getStaticFunctions();
               for(int i=0; i<funs.length; ++i){
                   env.addFunction(funs[i]);
               //             Log.trace("static function: "+funs[i].getName().getLocalName());
               }
            }
            return new ResolveTemplate(type);
        }catch(ClassNotFoundException exc){
            Log.exception(exc);
            throw new ObjectBoxException(exc);
        }
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
