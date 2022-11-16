package com.genband.m5.maps.ide.builder;

import java.util.List;
import com.genband.m5.maps.ide.sitemap.model.Footer;
import com.genband.m5.maps.ide.sitemap.model.Header;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.SubPage;
import com.genband.m5.maps.ide.sitemap.util.SiteMapUtil;
import java.util.StringTokenizer;
import com.genband.m5.maps.ide.CPFPlugin;

public class JbossPortletXML
{
  protected static String nl;
  public static synchronized JbossPortletXML create(String lineSeparator)
  {
    nl = lineSeparator;
    JbossPortletXML result = new JbossPortletXML();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = NL + "<!--" + NL + "/**********************************************************************" + NL + "*\t GENBAND, Inc. Confidential and Proprietary" + NL + "*" + NL + "* This work contains valuable confidential and proprietary " + NL + "* information." + NL + "* Disclosure, use or reproduction without the written authorization of" + NL + "* GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc." + NL + "* is protected by the laws of the United States and other countries." + NL + "* If publication of the work should occur the following notice shall " + NL + "* apply:" + NL + "* " + NL + "* \"Copyright 2007 GENBAND, Inc.  All rights reserved.\"" + NL + "**********************************************************************/" + NL + "-->" + NL + "" + NL + "<!DOCTYPE portlet-app PUBLIC" + NL + "   \"-//JBoss Portal//DTD JBoss Portlet 2.6//EN\"" + NL + "   \"http://www.jboss.org/portal/dtd/jboss-portlet_2_6.dtd\">" + NL + "" + NL + "\t<portlet-app>";
  protected final String TEXT_2 = NL + "    \t<portlet>" + NL + "      \t\t<portlet-name>";
  protected final String TEXT_3 = "</portlet-name>" + NL + "      \t\t<header-content>" + NL + "\t\t\t\t<script src=\"/js/mode.js\" language=\"javascript\"></script>" + NL + "\t\t\t\t<script src=\"/js/delete.js\" language=\"javascript\"></script>" + NL + "      \t\t</header-content>" + NL + "    \t</portlet>";
  protected final String TEXT_4 = NL + "  \t  \t<portlet>" + NL + "     \t \t<portlet-name>";
  protected final String TEXT_5 = "</portlet-name>" + NL + "    \t \t<header-content>" + NL + "\t\t\t\t<script src=\"/js/mode.js\" language=\"javascript\"></script>" + NL + "\t\t\t\t<script src=\"/js/delete.js\" language=\"javascript\"></script>" + NL + "    \t\t </header-content>             " + NL + "    \t</portlet>";
  protected final String TEXT_6 = NL + "\t</portlet-app>";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	//SiteMap object will come to this file as an argument
	SiteMap siteMap = (SiteMap)argument;

    stringBuffer.append(TEXT_1);
    
	String jsfLoc = "//WEB-INF//jsf//";
	String helpLoc = "//WEB-INF//help//";
	String mainJsfName = null; 	//Holds the base entity list screen JSF Name...

	for(int numChild=0 ; numChild < siteMap.getChildren().size() ; numChild++){
								
			if (siteMap.getChildren().get(numChild) instanceof MainPage){
				MainPage objMainPage = (MainPage)siteMap.getChildren().get(numChild);
				if (objMainPage.isDummy()==false){
				
				for(int num=0 ; num < objMainPage.getChildren().size() ; num++){
					
					//If child of MainPage is PlaceHolder
					if (objMainPage.getChildren().get(num) instanceof PlaceHolder){
						PlaceHolder objPlaceHolder = (PlaceHolder)objMainPage.getChildren().get(num);
						CPFPlugin.getDefault().info(" In PortletXML.java : In PlaceHolder object : "+objPlaceHolder.getName());
						for(int numCh=0 ; numCh < objPlaceHolder.getChildren().size() ; numCh++){
							Portlet objPortlet = (Portlet)objPlaceHolder.getChildren().get(numCh);
							
							

    stringBuffer.append(TEXT_2);
    stringBuffer.append( objPortlet.getName().replace(" ","_").concat("_").concat(siteMap.getName()).concat("_").concat(Integer.toString(objMainPage.getPageNo())).concat("_").concat(Integer.toString(objPlaceHolder.getPlaceHolderNo())).concat("_").concat(Integer.toString(objPortlet.getPortletNo())) );
    stringBuffer.append(TEXT_3);
    							
							}//end of closest for
					}//end of if for placeholder	
					
					//If child of MainPage is SubPage
					else if (objMainPage.getChildren().get(num) instanceof SubPage){
						SubPage objSubPage = (SubPage)objMainPage.getChildren().get(num);
						
						if (objSubPage.isDummy()==false){
						CPFPlugin.getDefault().info(" In PortletXML.java : In non-dummy SubPage object : "+objSubPage.getName());
						for(int numCh2=0 ; numCh2 < objSubPage.getChildren().size() ; numCh2++){
							
							//Assuming children of SubPage can only be a PlaceHolder
							PlaceHolder obPlaceHolder = (PlaceHolder)objSubPage.getChildren().get(numCh2);
							for(int numC=0 ; numC < obPlaceHolder.getChildren().size() ; numC++){
								
								Portlet obPortlet = (Portlet)obPlaceHolder.getChildren().get(numC);

    stringBuffer.append(TEXT_4);
    stringBuffer.append( obPortlet.getName().replace(" ","_").concat("_").concat(siteMap.getName()).concat("_").concat(Integer.toString(objMainPage.getPageNo())).concat("_").concat(Integer.toString(objSubPage.getPageNo())).concat("_").concat(Integer.toString(obPlaceHolder.getPlaceHolderNo())).concat("_").concat(Integer.toString(obPortlet.getPortletNo())) );
    stringBuffer.append(TEXT_5);
    							
							}//end of closest for
		
						}//end of next for
						}//end of SubPage.isDummy
					}//if instance of sub-page closes
				}//mainPage() children for closes
				}//if mainpage.dummy closes
									
			}
		}

    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}
