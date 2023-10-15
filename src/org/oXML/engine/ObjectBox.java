package org.oXML.engine;

import java.io.Writer;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import javax.xml.transform.Source;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.oXML.type.*;
import org.oXML.util.Log;
import org.oXML.ObjectBoxException;

public class ObjectBox {

    public static final Version version = new Version();

    public static final Name SESSION_NAME = 
	new Name(CompilationContext.OXML_NS, "session");

    public static final String TEST_REPORT_XSLT = "test-report.xsl";
    public static final String HTML_DOCUMENT_XSLT = "doc2html.xsl";
    public static final String DOCBOOK_DOCUMENT_XSLT = "doc2docbook.xsl";

    private static final int RUN_MODE = 1;
    private static final int UNIT_TEST_MODE = 2;
    private static final int HTML_DOCS_MODE = 3;
    private static final int XML_DOCS_MODE = 4;
    private static final int COMPILATION_MODE = 5;

    private Map vars;
    private String filename;
    private String encoding; // DOM Level 3: Document.getInputEncoding() or .getXmlEncoding()
    private boolean xsl = false;
    private boolean xml = false;
    private boolean sml = false;
    private int mode = RUN_MODE;
    private TransformerFactory xslfactory;
    private ProgramFactory programfactory;
    private MapNode session;
    private PrintStream out = System.out;

    public ObjectBox(String[] args)
	throws Exception{
	vars = new HashMap();
	filename = null;
	boolean defaultExtensions = true; // default is to load a set of extensions
        programfactory = new ProgramFactory();
	for(int i=0; i<args.length; ++i){
	    if(args[i].equals("-e") && i+1 < args.length){
		String className = args[++i];
		programfactory.loadExtension(className);
	    } else if(args[i].equals("-noext")){
		defaultExtensions = false;
	    } else if(args[i].equals("-version")){
		version();
		System.exit(0);
	    } else if(args[i].equals("-h") 
		      || args[i].equals("-help")){
		usage();
		System.exit(0);
	    } else if(args[i].equals("-xsl")){
		xsl = true;
	    } else if(args[i].equals("-sml")){
		sml = true;
	    } else if(args[i].equals("-xml")){
		xml = true;
	    } else if(args[i].equals("-test")){
		mode = UNIT_TEST_MODE;
	    } else if(args[i].equals("-xmldocs")){
		mode = XML_DOCS_MODE;
	    } else if(args[i].equals("-htmldocs")){
		mode = HTML_DOCS_MODE;
	    } else if(args[i].equals("-compile")){
		mode = COMPILATION_MODE;
	    } else if(args[i].equals("-v")){
		Log.setLevel(Log.DEBUG_LEVEL);
	    } else if(args[i].equals("-q")){
		Log.setLevel(Log.ERROR_LEVEL);
	    } else if(args[i].equals("-encoding")){
		if(++i < args.length){
		    encoding = args[i];
		}else{
		    usage();
		    System.exit(-1);
		}
	    } else if(args[i].startsWith("-D")){
		// set a parameter
		int pos = args[i].indexOf('=');
		if(pos < 0){
		    Name name = Name.makeName(args[i].substring(2));
                    vars.put(name, BooleanNode.booleanNode(true));
		}else{
                    Name name = Name.makeName(args[i].substring(2, pos));
                    vars.put(name, args[i].substring(pos+1));
		}
	    } else if(args[i].equals("-S")){
		// create empty session
		session = new MapNode();
		vars.put(SESSION_NAME, session);
	    } else if(args[i].startsWith("-S")){
		// create a session if not already done
		if(session == null){
		    session = new MapNode();
		    vars.put(SESSION_NAME, session);
		}
		// set a session value
		Node name;
		Node value;
		int pos = args[i].indexOf('=');
		if(pos < 0){
		    name = new NameNode(Name.makeName(args[i].substring(2)));
		    value = BooleanNode.booleanNode(true);
		}else{
		    name = new NameNode(Name.makeName(args[i].substring(2, pos)));
		    value = new StringNode(args[i].substring(pos+1));
		}
		session.put(name, value);
	    }else if(filename == null){
		filename = args[i];
                if(!xml && filename.endsWith(".sml"))
                    sml = true;
	    }else{
		usage();
		System.exit(-1); // todo: get rid of exit() calls, at least not in ctor
	    }
	}
	if(defaultExtensions)
            programfactory.loadDefaultExtensions();
        xslfactory = TransformerFactory.newInstance();
    }

    public void version(){
	System.err.println(version.version());
    }

    public void usage(){
	version();
	System.err.println("usage: java "+getClass().getName()
			   +" [options...] file");
	System.err.println("options:");
	System.err.println("\t-Dname\t\t\tset program parameter 'name' to true");
	System.err.println("\t-Dname=value\t\tset program parameter 'name' to 'value'");
	System.err.println("\t-Sname\t\t\tset session parameter 'name' to true");
	System.err.println("\t-Sname=value\t\tset session parameter 'name' to 'value'");
	System.err.println("\t-S\t\t\tcreate an empty program session");
	System.err.println("\t-encoding charset\tset output character encoding to 'charset'");
	System.err.println("\t-noext \t\t\tdo not load default language extensions");
	System.err.println("\t-e classname\t\tuse language extension 'classname'");
	System.err.println("\t-xsl\t\t\tpost-process with XSLT engine");
	System.err.println("\t-xml\t\t\texpect XML input");
	System.err.println("\t-sml\t\t\texpect SML input");
	System.err.println("\t-test\t\t\trun unit tests");
	System.err.println("\t-compile\t\tcompile program without executing");
	System.err.println("\t-xmldocs\t\tgenerate DocBook XML documentation");
	System.err.println("\t-htmldocs\t\tgenerate HTML documentation");
	System.err.println("\t-v\t\t\tbe verbose");
	System.err.println("\t-q\t\t\tbe quiet");
	System.err.println("\t-help (or -h)\t\tprint instructions and exit");
	System.err.println("\t-version\t\tprint version details and exit");
        // todo: new options:
//         -validate (perform XML validation)
//         -lint (XML validation plus compilation)
//         -o outputfile
    }

