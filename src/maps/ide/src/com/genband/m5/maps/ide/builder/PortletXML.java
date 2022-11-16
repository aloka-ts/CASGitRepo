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

public class PortletXML
{
  protected static String nl;
  public static synchronized PortletXML create(String lineSeparator)
  {
    nl = lineSeparator;
    PortletXML result = new PortletXML();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "" + NL + "<!--" + NL + "/**********************************************************************" + NL + "*\t GENBAND, Inc. Confidential and Proprietary" + NL + "*" + NL + "* This work contains valuable confidential and proprietary " + NL + "* information." + NL + "* Disclosure, use or reproduction without the written authorization of" + NL + "* GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc." + NL + "* is protected by the laws of the United States and other countries." + NL + "* If publication of the work should occur the following notice shall " + NL + "* apply:" + NL + "* " + NL + "* \"Copyright 2007 GENBAND, Inc.  All rights reserved.\"" + NL + "**********************************************************************/" + NL + "-->" + NL + "" + NL + "<portlet-app" + NL + "   xmlns=\"http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd\"" + NL + "   xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + NL + "   xsi:schemaLocation=\"http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd http://java.sun.com/xml/ns/portlet/portlet-app_1_0.xsd\"" + NL + "   version=\"1.0\">";
  protected final String TEXT_2 = NL + "    <portlet>" + NL + "      <portlet-name>";
  protected final String TEXT_3 = "</portlet-name>" + NL + "      <display-name>";
  protected final String TEXT_4 = "</display-name>" + NL + "      <portlet-class>org.jboss.portal.faces.portlet.JSFMetaBridgePortlet</portlet-class>" + NL + "      <init-param>" + NL + "         <name>VIEW</name>";
  protected final String TEXT_5 = NL + "         <value>/WEB-INF/jsf/";
  protected final String TEXT_6 = ".xhtml</value>" + NL + "      </init-param>";
  protected final String TEXT_7 = "     " + NL + "      <init-param>" + NL + "         <name>com.sun.faces.portlet.INIT_HELP</name>" + NL + "         <value>";
  protected final String TEXT_8 = "</value>" + NL + "      </init-param>";
  protected final String TEXT_9 = NL + "\t  <!--always expired-->" + NL + "      <expiration-cache>0</expiration-cache>" + NL + "      <supports>" + NL + "         <mime-type>text/html</mime-type>" + NL + "         <portlet-mode>VIEW</portlet-mode>";
  protected final String TEXT_10 = "         " + NL + "         <portlet-mode>HELP</portlet-mode>";
  protected final String TEXT_11 = NL + "      </supports>" + NL + "      <portlet-info>" + NL + "         <title>";
  protected final String TEXT_12 = "</title>" + NL + "         <keywords>management,admin</keywords>" + NL + "      </portlet-info>" + NL + "" + NL + "      " + NL + " \t  ";
  protected final String TEXT_13 = "            " + NL + "   \t\t<security-role-ref>" + NL + "           \t<role-name>";
  protected final String TEXT_14 = "</role-name>" + NL + "           \t<role-link>";
  protected final String TEXT_15 = "</role-link>" + NL + "\t\t</security-role-ref>";
  protected final String TEXT_16 = "               " + NL + "    </portlet>";
  protected final String TEXT_17 = NL + "\t  <!--always expired-->" + NL + "      <expiration-cache>0</expiration-cache>" + NL + "      <supports>" + NL + "         <mime-type>text/html</mime-type>" + NL + "         <portlet-mode>VIEW</portlet-mode>" + NL + "         <portlet-mode>HELP</portlet-mode>" + NL + "      </supports>" + NL + "      <portlet-info>" + NL + "         <title>";
  protected final String TEXT_18 = "</title>" + NL + "         <keywords>management,admin</keywords>" + NL + "      </portlet-info>" + NL;
  protected final String TEXT_19 = NL + "</portlet-app>";

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
    stringBuffer.append( objPortlet.getName() );
    stringBuffer.append(TEXT_4);
    
		mainJsfName = objPortlet.getListScreen().getJspName();
		mainJsfName = mainJsfName.substring(mainJsfName.lastIndexOf("/") + 1);

    stringBuffer.append(TEXT_5);
    stringBuffer.append( mainJsfName );
    stringBuffer.append(TEXT_6);
    
	  						if (objPortlet.isHelpEnabled()){

    stringBuffer.append(TEXT_7);
    stringBuffer.append( helpLoc.concat(objPortlet.getHelpScreen()) );
    stringBuffer.append(TEXT_8);
    
	  						}

    stringBuffer.append(TEXT_9);
    
							if (objPortlet.isHelpEnabled()){

    stringBuffer.append(TEXT_10);
    
							}

    stringBuffer.append(TEXT_11);
    stringBuffer.append( objPortlet.getName() );
    stringBuffer.append(TEXT_12);
    
	StringTokenizer returnStrToken = new StringTokenizer(objPortlet.getRoles(), "," );
	while(returnStrToken.hasMoreTokens()){
	String temp = returnStrToken.nextToken();

    stringBuffer.append(TEXT_13);
    stringBuffer.append( temp );
    stringBuffer.append(TEXT_14);
    stringBuffer.append( temp );
    stringBuffer.append(TEXT_15);
    
	}

    stringBuffer.append(TEXT_16);
    							
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

    stringBuffer.append(TEXT_2);
    stringBuffer.append( obPortlet.getName().replace(" ","_").concat("_").concat(siteMap.getName()).concat("_").concat(Integer.toString(objMainPage.getPageNo())).concat("_").concat(Integer.toString(objSubPage.getPageNo())).concat("_").concat(Integer.toString(obPlaceHolder.getPlaceHolderNo())).concat("_").concat(Integer.toString(obPortlet.getPortletNo())) );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( obPortlet.getName() );
    stringBuffer.append(TEXT_4);
    
		mainJsfName = obPortlet.getListScreen().getJspName();
		mainJsfName = mainJsfName.substring(mainJsfName.lastIndexOf("/") + 1);

    stringBuffer.append(TEXT_5);
    stringBuffer.append( mainJsfName );
    stringBuffer.append(TEXT_6);
    
								if (obPortlet.isHelpEnabled()){

    stringBuffer.append(TEXT_7);
    stringBuffer.append( helpLoc.concat(obPortlet.getHelpScreen()) );
    stringBuffer.append(TEXT_8);
    
								}

    stringBuffer.append(TEXT_17);
    stringBuffer.append( obPortlet.getName() );
    stringBuffer.append(TEXT_18);
    
	StringTokenizer returnStrToken = new StringTokenizer(obPortlet.getRoles(), "," );
	while(returnStrToken.hasMoreTokens()){
	String temp = returnStrToken.nextToken();

    stringBuffer.append(TEXT_13);
    stringBuffer.append( temp );
    stringBuffer.append(TEXT_14);
    stringBuffer.append( temp );
    stringBuffer.append(TEXT_15);
    
	}

    stringBuffer.append(TEXT_16);
    							
							}//end of closest for
		
						}//end of next for
						}//end of SubPage.isDummy
					}//if instance of sub-page closes
				}//mainPage() children for closes
				}//if mainpage.dummy closes
									
			}else if (siteMap.getChildren().get(numChild) instanceof Header){
				CPFPlugin.getDefault().info(" In PortletXML.java : Page is instanceof Header");
				//Child is Header 
			}else if (siteMap.getChildren().get(numChild) instanceof Footer){
				//Child is Main Page
				CPFPlugin.getDefault().info(" In PortletXML.java : Page is instanceof Footer");
			}
		}


    stringBuffer.append(TEXT_19);
    return stringBuffer.toString();
  }
}
