<xsl:stylesheet 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xsltc="http://xml.apache.org/xalan/xsltc"
   xmlns:redirect="http://xml.apache.org/xalan/redirect"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   extension-element-prefixes="xsltc redirect"
   version="1.0">

<xsl:output method="html" />

    <xsl:template match="xs:schema">
        <xsl:for-each select="//xs:complexType">
            <xsl:variable name="complexTypeName"><xsl:value-of select="@name"/></xsl:variable>
== <xsl:value-of select="xs:annotation//xs:appinfo/source"/> ==
=== <xsl:value-of select="substring-before(@name, 'type')"/> ===
                                &lt;<xsl:value-of select="substring-before(@name, 'type')"/>&gt; - <xsl:value-of select="xs:annotation/xs:documentation"/>
                                <xsl:for-each select="xs:attribute">
                                    <xsl:if test="position() = 1">
==== Attributes ====
</xsl:if>
                                    <xsl:if test="(@name!='label' and @name!='asynch') or (count(../xs:attribute[@name='label']) = 0 or count(../xs:attribute[@name='asynch']) = 0)">    
   ''''' <xsl:value-of select="@name"/> '''''
    '' <xsl:value-of select="xs:annotation/xs:documentation"/> 
    ''
          {{{ <xsl:if test="@type='xs:string'">
* Type: string literal.</xsl:if>
                                                <xsl:if test="@type='xs:boolean'">
* Type: a boolean value (true/false)</xsl:if>
                                                <xsl:if test="@use='required'">
* This is a requred attribute.</xsl:if>
                                                <xsl:if test="@use='optional'">
* This is an optional attribute.</xsl:if>
                                                <xsl:if test="@default">
* It's default value is <xsl:value-of select="@default"/>.</xsl:if>
          }}}</xsl:if>
                                    <xsl:if test="count(../xs:attribute[@name='label']) = 1 and count(../xs:attribute[@name='asynch']) = 1 and position() = last()">
      - Has ALC common action attributes. (label="&lt;literal&gt;", asynch="true|false")</xsl:if>
                                    <xsl:if test="@name='label'">                            
                                    </xsl:if>
                                </xsl:for-each>

                                <xsl:for-each select="xs:sequence">
                                        <xsl:for-each select="xs:element">
                                            <xsl:variable name="elementTypeName"><xsl:value-of select="@type"/></xsl:variable>
                                            <xsl:variable name="elementPosition"><xsl:value-of select="position()"/></xsl:variable>
                                            <xsl:if test="position() = 1">
==== Elements ====
</xsl:if>
                                            <xsl:if test="(@name!='results' and @name!='next-action' and @name!='default-action') or (count(../xs:element[@name='results']) = 0 or count(../xs:element[@name='next-action']) = 0 or count(../xs:element[@name='default-action']) = 0)">                            
                                                    <xsl:if test="@type='xs:string' or @type='xs:boolean' or @type='xs:integer'">
   ''''' <xsl:value-of select="@name"/> '''''
    '' <xsl:value-of select="xs:annotation/xs:documentation"/> 
    '' 

                                                    </xsl:if>
                                                    <xsl:if test="@type!='xs:string' and @type!='xs:boolean' and @type!='xs:integer'">
   ''''' <xsl:value-of select="@name"/> '''''
    '' <xsl:value-of select="xs:annotation/xs:documentation"/> 
    '' 
                                                    </xsl:if>
          {{{ <xsl:if test="@type!='xs:string' and @type!='xs:boolean' and @type!='xs:integer'">
* Type: complex type.</xsl:if>
                                                    <xsl:if test="@type='xs:string'">
* Type: string literal.</xsl:if>
                                                    <xsl:if test="@type='xs:integer'">
* Type: an integer value.</xsl:if>
                                                    <xsl:if test="@type='xs:boolean'">
* Type: a boolean value (true/false)</xsl:if>
                                                    <xsl:if test="@minOccurs='0'">
* This is an optional element.</xsl:if>
                                                    <xsl:if test="@minOccurs &gt; '0'">
* This is a required element.</xsl:if>
                                                    <xsl:if test="@maxOccurs">
   And can occur <xsl:if test="@maxOccurs='unbounded'">as many times as desired.</xsl:if><xsl:if test="@maxOccurs!='unbounded'"><xsl:value-of select="@maxOccurs"/> times.</xsl:if></xsl:if>
          }}}</xsl:if>
                                            <xsl:if test="count(../xs:element[@name='default-action']) = 1 and count(../xs:element[@name='results']) = 1 and count(../xs:element[@name='next-action']) = 1 and position() = last()">
      - Has ALC common action elements. (&lt;default-action&gt;, &lt;next-action&gt;, &lt;results&gt;)
                                            </xsl:if>
                                        </xsl:for-each>
                                        <xsl:for-each select="xs:choice">
   ''''' &lt;List&gt; ''''' 
          {{{ <xsl:if test="@minOccurs='0'">
* This is an optional list.</xsl:if>
                                                    <xsl:if test="@minOccurs &gt; '0'">
* This is a required list.</xsl:if>
                                                    <xsl:if test="@maxOccurs">
And elements can occur <xsl:if test="@maxOccurs='unbounded'">as often as desired.</xsl:if><xsl:if test="@maxOccurs!='unbounded'"><xsl:value-of select="@maxOccurs"/> times.</xsl:if></xsl:if>
          }}}
                                            <xsl:for-each select="xs:element">
                                                <xsl:variable name="elementTypeName"><xsl:value-of select="@type"/></xsl:variable>
                                                <xsl:variable name="elementName"><xsl:value-of select="@name"/></xsl:variable>
                                                <xsl:variable name="elementPosition"><xsl:value-of select="position()"/></xsl:variable>
                                                    <xsl:if test="@type='xs:string' or @type='xs:boolean' or @type='xs:integer'">
                                                        '' <xsl:value-of select="@name"/> '' 
                                                    </xsl:if>
                                                    <xsl:if test="@type!='xs:string' and @type!='xs:boolean' and @type!='xs:integer'">
                                                         '' <xsl:value-of select="@name"/> '' 
                                                    </xsl:if>
                                            </xsl:for-each>
                                        </xsl:for-each>
                                </xsl:for-each>
                                <xsl:for-each select="xs:annotation/xs:appinfo/html/body">
                                        <xsl:copy-of select="."/>
                                </xsl:for-each>
                </xsl:for-each>             
                <xsl:for-each select="xs:element">
                    <xsl:variable name="elementTypeName"><xsl:value-of select="@type"/></xsl:variable>
                    <xsl:if test="position() = 1">==== Elements ====</xsl:if>
===== Element: <xsl:value-of select="@name"/> =====
                    <xsl:if test="@type!='xs:string' and @type!='xs:boolean' and @type!='xs:integer'">
                        Type:: <xsl:value-of select="@type"/>
                    </xsl:if>
                        <xsl:value-of select="xs:annotation/xs:documentation"/>
                </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
