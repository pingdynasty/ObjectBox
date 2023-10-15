package org.oXML.engine.template;

import org.oXML.engine.RuntimeContext;
import org.oXML.engine.ProgramException;
import org.oXML.ObjectBoxException;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.util.Log;
import org.oXML.xpath.Expression;
import org.oXML.xpath.SourceLocator;
import org.oXML.type.Node;
import org.oXML.type.AssertionErrorNode;

public class AssertTemplate implements Template {

    private String expr;
    private Expression test;
    private Expression msg;
    private SourceLocator location;

    public AssertTemplate(String expr, Expression test, SourceLocator location){
	this.expr = expr;
        this.test = test;
	this.location = location;
    }

    public AssertTemplate(String expr, Expression test, Expression msg, SourceLocator location){
	this.expr = expr;
        this.test = test;
        this.msg = msg;
	this.location = location;
    }

    public void process(RuntimeContext ctxt)
        throws ObjectBoxException{
        if(!test(ctxt)){
	    String message;
	    if(msg == null)
		message  ="Assertion Error";
	    else
		message = msg.evaluate(ctxt).stringValue();

	    Node exc = new AssertionErrorNode(expr, message);
	    throw new ProgramException(exc, location);
	}
    }

    protected boolean test(RuntimeContext ctxt)
        throws ObjectBoxException{
	return test.evaluate(ctxt).booleanValue();
    }

    public String toString(){
        return getClass().getName()+'<'+test+'>';
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
