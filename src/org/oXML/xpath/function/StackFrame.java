package org.oXML.xpath.function;

import org.oXML.type.Name;
import org.oXML.xpath.SourceLocator;
import org.oXML.util.Log;

public class StackFrame {
//     private FunctionCall function; // the function call that caused this frame
    private String call; // the function or procedure call that generated this frame
    private SourceLocator location; // where the function call occured

//     public StackFrame(FunctionCall function, SourceLocator location){
// 	this.function = function;
// 	this.location = location;
//     }

    public StackFrame(String call, SourceLocator location){
	this.call = call;
	this.location = location;
    }

    public SourceLocator getLocation(){
	return location;
    }

//     public Name getFunctionName(){
// 	return function.getName();
//     }

    public String getCall(){
	return call;
    }

    public String toString(){
	return call+": "+(location == null ? "unknown" : location.print());
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
