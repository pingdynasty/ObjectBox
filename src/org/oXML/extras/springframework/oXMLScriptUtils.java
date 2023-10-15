/*
 * Copyright 2002-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.oXML.extras.springframework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.springframework.util.ClassUtils;
import org.springframework.scripting.ScriptCompilationException;

import java.io.IOException;
import java.io.StringReader;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.oXML.ObjectBoxException;
import org.oXML.type.Node;
import org.oXML.type.Name;
import org.oXML.engine.RuntimeContext;
import org.oXML.engine.InterpretedProgram;
import org.oXML.extras.java.JavaExtensions;
import org.oXML.extras.java.ReflectionType;
import org.oXML.extras.java.ReflectionNode;
import org.oXML.extras.java.function.Converter;

/**
 * Utility methods for handling o:XML scripted objects.
 *
 * @author Martin Klang
 * @since 2.0
 */
public abstract class oXMLScriptUtils {

    /**
     * Create a new o:XML object from the given script source.
     * @param scriptSource the script source text
     * @param interfaces the interfaces that the scripted Java object
     * is supposed to implement
     * @return the scripted Java object
     * @throws ObjectBoxException in case of script failure
     */
    public static Object createNodeObject(String scriptSource, Class[] interfaces)
        throws ObjectBoxException {
        JavaExtensions javaExtensions = new JavaExtensions();
        InputSource source = new InputSource(new StringReader(scriptSource));
        source.setSystemId("unknown source");
        InterpretedProgram program;
        try{
            program = new InterpretedProgram(source);
            program.addExtension(javaExtensions);
            //             program.loadDefaultExtensions();
            program.compile();
        }catch(Exception exc){
            throw new ScriptCompilationException("failed to compile o:XML script", exc);
        }
        RuntimeContext env = new RuntimeContext(program);
        Node result = program.run(env);
        if(result == null)
            throw new ScriptCompilationException("o:XML script returned no value - "+
                                                 "did you remember to include an <o:return... at the end?");
        return Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), interfaces,
                                      new oXMLObjectInvocationHandler(javaExtensions, result));
    }

    /**
     * InvocationHandler that invokes an o:XML function on an o:XML object.
     */
    private static class oXMLObjectInvocationHandler implements InvocationHandler {

        private final JavaExtensions javaExtensions;
        private final Node node;

        public oXMLObjectInvocationHandler(JavaExtensions javaExtensions, Node node){
            this.javaExtensions = javaExtensions;
            this.node = node;
        }

        public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
            Node[] nodeArgs = convertFromJava(args);
            Name methodName = new Name(method.getName());
            Node result = node.invoke(methodName, nodeArgs);
            Object j = convertToJava(result, method.getReturnType());
            return j;
        }

        private Object convertToJava(Node node, Class javaClass)
            throws ObjectBoxException {
            // translate primitive types
            if(javaClass == String.class)
                return node.stringValue();
            if(javaClass == Boolean.class || javaClass == Boolean.TYPE)
                return new Boolean(node.booleanValue());
            if(javaClass == Integer.class || javaClass == Integer.TYPE)
                return new Integer((int)node.numberValue());
            if(javaClass == Double.class || javaClass == Double.TYPE)
                return new Double(node.numberValue());
            if(javaClass == Long.class || javaClass == Long.TYPE)
                return new Long((long)node.numberValue());
            if(javaClass == Short.class || javaClass == Short.TYPE)
                return new Short((short)node.numberValue());
            if(node instanceof ReflectionNode)
                // unwrap Java object
                return ((ReflectionNode)node).getInstance();
            if(javaClass.isAssignableFrom(org.w3c.dom.DocumentFragment.class))
                // translate to DOM Node (DocumentFragment)
                return new Converter().fromNative(node);
            return node;
        }

        private Node[] convertFromJava(Object[] javaArgs) 
            throws ObjectBoxException {
            if(javaArgs == null || javaArgs.length == 0)
                return new Node[0];
            Node[] nodeArgs = new Node[javaArgs.length];
            for(int i=0; i<nodeArgs.length; ++i){
                Class javaClass = javaArgs[i].getClass();
                if(org.w3c.dom.Node.class.isAssignableFrom(javaClass)){
                    // translate DOM objects automatically
                    nodeArgs[i] = new Converter().toNative((org.w3c.dom.Node)javaArgs[i]);
                }else{
                    ReflectionType type = javaExtensions.resolve(javaClass);
                    nodeArgs[i] = type.primitive(javaArgs[i]);
                }
            }
            return nodeArgs;
        }
    }
}
