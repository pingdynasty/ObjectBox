package org.oXML.engine.template;

import java.util.Map;
import java.util.HashMap;
import org.oXML.type.Variable;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.xpath.Expression;
import org.oXML.type.Node;
import org.oXML.type.Name;
import org.oXML.util.Log;

/**
 */
public class ProgramTemplate implements Template{

    private Parameter[] params;
    private Template body;
    private String contenttype;

    public ProgramTemplate(Parameter[] params, Template body, String contenttype){
	this.params = params;
	this.body = body;
	this.contenttype = contenttype;
    }

    public String getContentType(){
	return contenttype;
    }

    public Parameter[] getParameters(){
	return params;
    }

    public Name[] getParameterNames(){
	Name[] names = new Name[params.length];
	for(int i=0; i<params.length; i++)
	    names[i] = params[i].getName();
	return names;
    }

//     public Node getParameter(Name name){
// 	for(int i=0; i<params.length; i++)
// 	    if(params[i].getName().equals(name))
// 		return params[i].getDefaultValue();
// 	return null;
//     }

//     public void setParameter(Variable var)
// 	throws ObjectBoxException {
// 	Name name = var.getName();
// 	for(int i=0; i<params.length; i++){
// 	    if(params[i].getName().equals(name)){
// 		if(var.getValue().getType().instanceOf(params[i].getType()))
// 		    variables.put(name, var);
// 		else
// 		    throw new ObjectBoxException("wrong parameter type for program parameter: "+params[i].getName());
// 		break;
// 	    }
// 	}
//     }

//     public void setParameter(Name name, Node value)
// 	throws ObjectBoxException {	
// 	for(int i=0; i<params.length; i++){
// 	    if(params[i].getName().equals(name)){
// 		if(value == null)
// 		    params[i].setDefaultValue(null);
// 		else if(value.getType().instanceOf(params[i].getType()))
// 		    params[i].setDefaultValue(value);
// 		else
// 		    throw new ObjectBoxException
// 			("wrong parameter type for program parameter: "+
// 			 params[i].getName()+" (should be "+params[i].getType().getName()+
// 			 " not "+value.getType().getName()+")");
// 		break;
// 	    }
// 	}
//     }

    public void process(RuntimeContext env)
        throws ObjectBoxException{

	for(int i=0; i<params.length; i++){
	    Name name = params[i].getName();
	    if(!env.hasVariable(name)){
		Expression expr = params[i].getDefaultExpression();
		if(expr == null)
		    throw new ObjectBoxException("undefined program parameter has no default value: "+name);
		Node value = expr.evaluate(env);
                env.setVariable(name, value);
	    }
	}
	body.process(env);
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
