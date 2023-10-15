package org.oXML.type;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

public abstract class AbstractNode implements Node {

    private final Type type;
    private Map instances;
    protected AbstractNode me;

    // called by subclass constructor - 'me' is this Java object
    public AbstractNode(Type type){
	this.type = type;
	me = this; // i am i
	instances = new java.util.HashMap();
	instances.put(type.getName(), this);
    }

    // called by subtype constructor - 'me' is another Java object
    public AbstractNode(AbstractNode me, Type type){
	this.me = me.me;
	this.type = type;
    }

    // abstract copy constructor
//     public AbstractNode(AbstractNode other, boolean deep){
// 	type = other.type;
// 	if(other.me == other){
// 	    me = this;
// 	    instances = new java.util.HashMap();
// 	    Iterator it = other.instances.keySet().iterator();
// 	    while(it.hasNext()){
// 		// copy parent instances
// 		Name key = (Name)it.next();
// 		Node parent = (Node)other.instances.get(key);
// 		AbstractNode copy = (AbstractNode)parent.copy(deep);
// 		copy.me = this;
// 		instances.put(key, copy);
// 	    }
// // 	}else{
// // 	    me = (AbstractNode)other.me.copy(deep);
// 	}
//     }

    // called direct, ie never
//     public AbstractNode(Type type){
// 	super(type);
// 	// should be set by derived class
// 	// instances = new HashMap();
// 	// or here: 
// 	if(me.instances == null)
// 	    me.instances = new HashMap();
//     }

    public final Type getType(){
        return type;
    }

    public Node cast(Type type){
	assert hasInstance(type.getName()) : type.getName();
	return (Node)me.instances.get(type.getName());
    }

    public Node cast(){
	return me;
    }

    // called by invoke to get right instance
    public AbstractNode getInstance(Name name){
	assert hasInstance(name) : name;
	return (AbstractNode)me.instances.get(name);
    }

    public boolean hasInstance(Name name){
	return me.instances.containsKey(name);
    }

    public void setInstance(Name name, AbstractNode instance){
	assert !hasInstance(name) : name;
	me.instances.put(name, instance);
    }

    public void init() {}
// 	throws ObjectBoxException {}

    // utility functions for Java engine

    private static final Name NUMBER_FUNCTION = new Name("number");
    private static final Type[] NO_PARAMS = new Type[0];
    private static final Node[] NO_ARGS = new Node[0];
    // implemented to use o:XML engine dispatch mechanism, to preserve
    // multiple inheritance and dynamic type semantics
    public final NumberNode toNumberNode()
        throws ObjectBoxException {
        // call number()
        Function fun = type.getFunction(NUMBER_FUNCTION, NO_PARAMS);
        if(fun == null)
            throw new ObjectBoxException("No such function: "+type.getName()+".number()");
        return (NumberNode)fun.invoke(this, NO_ARGS, Threads.getContext()).cast(NumberNode.TYPE);
	    // might be overridden to not return NumberNode, eg empty nodeset
// 	    Node value = fun.invoke(this, NO_ARGS);
// 	    if(!value.instanceOf(NumberNode.TYPE))
// 		throw new ObjectBoxException("number() does not return a Number value!");
// 	    value = value.cast(NumberNode.TYPE);
    }

    public final double numberValue(){
        try{
	    // call number()
 	    NumberNode value = toNumberNode();
	    return value.getDouble();
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
            return Double.NaN;
        }
    }

    private static final Name BOOLEAN_FUNCTION = new Name("boolean");
    public final BooleanNode toBooleanNode()
        throws ObjectBoxException {
        // call boolean()
        Function fun = type.getFunction(BOOLEAN_FUNCTION, NO_PARAMS);
        if(fun == null)
            throw new ObjectBoxException("No such function: "+type.getName()+".boolean()");
        return (BooleanNode)fun.invoke(this, NO_ARGS, Threads.getContext()).cast(BooleanNode.TYPE);
    }

    public final boolean booleanValue(){
        try{
	    // call boolean()
 	    BooleanNode value = toBooleanNode();
	    return value.getBoolean();
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
            return false;  // how about 'maybe'!
        }
    }

    private static final Name BYTES_FUNCTION = new Name("bytes");
    public final BytesNode toBytesNode()
        throws ObjectBoxException {
        // call bytes()
        Function fun = type.getFunction(BYTES_FUNCTION, NO_PARAMS);
        if(fun == null)
            throw new RuntimeException("No such function: "+type.getName()+".bytes()");
        return (BytesNode)fun.invoke(this, NO_ARGS, Threads.getContext());
    }

    public final byte[] byteValue(){
        try{
	    // call bytes()
 	    BytesNode value = toBytesNode();
	    return value.getBytes();
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
            return new byte[]{};
        }
    }

    private static final Name STRING_FUNCTION = new Name("string");
    public final StringNode toStringNode()
        throws ObjectBoxException {
	    // call string()
	    Function fun = type.getFunction(STRING_FUNCTION, NO_PARAMS);
	    if(fun == null)
		throw new RuntimeException("No such function: "+type.getName()+".string()");
 	    return (StringNode)fun.invoke(this, NO_ARGS, Threads.getContext());
    }

