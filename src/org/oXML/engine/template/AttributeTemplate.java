package org.oXML.engine.template;

import java.util.Map;
import java.util.Collections;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.type.AttributeNode;
import org.oXML.xpath.Expression;
import org.oXML.xpath.PrefixResolver;
import org.oXML.xpath.XPathException;

public class AttributeTemplate extends EvalTemplate {
    private Expression namespace;
    private Expression name;

    public AttributeTemplate(Expression namespace, Expression name, 
			   Expression value) {
	super(value);
        this.namespace = namespace;
        this.name = name;
    }

    public AttributeTemplate(Expression namespace, Expression name, 
			   Template body) {
	super(body);
        this.namespace = namespace;
        this.name = name;
    }

    public void process(RuntimeContext env)
        throws ObjectBoxException{        

	Node nm = name.evaluate(env);
	if(nm == null || nm.stringValue().equals(""))
	    throw new ObjectBoxException("invalid empty attribute name");

	Name resolvedName;
	if(namespace == null){
	    resolvedName = new Name(nm.stringValue());
	}else{
	    Node uri = namespace.evaluate(env);
	    resolvedName = new Name(uri.stringValue(), nm.stringValue());
	}

	Node value = evaluate(env);
	AttributeNode attribute = 
	    new AttributeNode(resolvedName, value.stringValue());	  
	env.push(attribute);
	env.pop();
    }

    public String toString(){
        return super.toString()+'<'+name+'>';
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
