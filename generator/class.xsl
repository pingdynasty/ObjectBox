<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:java="http://www.o-xml.org/java"
                xmlns:o="http://www.o-xml.org/lang/">

  <xsl:import href="common.xsl"/>

  <xsl:output method="text" indent="no" encoding="UTF-8"/>

  <xsl:template match="o:type" mode="inheritance">
    <xsl:if test="not(o:parent/@java:class)">
      <xsl:text> extends AbstractNode </xsl:text>
    </xsl:if>
    <xsl:if test="o:parent/@java:class">
      <xsl:text> extends </xsl:text>
      <xsl:value-of select="o:parent/@java:class"/>
    </xsl:if>
    <xsl:for-each select="o:parent[@java:interface]">
      <xsl:text> implements </xsl:text>
      <xsl:value-of select="@java:interface"/>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="o:type">
    <xsl:variable name="classname">
      <xsl:apply-templates select="." mode="javaclass"/>
    </xsl:variable>
public class <xsl:value-of select="$classname"/>
<xsl:apply-templates select="." mode="inheritance"/> {

<xsl:if test="@java:type">
  public static final Type TYPE = <xsl:value-of select="@java:type"/>;
</xsl:if>
<xsl:if test="not(@java:type)">
  public static final Type TYPE = new Type(<xsl:apply-templates select="@name" mode="name"/>);
</xsl:if>

  <xsl:apply-templates select="o:variable"/>

  // constructors
  // called by subtype (not subclass) constructor
  public <xsl:value-of select="$classname"/>(AbstractNode me, Type type){
    super(me, type);
    <xsl:apply-templates select="." mode="create-parents"/>
    <xsl:apply-templates select="o:constructor[not(o:param)]/java:code"/><!-- insert custom default constructor code -->
  }

  <xsl:if test="not(o:constructor[not(o:param)])">
  /** generated default constructor */
  public <xsl:value-of select="$classname"/>(){
    super(TYPE); // i am i
    <xsl:apply-templates select="." mode="create-parents"/>
  }

  /** generated default initialiser */
  public void init(){
    <xsl:apply-templates select="." mode="initialise-parents"/>
  }
  </xsl:if>

  <xsl:apply-templates select="java:code"/><!-- add in any verbatim code -->

  <xsl:apply-templates select="o:constructor|o:function"/>

  // generated copy functions
  public Node copy(boolean deep){
    if(me == this)
      return new <xsl:value-of select="$classname"/>(this, deep);
    else
      return me.copy(deep);
  }

  public AbstractNode copy(AbstractNode me, Type type, boolean deep){
    <xsl:value-of select="$classname"/> copy = new <xsl:value-of select="$classname"/>(me, type);
    copy.init(this, deep);
    return copy;
  }
}
  </xsl:template>

  <xsl:template match="o:type" mode="create-parents">
    <xsl:apply-templates select="o:parent[@name]" mode="create"/>
  </xsl:template>

  <xsl:template match="o:parent[@name]" mode="create">
    if(!hasInstance(<xsl:apply-templates select="@name" mode="name"/>)){
      setInstance(<xsl:apply-templates select="@name" mode="name"/>, new <xsl:apply-templates select="." mode="javaclass"/>(me, getType()));
    }
  </xsl:template>

  <xsl:template match="o:type" mode="initialise-parents">
    <xsl:apply-templates select="o:parent[@name]" mode="initialise"/>
  </xsl:template>

  <xsl:template match="o:constructor" mode="initialise-parents">
    <xsl:apply-templates select="parent::o:type/o:parent[@name]" mode="initialise"/>
    <!-- tbd - explicit parent initialisation -->
  </xsl:template>

  <xsl:template match="o:constructor[o:param/@type = parent::o:type/@name]"
                mode="initialise-parents">
    <xsl:apply-templates select="parent::o:type/o:parent" mode="copy"/>
  </xsl:template>

  <xsl:template match="o:parent[@name]" mode="copy">
    ((<xsl:apply-templates select="." mode="javaclass"/>
    <xsl:text>)getInstance(</xsl:text>
    <xsl:apply-templates select="@name" mode="name"/>
    <xsl:text>)).init((</xsl:text>
    <xsl:apply-templates select="." mode="javaclass"/>
    <xsl:text>)other.getInstance(</xsl:text>
    <xsl:apply-templates select="@name" mode="name"/>), deep);
  </xsl:template>

  <xsl:template match="o:parent[@name]" mode="initialise">
    getInstance(<xsl:apply-templates select="@name" mode="name"/>).init();
  </xsl:template>

  <!-- type function -->
  <xsl:template match="o:function[parent::o:type]">
  public <xsl:apply-templates select="." mode="returns"/>
  <xsl:text> </xsl:text>
  <xsl:apply-templates select="." mode="javaname"/>
  <xsl:text>(</xsl:text>
  <xsl:apply-templates select="o:param" mode="signature"/>)
  <xsl:apply-templates select="@java:throws"/> {
  <xsl:apply-templates select="java:code"/>
  }
  </xsl:template>

  <xsl:template match="o:function[@name = parent::o:type/@name]">
    <xsl:message>invalid constructor: <xsl:value-of select="@name"/></xsl:message>
  </xsl:template>

  <!-- type constructor function -->
  <xsl:template match="o:constructor">
    <xsl:variable name="name">
      <xsl:apply-templates select="parent::o:type" mode="javaclass"/>
    </xsl:variable>
  /** generated constructor, called directly */
  public <xsl:value-of select="$name"/>
  <xsl:text>(</xsl:text>
  <xsl:apply-templates select="o:param" mode="signature"/>)
  <xsl:apply-templates select="@java:throws"/> {
    super(TYPE); // generated call to super constructor
    <xsl:apply-templates select="parent::o:type" mode="create-parents"/>
    init(<xsl:apply-templates select="o:param" mode="args"/>);
  }

  public void init(<xsl:apply-templates select="o:param" mode="signature"/>)
  <xsl:apply-templates select="@java:throws"/> {
    <xsl:apply-templates select="." mode="initialise-parents"/>
    // constructor code
    <xsl:apply-templates select="java:code"/>
  }
  </xsl:template>

  <xsl:template match="o:param" mode="args">
    <xsl:apply-templates select="." mode="javaname"/>
    <xsl:if test="following-sibling::o:param">, </xsl:if>
  </xsl:template>

  <xsl:template match="o:variable">
    private <xsl:apply-templates select="." mode="javaclass"/>
    <xsl:text> </xsl:text>
    <xsl:value-of select="@name"/>;
  </xsl:template>

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
