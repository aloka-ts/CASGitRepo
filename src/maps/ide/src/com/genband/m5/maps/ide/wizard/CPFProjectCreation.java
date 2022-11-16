package com.genband.m5.maps.ide.wizard;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.sitemap.util.XmlUtils;

import org.w3c.dom.Element;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.eclipse.core.runtime.IStatus;

/**
 * This class creates the Project
 */

public class CPFProjectCreation implements IRunnableWithProgress {

	private CPFProjectWizard wizard = null;
	
	private static final String SITEMAP_PERSISTER = "sitemap.xml";
	private static final String SITEMAP_PERSISTER_RESOURCE = "resources/sitemap.xml";
	private static final String SECURITY_CSV_RESOURCE="resources/security.csv";
	private static final String EAR_FACET_CORE_FILE = "org.eclipse.wst.common.project.facet.core.xml";
	private static final String EAR_COMPONENT_FILE = "org.eclipse.wst.common.component";
	private static final String EAR_FACET_CORE_RESOURCE = "resources/org.eclipse.wst.common.project.facet.core.xml";
	private static final String EAR_COMPONENT_RESOURCE = "resources/org.eclipse.wst.common.component";
	private static final String DD_FIELD_NAME ="'$NAME'".intern();
	private static final String RESOURCE_BUNDLE_DEFAULT_FILE ="resources.properties";
	private static final String RESOURCE_BUNDLE ="resources/resources.properties";
	private static final String RESOURCE_FILE_PREFIX ="resources_";
	private static final String RESOURCE_FILE_SUFIX =".properties";
	private static final String SECURITY_CSV="security.csv"; 
	public CPFProjectCreation(CPFProjectWizard wizard) {
		super();
		this.wizard = wizard;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			monitor.beginTask("Creating the Project .....", 1);
			CPFPlugin.getDefault().log("Create a new Project");

			IRunnableWithProgress runnable = new CPFProjectBuildPath(wizard);
			runnable.run(monitor);

			// Run the Java Project Creation.

			String projectName = this.wizard.getFirstPage().getProjectName();

			IProject project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);

			IFolder resFolder = project.getFolder(".resources");

			if (!resFolder.exists())

				resFolder.create(true, true, monitor);
			
			IFolder sitemapResFolder = project.getFolder(new Path(".resources").append("sitemap"));

	             if (!sitemapResFolder.exists()) {
		               sitemapResFolder.create(true, true, monitor);
	                }
	             
	             IFile sitemapPerFile = project.getFolder(new Path(".resources").append("sitemap")).getFile(SITEMAP_PERSISTER); 
	             if (!sitemapPerFile.exists()) {
	            	 String path = CPFPlugin.fullPath(this.SITEMAP_PERSISTER_RESOURCE);
	         		 InputStream stream = new FileInputStream( path);
		               sitemapPerFile.create(stream, true, monitor);
		               //sitemapPerFile.cr
		               CPFPlugin.getDefault().log("Created empty sitemap persister file!!!!!!!!");
	                } 
	             
	             
	             //Adding EAR related file in .settings folder
	             IFolder settingFolder = project.getFolder(".settings");  
	             
	             if (!settingFolder.exists())
                     settingFolder.create(true, true, monitor);
	             
	             IFile facetFile = settingFolder.getFile(this.EAR_FACET_CORE_FILE); 
	             
	             if(!facetFile.exists()){
	            	 String path = CPFPlugin.fullPath(this.EAR_FACET_CORE_RESOURCE);
	         		 InputStream stream = new FileInputStream( path);
	         		 facetFile.create(stream, true, monitor);
	         		 CPFPlugin.getDefault().log("Created "+this.EAR_FACET_CORE_FILE+" file!!!!!!!!");
		              
	             }
	             
	             createEarComponentFile(settingFolder,projectName);
	             CPFPlugin.getDefault().log("Created "+this.EAR_COMPONENT_FILE+" file!!!!!!!!");
//                 IFile compFile = settingFolder.getFile(this.EAR_COMPONENT_FILE); 
//	             
//	             if(!compFile.exists()){
//	            	 String path = CPFPlugin.fullPath(this.EAR_COMPONENT_RESOURCE);
//	         		 InputStream stream = new FileInputStream( path);
//	         		 compFile.create(stream, true, monitor);
//		              
//		               CPFPlugin.getDefault().log("Created "+this.EAR_COMPONENT_FILE+" file!!!!!!!!");
//	             } 
	             
	             
	           
	           CPFPlugin.getDefault().log("Created sitemap persister file!!!!!!!!");
			IFolder webContFolder = project.getFolder("WebContent");

			if (!webContFolder.exists())

				webContFolder.create(true, true, monitor);

			IFolder webInfFolder = project.getFolder(new Path("WebContent")
					.append("WEB-INF"));
			CPFPlugin.getDefault().log(
					"Creating Web-InfFolder!!!!!!!!!!!!!!!!" + webInfFolder);

			if (!webInfFolder.exists())
				webInfFolder.create(true, true, monitor);

			IFolder sitemapFolder = project.getFolder(new Path("WebContent")
					.append("WEB-INF").append("sitemap"));

			if (!sitemapFolder.exists()) {
				sitemapFolder.create(true, true, monitor);
			}

			IFile buildPropertiesFile = project.getFile("build.properties");

