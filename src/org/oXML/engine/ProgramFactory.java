package org.oXML.engine;

import java.io.Writer;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.oXML.type.*;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;
import com.pingdynasty.sml.sax.SMLReader;
import com.pingdynasty.sml.dom.SMLDocumentBuilder;
import com.pingdynasty.sml.ObjectBoxMacroHandler;

// import org.apache.xerces.util.XMLCatalogResolver;

public class ProgramFactory {

    public static final String UNIT_TEST_XSLT = "unit-test.xsl";
    public static final String HTML_DOCUMENT_XSLT = "doc2html.xsl";
    public static final String DOCBOOK_DOCUMENT_XSLT = "doc2docbook.xsl";

    private ClassLoader loader;
    private List extensions;
    private TransformerFactory factory;

    public ProgramFactory(ClassLoader loader){
        this.loader = loader;
        factory = TransformerFactory.newInstance();
	extensions = new ArrayList();
    }

    public ProgramFactory(){
        this(null);
    }

// String [] catalogs =  
//   {"file:///C:/catalog/cat1.xml", "file:///C:/catalog/cat2.xml"};
// // Create catalog resolver and set a catalog list.
// XMLCatalogResolver resolver = new XMLCatalogResolver();
// resolver.setPreferPublic(true);
// resolver.setCatalogList(catalogs);
// // Set the resolver on the parser.
// reader.setProperty("http://apache.org/xml/properties/internal/entity-resolver", resolver);

    public URL getResourceURL(String id)
	throws ObjectBoxException {
	try{
            URL url;
            if(loader == null){
                // try for system resource
                url = ClassLoader.getSystemResource(id);
                if(url == null){
                    // not in system classpath, try context classloader
                    ClassLoader loader = 
                        Thread.currentThread().getContextClassLoader();
                    url = loader.getSystemResource(id);
                    if(url == null)
                        url = loader.getResource(id);
                }
                if(url == null)
                    url = ClassLoader.getSystemClassLoader().getResource(id);
            }else{
                url = loader.getSystemResource(id);
                if(url == null)
                    url = loader.getResource(id);
            }
	    return url;
	}catch(Exception exc){
	    throw new ObjectBoxException(exc);
	}
    }

    public void loadExtension(String className)
	throws ObjectBoxException {
	try{
	    Class klass = Class.forName(className);
	    LanguageExtension ext = (LanguageExtension)klass.newInstance();
            if(!extensions.contains(ext))
                extensions.add(ext);
	}catch(Exception exc){
            throw new ObjectBoxException("loading extension "+className, exc);
	}
    }

    public void loadDefaultExtensions()
        throws ObjectBoxException{
        // load default extensions
        loadExtension("org.oXML.extras.java.JavaExtensions");
        loadExtension("org.oXML.extras.db.DatabaseExtensions");
        loadExtension("org.oXML.extras.xinclude.XIncludeExtensions");
    }

    protected String getLocation(String systemId){
	try{
	    systemId = new java.net.URL(systemId).toString();
	}catch(MalformedURLException exc){
	    try{
		systemId = new File(systemId).toURL().toString();
	    }catch(MalformedURLException nexc){
		Log.warning(nexc.getMessage());
	    }
	}
        return systemId;
    }

    public InterpretedProgram getProgram(String systemId)
        throws ObjectBoxException, IOException, TransformerException, SAXException {
        systemId = getLocation(systemId);
        return new InterpretedProgram(systemId);
    }

    public InterpretedProgram getSMLProgram(String systemId)
        throws ObjectBoxException, IOException, TransformerException, SAXException {
        SMLDocumentBuilder parser = new SMLDocumentBuilder();
        parser.setMacroHandler(new ObjectBoxMacroHandler());
        Document doc = parser.parse(systemId);
        return new InterpretedProgram(systemId, doc);
    }

    public InterpretedProgram getTestProgram(String systemId)
        throws ObjectBoxException, IOException, TransformerException, SAXException {
        systemId = getLocation(systemId);
        DOMResult result = new DOMResult();
        URL url = getResourceURL(UNIT_TEST_XSLT);
        if(url == null)
            throw new IOException("missing or invalid resource: "+UNIT_TEST_XSLT);
        Source xslt = new StreamSource(url.toString());
        Transformer transformer = factory.newTransformer(xslt);
        transformer.setParameter("file", systemId);
        Source source = new StreamSource(systemId);
        transformer.transform(source, result);
        Document doc = result.getNode().getOwnerDocument();
        if(doc == null)
            doc = (Document)result.getNode();
        systemId += ".unit-test";
        return new InterpretedProgram(systemId, doc);
    }

    public InterpretedProgram getTestSMLProgram(String systemId)
        throws ObjectBoxException, IOException, TransformerException, SAXException {
        systemId = getLocation(systemId);
        InputSource is = new InputSource(systemId);
        SMLReader parser = new SMLReader();
        parser.setMacroHandler(new ObjectBoxMacroHandler());
        Source source = new SAXSource(parser, is);
        DOMResult result = new DOMResult();
        URL url = getResourceURL(UNIT_TEST_XSLT);
        if(url == null)
            throw new IOException("missing or invalid resource: "+UNIT_TEST_XSLT);
        Source xslt = new StreamSource(url.toString());
        Transformer transformer = factory.newTransformer(xslt);
        transformer.setParameter("file", systemId);
        transformer.transform(source, result);
        Document doc = result.getNode().getOwnerDocument();
        if(doc == null)
            doc = (Document)result.getNode();
        systemId += ".unit-test";
        return new InterpretedProgram(systemId, doc);
    }

    /**
     * load any configured language extensions then compile program.
     */
    public void compile(InterpretedProgram program)
        throws IOException, SAXException, ObjectBoxException {
        // set the mappings from any configured Language Extensions
	for(int i=0; i<extensions.size(); ++i){
	    LanguageExtension extension = (LanguageExtension)extensions.get(i);
	    Log.trace("adding language extension: "+extension);
	    program.addExtension(extension);
	}
        Log.trace("compiling...");
        program.compile();
        Log.trace("compilation done!");
    }

    public void compile(String filename)
        throws IOException, SAXException, ObjectBoxException, TransformerException {
        InterpretedProgram program = getProgram(filename);
        compile(program);
    }
}
/*
    ObjectBox - o:XML compiler and interpretor
    for more information see http://www.o-xml.org/objectbox
    Copyright (C) 2002-2006 Martin Klang, Alpha Plus Technology Ltd
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
