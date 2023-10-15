package org.oXML.engine.mapping;

import org.xml.sax.Locator;
import org.oXML.xpath.SourceLocator;

public class SimpleLocator implements SourceLocator, Locator {

    private final int column;
    private final int line;
    private final String systemId;
    private final String publicId;

    public SimpleLocator(Locator locator){
	column = locator.getColumnNumber();
	line = locator.getLineNumber();
	systemId = locator.getSystemId();
	publicId = locator.getPublicId();
    }

    public SimpleLocator(int column, int line, String systemId, String publicId){
	this.column = column;
	this.line = line;
	this.systemId = systemId;
	this.publicId = publicId;
    }

    public int getColumnNumber(){
	return column;
    }

    public int getLineNumber(){
	return line;
    }

    public String getSystemId(){
	return systemId;
    }

    public String getPublicId(){
	return publicId;
    }

    public String print(){
	return systemId+" line: "+line+" column: "+column;
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
