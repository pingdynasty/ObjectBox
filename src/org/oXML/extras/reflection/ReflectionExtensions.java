package org.oXML.extras.reflection;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.xpath.function.XPathFunction;
import org.oXML.util.Log;
import org.oXML.engine.mapping.dom.TemplateMapping;
import org.oXML.ObjectBoxException;
import org.oXML.engine.LanguageExtension;

public class ReflectionExtensions implements LanguageExtension{

    private List functions;
    private Map mappings;

    public static final String REFLECTION_NS = 
	"http://www.o-xml.org/lang/reflection";

    public ReflectionExtensions()
	throws ObjectBoxException{
        mappings = new HashMap();
        functions = new ArrayList();
	functions.add(new ProgramFunction());
    }

    public Map getMappings(){
        return mappings;
    }

    public List getFunctions(){
        return functions;
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
