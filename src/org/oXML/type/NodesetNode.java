package org.oXML.type;

import org.oXML.ObjectBoxException;
import org.oXML.xpath.iterator.DynamicNodeset;
import org.oXML.util.Log;
    
public class NodesetNode extends SimpleNode  {

    public static final Type TYPE = NodesetType.TYPE;
    public static final NodesetNode EMPTY_SET = new NodesetNode(Nodeset.EMPTY_SET);

    private final Nodeset kids;

    public NodesetNode(Nodeset set){
	super(TYPE);
	kids = set;
    }

    public Nodeset getChildNodes(){
        return kids;	
    }

    public Nodeset getNodeset(){
        return kids;
    }
  
    /** copy constructor */
    public NodesetNode(NodesetNode other, boolean deep){
	super(TYPE);
	kids = new DynamicNodeset();
	if(deep){
	    for(int i=0; i < other.kids.size(); ++i){
		Node kid = other.kids.getNode(i).copy(true);
		kids.addNode(kid);
	    }
	}else{
	    for(int i=0; i < other.kids.size(); ++i){
		kids.addNode(other.kids.getNode(i));
	    }
	}
    }

    public Node invoke(Name name, Node[] args)
        throws ObjectBoxException {
        if(kids.isEmpty())
            return NodesetNode.EMPTY_SET;
        else
            return super.invoke(name, args);
    }

    /**
       <doc:see>XPath v1.0 4.2 String Functions</doc:see>
       <doc:p>A node-set is converted to a string by returning the string-value of the node in the node-set that is first in document order. If the node-set is empty, an empty string is returned.</doc:p>
    */
    public String stringValue(){
	Node kid = kids.getNode(0);
	if(kid == null)
	    return "";
	return kid.stringValue();
    }

    public StringNode toStringNode(){
        return new StringNode(stringValue()); // inefficient! todo: check if kids(0) is a String
    }

    public boolean booleanValue(){
	return !kids.isEmpty();
    }

    public BooleanNode toBooleanNode(){
        return BooleanNode.booleanNode(!kids.isEmpty());
    }

    public byte[] byteValue(){
	return stringValue().getBytes();
    }

    public BytesNode toBytesNode(){
        return new BytesNode(stringValue().getBytes());
    }

    /**
       XPath v1.0 4.4 Number Functions
       A node-set is first converted to a string as if by a call to the string function and then converted in the same way as a string argument.
     */
    public double numberValue(){
	String value = stringValue();
	try{
	    return Double.parseDouble(value);
	}catch(NumberFormatException exc){
	    return Double.NaN;
	}
    }

    public NumberNode toNumberNode(){
        return new NumberNode(numberValue());
    }
  
    public Node copy(boolean deep){
	return new NodesetNode(this, deep);
    }

    public String toString(){
	return super.toString()+'<'+kids+'>';
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
