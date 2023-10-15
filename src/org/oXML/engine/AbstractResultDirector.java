package org.oXML.engine;

import org.oXML.xpath.XPathException;
import org.oXML.type.*;
import org.oXML.util.Log;

public abstract class AbstractResultDirector{

    protected ElementNode element;
    protected StringNode string;

    public void addHandler(ResultHandler handler){}

    public void start(String contenttype){}
    public void end(){}

    public final void output(Node value)
        throws ObjectBoxProcessingException {
        // first handle elements and attributes
        if(value.getType().instanceOf(AttributeNode.TYPE)){
            if(element == null)
                throw new ObjectBoxProcessingException
                    (value, "cannot add attribute: last node was not an element");
            element.setAttribute((AttributeNode)value.cast(AttributeNode.TYPE));
        }else if(value.getType().instanceOf(StringNode.TYPE)){
	    /* From XSL Transformations 1.0, 7.2 Creating Text:
	       Adjacent text nodes in the result tree are automatically merged.
	    */
	    if(string == null)
                string = (StringNode)value.cast(StringNode.TYPE);
	    else
		string = string.merge(value);
        }else if(value.getType().instanceOf(NodesetNode.TYPE)){
            // ignore, wait for kids to come
        }else{
	    clear();
            if(value.getType().instanceOf(ElementNode.TYPE)){
                // don't output yet, wait for possible attributes
                element = (ElementNode)value.cast(ElementNode.TYPE);
            }else{
                doNode(value);
            }
        }
    }

    /**
     * clear any cached nodes
     */
    public void clear()
        throws ObjectBoxProcessingException {
	if(element != null){
	    // push the queued-up element out first
	    doElement(element);
	    element = null;
	}
	if(string != null){
	    // push the queued-up string out first
	    doNode(string);
	    string = null;
	}
    }

    public final void pop(Name name)
        throws ObjectBoxProcessingException {
	clear();
	doPop(name);
    }

    protected abstract void doPop(Name name)
        throws ObjectBoxProcessingException;

    protected abstract void doNode(Node value)
        throws ObjectBoxProcessingException;

    protected abstract void doElement(ElementNode element)
        throws ObjectBoxProcessingException;

//     protected void text(String text){
//     }

//     protected void processingInstruction(ProcessingInstructionNode pi){
//     }

//     protected void comment(CommentNode value){
//     }
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
