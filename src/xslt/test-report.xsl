<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
  <xsl:output method="html" indent="yes"/>
  
  <xsl:template match="/">
    <html>
      <head>
        <title>o:XML Test Report Interface</title>
      </head>
      <body>
        <xsl:apply-templates/>
      </body>
    </html>
  </xsl:template>

  <xsl:template match="report">
    <xsl:apply-templates select="testrun" mode="summary"/>
    <br/>
    <xsl:apply-templates select="testrun" mode="index"/>
    <br/>
    <xsl:apply-templates select="coverage"/>
    <br/>
    <xsl:apply-templates select="testrun"/>
  </xsl:template>

  <xsl:template match="testrun" mode="summary">
    <p><b>Test Run: </b><xsl:value-of select="date"/></p>
    <p>
      <xsl:text>Number of Tests: Failed </xsl:text>
      <xsl:value-of select="count(test[error])"/>
      <xsl:text> Passed </xsl:text>
      <xsl:value-of select="count(test[passed])"/>
      <xsl:text> Total </xsl:text>
      <xsl:value-of select="count(test)"/>
    </p>
    <p>Total Test Time: <xsl:value-of select="sum(test/time)"/>ms</p>
  </xsl:template>

  <xsl:template match="testrun" mode="index">
    <table border="1" width="100%" cellpadding="3" cellspacing="0">
      <tr>
        <th bgcolor="lightgrey">Test Name</th>
        <th bgcolor="lightgrey">Type</th>
        <th bgcolor="lightgrey">Function</th>
        <th bgcolor="lightgrey">Procedure</th>
        <th bgcolor="lightgrey">Status</th>
      </tr>
      <xsl:apply-templates select="test" mode="index">
	<xsl:sort select="not(type)" data-type="number"/><!-- put type tests first -->
	<xsl:sort select="type"/>
	<xsl:sort select="function"/>
	<xsl:sort select="procedure"/>
      </xsl:apply-templates>
    </table>
  </xsl:template>

  <xsl:template match="test" mode="index">
    <tr>
      <td><a href="#test{position()}"><xsl:value-of select="name"/></a></td>
      <td>
        <xsl:value-of select="type"/>
        <xsl:if test="not(type)">&#160;</xsl:if>
      </td>
      <td>
        <xsl:value-of select="function"/>
        <xsl:if test="not(function)">&#160;</xsl:if>
      </td>
      <td>
        <xsl:value-of select="procedure"/>
        <xsl:if test="not(procedure)">&#160;</xsl:if>
      </td>
      <td>
        <xsl:if test="passed">
          <xsl:attribute name="bgcolor">forestgreen</xsl:attribute>          
          <xsl:text>passed</xsl:text>
        </xsl:if>
        <xsl:if test="error">
          <xsl:attribute name="bgcolor">firebrick</xsl:attribute>
          <xsl:text>failed</xsl:text>
        </xsl:if>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="testrun">
    <table border="1" width="100%" cellpadding="3" cellspacing="0">
      <tr><th colspan="4" bgcolor="lightgrey">Tests</th></tr>
      <xsl:apply-templates select="test">
	<xsl:sort select="not(type)" data-type="number"/><!-- put type tests first -->
	<xsl:sort select="type"/>
	<xsl:sort select="function"/>
	<xsl:sort select="procedure"/>
      </xsl:apply-templates>
    </table>
  </xsl:template>

  <xsl:template match="test">
    <tr><td><a name="test{position()}"/>
    <table width="80%">
      <tr><td width="30%"><b><xsl:value-of select="name"/></b></td><td width="70%">&#160;</td></tr>
      <xsl:apply-templates/>
    </table>
    </td></tr>
  </xsl:template>

  <xsl:template match="function">
    <tr><td>Function:</td><td><xsl:value-of select="."/></td></tr>
  </xsl:template>

  <xsl:template match="procedure">
    <tr><td>Procedure:</td><td><xsl:value-of select="."/></td></tr>
  </xsl:template>

  <xsl:template match="type">
    <tr><td>Type:</td><td><xsl:value-of select="."/></td></tr>
  </xsl:template>

  <xsl:template match="passed">
    <tr><td>Status:</td><td><font color="darkgreen">passed</font></td></tr>
  </xsl:template>

  <xsl:template match="error">
    <tr><td>Status:</td><td><font color="indianred">error</font></td></tr>
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="time">
    <tr><td>Time:</td><td><xsl:value-of select="."/>ms</td></tr>
  </xsl:template>

  <xsl:template match="assertion">
    <tr>
      <td><xsl:value-of select="message"/>:</td>
      <td><xsl:value-of select="expression"/></td>
    </tr>
  </xsl:template>

  <xsl:template match="exception">
    <tr>
      <td>Exception:</td>
      <td><xsl:value-of select="."/></td>
    </tr>
  </xsl:template>

  <xsl:template match="return">
    <tr><td>Returned:</td><td><xsl:value-of select="."/></td></tr>
  </xsl:template>

  <xsl:template match="expected">
    <tr><td>Expected:</td><td><xsl:value-of select="."/></td></tr>
  </xsl:template>

  <xsl:template match="coverage">
    <table border="1" width="100%" cellpadding="3" cellspacing="0">
      <tr><th colspan="4" bgcolor="lightgrey">Test Coverage</th></tr>
      <xsl:apply-templates mode="coverage"/>
    </table>
    <br/>
    <table border="1" width="100%" cellpadding="3" cellspacing="0">
      <tr><th colspan="2" bgcolor="lightgrey">Untested Types, Constructors, Functions and Procedures</th></tr>
      <xsl:apply-templates mode="untested"/>
      <xsl:if test="not(functions/function[tests = 0]|constructors/constructor[tests = 0]|procedures/procedure[tests = 0]|types/type[tests = 0])">
        <tr><td colspan="2"><i>-- none --</i></td></tr>
      </xsl:if>
    </table>
  </xsl:template>

  <xsl:template match="function[tests = 0]|constructor[tests = 0]|procedure[tests = 0]|type[tests = 0]" mode="untested">
    <tr>
      <td><xsl:value-of select="name(.)"/></td>
      <td><xsl:value-of select="name"/></td>
    </tr>
  </xsl:template>

  <xsl:template match="types" mode="coverage">
    <tr>
      <td>Types Tested:</td>
      <td><xsl:value-of select="tested"/></td>
      <td>Total in File:</td>
      <td><xsl:value-of select="total"/></td>
    </tr>
  </xsl:template>

  <xsl:template match="constructors" mode="coverage">
    <tr>
      <td>Constructors Tested:</td>
      <td><xsl:value-of select="tested"/></td>
      <td>Total in File:</td>
      <td><xsl:value-of select="total"/></td>
    </tr>
  </xsl:template>

  <xsl:template match="functions" mode="coverage">
    <tr>
      <td>Functions Tested:</td>
      <td><xsl:value-of select="tested"/></td>
      <td>Total in File:</td>
      <td><xsl:value-of select="total"/></td>
    </tr>
  </xsl:template>

  <xsl:template match="procedures" mode="coverage">
    <tr>
      <td>Procedures Tested:</td>
      <td><xsl:value-of select="tested"/></td>
      <td>Total in File:</td>
      <td><xsl:value-of select="total"/></td>
    </tr>
  </xsl:template>

  <xsl:template match="text()" mode="untested"/>
  <xsl:template match="text()" mode="coverage"/>
  <xsl:template match="text()"/>

</xsl:stylesheet>
