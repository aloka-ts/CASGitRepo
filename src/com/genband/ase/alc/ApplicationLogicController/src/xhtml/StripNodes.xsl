<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="">
        <xsl:output method="xml" indent="yes"/>

        <xsl:template match="//NodeDescriptor/Container/*|comment()|text()|@*">
                <xsl:copy>
                        <xsl:apply-templates select="*|comment()|text()|@*"/>
                </xsl:copy>
        </xsl:template>

        <xsl:template match="//NodeDescriptor/ItemValue/*|comment()|text()|@*">
                <xsl:copy>
                        <xsl:apply-templates select="*|comment()|text()|@*"/>
                </xsl:copy>
        </xsl:template>

        <!-- preservation of uninteresting nodes -->
        <xsl:template match="//comment()">
                <xsl:copy>
                        <xsl:apply-templates select="comment()"/>
                </xsl:copy>
        </xsl:template>
</xsl:stylesheet>
