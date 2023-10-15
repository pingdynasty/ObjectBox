package org.oXML.engine.template;

import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.engine.ReturnException;
import org.oXML.type.TypeFunction;
import org.oXML.type.NodesetNode;
import org.oXML.type.ThreadNode;
import org.oXML.type.Nodeset;
import org.oXML.type.Function;
import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.type.Node;
import org.oXML.util.Log;
import org.oXML.xpath.Expression;

public class ThreadTemplate implements Template {

    private Template body;
    private Expression priority;
    private Expression daemon;

    public ThreadTemplate(Template body, Expression priority, Expression daemon){
	this.body = body;
	this.priority = priority;
	this.daemon = daemon;
    }

    private static final Name RUN_NAME = new Name("run");
    private static final Type[] RUN_SIG = new Type[]{};
    public class RunFunction extends TypeFunction {

	public RunFunction(){
	    super(ThreadNode.TYPE, RUN_NAME, RUN_SIG);
	}

	public Node invoke(Node target, Node[] args, RuntimeContext context)
	    throws ObjectBoxException {
	    // create temporary result handler
	    Nodeset result = context.pushOutputNodeset();
	    try{
		// process
		body.process(context);
		// the result of executing a function with no return statement
		// is the nodeset it produced
	    }catch(ReturnException exc){
		return exc.getResult();
	    }finally{
		context.popOutputNodeset();
	    }
	    return new NodesetNode(result);
	}
    }

    public void process(RuntimeContext rc)
        throws ObjectBoxException{
	Function runner = new RunFunction();
	ThreadNode thread = new ThreadNode(runner);
	if(priority != null){
	    Node p = priority.evaluate(rc);
	    thread.setPriority((int)p.numberValue());
	}
	if(daemon != null){
	    Node d = daemon.evaluate(rc);
	    thread.setDaemon(d.booleanValue());
	}
	thread.start();
	rc.push(thread);
	rc.pop();
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
