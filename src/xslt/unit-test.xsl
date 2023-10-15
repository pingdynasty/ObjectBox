<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
  xmlns:ut="http://www.o-xml.org/namespace/unit-test/"
  xmlns:o="http://www.o-xml.org/lang/">
  <xsl:param name="file" select="''"/><!-- name of the file that we're processing -->

  <xsl:output method="xml" indent="yes"/>

  <xsl:template match="/">
    <o:program xmlns:o="http://www.o-xml.org/lang/"
	       xmlns:ut="http://www.o-xml.org/namespace/unit-test/"
	       xmlns:doc="http://www.o-xml.org/namespace/document/">

      <o:import href="lib/test.oml"/>
      <xsl:if test="$file"><o:import href="{$file}"/></xsl:if>

      <xsl:if test="not($file)"><xsl:copy-of select="ut:suite/o:*"/></xsl:if>

      <!-- named test definitions -->
      <xsl:apply-templates select="//ut:definition[@name]"/>

      <!-- define test types -->
      <xsl:apply-templates select="//ut:test"/>

      <o:variable name="suite" select="ut:TestSuite()"/>
      <xsl:apply-templates select="//ut:dataset" mode="instantiate"/>
      <xsl:apply-templates select="//ut:test" mode="instantiate"/>

      <o:do select="$suite.run()"/>

      <report>
        <!-- report info -->
        <file><xsl:value-of select="$file"/></file>

        <!-- test report -->
	<o:eval select="$suite.report()"/>

        <!-- coverage report -->
        <coverage>
          <types>
            <total><xsl:value-of select="count(//o:type)"/></total>
            <tested>
              <xsl:value-of select="count(//o:type[.//ut:test])"/>
            </tested>
            <xsl:apply-templates select="//o:type" mode="coverage"/>
          </types>
          <constructors>
            <total><xsl:value-of select="count(//o:constructor|//o:function[@name = parent::o:type/@name])"/></total>
            <tested>
              <xsl:value-of select="count(//o:constructor[.//ut:test]|//o:function[@name = parent::o:type/@name and .//ut:test])"/>
            </tested>
            <xsl:apply-templates select="//o:constructor|//o:function[@name = parent::o:type/@name]" mode="coverage"/>
          </constructors>
          <functions>
            <total><xsl:value-of select="count(//o:function[@name != parent::o:type/@name])"/></total>
            <tested>
              <xsl:value-of select="count(//o:function[@name != parent::o:type/@name and .//ut:test])"/>
            </tested>
            <xsl:apply-templates select="//o:function[@name != parent::o:type/@name]" mode="coverage"/>
          </functions>
          <procedures>
            <total><xsl:value-of select="count(//o:procedure)"/></total>
            <tested>
              <xsl:value-of select="count(//o:procedure[.//ut:test])"/>
            </tested>
            <xsl:apply-templates select="//o:procedure" mode="coverage"/>
          </procedures>
        </coverage>
      </report>
    </o:program>
  </xsl:template>

  <xsl:template match="o:type" mode="coverage">
    <type>
      <name><xsl:value-of select="@name"/></name>
      <tests><xsl:value-of select="count(.//ut:test)"/></tests>
    </type>    
  </xsl:template>

  <xsl:template match="o:function" mode="name">
    <!-- static function -->
    <xsl:value-of select="@name"/>
    <xsl:text>(</xsl:text>
    <xsl:apply-templates select="o:param" mode="name"/>
    <xsl:text>)</xsl:text>
  </xsl:template>

  <xsl:template match="o:function[parent::o:type]" mode="name">
    <!-- type function (not constructor) -->
    <xsl:value-of select="parent::o:type/@name"/>
    <xsl:text>.</xsl:text>
    <xsl:value-of select="@name"/>
    <xsl:text>(</xsl:text>
    <xsl:apply-templates select="o:param" mode="name"/>
    <xsl:text>)</xsl:text>
  </xsl:template>

  <xsl:template match="o:constructor|o:function[@name = parent::o:type/@name]" mode="name">
    <!-- constructor -->
    <xsl:value-of select="parent::o:type/@name"/>
    <xsl:text>(</xsl:text>
    <xsl:apply-templates select="o:param" mode="name"/>
    <xsl:text>)</xsl:text>
  </xsl:template>

  <xsl:template match="o:procedure" mode="name">
    <xsl:value-of select="@name"/>
    <xsl:text>(</xsl:text>
    <xsl:apply-templates select="o:param" mode="name"/>
    <xsl:text>)</xsl:text>
  </xsl:template>

  <xsl:template match="o:param" mode="name">
    <xsl:variable name="type">
      <xsl:choose>
        <xsl:when test="@type"><xsl:value-of select="@type"/></xsl:when>
        <xsl:otherwise>Node</xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:value-of select="concat($type, ' ', @name)"/>
    <xsl:if test="following-sibling::o:param">, </xsl:if>
  </xsl:template>

  <xsl:template match="o:constructor|o:function[@name = parent::o:type/@name]" mode="coverage">
    <constructor>
      <name><xsl:apply-templates select="." mode="name"/></name>
      <tests><xsl:value-of select="count(.//ut:test)"/></tests>
    </constructor>
  </xsl:template>

  <xsl:template match="o:function" mode="coverage">
    <function>
      <name><xsl:apply-templates select="." mode="name"/></name>
      <tests><xsl:value-of select="count(.//ut:test)"/></tests>
    </function>
  </xsl:template>

  <xsl:template match="o:procedure" mode="coverage">
    <procedure>
      <name><xsl:apply-templates select="." mode="name"/></name>
      <tests><xsl:value-of select="count(.//ut:test)"/></tests>
    </procedure>
  </xsl:template>

  <xsl:template match="text()"/>

  <xsl:template match="ut:dataset" mode="instantiate">
    <!-- create the variable that represents the dataset -->
    <o:variable name="{@name}">
      <xsl:copy-of select="node()"/>
    </o:variable>
    <o:do select="$suite.setDataset('{@name}', ${@name})"/>
  </xsl:template>

  <xsl:template match="ut:definition[@name]">
    <!-- create the function -->
    <o:function name="ut:{@name}">
      <o:param name="input"/>
      <o:do>
        <xsl:copy-of select="node()"/>
      </o:do>
    </o:function>
  </xsl:template>

  <xsl:template match="ut:test">
    <xsl:variable name="name">
      <xsl:value-of select="@name"/>
      <xsl:if test="not(@name)">Test<xsl:value-of select="position()"/></xsl:if>
    </xsl:variable>
    <o:type name="{$name}">
      <o:parent name="ut:Test"/>

      <o:constructor>
        <!-- constructor, takes input and expected results dataset -->
        <o:param name="input"/>
        <o:param name="result"/>
        <o:parent name="ut:Test" select="ut:Test('{$name}', $input, $result)"/>
        <o:do/>
      </o:constructor>

      <o:function name="test">
        <o:param name="input"/>
        <o:do>
          <xsl:apply-templates select="ut:definition"/>
        </o:do>
      </o:function>
    </o:type>
  </xsl:template>

  <xsl:template match="ut:definition[@ref]">
    <!-- call the function with this name -->
    <o:return select="ut:{@ref}($input)"/>
  </xsl:template>

  <xsl:template match="ut:definition">
    <!-- anonymous test definition -->
    <xsl:copy-of select="node()"/>
  </xsl:template>

  <xsl:template match="ut:test" mode="instantiate">
    <!-- call the generated constructor with input/result -->
    <xsl:variable name="name">
      <xsl:value-of select="@name"/>
      <xsl:if test="not(@name)">Test<xsl:value-of select="position()"/></xsl:if>
    </xsl:variable>
    <o:variable name="input"/>
    <o:variable name="result"/>
    <xsl:apply-templates select="ut:input|ut:result"/>
    <!-- instantiate this Test -->
    <o:set test="{$name}($input, $result)"/>
    <!-- set the available contextual information -->
    <xsl:if test="ancestor::o:type">
      <o:do select="$test.type('{ancestor::o:type/@name}')"/>
    </xsl:if>
    <xsl:if test="parent::o:function|parent::o:constructor">
      <xsl:variable name="function">
        <xsl:apply-templates select="parent::o:function|parent::o:constructor" mode="name"/>
      </xsl:variable>
      <o:do select="$test.function('{$function}')"/>
    </xsl:if>
    <xsl:if test="parent::o:procedure">
      <xsl:variable name="procedure">
        <xsl:apply-templates select="parent::o:procedure" mode="name"/>
      </xsl:variable>
      <o:do select="$test.procedure('{$procedure}')"/>
    </xsl:if>
    <o:do select="$suite.addTest($test)"/>
  </xsl:template>

  <xsl:template match="ut:result">
    <!-- anonymous result dataset -->
    <o:variable name="result">
      <xsl:copy-of select="node()"/>
    </o:variable>
  </xsl:template>

  <xsl:template match="ut:result[@ref]">
    <!-- dereference the variable with this name -->
    <o:variable name="result" select="$suite.getDataset('{@ref}')"/>
  </xsl:template>

  <xsl:template match="ut:input">
    <!-- anonymous input dataset -->
    <o:variable name="input">
      <xsl:copy-of select="node()"/>
    </o:variable>
  </xsl:template>

  <xsl:template match="ut:input[@ref]">
    <!-- referenced input dataset -->
    <!-- copy the variable with the referenced name -->
    <o:variable name="input" select="$suite.getDataset('{@ref}')"/>
  </xsl:template>

</xsl:stylesheet>
