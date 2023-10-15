package org.oXML.type;

import org.oXML.util.Log;
import org.oXML.ObjectBoxException;
import org.oXML.engine.RuntimeContext;

public abstract class ConstructorFunction extends Function {

    private Type type;

    /**
     * @param signature the function signature, or null
     */
    public ConstructorFunction(Type type, Type[] signature){
	super(type.getName(), signature);
        this.type = type;
    }

    public Type getDeclaringType(){
	return type;
    }

//     /**
//      * create a new Node
//      */
//     public Node invoke(Node[] args, RuntimeContext env)
//         throws ObjectBoxException{
// 	return construct(getDeclaringType(), args, env);
//     }

    public Node invoke(Node target, Node[] args, RuntimeContext context)
      throws ObjectBoxException {
	throw new ObjectBoxException("invalid invokation of type constructor: "+print());
    }

//       // we are being called as a parent initialiser
//       // 'target' is a node that is derived from this type
// 	return invoke((AbstractNode)target, args, context);
//     }

//     public Node invoke(AbstractNode target, Node[] args, RuntimeContext context)
//       throws ObjectBoxException {
//       if(!target.hasInstance(getName())){
// 	  Node instance = construct(target.getType(), args, context);
// 	  target.setInstance(getName(), instance);
//       }
//       return target;
//     }

//     public abstract Node construct(Type type, Node[] args, RuntimeContext context)
// 	throws ObjectBoxException;
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
