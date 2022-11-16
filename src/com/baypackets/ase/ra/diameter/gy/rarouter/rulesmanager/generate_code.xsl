<?xml version="1.0" encoding="ISO-8859-1"?><xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="pattern">
   public boolean _evaluate (String param[],ArrayList list) {  
    int k=0;
    return  (
  <xsl:if test="(and)">
	  <xsl:apply-templates mode="and" select="and"/> 
	  (true)
  </xsl:if>
  <xsl:if test="(or)">
	  <xsl:apply-templates mode="or" select="or"/> 
	  (false)
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
	     );
    }	     
} 
  </xsl:template>


  <!-- The AND condition template -->
  <xsl:template match="and">
	  (
        <xsl:apply-templates mode="and" select="equal"/> 
        <xsl:apply-templates mode="and" select="contains"/> 
        <xsl:apply-templates mode="and" select="exists"/> 
        <xsl:apply-templates mode="and" select="subdomain-of"/> 
        <xsl:apply-templates mode="and" select="not"/> 
        <xsl:apply-templates mode="and" select="and"/> 
        <xsl:apply-templates mode="and"  select="or"/> 
      (true)
    )
   </xsl:template>
  <xsl:template mode="and" match="and">
	  (
        <xsl:apply-templates mode="and" select="equal"/> 
        <xsl:apply-templates mode="and" select="contains"/> 
        <xsl:apply-templates mode="and" select="exists"/> 
        <xsl:apply-templates mode="and" select="subdomain-of"/> 
        <xsl:apply-templates mode="and" select="not"/> 
        <xsl:apply-templates mode="and" select="and"/> 
        <xsl:apply-templates mode="and"  select="or"/> 
      (true)
    ) <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
   </xsl:template>
  <xsl:template mode="or" match="and">
	  (
        <xsl:apply-templates mode="and" select="equal"/> 
        <xsl:apply-templates mode="and" select="contains"/> 
        <xsl:apply-templates mode="and" select="exists"/> 
        <xsl:apply-templates mode="and" select="subdomain-of"/> 
        <xsl:apply-templates mode="and" select="not"/> 
        <xsl:apply-templates mode="and" select="and"/> 
        <xsl:apply-templates mode="and"  select="or"/> 
      (true)
    ) ||
   </xsl:template>
  <xsl:template mode="not" match="and">
	  (!(
        <xsl:apply-templates mode="and" select="equal"/> 
        <xsl:apply-templates mode="and" select="contains"/> 
        <xsl:apply-templates mode="and" select="exists"/> 
        <xsl:apply-templates mode="and" select="subdomain-of"/> 
        <xsl:apply-templates mode="and" select="not"/> 
        <xsl:apply-templates mode="and" select="and"/> 
        <xsl:apply-templates mode="and"  select="or"/> 
      (true)
    ))
   </xsl:template>
   <!-- end of AND condition template -->




   <!-- The NOT condition template -->
   <xsl:template match="not">
	(
	<xsl:apply-templates mode="not" select="equal"/> 
        <xsl:apply-templates mode="not" select="contains"/> 
        <xsl:apply-templates mode="not" select="exists"/> 
        <xsl:apply-templates mode="not" select="subdomain-of"/> 
        <xsl:apply-templates mode="not" select="and"/> 
        <xsl:apply-templates mode="not" select="or"/> 
	<xsl:apply-templates mode="not" select="not"/> 
	)
   </xsl:template>

   <xsl:template mode="and" match="not">
	(
	<xsl:apply-templates mode="not" select="equal"/> 
        <xsl:apply-templates mode="not" select="contains"/> 
        <xsl:apply-templates mode="not" select="exists"/> 
        <xsl:apply-templates mode="not" select="subdomain-of"/> 
        <xsl:apply-templates mode="not" select="and"/> 
        <xsl:apply-templates mode="not" select="or"/> 
	<xsl:apply-templates mode="not" select="not"/> 
	)<xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
   </xsl:template>
   <xsl:template mode="or" match="not">
	(
	<xsl:apply-templates mode="not" select="equal"/> 
        <xsl:apply-templates mode="not" select="contains"/> 
        <xsl:apply-templates mode="not" select="exists"/> 
        <xsl:apply-templates mode="not" select="subdomain-of"/> 
        <xsl:apply-templates mode="not" select="and"/> 
        <xsl:apply-templates mode="not" select="or"/> 
	<xsl:apply-templates mode="not" select="not"/> 
        )||	
   </xsl:template>
   <xsl:template mode="not" match="not">
	!(
	<xsl:apply-templates mode="not" select="equal"/> 
        <xsl:apply-templates mode="not" select="contains"/> 
        <xsl:apply-templates mode="not" select="exists"/> 
        <xsl:apply-templates mode="not" select="subdomain-of"/> 
        <xsl:apply-templates mode="not" select="and"/> 
        <xsl:apply-templates mode="not" select="or"/> 
	<xsl:apply-templates mode="not" select="not"/> 
        )	
   </xsl:template>
   <!-- end of NOT condition template -->


 <!-- The OR condition template -->
 <xsl:template match="or">
 (
        <xsl:apply-templates mode="or" select="and"/> 
        <xsl:apply-templates mode="or" select="or"/> 
        <xsl:apply-templates mode="or" select="not"/> 
        <xsl:apply-templates mode="or" select="equal"/> 
        <xsl:apply-templates mode="or" select="contains"/> 
        <xsl:apply-templates mode="or" select="exists"/> 
        <xsl:apply-templates mode="or" select="subdomain-of"/> 
  (false)
  )
 </xsl:template>
 <xsl:template mode="and" match="or">
 (
  <xsl:apply-templates mode="or" select="and"/> 
	  <xsl:apply-templates mode="or" select="or"/> 
	  <xsl:apply-templates mode="or" select="not"/> 
	  <xsl:apply-templates mode="or" select="equal"/> 
	  <xsl:apply-templates mode="or" select="contains"/> 
	  <xsl:apply-templates mode="or" select="exists"/> 
	  <xsl:apply-templates mode="or" select="subdomain-of"/> 
  (false)
  ) <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
 </xsl:template>
 <xsl:template mode="or" match="or">
 (
  <xsl:apply-templates mode="or" select="and"/> 
	  <xsl:apply-templates mode="or" select="or"/> 
	  <xsl:apply-templates mode="or" select="not"/> 
	  <xsl:apply-templates mode="or" select="equal"/> 
	  <xsl:apply-templates mode="or" select="contains"/> 
	  <xsl:apply-templates mode="or" select="exists"/> 
	  <xsl:apply-templates mode="or" select="subdomain-of"/> 
  (false)
  ) ||
 </xsl:template>
 <xsl:template mode="not" match="or">
 (!(
  <xsl:apply-templates mode="or" select="and"/> 
	  <xsl:apply-templates mode="or" select="or"/> 
	  <xsl:apply-templates mode="or" select="not"/> 
	  <xsl:apply-templates mode="or" select="equal"/> 
	  <xsl:apply-templates mode="or" select="contains"/> 
	  <xsl:apply-templates mode="or" select="exists"/> 
	  <xsl:apply-templates mode="or" select="subdomain-of"/> 
  (false)
  ))
 </xsl:template>
 <!-- end of OR condition template --> 


 <!-- the EQUAL template -->
