package org.oXML.engine.template;

import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.util.Log;

public class FunctionTemplate implements Template{
    protected Type type;
    protected Name name;
    protected Parameter[] params;
    protected Template body;

    public FunctionTemplate(Name name, Template body, Parameter[] params){
        this.name = name;
        this.body = body;
        this.params = params;
    }

    public void process(RuntimeContext env)
        throws ObjectBoxException{
        // tbd set context node ??
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
