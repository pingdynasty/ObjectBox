package org.oXML.engine.template;

import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.engine.ObjectBoxProcessingException;
import org.oXML.type.Node;
import org.oXML.type.ElementNode;
import org.oXML.type.StringNode;
import org.oXML.type.NodeIterator;
import org.oXML.util.Log;
import org.oXML.xpath.Expression;

import java.io.StringWriter;
import org.oXML.engine.ResultHandler;
import org.oXML.engine.ResultDirector;
import org.oXML.engine.StreamResultHandler;

public class LogTemplate extends EvalTemplate{

    public static final int DEBUG_LEVEL = 1;
    public static final int INFO_LEVEL = 2;
    public static final int WARNING_LEVEL = 3;
    public static final int ERROR_LEVEL = 4;
    public static final int DEFAULT_LEVEL = INFO_LEVEL;

    private int level;

    public LogTemplate(int level, Expression select){
        super(select);
	this.level = level;
    }

    public LogTemplate(int level, Template body){
        super(body);
	this.level = level;
    }

    public void process(RuntimeContext env)
        throws ObjectBoxException{
        Node value = evaluate(env);
        if(value.getType().instanceOf(StringNode.TYPE)){
            log(value.stringValue());
        }else{
            StringWriter writer = new StringWriter();
            ResultDirector dir = new ResultDirector(new StreamResultHandler(writer));
            output(dir, value);
            log(writer.toString());
        }
    }

    protected void log(String msg){
	switch(level) {
	case DEBUG_LEVEL:
	    Log.trace(msg);
	    break;
	case INFO_LEVEL:
	    Log.info(msg);
	    break;
	case WARNING_LEVEL:
	    Log.warning(msg);
	    break;
	case ERROR_LEVEL:
	    Log.error(msg);
	    break;
	}
    }

    protected void output(ResultDirector dir, Node node)
        throws ObjectBoxException{
        dir.output(node);
        NodeIterator it = node.getChildNodes().getIterator();
        for(Node n = it.nextNode(); n != null; n = it.nextNode())
            output(dir, n);
        if(node.getType().instanceOf(ElementNode.TYPE)){
            ElementNode element = (ElementNode)node.cast(ElementNode.TYPE);
            dir.pop(element.getName());
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
