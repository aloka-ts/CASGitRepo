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
                                                                  package com.baypackets.sas.ide.descriptors;


import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.xml.sax.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;

import org.jdom.Element;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.input.*;
import com.baypackets.sas.ide.SasPlugin;
import org.jdom.output.XMLOutputter;



public class BPCASDeploymentDescriptor 
{
	
	private String projectName = null;
	private IProject project = null;	
	private boolean isSBB = false;
	public IFile dd = null;	
	private boolean modify = false;	
	private String serviceName = null;
	private String serviceVersion = "1.0";
	private String servicePriority = "1";
	
	
	public BPCASDeploymentDescriptor(String projectName)
	{

		project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		String casDescriptor = new Path("WEB-INF").append("cas.xml").toOSString();
        this.dd = project.getFile(casDescriptor);
		this.projectName = projectName;
		this.serviceName = projectName;
		
				
	}
	
	public BPCASDeploymentDescriptor(String projectName, String appName, String appVersion, String appPriority )
	{
		this.projectName = projectName;
		this.serviceName = appName;
		this.serviceVersion = appVersion;
		this.servicePriority = appPriority;
		
	}
	
	public void setSBBProject()
	{
		this.isSBB = true;
	}
	
	public boolean modifyDescriptor(String servicename, String version ,String priority)
	{
		this.modify = true;
		this.serviceName = servicename;
		this.serviceVersion = version;
		this.servicePriority = priority;
		
		
		project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		String casDescriptor = new Path("WEB-INF").append("cas.xml").toOSString();
        this.dd = project.getFile(casDescriptor);
			
		return modifyDescriptor();
		
		
	}
	public boolean generateCASDescriptor()
	{
		try
		{
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			String casDescriptor = new Path("WEB-INF").append("cas.xml").toString();
	        
			
			
			SasPlugin.getDefault().log("SERVICENAME ===>"+serviceName);
			SasPlugin.getDefault().log("SERVICE VERSION === >"+serviceVersion);
			return createDescriptor();
	

		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log("Exception thrown generateSASDescriptor() BPSASDeploymentDescriptor.java..."+e);
			return false;
		}
		
	}
	
	private boolean createDescriptor()
	{
		try
		{
			Element root = new Element("cas-app");

			//Neeraj DocType xhtml = new DocType("sas-app","-//Baypackets SIP Application Server//DTD SAS Descriptor//EN","http://www.baypackets.com/dtd/sas-app_1_0.dtd");
			
			root.addContent("\n");		
			
			root.addContent("\n");
			

			//Adding name tag
			
			Element servicename = new Element("name");
			
			servicename.setText(serviceName);
			
			root.addContent(servicename);
			root.addContent("\n");
			
			
			Element servicversion = new Element("version");
			
			servicversion.setText(serviceVersion);
			
			root.addContent(servicversion);
			root.addContent("\n");
			
			Element servicepriority = new Element("priority");
			
			servicepriority.setText(servicePriority);
			
			root.addContent(servicepriority);
			
			root.addContent("\n");

			if(isSBB)
			{
				Element sbb = new Element("sbb");
				sbb.addContent("\n");
				root.addContent(sbb);
				root.addContent("\n");
				
				
			}
			
			Document doc = new Document(root);
			//Neeraj doc.setDocType(xhtml);			
			ByteArrayOutputStream baosSipXML = new ByteArrayOutputStream();
			XMLOutputter serializer = new XMLOutputter();
			serializer.output(doc,baosSipXML );
				
			ByteArrayInputStream sourceproject = new ByteArrayInputStream(baosSipXML.toByteArray());				
			if(dd.exists())
			{
				dd.appendContents(sourceproject,true,true,null);
			}
			else
				dd.create(sourceproject, true, null);

			baosSipXML.flush();				
			baosSipXML.close();
			return true;
		
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log("Exception thrown createDescriptor() BPSASDeploymentDescriptor.java..."+e);
			return false;
		}
	}
	
	private boolean modifyDescriptor()
	{
		try
		{
			//Modifying the existing deployment descriptor
			SAXBuilder builder = new SAXBuilder();
			
			
			EntityResolver resolver = new  BPSipXmlEntityResolver();
			
			resolver.resolveEntity("-//Baypackets SIP Application Server//DTD SAS Descriptor//EN","http://www.baypackets.com/dtd/cas-app_1_0.dtd");
			
			
			builder.setEntityResolver(resolver);
			
			
			
			
			Document doc = builder.build(dd.getContents(true));
			//Reading the XML file and modifying accordingly.
			
			Element root = doc.getRootElement();
			
		
			Element servicename = root.getChild("name");
			servicename.setText(serviceName);
			
			Element serviceversion = root.getChild("version");
			serviceversion.setText(serviceVersion);
		
			SasPlugin.getDefault().log("SERVICENAME ===>"+serviceName);
			SasPlugin.getDefault().log("SERVICE VERSION === >"+serviceVersion);
		
			Element servicePri = root.getChild("priority");
			servicePri.setText(servicePriority);
			
			ByteArrayOutputStream baosSipXML = new ByteArrayOutputStream();
			XMLOutputter serializer = new XMLOutputter();

			serializer.output(doc,baosSipXML );
				
			ByteArrayInputStream sourceproject = new ByteArrayInputStream(baosSipXML.toByteArray());
			if(dd.exists())
			{
				dd.delete(true,null);
					//dd.appendContents(sourceproject,true,true,null);
				dd.create(sourceproject, true, null);
			}
			else
				dd.create(sourceproject, true, null);

				baosSipXML.flush();				
				baosSipXML.close();
			
			return true;
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log("Exception thrown modifyDescriptor() BPSASDeploymentDescriptor.java..."+e);
			return false;
		}
		
	}

}
