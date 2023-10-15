package org.oXML.engine.template;

import java.util.List;
import org.oXML.engine.CompilationContext;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.xpath.Expression;
import org.oXML.type.Node;
import org.oXML.type.NodesetNode;
import org.oXML.type.Type;
import org.oXML.util.Log;

public class ProcedureTemplate implements Template {
    private Template body;
    private Parameter[] params;
    private List calls;

    private static final Type type = Node.TYPE;

    public ProcedureTemplate(Template body, List calls, Parameter[] params){
	this.body = body;
        this.params = params;
	this.calls = calls;
    }

    /**
     * this runs when a user procedure definition is reached
     */
    public void process(RuntimeContext env)
        throws ObjectBoxException{
        // resolve default parameter values
        for(int i=0; i<params.length; ++i){
            Expression expr = params[i].getDefaultExpression();
            if(expr != null)
                params[i].setDefaultValue(expr.evaluate(env));
        }
	// set the procedure body
	for(int i=0; i<calls.size(); ++i){
	    DynamicTemplate call = (DynamicTemplate)calls.get(i);
	    call.setProcedureBody(body);
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
