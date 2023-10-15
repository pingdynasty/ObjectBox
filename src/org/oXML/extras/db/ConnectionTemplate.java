package org.oXML.extras.db;

import java.util.List;
import java.lang.reflect.Constructor;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.engine.template.Template;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.engine.mapping.dom.MappingException;
import org.oXML.xpath.Expression;
import org.tagbox.util.Pool;
import org.tagbox.util.MinMaxPool;
import org.oXML.util.Log;

public class ConnectionTemplate implements Template{

    private Name name;
    private Expression[] args;
    private int min;
    private int max;
    private int retry;
    private boolean initialised = false;

    public ConnectionTemplate(Name name, Expression[] args,
			      int min, int max, int retry){
	this.name = name;
	this.args = args;
	this.min = min;
	this.max = max;
	this.retry = retry;
    }

    protected String[] params(RuntimeContext env)
        throws ObjectBoxException{
	String[] vals = new String[args.length];
	for(int i=0; i<vals.length; ++i)
	    vals[i] = args[i].evaluate(env).stringValue();
	return vals;
    }

    public void process(RuntimeContext env)
        throws ObjectBoxException{

        if(initialised)
            return;

	ConnectionDirector dir = ConnectionDirector.getInstance();
	if(dir.hasConnector(name)){
	    Log.warning("connection already exists: "+name);
            initialised = true;
	    return;
	}

	Constructor ctor;
	try{
	    ctor = JdbcConnector.class.getConstructor(new Class[]
		{String.class, String.class, String.class, 
                 String.class, String.class, String.class});
	}catch(NoSuchMethodException exc){
	    throw new ObjectBoxException("invalid connection: "+name, exc);
	}

	Object[] args = params(env);

        Connector attempt;
        try{
            // Try to create a connection to see if this configuration is valid.
            // If this throws an exception then the connection pool
            // is not created and passed on to the ConnectionDirector.
            attempt = (Connector)ctor.newInstance(args);
        }catch(java.lang.reflect.InvocationTargetException exc){
            Log.warning("failed to initialise connection "+name+": "+exc.getTargetException().getMessage());
            return;
        }catch(Throwable exc){
            // exc.getCause() is not Java 1.3 compatible
//             while(exc.getCause() != null)
//                 exc = exc.getCause();
            Log.warning("failed to initialise connection "+name+": "+exc.getMessage());
            return;
        }

	MinMaxPool connections = new MinMaxPool(ctor, args);
	connections.setMinimumFree(min);
	connections.setMaximumFree(max);
        connections.add(attempt);

	Connector connector = new ConnectorProxy(connections, retry);
	dir.setConnector(name, connector);
	Log.trace("set connection: "+name);

        initialised = true;
    }

    public String toString(){
        return getClass().getName()+'<'+name+'>';
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
