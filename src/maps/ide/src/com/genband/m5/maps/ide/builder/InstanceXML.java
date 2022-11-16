package com.genband.m5.maps.ide.builder;

import java.util.List;
import java.util.StringTokenizer;
import com.genband.m5.maps.ide.sitemap.model.Footer;
import com.genband.m5.maps.ide.sitemap.model.Header;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.SubPage;
import com.genband.m5.maps.ide.sitemap.util.SiteMapUtil;
import com.genband.m5.maps.ide.CPFPlugin;

public class InstanceXML
{
  protected static String nl;
  public static synchronized InstanceXML create(String lineSeparator)
  {
    nl = lineSeparator;
    InstanceXML result = new InstanceXML();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" standalone=\"yes\"?>" + NL + "<!--" + NL + "/**********************************************************************" + NL + "*\t GENBAND, Inc. Confidential and Proprietary" + NL + "*" + NL + "* This work contains valuable confidential and proprietary " + NL + "* information." + NL + "* Disclosure, use or reproduction without the written authorization of" + NL + "* GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc." + NL + "* is protected by the laws of the United States and other countries." + NL + "* If publication of the work should occur the following notice shall " + NL + "* apply: " + NL + "* " + NL + "* \"Copyright 2007 GENBAND, Inc.  All rights reserved.\"" + NL + "**********************************************************************/" + NL + "-->" + NL + "<!DOCTYPE deployments PUBLIC" + NL + "   \"-//JBoss Portal//DTD Portlet Instances 2.6//EN\"" + NL + "   \"http://www.jboss.org/portal/dtd/portlet-instances_2_6.dtd\">" + NL + "" + NL + "<deployments>" + NL;
  protected final String TEXT_2 = NL + "   <deployment>" + NL + "      <instance>" + NL + "         <instance-id>";
  protected final String TEXT_3 = "</instance-id>" + NL + "         <portlet-ref>";
  protected final String TEXT_4 = "</portlet-ref>" + NL + "\t\t <portlet-preferences>" + NL + "            <preference>" + NL + "               <name>expires</name>" + NL + "               <value>180</value>" + NL + "            </preference>" + NL + "\t\t </portlet-preferences>" + NL + "         <security-constraint>" + NL + "\t\t         <policy-permission>\t";
  protected final String TEXT_5 = "            " + NL + "                   <role-name>";
  protected final String TEXT_6 = "</role-name>" + NL + "                   <action-name>view</action-name>";
  protected final String TEXT_7 = "               " + NL + "                 </policy-permission>" + NL + "         </security-constraint>" + NL + "      </instance>" + NL + "   </deployment>";
  protected final String TEXT_8 = "</portlet-ref>" + NL + "\t\t <portlet-preferences>" + NL + "            <preference>" + NL + "               <name>expires</name>" + NL + "               <value>180</value>" + NL + "            </preference>" + NL + "\t\t </portlet-preferences>" + NL + "\t\t <security-constraint>" + NL + "\t\t            <policy-permission>\t";
  protected final String TEXT_9 = "            " + NL + "               \t\t\t<role-name>";
  protected final String TEXT_10 = "</role-name>" + NL + "               \t\t\t<action-name>view</action-name>";
  protected final String TEXT_11 = "               " + NL + "                    </policy-permission>" + NL + "              </security-constraint>" + NL + "      </instance>" + NL + "   </deployment>";
  protected final String TEXT_12 = NL + "</deployments>";
  protected final String TEXT_13 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	//SiteMap object will come to this file as an argument
	//SiteMap siteMap = (SiteMap)argument;

    stringBuffer.append(TEXT_1);
    
	//SiteMap object will come to this file as an argument
	SiteMap siteMap = (SiteMap)argument;
	for(int numChild=0 ; numChild < siteMap.getChildren().size() ; numChild++){
								
			if (siteMap.getChildren().get(numChild) instanceof MainPage){
				MainPage objMainPage = (MainPage)siteMap.getChildren().get(numChild);
				if (objMainPage.isDummy()==false){
				for(int num=0 ; num < objMainPage.getChildren().size() ; num++){
					
					//If child of MainPage is PlaceHolder
					if (objMainPage.getChildren().get(num) instanceof PlaceHolder){
						PlaceHolder objPlaceHolder = (PlaceHolder)objMainPage.getChildren().get(num);
						CPFPlugin.getDefault().info("In InstanceXML.java : In PlaceHolder object : "+objPlaceHolder.getName());
						for(int numCh=0 ; numCh < objPlaceHolder.getChildren().size() ; numCh++){
							Portlet objPortlet = (Portlet)objPlaceHolder.getChildren().get(numCh);

    stringBuffer.append(TEXT_2);
    stringBuffer.append( objPortlet.getName().replace(" ","_").concat("_").concat(siteMap.getName()).concat("_").concat(Integer.toString(objMainPage.getPageNo())).concat("_").concat(Integer.toString(objPlaceHolder.getPlaceHolderNo())).concat("_").concat(Integer.toString(objPortlet.getPortletNo())).concat("Instance") );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( objPortlet.getName().replace(" ","_").concat("_").concat(siteMap.getName()).concat("_").concat(Integer.toString(objMainPage.getPageNo())).concat("_").concat(Integer.toString(objPlaceHolder.getPlaceHolderNo())).concat("_").concat(Integer.toString(objPortlet.getPortletNo())) );
    stringBuffer.append(TEXT_4);
    
	StringTokenizer retStr = new StringTokenizer(objPortlet.getRoles(), "," );
	while(retStr.hasMoreTokens()){

    stringBuffer.append(TEXT_5);
    stringBuffer.append( retStr.nextToken() );
    stringBuffer.append(TEXT_6);
    
	}

    stringBuffer.append(TEXT_7);
    				
							}//end of closest for
					}//end of if for placeholder	
			
					//If child of MainPage is SubPage
					else if (objMainPage.getChildren().get(num) instanceof SubPage){
						SubPage objSubPage = (SubPage)objMainPage.getChildren().get(num);
						if (objSubPage.isDummy()==false){
						CPFPlugin.getDefault().info(" In InstanceXML.java : In non-dummy SubPage object : "+objSubPage.getName());
						for(int numCh2=0 ; numCh2 < objSubPage.getChildren().size() ; numCh2++){
							
							//Assuming children of SubPage can only be a PlaceHolder
							PlaceHolder obPlaceHolder = (PlaceHolder)objSubPage.getChildren().get(numCh2);
							for(int numC=0 ; numC < obPlaceHolder.getChildren().size() ; numC++){
								
								Portlet obPortlet = (Portlet)obPlaceHolder.getChildren().get(numC);

    stringBuffer.append(TEXT_2);
    stringBuffer.append( obPortlet.getName().replace(" ","_").concat("_").concat(siteMap.getName()).concat("_").concat(Integer.toString(objMainPage.getPageNo())).concat("_").concat(Integer.toString(objSubPage.getPageNo())).concat("_").concat(Integer.toString(obPlaceHolder.getPlaceHolderNo())).concat("_").concat(Integer.toString(obPortlet.getPortletNo())).concat("Instance") );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( obPortlet.getName().replace(" ","_").concat("_").concat(siteMap.getName()).concat("_").concat(Integer.toString(objMainPage.getPageNo())).concat("_").concat(Integer.toString(objSubPage.getPageNo())).concat("_").concat(Integer.toString(obPlaceHolder.getPlaceHolderNo())).concat("_").concat(Integer.toString(obPortlet.getPortletNo())) );
    stringBuffer.append(TEXT_8);
    
	StringTokenizer retStr = new StringTokenizer(obPortlet.getRoles(), "," );
	while(retStr.hasMoreTokens()){

    stringBuffer.append(TEXT_9);
    stringBuffer.append( retStr.nextToken() );
    stringBuffer.append(TEXT_10);
    
	}

    stringBuffer.append(TEXT_11);
    								
								}//end of closest for
		
						}//end of next for
						}//end of SubPage.isDummy
					}//if instance of sub-page closes
					}//mainPage() children for closes
					}//if mainpage.dummy closes
									
			}else if (siteMap.getChildren().get(numChild) instanceof Header){
				CPFPlugin.getDefault().info(" In InstanceXML.java : Page is instanceof Header");
				//Child is Header 
			}else if (siteMap.getChildren().get(numChild) instanceof Footer){
				//Child is Main Page
				CPFPlugin.getDefault().info(" In InstanceXML.java : Page is instanceof Footer");
			}
		}


    stringBuffer.append(TEXT_12);
    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}
