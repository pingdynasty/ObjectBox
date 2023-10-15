package org.oXML.extras.http;

import java.util.StringTokenizer;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.MalformedURLException;
import javax.servlet.*;
import javax.servlet.http.*;
import org.xml.sax.SAXException;
import org.oXML.util.Log;
import org.oXML.type.*;
import org.oXML.engine.*;
import org.oXML.engine.template.*;
import org.oXML.xpath.iterator.DynamicNodeset;
import org.tagbox.xml.XSLTProcessor;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerException;
import org.oXML.engine.LanguageExtension;
import org.oXML.extras.java.JavaExtensions;
import org.oXML.extras.db.DatabaseExtensions;
import org.oXML.extras.xinclude.XIncludeExtensions;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;

/** utility class for Filters, Servlets and Struts Action specialisations */
public class ObjectBoxService {
    private ProgramProxy programproxy;
    private XSLTProcessor xsltProcessor;
    private ResponseFactory factory;
    private ServletContext ctxt;
    private String baseurl;
    private String uploaddir;
    private String program;
    private String parameterDelimiters = "/";
    private boolean useURLParameters = false;
    private boolean useServletParameters = true;
    private boolean useHttpParameters = true;
    private boolean xslt = true;
    private String xsltStylesheet = null;

    private static LanguageExtension[] extensions;
    private static JavaExtensions javaExtensions;
    private static final Name REQ_NAME = new Name(ServletExtensions.SERVLET_NS, "req");
    private static final Name RES_NAME = new Name(ServletExtensions.SERVLET_NS, "res");
    private static final Name SERVLET_NAME = new Name(ServletExtensions.SERVLET_NS, "servlet");
    private static final Name SESSION_NAME = new Name(CompilationContext.OXML_NS, "session");

    public void init(ServletConfig cfg)
        throws ServletException {
        init(new ServletFilterConfiguration(cfg));
    }

    public void init(FilterConfig cfg)
        throws ServletException {
        init(new ServletFilterConfiguration(cfg));
    }

    protected void init(ServletFilterConfiguration cfg)
        throws ServletException {
	// set loglevel
        String level = cfg.getInitParameter("loglevel");
	if(level != null)
            Log.setLevel(level);

	init(cfg.getServletContext());

        // get the program name, if set
        program = cfg.getInitParameter("program");
        if(program != null)
            program = baseurl + program;

	// get the upload directory - where we save any request attachments
	// (do after init(ctxt) as this needs baseurl to be set)
	uploaddir = cfg.getInitParameter("upload-directory");
	setUploadDirectory(uploaddir);

        // get url-parameters
        useURLParameters = cfg.getBooleanParameter("url-parameters", useURLParameters);

        // get url-parameter-delimiters
        parameterDelimiters = cfg.getInitParameter("url-parameter-delimiters", parameterDelimiters);

        // get servlet-parameters
        useServletParameters = cfg.getBooleanParameter("servlet-parameters", useServletParameters);

        // get http-parameters
        useHttpParameters = cfg.getBooleanParameter("http-parameters", useHttpParameters);

        // get xslt settings: xslt and xslt-stylesheet
        xslt = cfg.getBooleanParameter("xslt", xslt);
        xsltStylesheet = cfg.getInitParameter("xslt-stylesheet", xsltStylesheet);
    }

    protected void init(ServletContext context)
        throws ServletException {
	// initialise resources
        xsltProcessor = new XSLTProcessor();
        programproxy = new ProgramProxy();
	factory = new ResponseFactory();
	setBaseURL(context);
	try{
	    synchronized(this){
		if(extensions == null){
		    javaExtensions = new JavaExtensions();
		    extensions = new LanguageExtension[]{
			javaExtensions, 
			new DatabaseExtensions(),
			new XIncludeExtensions(),
			new ServletExtensions(javaExtensions.makeNode(context))
		    };
		}
	    }
	}catch(ObjectBoxException exc){
	    throw new ServletException(exc);
	}
    }

    protected void setBaseURL(ServletContext ctxt){
	baseurl = ctxt.getRealPath("/");
        try{
	    URL url;
	    if(baseurl == null)
		url = ctxt.getResource("/");
	    else
		url = new File(baseurl).toURL();
            baseurl = url.toString();
        }catch(MalformedURLException exc){
            Log.warning("invalid baseURL : "+baseurl);
        }
    }

    public boolean isXsltEnabled(){
        return xslt;
    }

    public String getBaseURL(){
	return baseurl;
    }

    protected void setUploadDirectory(String uploaddir){
	if(uploaddir == null){
	    Log.warning("no upload directory (upload-directory) specified");
	    Log.warning("using default: upload");
	    uploaddir = "upload";
	}
	this.uploaddir = baseurl+uploaddir;
	File dir = new File(this.uploaddir);
	if(!dir.exists())
	    dir.mkdirs();
	Log.trace("webapp upload-directory: "+this.uploaddir);
    }

    public String getUploadDirectory(){
	return uploaddir;
    }

    /** get the filename and directory part of the request uri, minus the context, plus baseURL */
    public String getFileURL(HttpServletRequest req){
	String xml = req.getRequestURI();
	String ctxt = req.getContextPath();
	if(ctxt != null && !ctxt.equals(""))
	    xml = xml.substring(xml.indexOf(ctxt)+ctxt.length()+1);
        return baseurl+xml;
    }

    public Node getSessionNode(HttpServletRequest req){
	return new SessionNode(req.getSession());
    }

