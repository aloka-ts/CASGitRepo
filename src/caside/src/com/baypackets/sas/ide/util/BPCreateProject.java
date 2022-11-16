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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IFile;
import org.jdom.Element;
import org.jdom.Document;
import org.jdom.output.XMLOutputter;
import org.eclipse.core.runtime.*;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import com.baypackets.bayprocessor.agent.BpJavaPing;
import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.wizards.BPJavaConfigPage;

/**
 * This class creates HTTP Project, SIP Project, SBB Project and Converged
 * application project depending on the entries given by the IDE user
 * 
 * @author eclipse
 * 
 */

public class BPCreateProject {

	private int currentProjectType = 1; // Default project is the SIP Project
	private String projectName = null;

	public BPCreateProject() {
		SasPlugin.getDefault().log("BPCreateProject Constructor");
	}

	/**
	 * This method initializes the Project Name and Project Type Project Type =
	 * 1 ------> SIP Project Project Type = 2 ------> HTTP Project Project Type
	 * = 3 ------> SBB Project Project Type = 4 ------> Converged Project
	 * 
	 * @param projectName
	 *            The name of the project thats has to be created
	 * @param projectType
	 *            The type of the Project
	 */
	public void initializeProjectParameters(String projectName, int projectType) {
		this.projectName = projectName;
		this.currentProjectType = projectType;

	}

	/**
	 * This method calls the appropriate method depending on the value of the
	 * variable projectType
	 * 
	 * @return It returns true if it creates the project successfully else it
	 *         returns false.
	 */

	public boolean createProject() {
		try {
			if (currentProjectType == BPProjectType.SIPProject) {
				return createSIPProject();
			}

			if (currentProjectType == BPProjectType.SBBProject)
				return createSBBProject();

			if (currentProjectType == BPProjectType.ConvergedProject)
				return createConvergedProject();

			if (currentProjectType == BPProjectType.HTTPProject)
				return createHTTPProject();
		} catch (Exception e) {
			return false;
		}

		return false;

	}

