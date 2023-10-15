package org.oXML.engine;

import org.oXML.type.Node;
import org.oXML.type.ElementNode;
import org.oXML.type.StringNode;
import org.oXML.type.NodeIterator;
import org.oXML.util.Log;

import java.io.Writer;
import java.io.StringWriter;
import org.oXML.engine.ResultHandler;
import org.oXML.engine.ResultDirector;
import org.oXML.engine.StreamResultHandler;

public class NodeSerializer{

    private String encoding;
    private static final String XML_CONTENT_TYPE = "text/xml";
    private static final String DEFAULT_ENCODING = "UTF-8";

    public NodeSerializer(){
        this(DEFAULT_ENCODING);
    }

    public NodeSerializer(String encoding){
        this.encoding = encoding;
    }

    public String toString(Node node)
        throws ObjectBoxProcessingException{
        if(node.getType().instanceOf(StringNode.TYPE)){
            return node.stringValue();
        }else{
            StringWriter writer = new StringWriter();
	    output(writer, node);
            return writer.toString();
        }
    }

    public void output(Writer writer, Node node)
	throws ObjectBoxProcessingException {
	ResultDirector dir = new ResultDirector(new StreamResultHandler(writer, encoding));
// 	dir.start(XML_CONTENT_TYPE);
	output(dir, node);
//  	dir.end();
	dir.clear();
// 	writer.flush();
    }

    public void output(ResultDirector dir, Node node)
        throws ObjectBoxProcessingException{
        dir.output(node);
        NodeIterator it = node.getChildNodes().getIterator();
        for(Node n = it.nextNode(); n != null; n = it.nextNode())
            output(dir, n);
        if(node.getType().instanceOf(ElementNode.TYPE)){
            dir.pop(node.getName());
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
