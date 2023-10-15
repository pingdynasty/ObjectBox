package org.oXML.engine;

import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import org.oXML.type.Name;
import org.oXML.type.AttributeNode;
import org.oXML.util.Log;

public class StreamResultHandler implements ResultHandler{

//     public static final String DEFAULT_ENCODING = "UTF-8";
    private PrintWriter out;
    private List current;
    private NamespaceHandler nsHandler;
    private boolean rest = false; // controls open or closed completion of elements
    private String encoding;
    private static final String XML_CONTENT_TYPE = "text/xml";
    /**
     * create a new StreamResultHandler using the default character encoding
     */
    public StreamResultHandler(OutputStream out) {
        this(new OutputStreamWriter(out));
    }

    /**
     * create a new StreamResultHandler using the specified character encoding
     */
    public StreamResultHandler(OutputStream out, String encoding)
	throws UnsupportedEncodingException {
        this(new OutputStreamWriter(out, encoding));
	this.encoding = encoding;
    }

    public StreamResultHandler(Writer out, String encoding){
        this(new PrintWriter(out), encoding);
    }

    public StreamResultHandler(PrintWriter out, String encoding){
        this.out = out;
	this.encoding = encoding;
        current = new ArrayList();
	nsHandler = new NamespaceHandler();
    }

    public StreamResultHandler(Writer out){
        this(new PrintWriter(out));
    }

    public StreamResultHandler(PrintWriter out){
        this.out = out;
        current = new ArrayList();
	nsHandler = new NamespaceHandler();
    }

    public Object getResult(){
	fill();
        return out;
    }

    public void text(String text){
	fill();
        out.print(escape(text));
    }

    public void comment(String text){
	fill();
        out.print("<!--"+text+"-->");
    }

    public void processingInstruction(String target, String data){
	fill();
        out.print("<?");
        out.print(target);
        out.print(' ');
        out.print(data);
        out.println("?>");
    }

    public void startElement(Name name, Collection attributes){
	fill();
	nsHandler.startElement(name, attributes);
        out.print('<');
	String ns = name.getNamespaceURI();
	if(ns.equals(Name.EMPTY_NAMESPACE)){
	    out.print(name.getLocalName());
	}else{
	    String prefix = nsHandler.getPrefix(ns);
	    if(prefix == null){
		// create a new namespace declaration
		prefix = nsHandler.createNamespaceDeclaration(name);
                out.print(prefix);
                out.print(':');
                out.print(name.getLocalName());
                out.print(' ');
		// print the new namespace declaration
		printNamespaceDeclaration(prefix, ns);
	    }else if(prefix.equals(Name.NO_PREFIX)){
		out.print(name.getLocalName());
	    }else{
		out.print(prefix+':'+name.getLocalName());
	    }
	}
        Iterator it = attributes.iterator();
        while(it.hasNext()){
            AttributeNode attr = (AttributeNode)it.next();
	    printAttribute(attr.getNodeName(), attr.getValue());
        }
	rest = true;
        current.add(0, name);
    }

    protected void fill(){
	if(rest){
	    out.print('>');
	    rest = false;
	}
    }

    protected void printNamespaceDeclaration(String prefix, String uri){
        if(!prefix.equals(NamespaceHandler.XMLNS_PREFIX))
            out.print("xmlns:");
	out.print(prefix);
        out.print("=\"");
        out.print(escape(uri));
        out.print('"');
    }

    protected void printAttribute(Name name, String value){
	out.print(' ');
	// print the (possibly namespaced) name
	String ns = name.getNamespaceURI();
        String local = name.getLocalName();
        if(Name.EMPTY_NAMESPACE.equals(ns)){
	    out.print(local);
	}else{
	    String prefix = nsHandler.getPrefix(ns);
	    if(prefix == null){
		// create a new prefix
		prefix = nsHandler.createNamespaceDeclaration(name);
		// print the new namespace declaration
		printNamespaceDeclaration(prefix, ns);
		out.print(' ');
		out.print(prefix+':'+local);
	    }else if(prefix.equals(Name.NO_PREFIX) || 
                     local.equals(NamespaceHandler.XMLNS_PREFIX)){
                out.print(local);
	    }else{
		out.print(prefix+':'+local);
	    }
	}
	out.print("=\"");
	out.print(escape(value));
	out.print('"');
    }

    public String closed(Name name){
	String ns = name.getNamespaceURI();
	if(ns.equals(Name.EMPTY_NAMESPACE))
	    return name.getLocalName();
	String prefix = nsHandler.getPrefix(ns);
	if(prefix.equals(Name.NO_PREFIX))
	    return name.getLocalName();
	return prefix+':'+name.getLocalName();
    }

    public void endElement(Name name){
	nsHandler.endElement();
	Name nm = (Name)current.remove(0);
	if(!name.equals(nm))
	    Log.warning("name mismatch: "+name+" != "+nm);
//              throw new RuntimeException("name mismatch: "+name+" != "+nm);
	if(rest){
	    out.print("/>");
	    rest = false;
	}else{
	    out.print("</");
	    out.print(closed(name));
	    out.print('>');
	}	
    }

    private static final String replace(String data, String from, String[] to){
	int size = data.length();
	StringBuffer result = new StringBuffer();
        for(int i=0; i < size; i++){
	    char c = data.charAt(i);
	    int pos = from.indexOf(c);
            if(pos < 0)
                result.append(c);
            else
                result.append(to[pos]);
        }
        return result.toString();
    }

//     private static final char[] from = {'&', '<', '>'};
    private static final String from = "&<>";
    private static final String[] to = new String[] {"&amp;", "&lt;", "&gt;"};

    /**
     * perform character escaping of &, < and > chars
     */
    public static final String escape(String data){
        return replace(data, from, to);

    }

    public void start(String contenttype){
	if(contenttype.equals(XML_CONTENT_TYPE)){
            // should be output in startDocument(encoding) but no document is produced?
            // check ResultDirector.document(doc)
            StringBuffer decl = new StringBuffer("<?xml version=\"1.0\"");
            if(encoding != null)
                decl.append(" encoding=\"").append(encoding).append('"');
            decl.append("?>");
            out.println(decl.toString());
	}
    }

    public void end(){
	out.flush();
    }

//     public void startDocument(String encoding) {
    public void startDocument() {
//         StringBuffer decl = new StringBuffer("<?xml version=\"1.0\"");
//         if(encoding != null)
//             decl.append(" encoding=\"").append(encoding).append('"');
//         decl.append("?>");
//         out.println(decl.toString());
    }

//     public void endDocument() {}

    public void startDocumentType(String name, String publicId, String systemId,
				  String internalSubset) {
	StringBuffer decl = new StringBuffer("<!DOCTYPE ");
	decl.append(name);
	if(publicId != null)
	    decl.append(" PUBLIC ")
		.append('"').append(publicId).append('"')
		.append(' ').append('"').append(systemId).append('"');
	else if(systemId != null)
	    decl.append(" SYSTEM ").append('"').append(systemId).append('"');
	if(internalSubset != null)
	    decl.append(" [").append(internalSubset).append(']');
	decl.append('>');
	out.println(decl.toString());
    }

    public void endDocumentType() {}

    public void notationDeclaration(String name, String publicId, 
				    String systemId) {}
    
    /**
     * @param notationName For unparsed entities, the name of the notation for the entity. For parsed entities, this is null.
     */
    public void entityDeclaration(String name, String publicId, 
				  String systemId, String notationName) {}
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
