package org.oXML.type;

import java.util.Map;
import java.util.HashMap;
import java.io.Writer;
import java.io.OutputStream;
import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.ResultHandler;
import org.oXML.engine.StreamResultHandler;

public abstract class Program {
    private String systemId;
    private TypeResolver resolver;
    public static final String DEFAULT_CONTENT_TYPE = "text/xml";
    public static final String NO_CONTENT_TYPE = "none";
    public static final String DEFAULT_ENCODING = "utf-8";

    public Program(String systemId){
	this.systemId = systemId;
        if(systemId == null)
            systemId = "unknown source";
	resolver = new TypeResolver();
    }

    public TypeResolver getResolver(){
	return resolver;
    }

    public void addModule(Module module)
	throws ObjectBoxException {
	resolver.addModule(module);
    }

    public void addFunction(Function function)
	throws ObjectBoxException {
	resolver.addFunction(function);
    }

    public void addType(Type type)
	throws ObjectBoxException {
	resolver.addType(type);
    }

    public Type getType(Name name)
	throws ObjectBoxException {
	return resolver.getType(name);
    }

    public boolean hasType(Name name){
        return resolver.hasType(name);
    }

    public String getSystemId(){
	return systemId;
    }

    public void setSystemId(String systemId){
	this.systemId = systemId;
    }

    public String getContentType(){
	return DEFAULT_CONTENT_TYPE;
    }

    public String getEncoding(){
	return DEFAULT_ENCODING;
    }

//     public Functions getFunctions(){
// 	return functions;
//     }

//     public Map getTypes(){
// 	return types;
//     }

//     public Name[] getParameterNames()
// 	throws ObjectBoxException{
// 	Name[] names = new Name[params.size()];
	
//     }

//     public void setParameter(Name name, Node value)
// 	throws ObjectBoxException{
// 	if(program == null)
// 	    throw new ObjectBoxException("program not compiled yet");
// 	if(p != null)
// 	    p.setParameter(name, value);
//     }

//     public Node getParameter(Name name)
// 	throws ObjectBoxException{
// 	if(program == null)
// 	    throw new ObjectBoxException("program not compiled yet");
// 	if(p != null)
// 	    return p.getParameter(name);
// 	return null;
//     }

    public Node run()
        throws ObjectBoxException{
	return run(System.out);
    }

    public Node run(OutputStream out)
        throws ObjectBoxException{
	return run(new StreamResultHandler(out));
    }

    public Node run(ResultHandler handler)
        throws ObjectBoxException{
        RuntimeContext env = new RuntimeContext(this);
        env.addResultHandler(handler);
// 	handler.start(getContentType());
	Node result = run(env);
// 	handler.end();
	return result;
    }

    public abstract Node run(RuntimeContext rc)
        throws ObjectBoxException;
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
