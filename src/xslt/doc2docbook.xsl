<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
		xmlns:o="http://www.o-xml.org/lang/"
		xmlns:doc="http://www.o-xml.org/namespace/document/"
                xmlns:ut="http://www.o-xml.org/namespace/unit-test/">
  <xsl:param name="root" select="'/docs'"/><!-- relative to the deployment path -->
  <xsl:param name="lang-root" select="'../build/docs/'"/><!-- relative to the path of this xsl -->

  <doc:purpose>
    stylesheet that produces a DocBook article from an
    o:XML type definition with inline documentation
  </doc:purpose>

  <xsl:output method="xml" indent="yes" encoding="UTF-8"/>

  <xsl:template match="/">
    <article>
      <xsl:apply-templates/>
      <xi:include xmlns:xi="http://www.w3.org/2001/XInclude" href="sidebar.xml"/>
    </article>
  </xsl:template>

  <xsl:template match="o:type">
    <title><xsl:value-of select="@name"/></title>
    <xsl:if test="contains(@name, ':')">
      <xsl:variable name="prefix" select="substring-before(@name, ':')"/>
      <command>xmlns:<xsl:value-of select="$prefix"/>="<xsl:value-of select="namespace::node()[name(.) = $prefix]"/>"</command>
    </xsl:if>

    <!-- parent types -->
    <xsl:apply-templates select="o:parent"/>

    <!-- type docs -->
    <xsl:apply-templates select="doc:*"/>

    <!-- type constructors -->
    <section>
      <title>Constructors</title>
      <xsl:apply-templates select="o:constructor|o:function[@name = current()/@name]"/>
    </section>

    <!-- type functions declared by this type -->
    <section>
      <title>Type Functions</title>
      <xsl:apply-templates select="o:function[@name != current()/@name]">
	<xsl:sort select="@name"/>
      </xsl:apply-templates>
    </section>

    <!-- inherited functions -->
    <xsl:apply-templates select="o:parent" mode="inherited-functions"/>
  </xsl:template>

  <xsl:template match="o:parent[@name]" mode="inherited-functions">
    <xsl:variable name="xref">
      <xsl:call-template name="xref">
	<xsl:with-param name="type" select="@name"/>
      </xsl:call-template>
    </xsl:variable>
    <xsl:variable name="parent" select="@name"/>
    <section>
      <title>Functions inherited from <ulink url="{$root}/{$xref}.html"><xsl:value-of select="@name"/></ulink></title>
      <xsl:apply-templates select="document(concat($lang-root, $xref, '.oml'))//o:type[@name = $parent]/o:function[@name != $parent]"
                           mode="inherited-functions">
	<xsl:sort select="@name"/>
      </xsl:apply-templates>
    </section>
    <!-- functions inherited by this parent type -->
    <xsl:apply-templates select="document(concat($lang-root, $xref, '.oml'))//o:type[@name = $parent]/o:parent"
                         mode="inherited-functions"/>
  </xsl:template>

  <xsl:template match="o:function" mode="inherited-functions">
    <xsl:apply-templates select="."/>
  </xsl:template>

  <xsl:template match="o:parent[@name]">
    <xsl:variable name="xref">
      <xsl:call-template name="xref">
	<xsl:with-param name="type" select="@name"/>
      </xsl:call-template>
    </xsl:variable>
    <para>extends <ulink url="{$root}/{$xref}.html"><xsl:value-of select="@name"/></ulink></para>
  </xsl:template>

  <xsl:template match="o:function[@access = 'private']"/>
  <xsl:template match="o:function[@access = 'protected']"/>

  <xsl:template match="o:function">
    <section>
      <title>
	<xsl:value-of select="@name"/>
	<xsl:text>(</xsl:text>
	<xsl:apply-templates select="o:param"/>
	<xsl:text>)</xsl:text>
      </title>
      <xsl:apply-templates select="doc:*"/>
    </section>
  </xsl:template>

  <xsl:template match="o:param">
    <xsl:variable name="type">
      <xsl:value-of select="@type"/>
      <xsl:if test="not(@type)">Node</xsl:if>
    </xsl:variable>
    <xsl:variable name="xref">
      <xsl:call-template name="xref">
	<xsl:with-param name="type" select="$type"/>
      </xsl:call-template>
    </xsl:variable>
    <ulink url="{$root}/{$xref}.html">
      <xsl:value-of select="$type"/>
    </ulink>
    <xsl:text> </xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:if test="following-sibling::o:param">, </xsl:if>
  </xsl:template>

  <xsl:template match="o:param[@type = ancestor::o:type/@name]">
    <xsl:value-of select="@type"/>
    <xsl:text> </xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:if test="following-sibling::o:param">, </xsl:if>
  </xsl:template>

  <xsl:template match="doc:bug">
    <para><emphasis>Bug: <xsl:value-of select="@ref"/>
    <xsl:value-of select="@status"/></emphasis></para>
  </xsl:template>

  <xsl:template match="doc:copyright">
    <para><emphasis>Copyright: </emphasis>
    <xsl:apply-templates mode="inline"/></para>
  </xsl:template>

  <xsl:template match="doc:purpose">
    <para><xsl:apply-templates mode="inline"/></para>
  </xsl:template>

  <xsl:template match="doc:p">
    <para><xsl:apply-templates mode="inline"/></para>
  </xsl:template>

  <xsl:template match="doc:return">
    <para><emphasis>Returns: </emphasis><xsl:apply-templates mode="inline"/></para>
  </xsl:template>

  <xsl:template match="doc:param">
    <para>
      <emphasis>
	<xsl:text>Parameter </xsl:text>
	<varname>
	  <xsl:value-of select="@name"/>
	  <xsl:if test="not(@name)">
	    <xsl:value-of select="ancestor::o:*/o:param/@name"/>
	  </xsl:if>
	</varname>
	<xsl:text>: </xsl:text>
      </emphasis>
      <xsl:apply-templates mode="inline"/>
    </para>
  </xsl:template>

  <xsl:template match="doc:same">
    <para><emphasis>Same as: </emphasis><xsl:apply-templates mode="inline"/></para>
  </xsl:template>

  <xsl:template match="doc:see">
    <para><emphasis>See also: </emphasis><xsl:apply-templates mode="inline"/></para>
  </xsl:template>

  <xsl:template match="doc:link[@url]" mode="inline">
    <ulink url="{@url}"><xsl:apply-templates mode="inline"/></ulink>
  </xsl:template>

  <xsl:template match="doc:link[@type]" mode="inline">
    <xsl:variable name="xref">
      <xsl:call-template name="xref">
	<xsl:with-param name="type" select="@type"/>
      </xsl:call-template>
    </xsl:variable>
    <ulink url="{$root}/{$xref}.html">
      <xsl:apply-templates mode="inline"/>
      <xsl:if test="not(./node())"><xsl:value-of select="@type"/></xsl:if>
    </ulink>
  </xsl:template>

  <xsl:template match="doc:param" mode="inline">
    <varname>
      <xsl:value-of select="ancestor::o:*/o:param/@name"/>
    </varname>
  </xsl:template>

  <xsl:template match="doc:param[@name]" mode="inline">
    <varname>
      <xsl:value-of select="@name"/>
    </varname>
  </xsl:template>

  <xsl:template match="doc:bug" mode="inline">
    <emphasis>Bug: <xsl:value-of select="@ref"/>
    <xsl:value-of select="@status"/></emphasis>
  </xsl:template>

  <xsl:template match="doc:type" mode="inline">
    <varname>
      <xsl:value-of select="ancestor::o:type/@name"/>
    </varname>
  </xsl:template>

  <xsl:template match="doc:type[@name]" mode="inline">
    <xsl:variable name="xref">
      <xsl:call-template name="xref">
	<xsl:with-param name="type" select="@name"/>
      </xsl:call-template>
    </xsl:variable>
    <varname>
     <ulink url="{$root}/{$xref}.html"><xsl:value-of select="@name"/></ulink>
    </varname>
  </xsl:template>

  <xsl:template match="doc:example">
    <informalexample>
      <programlisting>
	<xsl:copy-of select="node()"/>
      </programlisting>
    </informalexample>
  </xsl:template>

  <xsl:template match="doc:*">
    <xsl:message>
      warning - unknown doc element: <xsl:value-of select="name(.)"/>
    </xsl:message>
    <tr><td>
      <font color="red"><xsl:value-of select="name(.)"/></font>
      <xsl:apply-templates mode="inline"/>
      <font color="red"><xsl:value-of select="name(.)"/></font>
    </td></tr>
  </xsl:template>

  <xsl:template match="text()" mode="inline">
    <xsl:value-of select="."/>
  </xsl:template>

  <!-- utility templates -->
  <xsl:template name="xref">
    <xsl:param name="type"/>
    <xsl:choose>
      <xsl:when test="contains($type, ':')">
	<xsl:variable name="prefix" select="substring-before($type, ':')"/>
	<xsl:variable name="uri" select="//namespace::*[name(.) = $prefix]"/>
	<xsl:variable name="path">
          <xsl:choose><!-- todo: replace with a mappings xml file / xml catalogue -->
            <xsl:when test="$uri = 'http://www.o-xml.com/servlet/'">
              <xsl:text>lib/servlet/</xsl:text>
            </xsl:when>
            <xsl:otherwise>
              <xsl:value-of select="substring-after($uri, 'http://www.o-xml.org/')"/>
            </xsl:otherwise>
          </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$path"/>
	<xsl:value-of select="substring-after($type, ':')"/>
      </xsl:when>
      <xsl:when test="$type">
	<xsl:value-of select="concat('lang/', $type)"/>
      </xsl:when>
      <xsl:otherwise>lang/Node</xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template match="ut:*"/><!-- ignore unit test content -->
</xsl:stylesheet>
<!--
    for more information see http://www.o-xml.org/projects/document.html
    Copyright (C) 2002-2006 Martin Klang, Alpha Plus Technology Ltd
    email: mars at pingdynasty.com

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
