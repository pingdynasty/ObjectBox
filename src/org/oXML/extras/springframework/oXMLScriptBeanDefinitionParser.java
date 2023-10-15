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

import java.util.List;

import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.scripting.ScriptFactory;
import org.springframework.scripting.support.ScriptFactoryPostProcessor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * BeanDefinitionParser implementation for the '<code>&lt;script:oxml/&gt;</code>' tags.
 * Allows for objects written using o:XML to be easily exposed with
 * the {@link org.springframework.beans.factory.BeanFactory}.
 *
 * <p>The script for each object can be specified either as a reference to the Resource
 * containing it (using the '<code>script-source</code>' attribute) or inline in the XML configuration
 * itself (using the '<code>inline-script</code>' attribute.
 *
 * <p>By default, dynamic objects created with these tags are <strong>not</strong> refreshable.
 * To enable refreshing, specify the refresh check delay for each object (in milliseconds) using the
 * '<code>refresh-check-delay</code>' attribute.
 *
 * @author Rob Harrop
 * @author Rod Johnson
 * @author Martin Klang
 */

class oXMLScriptBeanDefinitionParser extends AbstractBeanDefinitionParser {

    /**
     * The unique name under which the internally managed {@link ScriptFactoryPostProcessor} is
     * registered in the {@link BeanDefinitionRegistry}.
     */
    private static final String SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME = ".scriptFactoryPostProcessor";

    private static final String SCRIPT_SOURCE_ATTRIBUTE = "script-source";

    private static final String INLINE_SCRIPT_ELEMENT = "inline-script";

    private static final String SCRIPT_INTERFACES_ATTRIBUTE = "script-interfaces";

    private static final String REFRESH_CHECK_DELAY_ATTRIBUTE = "refresh-check-delay";
	
    private static final String CUSTOMIZER_REF_ATTRIBUTE = "customizer-ref";


    /**
     * The {@link org.springframework.scripting.ScriptFactory} class that this
     * parser instance will create bean definitions for.
     */
    private final Class scriptFactoryClass;


    /**
     * Creates a new instance of this class that creates bean definitions
     * for the supplied {@link org.springframework.scripting.ScriptFactory} class.
     */
    public oXMLScriptBeanDefinitionParser(Class scriptFactoryClass) {
        Assert.isAssignable(ScriptFactory.class, scriptFactoryClass);
        this.scriptFactoryClass = scriptFactoryClass;
    }


    /**
     * Parses the dynamic object element and returns the resulting bean definition.
     * Registers a {@link ScriptFactoryPostProcessor} if needed.
     */
    protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
        // Resolve the script source.
        String value = resolveScriptSource(element, parserContext.getReaderContext());
        if (value == null) {
            return null;
        }

        // Set up infrastructure.
        registerScriptFactoryPostProcessorIfNecessary(parserContext.getRegistry());
        RootBeanDefinition beanDefinition = new RootBeanDefinition(this.scriptFactoryClass);
        beanDefinition.setSource(parserContext.extractSource(element));

        // Attach any refresh metadata.
        parseRefreshMetadata(element, beanDefinition);

        // Add constructor arguments.
        ConstructorArgumentValues cav = beanDefinition.getConstructorArgumentValues();
        int constructorArgNum = 0;
        cav.addIndexedArgumentValue(constructorArgNum++, value);
        if (element.hasAttribute(SCRIPT_INTERFACES_ATTRIBUTE)) {
            cav.addIndexedArgumentValue(constructorArgNum++, element.getAttribute(SCRIPT_INTERFACES_ATTRIBUTE));
        }
		
        // add any property definitions that need adding.
        parserContext.getDelegate().parsePropertyElements(element, beanDefinition);

        return beanDefinition;
    }

    /**
     * Parses the value of the '<code>refresh-check-delay</code>' attribute and
     * attaches it to the BeanDefinition metadata.
     * @see ScriptFactoryPostProcessor#REFRESH_CHECK_DELAY_ATTRIBUTE
     */
    private void parseRefreshMetadata(Element element, RootBeanDefinition beanDefinition) {
        String refreshCheckDelay = element.getAttribute(REFRESH_CHECK_DELAY_ATTRIBUTE);
        if (StringUtils.hasText(refreshCheckDelay)) {
            beanDefinition.setAttribute(
					ScriptFactoryPostProcessor.REFRESH_CHECK_DELAY_ATTRIBUTE, new Long(refreshCheckDelay));
        }
    }

    /**
     * Resolves the script source from either the '<code>script-source</code>' attribute or
     * the '<code>inline-script</code>' element. Logs and {@link XmlReaderContext#error} and
     * returns <code>null</code> if neither or both of these values are specified.
     */
    private String resolveScriptSource(Element element, XmlReaderContext readerContext) {
        boolean hasScriptSource = element.hasAttribute(SCRIPT_SOURCE_ATTRIBUTE);
        List elements = DomUtils.getChildElementsByTagName(element, INLINE_SCRIPT_ELEMENT);
        if (hasScriptSource && !elements.isEmpty()) {
            readerContext.error("Only one of 'script-source' and 'inline-script' should be specified.", element);
            return null;
        }
        else if (hasScriptSource) {
            return element.getAttribute(SCRIPT_SOURCE_ATTRIBUTE);
        }
        else if (!elements.isEmpty()) {
            Element inlineElement = (Element) elements.get(0);
            return "inline:" + DomUtils.getTextValue(inlineElement);
        }
        else {
            readerContext.error("Must specify either 'script-source' or 'inline-script'.", element);
            return null;
        }
    }

    /**
     * Registers a {@link ScriptFactoryPostProcessor} bean definition in the supplied
     * {@link BeanDefinitionRegistry} if the {@link ScriptFactoryPostProcessor} hasn't
     * already been registered.
     */
    private static void registerScriptFactoryPostProcessorIfNecessary(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME)) {
            RootBeanDefinition beanDefinition = new RootBeanDefinition(ScriptFactoryPostProcessor.class);
            registry.registerBeanDefinition(SCRIPT_FACTORY_POST_PROCESSOR_BEAN_NAME, beanDefinition);
        }
    }
}
