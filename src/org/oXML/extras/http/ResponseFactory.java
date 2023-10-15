package org.oXML.extras.http;

import org.oXML.type.*;
import org.oXML.engine.*;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;

public class ResponseFactory {

    private static final Name REDIRECT_TYPE = new Name(ServletExtensions.SERVLET_NS, "Redirect");
    private static final Name FORWARD_TYPE = new Name(ServletExtensions.SERVLET_NS, "Forward");
    private static final Name INCLUDE_TYPE = new Name(ServletExtensions.SERVLET_NS, "Include");
//   private static final Name RESPONSE_TYPE = new Name(ServletExtensions.SERVLET_NS, "Response");
    private static final Name ERROR_TYPE = new Name(ServletExtensions.SERVLET_NS, "Error");
    private static final Name MESSAGE_TYPE = new Name("http://www.o-xml.org/lib/net/", "Message");
    private static final Name URI_FUNCTION = new Name("uri"); // servlet:Redirect.uri()
    private static final Name CODE_FUNCTION = new Name("code"); // net:HttpResponse.code()
    private static final Name CONTENT_FUNCTION = new Name("content"); // net:Message.content()
    private static final Name MESSAGE_FUNCTION = new Name("message"); // net:HttpResponse.message()
    private static final Node[] NO_ARGS = new Node[]{};

    public Response createResponse(Program program, Node value)
	throws ObjectBoxException {
	if(value == null)
	    return null;
	Type type = program.getType(REDIRECT_TYPE);
	if(type != null && value.getType().instanceOf(type)){
	    int code = (int)value.invoke(CODE_FUNCTION, NO_ARGS).numberValue();
	    String uri = value.invoke(URI_FUNCTION, NO_ARGS).stringValue();
	    return new Response(Response.REDIRECT, code, null, uri, null);
	}
	type = program.getType(FORWARD_TYPE);
	if(type != null && value.getType().instanceOf(type)){
	    String uri = value.invoke(URI_FUNCTION, NO_ARGS).stringValue();
	    return new Response(Response.FORWARD, 204, null, uri, null);
	}
	type = program.getType(ERROR_TYPE);
	if(type != null && value.getType().instanceOf(type)){
	    int code = (int)value.invoke(CODE_FUNCTION, NO_ARGS).numberValue();
	    String msg = value.invoke(MESSAGE_FUNCTION, NO_ARGS).stringValue();
	    return new Response(Response.ERROR, code, null, null, msg);
	}
	type = program.getType(INCLUDE_TYPE); // todo: remove - currently not used??
	if(type != null && value.getType().instanceOf(type)){
	    String uri = value.invoke(URI_FUNCTION, NO_ARGS).stringValue();
	    return new Response(Response.INCLUDE, 204, null, uri, null);
	}
	// servlet:Response handled same as net:Message
// 	    type = program.getType(RESPONSE_TYPE);
// 	    if(type != null && value.getType().instanceOf(type)){
// 		String content = value.invoke(CONTENT_FUNCTION, NO_ARGS).stringValue();
// 		return new Response(content);
// 	    }
	type = program.getType(MESSAGE_TYPE);
	if(type != null && value.getType().instanceOf(type)){
	    int code = (int)value.invoke(CODE_FUNCTION, NO_ARGS).numberValue();
	    String content = value.invoke(CONTENT_FUNCTION, NO_ARGS).stringValue();
	    return new Response(code, content);
	}
	Log.warning("unknown return value type: "+value.getType().getName());
	String content = value.stringValue();
	return new Response(content);
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
