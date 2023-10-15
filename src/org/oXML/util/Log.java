package org.oXML.util;

import java.util.Date;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;

public class Log{

    private PrintWriter out;
    private int level = INFO_LEVEL;
    private boolean timestamp = true;

    private static final Log me = new Log();

    public static final int DEBUG_LEVEL = 1;
    public static final int INFO_LEVEL = 2;
    public static final int WARNING_LEVEL = 3;
    public static final int ERROR_LEVEL = 4;
    public static final int NONE_LEVEL = Integer.MAX_VALUE;

    public Log(){
        out = new PrintWriter(System.err, true);
    }

    public static Log getLog(){
	return me;
    }

    public static void setOutput(OutputStream out){
	me.out = new PrintWriter(out, true);
    }

    public static void setOutput(Writer out){
	me.out = new PrintWriter(out, true);
    }

    public static void setOutput(PrintWriter out){
	me.out = out;
    }

    /**
     * Set the log level using one of the level names:
     * <code>debug</code>
     * <code>info</code>
     * <code>warning</code>
     * <code>error</code> or:
     * <code>none</code>
     */
    public static void setLevel(String level){
	if("debug".equalsIgnoreCase(level))
	    setLevel(DEBUG_LEVEL);
	else if("info".equalsIgnoreCase(level))
	    setLevel(INFO_LEVEL);
	else if("warning".equalsIgnoreCase(level))
	    setLevel(WARNING_LEVEL);
	else if("error".equalsIgnoreCase(level))
	    setLevel(ERROR_LEVEL);
	else if("none".equalsIgnoreCase(level))
	    setLevel(NONE_LEVEL);
	else{
	    setLevel(DEBUG_LEVEL);
	    error("unknown log level: "+level
                  +". Valid levels are info, debug, warning, error or none.");
	}
    }

    public static void setLevel(int level){
	me.level = level;
    }

    public static int getLevel(){
	return me.level;
    }

    /**
     * control whether the log output should include the date and time
     */
    public static void timestamp(boolean timestamp){
	me.timestamp = timestamp;
    }

    /**
     * check whether the log output should include the date and time
     */
    public static boolean timestamp(){
	return me.timestamp;
    }

    public static void trace(String msg){
        me.log(DEBUG_LEVEL, msg);
    }

    public static void info(String msg){
        me.log(INFO_LEVEL, msg);
    }

    public static void warning(String msg){
        me.log(WARNING_LEVEL, msg);
    }

    public static void error(String msg){
        me.log(ERROR_LEVEL, msg);
    }

    public static void exception(Throwable exc){
        me.log(ERROR_LEVEL, exc.getMessage());
	if(me.level == DEBUG_LEVEL)
	    exc.printStackTrace(me.out);
    }

    public static void exception(Throwable exc, String msg){
	error(msg);
        error("\t  "+exc.getMessage());
	if(me.level == DEBUG_LEVEL)
	    exc.printStackTrace(me.out);
    }

    public void log(int type, String msg){
	if(level > type)
	    return;
	String head;
	switch(type){
	case DEBUG_LEVEL :
	    head = "trace";
	    break;
	case INFO_LEVEL :
	    head = "info";
	    break;
	case WARNING_LEVEL :
	    head = "warning";
	    break;
	case ERROR_LEVEL :
	    head = "error";
	    break;
	default:
	    head = "unknown";
	}
	if(timestamp)
	    out.print(new Date()+"\t");
	out.println(head+": "+msg);
    }
}
/*
    ObjectBox - MLML compiler and interpretor
    for more information see http://www.o-xml.org/objectbox
    Copyright (C) 2002-2005 Martin Klang, Alpha Plus Technology Ltd
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
