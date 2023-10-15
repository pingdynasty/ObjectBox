package org.oXML.extras.http;

import javax.servlet.*;

public class ServletFilterConfiguration {

    private ServletConfig scfg;
    private FilterConfig fcfg;
    private ServletContext ctxt;

    public ServletFilterConfiguration(ServletConfig scfg){
        this.scfg = scfg;
        ctxt = scfg.getServletContext();
    }

    public ServletFilterConfiguration(FilterConfig fcfg){
        this.fcfg = fcfg;
        ctxt = fcfg.getServletContext();
    }

    public ServletContext getServletContext(){
        return ctxt;
    }

    public boolean getBooleanParameter(String name, boolean def){
        String param = scfg == null ? 
            fcfg.getInitParameter(name) : scfg.getInitParameter(name);
        if(param == null)
            param = ctxt.getInitParameter(name);
        if(param == null)
            return def;
        return param.equalsIgnoreCase("yes") || param.equalsIgnoreCase("true");
    }

    public String getInitParameter(String name){
        return getInitParameter(name, null);
    }

    public String getInitParameter(String name, String def){
        String param = scfg == null ? 
            fcfg.getInitParameter(name) : scfg.getInitParameter(name);
        if(param == null)
            param = ctxt.getInitParameter(name);
        if(param == null)
            return def;
        return param;
    }

}
