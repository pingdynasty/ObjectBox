package org.oXML.engine.template;

import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.ReturnException;
import org.oXML.xpath.XPathException;
import org.oXML.type.Variable;
import org.oXML.type.Function;
import org.oXML.type.TypeFunction;
import org.oXML.type.NodesetNode;
import org.oXML.type.Nodeset;
import org.oXML.type.Node;
import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.util.Log;

public class DefaultNodesFunction extends TypeFunction {

    public static final Name NAME = new Name("nodes");
    public static final Type[] SIG = new Type[]{};

    public DefaultNodesFunction(Type type){
        super(type, NAME, SIG);
    }

    public Node invoke(Node target, Node[] args, RuntimeContext rc)
        throws ObjectBoxException{
        Type type = getDeclaringType();
        DynamicNode me = (DynamicNode)target.cast(type);
        Variable[] vars = type.getVariables();
	// this will hold our return value 
	Nodeset result = rc.pushOutputNodeset();
        try{
            for(int i=0; i<vars.length; ++i){
                // output every public variable
                // todo: check if calling context is public, protected or private
//                 if(vars[i].isPublic()){
                Node value = me.getVariable(vars[i].getName()).getValue();
                rc.push(value);
                rc.pop();
            }
        }finally{
	    rc.popOutputNodeset();
        }
        return new NodesetNode(result);
    }
}
