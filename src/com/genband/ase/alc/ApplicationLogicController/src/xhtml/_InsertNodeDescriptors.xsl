<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="">
        <xsl:output method="xml" indent="yes"/>

        <!-- leaf items -->
        <xsl:template match="//<!-- leaf items -->">
                <xsl:element name="NodeDescriptor">
                        <xsl:attribute name="action">display</xsl:attribute>
                        <xsl:attribute name="display">-</xsl:attribute>
                        <xsl:element name="ItemValue">
                                <xsl:copy>
                                        <xsl:apply-templates select="*|text()|@*"/>
                                </xsl:copy>
                        </xsl:element>     
                </xsl:element>
        </xsl:template>

        <!-- containers -->
        <xsl:template match="//<!-- containers -->">
                <xsl:element name="NodeDescriptor">
                        <xsl:attribute name="action">display</xsl:attribute>
                        <xsl:attribute name="display">+</xsl:attribute>
                        <xsl:element name="Container">
                                <xsl:copy>
                                        <xsl:apply-templates select="*|text()|@*"/>
                                </xsl:copy>
                        </xsl:element>     
                </xsl:element>
        </xsl:template>

        <xsl:template match="*|@*|text()">
                <xsl:copy>
                        <xsl:apply-templates select="*|text()|@*"/>
                </xsl:copy>
        </xsl:template>
        
</xsl:stylesheet>
