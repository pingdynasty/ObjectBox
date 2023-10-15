
  <!--  <xsl:template match="o:function[@name = parent::o:type/@name and
		                  o:param[1]/@type = parent::o:type/@name and
				  o:param[2]/java:class = 'boolean' and
				  count(o:param) = 2]"> -->


  <xsl:template match="o:function[@name = parent::o:type/@name and
		                  o:param/@type = parent::o:type/@name]"> 
    <!-- copy constructors -->
  /** generated copy constructor, called directly */
  public <xsl:apply-templates select="parent::o:type" mode="javaclass"/>
    <xsl:text>(</xsl:text>
    <xsl:apply-templates select="o:param" mode="signature"/>)
    <xsl:apply-templates select="@java:throws"/> {
    super(TYPE);
    // add ourselves to instance map
    setInstance(<xsl:apply-templates select="parent::o:type" mode="name"/>, this);
    <xsl:apply-templates select="parent::o:type" mode="copy-instances"/>
    // constructor code
    <xsl:apply-templates select="java:code"/>
  }

  /** generated copy constructor, called by subtype copy constructor */
  protected <xsl:apply-templates select="parent::o:type" mode="javaclass"/>
    <xsl:text>(AbstractNode __me, </xsl:text>
    <xsl:apply-templates select="o:param" mode="signature"/>)
    <xsl:apply-templates select="@java:throws"/> {
    super(__me, __me.getType());
    <xsl:apply-templates select="parent::o:type" mode="copy-instances"/>
    // constructor code
    <xsl:apply-templates select="java:code"/>
  }
  </xsl:template>

  <!-- parent instance initialiser for copy constructors -->
<!-- 
  for each parent
    if not already parent
      addparent new Parent(me, parent, deep);
-->
<!-- not needed
  <xsl:template match="o:type" mode="copy-parents">
    <xsl:apply-templates select="o:parent" mode="copy"/>
  </xsl:template> -->

<!--  <xsl:template match="o:type" mode="copy-instances">
    // initialise parents
    <xsl:apply-templates select="o:parent[@name]" mode="copy"/>
  </xsl:template>

  <xsl:template match="o:parent[@name]" mode="copy">
    if(!hasInstance(<xsl:apply-templates select="@name" mode="name"/>)){
      <xsl:apply-templates select="." mode="javaclass"/>
      <xsl:text> parent = (</xsl:text>
      <xsl:apply-templates select="." mode="javaclass"/>
      <xsl:text>)other.getInstance(</xsl:text><!-- assumes copy arg is called 'other' -->
      <xsl:apply-templates select="@name" mode="name"/>);
      parent = new <xsl:apply-templates select="." mode="javaclass"/>(me, parent, deep);
      setInstance(<xsl:apply-templates select="@name" mode="name"/>, parent);
    }
 </xsl:template>-->
<!--    <xsl:apply-templates select="document(concat('../src/lang/',@name, '.oml'))/o:type/o:parent[@name]" mode="initialise"/> -->
  <!-- tbd apply explicit parent initialiser eg o:function/o:parent -->
