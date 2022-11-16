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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.EntityResolver;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.samples.BPSampleApps;


public class BPWEBDeploymentDescriptor 
{
	
	private String className = null;
	private String packageName = null;
	private String projectName = null;	
	private boolean isDistributable = true;
	private boolean isLoadonStartup = true;
	private String sessiontimeout = null;
	
	private Hashtable initParams = null;
	private Hashtable contextParams = null;	
	private IFile dd = null;
	private IProject project = null;	
	private boolean isSBB = false;
	private boolean addServlet = false;
	
	public BPWEBDeploymentDescriptor(String projectName)
	{
		this.projectName = projectName;
	}
	
	
	public boolean generateEmptyDescriptor()
	{
		try
		{
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			String sipDescriptor = new Path("WEB-INF").append("web.xml").toOSString();
	        
			this.dd = project.getFile(sipDescriptor);
			createEmptyWEBXML();
			return true;
		}
		catch(Exception e)
		{
			return false;
			
		}
	
		
	}
	
	private void createEmptyWEBXML() throws Exception
	{
		
		Element root = new Element("web-app");
		DocType xhtml = new DocType("web-app","-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN","http://java.sun.com/dtd/web-app_2_3.dtd");
		Element displayName = new Element("display-name");
				
		displayName.setText("Application creation by AGNITY CAS IDE");
		root.addContent("\n");
				
		root.addContent(displayName);
		root.addContent("\n");
		if(this.isDistributable)
		{
			Element dist = new Element("distributable");
			root.addContent(dist);
								
		}
				
		root.addContent("\n");
		root.addContent("\n");
				
		root.addContent("\n");
		Document doc = new Document(root);
		doc.setDocType(xhtml);				
		ByteArrayOutputStream baosSipXML = new ByteArrayOutputStream();
		XMLOutputter serializer = new XMLOutputter();

		serializer.output(doc,baosSipXML );
					
		ByteArrayInputStream sourceproject = new ByteArrayInputStream(baosSipXML.toByteArray());				if(dd.exists())
		{
			dd.appendContents(sourceproject,true,true,null);
		}
		else
			dd.create(sourceproject, true, null);
		baosSipXML.flush();				
		baosSipXML.close();
	}

	public void generateDescriptor(String appName)
	{
		try
		{

			project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			String sipDescriptor = new Path("WEB-INF").append("web.xml").toString();
	        
			this.dd = project.getFile(sipDescriptor);
			BPSampleApps sampleApps =BPSampleApps.getInstance();
			if(appName.equals(sampleApps.ClickToDial))
				createWEBXML();
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log("Exception thrown generateDescriptor() BPWEBDeploymentDescriptor.java..."+e);
		}
		
		
		
	}
	
	public void createWEBXML()
	{
		try
		{
			String filepath = new Path(SasPlugin.fullPath("resources")).append("sampleapps").append("clicktodial").append("web.xml").toString();
			ByteArrayOutputStream baosSipXML = new ByteArrayOutputStream();
			  Writer writerSipXML = new OutputStreamWriter(baosSipXML);

			  FileReader fr = new FileReader(filepath);
			  BufferedReader br = new BufferedReader(fr);
			  StringBuffer buffer = new StringBuffer();
			  String string ="";
			  while((string = br.readLine())!=null)
			  {
				  buffer.append(string+"\n");
			  }
			  SasPlugin.getDefault().log("Reading the contents");

			  SasPlugin.getDefault().log("Closing the input stream");
			  fr.close();
			  br.close();
			  String content = buffer.toString();
			  writerSipXML.write(content);
			  writerSipXML.close();
			  baosSipXML.close();

			  ByteArrayInputStream sourceproject = new ByteArrayInputStream(baosSipXML.toByteArray());

			  if(dd.exists())
			  {
            	dd.setContents(sourceproject,true,true,null);
			  }
			  else
            	dd.create(sourceproject, true, null);
	

		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log("Exception thrown createWEBXML() BPWEBDeploymentDescriptor.java..."+e);
		}
		
	}
	
	public BPWEBDeploymentDescriptor(String projectName, String packageName, String className , Hashtable contextParams , Hashtable initParams, boolean isDis, boolean isLoad, String sessiontimeout)
	{
		this.className = className;
		this.packageName = packageName;
		this.projectName = projectName;
		
		this.isDistributable = isDis;
		this.isLoadonStartup = isLoad;
		this.contextParams = contextParams;
		this.initParams = initParams;
		this.sessiontimeout = sessiontimeout;
		
		if(this.contextParams==null)
			this.contextParams = new Hashtable();
		if(this.initParams==null)
			this.initParams = new Hashtable();
		
	}
	
