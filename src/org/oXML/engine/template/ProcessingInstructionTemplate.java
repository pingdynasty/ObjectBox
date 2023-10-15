package org.oXML.engine.template;

import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.xpath.Expression;
import org.oXML.xpath.step.LiteralExpression;
import org.oXML.util.Log;
import org.oXML.type.Name;
import org.oXML.type.*;

public class ProcessingInstructionTemplate extends EvalTemplate{

    private Expression target;

    public ProcessingInstructionTemplate(Expression target, Expression data){
	super(data);
	this.target = target;
    }

    public ProcessingInstructionTemplate(Expression target, Template body){
	super(body);
	this.target = target;
    }

    public ProcessingInstructionTemplate(String target, String data){
	super(new LiteralExpression(data));
	this.target = new LiteralExpression(target);
    }

    public void process(RuntimeContext env)
        throws ObjectBoxException{
	
	String value = target.evaluate(env).stringValue();
	String data = evaluate(env).stringValue();
	Node pi = new ProcessingInstructionNode(value, data);
        env.push(pi);
        env.pop();
    }

    public String toString(){
        return super.toString()+'<'+target+'>';
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
