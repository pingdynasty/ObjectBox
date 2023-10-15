<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:java="http://www.o-xml.org/java"
                xmlns:o="http://www.o-xml.org/lang/">

  <xsl:import href="common.xsl"/>

  <xsl:output method="text" indent="no" encoding="UTF-8"/>

  <xsl:template match="o:function">
    <xsl:variable name="classname">
      <xsl:apply-templates select="." mode="javaclass"/>
    </xsl:variable>
public class <xsl:value-of select="$classname"/> extends Function {

  public static final Name NAME = <xsl:apply-templates select="@name" mode="name"/>;

  public static final Type[] SIGNATURE = new Type[]{
  <xsl:apply-templates select="o:param[not(@java:class)]" mode="types"/>};

  public <xsl:value-of select="$classname"/>(){
        super(NAME, SIGNATURE);
  }

  public Node invoke(Node node, Node args[], RuntimeContext rc)
      throws ObjectBoxException{
      throw new ObjectBoxException("cannot invoke static function with a target");
  }

  /* generated function definition */
  public <xsl:apply-templates select="." mode="returns"/>
  <xsl:text> </xsl:text>
  <xsl:apply-templates select="." mode="javaname"/>
  <xsl:text>(</xsl:text>
  <xsl:apply-templates select="o:param" mode="signature"/>)
  <xsl:apply-templates select="@java:throws"/> {
  <xsl:apply-templates select="java:code"/>
  }

  public Node invoke(Node args[], RuntimeContext rc)
      throws ObjectBoxException{
      // downcast arguments to defined class
      <xsl:apply-templates select="o:param" mode="cast"/>
      // call the real class method and return value
      return <xsl:apply-templates select="." mode="javaname"/>
      <xsl:text>(</xsl:text>
      <xsl:apply-templates select="o:param" mode="args"/>);
  }

<xsl:if test="o:param/@min or o:param/@max">
  <xsl:variable name="min" select="sum(o:param/@min)+count(o:param[not(@min)])-count(o:param[@java:class])"/>
  public FunctionKey getKey(){
    return new VariableFunctionKey(getName(), <xsl:value-of select="$min"/>);
  }
</xsl:if>
}
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
