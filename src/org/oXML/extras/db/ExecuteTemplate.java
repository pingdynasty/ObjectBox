package org.oXML.extras.db;

import java.util.List;
import java.util.Iterator;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.Parameter;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.engine.mapping.dom.MappingException;
import org.oXML.xpath.function.FunctionException;
import org.oXML.xpath.function.StackFrame;
import org.oXML.xpath.SourceLocator;
import org.oXML.xpath.Expression;
import org.oXML.util.Log;

public class ExecuteTemplate implements Template {

    private Name connectionName;
    private Expression stmt;
    private Template target;
    protected SourceLocator location;

    public ExecuteTemplate(SourceLocator location, Name connectionName, Expression stmt){
        this.location = location;
	this.connectionName = connectionName;
	this.stmt = stmt;
    }

    public ExecuteTemplate(SourceLocator location, Name connectionName, 
                           Expression stmt, Template target){
        this.location = location;
	this.connectionName = connectionName;
	this.stmt = stmt;
	this.target = target;
    }

    public void process(RuntimeContext ctxt)
        throws ObjectBoxException{

	ConnectionDirector dir = ConnectionDirector.getInstance();
	Connector connection = dir.getConnector(connectionName);
	if(connection == null)
	    throw new DatabaseException("no such connection: "+connectionName);

	String query = stmt.evaluate(ctxt).stringValue();
	StackFrame frame = new StackFrame("db:execute()", location);
	ctxt.pushStackFrame(frame);
        try{
            if(target == null)
                connection.execute(query);
            else
                connection.execute(query, target, ctxt);
	}catch(FunctionException exc){
	    exc.addStackFrame(frame);
	    throw exc;
	}catch(Throwable exc){
	    throw new FunctionException(frame, exc);
	}finally{
	    ctxt.popStackFrame();
	}
    }

    public String toString(){
        return getClass().getName()+'<'+stmt+'>';
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
