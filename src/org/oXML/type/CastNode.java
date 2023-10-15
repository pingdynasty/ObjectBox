package org.oXML.type;

import org.oXML.ObjectBoxException;

public class CastNode implements Node {

    private Node wrapped;
    private Type cast;

    public CastNode(Node wrapped, Type cast){
        this.wrapped = wrapped;
        this.cast = cast;
    }

    public Type getType(){
        return cast;
    }

    public Node cast(Type type){
        if(type.instanceOf(cast))
            return wrapped.cast(type);
        else
            throw new RuntimeException("invalid cast: "+cast.getName()+" to "+type.getName());
    }

    /**  downcast to actual type */
    public Node cast(){
        return wrapped.cast(cast);
    }

    public StringNode toStringNode()
        throws ObjectBoxException{
        return wrapped.toStringNode();
    }
    public NumberNode toNumberNode()
        throws ObjectBoxException{
        return wrapped.toNumberNode();
    }
    public BooleanNode toBooleanNode()
        throws ObjectBoxException{
        return wrapped.toBooleanNode();
    }
    public BytesNode toBytesNode()
        throws ObjectBoxException{
        return wrapped.toBytesNode();
    }
    public double numberValue(){
        return wrapped.numberValue();
    }
    public boolean booleanValue(){
        return wrapped.booleanValue();
    }
    public byte[] byteValue(){
        return wrapped.byteValue();
    }
    public String stringValue(){
        return wrapped.stringValue();
    }
    public Node getDocument(){
        return wrapped.getDocument();
    }
    public Node getParent(){
        return wrapped.getParent();
    }
    public void setParent(Node parent){
        wrapped.setParent(parent);
    }
    public Node getPreviousSibling(){
        return wrapped.getPreviousSibling();
    }
    public Node getNextSibling(){
        return wrapped.getNextSibling();
    }
    public Nodeset getChildNodes(){
        return wrapped.getChildNodes();
    }
    public void addChildNode(Node child){
        wrapped.addChildNode(child);
    }
    public void removeChild(Node child){
        wrapped.removeChild(child);
    }
    public void insert(int pos, Node insert){
        wrapped.insert(pos, insert);
    }
    public java.util.Collection getAttributes(){
        return wrapped.getAttributes();
    }
    public AttributeNode getAttribute(Name name){
        return wrapped.getAttribute(name);
    }
    public void setAttribute(AttributeNode attribute){
        wrapped.setAttribute(attribute);
    }
    public String getAttributeValue(Name name){
        return wrapped.getAttributeValue(name);
    }
    public void setAttribute(Name name, String value){
        wrapped.setAttribute(name, value);
    }
    public Name getName(){
        return wrapped.getName();
    }
    public void setName(Name name){
        wrapped.setName(name);
    }
    public Node copy(boolean deep){
        return wrapped.getNextSibling();
    }
    public boolean equals(Object other){
        return wrapped.equals(other);
    }
    public boolean equals(Node other){
        return wrapped.equals(other);
    }

    /**  invoke a type function */
    public Node invoke(Name name, Node[] args)
        throws ObjectBoxException{
	Type[] sig = new Type[args.length];
	for(int i=0; i<sig.length; ++i)
	    sig[i] = args[i].getType();
	Function fun = cast.getFunction(name, sig);
	if(fun == null)
	    throw new ObjectBoxException("No such function: "+Function.print(name, sig));
	return fun.invoke(this, args, Threads.getContext());
    }
}