<xsl:template match="equal">
        <xsl:variable name="A"><xsl:apply-templates select="var"/></xsl:variable>
        <xsl:choose>
             <xsl:when test="starts-with($A,'request.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(0)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(0)).containsValue(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                     <xsl:when test="@ignore-case ='true'">
                        (((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).equalsIgnoreCase("<xsl:apply-templates select="value"/>")))
                    </xsl:when>
                    <xsl:otherwise>
                      (((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).equals("<xsl:apply-templates select="value"/>") ))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.to.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(1)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(1)).containsValue(((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        (((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).equalsIgnoreCase("<xsl:apply-templates select="value"/>")))
                    </xsl:when>
                    <xsl:otherwise>
                      (((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).equals("<xsl:apply-templates select="value"/>") ))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.from.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(2)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        (((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).equalsIgnoreCase("<xsl:apply-templates select="value"/>")))
                    </xsl:when>
               <xsl:otherwise>
                      (((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).equals("<xsl:apply-templates select="value"/>") ))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                ((param[<xsl:apply-templates select="var"/>] != null)
                <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                <xsl:choose>
                      <xsl:when test="@ignore-case ='true'">
                          param[<xsl:apply-templates select="var"/>].equalsIgnoreCase("<xsl:apply-templates select="value"/>"))
                       </xsl:when>
                       <xsl:otherwise>
                            param[<xsl:apply-templates select="var"/>].equals("<xsl:apply-templates select="value"/>"))
                       </xsl:otherwise>
                 </xsl:choose>
          </xsl:otherwise>
       </xsl:choose>
</xsl:template>



<xsl:template mode="and" match="equal">
           <xsl:variable name="A"><xsl:apply-templates select="var"/></xsl:variable>
         <xsl:choose>
            <xsl:when test="starts-with($A,'request.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(0)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(0)).containsValue(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        (((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).equalsIgnoreCase("<xsl:apply-templates select="value"/>")))
                    </xsl:when>
                    <xsl:otherwise>
                      (((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).equals("<xsl:apply-templates select="value"/>") ))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.to.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(1)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(1)).containsValue(((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        (((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).equalsIgnoreCase("<xsl:apply-templates select="value"/>")))
                    </xsl:when>
                    <xsl:otherwise>
                      (((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).equals("<xsl:apply-templates select="value"/>") ))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.from.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(2)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")))  
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        (((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).equalsIgnoreCase("<xsl:apply-templates select="value"/>")))
                    </xsl:when>
                    <xsl:otherwise>
                      (((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).equals("<xsl:apply-templates select="value"/>") ))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
         <xsl:otherwise>
         ((param[<xsl:apply-templates select="var"/>] != null)
                         <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                         <xsl:choose>
                           <xsl:when test="@ignore-case ='true'">
                                param[<xsl:apply-templates select="var"/>].equalsIgnoreCase("<xsl:apply-templates select="value"/>"))
                       </xsl:when>
                 <xsl:otherwise>
                            param[<xsl:apply-templates select="var"/>].equals("<xsl:apply-templates select="value"/>"))
                     </xsl:otherwise>
                    </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
    <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
 </xsl:template>


<xsl:template mode="or" match="equal">
<xsl:variable name="A"><xsl:apply-templates select="var"/></xsl:variable>
        <xsl:choose>
             <xsl:when test="starts-with($A,'request.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(0)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(0)).containsValue(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                     <xsl:when test="@ignore-case ='true'">
                        (((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).equalsIgnoreCase("<xsl:apply-templates select="value"/>")))
                    </xsl:when>
                    <xsl:otherwise>
                      (((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).equals("<xsl:apply-templates select="value"/>") ))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.to.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(1)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(1)).containsValue(((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        (((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).equalsIgnoreCase("<xsl:apply-templates select="value"/>")))
                    </xsl:when>
                    <xsl:otherwise>
                      (((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).equals("<xsl:apply-templates select="value"/>") ))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.from.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(2)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        (((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).equalsIgnoreCase("<xsl:apply-templates select="value"/>")))
                    </xsl:when>
               <xsl:otherwise>
                      (((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).equals("<xsl:apply-templates select="value"/>") ))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                ((param[<xsl:apply-templates select="var"/>] != null)
                <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                <xsl:choose>
                      <xsl:when test="@ignore-case ='true'">
                          param[<xsl:apply-templates select="var"/>].equalsIgnoreCase("<xsl:apply-templates select="value"/>"))
                       </xsl:when>
                       <xsl:otherwise>
                            param[<xsl:apply-templates select="var"/>].equals("<xsl:apply-templates select="value"/>"))
                       </xsl:otherwise>
                 </xsl:choose>
          </xsl:otherwise>
       </xsl:choose>
       ||
</xsl:template>      

<xsl:template mode="not" match="equal">
<xsl:variable name="A"><xsl:apply-templates select="var"/></xsl:variable>
        <xsl:choose>
             <xsl:when test="starts-with($A,'request.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(0)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(0)).containsValue(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                     <xsl:when test="@ignore-case ='true'">
                        (!(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).equalsIgnoreCase("<xsl:apply-templates select="value"/>"))))
                    </xsl:when>
                    <xsl:otherwise>
                      (!(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).equals("<xsl:apply-templates select="value"/>") )))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.to.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(1)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(1)).containsValue(((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        (!(((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).equalsIgnoreCase("<xsl:apply-templates select="value"/>"))))
                    </xsl:when>
                    <xsl:otherwise>
                      (!(((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).equals("<xsl:apply-templates select="value"/>") )))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.from.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(2)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        (!(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).equalsIgnoreCase("<xsl:apply-templates select="value"/>"))))
                    </xsl:when>
               <xsl:otherwise>
                      (!(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).equals("<xsl:apply-templates select="value"/>") )))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                ((param[<xsl:apply-templates select="var"/>] != null)
                <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                <xsl:choose>
                      <xsl:when test="@ignore-case ='true'">
                          (!param[<xsl:apply-templates select="var"/>].equalsIgnoreCase("<xsl:apply-templates select="value"/>")))
                       </xsl:when>
                       <xsl:otherwise>
                           (!param[<xsl:apply-templates select="var"/>].equals("<xsl:apply-templates select="value"/>")))
                       </xsl:otherwise>
                 </xsl:choose>
          </xsl:otherwise>
       </xsl:choose>
