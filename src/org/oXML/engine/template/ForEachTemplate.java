package org.oXML.engine.template;

import java.util.StringTokenizer;
import org.oXML.type.Name;
import org.oXML.xpath.parser.QName;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.Expression;
import org.oXML.type.*;
import org.oXML.util.Log;

public class ForEachTemplate implements Template {

    protected Expression select;
    protected Template body;
    protected Name name;
    protected Expression from;
    protected Expression to;
    protected Expression step;
    protected Expression in;
    protected Expression delim;

    public ForEachTemplate(Name name, Expression select, 
			 Template body){
        this.name = name;
        this.select = select;
        this.body = body;
    }

    /**
     * @param in string expression, not null
     * @param delim token delimiter, or null for default
     */
    public ForEachTemplate(Name name, Expression in, 
			 Expression delim, Template body){
        this.name = name;
        this.in = in;
        this.delim = delim;
        this.body = body;
    }

    /**
     * @param from start of iteration, or null for default
     * @param to end of iteration, not null
     * @param step increment of iteration, or null for default
     */
    public ForEachTemplate(Name name, Expression from, Expression to, 
                           Expression step, Template body){
        this.name = name;
        this.from = from;
        this.to = to;
        this.step = step;
        this.body = body;
    }

    public void process(RuntimeContext env)
        throws ObjectBoxException{

	Node savedNode = env.getContextNode();
        int savedPos = env.getContextPosition();
        int pos = 0;
	if(select != null){
	    Node nodeset = select.evaluate(env);
            if(nodeset.getType().instanceOf(NodesetNode.TYPE) ||
               nodeset.getType().instanceOf(ListNode.TYPE)){
                NodeIterator it = nodeset.getChildNodes().getIterator();
                for(Node value = it.nextNode(); 
                    value != null; value = it.nextNode()){
                    set(env, value, ++pos);
                    body.process(env);
                }
            }else{
                set(env, nodeset, ++pos);
                body.process(env);
            }
	}else if(to != null){
	    double stop = to.evaluate(env).numberValue();
	    
	    double start;
	    if(from == null)
		start = 0;
	    else
		start = from.evaluate(env).numberValue();
	    
	    double inc;
	    if(step == null)
		inc = 1;
	    else
		inc = step.evaluate(env).numberValue();
	    for(double d = start;d < stop; d += inc){
		set(env, new NumberNode(d), ++pos);
		body.process(env);
	    }
	}else{
	    String tokens = in.evaluate(env).stringValue();
	    String sep;
	    if(delim == null)
		sep = " ";
	    else
		sep = delim.evaluate(env).stringValue();
	    StringTokenizer tok = new StringTokenizer(tokens, sep);
	    while(tok.hasMoreElements()){
		String value = tok.nextToken();
		set(env, new StringNode(value), ++pos);
		body.process(env);
	    }
	}
	env.setContextNode(savedNode);
	env.setContextPosition(savedPos);
    }

    protected void set(RuntimeContext env, Node value, int pos){
        env.setContextPosition(pos);
	if(name == null){
	    env.setContextNode(value);
	}else{
	    env.setVariable(name, value);
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
