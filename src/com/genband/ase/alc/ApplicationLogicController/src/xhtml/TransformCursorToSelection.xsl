<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns="">
        <xsl:output method="xml" indent="yes"/>
        <xsl:template match="//NodeDescriptor[@action = 'cursor']">
                <xsl:element name="NodeDescriptor">
                        <xsl:attribute name="action">display</xsl:attribute>
                        <xsl:attribute name="display">-</xsl:attribute>

                        <xsl:if test="NodeOptions/Selection = 'NextAction'">
                                <ItemValue>  
                                        <NextAction>[Label]</NextAction>
                                </ItemValue>
                        </xsl:if>

                        <xsl:if test="NodeOptions/Selection = 'Parameter'">
                                <ItemValue>  
                                        <Parameter>[Parameter]</Parameter>
                                </ItemValue>
                        </xsl:if>

                        <xsl:if test="NodeOptions/Selection = 'Item'">
                                <ItemValue>  
                                        <Item>[Value]</Item>
                                </ItemValue>
                        </xsl:if>

                        <xsl:if test="NodeOptions/Selection = 'Regex'">
                                <ItemValue>  
                                        <Regex AppliedTo="[context variable]"><Pattern>[regex goes here.]</Pattern>
                                        </Regex>
                                </ItemValue>
                        </xsl:if>

                        <xsl:if test="NodeOptions/Selection = 'Expression'">
                                <ItemValue>  
                                        <Expression/>
                                </ItemValue>
                        </xsl:if>

                        <xsl:if test="NodeOptions/Selection = 'Service'">
                                <Container>  
                                        <Service/>
                                </Container>
                        </xsl:if>

                        <xsl:if test="NodeOptions/Selection = 'Results'">
                                <Container>
                                        <Results Value="[first key]"></Results>
                                </Container>
                        </xsl:if>

                        <xsl:if test="NodeOptions/Selection = 'ActionDefinition'">
                                <Container>
                                        <ActionDefinition Class="[Java Class Name]" Method="[Method Name]"/>
                                </Container>
                        </xsl:if>

                        <xsl:if test="NodeOptions/Selection = 'Match'">
                                <Container>
                                        <Match Value="[value for entry]"/>
                                </Container>
                        </xsl:if>

                        <xsl:if test="NodeOptions/Selection = 'Execute'">
                                <Container>
                                        <Execute Function="[Function Name Here]"/>
                                </Container>
                        </xsl:if>

                        <xsl:if test="NodeOptions/Selection = 'Loop'">
                                <Container>
                                        <Loop/>
                                </Container>
                        </xsl:if>
        
                        <xsl:if test="NodeOptions/Selection = 'Condition'">
                                <Container>
                                        <Condition/>
                                </Container>
                        </xsl:if>

                        <xsl:if test="NodeOptions/Selection = 'ParameterList'">
                                <Container>
                                        <ParameterList><Item>[Empty]</Item></ParameterList>
                                </Container>
                        </xsl:if>
                </xsl:element>     
        </xsl:template>

        <xsl:template match="*|@*|text()">
                <xsl:copy>
                        <xsl:apply-templates select="*|text()|@*"/>
                </xsl:copy>
        </xsl:template>

</xsl:stylesheet>