</xsl:template>  

<!--   -->



<!-- CONTAINS template -->
<xsl:template match="contains">
<xsl:variable name="A"><xsl:apply-templates select="var"/></xsl:variable>
        <xsl:choose>
             <xsl:when test="starts-with($A,'request.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(0)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(0)).containsValue(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                     <xsl:when test="@ignore-case ='true'">
                       ((((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))                    
                    </xsl:when>
                    <xsl:otherwise>
                       ((((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.to.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(1)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(1)).containsValue(((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                     ((((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))                    
                    </xsl:when>
                    <xsl:otherwise>
                        ((((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))               
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.from.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(2)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        ((((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:when>
               <xsl:otherwise>
                       ((((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                ((param[<xsl:apply-templates select="var"/>] != null)
                <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        (param[<xsl:apply-templates select="var"/>].indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1)
                   </xsl:when>
                   <xsl:otherwise>
                        (param[<xsl:apply-templates select="var"/>].indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1)
                   </xsl:otherwise>
               </xsl:choose>
          </xsl:otherwise>
       </xsl:choose>
</xsl:template>  


<xsl:template mode="and" match="contains">
<xsl:variable name="A"><xsl:apply-templates select="var"/></xsl:variable>
        <xsl:choose>
             <xsl:when test="starts-with($A,'request.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(0)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(0)).containsValue(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                     <xsl:when test="@ignore-case ='true'">
                       ((((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:when>
                    <xsl:otherwise>
                       ((((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.to.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(1)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(1)).containsValue(((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                     ((((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:when>
                    <xsl:otherwise>
                        ((((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.from.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(2)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        ((((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:when>
               <xsl:otherwise>
                       ((((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                ((param[<xsl:apply-templates select="var"/>] != null)
                <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        (param[<xsl:apply-templates select="var"/>].indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1)
                   </xsl:when>
                   <xsl:otherwise>
                        (param[<xsl:apply-templates select="var"/>].indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1)
                   </xsl:otherwise>
               </xsl:choose>
          </xsl:otherwise>
       </xsl:choose>
    <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
</xsl:template> 
 
<xsl:template mode="or" match="contains">
<xsl:variable name="A"><xsl:apply-templates select="var"/></xsl:variable>        
        <xsl:choose>
             <xsl:when test="starts-with($A,'request.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(0)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(0)).containsValue(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                     <xsl:when test="@ignore-case ='true'">
                       ((((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:when>
                    <xsl:otherwise>
                       ((((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.to.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(1)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(1)).containsValue(((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                     ((((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:when>
                    <xsl:otherwise>
                        ((((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.from.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(2)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        ((((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:when>
               <xsl:otherwise>
                       ((((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                ((param[<xsl:apply-templates select="var"/>] != null)
                <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        (param[<xsl:apply-templates select="var"/>].indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1)
                   </xsl:when>
                   <xsl:otherwise>
                        (param[<xsl:apply-templates select="var"/>].indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1)
                   </xsl:otherwise>
               </xsl:choose>
          </xsl:otherwise>
       </xsl:choose>
        ||
</xsl:template>  

<xsl:template mode ="not" match="contains">
<xsl:variable name="A"><xsl:apply-templates select="var"/></xsl:variable>
        <xsl:choose>
             <xsl:when test="starts-with($A,'request.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(0)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(0)).containsValue(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                     <xsl:when test="@ignore-case ='true'">
                       (!((((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1)))
                    </xsl:when>
                    <xsl:otherwise>
                       (!((((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>")).indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1)))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.to.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(1)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(1)).containsValue(((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                     (!((((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1)))
                    </xsl:when>
                    <xsl:otherwise>
                        (!((((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>")).indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1)))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="starts-with($A,'request.from.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(2)!=null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")))
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        (!((((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1)))
                    </xsl:when>
               <xsl:otherwise>
                       (!((((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")).indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1)))
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                ((param[<xsl:apply-templates select="var"/>] != null)
                <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                <xsl:choose>
                    <xsl:when test="@ignore-case ='true'">
                        !((param[<xsl:apply-templates select="var"/>].indexOf((new String("<xsl:apply-templates select="value"/>")).toLowerCase()))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                   </xsl:when>
                   <xsl:otherwise>
                        !((param[<xsl:apply-templates select="var"/>].indexOf("<xsl:apply-templates select="value"/>"))<xsl:text disable-output-escaping="yes">&gt;</xsl:text>-1))
                   </xsl:otherwise>
               </xsl:choose>
          </xsl:otherwise>
       </xsl:choose>
</xsl:template>  
<!--  -->



<!-- SUBDOMAIN template, requires a fucntion in base class -->
<xsl:template match="subdomain-of">
    (subdomainOf (param[<xsl:apply-templates select="var"/>], "<xsl:apply-templates select="value"/>"))
</xsl:template>
<xsl:template mode="and" match="subdomain-of">
    (subdomainOf (param[<xsl:apply-templates select="var"/>], "<xsl:apply-templates select="value"/>"))
    <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
</xsl:template>
<xsl:template mode="or" match="subdomain-of">
    (subdomainOf (param[<xsl:apply-templates select="var"/>], "<xsl:apply-templates select="value"/>"))
    ||
</xsl:template>
<xsl:template mode="not" match="subdomain-of">
    (!(subdomainOf (param[<xsl:apply-templates select="var"/>], "<xsl:apply-templates select="value"/>")))
</xsl:template>
<!--  -->



<!-- EXISTS template -->
<xsl:template match="exists">
<xsl:variable name="A"><xsl:apply-templates select="var"/></xsl:variable>
        <xsl:choose>
             <xsl:when test="starts-with($A,'request.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(0)!=null)
	         <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
	         ((HashMap)list.get(0)).containsValue(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>"))))
            </xsl:when>
            <xsl:when test="starts-with($A,'request.to.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(1)!=null)
	           <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
	           ((HashMap)list.get(0)).containsValue(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>"))))
             </xsl:when>
            <xsl:when test="starts-with($A,'request.from.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(2)!=null)
                  <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>"))))
            </xsl:when>
            <xsl:otherwise>
                (param[<xsl:apply-templates select="var"/>] != null)
          </xsl:otherwise>
       </xsl:choose>
</xsl:template>

<xsl:template mode="and" match="exists">
<xsl:variable name="A"><xsl:apply-templates select="var"/></xsl:variable>
        <xsl:choose>
             <xsl:when test="starts-with($A,'request.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(0)!=null)
                  <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(0)).containsValue(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>"))))
            </xsl:when>
            <xsl:when test="starts-with($A,'request.to.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(1)!=null)
                  <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(1)).containsValue(((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>"))))
            </xsl:when>
            <xsl:when test="starts-with($A,'request.from.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(2)!=null)
                  <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>"))))
            </xsl:when>
            <xsl:otherwise>
                (param[<xsl:apply-templates select="var"/>] != null)
          </xsl:otherwise>
       </xsl:choose>
     <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
</xsl:template>

<xsl:template mode="or" match="exists">
<xsl:variable name="A"><xsl:apply-templates select="var"/></xsl:variable>
        <xsl:choose>
             <xsl:when test="starts-with($A,'request.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(0)!=null)
                  <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(0)).containsValue(((String)((HashMap)list.get(0)).get("<xsl:apply-templates select="var"/>"))))
            </xsl:when>
            <xsl:when test="starts-with($A,'request.to.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(1)!=null)
                  <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(1)).containsValue(((String)((HashMap)list.get(1)).get("<xsl:apply-templates select="var"/>"))))
            </xsl:when>
            <xsl:when test="starts-with($A,'request.from.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(2)!=null)
                  <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 ((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>"))))
            </xsl:when>
            <xsl:otherwise>
                (param[<xsl:apply-templates select="var"/>] != null)
          </xsl:otherwise>
       </xsl:choose>
       ||
</xsl:template> 


<xsl:template mode="not" match="exists">
<xsl:variable name="A"><xsl:apply-templates select="var"/></xsl:variable>
        <xsl:choose>
             <xsl:when test="starts-with($A,'request.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(0)!=null)
                  <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (!((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")))))
            </xsl:when>
            <xsl:when test="starts-with($A,'request.to.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(1)!=null)
                  <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (!((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")))))
            </xsl:when>
            <xsl:when test="starts-with($A,'request.from.uri.param')">
                 ( (list != null)
                 <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (list.get(2)!=null)
                  <xsl:text disable-output-escaping="yes">&amp;&amp;</xsl:text>
                 (!((HashMap)list.get(2)).containsValue(((String)((HashMap)list.get(2)).get("<xsl:apply-templates select="var"/>")))))
            </xsl:when>
            <xsl:otherwise>
                (!(param[<xsl:apply-templates select="var"/>] != null))
          </xsl:otherwise>
       </xsl:choose>
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
