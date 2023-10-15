package org.oXML.extras.db;

import java.util.List;
import java.util.Iterator;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.type.StringNode;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.Parameter;
import org.oXML.engine.mapping.dom.MappingException;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.xpath.function.FunctionException;
import org.oXML.xpath.function.StackFrame;
import org.oXML.xpath.SourceLocator;
import org.oXML.util.Log;

public class CallTemplate implements Template{

    private Name queryName;
    private Parameter[] args;
    protected SourceLocator location;

    private ConnectionDirector dir = ConnectionDirector.getInstance();

    public CallTemplate(SourceLocator location, Name queryName, List args){
        this.location = location;
	this.queryName = queryName;
	this.args = new Parameter[args.size()];
	args.toArray(this.args);
    }

    public void process(RuntimeContext ctxt)
        throws ObjectBoxException{

	Query query = dir.getQuery(queryName);
	if(query == null)
	    throw new DatabaseException("no such query: "+queryName);

        Node[] values = new Node[args.length];
	for(int i=0; i<args.length; ++i){
	    values[i] = args[i].getDefaultExpression().evaluate(ctxt);
            values[i] = query.castParameter(args[i].getName(), values[i]);
        }
	ctxt.hide(); // protect current variables
	for(int i=0; i<args.length; ++i){
            ctxt.setVariable(args[i].getName(), values[i]);
        }
	StackFrame frame = new StackFrame(query.print(), location);
	ctxt.pushStackFrame(frame);
        try{
            query.execute(ctxt);
	}catch(FunctionException exc){
	    exc.addStackFrame(frame);
	    throw exc;
	}catch(Throwable exc){
	    throw new FunctionException(frame, exc);
	}finally{
	    ctxt.popStackFrame();
            ctxt.unhide();
	}
    }

    public String toString(){
        return getClass().getName()+'<'+queryName+'>';
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