    public final String stringValue(){
        try{
	    // call string()
 	    StringNode value = toStringNode();
	    return value.getString();
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
            return "";
        }
    }

    private static final Name NODES_FUNCTION = new Name("nodes");
    public final Nodeset getChildNodes(){
        try{
	    // call nodes()
	    Function fun = type.getFunction(NODES_FUNCTION, NO_PARAMS);
	    if(fun == null)
		return Nodeset.EMPTY_SET;
 	    NodesetNode value = (NodesetNode)fun.invoke(this, NO_ARGS, Threads.getContext());
	    return value.getNodeset();
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
	    return Nodeset.EMPTY_SET;
        }
    }

    private static final Name PARENT_FUNCTION = new Name("parent");
    public final Node getParent(){
        try{
	    // call parent()
	    Function fun = type.getFunction(PARENT_FUNCTION, NO_PARAMS);
 	    Node value = fun.invoke(this, NO_ARGS, Threads.getContext());
	    if(value == NodesetNode.EMPTY_SET)
		return null;
	    return value;
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
	    return null;
        }
    }

    private static final Name DETACH_FUNCTION = new Name("detach");
    public final void setParent(Node parent){ // should be ParentNode parent
	if(parent == null){
	    try{
		// call detach()
		Function fun = type.getFunction(DETACH_FUNCTION, NO_PARAMS);
		if(fun == null)
		    throw new RuntimeException
			("No such function: "+type.getName()+
			 ".detach()");
		fun.invoke(this, NO_ARGS, Threads.getContext());
	    }catch(ObjectBoxException exc){
		Log.exception(exc);
	    }
	}else{
	    try{
		// call parent(Node)
		Function fun = type.getFunction(PARENT_FUNCTION, new Type[]{parent.getType()});
		if(fun == null)
		    throw new RuntimeException
			("No such function: "+type.getName()+
			 ".parent("+parent.getType().getName()+')');
		fun.invoke(this, new Node[]{parent}, Threads.getContext());
	    }catch(ObjectBoxException exc){
		Log.exception(exc);
	    }
	}
    }

    private static final Name ATTRIBUTE_FUNCTION = new Name("attribute");
    private static final Type[] NAME_PARAM = new Type[]{NameNode.TYPE};
    public final AttributeNode getAttribute(Name name){
        try{
	    // call attribute(Name name)
	    Function fun = type.getFunction(ATTRIBUTE_FUNCTION, NAME_PARAM);
	    if(fun == null)
		return null;
	    Node value = fun.invoke(this, new Node[]{new NameNode(name)}, Threads.getContext());
	    if(value == NodesetNode.EMPTY_SET)
		return null;
 	    return (AttributeNode)value.cast(AttributeNode.TYPE);
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
	    return null;
        }
    }

    private static final Name ATTRIBUTES_FUNCTION = new Name("attributes");
    public java.util.Collection getAttributes(){
        try{
	    // call attributes()
	    Function fun = type.getFunction(ATTRIBUTES_FUNCTION, NO_PARAMS);
	    if(fun == null)
		return java.util.Collections.EMPTY_SET;
 	    Node value = fun.invoke(this, NO_ARGS, Threads.getContext());
	    Nodeset kids = value.getChildNodes();
	    NodeIterator nit = kids.getIterator();
	    java.util.Set attributes = new java.util.HashSet();
	    for(Node kid = nit.nextNode(); kid != null; kid = nit.nextNode()){
		attributes.add(kid);
	    }
	    return attributes;
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
	    return java.util.Collections.EMPTY_SET;
        }
    }

    private static final Name DOCUMENT_FUNCTION = new Name("document");
    public final Node getDocument(){
        try{
	    // call document()
	    Function fun = type.getFunction(DOCUMENT_FUNCTION, NO_PARAMS);
 	    Node value = fun.invoke(this, NO_ARGS, Threads.getContext());
	    if(value == NodesetNode.EMPTY_SET)
		return null;
	    return value;
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
	    return null;
        }
    }

    private static final Name NAME_FUNCTION = new Name("name");
    public final Name getName(){
        try{
	    // call name()
	    Function fun = type.getFunction(NAME_FUNCTION, NO_PARAMS);
	    if(fun == null)
		return null;
 	    Node value = fun.invoke(this, NO_ARGS, Threads.getContext());
	    if(value == NodesetNode.EMPTY_SET)
		return null;
	    return ((NameNode)value.cast(NameNode.TYPE)).getNodeName();
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
	    return null;
        }
    }

    public final void setName(Name name){
        try{
	    // call name(Name name)
	    Function fun = type.getFunction(NAME_FUNCTION, NAME_PARAM);
	    if(fun == null)
		throw new RuntimeException
		    ("No such function: "+type.getName()+".name(Name)");
 	    fun.invoke(this, new Node[]{new NameNode(name)}, Threads.getContext());
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
        }
    }

