package org.oXML.engine.template;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import org.oXML.type.*;
import org.oXML.type.AbstractNode;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

/**
   Node with variables and parent instances
 */
public class DynamicNode extends AbstractNode {

    private Map variables;

    public DynamicNode(Type type){
	super(type);
	variables = new HashMap();
	// create parent type instances
	if(!hasInstance(ParentNode.TYPE.getName()))
	    setInstance(ParentNode.TYPE.getName(), new ParentNode(this, type));
	if(!hasInstance(NodeNode.TYPE.getName()))
	    setInstance(NodeNode.TYPE.getName(), new NodeNode(this, type));
	init();
    }

    public DynamicNode(AbstractNode me, Type type){
	super(me, type);
	variables = new HashMap();
	// create parent type instances
	if(!hasInstance(ParentNode.TYPE.getName()))
	    setInstance(ParentNode.TYPE.getName(), new ParentNode(me, type));
	if(!hasInstance(NodeNode.TYPE.getName()))
	    setInstance(NodeNode.TYPE.getName(), new NodeNode(me, type));
    }

    protected DynamicNode(AbstractNode me, Type type, DynamicNode other, boolean deep){
	super(me, type);
	variables = new HashMap();
	// create parent type instances
	if(!hasInstance(ParentNode.TYPE.getName()))
	    setInstance(ParentNode.TYPE.getName(), new ParentNode(me, type));
	if(!hasInstance(NodeNode.TYPE.getName()))
	    setInstance(NodeNode.TYPE.getName(), new NodeNode(me, type));
	// copy variables
	Iterator it = other.variables.keySet().iterator();
	while(it.hasNext()){
	    Name key = (Name)it.next();
	    Node value = other.getVariable(key).getValue();
	    setVariable(new Variable(key, value.copy(deep)));
	}
    }

    public void init(){
	getInstance(ParentNode.TYPE.getName()).init();
	getInstance(NodeNode.TYPE.getName()).init();
    }

    protected DynamicNode(DynamicNode copy, boolean deep){
	this(copy.getType());
	// don't copy self instance
	// self instance is set by AbstractNode ctor
	// copy parent instances - those that are not already instantiated
	Type[] parents = getType().getParentTypes();
	for(int i=0; i<parents.length; ++i){
	    if(!hasInstance(parents[i].getName())){
		AbstractNode parent = (AbstractNode)copy.getInstance(parents[i].getName());
		setInstance(parents[i].getName(), parent.copy(this, getType(), deep));
	    }
	}
	// copy variables
	Iterator it = copy.variables.keySet().iterator();
	while(it.hasNext()){
	    Name key = (Name)it.next();
	    Node value = copy.getVariable(key).getValue();
	    setVariable(new Variable(key, value.copy(deep)));
	}
    }

    public void setVariable(Variable variable){
        variables.put(variable.getName(), variable);
    }

    public Variable getVariable(Name name){
	assert variables.containsKey(name) : name;
        return (Variable)variables.get(name);
    }

    public Node copy(boolean deep){
	if(me == this)
	    return new DynamicNode(this, deep);
	else
	    return me.copy(deep);
    }

    public AbstractNode copy(AbstractNode me, Type type, boolean deep){
        return new DynamicNode(me, type, this, deep);
    }

    // overload dynamic dispatch
//     public String stringValue(){
//         return toString();
//     }

//     public double numberValue(){
// 	try{
// 	    return Double.parseDouble(stringValue());
// 	}catch(NumberFormatException exc){
// 	    return Double.NaN;
// 	}
//     }

//     public boolean booleanValue(){
// 	return true; // because I am
//     }

//     public byte[] byteValue(){
// 	return stringValue().getBytes();
//     }
}