	/**
	 * This method creates the HTTP Project It returns true if it creates HTTP
	 * Project successfully else it returns false
	 * 
	 * @return
	 */
	private boolean createHTTPProject() {
		try {
			StringWriter writer = new StringWriter();
			IProject project = getNewProject();
			writer.write("");
			IFolder sourcefolder = null;
			IFolder classfilefolder = null;
			IFolder WEBfolder = null;
			IFolder libFolder = null;

			String WEBfolderName = new Path("WEB-INF").toString();
			String classfilefolderName = new Path("WEB-INF").append("classes")
					.toString();
			String libFolderName = new Path("WEB-INF").append("lib").toString();

			// Creating Java folder
			// This folder contains all the source file as developed by the user

			createBUILDProperties(project);
			sourcefolder = project.getFolder("java");

			if (sourcefolder.exists()) {
				SasPlugin.getDefault().log(
						sourcefolder + "exists in the workspace");

			} else {
				SasPlugin.getDefault().log("Creating the " + sourcefolder);

				sourcefolder.create(true, true, null);

			}

			WEBfolder = project.getFolder(WEBfolderName);
			if (WEBfolder.exists()) {
				SasPlugin.getDefault().log("Folder " + WEBfolder + "exists");
			} else {
				SasPlugin.getDefault().log("Creating the " + WEBfolder);
				WEBfolder.create(true, true, null);
			}

			classfilefolder = project.getFolder(classfilefolderName);
			if (classfilefolder.exists()) {
				SasPlugin.getDefault().log(
						"Folder " + classfilefolder + "exists");
			} else {
				SasPlugin.getDefault().log("Creating the " + classfilefolder);
				classfilefolder.create(true, true, null);
			}

			libFolder = project.getFolder(libFolderName);
			if (libFolder.exists()) {
				SasPlugin.getDefault()
						.log("Folder " + libFolderName + "exists");
			} else {
				SasPlugin.getDefault().log("Creating the " + libFolderName);
				libFolder.create(true, true, null);
			}
			IFile classfile = project.getFile(".classpath");

			SasPlugin.getDefault().log("CLASSFILE====>" + classfile);
			writeToClassPath(classfile);

			writer.write("");
			IFile projectfile = project.getFile(".project");

			SasPlugin.getDefault().log("Projectfile====>" + projectfile);
			ByteArrayOutputStream baosproject = null;
			baosproject = new ByteArrayOutputStream();
			Writer projectWriter = new OutputStreamWriter(baosproject);
			writeToProject(projectWriter);
			projectWriter.close();
			baosproject.close();
			ByteArrayInputStream sourceproject = new ByteArrayInputStream(
					baosproject.toByteArray());

			if (projectfile.exists())
				projectfile.setContents(sourceproject, true, true, null);
			else
				projectfile.create(sourceproject, true, null);

			writer.write("");

			String webDescriptor = new Path("WEB-INF").append("web.xml")
					.toString();
			IFile webXML = project.getFile(webDescriptor);
			writer.write("");
			ByteArrayOutputStream baoswebXML = new ByteArrayOutputStream();
			Writer writerWebXML = new OutputStreamWriter(baoswebXML);

			writerWebXML.write("");
			writerWebXML.close();
			baoswebXML.close();
			ByteArrayInputStream sourceprojectweb = new ByteArrayInputStream(
					baoswebXML.toByteArray());

			if (webXML.exists()) {
				webXML.setContents(sourceprojectweb, true, true, null);
			} else
				webXML.create(sourceprojectweb, true, null);

			String sasDescriptor = new Path("WEB-INF").append("cas.xml")
					.toString();
			IFile sasXML = project.getFile(sasDescriptor);
			writer.write("");
			ByteArrayOutputStream baosSasXML = new ByteArrayOutputStream();
			Writer writerSasXML = new OutputStreamWriter(baosSasXML);
			writerSasXML.write("");
			writerSasXML.close();
			baosSasXML.close();

			ByteArrayInputStream sourceprojectssas = new ByteArrayInputStream(
					baosSasXML.toByteArray());
			if (sasXML.exists()) {
				sasXML.setContents(sourceprojectssas, true, true, null);
			} else
				sasXML.create(sourceprojectssas, true, null);

			return true;

		}

		catch (Exception ee) {
			SasPlugin.getDefault().log(ee.getMessage(), ee);
			return false;

		}

	}

	/**
	 * This method creates the converged project
	 * 
	 * @return It returns true if it creates Converged Project successfully else
	 *         it returns false
	 */

