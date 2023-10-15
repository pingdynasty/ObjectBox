package org.oXML.xpath.function;

import java.util.List;
import org.oXML.type.Node;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.NamespaceException;
import org.oXML.xpath.parser.QName;
import org.oXML.type.Node;
import org.oXML.type.Function;
import org.oXML.type.Type;
import org.oXML.type.Name;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

public class TypeFunctionCall extends FunctionCall {
    private Expression object;

    public TypeFunctionCall(Expression object, QName qname, List args) {
        super(qname, args);
        this.object = object;
    }

    public void bind(Resolver ctxt)
        throws ObjectBoxException {
	super.bind(ctxt);
	object.bind(ctxt);
    }

    public Node evaluate(RuntimeContext ctxt)
        throws ObjectBoxException {
        Node instance = object.evaluate(ctxt);
        Node[] params = new Node[args.length];
        Type[] ptypes = new Type[args.length];
        for(int i=0; i<params.length; ++i){
            params[i] = args[i].evaluate(ctxt);
            ptypes[i] = params[i].getType();
        }
        if(instance == null)
            throw new FunctionException("cannot invoke type function "+printFunction(name, params)+" on null node");
        Type type = instance.getType();

        Function func = type.getFunction(name, ptypes);
        if(func == null)
            throw new FunctionException("no matching function: "+
				     printFunction(instance, name, params));
	Type decl = func.getDeclaringType();
	if(decl == null)
            throw new FunctionException("function "+printFunction(name, params)+" has no declaring type");
// 	// get the actual node instance (may be a parent instance)
// 	instance = instance.getInstance(decl.getName());
// 	if(instance == null)
//             throw new XPathException("function "+printFunction(name, params)+" called on an instance that has no parent of type: "+decl.getName());

	StackFrame frame = new StackFrame(func.print(), location);
	ctxt.pushStackFrame(frame);
	try{
	    return func.invoke(instance, params, ctxt);
	}catch(FunctionException exc){
	    exc.addStackFrame(frame);
	    throw exc;
	}catch(Throwable exc){
	    throw new FunctionException(frame, exc);
	}finally{
	    ctxt.popStackFrame();
	}
    }

    protected static String printFunction(Node instance, Name name, Node[] parts){
	return instance.getType().getName()+"."+printFunction(name, parts);
    }

    public String toString()
    {
        return super.toString()+"["+object+"]";
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
	atts.addAttribute("", "name", "name", "CDATA", qname.toString());
	handler.startElement("", "call", "call", atts);
	handler.startElement("", "target", "target", atts);
	object.write(handler);
	handler.endElement("", "target", "target");
        for(int i=0; i<args.length; ++i){
	    atts = new org.xml.sax.helpers.AttributesImpl();    
	    handler.startElement("", "param", "param", atts);
            args[i].write(handler);
	    handler.endElement("", "param", "param");
	}
	handler.endElement("", "call", "o:call");
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
