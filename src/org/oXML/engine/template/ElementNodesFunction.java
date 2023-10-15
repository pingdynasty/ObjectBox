package org.oXML.engine.template;

import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.ReturnException;
import org.oXML.type.Variable;
import org.oXML.type.Function;
import org.oXML.type.TypeFunction;
import org.oXML.type.NodesetNode;
import org.oXML.type.StringNode;
import org.oXML.type.NumberNode;
import org.oXML.type.BooleanNode;
import org.oXML.type.ElementNode;
import org.oXML.type.Nodeset;
import org.oXML.type.Node;
import org.oXML.type.Name;
import org.oXML.type.Type;
import org.oXML.util.Log;

public class ElementNodesFunction extends TypeFunction {

    public static final Name NAME = new Name("nodes");
    public static final Type[] SIG = new Type[]{};

    public ElementNodesFunction(Type type){
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
                // output every variable
                Node value = me.getVariable(vars[i].getName()).getValue();
                Type vtype = value.getType();
                if(vars.length > 1 && 
                   (vtype.instanceOf(StringNode.TYPE) ||
                    vtype.instanceOf(NumberNode.TYPE) ||
                    vtype.instanceOf(BooleanNode.TYPE))){
                    // wrap value in an element
                    rc.push(new ElementNode(vars[i].getName()));
                    rc.push(value);
                    rc.pop();
                    rc.pop();
                }else{
                    rc.push(value);
                    rc.pop();
                }
            }
        }finally{
	    rc.popOutputNodeset();
        }
        return new NodesetNode(result);
    }
}