	private boolean createConvergedProject() {

		try {
			StringWriter writer = new StringWriter();
			IProject project = getNewProject();

			writer.write("");
			IFolder sourcefolder = null;
			IFolder classfilefolder = null;
			IFolder WEBfolder = null;
			IFolder libFolder = null;

			String WEBfolderName = new Path("WEB-INF").toString();
			String classfilefolderName = new Path("WEB-INF").append("classes")
					.toString();
			String libFolderName = new Path("WEB-INF").append("lib").toString();
			createBUILDProperties(project);
			sourcefolder = project.getFolder("java");

			if (sourcefolder.exists()) {
				SasPlugin.getDefault().log(
						sourcefolder + "exists in the workspace");

			} else {
				SasPlugin.getDefault().log("Creating the " + sourcefolder);

				sourcefolder.create(true, true, null);

			}

			WEBfolder = project.getFolder(WEBfolderName);

			if (WEBfolder.exists()) {
				SasPlugin.getDefault().log("Folder " + WEBfolder + "exists");
			} else {
				SasPlugin.getDefault().log("Creating the " + WEBfolder);
				WEBfolder.create(true, true, null);
			}

			classfilefolder = project.getFolder(classfilefolderName);

			if (classfilefolder.exists()) {
				SasPlugin.getDefault().log(
						"Folder " + classfilefolder + "exists");
			} else {
				SasPlugin.getDefault().log("Creating the " + classfilefolder);
				classfilefolder.create(true, true, null);
			}
			libFolder = project.getFolder(libFolderName);

			if (libFolder.exists()) {
				SasPlugin.getDefault()
						.log("Folder " + libFolderName + "exists");
			} else {
				SasPlugin.getDefault().log("Creating the " + libFolderName);
				libFolder.create(true, true, null);
			}
			IFile classfile = project.getFile(".classpath");

			SasPlugin.getDefault().log("CLASSFILE====>" + classfile);
			writeToClassPath(classfile);

			writer.write("");
			IFile projectfile = project.getFile(".project");
			SasPlugin.getDefault().log("Projectfile====>" + projectfile);
			ByteArrayOutputStream baosproject = null;
			baosproject = new ByteArrayOutputStream();
			Writer projectWriter = new OutputStreamWriter(baosproject);
			writeToProject(projectWriter);
			projectWriter.close();
			baosproject.close();
			ByteArrayInputStream sourceproject = new ByteArrayInputStream(
					baosproject.toByteArray());

			if (projectfile.exists())
				projectfile.setContents(sourceproject, true, true, null);
			else
				projectfile.create(sourceproject, true, null);

			writer.write("");

			String sipDescriptor = new Path("WEB-INF").append("sip.xml")
					.toString();

			IFile sipXML = project.getFile(sipDescriptor);

			writer.write("");
			ByteArrayOutputStream baosSipXML = new ByteArrayOutputStream();
			Writer writerSipXML = new OutputStreamWriter(baosSipXML);

			writerSipXML.write("");
			writerSipXML.close();
			baosSipXML.close();

			ByteArrayInputStream sourceprojects = new ByteArrayInputStream(
					baosSipXML.toByteArray());

			if (sipXML.exists()) {
				sipXML.setContents(sourceprojects, true, true, null);
			} else
				sipXML.create(sourceprojects, true, null);

			String webDescriptor = new Path("WEB-INF").append("web.xml")
					.toString();

			IFile webXML = project.getFile(webDescriptor);

			writer.write("");
			ByteArrayOutputStream baoswebXML = new ByteArrayOutputStream();
			Writer writerWebXML = new OutputStreamWriter(baoswebXML);

			writerWebXML.write("");
			writerWebXML.close();
			baoswebXML.close();

			ByteArrayInputStream sourceprojectweb = new ByteArrayInputStream(
					baosSipXML.toByteArray());

			if (webXML.exists()) {
				webXML.setContents(sourceprojectweb, true, true, null);
			} else
				webXML.create(sourceprojects, true, null);

			String sasDescriptor = new Path("WEB-INF").append("cas.xml")
					.toString();
			IFile sasXML = project.getFile(sasDescriptor);
			writer.write("");
			ByteArrayOutputStream baosSasXML = new ByteArrayOutputStream();
			Writer writerSasXML = new OutputStreamWriter(baosSipXML);
			writerSasXML.write("");
			writerSasXML.close();
			baosSasXML.close();

			ByteArrayInputStream sourceprojectssas = new ByteArrayInputStream(
					baosSipXML.toByteArray());

			if (sasXML.exists()) {
				sasXML.setContents(sourceprojectssas, true, true, null);
			} else
				sasXML.create(sourceprojectssas, true, null);

			return true;

		}

		catch (Exception ee) {
			SasPlugin.getDefault().log(ee.getMessage(), ee);
			return false;

		}

	}

	/**
	 * This method creates the SIP Project
	 * 
	 * @return It returns true if it creates SIP Project successfully else it
	 *         returns false
	 */

