package org.oXML.engine.template;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;
import org.oXML.type.Node;
import org.oXML.type.Nodeset;
import org.oXML.type.NodesetNode;
import org.oXML.xpath.Expression;
import org.oXML.xpath.PrefixResolver;
import org.oXML.xpath.XPathException;

public class SortTemplate implements Template {
    private Expression select;
    private Expression order;
    private Expression datatype;
    private Template body;

    class Item implements Comparable{
        Node node;
        Comparable value;

	public Item(Node node, Comparable value) {
            this.node = node;
	    this.value = value;
        }

        public int compareTo(Object other){
	    return value.compareTo(((Item)other).value);
        }
    }

    public SortTemplate(Expression select, Expression order, 
			Expression datatype, Template body) {
        this.select = select;
        this.order = order;
        this.datatype = datatype;
        this.body = body;
    }

    public void process(RuntimeContext env)
        throws ObjectBoxException{        

	boolean reverse = false;
	if(order != null){
	    String o = order.evaluate(env).stringValue();
	    if(o.equals("descending"))
		reverse = true;
	    else if(!o.equals("ascending"))
		throw new ObjectBoxException("invalid sort order: "+o);
	}

	boolean number = false;
	if(datatype != null){
	    String d = datatype.evaluate(env).stringValue();
	    if(d.equals("number"))
		number = true;
	    else if(!d.equals("text"))
		throw new ObjectBoxException("invalid sort data-type: "+d);
	}

	// save the context node
	Node saved = env.getContextNode();

	// create unsorted nodeset
	// redirect output
	Nodeset unsorted = env.pushOutputNodeset();
	try{
	    // process
	    body.process(env);
	}finally{
	    // reset handler
	    env.popOutputNodeset();
	}

	// run through the list of child nodes, 
	// evaluating the sort expression for each one
	List sorted = new ArrayList();
	for(int i=0; i<unsorted.size(); ++i){
	    Node current = unsorted.getNode(i);
	    env.setContextNode(current);
	    // evaluate expression with the current context node set
	    Node sort = select.evaluate(env);
	    //  the Comparable value that we sort by - either string or number
	    Comparable value = number ?
		(Comparable)new Double(sort.numberValue()) :
		(Comparable)sort.stringValue();
	    sorted.add(new Item(current, value));
	}
	// reset the context node
	env.setContextNode(saved);

	Collections.sort(sorted); // sort it!

	// finally output the sorted list
	if(reverse){
	    int from = sorted.size() - 1;
	    for(int i=from; i>=0; --i){
		Item item = (Item)sorted.get(i);
		env.push(item.node);
		env.pop();
	    }
	}else{
	    int to = sorted.size();
	    for(int i=0; i<to; ++i){
		Item item = (Item)sorted.get(i);
		env.push(item.node);
		env.pop();
	    }
	}
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
