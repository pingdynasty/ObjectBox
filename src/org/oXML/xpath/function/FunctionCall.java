package org.oXML.xpath.function;

import java.util.List;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.oXML.xpath.SourceLocator;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.parser.QName;
import org.oXML.type.Name;
import org.oXML.type.Node;
import org.oXML.type.Function;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

public class FunctionCall implements Expression {
    protected QName qname;
    protected Name name;
    protected Expression[] args;
    protected SourceLocator location;

    public FunctionCall(QName qname, List args){
        this.qname = qname;
        this.args = new Expression[args.size()];
        args.toArray(this.args);
    }

     public FunctionCall(QName qname, Expression[] args){
         this.qname = qname;
         this.args = args;
     }

    public void bind(Resolver resolver)
        throws ObjectBoxException {
	name = resolver.getName(qname);
	qname = null;
        for(int i=0; i<args.length; ++i)
            args[i].bind(resolver);
	location = resolver.getLocation();
    }

    public Node evaluate(RuntimeContext ctxt)
        throws ObjectBoxException {
        Node[] parts = new Node[args.length];
        for(int i=0; i<parts.length; ++i)
            parts[i] = args[i].evaluate(ctxt);

        Function func = ctxt.getFunction(name, parts);


        if(func == null)
            throw new FunctionException("no matching function: "+printFunction(name, parts));

	StackFrame frame = new StackFrame(func.print(), location);
	ctxt.pushStackFrame(frame);
	try{
	    return func.invoke(parts, ctxt);
	}catch(FunctionException exc){
	    exc.addStackFrame(frame);
	    throw exc;
	}catch(Throwable exc){
	    throw new FunctionException(frame, exc);
	}finally{
	    ctxt.popStackFrame();
	}
    }

    protected static String printFunction(Name name, Node[] parts){
	StringBuffer buf = new StringBuffer(name.toString());
	buf.append('(');
	if(parts.length != 0){
	    buf.append(parts[0].getType().getName());
	    for(int i=1; i<parts.length; ++i){
		buf.append(", ");
		buf.append(parts[i].getType().getName());
	    }
	}
	buf.append(')');
	return buf.toString();
    }

    public String print(){
        StringBuffer buf = new StringBuffer();
        if(name != null)
            buf.append(name.print());
        else
            buf.append(qname);
        buf.append('(');
        for(int i=0; i<args.length; ++i)
            buf.append(args[i]);
        buf.append(')');
        return buf.toString();
    }

    public String toString(){
        return getClass().getName()+"["+qname+","+args+"]";
    }

    public void write(org.xml.sax.ContentHandler handler)
	throws org.xml.sax.SAXException {
	org.xml.sax.helpers.AttributesImpl atts = 
	    new org.xml.sax.helpers.AttributesImpl();
	atts.addAttribute("", "name", "name", "CDATA", qname.toString());
	handler.startElement("", "call", "call", atts);
        for(int i=0; i<args.length; ++i){
	    atts = new org.xml.sax.helpers.AttributesImpl();    
	    handler.startElement("", "param", "param", atts);
            args[i].write(handler);
	    handler.endElement("", "param", "param");
	}
	handler.endElement("", "call", "call");
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
