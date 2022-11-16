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



public class BPSASDeploymentDescriptor 
{
	
	private String projectName = null;
	private IProject project = null;	
	private boolean isSBB = false;
	public IFile dd = null;	
	private boolean modify = false;	
	private String serviceName = null;
	private String serviceVersion = "1.0";
	private String servicePriority = "1";
	
	
	public BPSASDeploymentDescriptor(String projectName)
	{

		project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		String sasDescriptor = new Path("WEB-INF").append("sas.xml").toOSString();
        this.dd = project.getFile(sasDescriptor);
		this.projectName = projectName;
		this.serviceName = projectName;
		
				
	}
	
	public BPSASDeploymentDescriptor(String projectName, String appName, String appVersion, String appPriority )
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
		String sasDescriptor = new Path("WEB-INF").append("sas.xml").toOSString();
        this.dd = project.getFile(sasDescriptor);
			
		return modifyDescriptor();
		
		
	}
	public boolean generateSASDescriptor()
	{
		try
		{
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			String sasDescriptor = new Path("WEB-INF").append("sas.xml").toString();
	        
			
			
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
			Element root = new Element("sas-app");

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
			
			resolver.resolveEntity("-//Baypackets SIP Application Server//DTD SAS Descriptor//EN","http://www.baypackets.com/dtd/sas-app_1_0.dtd");
			
			
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