	private boolean createSIPProject() {
		try {
			StringWriter writer = new StringWriter();
			IProject project = getNewProject();

			writer.write("");
			IFolder sourcefolder = null;

			IFolder classfilefolder = null;

			IFolder WEBfolder = null;

			IFolder libFolder = null;

			String WEBfolderName = new Path("WEB-INF").toString();

			String classfilefolderName = new Path("WEB-INF").append("classes")
					.toString();

			String libFolderName = new Path("WEB-INF").append("lib").toString();
			createBUILDProperties(project);
			sourcefolder = project.getFolder("java");

			if (sourcefolder.exists()) {
				SasPlugin.getDefault().log(
						sourcefolder + "exists in the workspace");

			} else {
				SasPlugin.getDefault().log("Creating the " + sourcefolder);

				sourcefolder.create(true, true, null);

			}

			WEBfolder = project.getFolder(WEBfolderName);

			if (WEBfolder.exists()) {
				SasPlugin.getDefault().log("Folder " + WEBfolder + "exists");
			} else {
				SasPlugin.getDefault().log("Creating the " + WEBfolder);
				WEBfolder.create(true, true, null);
			}

			classfilefolder = project.getFolder(classfilefolderName);

			if (classfilefolder.exists()) {
				SasPlugin.getDefault().log(
						"Folder " + classfilefolder + "exists");
			} else {
				SasPlugin.getDefault().log("Creating the " + classfilefolder);
				classfilefolder.create(true, true, null);
			}
			libFolder = project.getFolder(libFolderName);

			if (libFolder.exists()) {
				SasPlugin.getDefault()
						.log("Folder " + libFolderName + "exists");
			} else {
				SasPlugin.getDefault().log("Creating the " + libFolderName);
				libFolder.create(true, true, null);
			}
			IFile classfile = project.getFile(".classpath");

			SasPlugin.getDefault().log("CLASSFILE====>" + classfile);
			writeToClassPath(classfile);
			writer.write("");
			IFile projectfile = project.getFile(".project");
			SasPlugin.getDefault().log("Projectfile====>" + projectfile);
			ByteArrayOutputStream baosproject = null;
			baosproject = new ByteArrayOutputStream();
			Writer projectWriter = new OutputStreamWriter(baosproject);
			writeToProject(projectWriter);
			projectWriter.close();
			baosproject.close();
			ByteArrayInputStream sourceproject = new ByteArrayInputStream(
					baosproject.toByteArray());

			if (projectfile.exists())
				projectfile.setContents(sourceproject, true, true, null);
			else
				projectfile.create(sourceproject, true, null);

			writer.write("");

			String sipDescriptor = new Path("WEB-INF").append("sip.xml")
					.toString();

			IFile sipXML = project.getFile(sipDescriptor);

			writer.write("");
			ByteArrayOutputStream baosSipXML = new ByteArrayOutputStream();
			Writer writerSipXML = new OutputStreamWriter(baosSipXML);

			writerSipXML.write("");
			writerSipXML.close();
			baosSipXML.close();

			ByteArrayInputStream sourceprojects = new ByteArrayInputStream(
					baosSipXML.toByteArray());

			if (sipXML.exists()) {
				sipXML.setContents(sourceprojects, true, true, null);
			} else
				sipXML.create(sourceprojects, true, null);

			return true;

		}

		catch (Exception ee) {
			SasPlugin.getDefault().log(ee.getMessage(), ee);
			return false;

		}

	}

	/**
	 * It creates SBB Project
	 * 
	 * @return It returns true if it creates project successfully else it
	 *         returns false
	 */

