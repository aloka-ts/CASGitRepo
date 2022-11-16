<xsl:stylesheet 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xsltc="http://xml.apache.org/xalan/xsltc"
   xmlns:redirect="http://xml.apache.org/xalan/redirect"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   xmlns:xalan="http://xml.apache.org/xslt"
   extension-element-prefixes="xsltc redirect"
   version="1.0">

  <xsl:output method="xml" version="1.0" indent="yes" xalan:indent-amount="6"/> 
  <xsl:strip-space elements="*"/>

  <xsl:template match="/xs:schema/xs:include">
            <xsl:apply-templates select="document(@schemaLocation)/xs:schema/*|comment()|text()|@*"/>
            <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="@*|node()">
        <xsl:copy>
          <xsl:apply-templates select="@*|node()"/>
        </xsl:copy>
  </xsl:template>

</xsl:stylesheet>
