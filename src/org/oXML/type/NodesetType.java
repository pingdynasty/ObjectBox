package org.oXML.type;

import org.oXML.util.Log;

import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathException;
import org.oXML.ObjectBoxException;
import org.oXML.xpath.iterator.DynamicNodeset;

public class NodesetType extends Type{

    public static final Type TYPE = new NodesetType();

    public class NodesetFunction extends TypeFunction{

        public NodesetFunction(Type type, Name name, Type[] signature){
            super(type, name, signature);
        }

        /**
           Invoking a type function on a nodeset with:
           No nodes - returns an empty nodeset.
           One or more nodes - same as invoking it on each node in turn.
           The return value of calling a function on a nodeset is always
           a nodeset.
         */ 
        public Node invoke(Node obj, Node args[], RuntimeContext ctxt)
            throws ObjectBoxException {
//             Log.trace("nodeset function: "+this.print());
	    if(obj.getChildNodes().isEmpty())
                return NodesetNode.EMPTY_SET;
//             if(obj.getChildNodes().size() == 1){
//                 return obj.getChildNodes().getNode(0).invoke(getName(), args);
            Nodeset result = new DynamicNodeset();
            NodeIterator it = obj.getChildNodes().getIterator();
	    Type[] sig = this.getSignature();
	    Name name = this.getName();
            for(Node node = it.nextNode(); node != null; node = it.nextNode()){
		Function fun = node.getType().getFunction(name, sig);
                if(fun == null)
                    throw new XPathException("no matching function: "+print(node));
                Node value = fun.invoke(node, args, ctxt);
                result.addNode(value);
            }
            if(result.isEmpty())
                return NodesetNode.EMPTY_SET;
            return new NodesetNode(result);
        }

	private String print(Node node){
	    StringBuffer buf = new StringBuffer(node.getType().getName().toString());
	    buf.append('.')
		.append(getName())
		.append('(')
		.append(printSignature())
		.append(')');
	    return buf.toString();	
	}
    }

    private NodesetType(){
        super(new Name("Nodeset"), new Type[]{Node.TYPE});
    }

    public final Function getFunction(Name name, Type[] params){
	return new NodesetFunction(this, name, params);
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
