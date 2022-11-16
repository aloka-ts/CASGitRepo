<?xml version="1.0" encoding="ISO-8859-1"?><xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="pattern">

  <xsl:if test="(and)">
	  <xsl:apply-templates mode="and" select="and"/> 
  </xsl:if>
  <xsl:if test="(or)">
	  <xsl:apply-templates mode="or" select="or"/> 
  </xsl:if>
  <xsl:if test="(not)">
    <xsl:apply-templates mode="not" select="not"/> 
  </xsl:if>
  <xsl:if test="(equal)">
    <xsl:apply-templates select="equal"/> 
  </xsl:if>
  <xsl:if test="(contains)">
    <xsl:apply-templates select="contains"/> 
  </xsl:if>
  <xsl:if test="(exists)">
    <xsl:apply-templates select="exists"/> 
  </xsl:if>
  <xsl:if test="(subdomain-of)">
    <xsl:apply-templates select="subdomain-of"/> 
  </xsl:if>
</xsl:template>


  <!-- The AND condition template -->
  <xsl:template match="and">
        <xsl:apply-templates mode="and" select="equal"/> 
        <xsl:apply-templates mode="and" select="contains"/> 
        <xsl:apply-templates mode="and" select="exists"/> 
        <xsl:apply-templates mode="and" select="subdomain-of"/> 
        <xsl:apply-templates mode="and" select="not"/> 
        <xsl:apply-templates mode="and" select="and"/> 
        <xsl:apply-templates mode="and"  select="or"/> 
   </xsl:template>
  <xsl:template mode="and" match="and">
        <xsl:apply-templates mode="and" select="equal"/> 
        <xsl:apply-templates mode="and" select="contains"/> 
        <xsl:apply-templates mode="and" select="exists"/> 
        <xsl:apply-templates mode="and" select="subdomain-of"/> 
        <xsl:apply-templates mode="and" select="not"/> 
        <xsl:apply-templates mode="and" select="and"/> 
        <xsl:apply-templates mode="and"  select="or"/> 
   </xsl:template>
  <xsl:template mode="or" match="and">
        <xsl:apply-templates mode="and" select="equal"/> 
        <xsl:apply-templates mode="and" select="contains"/> 
        <xsl:apply-templates mode="and" select="exists"/> 
        <xsl:apply-templates mode="and" select="subdomain-of"/> 
        <xsl:apply-templates mode="and" select="not"/> 
        <xsl:apply-templates mode="and" select="and"/> 
        <xsl:apply-templates mode="and"  select="or"/> 
   </xsl:template>
  <xsl:template mode="not" match="and">
        <xsl:apply-templates mode="and" select="equal"/> 
        <xsl:apply-templates mode="and" select="contains"/> 
        <xsl:apply-templates mode="and" select="exists"/> 
        <xsl:apply-templates mode="and" select="subdomain-of"/> 
        <xsl:apply-templates mode="and" select="not"/> 
        <xsl:apply-templates mode="and" select="and"/> 
        <xsl:apply-templates mode="and"  select="or"/> 
   </xsl:template>
   <!-- end of AND condition template -->




   <!-- The NOT condition template -->
   <xsl:template match="not">
	<xsl:apply-templates mode="not" select="equal"/> 
        <xsl:apply-templates mode="not" select="contains"/> 
        <xsl:apply-templates mode="not" select="exists"/> 
        <xsl:apply-templates mode="not" select="subdomain-of"/> 
        <xsl:apply-templates mode="not" select="and"/> 
        <xsl:apply-templates mode="not" select="or"/> 
	<xsl:apply-templates mode="not" select="not"/> 
   </xsl:template>

   <xsl:template mode="and" match="not">
	<xsl:apply-templates mode="not" select="equal"/> 
        <xsl:apply-templates mode="not" select="contains"/> 
        <xsl:apply-templates mode="not" select="exists"/> 
        <xsl:apply-templates mode="not" select="subdomain-of"/> 
        <xsl:apply-templates mode="not" select="and"/> 
        <xsl:apply-templates mode="not" select="or"/> 
	<xsl:apply-templates mode="not" select="not"/> 
   </xsl:template>
   <xsl:template mode="or" match="not">
	<xsl:apply-templates mode="not" select="equal"/> 
        <xsl:apply-templates mode="not" select="contains"/> 
        <xsl:apply-templates mode="not" select="exists"/> 
        <xsl:apply-templates mode="not" select="subdomain-of"/> 
        <xsl:apply-templates mode="not" select="and"/> 
        <xsl:apply-templates mode="not" select="or"/> 
	<xsl:apply-templates mode="not" select="not"/> 
   </xsl:template>
   <xsl:template mode="not" match="not">
	<xsl:apply-templates mode="not" select="equal"/> 
        <xsl:apply-templates mode="not" select="contains"/> 
        <xsl:apply-templates mode="not" select="exists"/> 
        <xsl:apply-templates mode="not" select="subdomain-of"/> 
        <xsl:apply-templates mode="not" select="and"/> 
        <xsl:apply-templates mode="not" select="or"/> 
	<xsl:apply-templates mode="not" select="not"/> 
   </xsl:template>
   <!-- end of NOT condition template -->


 <!-- The OR condition template -->
 <xsl:template match="or">
        <xsl:apply-templates mode="or" select="and"/> 
        <xsl:apply-templates mode="or" select="or"/> 
        <xsl:apply-templates mode="or" select="not"/> 
        <xsl:apply-templates mode="or" select="equal"/> 
        <xsl:apply-templates mode="or" select="contains"/> 
        <xsl:apply-templates mode="or" select="exists"/> 
        <xsl:apply-templates mode="or" select="subdomain-of"/> 
 </xsl:template>
 <xsl:template mode="and" match="or">
  <xsl:apply-templates mode="or" select="and"/> 
	  <xsl:apply-templates mode="or" select="or"/> 
	  <xsl:apply-templates mode="or" select="not"/> 
	  <xsl:apply-templates mode="or" select="equal"/> 
	  <xsl:apply-templates mode="or" select="contains"/> 
	  <xsl:apply-templates mode="or" select="exists"/> 
	  <xsl:apply-templates mode="or" select="subdomain-of"/> 
 </xsl:template>
 <xsl:template mode="or" match="or">
  <xsl:apply-templates mode="or" select="and"/> 
	  <xsl:apply-templates mode="or" select="or"/> 
	  <xsl:apply-templates mode="or" select="not"/> 
	  <xsl:apply-templates mode="or" select="equal"/> 
	  <xsl:apply-templates mode="or" select="contains"/> 
	  <xsl:apply-templates mode="or" select="exists"/> 
	  <xsl:apply-templates mode="or" select="subdomain-of"/> 
 </xsl:template>
 <xsl:template mode="not" match="or">
  <xsl:apply-templates mode="or" select="and"/> 
	  <xsl:apply-templates mode="or" select="or"/> 
	  <xsl:apply-templates mode="or" select="not"/> 
	  <xsl:apply-templates mode="or" select="equal"/> 
	  <xsl:apply-templates mode="or" select="contains"/> 
	  <xsl:apply-templates mode="or" select="exists"/> 
	  <xsl:apply-templates mode="or" select="subdomain-of"/> 
 </xsl:template>
 <!-- end of OR condition template --> 



 <!-- the EQUAL template -->
 <xsl:template match="equal">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>