	public void generateDescriptor()
	{
		try
		{
			project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			String sipDescriptor = new Path("WEB-INF").append("web.xml").toOSString();
	        
			this.dd = project.getFile(sipDescriptor);
			
			if(addServlet)
				modifyDescriptor();
			else
				createDescriptor();

		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log("Exception thrown generateDescriptor() BPWEBDeploymentDescriptor.java..."+e);
		}
		
	}
	
	private void createDescriptor()
	{
		try
		{
			Element root = new Element("web-app");

			DocType xhtml = new DocType("web-app","-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN","http://java.sun.com/dtd/web-app_2_3.dtd");
			
			
			
			//Creating Display name 
			
			Element displayName = new Element("display-name");
			
			displayName.setText("Application creation by AGNITY CAS IDE");
			
			root.addContent("\n");
			
			root.addContent(displayName);
			root.addContent("\n");
			
			//Creating Distributable Tag
			if(this.isDistributable)
			{
				Element dist = new Element("distributable");
				root.addContent(dist);
								
			}
			
			root.addContent("\n");
			root.addContent("\n");
			
			
		
			//Creating Context Parameters
			
			
			if(isSBB)
			{
				Element listener = new Element("listener");
				listener.addContent("\n");
				
				Element listenerclass = new Element("listener-class");
				listenerclass.setText("com.baypackets.ase.sbb.impl.SBBServlet");
				listener.addContent(listenerclass);
				listener.addContent("\n");
				root.addContent(listener);
				root.addContent("\n");
				
				
				Element sbbServlet = new Element("servlet");
				sbbServlet.addContent("\n");
				Element sbbServletName = new Element("servlet-name");
				sbbServletName.setText("SBBServlet");
			
				sbbServlet.addContent(sbbServletName);
				sbbServlet.addContent("\n");
				
				
				Element sbbServletClass = new Element("servlet-class");
				sbbServletClass.setText("com.baypackets.ase.sbb.impl.SBBServlet");
				
				sbbServlet.addContent(sbbServletClass);
				sbbServlet.addContent("\n");
				
				root.addContent(sbbServlet);
				root.addContent("\n");
			

				
			}
			
			
			
			if(contextParams.size()>0)
			{
				Set set = contextParams.keySet();
        		
        		Iterator itr= set.iterator();
        		while(itr.hasNext())
        		{
        			String key = (String)itr.next();
        			Element contextParam = new Element("context-param");
        			
        			Element paramName = new Element("param-name");
        			Element paramValue = new Element("param-value");
        			
        			paramName.setText(key);
        			paramValue.setText((String)contextParams.get(key));
        			contextParam.addContent("\n");
        			contextParam.addContent(paramName);
        			contextParam.addContent("\n");
        			contextParam.addContent(paramValue);
        			contextParam.addContent("\n");
        			root.addContent("\n");
        			root.addContent(contextParam);
        		}
			}
        			
        	
			//Creating Servlet Tags in SIPXML
			//init params
			root.addContent("\n");
			root.addContent("\n");
			
			Element servlet = new Element("servlet");
			Element servletName = new Element("servlet-name");
			Element servletClass = new Element("servlet-class");
			servlet.addContent("\n");
			
			
			servletName.addContent(className);
			
			servlet.addContent("\n");
			servletClass.addContent(packageName+"."+className);
			
			
			servlet.addContent(servletName);
			servlet.addContent("\n");
			servlet.addContent(servletClass);
			servlet.addContent("\n");
			
			
			root.addContent("\n");
			if(initParams.size()>0)
			{
				Set set = initParams.keySet();
        		
        		Iterator itr= set.iterator();
        		while(itr.hasNext())
        		{
        			String key = (String)itr.next();
        			Element initParam = new Element("init-param");
        			
        			Element paramName = new Element("param-name");
        			Element paramValue = new Element("param-value");
        			
        			paramName.setText(key);
        			paramValue.setText((String)initParams.get(key));
        			initParam.addContent("\n");
        			initParam.addContent(paramName);
        			initParam.addContent("\n");
        			initParam.addContent(paramValue);
        			initParam.addContent("\n");
        			
        			servlet.addContent("\n");
        			servlet.addContent(initParam);
        		}
			}
			
			if(this.isLoadonStartup)
			{
				servlet.addContent("\n");
				
				Element loadOnStartup = new Element("load-on-startup");
				servlet.addContent(loadOnStartup);
			}
			
			servlet.addContent("\n");
			servlet.addContent("\n");
			root.addContent(servlet);
			
			root.addContent("\n");
			
			root.addContent("\n");
			//Servlet Mapping and Triggering Rules Start
			
			
			//End
			
			
			//Creating Session config tag
			
			Element sessionconfig = new Element("session-config");
			sessionconfig.addContent("\n");
			Element sessionTimeOut =new Element("session-timeout");
			sessionTimeOut.setText(sessiontimeout);
			
			sessionconfig.addContent(sessionTimeOut);
			
			
			sessionconfig.addContent("\n");
			
			root.addContent("\n");
			root.addContent(sessionconfig);
			
			//Complete Tagging
        
			root.addContent("\n");
			Document doc = new Document(root);

			doc.setDocType(xhtml);
			
			ByteArrayOutputStream baosSipXML = new ByteArrayOutputStream();
			XMLOutputter serializer = new XMLOutputter();

			serializer.output(doc,baosSipXML );
				
			ByteArrayInputStream sourceproject = new ByteArrayInputStream(baosSipXML.toByteArray());				if(dd.exists())
			{
				dd.appendContents(sourceproject,true,true,null);
			}
			else
				dd.create(sourceproject, true, null);

			baosSipXML.flush();				
			baosSipXML.close();
		
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log("Exception thrown createDescriptor() BPWEBDeploymentDescriptor.java..."+e);
		}
	}
	
