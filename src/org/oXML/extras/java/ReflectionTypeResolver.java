package org.oXML.extras.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.oXML.type.*;
import org.oXML.type.Variable;
import org.oXML.xpath.XPathException;
import org.oXML.type.Function;
import org.oXML.util.Log;

public class ReflectionTypeResolver{

    private Map types;
    private Map names;
    public static final String JAVA_NS = "http://www.o-xml.com/java/";
    private static ReflectionTypeResolver me; // singleton instance

    public static ReflectionTypeResolver getInstance(){
        if(me == null)
            me = new ReflectionTypeResolver();
        return me;
    }

    private ReflectionTypeResolver(){
//         // Node is the parent type of Object
//         // add java.lang.Object as parent type of String, Boolean, Number.
//         StringNode.TYPE.addParent(ObjectType.TYPE);
//         NumberNode.TYPE.addParent(ObjectType.TYPE);
//         BooleanNode.TYPE.addParent(ObjectType.TYPE);
//         NodesetNode.TYPE.addParent(ObjectType.TYPE);
        // add java.lang.Object as parent type of Node (removed parent in ObjectType)
        Node.TYPE.addParent(ObjectType.TYPE);

        names = new HashMap();
	names.put(Node.class, Node.TYPE.getName());
	names.put(StringNode.class, StringNode.TYPE.getName());
	names.put(NumberNode.class, NumberNode.TYPE.getName());
	names.put(BooleanNode.class, BooleanNode.TYPE.getName());
	names.put(ElementNode.class, ElementNode.TYPE.getName());
	names.put(AttributeNode.class, AttributeNode.TYPE.getName());
	names.put(NodesetNode.class, NodesetNode.TYPE.getName());
	names.put(CommentNode.class, CommentNode.TYPE.getName());
	names.put(ProcessingInstructionNode.class, 
		  ProcessingInstructionNode.TYPE.getName());
	names.put(DocumentNode.class, DocumentNode.TYPE.getName());

	names.put(BytesNode.class, BytesNode.TYPE.getName());

        types = new HashMap();
        types.put(getName(Object.class), new ObjectType(this));

        // set the String types - String and Character
        types.put(getName(String.class), new StringType(this));
        types.put(getName(Character.class), new StringType(this, StringType.CHARACTER));
        types.put(getName(Character.TYPE), new StringType(this, StringType.CHARACTER));
        types.put(getName(Character[].class), new StringType(this, StringType.CHARARRAY));
        types.put(getName(char[].class), new StringType(this, StringType.CHARARRAY));

//          resolve(String.class).getType().addParent(org.oXML.type.StringNode.TYPE);
//          ReflectionType stringType = new StringType(this);
//          stringType.getType().addParent(resolve(String.class).getType());
//          stringType.getType().addParent(resolve(Character.class).getType());
//          types.put(getName(String.class), stringType);

	// byte arrays
        types.put(getName(Byte[].class), new BytesType(this));
        types.put(getName(byte[].class), new BytesType(this));

        // set the Number types
        types.put(getName(Integer.class), new NumberType(this, NumberType.INTEGER));
        types.put(getName(Integer.TYPE), new NumberType(this, NumberType.INTEGER));
        types.put(getName(Long.class), new NumberType(this, NumberType.LONG));
        types.put(getName(Long.TYPE), new NumberType(this, NumberType.LONG));
        types.put(getName(Short.class), new NumberType(this, NumberType.SHORT));
        types.put(getName(Short.TYPE), new NumberType(this, NumberType.SHORT));
        types.put(getName(Double.class), new NumberType(this, NumberType.DOUBLE));
        types.put(getName(Double.TYPE), new NumberType(this, NumberType.DOUBLE));
        types.put(getName(Float.class), new NumberType(this, NumberType.FLOAT));
        types.put(getName(Float.TYPE), new NumberType(this, NumberType.FLOAT));

        // set the Boolean types
        types.put(getName(Boolean.class), new BooleanType(this));
        types.put(getName(Boolean.TYPE), new BooleanType(this));
 
        // set the native types
//          types.put(Node.TYPE.getName(), new NodeType(this, Node.TYPE));
        types.put(getName(Node.class), new NodeType(this, Node.TYPE));
        types.put(getName(ElementNode.class), new NodeType(this, ElementNode.TYPE)); 
        types.put(getName(NumberNode.class), new NodeType(this, NumberNode.TYPE));
        types.put(getName(StringNode.class), new NodeType(this, StringNode.TYPE));
        types.put(getName(BooleanNode.class), new NodeType(this, BooleanNode.TYPE));
        types.put(getName(NodesetNode.class), new NodeType(this, NodesetNode.TYPE));
        types.put(getName(BytesNode.class), new NodeType(this, BytesNode.TYPE));
   }

    public ReflectionType getResolvedType(Class javaclass){
        Name name = getName(javaclass);
        return (ReflectionType)types.get(name);
    }

