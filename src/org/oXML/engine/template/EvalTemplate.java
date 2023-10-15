package org.oXML.engine.template;

import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.engine.ResultHandler;
import org.oXML.xpath.Expression;
import org.oXML.type.Node;
import org.oXML.type.StringNode;
import org.oXML.type.NodesetNode;
import org.oXML.type.Nodeset;
import org.oXML.util.Log;

/**
 * A Template that substitutes either the text in the attribute called
 * "select", or the first child node if it is a Text node.
 */
public class EvalTemplate implements Template {

    protected Expression select;
    protected Template body;

    public EvalTemplate(Expression select){
        this.select = select;
    }

    protected EvalTemplate(Template body){
        this.body = body;
    }

    protected Node evaluate(RuntimeContext env)
        throws ObjectBoxException{
        if(select == null){
            // evaluate the child nodes
	    // create result nodeset
	    Nodeset result = env.pushOutputNodeset();
            try{
                // process
                body.process(env);
            }finally{
		// reset handler
		env.popOutputNodeset();
            }
	    return new NodesetNode(result);
        }else{
            // evaluate the select expression
	    return select.evaluate(env);
        }
    }

    public void process(RuntimeContext env)
        throws ObjectBoxException{
        if(select == null){
	    // process
	    body.process(env);
	}else{
	    Node result;
            // evaluate the select expression
	    result = select.evaluate(env);
	    env.push(result);
	    env.pop();
	}
    }

    public String toString(){
	return getClass().getName()+
	    '<'+(select == null ? body.toString() : select.toString())+'>';
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
