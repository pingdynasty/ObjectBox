package org.oXML.extras.db;

import org.tagbox.util.Pool;
import org.oXML.type.Node;

import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.template.Template;

/**
 * DataConnection proxy that will retry any query (default max 3 times) if
 * it throws an exception.
 */
public class ConnectorProxy implements Connector{

    // retry max x times if there's an Exception
    private int retry = 3;

    private Pool pool;

    /**
     * create a new ConnectorProxy using a specific DataConnection pool
     */
    public ConnectorProxy(Pool pool){
        this.pool = pool;
    }

    public ConnectorProxy(Pool pool, int retry){
        this.pool = pool;
	this.retry = retry;
    }

    public String getDialect(){
        Connector con = (Connector)pool.get();
        try{
            return con.getDialect();
        }finally{
            pool.release(con);
        }
    }

    public void execute(String query)
        throws DatabaseException{
	execute(query, 0);
    }

    protected void execute(String query, int retries)
        throws DatabaseException{
        Connector con = (Connector)pool.get();
        try{
	    con.execute(query);
        }catch(DatabaseException exc){
            if(++retries > retry)
                throw exc;
            // otherwise try again
	    execute(query, retries);
        }finally{
            pool.release(con);
        }
    }

    public void execute(String query, Template target, RuntimeContext context)
        throws ObjectBoxException {
	execute(query, target, context, 0);
    }

    protected void execute(String query, Template target, 
			   RuntimeContext context, int retries)
        throws ObjectBoxException {
        Connector con = (Connector)pool.get();
        try{
            con.execute(query, target, context);
        }catch(DatabaseException exc){
            if(retries++ > retry)
                throw exc;
            // otherwise try again
            execute(query, target, context, retries);
        }finally{
            pool.release(con);
        }
    }

    //************************** transaction methods **********************//

    public boolean autoCommit()
        throws DatabaseException{
        return true;
    }

//      public boolean autoCommit()
//          throws DatabaseException{
//          throw new DatabaseException(getClass()+".autoCommit(): operation not supported");
//      }

    public void autoCommit(boolean auto)
        throws DatabaseException{
        if(!auto)
            throw new DatabaseException(getClass()+".autoCommit(boolean auto): operation not supported");
    }

    public void commit()
        throws DatabaseException{
        throw new DatabaseException(getClass()+".commit(): operation not supported");
    }

    public void rollback()
        throws DatabaseException{
        throw new DatabaseException(getClass()+".rollback(): operation not supported");
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
