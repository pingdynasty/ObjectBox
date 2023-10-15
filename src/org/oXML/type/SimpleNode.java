package org.oXML.type;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

public abstract class SimpleNode implements Node {

    private final Type type;
    private Node parent;

    protected SimpleNode(Type type){
	this.type = type;
    }

    public SimpleNode(SimpleNode other, boolean deep){
	type = other.type;
    }

    public final Type getType(){
        return type;
    }

    public Node cast(Type type){
	return this;
    }

    public Node cast(){
	return this;
    }

    public Node getParent(){
	return parent;
    }

    public void setParent(Node parent){
	this.parent = parent;
    }

    public Node getDocument(){
	return parent == null ? null : parent.getDocument();
    }

    public AttributeNode getAttribute(Name name){
	return null;
    }

    public java.util.Collection getAttributes(){
	return java.util.Collections.EMPTY_SET;
    }

    public String getAttributeValue(Name name){
	AttributeNode attribute = getAttribute(name);
	return attribute == null ? null : attribute.getValue();
    }

    public Node getNextSibling(){
	Node parent = getParent();
        if(parent == null)
            return null;
        int pos = parent.getChildNodes().indexOf(this);
        if(pos < 0)
            throw new RuntimeException
                ("hierarchy exception: parent has no such child: "+this);
        return pos-- > 0 ? (Node)parent.getChildNodes().getNode(pos) : null;
    }

    public Node getPreviousSibling(){
	Node parent = getParent();
        if(parent == null)
            return null;
        int pos = parent.getChildNodes().indexOf(this);
        if(pos < 0)
            throw new RuntimeException
                ("hierarchy exception: parent has no such child: "+this);
        return ++pos < parent.getChildNodes().size() ? (Node)parent.getChildNodes().getNode(pos) : null;
    }

    public Nodeset getChildNodes(){
	return Nodeset.EMPTY_SET;
    }

    public Name getName(){
        return null;
    }

    public void setAttribute(Name name, String value){
	setAttribute(new AttributeNode(name, value));
    }

    public void setAttribute(AttributeNode attribute){
	Log.warning("cannot set attribute of: "+type.getName());
    }

    public void setName(Name name){
	Log.warning("cannot set name of: "+type.getName());
    }

    public void addChildNode(Node child){
	Log.warning("cannot add child node to: "+type.getName());
    }

    public void insert(int pos, Node child){
	Log.warning("cannot add child node to: "+type.getName());
    }

    public void removeChild(Node child){
	Log.warning("cannot remove child node from: "+type.getName());
    }

    public Node invoke(Name name, Node[] args)
        throws ObjectBoxException {
	Type[] sig = new Type[args.length];
	for(int i=0; i<sig.length; ++i)
	    sig[i] = args[i].getType();
	Function fun = type.getFunction(name, sig);
	if(fun == null)
	    throw new ObjectBoxException("No such function: "+Function.print(name, sig));
	return fun.invoke(this, args, Threads.getContext());
    }

    public String stringValue(){
	return "";
    }

    public double numberValue(){
	return Double.NaN;
    }

    public boolean booleanValue(){
	return false;
    }

    public byte[] byteValue(){
// 	return stringValue().getBytes();
	return new byte[0];
    }

    public StringNode toStringNode(){
        return new StringNode(stringValue());
    }

    public NumberNode toNumberNode(){
        return new NumberNode(numberValue());
    }

    public BooleanNode toBooleanNode(){
        return BooleanNode.booleanNode(booleanValue());
    }

    public BytesNode toBytesNode(){
        return new BytesNode(byteValue());
    }

    // subclasses may be depending on Node downcasting
    public boolean equals(Object other){
	if(other instanceof Node)
	    return equals((Node)other);
	return false;
    }

    public boolean equals(Node other){
        return this == other;
    }

    public String toString(){
        return getClass().getName()+'<'+type.getName()+'>';
    }

    public int hashCode(){
      return toString().hashCode();
    }
}