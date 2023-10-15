<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:o="http://www.o-xml.org/lang/"
  xmlns:redirect="org.apache.xalan.lib.Redirect"
  extension-element-prefixes="redirect">

  <!-- xmlns:redirect="http://xml.apache.org/xalan/redirect" -->

  <xsl:output method="text" indent="no" encoding="UTF-8"/>

  <xsl:template match="o:function" mode="javaname">
    <xsl:value-of select="concat(../@name, '_', @name)"/>
    <xsl:apply-templates select="o:param" mode="javaname"/>
  </xsl:template>

  <xsl:template match="o:param" mode="javaname">
    <xsl:text>_</xsl:text>
    <xsl:value-of select="@type"/>
    <xsl:if test="not(@type)">Node</xsl:if>
  </xsl:template>

  <xsl:template match="o:param" mode="signature">
    <xsl:value-of select="@type"/>
    <xsl:text>Node.TYPE</xsl:text>
    <!-- 
    <xsl:choose>
      <xsl:when test="@type">
        <xsl:value-of select="concat(@type, '.TYPE')"/>
      </xsl:when>
      <xsl:otherwise>Node.TYPE</xsl:otherwise>
    </xsl:choose>
    -->
    <xsl:if test="following-sibling::o:param">, </xsl:if>
  </xsl:template>

  <xsl:template match="o:param[@type != 'Node']">
    <xsl:text>      </xsl:text>
    <xsl:value-of select="@type"/>Node <xsl:value-of select="@name"/>
    <xsl:text> = </xsl:text>
    <!-- downcasting -->
    <xsl:value-of select="concat('(', @type, 'Node)')"/>
    <xsl:text>args[</xsl:text>
    <xsl:value-of select="position()-1"/>
    <xsl:text>];&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="o:param">
    <xsl:text>      Node </xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text> = args[</xsl:text>
    <xsl:value-of select="position()-1"/>
    <xsl:text>];&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="text()"/>

  <xsl:template match="@implements">
    <xsl:text> implements </xsl:text>
    <xsl:value-of select="."/>
  </xsl:template>    

  <xsl:template match="@extends">
    <xsl:text> extends </xsl:text>
    <xsl:value-of select="."/>
  </xsl:template>    

  <xsl:template match="o:type[java]">
    <redirect:write select="concat(java/@classname, '.java')">
package org.oXML.type;

import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

/**
 *  ObjectBox - o:XML compiler and interpretor
 *  for more information see http://www.o-xml.org/objectbox
 *  Copyright (C) 2002/2003 Martin Klang, Alpha Plus Technology Ltd
 *  email: martin at hack.org
 *  This class has been generated from an o:XML template
 */
public class <xsl:value-of select="java/@classname"/>
   <xsl:apply-templates select="java/@extends"/>
   <xsl:apply-templates select="java/@implements"/> {

    public static final Type TYPE = new Type(
      new Name("<xsl:value-of select="@name"/>"), new Type[]{
<xsl:apply-templates select="o:parent"/>
      });
    <xsl:value-of select="java"/>

    static {
<xsl:apply-templates select="o:function"/>
    }
}
    </redirect:write>
  </xsl:template>

  <xsl:template match="o:parent">
    <xsl:value-of select="@name"/>
    <xsl:if test="@name != 'Node'">Node</xsl:if>  
    <xsl:text>.TYPE</xsl:text>
    <xsl:if test="following-sibling::o:parent">, </xsl:if>
  </xsl:template>

  <!-- ctor -->
  <xsl:template match="o:function[@name = parent::o:type/@name]">
    <xsl:variable name="class">
      <xsl:apply-templates select="." mode="javaname"/>
    </xsl:variable>
    <xsl:variable name="declaringType">
      <xsl:if test="not(parent::o:type/java/@classname)">
        <xsl:value-of select="parent::o:type/@name"/>Node</xsl:if>
      <xsl:value-of select="parent::o:type/java/@classname"/>
    </xsl:variable>

    <!-- add an instance to the owning types constructor list  -->
    <xsl:text>TYPE.addConstructor(new </xsl:text>
    <xsl:value-of select="$class"/>());

    <redirect:write select="concat($class, '.java')">