	private boolean createSBBProject() {
		try {
			StringWriter writer = new StringWriter();
			IProject project = getNewProject();
			writer.write("");
			IFolder sourcefolder = null;
			IFolder classfilefolder = null;
			IFolder WEBfolder = null;
			IFolder libFolder = null;

			String WEBfolderName = new Path("WEB-INF").toString();
			String classfilefolderName = new Path("WEB-INF").append("classes")
					.toString();
			String libFolderName = new Path("WEB-INF").append("lib").toString();
			createBUILDProperties(project);
			sourcefolder = project.getFolder("java");

			if (sourcefolder.exists()) {
				SasPlugin.getDefault().log(
						sourcefolder + "exists in the workspace");

			} else {
				SasPlugin.getDefault().log("Creating the " + sourcefolder);

				sourcefolder.create(true, true, null);

			}

			WEBfolder = project.getFolder(WEBfolderName);

			if (WEBfolder.exists()) {
				SasPlugin.getDefault().log("Folder " + WEBfolder + "exists");
			} else {
				SasPlugin.getDefault().log("Creating the " + WEBfolder);
				WEBfolder.create(true, true, null);
			}

			classfilefolder = project.getFolder(classfilefolderName);

			if (classfilefolder.exists()) {
				SasPlugin.getDefault().log(
						"Folder " + classfilefolder + "exists");
			} else {
				SasPlugin.getDefault().log("Creating the " + classfilefolder);
				classfilefolder.create(true, true, null);
			}
			libFolder = project.getFolder(libFolderName);

			if (libFolder.exists()) {
				SasPlugin.getDefault()
						.log("Folder " + libFolderName + "exists");
			} else {
				SasPlugin.getDefault().log("Creating the " + libFolderName);
				libFolder.create(true, true, null);
			}
			IFile classfile = project.getFile(".classpath");

			SasPlugin.getDefault().log("CLASSFILE====>" + classfile);
			writeToClassPath(classfile);

			IFile projectfile = project.getFile(".project");

			SasPlugin.getDefault().log("Projectfile====>" + projectfile);
			ByteArrayOutputStream baosproject = null;
			baosproject = new ByteArrayOutputStream();
			Writer projectWriter = new OutputStreamWriter(baosproject);
			writeToProject(projectWriter);
			projectWriter.close();
			baosproject.close();
			ByteArrayInputStream sourceproject = new ByteArrayInputStream(
					baosproject.toByteArray());

			if (projectfile.exists())
				projectfile.setContents(sourceproject, true, true, null);
			else
				projectfile.create(sourceproject, true, null);

			writer.write("");

			String sipDescriptor = new Path("WEB-INF").append("sip.xml")
					.toString();
			IFile sipXML = project.getFile(sipDescriptor);
			writer.write("");
			ByteArrayOutputStream baosSipXML = new ByteArrayOutputStream();
			Writer writerSipXML = new OutputStreamWriter(baosSipXML);
			writerSipXML.write("");
			writerSipXML.close();
			baosSipXML.close();

			ByteArrayInputStream sourceprojects = new ByteArrayInputStream(
					baosSipXML.toByteArray());

			if (sipXML.exists()) {
				sipXML.setContents(sourceprojects, true, true, null);
			} else
				sipXML.create(sourceprojects, true, null);

			String sasDescriptor = new Path("WEB-INF").append("cas.xml")
					.toString();
			IFile sasXML = project.getFile(sasDescriptor);

			writer.write("");
			ByteArrayOutputStream baosSasXML = new ByteArrayOutputStream();
			Writer writerSasXML = new OutputStreamWriter(baosSipXML);
			writerSasXML.write("");
			writerSasXML.close();
			baosSasXML.close();

			ByteArrayInputStream sourceprojectssas = new ByteArrayInputStream(
					baosSipXML.toByteArray());

			if (sasXML.exists()) {
				sasXML.setContents(sourceprojectssas, true, true, null);
			} else
				sasXML.create(sourceprojectssas, true, null);

			return true;

		}

		catch (Exception ee) {
			SasPlugin.getDefault().log(ee.getMessage(), ee);
			return false;

		}

	}

