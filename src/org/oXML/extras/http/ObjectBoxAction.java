package org.oXML.extras.http;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import org.oXML.util.Log;
import org.oXML.type.Program;
import org.oXML.engine.*;
import org.oXML.ObjectBoxException;
import org.tagbox.util.http.MultiPartRequest;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionServlet;
import org.apache.struts.action.RedirectingActionForward;
import org.apache.struts.action.ForwardingActionForward;
import org.apache.struts.action.ActionForward;
import org.apache.struts.config.ForwardConfig;

/**
 * A Struts (@see http://struts.apache.org/) Action class that implements
 * ObjectBox Servlet-like behaviour, capable of producing Struts redirect/forward responses.
 */
public class ObjectBoxAction extends Action {
    protected ObjectBoxService service;

    public ObjectBoxAction() throws ObjectBoxException{
	service = new ObjectBoxService();
    }

    public void setServlet(ActionServlet servlet){
	if(servlet == null){
	    destroy();
	}else{
	    try{
		service.init(servlet.getServletConfig());
	    }catch(ServletException exc){
		Log.exception(exc);
	    }
	}
    }

    public ActionForward execute(ActionMapping mapping,
				 ActionForm form,
				 HttpServletRequest req,
				 HttpServletResponse res)
        throws ServletException, IOException, ObjectBoxException, 
	       SAXException, TransformerException {

        if(MultiPartRequest.isMultiPart(req))
            req = new MultiPartRequest(req, service.getUploadDirectory());

	String docname = mapping.getParameter();
	if(docname == null)
	    docname = mapping.getPath();
	docname = service.getBaseURL()+docname;

	Log.trace("ObjectBox action: "+docname);
 	InterpretedProgram program = service.compile(docname);

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
	}

	// run the program
	Response response = service.service(program, req, res, handler);
	if(response != null){
	    switch(response.getResponseType()){
	    case Response.REDIRECT : {
		Log.trace("redirecting to: "+response.getURI());
		return new RedirectingActionForward(response.getURI());
	    }
	    case Response.FORWARD : {
		String uri = response.getURI();
		Log.trace("forwarding to: "+uri);
		ActionForward action = mapping.findForward(uri);
		if(action != null)
		    return action;
		ForwardConfig cfg = mapping.getModuleConfig().findForwardConfig(uri);
		if(cfg == null)
		    return new ForwardingActionForward(uri);
		if(cfg.getRedirect())
		    return new RedirectingActionForward(cfg.getPath());
		else
		    return new ForwardingActionForward(cfg.getPath());
	    }
	    case Response.ERROR : {
		Log.trace("sending error: "+response.getMessage());
		res.sendError(response.getCode(), response.getMessage());
		return null;
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
		return null;
	    }
	}
	if(xmloutput){
	    // run an XSL transformation on the result
	    Source source = new DOMSource((org.w3c.dom.Node)handler.getResult());
	    source.setSystemId(program.getSystemId());
	    service.transform(source, res);
	}
	return null;
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