package org.oXML.type;

import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

/**
 *  ObjectBox - o:XML compiler and interpretor
 *  for more information see http://www.o-xml.org/objectbox
 *  Copyright (C) 2002/2003 Martin Klang, Alpha Plus Technology Ltd
 *  email: martin at hack.org
 *  This class has been generated from an o:XML template
 */
public class <xsl:value-of select="$class"/>
    <xsl:text> extends ConstructorFunction {</xsl:text>

    public <xsl:value-of select="$class"/>() {
      <xsl:text>super(</xsl:text>
      <xsl:value-of select="$declaringType"/>
      <xsl:text>.TYPE, new Type[]{</xsl:text>
      <xsl:apply-templates select="o:param" mode="signature"/>
      <xsl:text>});</xsl:text>
    }

    public Node construct(Type type, Node[] args, RuntimeContext context)
      throws ObjectBoxException {
      if(args.length != <xsl:value-of select="count(o:param)"/>)
        throw new FunctionException(getName()+" takes <xsl:value-of select="count(o:param)"/> arguments");
      <xsl:apply-templates select="o:param"/>
      <xsl:apply-templates select="java"/>
    }
}
    </redirect:write>    
  </xsl:template>

  <!-- type function -->
  <xsl:template match="o:function">

    <xsl:variable name="class">
      <xsl:apply-templates select="." mode="javaname"/>
    </xsl:variable>

    <!-- load an instance to the owning types function list  -->
    <xsl:text>TYPE.addFunction(new </xsl:text>
    <xsl:value-of select="$class"/>());

    <xsl:variable name="declaringType">
      <xsl:if test="not(parent::o:type/java/@classname)">
        <xsl:value-of select="parent::o:type/@name"/>Node</xsl:if>
      <xsl:value-of select="parent::o:type/java/@classname"/>
    </xsl:variable>

    <redirect:write select="concat($class, '.java')">
package org.oXML.type;

import org.oXML.engine.RuntimeContext;
import org.oXML.xpath.XPathContext;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;

/**
 *  ObjectBox - o:XML compiler and interpretor
 *  for more information see http://www.o-xml.org/objectbox
 *  Copyright (C) 2002/2003 Martin Klang, Alpha Plus Technology Ltd
 *  email: martin at hack.org
 *  This class has been generated from an o:XML template
 */
public class <xsl:value-of select="$class"/> extends TypeFunction {

    <xsl:text>public static final Name NAME = new Name("</xsl:text>
    <xsl:value-of select="@name"/>");

    public <xsl:value-of select="$class"/>() {
    <xsl:text>super(</xsl:text>
    <xsl:value-of select="$declaringType"/>
    <xsl:text>.TYPE, NAME, new Type[]{</xsl:text>
      <xsl:apply-templates select="o:param" mode="signature"/>
      <xsl:text>});</xsl:text>
    }

    public Node invoke(Node instance, Node[] args, RuntimeContext context)
      throws ObjectBoxException {
      if(args.length != <xsl:value-of select="count(o:param)"/>)
        throw new FunctionException(getName()+" takes <xsl:value-of select="count(o:param)"/> arguments");
      <xsl:variable name="type">
        <xsl:value-of select="parent::o:type/@name"/>
        <xsl:if test="parent::o:type/@name != 'Node'">Node</xsl:if>
      </xsl:variable>
      <!-- downcast -->
      <xsl:value-of select="$type"/>
      <xsl:text> target = (</xsl:text>
      <xsl:value-of select="$type"/>
      <xsl:text>)instance;&#10;</xsl:text>
      <xsl:apply-templates select="o:param"/>
      <xsl:apply-templates select="java"/>
    }
}
    </redirect:write>
  </xsl:template>

  <xsl:template match="java">
    <xsl:apply-templates mode="java"/>
  </xsl:template>

  <xsl:template match="text()"/>

  <xsl:template match="text()" mode="java">
    <xsl:value-of select="."/>
  </xsl:template>

</xsl:stylesheet>
<!--
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
-->
