package org.oXML.extras.db;

import java.sql.Types;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.w3c.dom.Element;
import org.tagbox.xml.NodeFinder;
import org.tagbox.util.ResourceManager;
import org.oXML.util.Log;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.type.StringNode;
import org.oXML.type.NumberNode;
import org.oXML.type.NodesetNode;
import org.oXML.type.BooleanNode;
import org.oXML.type.NodeIterator;
import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.template.Template;

/**
 * wraps a jdbc Connection
 * this implementation is not thread safe!
 */
public class JdbcConnector implements Connector {
    private Connection connection;

    private String driver;
    private String url;
    private String user;
    private String passwd;
    private boolean log = false;
    private String dialect;

    public JdbcConnector(String driver, String url, String user, String passwd, String log, String dialect)
	throws DatabaseException{
	this.driver = driver;
	this.url = url;
	this.user = user;
	this.passwd = passwd;
	this.log = !log.equals("") && !log.equals("no") && !log.equals("false");
        this.dialect = dialect;
	connect();
    }

    public String getDialect(){
        if(dialect == null || dialect.equals("")){
            dialect = StatementTemplate.DEFAULT_DIALECT_NAME;
            if(connection != null){
                // try to automatically determine SQL dialect from JDBC connection
                try{
                    String product = connection.getMetaData().getDatabaseProductName();
                    Log.trace("Database product: "+product);
                    if(product.indexOf("HSQL") != -1)
                        dialect = "hsql";
                    else if(product.indexOf("MySQL") != -1)
                        dialect = "mysql";
                    else if(product.indexOf("PostgreSQL") != -1)
                        dialect = "pgsql";
                    else if(product.indexOf("SQL Server") != -1)
                        dialect = "sqlserver";
                    else if(product.indexOf("Oracle") != -1)
                        dialect = "oracle";
                    else if(product.indexOf("DB2") != -1)
                        dialect = "db2";
                    else if(product.indexOf("Informix") != -1)
                        dialect = "informix";
                }catch(Exception e){}
            }
        }
        Log.trace("Connection SQL dialect: "+dialect);
        return dialect;
    }

    public void execute(String statement)
        throws DatabaseException {
	if(log)
	    Log.info("execute query: "+statement);
        try{
	    if(connection == null || connection.isClosed())
		connect();
            Statement stmt = connection.createStatement();
            stmt.execute(statement);
            stmt.close();
        }catch(SQLException e){
            throw new DatabaseException(e);
        }
    }

    public void execute(String statement, Template target, RuntimeContext context)
        throws ObjectBoxException {
	if(log)
	    Log.info("execute query: "+statement);
	context.hide(); // hide existing variables
        int savedPos = context.getContextPosition();
        try{
	    if(connection == null || connection.isClosed())
		connect();

            Statement stmt = connection.createStatement();
            stmt.execute(statement);
            ResultSet rs = stmt.getResultSet();
            ResultSetMetaData meta = rs.getMetaData();

            // get meta data: number of columns and column names
            int cols = meta.getColumnCount();
            Name names[] = new Name[cols];
            int types[] = new int[cols];
            for(int i=0; i<cols; ++i){
                names[i] = new Name(meta.getColumnLabel(i+1).toLowerCase());
                types[i] = meta.getColumnType(i+1);
            }
            int pos = 1;
            while(rs.next()){
		// we don't need to hide variables for each row because
		// we set all of them - no 'old' values
		// and we're not keeping old values anyhow
		for(int i=0; i<cols; ++i){
                    switch(types[i]){
                        // figure out variable type.
                        // it would be nice to have a core o:XML java
                        // object type to convert to.
                    case Types.NULL :
                        context.setVariable(names[i], NodesetNode.EMPTY_SET);
                        break;
                    case Types.BIT :
                    case Types.BOOLEAN :
                        context.setVariable(names[i], BooleanNode.booleanNode(rs.getBoolean(i+1)));
                        break;
                    case Types.REAL :
                    case Types.FLOAT :
                    case Types.BIGINT :
                    case Types.DECIMAL :
                    case Types.INTEGER :
                    case Types.NUMERIC :
                    case Types.TINYINT :
                    case Types.SMALLINT :
                    case Types.DOUBLE :
                        context.setVariable(names[i], new NumberNode(rs.getDouble(i+1)));
                        break;
//                     case Types.DATE :
//                     case Types.TIME :
//                     case Types.TIMESTAMP :
                        // no core o:XML type available
//                     case Types.CLOB :
//                     case Types.CHAR :
//                     case Types.VARCHAR :
//                     case Types.LONGVARCHAR :
                    default :
                        context.setVariable(names[i], new StringNode(rs.getString(i+1)));
                    }
                }
                context.setContextPosition(pos++);
                target.process(context); // produces output to result handler
            }
            stmt.close();
        }catch(SQLException e){
            throw new DatabaseException(e);
	}finally{
            context.setContextPosition(savedPos);
	    context.unhide();
	}
    }

    protected void connect(String driver, String url, 
                           String user, String password)
        throws SQLException, ClassNotFoundException {
        Class.forName(driver);
        connection = DriverManager.getConnection(url, user, password);
        Log.trace("connected to: "+url+" as "+user);
    }

    public void connect()
	throws DatabaseException{
        try{
            connect(driver, url, user, passwd);
        }catch(ClassNotFoundException exc){
            throw new DatabaseException("JDBC driver class not found: "+driver, exc);
        }catch(SQLException exc){
            throw new DatabaseException(exc);
        }
    }

    public void close()
	throws DatabaseException{
        try{
	    if(connection != null)
		connection.close();
        }catch(SQLException exc){
            throw new DatabaseException(exc);
        }
    }

    public void finalize()
        throws SQLException
    {
        connection.close();
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
