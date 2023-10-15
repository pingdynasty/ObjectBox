<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
                xmlns:java="http://www.o-xml.org/java"
                xmlns:o="http://www.o-xml.org/lang/">

  <xsl:import href="common.xsl"/>

  <xsl:output method="text" indent="no" encoding="UTF-8"/>
  <xsl:template match="o:module">
    <xsl:variable name="classname">
      <xsl:apply-templates select="." mode="javaclass"/>
    </xsl:variable>
public class <xsl:value-of select="$classname"/> implements Module {

  private java.util.Map types = new java.util.HashMap();
  private Functions functions = new Functions(null);

  public <xsl:value-of select="$classname"/>(){
    // create types and functions
    Type type;
    Function function;
    <xsl:apply-templates/>
  }

  public Function getFunction(Name name, Type[] args){
    return functions.getFunction(name, args);
  }

  public Functions getFunctions(){
    return functions;
  }

  public Type getType(Name name){
    return (Type)types.get(name);
  }

  public java.util.Map getTypes(){
    return types;
  }

  <xsl:apply-templates mode="define"/>
}
  </xsl:template>

  <xsl:template match="o:type">
<!--    <xsl:if test="not(preceding-sibling::o:type)">
    Type type;
    </xsl:if>-->
<!--    type = new Type(<xsl:apply-templates select="@name" mode="name"/>); -->
    // type <xsl:value-of select="@name"/>
    type = <xsl:apply-templates select="." mode="javaclass"/>.TYPE;
    types.put(type.getName(), type); // add to module first for self-referencing functions etc
    <xsl:apply-templates select="o:parent"/>
    <xsl:apply-templates select="o:variable"/>
    <xsl:apply-templates select="o:constructor"/>
    <xsl:apply-templates select="o:function"/>
  </xsl:template>

  <xsl:template match="o:type/o:parent">
    type.addParent(<xsl:apply-templates select="@name" mode="type"/>);
  </xsl:template>  

  <xsl:template match="o:type/o:parent[@java:class]"/>
  <xsl:template match="o:type/o:parent[@java:interface]"/>

  <xsl:template match="o:type/o:variable">
    type.addVariable(<xsl:apply-templates select="@name" mode="name"/>, Node.TYPE);
  </xsl:template>  

  <!-- type function -->
  <xsl:template match="o:function[parent::o:type]">
<!--    <xsl:if test="not(preceding-sibling::o:function)">
    Function function;
    </xsl:if>-->
    // function <xsl:value-of select="@name"/>(<xsl:apply-templates select="o:param" mode="signature"/>)
    function = new TypeFunction(<xsl:apply-templates select="ancestor::o:type/@name" mode="type"/>
    <xsl:text>, </xsl:text>
    <xsl:apply-templates select="@name" mode="name"/>
    <xsl:text>, new Type[]{</xsl:text>
    <xsl:apply-templates select="o:param" mode="types"/>}){
      public Node invoke(Node instance, Node[] args, RuntimeContext rc)
        throws ObjectBoxException{
        // downcast target to defining class
        <xsl:apply-templates select="parent::o:type" mode="javaclass"/>
        <xsl:text> realinstance = (</xsl:text>
        <xsl:apply-templates select="parent::o:type" mode="javaclass"/>
        <xsl:text>)instance.cast(</xsl:text>
        <xsl:apply-templates select="parent::o:type/@name" mode="type"/>);

        // downcast arguments to defined class
        <xsl:apply-templates select="o:param" mode="cast"/>

        // call the real class method and return value
        return realinstance.<xsl:apply-templates select="." mode="javaname"/>
        <xsl:text>(</xsl:text>
        <xsl:apply-templates select="o:param" mode="args"/>);
      }
    };
    type.addFunction(function);
  </xsl:template>

  <xsl:template match="o:function[@name = parent::o:type/@name]">
    <xsl:message>invalid constructor: <xsl:value-of select="@name"/></xsl:message>
  </xsl:template>
 
  <!-- type constructor function -->
  <xsl:template match="o:constructor">
    // constructor <xsl:value-of select="../@name"/>(<xsl:apply-templates select="o:param" mode="signature"/>)
    function = new ConstructorFunction(
    <xsl:apply-templates select="../@name" mode="type"/>
    <xsl:text>, new Type[]{</xsl:text>
    <xsl:apply-templates select="o:param" mode="types"/>}){
      /** called directly */
      public Node invoke(Node[] args, RuntimeContext rc)
          throws ObjectBoxException{
        // downcast arguments to their defined classes
	<xsl:apply-templates select="o:param" mode="cast"/>
	return new <xsl:apply-templates select="parent::o:type" mode="javaclass"/>
	<xsl:text>(</xsl:text>
	<xsl:apply-templates select="o:param" mode="args"/>);
      }

      public Node invoke(Node me, Node[] args, RuntimeContext rc)
          throws ObjectBoxException{
	return invoke((AbstractNode)me, args, rc);
      }

      /** called by subtype parent initialiser, eg DynamicConstructor */
      public Node invoke(AbstractNode me, Node[] args, RuntimeContext rc)
          throws ObjectBoxException{
        <xsl:apply-templates select="parent::o:type" mode="javaclass"/>
	<xsl:text> it = new </xsl:text>
	<xsl:apply-templates select="parent::o:type" mode="javaclass"/>(me, me.getType());
	// downcast arguments to their defined classes
	<xsl:apply-templates select="o:param" mode="cast"/>
	it.init(<xsl:apply-templates select="o:param" mode="args"/>);
	return it;
      }
    };
