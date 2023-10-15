package org.oXML.engine.template;

import java.util.List;
import java.util.ArrayList;
import org.oXML.xpath.Expression;
import org.oXML.engine.ProgramException;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.type.Function;
import org.oXML.type.SystemErrorNode;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.type.Type;
import org.oXML.util.Log;

/**
 */
public class CatchTemplate implements Template {

    private Template content;
    private List exceptions;
    private Expression handler;

    private static final Name HANDLE_FUNCTION =
	new Name("handle");

    public CatchTemplate(Template content, List exceptions, 
		       Expression handler){
	this.content = content;
	this.exceptions = exceptions;
	this.handler = handler;
    }

    protected boolean doCatch(Type type){
	if(exceptions.size() == 0)
	    return true; // we handle all exceptions
	for(int i=0; i<exceptions.size(); ++i){
	    Type match = (Type)exceptions.get(i);
	    if(type.instanceOf(match))
		return true;
	}
	return false;
    }

    public void process(RuntimeContext ctxt)
        throws ObjectBoxException{

	Node result = null;
	try{
	    content.process(ctxt);
	}catch(ProgramException exc){
	    Node exception = exc.getException(); // what was actually thrown
	    if(doCatch(exception.getType())){
		result = handle(exception, ctxt);
	    }else{
		throw exc;
	    }
	}catch(ObjectBoxException exc){
	    // otherwise we treat it as a system error
	    if(doCatch(SystemErrorNode.TYPE)){
		result = handle(new SystemErrorNode(exc), ctxt);
	    }else{
		throw exc;
	    }
	}
	if(result != null){
	    ctxt.push(result);
	    ctxt.pop();
	}
    }

    protected Node handle(Node exception, RuntimeContext ctxt)
	throws ObjectBoxException {
	// evaluate the o:Path expression that will give us our
	// real exception handler
	Node catcher = handler.evaluate(ctxt);
	if(catcher == null)
	    throw new ObjectBoxException("empty exception handler");
	Function func = catcher.getType()
	    .getFunction(HANDLE_FUNCTION, new Type[]{exception.getType()});
	if(func == null)
	    throw new ObjectBoxException("invalid exception handler: "+
					 catcher.getType().getName());

// 	Type decl = func.getDeclaringType();
// 	if(decl != null && !decl.equals(catcher.getType()))
// 	    // get the parent node instance
// 	    catcher = catcher.getInstance(decl.getName());
	return func.invoke(catcher, new Node[]{exception}, ctxt);
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
