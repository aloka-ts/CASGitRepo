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

public class ObjectXML
{
  protected static String nl;
  public static synchronized ObjectXML create(String lineSeparator)
  {
    nl = lineSeparator;
    ObjectXML result = new ObjectXML();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "" + NL + "<!--" + NL + "/**********************************************************************" + NL + "*\t GENBAND, Inc. Confidential and Proprietary" + NL + "*" + NL + "* This work contains valuable confidential and proprietary " + NL + "* information." + NL + "* Disclosure, use or reproduction without the written authorization of" + NL + "* GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc." + NL + "* is protected by the laws of the United States and other countries." + NL + "* If publication of the work should occur the following notice shall " + NL + "* apply:" + NL + "* " + NL + "* \"Copyright 2007 GENBAND, Inc.  All rights reserved.\"" + NL + "**********************************************************************/" + NL + "-->" + NL + "" + NL + "<deployments>" + NL + "   <deployment>" + NL + "      <parent-ref/>" + NL + "      <if-exists>overwrite</if-exists>" + NL + "      <portal>" + NL + "         <portal-name>";
  protected final String TEXT_2 = "</portal-name>" + NL + "         <properties>" + NL + "            <property>" + NL + "               <name>layout.id</name>" + NL + "               <value>";
  protected final String TEXT_3 = "</value>" + NL + "            </property>" + NL + "            <property>" + NL + "               <name>theme.id</name>" + NL + "               <value>";
  protected final String TEXT_4 = "</value>" + NL + "            </property>" + NL + "            <property>" + NL + "               <name>theme.renderSetId</name>" + NL + "               <value>divRenderer</value>" + NL + "            </property>" + NL + "         </properties>" + NL + "         <supported-modes>" + NL + "            <mode>view</mode>" + NL + "            <mode>help</mode>" + NL + "         </supported-modes>" + NL + "         <supported-window-states>" + NL + "            <window-state>normal</window-state>" + NL + "            <window-state>minimized</window-state>" + NL + "            <window-state>maximized</window-state>" + NL + "         </supported-window-states>" + NL + "         <security-constraint>" + NL + "            <policy-permission>";
  protected final String TEXT_5 = "            " + NL + "               <role-name>";
  protected final String TEXT_6 = "</role-name>" + NL + "               <action-name>view</action-name>";
  protected final String TEXT_7 = "               " + NL + "            </policy-permission>" + NL + "         </security-constraint>";
  protected final String TEXT_8 = NL + "         <page>";
  protected final String TEXT_9 = NL + "\t\t<page-name>default</page-name>";
  protected final String TEXT_10 = NL + "         <page-name>";
  protected final String TEXT_11 = "</page-name>";
  protected final String TEXT_12 = NL + "         <properties>" + NL + "            <property>" + NL + "               <name>order</name>" + NL + "               <value>";
  protected final String TEXT_13 = "</value>" + NL + "            </property>" + NL + "            <property>" + NL + "               <name>layout.id</name>" + NL + "               <value>";
  protected final String TEXT_14 = "</value>" + NL + "            </property>" + NL + "            " + NL + "         </properties>" + NL + "         <security-constraint>" + NL + "            <policy-permission>\t";
  protected final String TEXT_15 = NL + "         <window>" + NL + "            <window-name>";
  protected final String TEXT_16 = "</window-name>" + NL + "            <instance-ref>";
  protected final String TEXT_17 = "</instance-ref>" + NL + "            <region>";
  protected final String TEXT_18 = "</region>" + NL + "            <height>";
  protected final String TEXT_19 = "</height>" + NL + "         </window>";
  protected final String TEXT_20 = NL + "               <page>" + NL + "               <page-name>";
  protected final String TEXT_21 = "</page-name>" + NL + "               <properties>" + NL + "\t\t            <property>" + NL + "\t\t               <name>order</name>" + NL + "\t\t               <value>";
  protected final String TEXT_22 = "</value>" + NL + "\t\t            </property>" + NL + "\t\t           \t<property>" + NL + "               \t\t\t<name>layout.id</name>" + NL + "               \t\t\t<value>";
  protected final String TEXT_23 = "</value>" + NL + "\t\t\t\t\t</property>" + NL + "\t\t\t\t\t<property>" + NL + "               \t\t\t<name>theme.id</name>" + NL + "               \t\t\t<value>";
  protected final String TEXT_24 = "</value>" + NL + "            \t\t</property>" + NL + "\t\t       </properties>" + NL + "\t\t       <security-constraint>" + NL + "\t\t            <policy-permission>\t";
  protected final String TEXT_25 = "               " + NL + "                    </policy-permission>" + NL + "              </security-constraint>";
  protected final String TEXT_26 = NL + "\t\t\t\t     <window>" + NL + "\t\t\t\t        <window-name>";
  protected final String TEXT_27 = "</window-name>" + NL + "\t\t\t\t        <instance-ref>";
  protected final String TEXT_28 = "</instance-ref>" + NL + "\t\t\t\t        <region>";
  protected final String TEXT_29 = "</region>" + NL + "\t\t\t\t        <height>";
  protected final String TEXT_30 = "</height>" + NL + "\t\t\t\t     </window>";
  protected final String TEXT_31 = NL + "               </page>";
  protected final String TEXT_32 = NL + "         </page>";
  protected final String TEXT_33 = "      " + NL + "       </portal>" + NL + "    </deployment>" + NL + "</deployments>";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	//SiteMap object will come to this file as an argument
	SiteMap siteMap = (SiteMap)argument;

    stringBuffer.append(TEXT_1);
    stringBuffer.append( siteMap.getName().toString() );
    stringBuffer.append(TEXT_2);
    stringBuffer.append( siteMap.getLayout().toString() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( siteMap.getTheme().toString() );
    stringBuffer.append(TEXT_4);
    
	StringTokenizer returnStrToken = new StringTokenizer(siteMap.getRoles(), "," );
	while(returnStrToken.hasMoreTokens()){

    stringBuffer.append(TEXT_5);
    stringBuffer.append( returnStrToken.nextToken() );
    stringBuffer.append(TEXT_6);
    
	}

    stringBuffer.append(TEXT_7);
    
	for(int numChild=0 ; numChild < siteMap.getChildren().size() ; numChild++){
								
			if (siteMap.getChildren().get(numChild) instanceof MainPage){
				MainPage objMainPage = (MainPage)siteMap.getChildren().get(numChild);
				if (objMainPage.isDummy()==false){

    stringBuffer.append(TEXT_8);
    
		if ( 1 == objMainPage.getPageNo() ) {

    stringBuffer.append(TEXT_9);
    
		} else {

    stringBuffer.append(TEXT_10);
    stringBuffer.append( objMainPage.getName() );
    stringBuffer.append(TEXT_11);
    
		}

    stringBuffer.append(TEXT_12);
    stringBuffer.append( objMainPage.getPageNo() );
    stringBuffer.append(TEXT_13);
    stringBuffer.append( objMainPage.getLayout().toString() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( objMainPage.getTheme().toString() );
    stringBuffer.append(TEXT_14);
    
	StringTokenizer retStrToken = new StringTokenizer(objMainPage.getRoles(), "," );
	while(retStrToken.hasMoreTokens()){

    stringBuffer.append(TEXT_5);
    stringBuffer.append( retStrToken.nextToken() );
    stringBuffer.append(TEXT_6);
    
	}

    stringBuffer.append(TEXT_7);
    								
				for(int num=0 ; num < objMainPage.getChildren().size() ; num++){
					
					//If child of MainPage is PlaceHolder
					if (objMainPage.getChildren().get(num) instanceof PlaceHolder){
						PlaceHolder objPlaceHolder = (PlaceHolder)objMainPage.getChildren().get(num);
						CPFPlugin.getDefault().info(" In ObjectXML.java : In PlaceHolder object : "+objPlaceHolder.getName());
						for(int numCh=0 ; numCh < objPlaceHolder.getChildren().size() ; numCh++){
							Portlet objPortlet = (Portlet)objPlaceHolder.getChildren().get(numCh);

    stringBuffer.append(TEXT_15);
    stringBuffer.append( objPortlet.getName().replace(" ","_").concat("_").concat(Integer.toString(objPlaceHolder.getPlaceHolderNo())).concat("_").concat(Integer.toString(objPortlet.getPortletNo())) );
    stringBuffer.append(TEXT_16);
    stringBuffer.append( objPortlet.getName().replace(" ","_").concat("_").concat(siteMap.getName()).concat("_").concat(Integer.toString(objMainPage.getPageNo())).concat("_").concat(Integer.toString(objPlaceHolder.getPlaceHolderNo())).concat("_").concat(Integer.toString(objPortlet.getPortletNo())).concat("Instance") );
    stringBuffer.append(TEXT_17);
    stringBuffer.append( objPlaceHolder.getName() );
    stringBuffer.append(TEXT_18);
    stringBuffer.append( objPortlet.getPortletNo() );
    stringBuffer.append(TEXT_19);
    
						}//end of closest for
				}//end of if for placeholder		

    																											
					//If child of MainPage is SubPage
					else if (objMainPage.getChildren().get(num) instanceof SubPage){
						SubPage objSubPage = (SubPage)objMainPage.getChildren().get(num);
						if (objSubPage.isDummy()==false){
						CPFPlugin.getDefault().info(" In ObjectXML.java : In non-dummy SubPage object : "+objSubPage.getName());

    stringBuffer.append(TEXT_20);
    stringBuffer.append( objSubPage.getName() );
    stringBuffer.append(TEXT_21);
    stringBuffer.append( objSubPage.getPageNo() );
    stringBuffer.append(TEXT_22);
    stringBuffer.append( objSubPage.getLayout().toString() );
    stringBuffer.append(TEXT_23);
    stringBuffer.append( objSubPage.getTheme().toString() );
    stringBuffer.append(TEXT_24);
    
	StringTokenizer retStr = new StringTokenizer(objSubPage.getRoles(), "," );
	while(retStr.hasMoreTokens()){

    stringBuffer.append(TEXT_5);
    stringBuffer.append( retStr.nextToken() );
    stringBuffer.append(TEXT_6);
    
	}

    stringBuffer.append(TEXT_25);
    
						for(int numCh2=0 ; numCh2 < objSubPage.getChildren().size() ; numCh2++){
							
							//Assuming children of SubPage can only be a PlaceHolder
							PlaceHolder obPlaceHolder = (PlaceHolder)objSubPage.getChildren().get(numCh2);
							for(int numC=0 ; numC < obPlaceHolder.getChildren().size() ; numC++){
								
								Portlet obPortlet = (Portlet)obPlaceHolder.getChildren().get(numC);

    stringBuffer.append(TEXT_26);
    stringBuffer.append( obPortlet.getName().replace(" ","_").concat("_").concat(Integer.toString(obPlaceHolder.getPlaceHolderNo())).concat("_").concat(Integer.toString(obPortlet.getPortletNo())) );
    stringBuffer.append(TEXT_27);
    stringBuffer.append( obPortlet.getName().replace(" ","_").concat("_").concat(siteMap.getName()).concat("_").concat(Integer.toString(objMainPage.getPageNo())).concat("_").concat(Integer.toString(objSubPage.getPageNo())).concat("_").concat(Integer.toString(obPlaceHolder.getPlaceHolderNo())).concat("_").concat(Integer.toString(obPortlet.getPortletNo())).concat("Instance") );
    stringBuffer.append(TEXT_28);
    stringBuffer.append( obPlaceHolder.getName() );
    stringBuffer.append(TEXT_29);
    stringBuffer.append( obPortlet.getPortletNo() );
    stringBuffer.append(TEXT_30);
    
							}//end of closest for
		
						}//end of next for

    stringBuffer.append(TEXT_31);
    						
						}//end of SubPage.isDummy
					}//if instance of sub-page closes
					}//mainPage() children for closes

    stringBuffer.append(TEXT_32);
    
				}//if mainpage.dummy closes
									
			}else if (siteMap.getChildren().get(numChild) instanceof Header){
				CPFPlugin.getDefault().info(" In ObjectXML.java : Page is instanceof Header");
				//Child is Header 
			}else if (siteMap.getChildren().get(numChild) instanceof Footer){
				//Child is Main Page
				CPFPlugin.getDefault().info(" In ObjectXML.java : Page is instanceof Footer");
			}
		}
				

    stringBuffer.append(TEXT_33);
    return stringBuffer.toString();
  }
}
