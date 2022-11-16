<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="">
           <xsl:output method="xml" indent="yes"/>
           <xsl:template match="//NodeDesciptor">
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
</xsl:stylesheet>
