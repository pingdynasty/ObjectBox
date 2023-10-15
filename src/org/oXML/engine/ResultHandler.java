package org.oXML.engine;

import java.util.Collection;
import org.oXML.type.Name;
import org.oXML.util.Log;

public interface ResultHandler {

    /**
     * called before start of output
     */
    public void start(String contenttype);

    /**
     * called after end of output
     */
    public void end();

//     public void startDocument();
//     public void endDocument();
    public void startDocumentType(String name, String publicId, String systemId,
				  String internalSubset);
    public void endDocumentType();

    public void notationDeclaration(String name, String publicId, String systemId);
    
    /**
     * @param notationName For unparsed entities, the name of the notation for the entity. For parsed entities, this is null.
     */
    public void entityDeclaration(String name, String publicId, 
				  String systemId, String notationName);

    public void startElement(Name name, Collection attributes);

    public void endElement(Name name);

    public void text(String text);

    public void comment(String text);

    public void processingInstruction(String target, String data);

    public Object getResult();
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
