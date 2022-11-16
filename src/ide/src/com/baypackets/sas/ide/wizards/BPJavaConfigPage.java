package com.baypackets.sas.ide.wizards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPage;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.alc.AlcExtensionNature;
import com.baypackets.sas.ide.alc.AlcNature;
import com.baypackets.sas.ide.util.BPSASSOAServicesNature;
import com.baypackets.sas.ide.util.BPSASServicesNature;

public class BPJavaConfigPage extends NewJavaProjectWizardPage {

	/*
	 * public static final String[] DEFAULT_JAR_FILES = new String[] {
	 * "/otherjars/sipservlet.jar", "/otherjars/servlet-2.4.jar",
	 * "/otherjars/log4j-1.2.8.jar", "/bpjars/bpsbb.jar",
	 * "/bpjars/bpresource.jar"};
	 */

	public static String[] DEFAULT_JAR_FILES = new String[] {
			"sipservlet-1_1-api.jar", "sipservlet-common-api-5.3.0.jar",
			"servlet-api.jar,log4j-1.2.16.jar", "sbb-if.jar", "bpresource.jar",
			"bpari.jar", "tcap-provider-api.jar" };

	public static final String DEFAULT_CLASSPATH = "default.classpath";
	public static final String DIAMETER_RA_CLASSPATH = "diameter.ra.classpath";
	public static final String HTTP_RA_CLASSPATH = "http.ra.classpath";
	public static final String SOA_CLASSPATH = "soa.classpath";
	public static final String ALC_CLASSPATH = "alc.classpath";
	public static final String LOG4J_CLASSPATH = "log4j.classpath";
	public static final String JAVA_DOC_EXT_PROP="java.docs.extension";

	public static final String DIAMETER_RA_JARS="rf-if.jar,ro-if.jar,sh-if.jar,gy-if.jar";
	public static final String HTTP_RA_JARS="http-if.jar";
	public static final String SOA_IFACE_JAR = "soa-iface.jar";
	public static final String ALC_JAR = "alc.jar";
	public static final String ALC_JAXB_JAR = "alc_JAXB.jar";
	public static  String JAVA_DOC_FILE_EXT="-docs.zip";
	
	private WizardNewProjectCreationPage mainPage = null;

	public BPJavaConfigPage(IWorkspaceRoot root,
			WizardNewProjectCreationPage mainpage) {
		super(root, mainpage);
		this.mainPage = mainpage;
	}

	protected IPath getLocationPath() {
		IPath location = null;
		if (!mainPage.useDefaults()) {
			location = mainPage.getLocationPath();
		}
		return location;
	}