    private Name getName(Class javaclass){
	if(names.containsKey(javaclass))
	    return (Name)names.get(javaclass);	
        String classname = javaclass.getName();
        String ns = JAVA_NS;
        String local;
        if(classname.indexOf('.') > 0){
            ns += classname.substring(0, classname.lastIndexOf('.'));
            local = classname.substring(classname.lastIndexOf('.')+1);
        }else{
            local = classname;
        }
	Name name = new Name(ns, local);
	names.put(javaclass, name);
	return name;
    }

    public ReflectionType resolve(String classname)
        throws ClassNotFoundException{
        Class javaclass = Class.forName(classname);
        ReflectionType type = resolve(javaclass);
        return type;
    }

    public ReflectionType resolve(Class javaclass){
        // check if it is already resolved
        ReflectionType resolved = getResolvedType(javaclass);
        if(resolved != null){
            // it is a known type
            return resolved;
        }

//          Log.trace("resolving java class: "+javaclass.getName());
        Name name = getName(javaclass);

        // resolve parent types
        List list = new ArrayList();

        // first the superclass
        Class superclass = javaclass.getSuperclass();
        // If this object represents an array class then the Class object representing the Object class is returned. todo!
        // If this Class represents either the Object class, an interface, a primitive type, or void, then null is returned.
        if(superclass != null)
	    list.add(resolve(superclass).getType());

        // resolve interfaces
        Class[] ifs = javaclass.getInterfaces();
        for(int i=0; i<ifs.length; ++i)
            list.add(resolve(ifs[i]).getType());

        // if it's an array, add Object[] to the parents
        // this is a bit of a hack to approximate inheritance with arrays
        if(javaclass.isArray() && javaclass != Object[].class)
            list.add(resolve(Object[].class).getType());
            
        // create the type
        Type[] parents = new Type[list.size()];
        list.toArray(parents);
        Type type = new Type(name, parents);

        // create reflection type
        resolved = new ReflectionType(this, type);
        types.put(name, resolved);

        // namespace for static methods and public fields
        String typeNS = name.getNamespaceURI()+'.'+name.getLocalName();

        // resolve methods

	// tbd: investigate: this should probably be:
//         Method[] methods = javaclass.getDeclaredMethods();
	// ...because non-declared methods _should_ be added from parent types
        Method[] methods = javaclass.getMethods();
        for(int i=0; i<methods.length; ++i){
            Function fun = resolveFunction(methods[i]);
		int mod = methods[i].getModifiers();
//                  if(!Modifier.isAbstract(mod) && !Modifier.isPrivate(mod)){
//                      // only consider non-abstract, non-private methods 
// 		Log.trace("function: "+fun);
		if(Modifier.isPublic(mod)){
		    type.addFunction(fun);
                if(Modifier.isStatic(mod)){
                    Name staticName = new Name(typeNS, fun.getName().getLocalName());
                    resolved.addStaticFunction(new StaticMethod(staticName, fun));
		}
            }
        }

        // resolve constructors
        Constructor[] ctors = javaclass.getConstructors();
        for(int i=0; i<ctors.length; ++i)
            resolved.addConstructor(resolveConstructor(resolved, ctors[i]));

        // resolve static public fields
        Field[] fields = javaclass.getFields();
        for(int i=0; i<fields.length; ++i){
            if(Modifier.isPublic(fields[i].getModifiers()) && Modifier.isStatic(fields[i].getModifiers()))
                resolved.addField(resolveField(typeNS, fields[i]));
        }
        
        return resolved;
    }

    /**
     * turn an array of java Class objects into an array of ReflectionTypes
     */
    private ReflectionType[] resolveParameters(Class[] params){
        List args = new ArrayList();
        for(int i=0; i<params.length; ++i)
            args.add(resolve(params[i]));
        ReflectionType[] sig = new ReflectionType[args.size()];
        args.toArray(sig);
        return sig;
    }

    private Function resolveConstructor(ReflectionType type, Constructor ctor){
        ReflectionType[] sig = resolveParameters(ctor.getParameterTypes());
        return new ConvertingConstructor(type, sig, ctor);
    }

    private Function resolveFunction(Method method){
        Name name = new Name(method.getName());
        ReflectionType[] sig = resolveParameters(method.getParameterTypes());
        ReflectionType type = resolve(method.getReturnType());
        return new ConvertingMethod(name, type, sig, method);
    }

    private Variable resolveField(String typeNS, Field field){
        Name name = new Name(typeNS, field.getName());
        Object object;
        try{
            object = field.get(null); // it better be static
        }catch(IllegalAccessException exc){
            object = null;
        }
        // if the return value is null, make it an empty nodeset
        Node value = object == null ? 
            NodesetNode.EMPTY_SET : resolve(object.getClass()).primitive(object);
        return new Variable(name, value);
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
