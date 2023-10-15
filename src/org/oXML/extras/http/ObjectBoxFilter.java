package org.oXML.extras.http;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;

public class ObjectBoxFilter extends ObjectBoxServlet implements Filter {

    public ObjectBoxFilter() throws ObjectBoxException {
	super();
    }

    public void init(FilterConfig cfg)
        throws ServletException {
	service.init(cfg);
    }

    public void doFilter(ServletRequest req, ServletResponse res,
			 FilterChain chain) 
	throws IOException, ServletException {
	Response response = null;
	if(req instanceof HttpServletRequest &&
	   res instanceof HttpServletResponse){
	    try{
		response = execute((HttpServletRequest)req, (HttpServletResponse)res);
	    }catch(ObjectBoxException exc){
		throw new ServletException(exc);
	    }catch(SAXException exc){
		throw new ServletException(exc);
	    }catch(TransformerException exc){
		throw new ServletException(exc);
	    }
	}
	// only continue filter chain if page did not return or commit a response
	if(response == null && !res.isCommitted())
	    chain.doFilter(req, res);
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
