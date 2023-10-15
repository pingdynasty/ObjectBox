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

import junit.framework.TestCase;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.dynamic.Refreshable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.JdkVersion;
import org.springframework.core.NestedRuntimeException;
import org.springframework.scripting.ScriptCompilationException;

import java.io.StringReader;
import org.w3c.dom.Node;
import org.w3c.dom.Document;

import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Unit tests for the oXMLScriptFactory class.
 *
 */
public class oXMLScriptFactoryTests extends TestCase {

    private static final String OXML_SCRIPT_SOURCE_LOCATOR =
        "inline:<![CDATA[\n"+
        "<o:do xmlns:o=\"http://www.o-xml.org/lang/\">\n"+
        "<o:return select=\"'o:XML string object'\"\n"+
        "</o:do>]]>\n";

    private static final String VALID_DOM_INPUT =
        "<data>\n"+
        "<item>un</item>\n"+
        "<item>deux</item>\n"+
        "<item>trois</item>\n"+
        "<item>quatre</item>\n"+
        "</data>";

    private static final String INVALID_DOM_INPUT =
        "<data>\n"+
        "<item>uno</item>\n"+
        "<item>due</item>\n"+
        "</data>\n";


    public void testStatic() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }

        ApplicationContext ctx =
            new ClassPathXmlApplicationContext("org/oXML/extras/springframework/oxmlContext.xml");
        Calculator calc = (Calculator) ctx.getBean("calculator");
        Messenger messenger = (Messenger) ctx.getBean("messenger");

        assertFalse("Scripted object should not be instance of Refreshable", calc instanceof Refreshable);
        assertFalse("Scripted object should not be instance of Refreshable", messenger instanceof Refreshable);

        assertEquals("Calculator can't add", calc.add(1, 1), 2);

        String desiredMessage = "Hello o:XML!";
        assertEquals("Message is incorrect", desiredMessage, messenger.getMessage());
    }

    public void testNonStatic() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }

        ApplicationContext ctx =
            new ClassPathXmlApplicationContext("org/oXML/extras/springframework/oxmlRefreshableContext.xml");
        Messenger messenger = (Messenger) ctx.getBean("messenger");

        assertTrue("Should be a proxy for refreshable scripts", AopUtils.isAopProxy(messenger));
        assertTrue("Should be an instance of Refreshable", messenger instanceof Refreshable);

        String desiredMessage = "Hello o:XML!";
        assertEquals("Message is incorrect.", desiredMessage, messenger.getMessage());

        Refreshable refreshable = (Refreshable) messenger;
        refreshable.refresh();

        assertEquals("Message is incorrect after refresh.", desiredMessage, messenger.getMessage());
        assertEquals("Incorrect refresh count", 2, refreshable.getRefreshCount());
    }

    public void testDOMArguments() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }
        ApplicationContext ctx =
            new ClassPathXmlApplicationContext("org/oXML/extras/springframework/oxmlDOMContext.xml");
        Node input = parseText(VALID_DOM_INPUT);
        Converter converter = (Converter)ctx.getBean("converter");
        converter.validate(input);
        Node result = converter.convert(input);
        assertNotNull("null return value", result);
        // DOM sure makes you appreciate XPath...
        assertNotNull("incomplete return value", result.getFirstChild().getFirstChild().getNextSibling().getFirstChild());
        assertEquals("invalid conversion", result.getFirstChild().getFirstChild().getNextSibling().getFirstChild().getNodeValue(), "deux");
    }

    public void testUserException() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }
        ApplicationContext ctx =
            new ClassPathXmlApplicationContext("org/oXML/extras/springframework/oxmlDOMContext.xml");
        Node input = parseText(INVALID_DOM_INPUT);
        Converter converter = (Converter)ctx.getBean("converter");
        try{
            converter.validate(input);
            fail("Should throw user exception");
        }catch(Exception exc){
            assertTrue("Invalid user exception thrown", exc instanceof org.oXML.engine.ProgramException);
        }        
    }

    public void testJavaInteroperability() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }
        ApplicationContext ctx =
            new ClassPathXmlApplicationContext("org/oXML/extras/springframework/oxmlJavaContext.xml");
        // mutator is an o:XML bean
        MessageMutator mutator = (MessageMutator)ctx.getBean("mutator");
        // messenger is a Java bean
        Messenger messenger = mutator.createMessenger();
        assertEquals(messenger.getMessage(), "Hello Java!");
        mutator.mutate(messenger);
        assertEquals(messenger.getMessage(), "Greetings Java!");
    }

    public void testRubyInteroperability() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }
        ApplicationContext ctx =
            new ClassPathXmlApplicationContext("org/oXML/extras/springframework/oxmlRubyContext.xml");
        // messenger is a JRuby bean
        Messenger messenger = (Messenger)ctx.getBean("messenger");
        assertEquals(messenger.getMessage(), "Hello Ruby!");
        // mutator is an o:XML bean
        MessageMutator mutator = (MessageMutator)ctx.getBean("mutator");
        mutator.mutate(messenger);
        assertEquals(messenger.getMessage(), "Greetings Ruby!");
    }

    public void testScriptCompilationException() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }

        try {
            new ClassPathXmlApplicationContext("org/oXML/extras/springframework/oxmlBrokenContext.xml");
            fail("Should throw exception for broken script file");
        }
        catch (NestedRuntimeException e) {
            assertTrue(e.contains(ScriptCompilationException.class));
        }
    }

    public void testInvalidScript() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }

        try {
            new ClassPathXmlApplicationContext("org/oXML/extras/springframework/oxmlInvalidContext.xml");
            fail("Should throw exception for invalid script file");
        }
        catch (NestedRuntimeException e) {
            assertTrue(e.contains(ScriptCompilationException.class));
        }
    }

    public void testWhereScriptDoesNotReturnNewObject() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }

        try {
            new ClassPathXmlApplicationContext("org/oXML/extras/springframework/oxmlDoesNotReturnObjectContext.xml");
            fail("Must throw exception for script file that does not have '<o:return ...' as last statement");
        }
        catch (NestedRuntimeException expected) {
            expected.printStackTrace();
            assertTrue(expected.contains(ScriptCompilationException.class));
        }
    }

    public void testCtorWithNullScriptSourceLocator() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }

        try {
            new oXMLScriptFactory(null, new Class[] {Messenger.class});
            fail("Must have thrown exception by this point.");
        }
        catch (IllegalArgumentException expected) {
            expected.printStackTrace();
        }
    }

    public void testCtorWithEmptyScriptSourceLocator() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }

        try {
            new oXMLScriptFactory("", new Class[] {Messenger.class});
            fail("Must have thrown exception by this point.");
        }
        catch (IllegalArgumentException expected) {
        }
    }

    public void testCtorWithWhitespacedScriptSourceLocator() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }

        try {
            new oXMLScriptFactory("\n   ", new Class[] {Messenger.class});
            fail("Must have thrown exception by this point.");
        }
        catch (IllegalArgumentException expected) {
        }
    }

    public void testCtorWithNullScriptInterfacesArray() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }

        try {
            new oXMLScriptFactory(OXML_SCRIPT_SOURCE_LOCATOR, null);
            fail("Must have thrown exception by this point.");
        }
        catch (IllegalArgumentException expected) {
        }
    }

    public void testCtorWithEmptyScriptInterfacesArray() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }

        try {
            new oXMLScriptFactory(OXML_SCRIPT_SOURCE_LOCATOR, new Class[] {});
            fail("Must have thrown exception by this point.");
        }
        catch (IllegalArgumentException expected) {
        }
    }

    private Document parseText(String text)
        throws Exception {
        InputSource source = new InputSource(new StringReader(text));
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
    }

    public void testResourceScriptFromTag() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }

        ApplicationContext ctx = new ClassPathXmlApplicationContext("oxml-with-xsd.xml", getClass());
        Messenger messenger = (Messenger) ctx.getBean("messenger");
        assertEquals("Hello o:XML!", messenger.getMessage());
        assertFalse(messenger instanceof Refreshable);
    }

    public void testInlineScriptFromTag() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }

        ApplicationContext ctx = new ClassPathXmlApplicationContext("oxml-with-xsd.xml", getClass());
        Calculator calculator = (Calculator) ctx.getBean("calculator");
        assertNotNull(calculator);
        assertFalse(calculator instanceof Refreshable);
    }

    public void testRefreshableFromTag() throws Exception {
        if (JdkVersion.getMajorJavaVersion() < JdkVersion.JAVA_14) {
            return;
        }

        ApplicationContext ctx = new ClassPathXmlApplicationContext("oxml-with-xsd.xml", getClass());
        Messenger messenger = (Messenger) ctx.getBean("refreshableMessenger");
        assertEquals("Hello o:XML!", messenger.getMessage());
        assertTrue("Messenger should be Refreshable", messenger instanceof Refreshable);
    }
}
