package org.oXML.engine.template;

import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.ReturnException;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodesetNode;
import org.oXML.xpath.iterator.NullNodeset;
import org.oXML.util.Log;

/**
 */
public class ImportTemplate implements Template{

    private String systemId;
    private Template imported;
    private final Nodeset out = new NullNodeset();

    public ImportTemplate(String systemId, Template imported){
	this.systemId = systemId;
	this.imported = imported;
    }

    public void process(RuntimeContext env)
        throws ObjectBoxException{
	// redirect output
	env.pushOutputNodeset(out);
	try{
	    // process the import
	    imported.process(env);
	}catch(ReturnException exc){
            // ignore
	}catch(ObjectBoxException exc){
	    Log.error("importing: "+systemId);
	    throw exc;
	}finally{
	    // reset output handler
	    env.popOutputNodeset();
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