    public Node run(InterpretedProgram program, ResultHandler handler)
        throws ObjectBoxException, IOException, SAXException {
        RuntimeContext env = new RuntimeContext(program);
        env.addResultHandler(handler);
        // set all defined program parameters
        Iterator it = vars.keySet().iterator();
        while(it.hasNext()){
            Name name = (Name)it.next();
            Object value = vars.get(name);
            Variable var;
            if(value instanceof Node){
                var = program.createParameter(name, (Node)value);
            }else{
                var = program.createParameter(name, value.toString());
            }
            Log.trace("setting parameter "+var.getName());
            env.setVariable(var);
        }
        Log.trace("executing...");
        Node value = program.run(env);
        Log.trace("execution done!");
        return value;
    }

    public void transform(Source source, Source style)
        throws IOException, TransformerException, SAXException {
        StreamResult target = new StreamResult(out);
        Transformer xslt;
        if(style == null){
            Log.warning("no associated stylesheet");
            xslt = xslfactory.newTransformer();
        }else{
            xslt = xslfactory.newTransformer(style);
        }
        if(encoding != null)
            xslt.setOutputProperty(OutputKeys.ENCODING, encoding);
        xslt.transform(source, target);
    }

    public void run()
        throws ObjectBoxException, IOException, TransformerException, SAXException{
	if(filename == null){
	    usage();
            return;
        }
        switch(mode){
        case UNIT_TEST_MODE:
            test(filename);
            break;
        case HTML_DOCS_MODE:
            htmldocs(filename);
            break;
        case XML_DOCS_MODE:
            xmldocs(filename);
            break;
        case COMPILATION_MODE:
            compile(filename);
            break;
        case RUN_MODE:
        default:
            run(filename);
        }
    }

    public void compile(String filename)
        throws IOException, SAXException, ObjectBoxException, TransformerException {
        InterpretedProgram program;
        if(sml)
            program = programfactory.getSMLProgram(filename);
        else
            program = programfactory.getProgram(filename);
        programfactory.compile(program);
    }

    public void run(String filename)
        throws ObjectBoxException, IOException, TransformerException, SAXException{
        InterpretedProgram program;
        if(sml)
            program = programfactory.getSMLProgram(filename);
        else
            program = programfactory.getProgram(filename);
        programfactory.compile(program);
        ResultHandler handler;
	if(xsl)
	    handler = new DOMResultHandler();
	else if(program.getContentType().equals(Program.NO_CONTENT_TYPE))
	    handler = new NullResultHandler();
	else if(encoding == null)
	    handler = new StreamResultHandler(out);
	else
	    handler = new StreamResultHandler(out, encoding);
        Node value = run(program, handler);
	if(value != null){
	    out.println(value.stringValue());
	}else if(xsl){
            Source source = new DOMSource((org.w3c.dom.Node)handler.getResult());
	    source.setSystemId(filename);
	    Source style = null;
            try{
                style = xslfactory.getAssociatedStylesheet(source, null, null, encoding);
            }catch(TransformerConfigurationException exc){
                // Saxon throws exception if there's no associated 
                // stylesheet, while Xalan returns null instead
            }
            transform(source, style);
	}else if(handler.getResult() != null && handler.getResult() instanceof Writer){
	    Writer writer = (Writer)handler.getResult();
	    writer.flush();
	}
    }

    public void test(String filename)
        throws ObjectBoxException, IOException, TransformerException, SAXException{
        InterpretedProgram program;
        if(sml)
            program = programfactory.getTestSMLProgram(filename);
        else
            program = programfactory.getTestProgram(filename);
        programfactory.compile(program);
        ResultHandler handler;
	if(xsl)
	    handler = new DOMResultHandler();
	else if(encoding == null)
	    handler = new StreamResultHandler(out);
	else
	    handler = new StreamResultHandler(out, encoding);
        // run test
        run(program, handler);
	if(xsl){
            // generate HTML results
            Source source = new DOMSource((org.w3c.dom.Node)handler.getResult());
            source.setSystemId(filename);
            URL xsl = ClassLoader.getSystemResource(TEST_REPORT_XSLT);
            Source style = new StreamSource(xsl.toString());
            transform(source, style);
        }else{
            if(handler.getResult() != null && handler.getResult() instanceof Writer){
                Writer writer = (Writer)handler.getResult();
                writer.flush();
            }
        }
    }

    public void htmldocs(String filename)
        throws IOException, TransformerException, SAXException{
        Source source = new StreamSource(filename);
        URL xsl = ClassLoader.getSystemResource(HTML_DOCUMENT_XSLT);
        Source style = new StreamSource(xsl.toString());
        transform(source, style);
    }

    public void xmldocs(String filename)
        throws IOException, TransformerException, SAXException{
        Source source = new StreamSource(filename);
        URL xsl = ClassLoader.getSystemResource(DOCBOOK_DOCUMENT_XSLT);
        Source style = new StreamSource(xsl.toString());
        transform(source, style);
    }

    public static void main(String args[]) {
        try{
	    ObjectBox obox = new ObjectBox(args);
            obox.run();
        }catch(ObjectBoxException exc){
            Log.exception(exc);
        }catch(Exception exc){
            Log.exception(exc);
        }
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
