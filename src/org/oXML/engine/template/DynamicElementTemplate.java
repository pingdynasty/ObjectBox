package org.oXML.engine.template;

import java.util.Map;
import java.util.Collections;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.xpath.Expression;
import org.oXML.xpath.XPathException;
import org.oXML.util.Log;
import org.oXML.type.Name;
import org.oXML.type.ElementNode;
import org.oXML.xpath.Expression;

public class DynamicElementTemplate implements Template{

    private Expression ns;
    private Expression name;
    private Map attributes;
    private Template body;

    public DynamicElementTemplate(Expression name, Template body){
        this(null, name, Collections.EMPTY_MAP, body);
    }

    public DynamicElementTemplate(Expression ns, Expression name, Template body){
        this(ns, name, Collections.EMPTY_MAP, body);
    }

    public DynamicElementTemplate(Expression ns, Expression name, Map attributes, Template body){
        this.ns = ns;
        this.name = name;
        this.attributes = attributes;
        this.body = body;
    }

    public void process(RuntimeContext env)
        throws ObjectBoxException{        

        Name elementName;
	String local = name.evaluate(env).stringValue();
	if(ns == null)
	    elementName = new Name(local);
	else
	    elementName = new Name(ns.evaluate(env).stringValue(), local);

        env.push(new ElementNode(elementName, attributes));
        try{
            if(body != null)
                body.process(env);
        }finally{
            env.pop();
        }
    }

    public String toString(){
        return getClass().getName()+'<'+ns+','+name+','+body+'>';
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