	private void writeToClassPath(IFile dd)
	{
		String sipservletjar = null;
		String servletjar = null;		   
		String sbbjar = null;
		String log4jjar = null;
		String bpresource = null;
	 
//		try
//		{
//			String libpath = SasPlugin.fullPath("library");			  
//			sipservletjar = new Path(libpath).append("sipservlet.jar").toString();			   
//			servletjar = new Path(libpath).append("servlet-2.4.jar").toString();			   
//			sbbjar = new Path(libpath).append("bpsbb.jar").toString();
//			log4jjar = new Path(libpath).append("log4j-1.2.8.jar").toString();
//			bpresource = new Path(libpath).append("bpresource.jar").toString();
//			
//		}
//		catch(Exception ee)
//		{
//			SasPlugin.getDefault().log(ee.getMessage(), ee);
//		}
		   
		try
		{
			Element root = new Element("classpath");	
			Element classPathEntrySource = new Element("classpathentry");					
			classPathEntrySource.setAttribute("kind","src");
			classPathEntrySource.setAttribute("path","java");
						
			root.addContent(classPathEntrySource);
			root.addContent("\n");
							
			Element classPathEntryCon = new Element("classpathentry");
			classPathEntryCon.setAttribute("kind","con");
			classPathEntryCon.setAttribute("path","org.eclipse.jdt.launching.JRE_CONTAINER");
						
			root.addContent(classPathEntryCon);
			root.addContent("\n");
		
			Element classPathEntryOutput = new Element("classpathentry");
			classPathEntryOutput.setAttribute("kind","output");
			classPathEntryOutput.setAttribute("path","bin");
							
			root.addContent(classPathEntryOutput);
			root.addContent("\n");
		
			
			IPath classpathProp = new Path(
					SasPlugin.fullPath("project_classpath.properties"));

			File clasPropFile = classpathProp.toFile();
			SasPlugin.getDefault().log(
					"Reading the project_classpath.properties for classpath entry :" + clasPropFile);
			Properties pathp = new Properties();
			String[] DEFAULT_JAR_FILES=BPJavaConfigPage.DEFAULT_JAR_FILES;
			String JSR116_JAR_FILES=BPJavaConfigPage.JSR116_JARS;
			try {
				pathp.load(new FileReader(clasPropFile));
				String classp = pathp.getProperty(BPJavaConfigPage.DEFAULT_CLASSPATH);
				
				SasPlugin.getDefault().log("The property "+BPJavaConfigPage.DEFAULT_CLASSPATH+" is" + classp);
				if (classp != null) {
					DEFAULT_JAR_FILES = classp.split(",");
				}

			} catch (FileNotFoundException e) {
				SasPlugin.getDefault().log(
						"File not found project_classpath.properties :", e);
				e.printStackTrace();
			} catch (IOException e) {
				SasPlugin.getDefault().log(
						"IOException project_classpath.properties :", e);
				e.printStackTrace();
			}
			
			IPath libpath = new Path(SasPlugin.fullPath("library"));
	        String javadoc_loc_prefix ="jar:file:/"+libpath.append("docs/").toPortableString();
			
			String javadoc_loc_suffix=BPJavaConfigPage.JAVA_DOC_FILE_EXT+"!/";
					
			SasPlugin.getDefault().log("JAVA_DOC_FILE_EXT :" + javadoc_loc_suffix);
			SasPlugin.getDefault().log("Plugin library home :" + libpath);
		
               if (libpath != null) {
					
					for (int i = 0; i < DEFAULT_JAR_FILES.length; i++) {
						
						SasPlugin.getDefault().log("Adding to classpath:" + DEFAULT_JAR_FILES[i]);
						 
						String doclocation=javadoc_loc_prefix+DEFAULT_JAR_FILES[i].substring(0, DEFAULT_JAR_FILES[i].indexOf(".jar"))+javadoc_loc_suffix;
						
						SasPlugin.getDefault().log("JAVADOC location is " + doclocation);
						Element classPathEntrylibsip = new Element("classpathentry");
						classPathEntrylibsip.setAttribute("kind","lib");
						classPathEntrylibsip.setAttribute("path",libpath.append(DEFAULT_JAR_FILES[i]).toOSString());
						classPathEntrylibsip.setAttribute("javadoc_location",doclocation);
						root.addContent(classPathEntrylibsip);
						root.addContent("\n");
						

				}
			}
			
			Element classPathEntrylibsip = new Element("classpathentry");
			classPathEntrylibsip.setAttribute("kind","lib");
			classPathEntrylibsip.setAttribute("path",libpath.append(JSR116_JAR_FILES).toOSString());
			String doclocation=javadoc_loc_prefix+JSR116_JAR_FILES.substring(0, JSR116_JAR_FILES.indexOf(".jar"))+javadoc_loc_suffix;
			classPathEntrylibsip.setAttribute("javadoc_location",doclocation);
			root.addContent(classPathEntrylibsip);
			root.addContent("\n");
			
		
//									
//			Element classPathEntryservlet = new Element("classpathentry");
//			classPathEntryservlet.setAttribute("kind","lib");
//			classPathEntryservlet.setAttribute("path",servletjar);
//			root.addContent(classPathEntryservlet);
//			root.addContent("\n");
//					
//			Element classPathEntrysbb = new Element("classpathentry");
//			classPathEntrysbb.setAttribute("kind","lib");
//			classPathEntrysbb.setAttribute("path",sbbjar);
//			root.addContent(classPathEntrysbb);
//			root.addContent("\n");
//
//			Element classPathEntrylog = new Element("classpathentry");
//			classPathEntrylog.setAttribute("kind","lib");
//			classPathEntrylog.setAttribute("path",log4jjar);
//			root.addContent(classPathEntrylog);
//			root.addContent("\n");
//
//
//			Element classPathEntryresource = new Element("classpathentry");
//			classPathEntryresource.setAttribute("kind","lib");
//			classPathEntryresource.setAttribute("path",bpresource);
//			root.addContent(classPathEntryresource);
//			root.addContent("\n");

			Document doc = new Document(root);
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
				
			   
		   }
		   catch(Exception e)
		   {
			SasPlugin.getDefault().log(e.getMessage(), e);
		   }
	}

