<xsl:stylesheet 
   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
   xmlns:xsltc="http://xml.apache.org/xalan/xsltc"
   xmlns:redirect="http://xml.apache.org/xalan/redirect"
   xmlns:xs="http://www.w3.org/2001/XMLSchema"
   extension-element-prefixes="xsltc redirect"
   version="1.0">

<xsl:output method="html" />

    <xsl:template match="xs:schema">
        <html>
            <head>
                <title>Application Logic Control Markup</title>
                <script language="JavaScript" src="tigra_hints.js"></script>
                <style type="text/css"><![CDATA[
                    .hintsClass {
                            font-family: tahoma, verdana, arial;
                            font-size: 12px;
                            background-color: #f0f0f0;
                            color: #000000;
                            border: 1px solid #808080;
                            padding: 5px;
                    }
                    .hintSource {
                            color: green;
                            text-decoration: underline;
                            cursor: pointer;
                    }
                    h2 { padding:0px 0px 0px 20px; } 
                    #complextypedocumentation 
                    {
                        font-size: 10px;
                        padding:0px 0px 0px 40px; 
                    } 
                    h4 { padding:0px 0px 0px 60px; } 
                    h5 { padding:0px 0px 0px 80px; } 
                ]]></style>
            </head>
            <body>
                <script language="JavaScript">

                // configuration variable for the hint object, these setting will be shared among all hints created by this object
                var HINTS_CFG = {
                    'wise'       : true, // don't go off screen, don't overlap the object in the document
                    'margin'     : 10, // minimum allowed distance between the hint and the window edge (negative values accepted)
                    'gap'        : 0, // minimum allowed distance between the hint and the origin (negative values accepted)
                    'align'      : 'tcbc', // align of the hint and the origin (by first letters origin's top|middle|bottom left|center|right to hint's top|middle|bottom left|center|right)
                    'css'        : 'hintsClass', // a style class name for all hints, applied to DIV element (see style section in the header of the document)
                    'show_delay' : 200, // a delay between initiating event (mouseover for example) and hint appearing
                    'hide_delay' : 100, // a delay between closing event (mouseout for example) and hint disappearing
                    'follow'     : false, // hint follows the mouse as it moves
                    'z-index'    : 100, // a z-index for all hint layers
                    'IEfix'      : false, // fix IE problem with windowed controls visible through hints (activate if select boxes are visible through the hints)
                    'IEtrans'    : ['blendTrans(DURATION=.3)', 'blendTrans(DURATION=.3)'], // [show transition, hide transition] - nice transition effects, only work in IE5+
                    'opacity'    : 100 // opacity of the hint in %%
                };
                // text/HTML of the hints
                var HINTS_ITEMS = [
                    'short definition',
                    'tooltip for item2 with some <b>HTML</b>',
                    'tooltip for item3<br/>This one is multi line',
                    'tooltip for item4',
                    'another sample tooltip with the <a href="http://www.softcomplex.com">link</a>'
                ];

                var myHint = new THints (HINTS_ITEMS, HINTS_CFG);
                </script>
                <xsl:for-each select="//xs:complexType">
                    <xsl:variable name="complexTypeName"><xsl:value-of select="@name"/></xsl:variable>
                    <xsltc:output file="{$complexTypeName}.html">
                        <html>
                            <head>
                                <title>Application Logic Control Markup</title>
                                <script language="JavaScript" src="tigra_hints.js"></script>
                                <style type="text/css"><![CDATA[
                                    .hintsClass {
                                            font-family: tahoma, verdana, arial;
                                            font-size: 12px;
                                            background-color: #f0f0f0;
                                            color: #000000;
                                            border: 1px solid #808080;
                                            padding: 5px;
                                    }
                                    .hintSource {
                                            color: green;
                                            text-decoration: underline;
                                            cursor: pointer;
                                    }
                                    h2 { padding:0px 0px 0px 20px; } 
                                    #complextypedocumentation 
                                    {
                                        padding:0px 0px 0px 40px; 
                                        font-size:12pt;
                                    } 
                                    #elementtitle
                                    {
                                        padding:30px 0px 0px 50px; 
                                        font-size:12pt;
                                        font-weight: bold;
                                    }
                                    #complextypeattributetitle
                                    {
                                        padding:30px 0px 0px 50px; 
                                        font-size:12pt;
                                        font-weight: bold;
                                    }
                                    #elementname
                                    {
                                        padding:15px 0px 0px 65px; 
                                        font-weight: bold;
                                    }
                                    #aList
                                    {
                                        font-style: oblique;
                                        color: teal;
                                    }
                                    #attributename
                                    {
                                        padding:15px 0px 0px 65px; 
                                        font-weight: bold;
                                    }
                                    #elementrequired
                                    {
                                        padding:0px 0px 0px 90px; 
                                    }
                                    #attributerequired
                                    {
                                        padding:0px 0px 0px 90px; 
                                    }
                                    #elementtype
                                    {
                                        padding:0px 0px 0px 90px; 
                                    }
                                    #attributetype
                                    {
                                        padding:0px 0px 0px 90px; 
                                    }
                                    #attributedefault
                                    {
                                        padding:0px 0px 0px 90px; 
                                    }
                                    #elementdocumentation
                                    {
                                        font-style: oblique;
                                        padding:5px 10px 0px 110px; 
                                    }
                                    #attributedocumentation
                                    {
                                        font-style: oblique;
                                        padding:5px 10px 0px 110px; 
                                    }
                                    #attributescommon
                                    {
                                        padding:30px 0px 0px 90px; 
                                    }
                                    h4 { padding:0px 0px 0px 60px; } 
                                    h5 { padding:0px 0px 0px 80px; } 
                                    #complextypechoicedocumentation
                                    {
                                        padding:0px 0px 0px 140px;
                                        font-size:12pt;
                                    }
                                    
                                    #elementsinachoice
                                    {
                                        padding:0px 0px 0px 160px;
                                        font-size:10pt;
                                    }
                                ]]></style>
                            </head>
                            <body>
                                <script language="JavaScript">
                                // configuration variable for the hint object, these setting will be shared among all hints created by this object
                                var HINTS_CFG = {
                                    'wise'       : true, // don't go off screen, don't overlap the object in the document
                                    'margin'     : 10, // minimum allowed distance between the hint and the window edge (negative values accepted)
                                    'gap'        : 0, // minimum allowed distance between the hint and the origin (negative values accepted)
                                    'align'      : 'tcbc', // align of the hint and the origin (by first letters origin's top|middle|bottom left|center|right to hint's top|middle|bottom left|center|right)
                                    'css'        : 'hintsClass', // a style class name for all hints, applied to DIV element (see style section in the header of the document)
                                    'show_delay' : 200, // a delay between initiating event (mouseover for example) and hint appearing
                                    'hide_delay' : 100, // a delay between closing event (mouseout for example) and hint disappearing
                                    'follow'     : false, // hint follows the mouse as it moves
                                    'z-index'    : 100, // a z-index for all hint layers
                                    'IEfix'      : false, // fix IE problem with windowed controls visible through hints (activate if select boxes are visible through the hints)
                                    'IEtrans'    : ['blendTrans(DURATION=.3)', 'blendTrans(DURATION=.3)'], // [show transition, hide transition] - nice transition effects, only work in IE5+
                                    'opacity'    : 100 // opacity of the hint in %%
                                };
                                // text/HTML of the hints
                                var HINTS_ITEMS = [
                                    'Common user type attributes:<br/>label - indicates a label name for this action.<br/>asynch - is a boolean, when set to true indicates that this action is non-blocking and results should be handled when available',
                                    'Common user type elements:<br/>next-action - indicates a which label named action should be executed next.<br/>results - a results handler that is executed upon action completion (see <a href="Resultstype.html">definition</a>)',
                                    'tooltip for item2 with some <b>HTML</b>',
                                    'tooltip for item3<br/>This one is multi line',
                                    'tooltip for item4',
                                    'another sample tooltip with the <a href="http://www.softcomplex.com">link</a>'
                                ];

                                var myHint = new THints (HINTS_ITEMS, HINTS_CFG);
                                </script>
                                <a href="index.html">Top Level</a>
                                <h1><xsl:value-of select="xs:annotation//xs:appinfo/source"/></h1>
                                <h2 id="complextype">
                                    &lt;<xsl:value-of select="substring-before(@name, 'type')"/>&gt;
                                </h2>

                                <div id="complextypedocumentation">
                                    <xsl:value-of select="xs:annotation/xs:documentation"/>
                                </div>

                                
                                <xsl:for-each select="xs:attribute">

                                    <xsl:if test="position() = 1">
                                        <div id="complextypeattributetitle">
                                            Attributes
                                        </div>
                                    </xsl:if>
                                    
                                    <xsl:if test="(@name!='label' and @name!='asynch') or (count(../xs:attribute[@name='label']) = 0 or count(../xs:attribute[@name='asynch']) = 0)">    
                                        <div id="complextypeattribute">
                                            <div id="attributename">
                                                <xsl:value-of select="@name"/>
                                            </div>
                                            <div id="attributetype">
                                                <xsl:if test="@type='xs:string'">
                                                    Type: string literal.
                                                </xsl:if>
                                                <xsl:if test="@type='xs:boolean'">
                                                    Type: a boolean value (true/false)
                                                </xsl:if>
                                            </div>
                                            <div id="attributerequired">                                            
                                                <xsl:if test="@use='required'">
                                                    This is a requred attribute.
                                                </xsl:if>
                                                <xsl:if test="@use='optional'">
                                                    This is an optional attribute.
                                                </xsl:if>
                                            </div>
                                            <div id="attributedefault">                                            
                                                <xsl:if test="@default">
                                                    It's default value is <xsl:value-of select="@default"/>.
                                                </xsl:if>
                                            </div>                                            
                                            <div id="attributedocumentation">
                                                <xsl:value-of select="xs:annotation/xs:documentation"/>
                                            </div>
                                         </div>
                                    </xsl:if>
                                    <xsl:if test="count(../xs:attribute[@name='label']) = 1 and count(../xs:attribute[@name='asynch']) = 1 and position() = last()">
                                        <div id="attributescommon">
                                            <span class="hintSource" onmouseover="myHint.show(0, this)" onmouseout="myHint.hide()">
                                                Has ALC common action attributes.
                                            </span>
                                        </div>
                                    </xsl:if>

                                    <xsl:if test="@name='label'">                            
                                    </xsl:if>
                                </xsl:for-each>

                                <xsl:for-each select="xs:sequence">
                                    <div id="containedelements">
                                        <xsl:for-each select="xs:element">
                                            <xsl:variable name="elementTypeName"><xsl:value-of select="@type"/></xsl:variable>
                                            <xsl:variable name="elementPosition"><xsl:value-of select="position()"/></xsl:variable>
                                            <xsl:if test="position() = 1">
                                                <div id="elementtitle">
                                                    Elements
                                                </div>
                                            </xsl:if>
                                            <xsl:if test="(@name!='results' and @name!='next-action' and @name!='default-action') or (count(../xs:element[@name='results']) = 0 or count(../xs:element[@name='next-action']) = 0 or count(../xs:element[@name='default-action']) = 0)">                            
                                                <div id="elementname">
                                                    <xsl:if test="@type='xs:string' or @type='xs:boolean' or @type='xs:integer'">
                                                        <xsl:value-of select="@name"/>
                                                    </xsl:if>
                                                    <xsl:if test="@type!='xs:string' and @type!='xs:boolean' and @type!='xs:integer'">
                                                        <script language="JavaScript">
                                                            // configuration variable for the hint object, these setting will be shared among all hints created by this object
                                                            var temp<xsl:value-of select="position()"/>HINTS_CFG = {
                                                                'wise'       : true, // don't go off screen, don't overlap the object in the document
                                                                'margin'     : 10, // minimum allowed distance between the hint and the window edge (negative values accepted)
                                                                'gap'        : 0, // minimum allowed distance between the hint and the origin (negative values accepted)
                                                                'align'      : 'tcbc', // align of the hint and the origin (by first letters origin's top|middle|bottom left|center|right to hint's top|middle|bottom left|center|right)
                                                                'css'        : 'hintsClass', // a style class name for all hints, applied to DIV element (see style section in the header of the document)
                                                                'show_delay' : 200, // a delay between initiating event (mouseover for example) and hint appearing
                                                                'hide_delay' : 100, // a delay between closing event (mouseout for example) and hint disappearing
                                                                'follow'     : false, // hint follows the mouse as it moves
                                                                'z-index'    : 100, // a z-index for all hint layers
                                                                'IEfix'      : false, // fix IE problem with windowed controls visible through hints (activate if select boxes are visible through the hints)
                                                                'IEtrans'    : ['blendTrans(DURATION=.3)', 'blendTrans(DURATION=.3)'], // [show transition, hide transition] - nice transition effects, only work in IE5+
                                                                'opacity'    : 100 // opacity of the hint in %%
                                                            };
                                                            // text/HTML of the hints
                                                            var temp<xsl:value-of select="position()"/>HINTS_ITEMS = [
                                                                'Click to see specification for <xsl:value-of select="@name"/>'
                                                            ];<xsl:value-of select="substring-before(@name, 'type')"/>
                                                            var temp<xsl:value-of select="position()"/>Hints = new THints (temp<xsl:value-of select="position()"/>HINTS_ITEMS, temp<xsl:value-of select="position()"/>HINTS_CFG);
                                                        </script>
                                                        <a href="{$elementTypeName}.html" class="hintSource" onmouseover="temp{$elementPosition}Hints.show(0, this)" onmouseout="temp{$elementPosition}Hints.hide()">
                                                            <xsl:value-of select="@name"/>
                                                        </a>
                                                    </xsl:if>
                                                </div>                                                
                                                <div id="elementtype">
                                                    <xsl:if test="@type!='xs:string' and @type!='xs:boolean' and @type!='xs:integer'">
                                                        Type: complex type.
                                                    </xsl:if>
                                                    <xsl:if test="@type='xs:string'">
                                                        Type: string literal.
                                                    </xsl:if>
                                                    <xsl:if test="@type='xs:integer'">
                                                        Type: an integer value.
                                                    </xsl:if>
                                                    <xsl:if test="@type='xs:boolean'">
                                                        Type: a boolean value (true/false)
                                                    </xsl:if>
                                                </div>
                                                <div id="elementrequired">
                                                    <xsl:if test="@minOccurs='0'">
                                                        This is an optional element.
                                                    </xsl:if>
                                                    <xsl:if test="@minOccurs &gt; '0'">
                                                        This is a required element.
                                                    </xsl:if>
                                                    <xsl:if test="@maxOccurs">
                                                        And can occur 
                                                        <xsl:if test="@maxOccurs='unbounded'">
                                                            as many times as desired.
                                                        </xsl:if>
                                                        <xsl:if test="@maxOccurs!='unbounded'">
                                                            <xsl:value-of select="@maxOccurs"/> times.
                                                        </xsl:if>
                                                    </xsl:if>
                                                </div>
                                                <div id="elementdocumentation">
                                                    <xsl:value-of select="xs:annotation/xs:documentation"/>
                                                </div>
                                            </xsl:if>
                                            <xsl:if test="count(../xs:element[@name='default-action']) = 1 and count(../xs:element[@name='results']) = 1 and count(../xs:element[@name='next-action']) = 1 and position() = last()">
                                                <div id="attributescommon">
                                                    <span class="hintSource" onmouseover="myHint.show(1, this)" onmouseout="myHint.hide()">
                                                        Has ALC common action elements.
                                                    </span>
                                                </div>
                                            </xsl:if>
                                        </xsl:for-each>
                                        <xsl:for-each select="xs:choice">
                                            <div id="listtitle">
                                                <script language="JavaScript">
                                                    <xsl:text>
                                                    // configuration variable for the hint object, these setting will be shared among all hints created by this object
                                                    var choiceHINTS_CFG = {
                                                        'wise'       : true, // don't go off screen, don't overlap the object in the document
                                                        'margin'     : 10, // minimum allowed distance between the hint and the window edge (negative values accepted)
                                                        'gap'        : 0, // minimum allowed distance between the hint and the origin (negative values accepted)
                                                        'align'      : 'tcbc', // align of the hint and the origin (by first letters origin's top|middle|bottom left|center|right to hint's top|middle|bottom left|center|right)
                                                        'css'        : 'hintsClass', // a style class name for all hints, applied to DIV element (see style section in the header of the document)
                                                        'show_delay' : 200, // a delay between initiating event (mouseover for example) and hint appearing
                                                        'hide_delay' : 100, // a delay between closing event (mouseout for example) and hint disappearing
                                                        'follow'     : false, // hint follows the mouse as it moves
                                                        'z-index'    : 100, // a z-index for all hint layers
                                                        'IEfix'      : false, // fix IE problem with windowed controls visible through hints (activate if select boxes are visible through the hints)
                                                        'IEtrans'    : ['blendTrans(DURATION=.3)', 'blendTrans(DURATION=.3)'], // [show transition, hide transition] - nice transition effects, only work in IE5+
                                                        'opacity'    : 100 // opacity of the hint in %%
                                                    };
                                                    // text/HTML of the hints
                                                    var choiceHINTS_ITEMS = [
                                                        '</xsl:text><xsl:value-of select="xs:annotation/xs:documentation"/><xsl:text>'
                                                    ];
                                                    var choiceHints = new THints (choiceHINTS_ITEMS, choiceHINTS_CFG);
                                                    </xsl:text>
                                                </script>
                                                <div id="elementname">
                                                    <div id="aList">
                                                        <span class="hintSource" onmouseover="choiceHints.show(0, this)" onmouseout="choiceHints.hide()">
                                                            List 
                                                        </span>
                                                    </div>
                                                </div>
                                                <div id="elementrequired">
                                                    <xsl:if test="@minOccurs='0'">
                                                        This is an optional list.
                                                    </xsl:if>
                                                    <xsl:if test="@minOccurs &gt; '0'">
                                                        This is a required list.
                                                    </xsl:if>
                                                    <xsl:if test="@maxOccurs">
                                                        And elements can occur 
                                                        <xsl:if test="@maxOccurs='unbounded'">
                                                            as often as desired.
                                                        </xsl:if>
                                                        <xsl:if test="@maxOccurs!='unbounded'">
                                                            <xsl:value-of select="@maxOccurs"/> times.
                                                        </xsl:if>
                                                    </xsl:if>
                                                </div>
                                            </div>
                                            <xsl:for-each select="xs:element">
                                                <xsl:variable name="elementTypeName"><xsl:value-of select="@type"/></xsl:variable>
                                                <xsl:variable name="elementName"><xsl:value-of select="@name"/></xsl:variable>
                                                <xsl:variable name="elementPosition"><xsl:value-of select="position()"/></xsl:variable>
                                                <div id="elementsinachoice">
                                                    <script language="JavaScript">
                                                        // configuration variable for the hint object, these setting will be shared among all hints created by this object
                                                        var temp_<xsl:value-of select="position()"/>HINTS_CFG = {
                                                            'wise'       : true, // don't go off screen, don't overlap the object in the document
                                                            'margin'     : 10, // minimum allowed distance between the hint and the window edge (negative values accepted)
                                                            'gap'        : 0, // minimum allowed distance between the hint and the origin (negative values accepted)
                                                            'align'      : 'tcbc', // align of the hint and the origin (by first letters origin's top|middle|bottom left|center|right to hint's top|middle|bottom left|center|right)
                                                            'css'        : 'hintsClass', // a style class name for all hints, applied to DIV element (see style section in the header of the document)
                                                            'show_delay' : 200, // a delay between initiating event (mouseover for example) and hint appearing
                                                            'hide_delay' : 100, // a delay between closing event (mouseout for example) and hint disappearing
                                                            'follow'     : false, // hint follows the mouse as it moves
                                                            'z-index'    : 100, // a z-index for all hint layers
                                                            'IEfix'      : false, // fix IE problem with windowed controls visible through hints (activate if select boxes are visible through the hints)
                                                            'IEtrans'    : ['blendTrans(DURATION=.3)', 'blendTrans(DURATION=.3)'], // [show transition, hide transition] - nice transition effects, only work in IE5+
                                                            'opacity'    : 100 // opacity of the hint in %%
                                                        };
                                                        // text/HTML of the hints
                                                        var temp_<xsl:value-of select="position()"/>HINTS_ITEMS = [
                                                            '<xsl:value-of select="@name"/><xsl:if test="@type!='xs:string' and @type!='xs:boolean' and @type!='xs:integer'"><br/>Type: complex type.</xsl:if><xsl:if test="@type='xs:string'"><br/>Type: string literal.</xsl:if><xsl:if test="@type='xs:integer'">Type: an integer value.<br/></xsl:if><xsl:if test="@type='xs:boolean'"><br/>Type: a boolean value (true/false)</xsl:if><xsl:if test="@minOccurs='0'"><br/>This is an optional element.</xsl:if><xsl:if test="@minOccurs &gt; '0'"><br/>This is a required element.</xsl:if><xsl:if test="@maxOccurs"> And can occur<xsl:if test="@maxOccurs='unbounded'"> as many times as desired.</xsl:if><xsl:if test="@maxOccurs!='unbounded'"> <xsl:value-of select="@maxOccurs"/> times.</xsl:if></xsl:if><br/><xsl:value-of select="xs:annotation/xs:documentation"/>',
                                                            'Click to see specification for <xsl:value-of select="@name"/>'
                                                        ];
                                                        var temp_<xsl:value-of select="position()"/>Hints = new THints (temp_<xsl:value-of select="position()"/>HINTS_ITEMS, temp_<xsl:value-of select="position()"/>HINTS_CFG);
                                                    </script>
                                                    <xsl:if test="@type='xs:string' or @type='xs:boolean' or @type='xs:integer'">
                                                        <span class="hintSource" onmouseover="temp_{$elementPosition}Hints.show(0, this)" onmouseout="temp_{$elementPosition}Hints.hide()"><xsl:value-of select="@name"/></span>
                                                    </xsl:if>
                                                    <xsl:if test="@type!='xs:string' and @type!='xs:boolean' and @type!='xs:integer'">
                                                        <a href="{$elementTypeName}.html" class="hintSource" onmouseover="temp_{$elementPosition}Hints.show(0, this)" onmouseout="temp_{$elementPosition}Hints.hide()">
                                                            <xsl:value-of select="@name"/>
                                                        </a>
                                                    </xsl:if>
                                                </div>
                                            </xsl:for-each>
                                        </xsl:for-each>
                                    </div>
                                </xsl:for-each>
                                <xsl:for-each select="xs:annotation/xs:appinfo/html/body">
                                    <div id="complextypedocumentation">
                                        <xsl:copy-of select="."/>
                                    </div>
                                </xsl:for-each>
                            </body>
                        </html>
                    </xsltc:output>
                </xsl:for-each>             
                <xsl:for-each select="xs:element">
                    <xsl:variable name="elementTypeName"><xsl:value-of select="@type"/></xsl:variable>
                    <xsl:if test="position() = 1">
                        <div id="elementtitle">
                            Elements
                        </div>
                    </xsl:if>
                    <h5>
                    Element: <xsl:value-of select="@name"/>                      
                    <xsl:if test="@type!='xs:string' and @type!='xs:boolean' and @type!='xs:integer'">
                        Type: <a href="{$elementTypeName}.html"><xsl:value-of select="@type"/></a>
                    </xsl:if>
                    <div id="complextypeelementdocumentation">
                        <xsl:value-of select="xs:annotation/xs:documentation"/>
                    </div>
                    </h5>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
