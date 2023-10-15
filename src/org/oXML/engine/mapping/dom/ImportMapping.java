package org.oXML.engine.mapping.dom;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.oXML.engine.InterpretedProgram;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.NullTemplate;
import org.oXML.engine.template.ImportTemplate;
import org.oXML.engine.CompilationContext;
import org.oXML.ObjectBoxException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.oXML.util.Log;
import org.oXML.xpath.Expression;

public class ImportMapping implements TemplateMapping{

    public Template map(Element e, CompilationContext env)
        throws ObjectBoxException{

        String expr = e.getAttribute("href");
        if(expr.equals(""))
            throw new MappingException
                (e, "missing required attribute: href");

	URL url = null;
	Template imported;
	try{
	    try{
		// try absolute URL
		url = new URL(expr);
	    }catch(MalformedURLException exc){
		// try relative (of program system id) URL
		url = new URL(env.getSystemId());
		url = new URL(url, expr);
	    }
	    imported = env.importURL(url);
	}catch(ObjectBoxException exc){
	    Log.error("importing: "+url);
	    throw exc;
	}catch(Exception exc){
	    // try for system resource
	    url = ClassLoader.getSystemResource(expr);
	    if(url == null){
		// not in system classpath, try context classloader
		ClassLoader loader = 
		    Thread.currentThread().getContextClassLoader();
		url = loader.getResource(expr);
	    }
	    if(url == null)
		throw new MappingException
		    (e, "missing or invalid imported file: "+expr, exc);
	    try{
		imported = env.importURL(url);
	    }catch(ObjectBoxException nexc){
		Log.error("importing: "+url);
		throw nexc;
	    }catch(SAXException nexc){
		throw new MappingException
		    (e, "missing or invalid imported file: "+expr, nexc);
	    }catch(IOException nexc){
		throw new MappingException
		    (e, "missing or invalid imported file: "+expr, nexc);
	    }
	}

	if(imported != null)
	    return new ImportTemplate(url.toString(), imported);
	return new NullTemplate();
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
