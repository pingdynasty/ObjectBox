<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:o="http://www.o-xml.org/lang/"
  xmlns:doc="http://www.o-xml.org/namespace/document/">

  <xsl:output method="html" indent="no" encoding="UTF-8"/>

  <xsl:template match="/">
    <html>
      <head>
        <title>o:XML <xsl:value-of select="o:type/@name"/> Type Interface</title>
        <!-- <link href="complete.css" rel="stylesheet" type="text/css"/> -->
      </head>
      <body marginwidth="40%">
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="o:type">
    <table border="1" width="100%" cellpadding="3" cellspacing="0">
      <!-- type name and documentation -->
      <tr><td><h3>Type <xsl:value-of select="@name"/></h3></td></tr>
      <tr><td><xsl:apply-templates select="doc:*"/></td></tr>
      <br/>
      <!-- parent types -->
      <xsl:apply-templates select="o:parent"/>
    </table>
    <br/>

    <!-- type variables -->
    <xsl:if test="o:variable">
      <table border="1" width="100%" cellpadding="3" cellspacing="0">
        <thead>
          <tr><td bgcolor="lightgrey">Variable</td></tr>
        </thead>
        <tbody>
          <xsl:apply-templates select="o:variable"/>
        </tbody>
      </table>
      <br/>
    </xsl:if>

    <!-- defined type functions -->
    <table border="1" width="100%" cellpadding="3" cellspacing="0">
      <thead>
        <tr><th bgcolor="lightgrey">Functions:</th></tr>
      </thead>
      <tbody>
        <xsl:apply-templates select="o:function">
          <xsl:sort select="@name"/>
        </xsl:apply-templates>
      </tbody>
    </table>
    <br/>
    <!-- inherited functions -->
    <xsl:apply-templates select="o:parent" mode="inherited-functions"/>

  </xsl:template>

  <xsl:template match="o:parent" mode="inherited-functions">
    <table border="1" width="100%" cellpadding="3" cellspacing="0">
      <thead>
        <tr><th bgcolor="lightgrey">Functions inherited from <a href="{@name}.html"><xsl:value-of select="@name"/></a>:</th></tr>
      </thead>
      <tbody>
        <xsl:apply-templates 
          select="document(concat(@name, '.oml'))/o:type/o:function"
          mode="inherited-functions">
          <xsl:sort select="@name"/>
        </xsl:apply-templates>
      </tbody>
    </table>
    <br/>
    <xsl:apply-templates select="document(concat(@name, '.oml'))/o:type/o:parent"
      mode="inherited-functions"/>
  </xsl:template>

  <xsl:template match="o:function" mode="inherited-functions">
    <xsl:apply-templates select="."/>
  </xsl:template>

  <xsl:template match="o:parent">
    <tr>
      <td><i>extends </i><a href="{@name}.html"><xsl:value-of select="@name"/></a></td>
    </tr>
  </xsl:template>

  <xsl:template match="o:variable">
    <tr>
      <td>
        <xsl:variable name="type">
          <xsl:value-of select="@type"/>
          <xsl:if test="not(@type)">Node</xsl:if>      
        </xsl:variable>
        <a href="{$type}.html"><xsl:value-of select="$type"/></a>
        <xsl:text> </xsl:text>
        <xsl:value-of select="@name"/>
      </td>
    </tr>
    <tr>
      <td class="comment">
        <xsl:apply-templates select="doc:*"/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="o:function[java/@hide='yes']"/>

  <xsl:template match="o:function">
    <tr>
      <td>
        <table>
          <tr>
            <td>
              <b><xsl:value-of select="@name"/></b>
              <xsl:text>(</xsl:text>
              <xsl:apply-templates select="o:param"/>
              <xsl:text>)</xsl:text>
            </td>
          </tr>
              <xsl:apply-templates select="doc:*"/>
        </table>
      </td>
    </tr>
  </xsl:template>


  <xsl:template match="o:param">
    <xsl:variable name="type">
      <xsl:value-of select="@type"/>
      <xsl:if test="not(@type)">Node</xsl:if>      
    </xsl:variable>
    <a href="{$type}.html"><xsl:value-of select="$type"/></a>
    <xsl:text> </xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:if test="following-sibling::o:param">, </xsl:if>
  </xsl:template>

  <xsl:template match="o:param" mode="name">
    <xsl:value-of select="@type"/>
    <xsl:if test="not(@type)">Node</xsl:if>
    <xsl:text> </xsl:text><xsl:value-of select="@name"/>
  </xsl:template>

  <xsl:template match="doc:p">
    <tr><td><xsl:apply-templates mode="inline"/></td></tr>
  </xsl:template>

  <xsl:template match="doc:return">
    <tr><td><b>Returns: </b><xsl:apply-templates mode="inline"/></td></tr>
  </xsl:template>

  <xsl:template match="doc:param" mode="inline">
    <b><tt>
    <xsl:value-of select="@name"/>
    <xsl:if test="not(@name)">
      <xsl:value-of select="ancestor::o:*/o:param/@name"/>
    </xsl:if>
    </tt></b>
    <!-- overkill    <b><xsl:apply-templates select="ancestor::o:*/o:param[@name=current()/@name]" mode="name"/></b> -->
  </xsl:template>

  <xsl:template match="doc:param">
    <tr><td><b>Parameter <tt>
      <xsl:value-of select="@name"/>
      <xsl:if test="not(@name)">
        <xsl:value-of select="ancestor::o:*/o:param/@name"/>
      </xsl:if>
    </tt>: </b>
    <xsl:apply-templates mode="inline"/></td></tr>
    <!--      <xsl:text>Parameter </xsl:text>
      <xsl:apply-templates select="../o:param[@name=current()/@name]" mode="name"/>
-->
  </xsl:template>

  <xsl:template match="doc:same">
    <tr><td><b>Same as: </b><xsl:apply-templates mode="inline"/></td></tr>
  </xsl:template>

  <xsl:template match="doc:see">
    <tr><td><b>See Also: </b><xsl:apply-templates mode="inline"/></td></tr>
  </xsl:template>

  <xsl:template match="doc:link[@url]" mode="inline">
    <a href="{@url}"><xsl:apply-templates mode="inline"/></a>
  </xsl:template>

  <xsl:template match="doc:link[@type]" mode="inline">
    <a href="{@type}.html"><xsl:apply-templates mode="inline"/></a>
  </xsl:template>

  <xsl:template match="doc:*">
    <xsl:message>
      warning - unknown doc element: <xsl:value-of select="name(.)"/>
    </xsl:message>
    <tr><td><font color="red"><xsl:apply-templates mode="inline"/></font></td></tr>
  </xsl:template>

  <xsl:template match="text()" mode="inline">
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
