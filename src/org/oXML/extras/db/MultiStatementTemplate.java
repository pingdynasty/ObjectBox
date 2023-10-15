package org.oXML.extras.db;

import java.util.Map;
import java.util.HashMap;
import org.oXML.util.Log;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;

public class MultiStatementTemplate implements StatementTemplate {

    private Map templates;

    public MultiStatementTemplate(){
        templates = new HashMap();
    }

    public void addTemplate(String dialect, StatementTemplate template){
        templates.put(dialect, template);
    }

    /**
     * evaluate all expressions used in this template.
     */
    public synchronized String evaluateStatement(RuntimeContext context, String dialect)
	throws ObjectBoxException {
        StatementTemplate template = (StatementTemplate)templates.get(dialect);
        if(template == null){
            Log.warning("No query for connection dialect "+dialect);
            template = (StatementTemplate)templates.get(DEFAULT_DIALECT_NAME);
        }
        if(template == null)
            throw new DatabaseException("No default template for connection dialect "+dialect);
        return template.evaluateStatement(context, dialect);
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
