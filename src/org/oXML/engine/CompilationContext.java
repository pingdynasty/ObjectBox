package org.oXML.engine;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import org.oXML.type.*;
import org.oXML.ObjectBoxException;
import org.oXML.engine.template.*;
import org.oXML.engine.mapping.dom.*;
import org.oXML.engine.mapping.FileLocator;
import org.oXML.xpath.Resolver;
import org.oXML.xpath.Expression;
import org.oXML.xpath.SourceLocator;
import org.oXML.xpath.PrefixResolver;
import org.oXML.xpath.XPathException;
import org.oXML.xpath.NamespaceException;
import org.oXML.xpath.parser.Parser;
import org.oXML.xpath.parser.ExpressionParser;
import org.oXML.xpath.parser.MixedExpressionParser;
import org.oXML.xpath.step.LiteralExpression;
import org.oXML.util.Log;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Element;

import java.util.HashSet;
import org.w3c.dom.Document;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

/**
 * represents the context in which an xml document is being compiled
 */
public class CompilationContext {

    private Program program;
    protected Parser parser;
    protected Parser mixedparser;
    private Map mappings;
    private ProgramTemplate progtemplate;

    public static final String OXML_NS = "http://www.o-xml.org/lang/";

    protected static final Template noTemplate = new NullTemplate();
    protected static final TemplateMapping elementMapping = new ElementMapping();
    protected static final DocumentMapping docMapping = new DocumentMapping();

    /* whitespace, comment and attribute value evaluation settings */
    private short ignoreWS = DEFAULT_SETTING;
    private short ignoreComments = DEFAULT_SETTING;
    private short attributeSub = TRUE_SETTING;
    private short textSub = FALSE_SETTING;
    private boolean mappedContext = false;
    private static final short FALSE_SETTING = 0;
    private static final short TRUE_SETTING = 1;
    private static final short DEFAULT_SETTING = 2;

    public void ignoreComments(boolean ignore){
	ignoreComments = ignore ? TRUE_SETTING : FALSE_SETTING;
    }

    public boolean ignoreComments(){
	return ignoreComments == TRUE_SETTING ||
	    (ignoreComments == DEFAULT_SETTING && mappedContext);
    }

    public void ignoreWhitespace(boolean ignore){
	ignoreWS = ignore ? TRUE_SETTING : FALSE_SETTING;
    }

    public boolean ignoreWhitespace(){
	return ignoreWS == TRUE_SETTING ||
	    (ignoreWS == DEFAULT_SETTING && mappedContext);
    }

    public boolean attributeSubstitution(){
	return attributeSub == TRUE_SETTING ||
	    (attributeSub == DEFAULT_SETTING && mappedContext);
    }

    public void attributeSubstitution(boolean evaluate){
	attributeSub = evaluate ? TRUE_SETTING : FALSE_SETTING;
    }

    public boolean textSubstitution(){
	return textSub == TRUE_SETTING;
    }

    public void textSubstitution(boolean evaluate){
	textSub = evaluate ? TRUE_SETTING : FALSE_SETTING;
    }

    class CompilationResolver extends PrefixResolver implements Resolver {
	private org.w3c.dom.Node ctxt;

	CompilationResolver(org.w3c.dom.Node ctxt) {
	    this.ctxt = ctxt;
	}

	public String getNamespaceURI(String prefix) {
	    if(prefix.equals(XMLNS_PREFIX))
		return XMLNS_NAMESPACE;
	    return resolve(prefix, ctxt);
	}

	private String resolve(String prefix, org.w3c.dom.Node ctxt) {
	    if(ctxt == null)
		return null;

	    NamedNodeMap nnm = ctxt.getAttributes();
	    String ns = resolve(prefix, nnm);
	    if(ns != null)
		return ns;

	    // look upwards
	    return resolve(prefix, ctxt.getParentNode());
	}

