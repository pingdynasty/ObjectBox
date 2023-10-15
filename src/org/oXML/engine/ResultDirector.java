package org.oXML.engine;

import java.util.Iterator;
import java.util.Collection;
import org.oXML.xpath.XPathException;
import org.oXML.type.*;
import org.oXML.util.Log;

public class ResultDirector extends AbstractResultDirector{

    private ResultHandler[] handlers;

    public ResultDirector(){
        handlers = new ResultHandler[0];
    }

    public ResultDirector(ResultHandler handler){
        handlers = new ResultHandler[]{handler};
    }

    public void start(String contenttype){
        for(int i=0; i<handlers.length; i++)
            handlers[i].start(contenttype);
    }

    public void end(){
        for(int i=0; i<handlers.length; i++)
            handlers[i].end();
    }

    public void addHandler(ResultHandler handler){
        ResultHandler[] updated = new ResultHandler[handlers.length + 1];
        for(int i=0; i<handlers.length; i++)
            updated[i] = handlers[i];
        updated[handlers.length] = handler;
        handlers = updated;
    }

    protected void doNode(Node value)
        throws ObjectBoxProcessingException{
        if(value.getType().instanceOf(StringNode.TYPE) ||
                 value.getType().instanceOf(NumberNode.TYPE) ||
                 value.getType().instanceOf(BooleanNode.TYPE)){
            // it's either a Number, Boolean or String
            text(value.stringValue());
        }else if(value.getType().instanceOf(ProcessingInstructionNode.TYPE)){
            processingInstruction(value.getName().toString(), value.stringValue());
        }else if(value.getType().instanceOf(CommentNode.TYPE)){
            comment(value.stringValue());
        }else if(value.getType().instanceOf(DocumentNode.TYPE)){
	    document((DocumentNode)value.cast(DocumentNode.TYPE));
        }else
	    Log.trace("unhandled output: "+value);
    }

    protected void doPop(Name name)
        throws ObjectBoxProcessingException{
        for(int i=0; i<handlers.length; i++)
            handlers[i].endElement(name);
    }

    protected void text(String text){
        for(int i=0; i<handlers.length; i++)
            handlers[i].text(text);
    }

    protected void doElement(ElementNode element)
        throws ObjectBoxProcessingException{
        // push the element
        Name name = element.getName();
        Collection attrs = element.getAttributes();
        for(int i=0; i<handlers.length; i++)
            handlers[i].startElement(name, attrs);
    }

    protected void processingInstruction(String target, String data){
        for(int i=0; i<handlers.length; i++)
            handlers[i].processingInstruction(target, data);
    }

    protected void comment(String value){
        for(int i=0; i<handlers.length; i++)
            handlers[i].comment(value);
    }

    // where does endDocument come from? pop??
    protected void document(DocumentNode doc){
	// todo: if standalone, output XML declaration
//         for(int i=0; i<handlers.length; i++)
//             handlers[i].startDocument();
	DocumentTypeNode doctype = doc.getDocumentType();
	if(doctype != null){
	    for(int i=0; i<handlers.length; i++){
		handlers[i].startDocumentType(doctype.getDocumentTypeName(),
					      doctype.getPublicId(),
					      doctype.getSystemId(),
					      doctype.getInternalSubset());
		// do notations and entities
		Iterator it = doctype.getEntities().iterator();
		while(it.hasNext()){
		    EntityNode ent = (EntityNode)it.next();
		    handlers[i].entityDeclaration(ent.getEntityName(),
						  ent.getPublicId(),
						  ent.getSystemId(),
						  ent.getNotationName());
		}
		it = doctype.getNotations().iterator();
		while(it.hasNext()){
		    NotationNode notation = (NotationNode)it.next();
		    handlers[i].notationDeclaration(notation.getNotationName(),
						    notation.getPublicId(),
						    notation.getSystemId());
		}
		// doctype done
		handlers[i].endDocumentType();
	    }
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
