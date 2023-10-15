<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:java="http://www.o-xml.org/java"
                xmlns:o="http://www.o-xml.org/lang/">

  <xsl:output method="text" indent="no" encoding="UTF-8"/>

  <!-- utility templates  -->

  <!-- naive XInclude implementation -->
  <xsl:template match="xi:include" 
                xmlns:xi="http://www.w3.org/2001/XInclude">
    <xsl:apply-templates select="document(@href)/*"/>
  </xsl:template>

  <xsl:template match="/">
    <xsl:choose>
      <xsl:when test="*/@java:package">
        <xsl:text>package </xsl:text>
        <xsl:value-of select="*/@java:package"/>;
      </xsl:when>
      <xsl:otherwise>package org.oXML.type;</xsl:otherwise>
    </xsl:choose>

    <xsl:call-template name="imports"/>

  /**
    *  ObjectBox - o:XML compiler and interpretor
    *  for more information see http://www.o-xml.org/objectbox
    *  Copyright (C) 2002/2003 Martin Klang, Alpha Plus Technology Ltd
    *  email: martin at hack.org
    *  This class has been generated from an o:XML template
    */
    <xsl:apply-templates/>
  </xsl:template>

  <!-- create Name with or without namespace -->
  <xsl:template match="node()|@*" mode="name">
    <xsl:variable name="prefix" select="substring-before(., ':')"/>
    <xsl:text>new Name("</xsl:text>
    <xsl:choose>
      <xsl:when test="$prefix">
        <xsl:value-of select="ancestor-or-self::*/namespace::node()[name(.) = $prefix]"/>
        <xsl:text>", "</xsl:text>
        <xsl:value-of select="substring-after(., ':')"/>
      </xsl:when>
      <xsl:otherwise>
        <xsl:value-of select="."/>
      </xsl:otherwise>
    </xsl:choose>
    <xsl:text>")</xsl:text>
  </xsl:template>

  <xsl:template match="o:type" mode="name">
    <xsl:apply-templates select="." mode="javaclass"/>
    <xsl:text>.TYPE.getName()</xsl:text>
  </xsl:template>

  <xsl:template match="o:param" mode="name">
    <!-- default parameter type name -->
    <xsl:text>NodeNode.TYPE.getName()</xsl:text>
  </xsl:template>

  <xsl:template match="o:param[@type]" mode="name">
    <!-- explicit parameter type name -->
    <xsl:apply-templates select="@type" mode="type"/>
    <xsl:text>.getName()</xsl:text>
  </xsl:template>

  <!-- type templates: get Java Type object representing type -->
  <xsl:template match="node()|@*" mode="type">
    <xsl:value-of select="."/>
    <xsl:text>Node.TYPE</xsl:text>
    <!-- should do some clever lookup somewhere -->
<!-- disabled in favour of static type refs
    <xsl:text>(Type)types.get(</xsl:text>
    <xsl:apply-templates select="." mode="name"/>
    <xsl:text>)</xsl:text>
-->
  </xsl:template>

  <xsl:template match="o:function" mode="javaname">
    <xsl:value-of select="translate(@name, '-', '_')"/>
  </xsl:template>

  <xsl:template match="o:function[@java:name]" mode="javaname">
    <xsl:value-of select="@java:name"/>
  </xsl:template>

  <xsl:template match="o:param" mode="javaname">
    <xsl:value-of select="@name"/>
  </xsl:template>

  <xsl:template match="o:param" mode="types">
    <xsl:apply-templates select="." mode="type"/>
    <xsl:if test="following-sibling::o:param">, </xsl:if>
  </xsl:template>
    
  <xsl:template match="o:param" mode="type">
    <!-- default parameter type -->
    <xsl:text>NodeNode.TYPE</xsl:text>
  </xsl:template>

  <xsl:template match="o:param[@type]" mode="type">
    <xsl:apply-templates select="@type" mode="type"/>
  </xsl:template>

  <xsl:template match="o:param" mode="signature">
    <xsl:apply-templates select="." mode="javaclass"/>
    <xsl:text> </xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:if test="following-sibling::o:param">, </xsl:if>
  </xsl:template>

  <xsl:template match="o:param[@min]" mode="signature">
<!--    <xsl:apply-templates select="." mode="javaclass"/> -->
    <xsl:text>Node[] </xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:if test="following-sibling::o:param">, </xsl:if>
  </xsl:template>

  <!-- javaclass templates: get Java class / classname -->
  <xsl:template match="o:function" mode="javaclass">
    <xsl:value-of select="@name"/>
    <xsl:text>Function</xsl:text>
  </xsl:template>

  <xsl:template match="o:function[@java:name]" mode="javaclass">
    <xsl:value-of select="translate(@java:name, '_-', '')"/>
    <xsl:text>Function</xsl:text>
  </xsl:template>

  <xsl:template match="o:param" mode="javaclass">
    <xsl:text>Node</xsl:text><!-- default parameter class -->
  </xsl:template>

  <xsl:template match="o:param[@type]" mode="javaclass">
    <xsl:value-of select="@type"/>
    <xsl:text>Node</xsl:text>
  </xsl:template>

  <xsl:template match="o:param[@java:class]" mode="javaclass">
    <xsl:value-of select="@java:class"/>
  </xsl:template>

  <xsl:template match="o:type" mode="javaclass">
    <xsl:value-of select="@name"/>
    <xsl:text>Node</xsl:text>
  </xsl:template>

  <xsl:template match="o:type[@java:class]" mode="javaclass">
    <xsl:value-of select="@java:class"/>
  </xsl:template>

  <xsl:template match="o:parent" mode="javaclass">
    <xsl:value-of select="@name"/>
    <xsl:text>Node</xsl:text>
  </xsl:template>

  <xsl:template match="o:parent[@java:class]" mode="javaclass">
    <xsl:value-of select="@java:class"/>
  </xsl:template>

  <xsl:template match="o:module" mode="javaclass">
    <xsl:value-of select="@name"/>
    <xsl:text>Module</xsl:text>
  </xsl:template>

  <xsl:template match="o:module[@java:class]" mode="javaclass">
    <xsl:value-of select="@java:class"/>
  </xsl:template>

  <xsl:template match="o:variable" mode="javaclass">
    <xsl:text>Node</xsl:text>
  </xsl:template>

  <xsl:template match="o:variable[@type]" mode="javaclass">
    <xsl:value-of select="@type"/>
    <xsl:text>Node</xsl:text>
  </xsl:template>

  <xsl:template match="o:variable[@java:class]" mode="javaclass">
    <xsl:value-of select="@java:class"/>
  </xsl:template>

  <!-- function return types -->
  <xsl:template match="o:function" mode="returns">
    <xsl:text>Node</xsl:text>
  </xsl:template>

  <xsl:template match="o:function[@type]" mode="returns">
    <xsl:value-of select="@type"/>
    <xsl:text>Node</xsl:text>
  </xsl:template>

  <xsl:template match="o:function[@java:class]" mode="returns">
    <xsl:value-of select="@java:class"/>
  </xsl:template>

  <!-- function exception declaration -->
  <xsl:template match="@java:throws">
    <xsl:text>throws </xsl:text><xsl:value-of select="."/>
  </xsl:template>

  <!-- downcast function parameter values -->
  <xsl:template match="o:param" mode="cast">
    <!-- produces:
        SomeNode arg1 = (SomeNode)args[0].cast(type1);
    -->
    <xsl:text>&#10;        </xsl:text>
    <xsl:apply-templates select="." mode="javaclass"/>
    <xsl:text> arg</xsl:text>
    <xsl:value-of select="position()"/>
    <xsl:text> = (</xsl:text>
    <xsl:apply-templates select="." mode="javaclass"/>
    <xsl:text>)args[</xsl:text>
    <xsl:value-of select="position() - 1"/>
    <xsl:text>].cast(</xsl:text>
    <xsl:apply-templates select="." mode="type"/>);
  </xsl:template>

  <xsl:template match="o:param[@min]" mode="cast">
    <xsl:text>&#10;        </xsl:text>
    <xsl:text>Node[] arg</xsl:text>
<!--    <xsl:apply-templates select="." mode="javaclass"/>
    <xsl:text> arg</xsl:text>-->
    <xsl:value-of select="position()"/> = args;
  </xsl:template>

  <xsl:template match="o:param[@java:class = 'org.oXML.engine.RuntimeContext']" mode="cast"/>

  <!-- reference function values -->
  <xsl:template match="o:param" mode="args">
    <xsl:text>arg</xsl:text>
    <xsl:value-of select="position()"/>
    <xsl:if test="following-sibling::o:param">, </xsl:if>
  </xsl:template>

  <xsl:template match="o:param[@java:class = 'org.oXML.engine.RuntimeContext']" mode="args">
    <xsl:text>rc</xsl:text>    
    <xsl:if test="following-sibling::o:param">, </xsl:if>
  </xsl:template>

  <xsl:template name="imports">
import org.oXML.type.*;
import org.oXML.engine.RuntimeContext;
import org.oXML.ObjectBoxException;
import org.oXML.util.Log;
  </xsl:template>

  <xsl:template match="text()"/>

  <xsl:template match="java:code">
    <xsl:value-of select="."/>
  </xsl:template>

  <xsl:template match="java:code[@source = '1.4']"/><!-- suppress jdk 1.4 specifics -->

</xsl:stylesheet>
<!--
    ObjectBox - o:XML compiler and interpretor
    for more information see http://www.o-xml.org/objectbox
    Copyright (C) 2002-2004 Martin Klang, Alpha Plus Technology Ltd
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
