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
package com.baypackets.sas.ide.util;

import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.descriptors.BPSipXmlEntityResolver;
//import org.w3c.dom.*;

public class BPProjectINFO 
{
	
	private static Hashtable BPProjectInfoTable = null;
	
	private static BPProjectINFO instance = null;
	
	
	private BPProjectINFO()
	{
		BPProjectInfoTable = new Hashtable();
		
	}
	
	public static synchronized BPProjectINFO getInstance()
	{
		if(instance ==null)
		{
			instance = new BPProjectINFO();
		}
		return instance;
	}
	
	
	public boolean addProjectInfo(String projectName, String applicationName, String priority, String version)
	{
		try
		{
			if(BPProjectInfoTable.get(projectName)!=null)
				BPProjectInfoTable.remove(projectName);
			
			
			ArrayList projectInfo = new ArrayList();
			projectInfo.add(0,applicationName);
			projectInfo.add(1, priority);
			projectInfo.add(2,version);
			
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			
			//String applicationPath = project.getFolder("DeploymentFolder").getLocation().append(applicationName+".sar").toString();
			//String applicationPath = project.getLocation().append(applicationName+".sar").toString();
			String applicationPath = project.getLocation().append(projectName+".sar").toString();
			
			projectInfo.add(3,applicationPath);
			
			BPProjectInfoTable.put(projectName,projectInfo);
			return true;			
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log(e.getMessage(), e);
			return false;
		}
	}
	
	public String getApplicationName(String projectName)
	{
		try
		{
			ArrayList projectInfo = null;
			projectInfo = (ArrayList)BPProjectInfoTable.get(projectName);
			
			return (String)projectInfo.get(0);
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log(e.getMessage(), e);
			return null;
		}
		
		
	}
	
	
	public String getApplicationPriority(String projectName)
	{
		try
		{
			ArrayList projectInfo = null;
			projectInfo = (ArrayList)BPProjectInfoTable.get(projectName);
			
			return (String)projectInfo.get(1);
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log(e.getMessage(), e);
			return null;
		}
		
		
	}
	
	public String getApplicationVersion(String projectName)
	{
		try
		{
			ArrayList projectInfo = null;
			projectInfo = (ArrayList)BPProjectInfoTable.get(projectName);
			
			return (String)projectInfo.get(2);
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log(e.getMessage(), e);
			return null;
		}
		
		
	}
	
	public String getApplicationPath(String projectName)
	{
		try
		{
			ArrayList projectInfo = null;
			projectInfo = (ArrayList)BPProjectInfoTable.get(projectName);
			
			return (String)projectInfo.get(3);
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log(e.getMessage(), e);
			return null;
		}
		
		
	}
	
	public boolean initialize(String projectName) 
	{
		try
		{
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			
			String sasDescriptor = new Path("WEB-INF").append("sas.xml").toString();
	        
			IFile dd = project.getFile(sasDescriptor);
			
			if(!dd.exists()){
				
				String casDescriptor = new Path("WEB-INF").append("cas.xml").toString();
		        
				dd = project.getFile(casDescriptor);
			}
			
			SAXBuilder builder = new SAXBuilder();			
			/*	EntityResolver resolver = new  BPSipXmlEntityResolver();			
			resolver.resolveEntity("-//Baypackets SIP Application Server//DTD SAS Descriptor//EN","http://www.baypackets.com/dtd/sas-app_1_0.dtd");			
			builder.setEntityResolver(resolver);	*/	//Changed by NJADAUN
			Document document = builder.build(dd.getContents(true));
			//Reading the XML file and modifying accordingly.
			
			Element root = document.getRootElement();
			
		
			Element serviceName = root.getChild("name");
			String applicationName = serviceName.getText().trim();
			
			Element serviceVersion = root.getChild("version");
			String applicationVersion = serviceVersion.getTextTrim();

		
			Element servicePri = root.getChild("priority");
			String applicationPriority = servicePri.getTextTrim();
			
			
			SasPlugin.getDefault().log("Application Name === >"+applicationName);
			SasPlugin.getDefault().log("Application Version === >"+applicationVersion);
			SasPlugin.getDefault().log("Application Priority === >"+applicationPriority);
			
			
			this.addProjectInfo(projectName,applicationName,applicationPriority,applicationVersion);
			
			

			return true;
			
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log(e.getMessage(), e);
			return false;
		}
	}
	
	
}