			try {
				ByteArrayOutputStream buildProp = new ByteArrayOutputStream();
				Writer writerbProp = new OutputStreamWriter(buildProp);

				writerbProp.write("");
				writerbProp.close();
				buildProp.close();
				ByteArrayInputStream sourcebp = new ByteArrayInputStream(
						buildProp.toByteArray());
				if (buildPropertiesFile.exists()) {
					buildPropertiesFile.setContents(sourcebp, true, true, null);
				} else
					buildPropertiesFile.create(sourcebp, true, null);
			} catch (Exception e) {
				CPFPlugin.getDefault().log(e.getMessage(), e,-1);
			}

			Properties buildProperties = new Properties();

			try {
				OutputStream outstream = new FileOutputStream(
						buildPropertiesFile.getRawLocation().toOSString());

				buildProperties.setProperty("source", "src");

				buildProperties.setProperty("output", "bin");

				buildProperties.store(outstream,
						"Project Specific Build Properties");

			} catch (Exception e) {
				CPFPlugin.getDefault().log(e.getMessage(), e,-1);
			}
			
			
			//Adding Security data
			IFolder securityFolder = project.getFolder(new Path(".resources").append("security"));

            if (!securityFolder.exists()) {
            	securityFolder.create(true, true, monitor);
               }
            
            IFile securityCSV = securityFolder.getFile(this.SECURITY_CSV); 
            
            if(securityFolder.exists()){
            	if(!securityCSV.exists()){
            		 String path = CPFPlugin.fullPath(this.SECURITY_CSV_RESOURCE);
	         		 InputStream stream = new FileInputStream( path);
            		securityCSV.create(stream, true, monitor);
            	}
            }
            

			CPFPlugin.getDefault().log(
					"Storing Properties of the Project at this project scope");
			this.wizard.getFirstPage().storeProperties();
			
			
			java.util.List codesList=this.wizard.getFirstPage().getLocalesCodes();
			IFolder bundleFolder = project.getFolder("bundle");
			if (!bundleFolder.exists())
				bundleFolder.create(true, true, monitor);
            
            String path = CPFPlugin.fullPath(this.RESOURCE_BUNDLE);
            
			if(codesList!=null){
				for(int i=0;i<codesList.size();i++){
					String fileName=this.RESOURCE_FILE_PREFIX+codesList.get(i)+this.RESOURCE_FILE_SUFIX;
					
					CPFPlugin.getDefault().log("Resoucres Locale file name is..."+fileName);
					IFile  resFile=bundleFolder.getFile(fileName); 
					 InputStream stream = new FileInputStream( path);
			            if(!resFile.exists()){
			            	 resFile.create(stream, true, monitor);
			         		 CPFPlugin.getDefault().log("Created "+fileName+" file!!!!!!!!");
				              
			             }
			             
			             
				}
			}
			
			
			IFile  resFile=bundleFolder.getFile(this.RESOURCE_BUNDLE_DEFAULT_FILE);  
            if(!resFile.exists()){
            	 InputStream stream = new FileInputStream( path);
            	 resFile.create(stream, true, monitor);
         		 CPFPlugin.getDefault().log("Created "+this.RESOURCE_BUNDLE_DEFAULT_FILE+" file!!!!!!!!");
	              
             }
			

		} catch (Exception e) {
			CPFPlugin.getDefault().log(e.getMessage(), e,-1);
		} finally {
			monitor.done();
		}
	}
	
	
	protected void createEarComponentFile(IFolder directory,String projectName)
	{
		try
		{
			String path = CPFPlugin.fullPath(this.EAR_COMPONENT_RESOURCE);
			CPFPlugin.getDefault().log("create File :" + path); 
			
			StringBuffer buffer = this.getContents(path);
			this.processContents(buffer,projectName);
			IFile file = directory.getFile(this.EAR_COMPONENT_FILE);
			if(!file.exists())
			{
				file.create(new ByteArrayInputStream(buffer.toString().getBytes()), true, null);
			}
		}
		catch(Exception e)
		{
			CPFPlugin.getDefault().error(e.getMessage(), e);
		}
	}
	
	protected StringBuffer getContents(String path) throws Exception
	{
		StringBuffer buffer = new StringBuffer();
		InputStream stream = new FileInputStream( path);
		InputStreamReader reader = new InputStreamReader(stream);
		BufferedReader breader = new BufferedReader(reader);
		String line = null;
		while((line = breader.readLine()) != null)
		{
			buffer.append(line);
			buffer.append("\n");
		}
		breader.close();
		return buffer;
	}

	public void processContents(StringBuffer buffer,String projectName)
	{
		
		if(buffer == null)
			return;
		
		buffer = replace(buffer, this.DD_FIELD_NAME, '"'+projectName+'"', 0, true);
		
	}
	
	/**
	 * Replace method using a StringBuffer object
	 */
	public static StringBuffer replace(StringBuffer buffer, 
						String from, String to,
						int fromIndex, boolean all){

		from = (from == null) ? "" : from;
		to = (to == null) ? "" : to;
		if(from.equals(""))
			return buffer;

		//Find the first position of the place holder
		int pos = buffer.indexOf(from, fromIndex);

		while(pos != -1){
			
			//Remove the occurance of the from text
			buffer.delete(pos, pos+from.length());
			
			//Insert the value at the place of the place holder
			buffer.insert(pos, to);

			//If we do not want to replace all, then break the loop here.			
			if(!all){
				break;
			}

			//Find the position of the next place holder
			pos = buffer.indexOf(from, pos+to.length());
		}
		return  buffer;
	}

}