	private String resolve(String prefix, NamedNodeMap nnm) {
	    if(nnm == null)
		return null;
	    for(int i = 0; i < nnm.getLength(); i++){
		org.w3c.dom.Node attr = nnm.item(i);
		String aname = attr.getNodeName();
// 		if(XMLNS_PREFIX.equals(aname)){
// 		    if(prefix == null || prefix.equals(""))
// 			return attr.getNodeValue();
// 		}else{
		int index = aname.indexOf(':');
		if(index > 0 && aname.substring(0, index).equals(XMLNS_PREFIX)
		   && prefix.equals(aname.substring(index + 1))){
		    return attr.getNodeValue();
// 		    }
		}
	    }
	    return null;
	}

	public SourceLocator getLocation(){
	    return new FileLocator(program.getSystemId());
	}
    }

    public CompilationContext(Program program){
	this.program = program;
        mappings = new HashMap();
        parser = new ExpressionParser();
        mixedparser = new MixedExpressionParser();
    }

    public void init(){

        setMapping(new Name(OXML_NS, "procedure"), new ProcedureMapping());
        setMapping(new Name(OXML_NS, "function"), new FunctionMapping());
        setMapping(new Name(OXML_NS, "choose"), new ChooseMapping());
        setMapping(new Name(OXML_NS, "while"), new WhileMapping());
        setMapping(new Name(OXML_NS, "variable"), new VariableMapping());
        setMapping(new Name(OXML_NS, "set"), new VariableMapping());
        setMapping(new Name(OXML_NS, "do"), new DoMapping());
        setMapping(new Name(OXML_NS, "eval"), new EvalMapping());
        setMapping(new Name(OXML_NS, "for-each"), new ForEachMapping());
        setMapping(new Name(OXML_NS, "if"), new IfMapping());
        setMapping(new Name(OXML_NS, "log"), new LogMapping());
        setMapping(new Name(OXML_NS, "type"), new TypeMapping());
        setMapping(new Name(OXML_NS, "return"), new ReturnMapping());
        setMapping(new Name(OXML_NS, "element"), new DynamicElementMapping());
        setMapping(new Name(OXML_NS, "attribute"), new AttributeMapping());
        setMapping(new Name(OXML_NS, "processing-instruction"), new ProcessingInstructionMapping());
        setMapping(new Name(OXML_NS, "comment"), new CommentMapping());
        setMapping(new Name(OXML_NS, "text"), new TextMapping());
        setMapping(new Name(OXML_NS, "set"), new SetMapping());
        setMapping(new Name(OXML_NS, "import"), new ImportMapping());
        setMapping(new Name(OXML_NS, "catch"), new CatchMapping());
        setMapping(new Name(OXML_NS, "throw"), new ThrowMapping());
        setMapping(new Name(OXML_NS, "assert"), new AssertMapping());
        setMapping(new Name(OXML_NS, "program"), new ProgramMapping());
        setMapping(new Name(OXML_NS, "thread"), new ThreadMapping());
        setMapping(new Name(OXML_NS, "sort"), new SortMapping());
        setMapping(new Name(OXML_NS, "document"), new DocumentMapping());
        setMapping(new Name(OXML_NS, "module"), new ModuleMapping());
        setMapping(new Name(OXML_NS, "meta"), new MetaMapping());
    }

    public ProgramTemplate getProgramTemplate(){
	return progtemplate;
    }

    public void setProgramTemplate(ProgramTemplate program)
	throws ObjectBoxException{
	if(progtemplate != null)
	    throw new ObjectBoxException
		("program already declared, there can be only one program in a program file");
	progtemplate = program;
    }

    public String getSystemId(){
	return program.getSystemId();
    }

    public Type getType(Name name)
	throws ObjectBoxException {
	return program.getType(name);
    }

    public boolean hasType(Name name){
        return program.hasType(name);
    }

    public void addType(Type type)
	throws ObjectBoxException {
	program.addType(type);
    }

    public void addFunction(Function function)
	throws ObjectBoxException {
        program.addFunction(function);
    }

    public void setMapping(Name name, TemplateMapping mapping){
        mappings.put(name, mapping);
    }

    public TemplateMapping getMapping(Name name){
        return (TemplateMapping)mappings.get(name);
    }

    public Resolver getResolver(org.w3c.dom.Node ctxt){
	return new CompilationResolver(ctxt);
    }

    public Template compile(org.w3c.dom.Node node)
        throws ObjectBoxException{
	return getTemplate(node);
    }

