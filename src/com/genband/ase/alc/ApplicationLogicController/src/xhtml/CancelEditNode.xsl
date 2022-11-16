<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="">
        <xsl:output method="xml" indent="yes"/>

        <xsl:template match="//NodeDescriptor[@action = 'cancel']">
        </xsl:template>
        
	<xsl:template match="//SavedContent">
		<xsl:element name="NodeDescriptor">
			<xsl:attribute name="action">display</xsl:attribute>
			<xsl:attribute name="display">+</xsl:attribute>
			<xsl:apply-templates select="ItemValue|Container"/>
		</xsl:element>
	</xsl:template>

        <!-- preservation of uninteresting nodes -->
        <xsl:template match="*|comment()|text()|@*">
                <xsl:copy>
                        <xsl:apply-templates select="*|comment()|text()|@*"/>
                </xsl:copy>
        </xsl:template>

</xsl:stylesheet>