	public void addNewServlet()
	{
		this.addServlet = true;
	}
	private void modifyDescriptor()
	{
		try
		{
			//Modifying the existing deployment descriptor
			SAXBuilder builder = new SAXBuilder();
			
			
			EntityResolver resolver = new  BPSipXmlEntityResolver();
			
			resolver.resolveEntity("-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN","http://java.sun.com/dtd/web-app_2_3.dtd");
			
			
			builder.setEntityResolver(resolver);
			
			
			
			
			Document doc = builder.build(dd.getContents(true));
			//Reading the XML file and modifying accordingly.
			
			Element root = doc.getRootElement();
			
			//Context Params
			
			List contextPARAMS = root.getChildren("context-param");
			
			
			SasPlugin.getDefault().log("Context Params ==== >"+contextPARAMS);
			
			
			if(contextParams.size()>0)
			{
				Set set = contextParams.keySet();
        		
        		Iterator itr= set.iterator();
        		while(itr.hasNext())
        		{
        			String key = (String)itr.next();
        			Element contextParam = new Element("context-param");
        			
        			Element paramName = new Element("param-name");
        			Element paramValue = new Element("param-value");
        			
        			paramName.setText(key);
        			paramValue.setText((String)contextParams.get(key));
        			contextParam.addContent(paramName);
        			contextParam.addContent(paramValue);
        			contextParam.addContent("\n");
        			
        			contextPARAMS.add(contextParam);
        		}
			}
			
			//root.setContent(contextPARAMS);
			
			List servletlist = root.getChildren("servlet");
        	
			
			Element servlet = new Element("servlet");
			servlet.addContent("\n");
			Element servletName = new Element("servlet-name");
			Element servletClass = new Element("servlet-class");
			servletName.addContent(className);
			servletClass.addContent(packageName+"."+className);
			
			servlet.addContent("\n");
			servlet.addContent(servletName);
			servlet.addContent(servletClass);
			
			servlet.addContent("\n");
			
			if(initParams.size()>0)
			{
				Set set = initParams.keySet();
        		
        		Iterator itr= set.iterator();
        		while(itr.hasNext())
        		{
        			String key = (String)itr.next();
        			Element initParam = new Element("init-param");
        			
        			Element paramName = new Element("param-name");
        			Element paramValue = new Element("param-value");
        			
        			paramName.setText(key);
        			paramValue.setText((String)initParams.get(key));
        			
        			initParam.addContent("\n");
        			initParam.addContent(paramName);
        			initParam.addContent(paramValue);
        			initParam.addContent("\n");
        			servlet.addContent("\n");
        			servlet.addContent(initParam);
        			servlet.addContent("\n");
        		}
			}
			
			if(this.isLoadonStartup)
			{
				Element loadOnStartup = new Element("load-on-startup");
				servlet.addContent(loadOnStartup);
				servlet.addContent("\n");
			}

			servletlist.add(servlet);
			
			
			root.addContent("\n");
			root.addContent("\n");
		
			
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
			
			
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log("Exception thrown modifyDescriptor() BPWEBDeploymentDescriptor.java..."+e);
		}
		
	}
	
	
	public void setSBBProject()
	{
		this.isSBB = true;
	}
}
