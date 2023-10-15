package org.oXML.engine.mapping.dom;

import org.oXML.engine.template.DocumentTemplate;
import org.oXML.engine.template.Template;
import org.oXML.engine.CompilationContext;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;
import org.w3c.dom.Node;
import org.w3c.dom.Entity;
import org.w3c.dom.Element;
import org.w3c.dom.Notation;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.DocumentType;

public class DocumentMapping implements TemplateMapping {

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{
	Template body = env.getBody(e);
	return new DocumentTemplate(body);
    }

    public Template map(Document doc, CompilationContext env)
        throws ObjectBoxException{

	Template body = env.getBody(doc);

	DocumentType doctype = doc.getDoctype();
	if(doctype == null)
	    return new DocumentTemplate(body);

	String pub = doctype.getPublicId();
	String sys = doctype.getSystemId();
	String name = doctype.getName();
	String internal = doctype.getInternalSubset();

	DocumentTemplate template;
	if(pub == null && sys == null && internal == null)
	    return new DocumentTemplate(body);

	template = new DocumentTemplate(name, pub, sys, internal, body);

	NamedNodeMap ents = doctype.getEntities();
	for(int i=0; i<ents.getLength(); ++i){
	    Entity ent = (Entity)ents.item(i);
	    template.addEntity(ent.getNodeName(), ent.getPublicId(),
			       ent.getSystemId(), ent.getNotationName());
	}

	NamedNodeMap nots = doctype.getNotations();
	for(int i=0; i<nots.getLength(); ++i){
	    Notation not = (Notation)nots.item(i);
	    template.addNotation(not.getNodeName(), not.getPublicId(), 
				 not.getSystemId());
	}
	return template;
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