    private static final Type[] ATTRIBUTE_PARAM = new Type[]{AttributeNode.TYPE};
    public final void setAttribute(AttributeNode attribute){
        try{
	    // call attribute(Attribute attribute)
	    Function fun = type.getFunction(ATTRIBUTE_FUNCTION, ATTRIBUTE_PARAM);
	    if(fun == null)
		throw new RuntimeException
		    ("No such function: "+type.getName()+
		     ".attribute("+attribute.getType().getName()+')');
 	    fun.invoke(this, new Node[]{attribute}, Threads.getContext());
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
        }
    }

    private static final Name NEXT_FUNCTION = new Name("next");
    public final Node getNextSibling(){
        try{
	    // call next()
	    Function fun = type.getFunction(NEXT_FUNCTION, NO_PARAMS);
	    if(fun == null)
		throw new RuntimeException("No such function: "+type.getName()+".next()");
	    Node value = fun.invoke(this, NO_ARGS, Threads.getContext());
	    if(value == NodesetNode.EMPTY_SET)
		return null;
 	    return value;
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
	    return null;
        }
    }

    private static final Name PREVIOUS_FUNCTION = new Name("previous");
    public final Node getPreviousSibling(){
        try{
	    // call next()
	    Function fun = type.getFunction(PREVIOUS_FUNCTION, NO_PARAMS);
	    if(fun == null)
		throw new RuntimeException("No such function: "+type.getName()+".previous()");
	    Node value = fun.invoke(this, NO_ARGS, Threads.getContext());
	    if(value == NodesetNode.EMPTY_SET)
		return null;
	    return value;
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
	    return null;
        }
    }

    private static final Name APPEND_FUNCTION = new Name("append");
    public final void addChildNode(Node child){
        try{
	    // call append(Node)
	    Function fun = type.getFunction(APPEND_FUNCTION, new Type[]{child.getType()});
	    if(fun == null)
		throw new RuntimeException
		    ("No such function: "+type.getName()+
		     ".append("+child.getType().getName()+')');
 	    fun.invoke(this, new Node[]{child}, Threads.getContext());
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
        }
    }

    private static final Name REMOVE_FUNCTION = new Name("remove");
    public final void removeChild(Node child){
        try{
	    // call remove(Node)
	    Function fun = type.getFunction(REMOVE_FUNCTION, new Type[]{child.getType()});
	    if(fun == null)
		throw new RuntimeException
		    ("No such function: "+type.getName()+
		     ".remove("+child.getType().getName()+')');
 	    fun.invoke(this, new Node[]{child}, Threads.getContext());
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
        }
    }

    private static final Name INSERT_FUNCTION = new Name("insert");
    public final void insert(int pos, Node child){
        try{
	    // call insert(Number, Node)
	    Function fun = type.getFunction
		(INSERT_FUNCTION, new Type[]{NumberNode.TYPE, child.getType()});
	    if(fun == null)
		throw new RuntimeException
		    ("No such function: "+type.getName()+
		     ".insert(Number, "+child.getType().getName()+')');
 	    fun.invoke(this, new Node[]{new NumberNode(pos), child}, Threads.getContext());
        }catch(ObjectBoxException exc){
	    Log.exception(exc);
        }
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

    // functions that call other dispatch functions

    public void setAttribute(Name name, String value){
	setAttribute(new AttributeNode(name, value));
    }

    public String getAttributeValue(Name name){
	AttributeNode attribute = getAttribute(name);
	return attribute == null ? null : attribute.getValue();
    }

//     public void insertBefore(Node insert, Node child){
//         throw new RuntimeException
//            ("hierarchy exception: node may not have child: "+getType());
//     }

//     public void insertAfter(Node insert, Node child){
//         throw new RuntimeException
//            ("hierarchy exception: node may not have child: "+getType());
//     }

//     public void replaceChild(Node child, Node replacement){
//         throw new RuntimeException
//            ("hierarchy exception: node may not have child: "+getType());
//     }

    // subclasses are depending on Node downcasting
    public boolean equals(Object other){
// 	if(other instanceof AbstractNode)
// 	    return equals((AbstractNode)other);
	if(other instanceof Node)
	    return equals((Node)other);
	return false;
    }

    /**
     * identity function
     */
    public boolean equals(Node other){
	if(other instanceof AbstractNode)
	    return ((AbstractNode)other).me == me;
	return false;
    }

    public String toString(){
	StringBuffer buf = new StringBuffer(getClass().getName());
	buf.append('<').append(type.getName());
	Iterator it = me.instances.keySet().iterator();
	while(it.hasNext())
	    buf.append(',').append(it.next().toString());
	buf.append('>');
	return buf.toString();
	    //        return getClass().getName()+'<'+type.getName()+'>';
    }

    public int hashCode(){
      return toString().hashCode();
    }

    /* this copy method is used to create instances of parent types */
    public abstract AbstractNode copy(AbstractNode me, Type type, boolean deep);
}