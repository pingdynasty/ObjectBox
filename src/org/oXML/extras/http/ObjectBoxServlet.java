package org.oXML.extras.http;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.Writer;
import java.net.URL;
import java.net.MalformedURLException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import org.oXML.util.Log;
import org.oXML.type.*;
import org.oXML.engine.*;
import org.oXML.engine.template.*;
import org.oXML.ObjectBoxException;
import org.tagbox.util.http.MultiPartRequest;

public class ObjectBoxServlet extends HttpServlet {
    protected ObjectBoxService service;

    public ObjectBoxServlet() throws ObjectBoxException{
	service = new ObjectBoxService();
    }

    public void init(ServletConfig servletConfig)
        throws ServletException{
	super.init(servletConfig);
	service.init(servletConfig);
    }

    public void doGet(HttpServletRequest req,
                      HttpServletResponse res)
        throws ServletException, IOException{
        doPost(req, res);
    }

    public void doPost(HttpServletRequest req,
                       HttpServletResponse res)
        throws ServletException, IOException{
        // this is for trace/debug output only, can be removed
        StringBuffer msg = new StringBuffer(req.getRequestURI());
        String query = req.getQueryString();
        if(query != null)
            msg.append('?').append(query);
        msg.append(" : ");
        long time = System.currentTimeMillis();
        // end trace/debug msg (more near end)

        if(MultiPartRequest.isMultiPart(req))
            req = new MultiPartRequest(req, service.getUploadDirectory());

	try{
	    execute(req, res);
        }catch(FileNotFoundException exc){
	    res.sendError(HttpServletResponse.SC_NOT_FOUND, exc.getMessage());
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
            throw new ServletException(exc);
        }catch(SAXException exc){
	    Log.exception(exc);
            throw new ServletException(exc);
	}catch(TransformerException exc){
	    Log.exception(exc);
            throw new ServletException(exc);
        }
        // trace/debug msg
        msg.append(System.currentTimeMillis()-time).append("ms");
        Log.trace(msg.toString());
        // end trace/debug msg
    }

    public Response execute(HttpServletRequest req,
			    HttpServletResponse res)
        throws ServletException, IOException, ObjectBoxException, 
	       SAXException, TransformerException {

	InterpretedProgram program = service.getProgram(req);

	// determine if we're outputting XML or not
	String contenttype = program.getContentType();
	boolean xmloutput =  service.isXsltEnabled() && 
            ( contenttype.equals("text/xml") || contenttype.equals("application/xml") );

	ResultHandler handler;
	if(xmloutput){
	    handler = new DOMResultHandler();
	}else if(contenttype.equals(Program.NO_CONTENT_TYPE)){
	    handler = new NullResultHandler();
	}else{
	    handler = new StreamResultHandler(res.getWriter());
            res.setContentType(contenttype);
	}

	// run the program
	Response response = service.service(program, req, res, handler);

	if(response != null){
	    switch(response.getResponseType()){
	    case Response.REDIRECT : {
		Log.trace("redirecting to: "+response.getURI());
		res.sendRedirect(res.encodeRedirectURL(response.getURI()));
		return response;
	    }
	    case Response.FORWARD : {
		Log.trace("forwarding to: "+response.getURI());
		RequestDispatcher dispatcher  = req.getRequestDispatcher(response.getURI());
		if(dispatcher == null)
		    throw new ServletException("no request dispatcher available for "+
					       response.getURI());
		dispatcher.forward(req, res);
		// getServletContext().getRequestDispatcher() / .getNamedDispatcher(name)
		// req.getRequestDispatcher(path).forward(req, res); 
		return response;
	    }
	    case Response.ERROR : {
		res.sendError(response.getCode(), response.getMessage());
		return response;
	    }
	    case Response.INCLUDE : {
		Log.trace("including: "+response.getURI());
		RequestDispatcher dispatcher  = req.getRequestDispatcher(response.getURI());
		if(dispatcher == null)
		    throw new ServletException("no request dispatcher available for "+
					       response.getURI());
		dispatcher.include(req, res);
		// deliberately falls back to end of block: 'if(xmloutput)'
		break;
	    }
	    default :
		// todo: set response headers
		res.setStatus(response.getCode());
		if(response.getContent() != null){
		    res.getWriter().write(response.getContent());
		    res.getWriter().close();
		}
		return response;
	    }
	}
	if(xmloutput){
	    // run an XSL transformation on the result
	    Source source = new DOMSource((org.w3c.dom.Node)handler.getResult());
	    source.setSystemId(program.getSystemId());
	    service.transform(source, res);
	}
	return response;
    }

    public void destroy() {
	// deallocate resources
	service.destroy();
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
