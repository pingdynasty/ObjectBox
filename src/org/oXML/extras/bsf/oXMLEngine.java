package org.oXML.extras.bsf;

import java.io.Writer;
import java.io.StringWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.oXML.ObjectBoxException;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.type.StringNode;
import org.oXML.type.NumberNode;
import org.oXML.type.BooleanNode;
import org.oXML.type.Variable;
import org.oXML.engine.ResultHandler;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.InterpretedProgram;
import org.oXML.engine.StreamResultHandler;
import org.apache.bsf.*;
import org.apache.bsf.util.BSFEngineImpl;
import org.apache.bsf.util.BSFFunctions;
import org.apache.bsf.debug.util.DebugLog;
import org.oXML.util.Log;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class oXMLEngine extends BSFEngineImpl {

    private DocumentBuilder builder;

    /**
     * Initialize the engine.
     */
    public void initialize(BSFManager mgr, String lang,
			   Vector declaredBeans) throws BSFException {
	super.initialize (mgr, lang, declaredBeans);

	// create an XML document builder
	try{
	    DocumentBuilderFactory factory = 
		DocumentBuilderFactory.newInstance();
	    factory.setNamespaceAware(true);
	    builder = factory.newDocumentBuilder();
	}catch(ParserConfigurationException exc){
	    throw new BSFException(BSFException.REASON_OTHER_ERROR, 
				   "error instantiating XML parser", exc);
	}
    }

    /**
     * Declare a bean
     */
    public void declareBean(BSFDeclaredBean bean) throws BSFException {
 	declaredBeans.add(bean);
    }

    /**
     * Undeclare a bean
     */
    public void undeclareBean(BSFDeclaredBean bean) throws BSFException {
	declaredBeans.remove(bean);
    }

    protected Variable createParameter(InterpretedProgram program, BSFDeclaredBean bean)
	throws BSFException {
	Name name = new Name(bean.name);
	Node value;
	if(bean.bean instanceof Number){
	    value = new NumberNode((Number)bean.bean);
	}else if(bean.bean instanceof Boolean){
	    value = BooleanNode.booleanNode((Boolean)bean.bean);
	}else{
	    value = new StringNode(bean.bean.toString());
	}
	Log.trace(lang+" setting parameter: "+name+"="+value);
	try{
	    return program.createParameter(name, value);
	}catch(ObjectBoxException exc){
	    throw new BSFException(BSFException.REASON_EXECUTION_ERROR, 
				   "error setting parameter: "+name, exc);
	}
    }

    /**
     * call the named method of the given object.
     */
    public Object call(Object object, String method, Object[] args) 
        throws BSFException {
	throw new BSFException (BSFException.REASON_UNSUPPORTED_FEATURE,
                                "BSF "+lang+" can't call methods");
    }

    protected InterpretedProgram readInterpretedProgram(String systemId, String script)
	throws BSFException {

	InterpretedProgram program;
	Reader reader = new StringReader(script);
	try{
	    InputSource in = new InputSource(reader);
	    if(systemId != null)
		in.setSystemId(systemId);
	    Document xml = builder.parse(in);
	    program = new InterpretedProgram(systemId, xml);
	}catch(IOException exc){
	    throw new BSFException(BSFException.REASON_IO_ERROR, 
				   "error parsing "+lang+" program", exc);
	}catch(SAXException exc){
	    throw new BSFException(BSFException.REASON_IO_ERROR, 
				   "error parsing "+lang+" program", exc);
	}
	return program;
    }

    /**
     * Evaluate an expression. In this case, an expression is assumed
     * to be a stylesheet of the template style (see the XSLT spec).
     */
    public Object eval(String source, int lineNo, int columnNo, 
		       Object oscript) throws BSFException {

	Object beano = mgr.lookupBean ("o:baseURI");
	String systemId = (beano == null) ? null : beano.toString();

	InterpretedProgram program = readInterpretedProgram(systemId, oscript.toString());

	Writer writer = new StringWriter();
	ResultHandler result = new StreamResultHandler(writer);

        // create runtime context
        RuntimeContext env = new RuntimeContext(program);
        env.addResultHandler(result);

	// set all declared beans as variables.
	for (int i = 0; i < declaredBeans.size(); i++){
            BSFDeclaredBean bean = (BSFDeclaredBean)declaredBeans.elementAt(i);
            env.setVariable(createParameter(program, bean));
	}

	try{
	    Log.trace(lang+" compiling...");
	    program.compile();
	}catch(IOException exc){
	    throw new BSFException(BSFException.REASON_IO_ERROR, 
				   "error compiling "+lang+" program", exc);
	}catch(SAXException exc){
	    throw new BSFException(BSFException.REASON_IO_ERROR, 
				   "error compiling "+lang+" program", exc);
	}catch(ObjectBoxException exc){
	    throw new BSFException(BSFException.REASON_EXECUTION_ERROR, 
				   "error compiling "+lang+" program", exc);
	}
	try{
	    Log.trace(lang+" executing...");
	    program.run(env);
	}catch(ObjectBoxException exc){
	    throw new BSFException(BSFException.REASON_EXECUTION_ERROR, 
				   "error executing "+lang+" program", exc);
	}
	Log.trace(lang+" done!");
	return writer.toString();
    }
}
/*
    ObjectBox - o:XML compiler and interpretor
    for more information see http://www.o-xml.org/objectbox
    Copyright (C) 2003 Martin Klang, Alpha Plus Technology Ltd
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
