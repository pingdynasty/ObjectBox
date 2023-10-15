package org.oXML.engine;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.oXML.ObjectBoxException;
import org.oXML.type.*;
import org.oXML.engine.mapping.dom.TemplateMapping;
import org.oXML.engine.template.Template;
import org.oXML.engine.template.Parameter;
import org.oXML.engine.template.ProgramTemplate;
import org.oXML.type.Function;
import org.oXML.util.Log;

public class InterpretedProgram extends Program{
    private String encoding;
    private Document xml;
    private Template program;
    private Map mappings;
//     private List functions;
    private ProgramTemplate p;

    public InterpretedProgram(InputSource in)
        throws IOException, SAXException {
	super(in.getSystemId());
	encoding = in.getEncoding();
	if(encoding == null)
	    encoding = DEFAULT_ENCODING;
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setNamespaceAware(true);
	try {
	    xml = factory.newDocumentBuilder().parse(in);
	}catch(ParserConfigurationException exc){
	    throw new SAXException(exc);
	}
	// field initalisation
        mappings = new HashMap();
    }

    /**
     * constructor that reads a program from file
     * @param filename identifier of the xml file
     */
    public InterpretedProgram(String filename)
        throws IOException, SAXException {
        this(new InputSource(filename));
    }

    public InterpretedProgram(String systemId, Document xml){
        super(systemId);
        this.xml = xml;
	// field initalisation
        mappings = new HashMap();
    }

    public void loadExtension(String className)
	throws ObjectBoxException {
	try{
	    Class klass = Class.forName(className);
	    LanguageExtension ext = (LanguageExtension)klass.newInstance();
	    addExtension(ext);
	}catch(ObjectBoxException exc){
	    throw exc;
	}catch(Exception exc){
	    Log.exception(exc, "loading extension: "+className);
	}
    }

    public void addExtension(LanguageExtension extension)
	throws ObjectBoxException {
	// add mappings
	Map mappings = extension.getMappings();
	Iterator it = mappings.keySet().iterator();
	while(it.hasNext()){
	    Name name = (Name)it.next();
	    TemplateMapping mapping = (TemplateMapping)mappings.get(name);
	    setMapping(name, mapping);
	}

	// add functions
	it = extension.getFunctions().iterator();
	while(it.hasNext()){
	    Function fun = (Function)it.next();
	    addFunction(fun);
	}
    }

//     public void addFunction(Function function){
//         functions.add(function);
//     }

    public Name[] getParameterNames()
	throws ObjectBoxException{
	if(program == null)
	    throw new ObjectBoxException("program not compiled yet");
	if(p == null)
	    return new Name[0];
	return p.getParameterNames();
    }

    public Parameter getParameter(Name name)
	throws ObjectBoxException{
        Parameter[] params = p.getParameters();
        for(int i=0; i<params.length; i++){
	    if(params[i].getName().equals(name))
                return params[i];
        }
        return null;
    }

    public Variable createParameter(Name name, Node value)
	throws ObjectBoxException{
        Parameter param = getParameter(name);
        if(param == null)
            throw new ObjectBoxException("no such parameter: "+name);
        if(param.getType() != Node.TYPE && !value.getType().instanceOf(param.getType()))
            throw new ObjectBoxException
                ("invalid parameter type for program parameter: "+name
                 +" - should be "+param.getType().getName()+
                 " not "+value.getType().getName());
        return new Variable(name, value);
    }

    public Variable createParameter(Name name, String str)
	throws ObjectBoxException {
        Parameter param = getParameter(name);
        if(param == null)
            throw new ObjectBoxException("no such parameter: "+name);
        Node value;
        if(StringNode.TYPE.instanceOf(param.getType()))
            value = new StringNode(str);
        else if(NumberNode.TYPE.instanceOf(param.getType()))
            try{
                value = new NumberNode(Double.parseDouble(str));
            }catch(Exception exc){ // NumberFormatException or NullPointerException
                value = new NumberNode(Double.NaN);
            }
        else if(BooleanNode.TYPE.instanceOf(param.getType()))
            value = BooleanNode.booleanNode(str != null && !str.equals("") && 
                                            !str.equalsIgnoreCase("false"));
        else
            throw new ObjectBoxException
                ("incompatible types - cannot set program parameter "+name
                 +" of type "+param.getType().getName());
        // todo: get constructor for type and call ctor(String)
        return new Variable(name, value);
    }

    public String getContentType(){
	return p == null ? DEFAULT_CONTENT_TYPE : p.getContentType();
    }

    public String getEncoding(){
	return encoding;
    }

    public void setMapping(Name name, TemplateMapping mapping){
        mappings.put(name, mapping);
    }

    public void compile()
        throws IOException, SAXException, ObjectBoxException{
        CompilationContext env = new CompilationContext(this);
        env.init();
	program = compile(env);
        xml = null;
    }

    public Template compile(CompilationContext env)
        throws IOException, SAXException, ObjectBoxException{
	assert program == null;
	assert xml != null;
        // set any node mappings we may know about
        Iterator it = mappings.keySet().iterator();
        while(it.hasNext()){
            Name key = (Name)it.next();
            TemplateMapping value = (TemplateMapping)mappings.get(key);
            env.setMapping(key, value);
        }
	try{
	    Template body = env.compile(xml);
	    p = env.getProgramTemplate();
	    return body;
	}catch(ObjectBoxException exc){
	    Log.error("in program: "+getSystemId());
	    throw exc;
	}
    }

    /**
     * @return true if this program has been compiled, false otherwise
     */
    public boolean compiled(){
        return program != null;
    }

    public Node run(RuntimeContext env)
        throws ObjectBoxException{

        if(program == null)
            throw new ObjectBoxException("program has not been compiled");

//         // add any extra functions we know about
//         it = functions.iterator();
//         while(it.hasNext()){
//             Function fun = (Function)it.next();
//             env.addFunction(fun);
//         }

        // run the program
	try{
	    env.start(getContentType());
	    program.process(env);
	    env.end();
	}catch(ReturnException exc){
	    Log.trace("got return value: "+exc.getResult().getType().getName());
	    return exc.getResult();
	}catch(ObjectBoxException exc){
	    Log.error("in program: "+getSystemId());
	    throw exc;
	}catch(RuntimeException exc){
	    Log.error("in program: "+getSystemId());
	    throw exc;
	}
	return null;
    }

    public String toString(){
        return getClass().getName()+'<'+program+'>';
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