    /** lookup and, if necessary, compile the program */
    public InterpretedProgram compile(String systemId)
        throws IOException, ObjectBoxException, 
	       SAXException {
	InterpretedProgram program = programproxy.getProgram(systemId);
	if(!program.compiled()){
	    // set the mappings from any configured Language Extensions
	    for(int i=0; i<extensions.length; ++i){
		Log.trace("adding language extension: "+extensions[i]);
		program.addExtension(extensions[i]);
	    }
	    Log.trace("compiling: "+systemId);
	    program.compile();
	}
	return program;
    }

    public InterpretedProgram getProgram(HttpServletRequest req)
        throws IOException, ObjectBoxException, SAXException {
	if(program == null)
	    return compile(getFileURL(req));
	return compile(program);
    }

    public void setHttpParameters(InterpretedProgram program, 
                                  HttpServletRequest req,
                                  RuntimeContext ctxt)
        throws ServletException, IOException, ObjectBoxException{
	// set any parameters as variables
	Name[] names = program.getParameterNames();
	for(int i=0; i<names.length; ++i){
            String[] values = req.getParameterValues(names[i].toString());
            if(values == null){
            }else if(values.length == 1){
                ctxt.setVariable(program.createParameter(names[i], values[0]));
            }else{
                Nodeset value = new DynamicNodeset();
                for(int j=0; j<values.length; ++j)
                    value.addNode(new StringNode(values[j]));
                ctxt.setVariable(program.createParameter(names[i], new NodesetNode(value)));
            }
	}
    }

    public void setServletParameters(InterpretedProgram program, 
                                     HttpServletRequest req,
                                     HttpServletResponse res, 
                                     RuntimeContext ctxt)
        throws ServletException, IOException, ObjectBoxException{
	// set servlet parameters as variables
	Name[] names = program.getParameterNames();
	for(int i=0; i<names.length; ++i){
	    if(REQ_NAME.equals(names[i]))
		ctxt.setVariable(program.createParameter
                                 (names[i], javaExtensions.makeNode(req)));
	    else if(RES_NAME.equals(names[i]))
		ctxt.setVariable(program.createParameter
                                 (names[i], javaExtensions.makeNode(res)));
	    else if(SESSION_NAME.equals(names[i]))
		ctxt.setVariable(program.createParameter
                                 (names[i], getSessionNode(req)));
        }
    }

    public void setCookieParameters(InterpretedProgram program, 
                                    HttpServletRequest req,
                                    RuntimeContext ctxt)
        throws ServletException, IOException, ObjectBoxException{
        // todo!
//         Cookie[] cookies = req.getCookies();
//         cookies.getName();
//         cookies[i].getValue();
    }

    public void setURLParameters(InterpretedProgram program, 
                                 HttpServletRequest req,
                                 RuntimeContext ctxt)
        throws ServletException, IOException, ObjectBoxException{
        String prefix = req.getServletPath();
        String uri = req.getRequestURI();
        int pos = uri.indexOf(prefix) + prefix.length();
        String args = uri.substring(pos);
	// set any parameters as variables
	Name[] names = program.getParameterNames();
        StringTokenizer tok = new StringTokenizer(args, parameterDelimiters);
        pos = 0; // parameter position
        while(tok.hasMoreTokens() && pos < names.length){
            String value = tok.nextToken();
            // URL unescape value
            value = java.net.URLDecoder.decode(value);
            ctxt.setVariable(program.createParameter(names[pos++], value));
        }
    }

    /** run the provided program after setting input parameters etc */
    public Response service(InterpretedProgram program, 
                            HttpServletRequest req,
                            HttpServletResponse res, 
                            ResultHandler output)
        throws ServletException, IOException,
               ObjectBoxException, SAXException{
        // create runtime context
        RuntimeContext ctxt = new RuntimeContext(program);
        ctxt.addResultHandler(output);

        if(useURLParameters)
            setURLParameters(program, req, ctxt);
        if(useServletParameters)
            setServletParameters(program, req, res, ctxt);
        if(useHttpParameters)
            setHttpParameters(program, req, ctxt);

	// run the program
	Node value = program.run(ctxt);
	return factory.createResponse(program, value);
    }

    /** run the provided program after setting input parameters etc */
    public Response run(InterpretedProgram program, RuntimeContext ctxt)
        throws ServletException, IOException, ObjectBoxException, SAXException{
	return factory.createResponse(program,  program.run(ctxt));
    }

    public void transform(Source source, HttpServletResponse res)
        throws ServletException, IOException, TransformerException {
        String stylesheet = xsltStylesheet;
        if(stylesheet == null)
            stylesheet = xsltProcessor.getAssociatedStylesheet(source);

	// figure out the right content type
	// output method - if no stylesheet then xml
//             String media = stylesheet == null ? "text/xml" :
//                 xsltProcessor.getMediaType(stylesheet);
// 	    Log.trace("output content type: "+media);
//             res.setContentType(media);
	String media = stylesheet == null ? "xml" :
	    xsltProcessor.getOutputMethod(stylesheet);
	res.setContentType("text/"+media);

	Writer out = res.getWriter();
	StreamResult target = new StreamResult(out);

	if(stylesheet == null)
	    xsltProcessor.process(source, target);
	else
	    xsltProcessor.process(source, target, stylesheet);

	out.close();
    }

    public void destroy() {
	// deallocate resources
	extensions = null;
	javaExtensions = null;
	programproxy = null;
	xsltProcessor = null;
	uploaddir = null;
	baseurl = null;
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