<xsl:template mode="and" match="equal">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>

<xsl:template mode="or" match="equal">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>


<xsl:template mode="not" match="equal">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>
<!--   -->



<!-- CONTAINS template -->
<xsl:template match="contains">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>

<xsl:template mode="and" match="contains">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>


<xsl:template mode="or" match="contains">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>

<xsl:template mode ="not" match="contains">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>

<!--  -->



<!-- SUBDOMAIN template, requires a fucntion in base class -->
<xsl:template match="subdomain-of">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>
<xsl:template mode="and" match="subdomain-of">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>
<xsl:template mode="or" match="subdomain-of">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>
<xsl:template mode="not" match="subdomain-of">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>
<!--  -->



<!-- EXISTS template -->
<xsl:template match="exists">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>
<xsl:template mode="and" match="exists">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>
<xsl:template mode="or" match="exists">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>
<xsl:template mode="not" match="exists">
	<xsl:apply-templates select="var"/> 
	<xsl:text disable-output-escaping="yes">,</xsl:text>
</xsl:template>
<!-- -->

<!-- the VALUE extraction template -->
<xsl:template match="value">
<xsl:value-of select="normalize-space(.)"/>
</xsl:template>

<!-- the VAR extraction template -->
<xsl:template match="var">
<xsl:value-of select="normalize-space(.)"/>
</xsl:template>

</xsl:stylesheet>
