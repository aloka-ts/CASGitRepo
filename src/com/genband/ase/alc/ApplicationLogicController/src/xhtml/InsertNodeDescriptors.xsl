<?xml version="1.0" encoding="UTF-8"?>
     <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="">
           <xsl:output method="xml" indent="yes"/>
            <xsl:template match="//compare">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">compare<xsl:if test="@identifier != ''"> identifier <xsl:value-of select="@identifier"/></xsl:if><xsl:if test="@label != ''"> label <xsl:value-of select="@label"/></xsl:if><xsl:if test="@asynch != ''"> asynch <xsl:value-of select="@asynch"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//next">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">next</xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//include">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">include</xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//print">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">print<xsl:if test="@value != ''"> value <xsl:value-of select="@value"/></xsl:if><xsl:if test="@label != ''"> label <xsl:value-of select="@label"/></xsl:if><xsl:if test="@asynch != ''"> asynch <xsl:value-of select="@asynch"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//loop">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">+</xsl:attribute>
                       <xsl:attribute name="displayText">loop</xsl:attribute>
                       <xsl:element name="Container">
                       <xsl:attribute name="displayText">loop<xsl:if test="@count != ''"> count <xsl:value-of select="@count"/></xsl:if><xsl:if test="@label != ''"> label <xsl:value-of select="@label"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//attribute">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">attribute<xsl:if test="@name != ''"> name <xsl:value-of select="@name"/></xsl:if><xsl:if test="@value != ''"> value <xsl:value-of select="@value"/></xsl:if><xsl:if test="@reference != ''"> reference <xsl:value-of select="@reference"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//log">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">log<xsl:if test="@level != ''"> level <xsl:value-of select="@level"/></xsl:if><xsl:if test="@value != ''"> value <xsl:value-of select="@value"/></xsl:if><xsl:if test="@label != ''"> label <xsl:value-of select="@label"/></xsl:if><xsl:if test="@asynch != ''"> asynch <xsl:value-of select="@asynch"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//list">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">list</xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//set">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">set<xsl:if test="@variable != ''"> variable <xsl:value-of select="@variable"/></xsl:if><xsl:if test="@label != ''"> label <xsl:value-of select="@label"/></xsl:if><xsl:if test="@asynch != ''"> asynch <xsl:value-of select="@asynch"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//last">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">last</xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//match">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">+</xsl:attribute>
                       <xsl:attribute name="displayText">match</xsl:attribute>
                       <xsl:element name="Container">
                       <xsl:attribute name="displayText">match<xsl:if test="@value != ''"> value <xsl:value-of select="@value"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//play">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">play<xsl:if test="@specification != ''"> specification <xsl:value-of select="@specification"/></xsl:if><xsl:if test="@label != ''"> label <xsl:value-of select="@label"/></xsl:if><xsl:if test="@asynch != ''"> asynch <xsl:value-of select="@asynch"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//else">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">+</xsl:attribute>
                       <xsl:attribute name="displayText">else</xsl:attribute>
                       <xsl:element name="Container">
                       <xsl:attribute name="displayText">else</xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//service">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">+</xsl:attribute>
                       <xsl:attribute name="displayText">service</xsl:attribute>
                       <xsl:element name="Container">
                       <xsl:attribute name="displayText">service<xsl:if test="@name != ''"> name <xsl:value-of select="@name"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//execute">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">execute<xsl:if test="@name != ''"> name <xsl:value-of select="@name"/></xsl:if><xsl:if test="@label != ''"> label <xsl:value-of select="@label"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//wait">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">wait<xsl:if test="@seconds != ''"> seconds <xsl:value-of select="@seconds"/></xsl:if><xsl:if test="@label != ''"> label <xsl:value-of select="@label"/></xsl:if><xsl:if test="@asynch != ''"> asynch <xsl:value-of select="@asynch"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//then">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">+</xsl:attribute>
                       <xsl:attribute name="displayText">then</xsl:attribute>
                       <xsl:element name="Container">
                       <xsl:attribute name="displayText">then</xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//regex">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">regex<xsl:if test="@label != ''"> label <xsl:value-of select="@label"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//label">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">-</xsl:attribute>
                       <xsl:element name="ItemValue">
                       <xsl:attribute name="displayText">label<xsl:if test="@name != ''"> name <xsl:value-of select="@name"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//results">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">+</xsl:attribute>
                       <xsl:attribute name="displayText">results</xsl:attribute>
                       <xsl:element name="Container">
                       <xsl:attribute name="displayText">results<xsl:if test="@value != ''"> value <xsl:value-of select="@value"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="//condition">
                  <xsl:element name="NodeDescriptor">
                       <xsl:attribute name="action">display</xsl:attribute>
                       <xsl:attribute name="display">+</xsl:attribute>
                       <xsl:attribute name="displayText">condition</xsl:attribute>
                       <xsl:element name="Container">
                       <xsl:attribute name="displayText">condition<xsl:if test="@if != ''"> if <xsl:value-of select="@if"/></xsl:if><xsl:if test="@label != ''"> label <xsl:value-of select="@label"/></xsl:if></xsl:attribute>
                       </xsl:element>
                 </xsl:element>
                             <xsl:copy>
                                   <xsl:apply-templates select="*|text()|@*"/>
                             </xsl:copy>
           </xsl:template>
            <xsl:template match="*|@*|text()">
                 <xsl:copy>
                       <xsl:apply-templates select="*|text()|@*"/>
                 </xsl:copy>
           </xsl:template>
</xsl:stylesheet>
