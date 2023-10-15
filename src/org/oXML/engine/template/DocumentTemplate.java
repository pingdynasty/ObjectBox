package org.oXML.engine.template;

import java.util.List;
import java.util.ArrayList;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.type.Node;
import org.oXML.type.DocumentNode;
import org.oXML.type.DocumentTypeNode;
import org.oXML.type.EntityNode;
import org.oXML.type.NotationNode;
import org.oXML.util.Log;
import org.oXML.xpath.Expression;

/**
 */
public class DocumentTemplate implements Template {

    private Template body;
    private String name;
    private String publicId;
    private String systemId;
    private String internalSubset;
    private List entities;
    private List notations;

    public DocumentTemplate(Template body){
	entities = new ArrayList();
	notations = new ArrayList();
	this.body = body;
    }

    public DocumentTemplate(String name, String publicId, String systemId, 
			    String internalSubset, Template body){
	this(body);
	this.name = name;
	this.publicId = publicId;
	this.systemId = systemId;
	this.internalSubset = internalSubset;
    }

    public void addEntity(String name, String publicId, 
			  String systemId, String notationName){
	entities.add(new String[]{name, publicId, systemId, notationName});
    }

    public void addNotation(String name, String publicId, String systemId){
	notations.add(new String[]{name, publicId, systemId});
    }

    public void process(RuntimeContext env)
        throws ObjectBoxException{
	DocumentNode doc;
	if(name == null){
	    doc = new DocumentNode();
	}else{
	    DocumentTypeNode doctype = 
		new DocumentTypeNode(name, publicId, systemId, internalSubset);
	    for(int i=0; i<entities.size(); ++i){
		String[] tuple = (String[])entities.get(i);
		doctype.addEntity(new EntityNode(tuple[0], tuple[1], 
						 tuple[2], tuple[3]));
	    }
	    for(int i=0; i<notations.size(); ++i){
		String[] tuple = (String[])notations.get(i);
		doctype.addNotation(new NotationNode(tuple[0], tuple[1], tuple[2]));
	    }
	    doc = new DocumentNode(doctype);
	}
	env.push(doc);
	body.process(env);
	env.pop();
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
