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
 *     Project:  CPFSupport
 *
 *     Package:  com.genband.m5.maps.ide.sitemap.util
 *
 *     File:     PortletUtil.java
 *
 *     Desc:   	Provide helper methods to get info. about portlets(CPFPortlet)
 *               created by the developer.
 *
 *     Author    Date                Description
 *    ---------------------------------------------------------
 *     Genband   December 28, 2007   Initial Creation
 *
 **********************************************************************
 **/

package com.genband.m5.maps.ide.sitemap.util;

import java.util.ArrayList;
import java.util.List;
import java.io.File;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.CPFPortlet;
import com.genband.m5.maps.ide.model.CPFScreen;
import com.genband.m5.maps.ide.model.util.CPFPortalObjectPersister;
import com.sun.corba.se.spi.ior.MakeImmutable;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import java.util.Map;

/**
 * @author Genband
 */
public class PortletUtil {

	private static CPFPlugin LOG = CPFPlugin.getDefault();
	
	private PortletUtil() {
	}

	/*
	 * Please change this method. Right now this is not doing whatever is
	 * expected from it. This method return the portlets' name which are to be
	 * displayed in palette. This method can do some filtering on the basis of
	 * roles passed as arguments and roles for a particular portlet is designed.
	 * Note: This method will be used if different icons are not required to be
	 * displayed corresponding to each portlet(in palette) based on the roles.
	 */

	/**
	 * @param roles
	 *            Roles of SiteMap
	 */
	public static List<String> getPortletsName(List<String> roles) {
		List<String> list;
		list = new ArrayList();
		list.add("NP Portlet");
		list.add("SP Portlet");
		list.add("Portlet3");
		list.add("Portlet4");
		list.add("Portlet5");
		list.add("Portlet6");
		list.add("Portlet7");

		return list;
	}

	/*
	 * Please change its implementation. Right now this is not accomplishing its
	 * desired task. This method return the portlets' name which are to be
	 * displayed in palette which portlets are to be displayed Note: This method
	 * will be used if different icons are required to be displayed
	 * corresponding to each portlet(in palette) based on the roles.Text for
	 * toolTip is also decided here.
	 */
	public static List<PortletInfo> getPortletsInfo(String projectName,
			List<String> roles) {
		
		CPFPlugin.getDefault().log("PortletUtil :getPortletInfo for the.........."+projectName);
		
		System.out.println("portletUtil: roles are :  " + makeString(roles));
		
		List<PortletInfo> list = new ArrayList<PortletInfo>();
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
							CPFScreen screen = portlet.getListScreen();
							if (screen != null) {
								String portletTitle = screen.getPreference()
										.getTitle();
								String jspName = screen.getJspName();
								Map<CPFConstants.OperationType, List<String>> mappedRoles = screen
										.getMappedRoles();
								List<String> listRoles = mappedRoles
										.get(CPFConstants.OperationType.LIST);
								String toolTip = portletTitle
										+ " Listing jsp is " + jspName;
								PortletInfo portletInfo = null ;
								
								if(listRoles!=null){
									if (listRoles.containsAll(roles)) {
										portletInfo = new PortletInfo(portletTitle,
												Constants.NORMAL_ICON, toolTip,
												listRoles,portlet);
										list.add(portletInfo);
	
									} else {
										//boolean warning = false;
										for ( int k = 0; k < roles.size(); k++ ) {
											if ( listRoles.contains(roles.get(k)) ) { //&& (false == warning) ) {
												//warning = true;
												portletInfo = new PortletInfo(
														portletTitle,
														Constants.WARNING_ICON,
														toolTip, listRoles,portlet);
												list.add(portletInfo);
												break;
											}
	
										}
									}
							 }	
							}
						}
					}
				}

			}
		}

		return list;
	}
	public static IProject getProjectHandle(String projectName) {
		if (projectName != null) {
			IProject proj = ResourcesPlugin.getWorkspace().getRoot().getProject(
					projectName);
			LOG.info ("getting proj handle: " + proj);
			return proj;
		}
		else
			return null;
	}

	public static String makeString(List stringArray) {
		String str = "" ;
		if ( null == stringArray ||stringArray.size() == 0 ) {
			return "";
		}
		for( int i = 0 ; i < stringArray.size()-1 ; i++ ){
			str = str.concat((String)stringArray.get(i));
			str = str.concat(",");
		}
		str = str.concat((String)stringArray.get(stringArray.size()-1));
		return str;
	}

}
