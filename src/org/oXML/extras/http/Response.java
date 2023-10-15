package org.oXML.extras.http;

import org.oXML.type.*;
import org.oXML.engine.*;
import org.oXML.ObjectBoxException;

public class Response {

    public static final int RESPONSE = 0;
    public static final int REDIRECT = 1;
    public static final int FORWARD = 2;
    public static final int ERROR = 3;
    public static final int INCLUDE = 4;
    public static final int MESSAGE = 5; // other unidentified net:Message type

    protected int type = RESPONSE;
    private int code = 200;
    private String content;
    private String uri;
    private String message;

    public Response(String content){
	this.content = content;
    }

    public Response(int code, String content){
	this.code = code;
	this.content = content;
    }

    public Response(int type, int code, String content, String uri, String message){
	this.type = type;
	this.code = code;
	this.content = content;
	this.uri = uri;
	this.message = message;
    }

    public int getResponseType(){
	return type;
    }

    public int getCode(){
	return code;
    }

    public String getContent(){
	return content;
    }

    public String getURI(){
	return uri;
    }

    public String getMessage(){
	return message;
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