    protected Template getTemplate(org.w3c.dom.Node node)
        throws ObjectBoxException{

	switch(node.getNodeType()){
	case org.w3c.dom.Node.ELEMENT_NODE: 
	    Element e = (Element)node;
	    Name key = new Name(e.getNamespaceURI(), e.getLocalName());
	    TemplateMapping mapping = (TemplateMapping)mappings.get(key);
	    if(mapping == null){
		if(key.getNamespaceURI().equals(OXML_NS))
		    Log.warning("unhandled o:XML element: "+key.getLocalName());
		mapping = elementMapping;
		// we've entered a literal element, no longer mapped context
		mappedContext = false;
	    }else{
		// we've entered a mapped element, no longer literal XML context
		mappedContext = true;
	    }
	    return mapping.map(e, this);
	case org.w3c.dom.Node.COMMENT_NODE:
	    if(ignoreComments())
		return noTemplate;
	    else
		return new CommentTemplate
		    (new LiteralExpression(node.getNodeValue()));
	case org.w3c.dom.Node.TEXT_NODE:
	case org.w3c.dom.Node.CDATA_SECTION_NODE:
	    if(ignoreWhitespace() && node.getNodeValue().trim().equals("")){
		// ignore whitespace
		return noTemplate;
	    }else if(textSubstitution()){
		// substitute text expressions
		Expression txt = evaluate(node.getNodeValue());
		txt.bind(getResolver(node));
		return new TextTemplate(txt);
	    }else{
		// literal text
		return new StringTemplate(node.getNodeValue());
	    }
	case org.w3c.dom.Node.PROCESSING_INSTRUCTION_NODE:
	    return new ProcessingInstructionTemplate
		(node.getNodeName(), node.getNodeValue());
	case org.w3c.dom.Node.DOCUMENT_NODE:	    
	    return docMapping.map((Document)node, this);
	case org.w3c.dom.Node.DOCUMENT_TYPE_NODE:
	    return noTemplate;
	case org.w3c.dom.Node.DOCUMENT_FRAGMENT_NODE:
	    return getBody(node);
	default:
	    Log.warning("unhandled node: "+node);
	    return noTemplate;
	}
    }

    public Template getBody(org.w3c.dom.Node node)
        throws ObjectBoxException {

        List actions = new ArrayList();

        for(org.w3c.dom.Node kid=node.getFirstChild(); kid != null; 
            kid = kid.getNextSibling()){
            Template action = getTemplate(kid);
            if(action != noTemplate)
                actions.add(action);
        }
        switch(actions.size()){
            case 0:
                return noTemplate;
            case 1:
                return (Template)actions.get(0);
            default:
                return new CompoundTemplate(actions);
        }
    }

    /**
     * parse a ObjectBox expression
     */
    public Expression parse(String expr)
        throws ObjectBoxException{
	return parser.parse(expr);
    }

    public Expression evaluate(String mixed)
        throws ObjectBoxException{
	return mixedparser.parse(mixed);
    }

//     public List getConstructors(){
// 	List ctors = new ArrayList();
// 	Iterator it = types.values().iterator();
// 	while(it.hasNext()){
// 	    Type type = (Type)it.next();
// 	    Function[] ctor = type.getConstructors();
// 	    for(int i=0; ctor != null && i<ctor.length; ++i)
// 		ctors.add(ctor[i]);
// 	}
// 	return ctors;
//     }

    HashSet imported = new HashSet();

    public Template importURL(URL url)
	throws ObjectBoxException, SAXException, IOException {
	if(imported.contains(url))
	    return null; // cyclic imports!

	Document doc = getDocument(url);
	imported.add(url);
	Log.trace("importing: "+url);
	String saved = program.getSystemId();
	program.setSystemId(url.toString());
	try{
	    Template imported = compile(doc);
	    return imported;
	}finally{
	    program.setSystemId(saved);
	}
    }

    protected Document getDocument(URL url)
        throws SAXException, IOException {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	factory.setNamespaceAware(true);
	Document xml;
	try {
	    xml = factory.newDocumentBuilder().parse(url.toString());
	}catch(ParserConfigurationException exc){
	    throw new SAXException(exc);
	}
	return xml;
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