// constructor is not a 'normal' function! // still goes top-level
    functions.addFunction(function);
    type.addConstructor(function);
  </xsl:template>

  <!-- suppress java-typed functions and constructors -->
  <xsl:template match="o:constructor[@java:class or o:param/@java:class]"/>
  <xsl:template match="o:function[@java:class or o:param/@java:class]"/>

  <!-- top level function - add to functions -->
  <xsl:template match="o:function[not(parent::o:type)]">
    // function <xsl:value-of select="@name"/>(<xsl:apply-templates select="o:param" mode="signature"/>)
    function = new Function(<xsl:apply-templates select="@name" mode="name"/>, new Type[]{
    <xsl:apply-templates select="o:param[not(@java:class)]" mode="types"/>}){

      public Node invoke(Node node, Node args[], RuntimeContext rc)
        throws ObjectBoxException{
        throw new ObjectBoxException("cannot invoke static function with a target");
      }

      public Node invoke(Node args[], RuntimeContext rc)
         throws ObjectBoxException{
        // downcast arguments to defined class
        <xsl:apply-templates select="o:param" mode="cast"/>
        // call the real method and return the result
        return <xsl:apply-templates select="." mode="javaname"/>
        <xsl:text>(</xsl:text>
        <xsl:apply-templates select="o:param" mode="args"/>);
      }

<xsl:if test="o:param/@min or o:param/@max">
  <!-- variable length argument list -->
  <xsl:variable name="min" select="sum(o:param/@min)+count(o:param[not(@min)])-count(o:param[@java:class])"/>
      public FunctionKey getKey(){
	return new VariableFunctionKey(getName(), <xsl:value-of select="$min"/>);
      }
</xsl:if>
    };
    functions.addFunction(function);
  </xsl:template>

  <xsl:template match="o:param[@java:class = 'org.oXML.engine.RuntimeContext']" mode="cast"/>

  <xsl:template match="o:param" mode="args">
    <xsl:text>arg</xsl:text>
    <xsl:value-of select="position()"/>
    <xsl:if test="following-sibling::o:param">, </xsl:if>
  </xsl:template>

  <xsl:template match="o:param[@java:class = 'org.oXML.engine.RuntimeContext']" mode="args">
    <xsl:text>rc</xsl:text>    
    <xsl:if test="following-sibling::o:param">, </xsl:if>
  </xsl:template>

  <!-- function definition templates -->
  <xsl:template match="*" mode="define">
    <xsl:apply-templates mode="define"/>
  </xsl:template>

  <xsl:template match="xi:include" mode="define"
                xmlns:xi="http://www.w3.org/2001/XInclude">
    <xsl:apply-templates select="document(@href)/*" mode="define"/>
  </xsl:template>

  <xsl:template match="text()" mode="define"/>

  <xsl:template match="o:function[not(parent::o:type)]" mode="define">
    <!-- define a static function -->
    /* generated function definition */
    public static <xsl:apply-templates select="." mode="returns"/>
    <xsl:text> </xsl:text>
    <xsl:apply-templates select="." mode="javaname"/>
    <xsl:text>(</xsl:text>
    <xsl:apply-templates select="o:param" mode="signature"/>)
    <xsl:apply-templates select="@java:throws"/> {
    <xsl:apply-templates select="java:code"/>
    }
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
