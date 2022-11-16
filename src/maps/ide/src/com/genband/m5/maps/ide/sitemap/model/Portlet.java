/**********************************************************************
*	 GENBAND, Inc. Confidential and Proprietary
*
* This work contains valuable confidential and proprietary 
* information.
* Disclosure, use or reproduction without the written authorization of
* GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc.
* is protected by the laws of the United States and other countries.
* If publication of the work should occur the following notice shall 
* apply:
* 
* "Copyright 2007 GENBAND, Inc.  All rights reserved."
**********************************************************************
**/


/**********************************************************************
*
*     Project:  MAPS
*
*     Package:  com.genband.m5.maps.ide.sitemap.model
*
*     File:     Portlet.java
*
*     Desc:   	Model class for Portlet.
*
*     Author    Date                Description
*    ---------------------------------------------------------
*     Genband   December 28, 2007   Initial Creation
*
**********************************************************************
**/

package com.genband.m5.maps.ide.sitemap.model;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFPortlet;
import com.genband.m5.maps.ide.model.CPFScreen;
import com.genband.m5.maps.ide.model.util.CPFPortalObjectPersister;


/**
 * Model class for Portlet.
 * @author Genband
 */
public class Portlet extends ModelElement {

/** 
 * A static array of property descriptors.
 * There is one IPropertyDescriptor entry per editable property.
 * @see #getPropertyDescriptors()
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
private static IPropertyDescriptor[] descriptors;
/** ID for the name property value (used for by the corresponding property descriptor).  */
public static final String PORTLET_NAME = "Portlet.Name";
/** ID for the roles property value (used for by the corresponding property descriptor).  */
public static final String PORTLET_ROLES = "Portlet.Roles";
/** ID for the help page property value (used for by the corresponding property descriptor).  */
public static final String PORTLET_HELP_PAGE = "Portlet.HelpPage";
/** ID for the is help enabled property value (used for by the corresponding property descriptor).  */
public static final String PORTLET_HELP_ENABLED = "Portlet.HelpEnabled";

/** A 16x16 pictogram of Portlet */
private static final Image PORTLET_ICON = createImage("portlet16.bmp");


/** Index of this Portlet */
private int portletNo = 1;
private String toolTip ;
private int iconType;
private CPFPortlet cpfPortlet;
private boolean helpEnabled = false;
private String helpScreen = "";
/*
 * Initializes the property descriptors array.
 * @see #getPropertyDescriptors()
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
static {
	descriptors = new IPropertyDescriptor[] { 
			new TextPropertyDescriptor(PORTLET_NAME, "Portlet"), // id and description pair
			new TextPropertyDescriptor(PORTLET_ROLES, "Specify roles"), // id and description pair
			new TextPropertyDescriptor(PORTLET_HELP_ENABLED, "Help"), // id and description pair
			new TextPropertyDescriptor(PORTLET_HELP_PAGE, "Help Page"), // id and description pair
	};
	// use a custom cell editor validator for FILE_PATH_PROP
	for (int i = 0; i < descriptors.length; i++) {
		((PropertyDescriptor) descriptors[i]).setValidator(new ICellEditorValidator() {
			public String isValid(Object value) {
				return null;
			}
		});
	}
} // static

public Portlet(){
	System.out.println("In constructor of portlet");
}

protected static Image createImage(String name) {
	return null;//CPFPlugin.getDefault().getImageRegistry().get(name);
}
private String name = "PortletTitle";

public Image getIcon() {
	return PORTLET_ICON;
}

/**
 * Returns an array of IPropertyDescriptors for this Page.
 * <p>The returned array is used to fill the property view, when the edit-part corresponding
 * to this model element is selected.</p>
 * @see #descriptors
 * @see #getPropertyValue(Object)
 * @see #setPropertyValue(Object, Object)
 */
public IPropertyDescriptor[] getPropertyDescriptors() {
	return descriptors;
}

/**
 * Return the property value for the given propertyId, or null.
 * The property view uses the IDs from the IPropertyDescriptors array 
 * to obtain the value of the corresponding properties.
 * @see #descriptors
 * @see #getPropertyDescriptors()
 */
public Object getPropertyValue(Object propertyId) {
	if (PORTLET_NAME.equals(propertyId)) {
		return name;
	}else if (PORTLET_ROLES.equals(propertyId)) {
		return roles;
	}else if (PORTLET_HELP_ENABLED.equals(propertyId)) {
		if(isHelpEnabled()){
			return "Enabled";
		}else{
			return "Disabled";
		}
	}else if (PORTLET_HELP_PAGE.equals(propertyId)) {
		if(null != getHelpScreen()){
			return getHelpScreen();
		}else{
			return "Help link is not specified.";
		}
	}
	return super.getPropertyValue(propertyId);
}

/**
 * Set the property value for the given property id.
 * If no matching id is found, the call is forwarded to the superclass.
 * The property view uses the IDs from the IPropertyDescriptors array to set the values
 * of the corresponding properties.
 * @see #descriptors
 * @see #getPropertyDescriptors()
 */
public void setPropertyValue(Object propertyId, Object value) {
	if (PORTLET_NAME.equals(propertyId)) {
		name = (String)value;
		firePropertyChange(PORTLET_NAME, null, name);
	} else if (PORTLET_ROLES.equals(propertyId)) {
		roles = (String)value;
		firePropertyChange(PORTLET_ROLES, null, roles);
	} else if (PORTLET_HELP_ENABLED.equals(propertyId)) {
		if(((String)value).toUpperCase().contains("ENABLE")){
			if ( false == helpEnabled ) {
				helpScreen = "Please specify the help page here and copy it in WebContent/help";
			}
			helpEnabled = true;
		}else if(((String)value).toUpperCase().contains("DISABLE")){
			if ( true == helpEnabled) {
				helpScreen = "help is disabled";
			}
			helpEnabled = false ;
		}
		firePropertyChange(PORTLET_HELP_ENABLED, null, helpEnabled);
	} else if (PORTLET_HELP_PAGE.equals(propertyId)) {
		helpScreen = (String)value;
		firePropertyChange(PORTLET_HELP_PAGE, null, helpScreen);
	} else {
		super.setPropertyValue(propertyId, value);
	}
}
public String toString() {
	return "Portlet " + hashCode();
}

public String getName() {
	return name;
}

public void setName(String name) {
	this.name = name;
}

public int getPortletNo() {
	return portletNo;
}

public void setPortletNo(int portletNo) {
	this.portletNo = portletNo;
}

public String getRoles() {
	return roles;
}

public void setRoles(String roles) {
	this.roles = roles;
}

public String getToolTip() {
	return toolTip;
}

public void setToolTip(String toolTip) {
	this.toolTip = toolTip;
}

public int getIconType() {
	return iconType;
}

public void setIconType(int iconType) {
	this.iconType = iconType;
}

public CPFPortlet getCpfPortlet() {
	return cpfPortlet;
}

public void setCpfPortlet(CPFPortlet cpfPortlet) {
	this.cpfPortlet = cpfPortlet;
}

public int getPortletId() {
	return cpfPortlet.getPortletId();
}

public CPFScreen getDetailsScreen() {
	System.out.println("updated porlet having portletId = "+cpfPortlet.getPortletId() 
			+"'s getDetailsScreen called : returning " + cpfPortlet.getDetailsScreen());
	try {
		CPFPortlet updatedcpfPortlet = getUpdatedCPFPortlet(cpfPortlet.getCurrentProject(), cpfPortlet.getPortletId());
		//if()
		cpfPortlet.setDetailsScreen(updatedcpfPortlet.getDetailsScreen(),true);
		System.out.println("detail Screen updated in getDetailsScreen");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return cpfPortlet.getDetailsScreen();
}

public CPFScreen getListScreen() {

	try {
		CPFPortlet updatedcpfPortlet = getUpdatedCPFPortlet(cpfPortlet.getCurrentProject(), cpfPortlet.getPortletId());
		//if()
		cpfPortlet.setListScreen(updatedcpfPortlet.getListScreen(),true);
		System.out.println("list Screen updated in getListScreen");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	return cpfPortlet.getListScreen();
}

public boolean isHelpEnabled() {
	return helpEnabled;
}

public boolean isHelpEnabledInCPFPortlet() {
	if ( null != cpfPortlet && null != cpfPortlet.getListScreen() 
			&& null != cpfPortlet.getListScreen().getPreference() ){
	return cpfPortlet.getListScreen().getPreference().getHelpSupported();
	}
	else{
		return false;
	}
}

public String getHelpScreenFromCPFPortlet() {
	if ( false == isHelpEnabled() ) {
		return "Help is not enabled.";
	}
	if ( null != cpfPortlet && null != cpfPortlet.getListScreen() 
			&& null != cpfPortlet.getListScreen().getPreference() 
			&& null != cpfPortlet.getListScreen().getPreference().getHelpJsp()){
	return cpfPortlet.getListScreen().getPreference().getHelpJsp();
	}
	else{
		return "Not able to get link";
	}
}

public static CPFPortlet getUpdatedCPFPortlet(String projectName , int portletId) {
	
	CPFPlugin.getDefault().log("Portlet  :getUpdatedCPFPortlet for the.........."+projectName);
	
	
	IFolder folder = getProjectHandle(projectName).getFolder(
			new Path(".resources").append("portal"));
	String path = Platform.getLocation().toOSString()
			+ folder.getFullPath().toOSString();

	File portalFolder = new File(path);
	if (portalFolder.exists()) {
		// got the entities folders
		File[] entitiesFolders = portalFolder.listFiles();
		// get the persisted files in these folders
		for (int i = 0; i < entitiesFolders.length; i++) {
			File[] dataPersisFiles = entitiesFolders[i].listFiles();
			for (int j = 0; j < dataPersisFiles.length; j++) {
				if (dataPersisFiles[j].getName().endsWith(".ser")) {
					CPFPortlet portlet = CPFPortalObjectPersister
							.getInstance().readObject(dataPersisFiles[j]);
					if (portlet != null) {
						if ( portletId == portlet.getPortletId() ) {
							return portlet;
						}
					}
				}
			}

		}
	}

	return null;
}

public static IProject getProjectHandle(String projectName) {
	if (projectName != null) {
		IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(
				projectName);
		CPFPlugin.getDefault().info ("Portlet : getting proj handle: " + proj);
		return proj;
	}
	else
		return null;
}

public void setHelpEnabled(boolean helpEnabled) {
	this.helpEnabled = helpEnabled;
}

public String getHelpScreen() {
	return helpScreen;
}

public void setHelpScreen(String helpScreen) {
	this.helpScreen = helpScreen;
}

}
