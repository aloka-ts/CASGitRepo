//***********************************************************************************
//GENBAND, Inc. Confidential and Proprietary
//
//This work contains valuable confidential and proprietary
//information.
//Disclosure, use or reproduction without the written authorization of
//GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
//is protected by laws of United States and other countries.
//If publication of work should occur the following notice shall
//apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//***********************************************************************************
                                                                                                     
                                                                                                     
//***********************************************************************************
//
//      File:   JetTask.java
//
//      Desc:   This file compiles JET templates into Java source files using the EMF
//		 JET framework
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh	              27/12/07        Initial Creation
//
//***********************************************************************************
                                                                                                     
                                                                                                     
package com.baypackets.ase.soa.util;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;
import org.eclipse.emf.codegen.jet.JETCompiler;
import org.eclipse.emf.codegen.jet.JETException;
import org.eclipse.emf.codegen.jet.JETSkeleton;
                                                                                                 
                                                                                                     
public class JetTask extends Task  {
	
	private File destDir;
	private List filesets = new ArrayList();
	private List templateFiles = new ArrayList();
	private File templateFile;
	private String packageName;
	private String className;

	public void execute() throws BuildException {
		preCheck();

		// check that filename ends in 'jet'?
		if (templateFile != null) {
			templateFiles.add(templateFile);
		}
		for (Iterator i = filesets.iterator(); i.hasNext();) {
			FileSet fs = (FileSet) i.next();
			DirectoryScanner ds = fs.getDirectoryScanner(getProject());
			//log("value of ds.getDir(): " + ds.getBasedir());
			File fromDir = fs.getDir(getProject());
			//log("value of fromDir: " + fromDir);
			String[] includedFiles = ds.getIncludedFiles();
			for (int j = 0; j < includedFiles.length; j++) {
				//log("IncludedFile "+j +": " + includedFiles[j]);
				templateFiles.add(project.resolveFile(includedFiles[j],fromDir));
			}
			/*
			File[] includedFiles = fromDir.listFiles();
			for (int j = 0; j < includedFiles.length; j++) {
				templateFiles.add(includedFiles[j]);
			}
			*/
		}

		try {
			compileJETTemplates();
		} catch (Exception e) {
			throw new BuildException(e, location);
		}
	}

	public void setDestdir(File destDir) {
		this.destDir = destDir;
	}

	public void setTemplate(File templateFile) {
		this.templateFile = templateFile;
	}

	public void setPackage(String packageName) {
		this.packageName = packageName;
	}

	public void setClass(String className) {
		this.className = className;
	}

	public void addFileset(FileSet fileset) {
		filesets.add(fileset);
	}

	private void preCheck() throws BuildException {
		assertTrue(destDir != null, "No destination directory specified");
		assertTrue(
			destDir.isDirectory(),
			"Specified destination directory does not exist: " + destDir);
		assertTrue(
			templateFile != null || filesets.size() > 0,
			"No template files specified");
		if (templateFile != null) {
			assertTrue(
				templateFile.isFile(),
				"Specified template does not exist: " + templateFile);
		} else {
			assertTrue(
				className == null && packageName == null,
				"Attributes \"class\" and \"package\" can only be used together with attribute \"template\"");
		}
	}

	private void compileJETTemplates()
		throws MalformedURLException, JETException, FileNotFoundException {
		log(
			"Compiling "
				+ templateFiles.size()
				+ " JET template(s) to "
				+ destDir);
		for (Iterator i = templateFiles.iterator(); i.hasNext();) {
			File template = (File) i.next();
			compileJETTemplate(template, destDir);
		}
	}

	private File compileJETTemplate(File template, File outputDir)
		throws JETException, FileNotFoundException, MalformedURLException {
		log("Compiling template : " + template);
		String templateURI = template.getAbsoluteFile().toURL().toString();
		JETCompiler jetCompiler = new JETCompiler(templateURI);
		log("Parsing template " + template, Project.MSG_VERBOSE);
		jetCompiler.parse();
		JETSkeleton skeleton = null;
		// initialize skeleton
		//JETSkeleton skeleton = jetCompiler.getSkeleton();
		//if (skeleton == null) {
	//		log("JET directive missing in " + templateFile, Project.MSG_WARN);
			jetCompiler.handleDirective("jet", null, null, new HashMap());
			skeleton = jetCompiler.getSkeleton();
	//	}
		if (className != null) {
			skeleton.setClassName(className);
		}
		log("Package name: " + packageName);
		if (packageName != null) {
			skeleton.setPackageName(packageName);
		}

		File packageDir =
			new File(outputDir, skeleton.getPackageName().replace('.', '/'));
		packageDir.mkdirs();
		File targetFile =
			new File(packageDir, skeleton.getClassName() + ".java");

		OutputStream output =
			new BufferedOutputStream(new FileOutputStream(targetFile));
		log("Generating java source " + targetFile, Project.MSG_VERBOSE);
		jetCompiler.generate(output);
		return targetFile;
	}

	private void assertTrue(boolean expr, String message)
		throws BuildException {
		if (!expr) {
			throw new BuildException(message, location);
		}
	}


}