	protected void initBuildPaths() {
		ArrayList list = new ArrayList();
		BPProjectTypePage appPage = ((BPProjectWizard) this.getWizard())
				.getApplicationTypePage();
		list.add(JavaCore.newSourceEntry(mainPage.getProjectHandle()
				.getFullPath().append("/src")));
		// Add the default Jar files to the Classpath.

		// As we are supplying all these jars in the library/*.jars
		// So we should include jars from this folder only

		// IPath aseHome = JavaCore.getClasspathVariable("ASE_HOME");

		IPath classpathProp = new Path(
				SasPlugin.fullPath("project_classpath.properties"));

		File clasPropFile = classpathProp.toFile();
		SasPlugin.getDefault().log(
				"Reading the project_classpath.properties :" + clasPropFile);
		Properties pathp = new Properties();
		try {
			pathp.load(new FileReader(clasPropFile));
			String classp = pathp.getProperty(DEFAULT_CLASSPATH);
			
			SasPlugin.getDefault().log("The property "+DEFAULT_CLASSPATH+" is" + classp);
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

		IPath aseHome = new Path(SasPlugin.fullPath("library"));
		String docExt=pathp.getProperty(JAVA_DOC_EXT_PROP);
		
		if(docExt!=null){
			JAVA_DOC_FILE_EXT=docExt;
		}
		
		String javadoc_loc_prefix ="jar:file:/"+aseHome.append("docs/").toPortableString();
		
		String javadoc_loc_suffix=JAVA_DOC_FILE_EXT+"!/";
				
		SasPlugin.getDefault().log("Plugin library home :" + aseHome);
		SasPlugin.getDefault().log("JAVA_DOC_FILE_EXT :" + pathp.getProperty(JAVA_DOC_EXT_PROP));
		
		if (aseHome != null) {
			if (!appPage.isAlcExtension()) {
				
				for (int i = 0; i < DEFAULT_JAR_FILES.length; i++) {

					//jar:file:/D:/Eclipse_kepler/eclipse/plugins/com.baypackets.sas.ide_6.0.0.8/library/docs/sipservlet-1_1-api-docs.zip!/
					String doclocation=javadoc_loc_prefix+DEFAULT_JAR_FILES[i].substring(0, DEFAULT_JAR_FILES[i].indexOf(".jar"))+javadoc_loc_suffix;
					
					SasPlugin.getDefault().log("JAVADOC location is " + doclocation);
					
					IClasspathAttribute ca=JavaCore.newClasspathAttribute("javadoc_location",doclocation);
					
					IClasspathEntry cp = JavaCore.newLibraryEntry(
							aseHome.append(DEFAULT_JAR_FILES[i]), null, null,null,new IClasspathAttribute[]{ca}, false);
					list.add(cp);
					SasPlugin.getDefault().log("Adding to classpath:" + cp);
				}

			}
			
           if (appPage.isDiameterRAApplication()) {
        	   
					String diam = pathp.getProperty(DIAMETER_RA_CLASSPATH);
					SasPlugin.getDefault().log("Found property ??:" + DIAMETER_RA_CLASSPATH + diam);
					if(diam==null){
						diam=DIAMETER_RA_JARS;
					}
					String[] diamjarsArr = diam.split(",");
					
					for (String diamjar : diamjarsArr) {	
						
						String doclocation=javadoc_loc_prefix+diamjar.substring(0, diamjar.indexOf(".jar"))+javadoc_loc_suffix;
						SasPlugin.getDefault().log("JAVADOC location is " + doclocation);
						
						IClasspathAttribute ca=JavaCore.newClasspathAttribute("javadoc_location",doclocation);
						
						IClasspathEntry cp = JavaCore.newLibraryEntry(
								aseHome.append(diamjar), null, null,null,new IClasspathAttribute[]{ca}, false);
						list.add(cp);
					}
			 
			}
           
           if (appPage.isHttpRAApplication()) {
        	   
				String httpRA = pathp.getProperty(HTTP_RA_CLASSPATH);
				SasPlugin.getDefault().log("Found property ??:" + HTTP_RA_CLASSPATH + httpRA);
				if(httpRA==null){
					httpRA=HTTP_RA_JARS;
				}
				String[] httpRAjarsArr = httpRA.split(",");
				
				for (String httpRAjar : httpRAjarsArr) {	
					
					String doclocation=javadoc_loc_prefix+httpRAjar.substring(0, httpRAjar.indexOf(".jar"))+javadoc_loc_suffix;
					SasPlugin.getDefault().log("JAVADOC location is " + doclocation);
					
					IClasspathAttribute ca=JavaCore.newClasspathAttribute("javadoc_location",doclocation);
					
					IClasspathEntry cp = JavaCore.newLibraryEntry(
							aseHome.append(httpRAjar), null, null,null,new IClasspathAttribute[]{ca}, false);
					list.add(cp);
				}
		 
		}
      
			

			// REETA added
			if (appPage.isSoaService() || appPage.isSoaApplication()) {
				if (pathp.getProperty(SOA_CLASSPATH) != null) {
					
					SasPlugin.getDefault().log("Found property :" + SOA_CLASSPATH);
					String soap = pathp.getProperty(SOA_CLASSPATH);
					String[] soajarsArr = soap.split(",");
					
					for (String soajar : soajarsArr) {	
						
						String doclocation=javadoc_loc_prefix+soajar.substring(0, soajar.indexOf(".jar"))+javadoc_loc_suffix;
						SasPlugin.getDefault().log("JAVADOC location is " + doclocation);
						
						IClasspathAttribute ca=JavaCore.newClasspathAttribute("javadoc_location",doclocation);
						
						IClasspathEntry cp = JavaCore.newLibraryEntry(
								aseHome.append(soajar), null, null,null,new IClasspathAttribute[]{ca}, false);
						list.add(cp);
					}
				} else {
					IClasspathEntry cp = JavaCore.newLibraryEntry(
							aseHome.append(SOA_IFACE_JAR), null, null);
					list.add(cp);
				}
			}

			if (appPage.isAlcService() || appPage.isAlcExtension()) {

				if (pathp.getProperty(ALC_CLASSPATH) != null) {
					SasPlugin.getDefault().log("Found property :" + ALC_CLASSPATH);
					
					String alcp = pathp.getProperty(ALC_CLASSPATH);
					String[] alcjarsArr = alcp.split(",");
					for (String alcjar : alcjarsArr) {
						
						String doclocation=javadoc_loc_prefix+alcjar.substring(0, alcjar.indexOf(".jar"))+javadoc_loc_suffix;
						SasPlugin.getDefault().log("JAVADOC location is " + doclocation);
						
						IClasspathAttribute ca=JavaCore.newClasspathAttribute("javadoc_location",doclocation);
						
						IClasspathEntry cp = JavaCore.newLibraryEntry(
								aseHome.append(alcjar), null, null,null,new IClasspathAttribute[]{ca}, false);
						list.add(cp);
					}
				} else {
					IClasspathEntry cp = JavaCore.newLibraryEntry(
							aseHome.append(ALC_JAR), null, null);
					list.add(cp);
					cp = JavaCore.newLibraryEntry(
							aseHome.append(this.ALC_JAXB_JAR), null, null);
					list.add(cp);
				}
			}

			if (appPage.isAlcExtension()) {
				String logp = pathp.getProperty(LOG4J_CLASSPATH);
				
				if(logp!=null){
					SasPlugin.getDefault().log("Found property :" + LOG4J_CLASSPATH);
					IClasspathEntry cp = JavaCore.newLibraryEntry(
							aseHome.append(logp), null, null);
					list.add(cp);
				}
				
			}

		}
		IClasspathEntry[] cps = new IClasspathEntry[list.size()];
		cps = (IClasspathEntry[]) list.toArray(cps);
		this.setDefaultClassPath(cps, true);

		// Set the destination location for this project.
		IPath outputPath = mainPage.getProjectHandle().getFullPath()
				.append("/bin");
		this.setDefaultOutputFolder(outputPath);
		super.initBuildPaths();

		// if(appPage.isAlcService()){
		// this.addAlcNature(mainPage.getProjectHandle());
		// }
	}

	/**
	 * Add ALC nature to this New project
	 * 
	 * @param project
	 *            to have nature added
	 */
	public void addAlcNature(IProject project) {
		try {
			SasPlugin.getDefault().log(
					"Add ALC Extension nature for project!!!!!!!!!!!!"
							+ project.getName());
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();

			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = AlcNature.NATURE_ID;
			description.setNatureIds(newNatures);
			SasPlugin.getDefault().log(
					"Add Project nature!!!!!!!!!!!!" + newNatures);
			for (int j = 0; j < newNatures.length; j++) {
				SasPlugin.getDefault().log(
						"Add Project nature!!!!!!!!!!!!" + newNatures[j]);
			}

			project.setDescription(description, null);
		} catch (CoreException e) {
		}
	}

	/**
	 * Add ALC Extension nature to this New project
	 * 
	 * @param project
	 *            to have nature added
	 */
	public void addAlcExtensionNature(IProject project) {
		try {
			SasPlugin.getDefault().log(
					"Add ALC Extension nature for project!!!!!!!!!!!!"
							+ project.getName());
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = AlcExtensionNature.NATURE_ID;
			description.setNatureIds(newNatures);
			SasPlugin.getDefault().log(
					"Add Project nature!!!!!!!!!!!!" + newNatures);
			for (int j = 0; j < newNatures.length; j++) {
				SasPlugin.getDefault().log(
						"Add Project nature!!!!!!!!!!!!" + newNatures[j]);
			}

			project.setDescription(description, null);
		} catch (CoreException e) {
		}
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
					"Add SASServicesNature for project!!!!!!!!!!!!"
							+ project.getName());
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

	/**
	 * Add SASServices nature to this New project
	 * 
	 * @param project
	 *            to have nature added
	 */
	public void addSASSOAServicesNature(IProject project) {
		try {
			SasPlugin.getDefault().log(
					"Add SASSOAServicesNature for project!!!!!!!!!!!!"
							+ project.getName());
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			// Add the nature
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = BPSASSOAServicesNature.NATURE_ID;
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
