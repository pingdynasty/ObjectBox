package org.oXML.engine.mapping.dom;

import java.util.Map;
import java.util.HashMap;
import org.w3c.dom.*;
import org.oXML.type.Name;
import org.oXML.type.*;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.oXML.xpath.step.LiteralExpression;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.ElementTemplate;
import org.oXML.engine.CompilationContext;
import org.oXML.ObjectBoxException;
import org.w3c.dom.Element;

public class ElementMapping implements TemplateMapping{

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

        Name name = new Name(e.getNamespaceURI(), e.getLocalName(), e.getPrefix());
        Map attributes = new HashMap();
        NamedNodeMap atts = e.getAttributes();
	Resolver resolver = env.getResolver(e);
        for(int i=0; i<atts.getLength(); ++i){
            Attr attr = (Attr)atts.item(i);
	    Name nm = new Name(attr.getNamespaceURI(), attr.getLocalName(), attr.getPrefix());

// 	    Name nm = new Name(attr.getName());

// 	    String name = attr.getName();
// 	    if(name.equals("xml:space"))
// 	    String uri = attr.getNamespaceURI();
// 	    if(uri != null && uri.equals(env.OXML_NS)){
// 		if(
// 	    if(name.equals("xml:space"))
// 		env.ignoreWhitespace(true);
// 	    else if(name.equals("xml:comments"))
// 		env.ignoreWhitespace(true);
	    Expression value;
	    if(env.attributeSubstitution()){
		value = env.evaluate(attr.getValue());
		value.bind(resolver);
	    }else{
		value = new LiteralExpression(attr.getValue());
	    }
	    attributes.put(nm, value);
        }
        Template body = env.getBody(e);
        return new ElementTemplate(name, attributes, body);
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
