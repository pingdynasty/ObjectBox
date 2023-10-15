package org.oXML.engine;

import java.util.*;
import org.oXML.type.Name;
import org.oXML.type.AttributeNode;
import org.oXML.util.Log;

public class NamespaceHandler{
    private Map prefix;
    private Map ns;
    private List last;
    private int inc = 1;

    public static final String XML_PREFIX = "xml";
    public static final String XML_NAMESPACE = "http://www.w3.org/XML/1998/namespace";
    public static final String XMLNS_PREFIX = "xmlns";
    public static final String XMLNS_NAMESPACE = "http://www.w3.org/2000/xmlns/";

    private class Item{
        public Object key;
        public Object value;

        public Item(Object key, Object value){
            this.key = key;
            this.value = value;
        }
    }

    public NamespaceHandler(){
        ns = new HashMap();
        prefix = new HashMap();
        last = new ArrayList();
	// add xml namespace
	prefix.put(XML_NAMESPACE, XML_PREFIX);
	ns.put(XML_PREFIX, XML_NAMESPACE);
	// add xmlns namespace
	prefix.put(XMLNS_NAMESPACE, XMLNS_PREFIX);
	ns.put(XMLNS_PREFIX, XMLNS_NAMESPACE);
    }

    public void startElement(Name elementName, Collection attributes){        
        List prefixes = new ArrayList();
        Iterator it = attributes.iterator();
        while(it.hasNext()){
            AttributeNode attr = (AttributeNode)it.next();
	    Name name = attr.getName();
	    String local = name.getLocalName();
	    if(XMLNS_PREFIX.equals(local)){ // add default namespace
		String uri = attr.getValue();
		prefixes.add(new Item(Name.NO_PREFIX, getNamespace(Name.NO_PREFIX)));
		setNamespace(Name.NO_PREFIX, uri);
	    }else if(XMLNS_NAMESPACE.equals(name.getNamespaceURI())){
		String uri = attr.getValue();
		prefixes.add(new Item(local, getNamespace(uri)));
		setNamespace(local, uri);
            }
        }
        last.add(0, prefixes);
    }

    public void setNamespace(String prefix, String uri){
	assert !prefix.equals(XML_PREFIX) : uri;
        assert !uri.equals(XML_NAMESPACE) : prefix;
	assert !prefix.equals(XMLNS_PREFIX) : uri;
	assert !uri.equals(XMLNS_NAMESPACE) : prefix;
	ns.put(prefix, uri);
	this.prefix.put(uri, prefix);
    }

    public void endElement(){
        List prefixes = (List)last.remove(0);
        for(int i=0; i<prefixes.size(); ++i){
            Item item = (Item)prefixes.get(i);
            ns.put(item.key, item.value);
	    if(item.value != null)
		prefix.put(item.value, item.key);
        }
    }

    public String getNamespace(String prefix){
        return (String)ns.get(prefix);
    }

    public String getPrefix(String uri){
        return (String)prefix.get(uri);
    }

    public String createNamespaceDeclaration(Name name){
        // todo: check that this does not run out of scope without being removed
        String uri = name.getNamespaceURI();
	String prefix = name.getPrefix();
        if(prefix == null){
            prefix = "ns"+inc++;
            name.setPrefix(prefix);
        }
	setNamespace(prefix, uri);
	return prefix;
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
