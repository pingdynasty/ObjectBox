package org.oXML.engine.mapping;

import org.oXML.xpath.SourceLocator;

/**
 * SourceLocator that knows only about files.
 */
public class FileLocator implements SourceLocator {

    private final String systemId;
    private String name;

    public FileLocator(String systemId){
        if(systemId == null)
            systemId = "";
	this.systemId = systemId;
	int pos = systemId.lastIndexOf('/');
	if(pos++ > 0 && pos < systemId.length())
	    name = systemId.substring(pos);
	else
	    name = systemId;
    }

    public int getColumnNumber(){
	return 0;
    }

    public int getLineNumber(){
	return 0;
    }

    public String getSystemId(){
	return systemId;
    }

    public String getPublicId(){
	return null;
    }

    public String print(){
	return name;
    }

    public String toString(){
	return getClass().getName()+'<'+print()+'>';
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
