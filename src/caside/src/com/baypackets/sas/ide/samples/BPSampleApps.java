/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.baypackets.sas.ide.samples;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.BPSASServicesNature;

public class BPSampleApps {
	private static BPSampleApps instance = null;
	
	private static ArrayList listOfClassesClickToDialServlets = null;
	private static ArrayList listOfClassesClickToDialUtil = null;
	private static String clickToDialUtilPackage = null;
	private static String clickToDialServletsPackage = null;
	public static String ClickToDial = "CLICKTODIAL";
	public static String B2bUA = "B2bUA";
	private static String B2bUAServletPackage = null;
	private static String B2bUAServletName = "B2bUAServlet";
	
	public static String ProxyApp = "ProxyApp";
	private static String ProxyAppServletPackage = null;
	private static String ProxyAppName = "ProxyApp";
	
	public static String UASApp = "UASApp";
	private static String UASAppServletPackage = null;
	private static String UASAppName = "UASApp";
	
	
	public static String UACApp = "UACApp";
	private static String UACAppServletPackage = null;
	private static String UACAppName = "UACApp";
	
	private BPSampleApps()
	{
		clickToDialUtilPackage = "com.baypackets.clicktodial.util";
		clickToDialServletsPackage="com.baypackets.clicktodial.servlets";
		ProxyAppServletPackage ="com.baypackets.sampleapps.proxy";
		UASAppServletPackage ="com.baypackets.sampleapps.uas";
		UACAppServletPackage = "com.baypackets.sampleapps.uac";
		B2bUAServletPackage = "com.baypackets.sampleapps.b2bua";
		
		listOfClassesClickToDialServlets = new ArrayList();
		listOfClassesClickToDialUtil = new ArrayList();
		
		listOfClassesClickToDialUtil.add(0,"Call");
		listOfClassesClickToDialUtil.add(1,"CallDAO");
		listOfClassesClickToDialUtil.add(2,"CallStatePersister");
		listOfClassesClickToDialUtil.add(3,"Constants");
		listOfClassesClickToDialUtil.add(4,"FileBasedCallDAO");
		listOfClassesClickToDialUtil.add(5,"TestAttribute");
		
		listOfClassesClickToDialServlets.add(0,"ClickToDialHttpServlet");
		listOfClassesClickToDialServlets.add(1,"ClickToDialSipServlet1");
		listOfClassesClickToDialServlets.add(2,"ClickToDialSipServlet2");
		listOfClassesClickToDialServlets.add(3,"ClickToDialSipServlet3");
		listOfClassesClickToDialServlets.add(4,"InitServlet");
		listOfClassesClickToDialServlets.add(5,"CallStatusServlet");
		
	}
	
	public static BPSampleApps getInstance()
	{
		if(instance==null)
			instance = new BPSampleApps();
		return instance;
		
	}
	
	public ArrayList getClickToDialUtilClassesList()
	{
		return listOfClassesClickToDialUtil;
	}
	
	public ArrayList getClickToDialServletsClassesList()
	{
		return listOfClassesClickToDialServlets;
	}
	
	public String getClickToDialUtilPackage()
	{
		return this.clickToDialUtilPackage;
	}
	public String getClickToDialServletsPackage()
	{
		return this.clickToDialServletsPackage;
		
	}
	
	public String getB2bUAPackage()
	{
		return this.B2bUAServletPackage;
	}
	
	public String getB2bUAServletName()
	{
		return this.B2bUAServletName;
	}
	
	public String getProxyAppPackage()
	{
		return this.ProxyAppServletPackage;
	}
	
	public String getProxyAppName()
	{
		return this.ProxyAppName;
	}
	
	public String getUASAppPackage()
	{
		return this.UASAppServletPackage;
	}
	
	public String getUASAppName()
	{
		return this.UASAppName;
	}
	
	public String getUACAppPackage()
	{
		return this.UACAppServletPackage;
	}
	
	public String getUACAppName()
	{
		return this.UACAppName;
	}
	
	/**
	 * Add SASServices nature to this New project
	 * 
	 * @param project
	 *            to have nature added
	 */
	public void addSASServicesNature(IProject project) {
		try {
			SasPlugin.getDefault().log(
					"Add SASServicesNature for project!!!!!!!!!!!!" +project.getName());
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = BPSASServicesNature.NATURE_ID;
			description.setNatureIds(newNatures);
			for (int j = 0; j < newNatures.length; j++) {
				SasPlugin.getDefault().log(
						"Add Project nature!!!!!!!!!!!!" + newNatures[j]);
			}

			project.setDescription(description, null);
		} catch (CoreException e) {
		}
	}

}
