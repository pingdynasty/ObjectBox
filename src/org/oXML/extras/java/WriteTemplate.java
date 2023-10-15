package org.oXML.extras.java;

import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodeIterator;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.EvalTemplate;
import org.oXML.ObjectBoxException;
import org.oXML.xpath.Expression;
import org.oXML.xpath.XPathException;
import org.oXML.util.Log;

import java.io.IOException;
import java.io.FileWriter;
import org.oXML.type.ElementNode;
import org.oXML.type.NodeIterator;
import org.oXML.engine.ResultHandler;
import org.oXML.engine.ResultDirector;
import org.oXML.engine.StreamResultHandler;

public class WriteTemplate extends EvalTemplate{

    private Expression filename;

    public WriteTemplate(Expression filename, Template body){
        super(body);
	this.filename = filename;
    }

    public WriteTemplate(Expression filename, Expression select){
        super(select);
	this.filename = filename;
    }

    public void process(RuntimeContext env)
        throws ObjectBoxException{

	try{
	    Node value = evaluate(env);
	    String file = filename.evaluate(env).stringValue();
	    FileWriter writer = new FileWriter(file);
	    ResultDirector dir = new ResultDirector(new StreamResultHandler(writer));
	    output(dir, value);
	    writer.close();
	}catch(IOException exc){
	    throw new ObjectBoxException(exc);
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

    public String toString(){
        return getClass().getName()+'<'+filename+'>';
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
