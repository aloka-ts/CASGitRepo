package com.genband.m5.maps.ide.model.template;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.*;
import java.util.*;
import java.util.List;

public class FacesConfig
{
  protected static String nl;
  public static synchronized FacesConfig create(String lineSeparator)
  {
    nl = lineSeparator;
    FacesConfig result = new FacesConfig();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t<managed-bean>" + NL + "      \t\t<managed-bean-name>createMBean";
  protected final String TEXT_2 = "</managed-bean-name>" + NL + "      \t\t<managed-bean-class>com.genband.m5.maps.ide.mbeans.createMBean_";
  protected final String TEXT_3 = "</managed-bean-class>" + NL + "      \t\t<managed-bean-scope>session</managed-bean-scope> " + NL + "   \t\t</managed-bean>";
  protected final String TEXT_4 = NL + "\t\t<managed-bean>" + NL + "      \t\t<managed-bean-name>listMBean";
  protected final String TEXT_5 = "</managed-bean-name>" + NL + "      \t\t<managed-bean-class>com.genband.m5.maps.ide.mbeans.listMBean_";
  protected final String TEXT_6 = "nested";
  protected final String TEXT_7 = "</managed-bean-name>" + NL + "      \t\t<managed-bean-class>com.genband.m5.maps.ide.mbeans.listMBean";
  protected final String TEXT_8 = "_nested";
  protected final String TEXT_9 = NL + NL + "\t\t<navigation-rule>" + NL + "\t  \t\t<from-view-id>/WEB-INF/jsf/";
  protected final String TEXT_10 = ".xhtml</from-view-id>" + NL + "      \t\t<navigation-case>" + NL + "         \t\t<from-outcome>SUCCESS</from-outcome>" + NL + " \t\t        <to-view-id>/WEB-INF/jsf/";
  protected final String TEXT_11 = ".xhtml</to-view-id>" + NL + "      \t\t</navigation-case>" + NL + "\t  \t\t<navigation-case>" + NL + "         \t\t<from-outcome>PROVERROR</from-outcome>" + NL + "         \t\t<to-view-id>/WEB-INF/jsf/prov-error.xhtml</to-view-id>" + NL + "      \t\t</navigation-case>";
  protected final String TEXT_12 = NL + "\t\t\t<navigation-case>" + NL + "         \t\t<from-outcome>delete</from-outcome>" + NL + "         \t\t<to-view-id>/WEB-INF/jsf/";
  protected final String TEXT_13 = ".xhtml</to-view-id>" + NL + "      \t\t</navigation-case>";
  protected final String TEXT_14 = NL + "\t\t\t<navigation-case>" + NL + "         \t\t<from-outcome>add</from-outcome>" + NL + "        \t\t <to-view-id>/WEB-INF/jsf/";
  protected final String TEXT_15 = " " + NL + "\t\t\t<navigation-case>" + NL + "        \t\t <from-outcome>listMBean";
  protected final String TEXT_16 = "</from-outcome>" + NL + "         \t\t <to-view-id>/WEB-INF/jsf/";
  protected final String TEXT_17 = ".xhtml</to-view-id>" + NL + "     \t\t</navigation-case>";
  protected final String TEXT_18 = NL + "      \t</navigation-rule>";
  protected final String TEXT_19 = NL + "\t\t<navigation-rule>" + NL + "\t \t\t <from-view-id>/WEB-INF/jsf/";
  protected final String TEXT_20 = ".xhtml</from-view-id>" + NL + "\t  \t\t <navigation-case>" + NL + "         \t\t <from-outcome>list</from-outcome>" + NL + "        \t\t <to-view-id>/WEB-INF/jsf/";
  protected final String TEXT_21 = ".xhtml</to-view-id>" + NL + "     \t\t </navigation-case>" + NL + "\t  \t\t<navigation-case>" + NL + "         \t\t<from-outcome>viewDetails</from-outcome>" + NL + "        \t\t <to-view-id>/WEB-INF/jsf/";
  protected final String TEXT_22 = ".xhtml</to-view-id>" + NL + "      \t\t</navigation-case>" + NL + "   \t\t</navigation-rule>";
  protected final String TEXT_23 = NL + "\t\t<navigation-rule>" + NL + "\t  \t\t<from-view-id>/WEB-INF/jsf/";
  protected final String TEXT_24 = ".xhtml</from-view-id>" + NL + "\t \t\t<navigation-case>" + NL + "         \t\t<from-outcome>SUCCESS</from-outcome>" + NL + "         \t\t<to-view-id>/WEB-INF/jsf/";
  protected final String TEXT_25 = ".xhtml</to-view-id>" + NL + "      \t\t</navigation-case>" + NL + "\t  \t\t<navigation-case>" + NL + "         \t\t<from-outcome>resetToMain</from-outcome>" + NL + "         \t\t<to-view-id>/WEB-INF/jsf/";
  protected final String TEXT_26 = ".xhtml</to-view-id>" + NL + "      \t\t</navigation-case>" + NL + "\t \t</navigation-rule>";
  protected final String TEXT_27 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	CPFPlugin LOG = CPFPlugin.getDefault() ;
	CPFPortlet cpfPortlet = (CPFPortlet)argument ;
	LOG.info("Faces Config stuff generation started for portlet id : " + cpfPortlet.getPortletId());
	int portletId = cpfPortlet.getPortletId();
	CPFScreen cpfScreen = null;
	String mainJsfName = null; 	//Holds the base entity list screen JSF Name...
	String detailsJsfName = null;	//Holds  the details screen JSF Name....
	List<String> nestedJsfNames = null;
	//Managaed bean tag generation starts here..................
		//generating Managed Bean tag in case of Details Screen exists.......
	if(cpfPortlet.getDetailsScreen() != null) { 
		LOG.info("managed bean declaration for Details MBean : createMBean_" + portletId);

    stringBuffer.append(TEXT_1);
    stringBuffer.append( portletId );
    stringBuffer.append(TEXT_2);
    stringBuffer.append( portletId );
    stringBuffer.append(TEXT_3);
    
	}
		//generating Managed Bean tag in case of List Screen exists.......
	if(cpfPortlet.getListScreen() != null) { 
		cpfScreen = cpfPortlet.getListScreen();
		LOG.info("managed bean declaration for List MBean : listMBean_" + portletId);

    stringBuffer.append(TEXT_4);
    stringBuffer.append( portletId );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( portletId );
    stringBuffer.append(TEXT_3);
    
			//generating Managed Bean tag in case of Nested List Screen exists (i.e many2many relation exists).......
		if(cpfScreen.getNestedJspNames() != null) {
			nestedJsfNames = new ArrayList<String>();
			Iterator<RelationKey> itrNested = cpfScreen.getNestedJspNames().keySet().iterator();
			int i = 1;
			while(itrNested.hasNext()) {
				nestedJsfNames.add(cpfScreen.getNestedJspNames().get(itrNested.next()).getJspName());
				LOG.info("managed bean declaration for nested List MBean : listMBean" + portletId + "_nested" + i);

    stringBuffer.append(TEXT_4);
    stringBuffer.append( portletId );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( i );
    stringBuffer.append(TEXT_7);
    stringBuffer.append( portletId );
    stringBuffer.append(TEXT_8);
    stringBuffer.append( i++ );
    stringBuffer.append(TEXT_3);
    
			}
		}	//End of geenrating managed Beans tags................
		
		//Navigation rule tags generation starts here............................
		
			//Holding the name of main JSP 
		List<CPFConstants.OperationType> actionsSupported = cpfScreen.getActionsSupported();
		mainJsfName = cpfScreen.getJspName();
		mainJsfName = mainJsfName.substring(mainJsfName.lastIndexOf("/") + 1);
		if(cpfPortlet.getDetailsScreen() != null) {
			detailsJsfName = cpfPortlet.getDetailsScreen().getJspName();
			detailsJsfName = detailsJsfName.substring(detailsJsfName.lastIndexOf("/") + 1); 
		}
		//Generating Navigation Rules for base entity listing screen starts here.....
		LOG.info("Generating Navigation FLow for XHTML : " + mainJsfName);

    stringBuffer.append(TEXT_9);
    stringBuffer.append( mainJsfName );
    stringBuffer.append(TEXT_10);
    stringBuffer.append( mainJsfName );
    stringBuffer.append(TEXT_11);
    
			//In case of Delete is supported then defining navigation after deleting
		if(actionsSupported.contains(CPFConstants.OperationType.DELETE)) {

    stringBuffer.append(TEXT_12);
    stringBuffer.append( mainJsfName );
    stringBuffer.append(TEXT_13);
    
		}
			//Defining Navigation in case of add and/or modify and/or view actions supported
			//Navigation form list JSF to Details JSF
		if(actionsSupported.contains(CPFConstants.OperationType.CREATE) 
			|| actionsSupported.contains(CPFConstants.OperationType.MODIFY)
				|| actionsSupported.contains(CPFConstants.OperationType.VIEW)) {
			if(detailsJsfName != null) {

    stringBuffer.append(TEXT_14);
    stringBuffer.append( detailsJsfName );
    stringBuffer.append(TEXT_13);
    
			}
		}
		if(cpfScreen.getNestedJspNames() != null) {
			Iterator<RelationKey> itrNested = cpfScreen.getNestedJspNames().keySet().iterator();
			int i = 1;
			//while(itrRelationKey.hasNext()) {
			while(itrNested.hasNext()) {
				//RelationKey nested = itrNested.next();
				itrNested.next();
				String nestedJsfName = nestedJsfNames.get(i-1);
				nestedJsfName = nestedJsfName.substring(nestedJsfName.lastIndexOf("/") + 1); 

    stringBuffer.append(TEXT_15);
    stringBuffer.append( portletId );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( i++ );
    stringBuffer.append(TEXT_16);
    stringBuffer.append( nestedJsfName );
    stringBuffer.append(TEXT_17);
    
			}
		}	

    stringBuffer.append(TEXT_18);
    
	}	//End of Generating Navigation Rules for base entity listing screen.....
					//cpfScreen still holding the screen details for Listing only......
			//Start of Generating navigation rules for details screen in case 
				//Create and/or Modify and/or View actions supported..
	if(cpfPortlet.getDetailsScreen() != null) { 
		LOG.info("Generating Navigatino FLow for details linked screen : " + detailsJsfName);

    stringBuffer.append(TEXT_19);
    stringBuffer.append( detailsJsfName );
    stringBuffer.append(TEXT_20);
    stringBuffer.append( mainJsfName );
    stringBuffer.append(TEXT_21);
    stringBuffer.append( detailsJsfName );
    stringBuffer.append(TEXT_22);
    
	}		//End of Generating navigation rules for details screen
		//Start of Generating Navigation Rules in case of nested relations exist between Base and related entities....
	if(cpfScreen.getNestedJspNames() != null) {
		Iterator<RelationKey> itrNested = cpfScreen.getNestedJspNames().keySet().iterator();
		while(itrNested.hasNext()) {
			RelationKey nested = itrNested.next();
			String nestedJsfName = cpfScreen.getNestedJspNames().get(nested).getJspName();
			LOG.info("Generating Navigation Flow for nested screen XHTMl : " + nestedJsfName);

    stringBuffer.append(TEXT_23);
    stringBuffer.append( nestedJsfName );
    stringBuffer.append(TEXT_24);
    stringBuffer.append( nestedJsfName );
    stringBuffer.append(TEXT_25);
    stringBuffer.append( mainJsfName );
    stringBuffer.append(TEXT_26);
    
		}
		LOG.info("Faces Config Generation for portlet Id : " + portletId + "Finished");
	}

    stringBuffer.append(TEXT_27);
    return stringBuffer.toString();
  }
}
