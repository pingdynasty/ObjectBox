package org.oXML.engine;

import org.oXML.xpath.XPathException;
import org.oXML.type.*;
import org.oXML.util.Log;

public class NodeCollector extends AbstractResultDirector{

    private Nodeset contents;
    private Node current;

    public NodeCollector(Nodeset contents){
        this.contents = contents;
    }

    public Nodeset getContents(){
	return contents;
    }

    protected void doPop(Name name)
        throws ObjectBoxProcessingException{
//         super.pop(name);
// 	Log.trace("do pop: "+contents);
	// assert(current instanceOf Element)
	// assert(current.getName().equals(name))
	// assert(contents.getParent() != null)
        if(!current.getType().instanceOf(ElementNode.TYPE))
            Log.error("pop "+name+": output not an Element: "+current);
	current = current.getParent();
        if(current == NodesetNode.EMPTY_SET)
	    current = null;
    }

    protected void doNode(Node value){
// 	Log.trace("do node: "+value);
	if(current == null)
	    contents.addNode(value);
	else
	    current.addChildNode(value);
    }

    protected void doElement(ElementNode element)
        throws ObjectBoxProcessingException{
// 	Log.trace("do element: "+element);
        element = (ElementNode)element.copy(false).cast(ElementNode.TYPE);
	// make shallow copy in order to exclude child nodes
	if(current == null)
	    contents.addNode(element);
	else
	    current.addChildNode(element);
        current = element;
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
