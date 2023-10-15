package org.oXML.engine;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collection;
import org.oXML.ObjectBoxException;
import org.oXML.type.*;
import org.oXML.xpath.Context;
import org.oXML.xpath.XPathContext;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.function.StackFrame;
import org.oXML.xpath.iterator.DynamicNodeset;
import org.oXML.engine.mapping.dom.TemplateMapping;
import org.oXML.util.Log;

public class RuntimeContext extends Context{

    private StackedMap variables;
    private List hiddenVars;
    private OutputDirector out;
    private List stack;
    private Program program;
    private Stack frames;

    public static final Name NONAME = new Name("");

    public RuntimeContext(Program program, Node ctxt){
        super(program.getResolver(), ctxt);
	this.program = program;
        variables = new StackedMap();
        hiddenVars = new ArrayList();
        stack = new ArrayList();
        out = new OutputDirector();
	frames = new Stack();
// 	// make sure we've got all type constructors in our functions list
// should be done when creating constructor
// 	Iterator it = program.getTypes().values().iterator();
// 	while(it.hasNext()){
// 	    Type type = (Type)it.next();
// 	    functions.addFunctions(type.getConstructors());
// 	}
	org.oXML.type.Threads.setContext(this);
    }

    public RuntimeContext(Program program){
        this(program, new DocumentNode());
    }

    protected RuntimeContext(RuntimeContext other){
        super(other);
	program = other.program;
        variables = other.variables.copy();
        hiddenVars = new ArrayList();
        stack = new ArrayList();
        out = new OutputDirector();
	frames = new Stack();
    }

    public void pushStackFrame(StackFrame frame){
	frames.push(frame);
    }

    public StackFrame popStackFrame(){
	return frames.pop();
    }

    public void addResultHandler(ResultHandler handler){
        out.addHandler(handler);
    }

    public Program getProgram(){
	return program;
    }

    public boolean hasVariable(Name name){
        return variables.containsKey(name);
    }

    public Node getVariable(Name name)
        throws XPathException {
        Variable var = variables.get(name);
        if(var == null)
            throw new XPathException("undefined variable: "+name);
        return var.getValue();
    }

    public void setVariable(Name name, Node value){
        Variable var = variables.get(name);
        if(var == null){
            var = new Variable(name, value);
            variables.set(name, var);
        }else{
            var.setValue(value);
        }
    }

    public void setVariable(Variable var){
        variables.set(var.getName(), var);
    }

    public void hide(){
        hiddenVars.add(0, variables);
        variables = new StackedMap();
    }

    public void unhide(){
        variables = (StackedMap)hiddenVars.remove(0);
    }

//     /**
//      * Add a function at runtime - should only
//      * be used if the function cannot be created at compile time.
//      */
//     public void addFunction(Function function)
// 	throws ObjectBoxException {
//         program.addFunction(function);
//     }

    /**
     * called at the start of processing
     */
    public void start(String contenttype)
        throws ObjectBoxException {
	out.start(contenttype);
    }

    /**
     * called at the end of processing
     */
    public void end()
        throws ObjectBoxException {
	out.end();
    }

    public void push(Node value)
        throws ObjectBoxException{
//          Log.trace("push: "+value);
        if(value.getType().instanceOf(ElementNode.TYPE)){
            push((ElementNode)value.cast(ElementNode.TYPE));
        }else{
            stack.add(0, NONAME);
            out.output(value);
        }
        // push the kids
        Nodeset kids = value.getChildNodes();
        for(int i=0; i<kids.size(); ++i){
            push(kids.getNode(i));
            pop();
        }
//          NodeIterator it = value.getChildNodes().getIterator();
//          for(Node kid = it.nextNode(); kid != null; kid = it.nextNode()){
//              push(kid);
//              pop();
//          }
    }

    public void push(ElementNode value)
        throws ObjectBoxException{
        Name name = value.getName();
        stack.add(0, name);
        variables.increment();
        out.output(value);
//          NodeIterator it = value.getChildNodes().getIterator();
//          for(Node n = it.nextNode(); n != null; n = it.nextNode()){
//              push(n);
//              pop();
//          }
    }

    public void pop()
        throws ObjectBoxException{
        Name name = (Name)stack.remove(0);
        if(name != NONAME){
            out.pop(name);
            variables.decrement();
        }
    }

    private Nodeset output;

    public Nodeset pushOutputNodeset(){
	output = new DynamicNodeset();
        out.setOutputNodeset(output);
	return output;
    }

    public Nodeset pushOutputNodeset(Nodeset output){
	this.output = output;
        out.setOutputNodeset(output);
	return output;
    }	

    public Nodeset popOutputNodeset()
        throws ObjectBoxException {
	out.resetOutputNodeset();
        return output;
    }

//     public void setOutputNodeset(Nodeset outputNodeset){
//         out.setOutputNodeset(outputNodeset);
//     }

//     public void resetOutputNodeset(){
//         out.resetOutputNodeset();
//     }

    public XPathContext copy(){
	return new RuntimeContext(this);
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
