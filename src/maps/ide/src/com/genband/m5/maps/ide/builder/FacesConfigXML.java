package com.genband.m5.maps.ide.builder;

import java.util.List;
import java.util.ArrayList;
import com.genband.m5.maps.ide.sitemap.model.Footer;
import com.genband.m5.maps.ide.sitemap.model.Header;
import com.genband.m5.maps.ide.sitemap.model.MainPage;
import com.genband.m5.maps.ide.sitemap.model.PlaceHolder;
import com.genband.m5.maps.ide.sitemap.model.Portlet;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.model.SubPage;
import com.genband.m5.maps.ide.sitemap.util.SiteMapUtil;
import com.genband.m5.maps.ide.CPFPlugin;

public class FacesConfigXML
{
  protected static String nl;
  public static synchronized FacesConfigXML create(String lineSeparator)
  {
    nl = lineSeparator;
    FacesConfigXML result = new FacesConfigXML();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\"?>" + NL + "" + NL + "<!--" + NL + "/**********************************************************************" + NL + "*\t GENBAND, Inc. Confidential and Proprietary" + NL + "*" + NL + "* This work contains valuable confidential and proprietary " + NL + "* information." + NL + "* Disclosure, use or reproduction without the written authorization of" + NL + "* GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc." + NL + "* is protected by the laws of the United States and other countries." + NL + "* If publication of the work should occur the following notice shall " + NL + "* apply:" + NL + "* " + NL + "* \"Copyright 2007 GENBAND, Inc.  All rights reserved.\"" + NL + "**********************************************************************/" + NL + "-->" + NL + "" + NL + "<!DOCTYPE faces-config PUBLIC" + NL + "   \"-//Sun Microsystems, Inc.//DTD JavaServer Faces Config 1.0//EN\"" + NL + "   \"http://java.sun.com/dtd/web-facesconfig_1_0.dtd\">" + NL + "<faces-config>" + NL + "" + NL + "   <application>" + NL + "      <property-resolver>org.jboss.portal.core.admin.ui.AdminPropertyResolver</property-resolver>" + NL + "      <view-handler>com.sun.facelets.FaceletPortletViewHandler</view-handler>" + NL + "\t\t<resource-bundle>" + NL + "\t\t\t<base-name>bundle.resources</base-name>" + NL + "\t\t\t<var>bundle</var>" + NL + "\t\t</resource-bundle>" + NL + "" + NL + "    </application>" + NL + "" + NL + "   <converter>" + NL + "      <converter-for-class>org.jboss.portal.core.model.content.ContentType</converter-for-class>" + NL + "      <converter-class>org.jboss.portal.core.admin.ui.conversion.ContentTypeConverter</converter-class>" + NL + "   </converter>" + NL + "" + NL + "   <converter>" + NL + "      <converter-for-class>org.jboss.portal.core.model.portal.PortalObjectId</converter-for-class>" + NL + "      <converter-class>org.jboss.portal.core.admin.ui.conversion.PortalObjectIdConverter</converter-class>" + NL + "   </converter>" + NL + "   " + NL + "   <managed-bean>" + NL + "      <managed-bean-name>CPFManager</managed-bean-name>" + NL + "      <managed-bean-class>com.genband.m5.maps.common.CPFManager</managed-bean-class>" + NL + "      <managed-bean-scope>session</managed-bean-scope>" + NL + "   </managed-bean>" + NL + "\t" + NL + "   <managed-bean>" + NL + "      <managed-bean-name>portletUtil</managed-bean-name>" + NL + "      <managed-bean-class>com.genband.m5.maps.common.PortletUtil</managed-bean-class>" + NL + "      <managed-bean-scope>application</managed-bean-scope>" + NL + "   </managed-bean>" + NL + "   ";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = " " + NL + "   <lifecycle>" + NL + "      <phase-listener>org.jboss.portal.core.admin.ui.Refresher</phase-listener>" + NL + "   </lifecycle>" + NL + "" + NL + "   <component>" + NL + "      <component-type>org.jboss.portal.Scroller</component-type>" + NL + "      <component-class>org.jboss.portal.faces.component.scroller.UIScroller</component-class>" + NL + "   </component>" + NL + "" + NL + "   <component>" + NL + "      <component-type>org.jboss.portal.Portlet</component-type>" + NL + "      <component-class>org.jboss.portal.faces.component.portlet.UIPortlet</component-class>" + NL + "   </component>" + NL + "   " + NL + "   <render-kit>" + NL + "      <renderer>" + NL + "         <component-family>javax.faces.Input</component-family>" + NL + "         <renderer-type>default</renderer-type>" + NL + "         <renderer-class>org.jboss.portal.faces.component.scroller.ScrollerRenderer</renderer-class>" + NL + "      </renderer>" + NL + "   </render-kit>" + NL + "</faces-config>";

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	//SiteMap object will come to this file as an argument
	SiteMap siteMap = (SiteMap)argument;

    stringBuffer.append(TEXT_1);
       
	String fcXMLString="";
	List<Integer> portletIds = new ArrayList();
	FacesConfigIntermediate oFCIntmdt = new FacesConfigIntermediate();
	for(int numChild=0 ; numChild < siteMap.getChildren().size() ; numChild++){
								
			if (siteMap.getChildren().get(numChild) instanceof MainPage){
				MainPage objMainPage = (MainPage)siteMap.getChildren().get(numChild);
				if (objMainPage.isDummy()==false){
				for(int num=0 ; num < objMainPage.getChildren().size() ; num++){
					
					//If child of MainPage is PlaceHolder
					if (objMainPage.getChildren().get(num) instanceof PlaceHolder){
						PlaceHolder objPlaceHolder = (PlaceHolder)objMainPage.getChildren().get(num);
						CPFPlugin.getDefault().info(" In FacesConfigXML.java :  In PlaceHolder object : "+objPlaceHolder.getName());
						for(int numCh=0 ; numCh < objPlaceHolder.getChildren().size() ; numCh++){
							Portlet objPortlet = (Portlet)objPlaceHolder.getChildren().get(numCh);
							if ( false == portletIds.contains(new Integer(objPortlet.getPortletId())) ){
								fcXMLString = oFCIntmdt.generate(objPortlet);
								portletIds.add(new Integer(objPortlet.getPortletId()));
							}
							
							//fcXMLString = oFCIntmdt.generate(objPortlet);

    stringBuffer.append(TEXT_2);
    stringBuffer.append( fcXMLString );
    
					fcXMLString = "";	
							}//end of closest for
					}//end of if for placeholder	
				
					//If child of MainPage is SubPage
					else if (objMainPage.getChildren().get(num) instanceof SubPage){
						SubPage objSubPage = (SubPage)objMainPage.getChildren().get(num);
						if (objSubPage.isDummy()==false){
						CPFPlugin.getDefault().info(" In FacesConfigXML.java :  In non-dummy SubPage object : "+objSubPage.getName());
						for(int numCh2=0 ; numCh2 < objSubPage.getChildren().size() ; numCh2++){
							
							//Assuming children of SubPage can only be a PlaceHolder
							PlaceHolder obPlaceHolder = (PlaceHolder)objSubPage.getChildren().get(numCh2);
							for(int numC=0 ; numC < obPlaceHolder.getChildren().size() ; numC++){
								
								Portlet obPortlet = (Portlet)obPlaceHolder.getChildren().get(numC);
							if ( false == portletIds.contains(new Integer(obPortlet.getPortletId())) ){
								fcXMLString = oFCIntmdt.generate(obPortlet);
								portletIds.add(new Integer(obPortlet.getPortletId()));
							}
							
								//fcXMLString = oFCIntmdt.generate(obPortlet);

    stringBuffer.append(TEXT_2);
    stringBuffer.append( fcXMLString );
    
					fcXMLString = "";	
								}//end of closest for
		
						}//end of next for
						}//end of SubPage.isDummy
					}//if instance of sub-page closes
					}//mainPage() children for closes
					}//if mainpage.dummy closes
									
			}else if (siteMap.getChildren().get(numChild) instanceof Header){
				CPFPlugin.getDefault().info(" In FacesConfigXML.java :  Page is instanceof Header");
				//Child is Header 
			}else if (siteMap.getChildren().get(numChild) instanceof Footer){
				//Child is Main Page
				CPFPlugin.getDefault().info(" In FacesConfigXML.java :  Page is instanceof Footer");
			}
		}

    stringBuffer.append(TEXT_3);
    return stringBuffer.toString();
  }
}
