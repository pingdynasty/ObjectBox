package org.oXML.extras.db;

import java.util.Map;
import java.util.HashMap;
import org.oXML.type.Node;
import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.type.StringNode;
import org.oXML.type.NumberNode;
import org.oXML.type.BooleanNode;
import org.oXML.type.BytesNode;
import org.oXML.type.Variable;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.template.Template;

/**
 * Encapsulates a db-to-xml Mapping - comprises of a mapping with:
 * a set of StatementTemplates representing the db queries for different SQL dialects;
 * a query Name;
 * a target Template;
 * a connection Name.
 * @see StatementTemplate
 */
public class Query {
    private Map statements;

    private StatementTemplate statement;
    private Name name;
    private Template target;
    private Name connectionName;
    private Variable[] params;
    private Map values;

    public Query(Name name, StatementTemplate statement, 
                 Name connectionName, Variable[] params) {
        this(name, statement, null, connectionName, params);
    }

    public Query(Name name, StatementTemplate statement, Template target, 
		 Name connectionName, Variable[] params) {
        this.name = name;
        this.statement = statement;
        this.target = target;
	this.connectionName = connectionName;
	this.params = params;
	this.values = new HashMap();
	for(int i=0; i<params.length; ++i)
	    values.put(params[i].getName(), params[i]);
    }

    public Name getConnectionName(){
	return connectionName;
    }

    public Node castParameter(Name name, Node value)
        throws ObjectBoxException {
	Variable param = (Variable)values.get(name);
	Type type = param.getType();
	if(type != null){
            if(type.instanceOf(StringNode.TYPE))
                value = QuoteFunction.quote(value); // quote strings
            // cast to ensure that values are of the right type
            else if(type.instanceOf(NumberNode.TYPE))
                value = value.toNumberNode();
            else if(type.instanceOf(BooleanNode.TYPE))
                value = value.toBooleanNode();
            else if(type.instanceOf(BytesNode.TYPE))
                value = value.toBytesNode();
        }
	return value;
    }

    public void execute(RuntimeContext ctxt)
	throws ObjectBoxException {
	ConnectionDirector dir = ConnectionDirector.getInstance();
	Connector connection = dir.getConnector(connectionName);
	if(connection == null)
	    throw new DatabaseException("no such connection: "+connectionName);
        String dialect = connection.getDialect();
        String query = statement.evaluateStatement(ctxt, dialect); // evaluate and get the SQL query
	if(target == null){
	    connection.execute(query);
	}else{
	    connection.execute(query, target, ctxt);
	}
    }

    public String print(){
        StringBuffer buf = new StringBuffer("db:call<");
        buf.append(name).append(">(");
        for(int i=0; i<params.length; ++i){
            if(i>0)
                buf.append(", ");
            buf.append(params[i].getType().getName());
            buf.append(' ');
            buf.append(params[i].getName());
        }
        buf.append(')');
        return buf.toString();
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