	private IProject getNewProject() {
		try {
			IProject handle = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);

			if (!handle.exists())
				try {
					handle.create(null);
				} catch (CoreException e) {
					SasPlugin.getDefault().log("Exception ");

					return null;
				}
			if (!handle.isOpen())
				try {
					handle.open(null);
				} catch (CoreException e) {
					SasPlugin.getDefault().log(
							"Error while opening project " + projectName + ": "
									+ e.getMessage());
					return null;
				}
			return handle;

		} catch (Exception ee) {
			SasPlugin.getDefault().log(ee.getMessage(), ee);
			return null;
		}

	}

	private void writeToProject(Writer writer) {

		try {

			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<projectDescription>\r\n\t<name>");
			String buf3 = projectName;
			writer.write(buf3);
			writer.write("</name>\r\n\t<comment></comment>\r\n\t<projects>\r\n\t</projects>\r\n\t<buildSpec>\r\n\t\t<buildCommand>\r\n\t\t\t<name>org.eclipse.jdt.core.javabuilder</name>\r\n\t\t\t<arguments>\r\n\t\t\t</arguments>\r\n\t\t</buildCommand>\r\n\t</buildSpec>\r\n\t<natures>\r\n\t\t<nature>org.eclipse.jdt.core.javanature</nature>\r\n\t\t<nature></nature>\r\n\t</natures>\r\n</projectDescription>");
		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

	private void createBUILDProperties(IProject project) {

		try {
			IFile buildPropertiesFile = project.getFile("build.properties");

			buildPropertiesFile.create(null, true, null);

			Properties buildProperties = new Properties();
			OutputStream outstream = new FileOutputStream(buildPropertiesFile
					.getRawLocation().toOSString());

			buildProperties.setProperty("source", "java");
			buildProperties.setProperty("output", "bin");

			buildProperties.store(outstream,
					"Project Specific Build Properties");

			buildProperties.clear();
			buildProperties.setProperty("WEB-INF/classes", "bin");

			buildProperties.setProperty("WEB-INF/lib", "");

			buildProperties.setProperty("WEB-INF/", "");

			buildProperties.setProperty(".", "");
			buildProperties.store(outstream,
					"SAR File Specific Build Properties");
		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}

}
