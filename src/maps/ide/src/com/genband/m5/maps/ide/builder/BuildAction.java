package com.genband.m5.maps.ide.builder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
//import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.genband.m5.maps.ide.MyObjectInputStream;
import com.genband.m5.maps.ide.model.CPFScreen;
import com.genband.m5.maps.ide.preferences.PreferenceConstants;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import com.genband.m5.maps.ide.sitemap.util.SiteMapUtil;

import com.genband.m5.maps.ide.CPFPlugin;

public class BuildAction implements IObjectActionDelegate {

	private IWorkbenchWindow window;
	private ISelection selection;
	protected IVMInstall jre;
	private String dirName="WEB-INF";
	private String sepChar = File.separator;
	private String mbeansLoc="com"+sepChar+"genband"+sepChar+"m5"+sepChar+"maps"+sepChar+"mbeans";
	private String webServiceLoc1="com"+sepChar+"genband"+sepChar+"m5"+sepChar+"maps"+sepChar+"services";
	private String webServiceLoc2="com"+sepChar+"genband"+sepChar+"m5"+sepChar+"maps"+sepChar+"messages";
	private String resourceBundleLoc="bundle"+sepChar;
	private String pluginPath=null;
	private boolean success = true;
        //Changes for Close/Open Project
	private IProgressMonitor monitor;        

	
	public BuildAction() {
		super();
	}
	
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void run(IAction action) {
		try{
		IFile sitemapFile = null;
		SiteMap siteMap = null;
		CPFPortletCSVWriter csvWriter= new CPFPortletCSVWriter();
		Shell shell = CPFPlugin.getDefault().getWorkbench()
		.getActiveWorkbenchWindow().getShell();
		success = true;
		if (selection instanceof IStructuredSelection) {
			for (Iterator it = ((IStructuredSelection) selection).iterator(); it.hasNext();) {
				Object element = it.next();
				IProject project = null;
				if (element instanceof IProject) {
					project = (IProject) element;
				} else if (element instanceof IAdaptable) {
					//Unreachable code
					project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
					List<IFile> siteMapFiles = SiteMapUtil.getSiteMapFiles(project.getName());
					for ( int i = 0 ; i < siteMapFiles.size() ; i++ ) {
						ObjectInputStream in = null;
						
						try {
							in = new ObjectInputStream(sitemapFile.getContents());
						} catch (IOException e) {
							e.printStackTrace();
						} catch (CoreException e) {
							e.printStackTrace();
						}
						
						try {
							siteMap = (SiteMap) in.readObject();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
																		
						try {
							in.close();
							//success = false;
						} catch (IOException e) {
							e.printStackTrace();
						}	
					}
					//Unreachable code		
				}
				if (project != null) {
					
					List<IFile> siteMapFiles = SiteMapUtil.getSiteMapFiles(project.getName());
					//Creates the EarContent Directory in absolute project path.
					String absProjPath = getProjectPath(project.getName());
					
					createDir(absProjPath,"EarContent");
					CPFPlugin.getDefault().info("run() : Absolute Project Path : "+absProjPath);
					String destLoc = absProjPath.concat(sepChar+"EarContent"+sepChar);
					
					CPFPlugin.getDefault().log("sitemap list : " + siteMapFiles);
					
					
					// Changes related to PR 49914 (ClassLoading issue) starts
					List<String> externalJarsPath = SiteMapUtil.getExtrnalJars(project.getName());
					File gbFile = new File (CPFPlugin.fullPath("library/gb-common.jar"));
					ClassLoader parentLoader = 	CPFScreen.class.getClassLoader();
					URLClassLoader loader = null;
					try {
						loader = new URLClassLoader(new URL[] { new URL(
								"file:///" + gbFile.getAbsolutePath()) }, parentLoader);
					} catch (MalformedURLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
						CPFPlugin.getDefault().log("MalformedURLException gb-common");
						//success = false;
					}
					//URLClassLoader loader = (URLClassLoader)Thread.currentThread().getContextClassLoader();

					
					URL urls[]= new URL[30];
					for ( int i = 0 ; i < externalJarsPath.size() ; i++ ) {
						File jarFile = new File (externalJarsPath.get(i));
						try {
							urls[i] = new URL("file:///" + jarFile.getAbsolutePath());
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							CPFPlugin.getDefault().log("MalformedURLException " + jarFile.getName());
							e.printStackTrace();
						}
						
					}
					
					loader = new URLClassLoader(urls, parentLoader);
					
					
					/*for ( int i = 0 ; i < externalJarsPath.size() ; i++ ) {
						File file1 = new File (externalJarsPath.get(i));
						
						try {
							loader = new URLClassLoader(new URL[] { new URL(
									"file:///" + file1.getAbsolutePath()) }, parentLoader);
						} catch (MalformedURLException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						//Thread.currentThread().setContextClassLoader( loader );
						//CPFPlugin.getDefault().log(" new loader url is : " + loader.getURLs()[0]);
						//CPFPlugin.getDefault().info("url is : " + ((URLClassLoader)Thread.currentThread().getContextClassLoader()).getURLs()[0]);
					}*/
					// Changes related to PR 49914 (ClassLoading issue) ends
					
					
					for ( int i = 0 ; i < siteMapFiles.size() ; i++ ) {
						
						sitemapFile = siteMapFiles.get(i);
						ObjectInputStream in = null;
						
						try {
												
							//in = new ObjectInputStream(sitemapFile.getContents());
							in = new MyObjectInputStream(loader,sitemapFile.getContents());
							// Changed due to PR 49914 (ClassLoading issue)
						
						} catch (Exception e) {
							CPFPlugin.getDefault().error("Exception while reading siteMap!!!");
								e.printStackTrace();
							
								throw e;
						} 
						try {
							
							String fileLoc="";
							siteMap = (SiteMap)in.readObject();
																											
							//xmlLocation identifies the place to put the relevant XMLs for a sitemap.
							createDir(absProjPath,siteMap.getName()+sepChar+dirName);
							String xmlLocation = absProjPath.concat(sepChar).concat(siteMap.getName()).concat(sepChar+"WEB-INF"+sepChar);
							CPFPlugin.getDefault().info("xmlLocation :" + xmlLocation);
							
							//Create JSF location and move files there.
							createDir(xmlLocation,"jsf");
							String srcDir = absProjPath+sepChar+"WebContent"+sepChar;
							copyXHTMLFiles(srcDir,xmlLocation.concat("jsf").concat(sepChar));
							
							//Create help directory parallel to JSF
							createDir(xmlLocation,"help");
							String helpSrcLoc = absProjPath+sepChar+"WebContent"+sepChar+"help"+sepChar;
							String helpDestLoc = xmlLocation+sepChar+"help"+sepChar;
							copyDir(helpSrcLoc,helpDestLoc);
							
							String destbeansLoc = "classes"+sepChar+mbeansLoc;
							createDir(xmlLocation,destbeansLoc);
							srcDir = absProjPath+sepChar+"bin"+sepChar+mbeansLoc;
							copyDir(srcDir,xmlLocation.concat(destbeansLoc).concat(sepChar));
														
							//Generating Object XML
							ObjectXML oObjectXML = new ObjectXML();
							String objectXMLString = oObjectXML.generate(siteMap);
							fileLoc = xmlLocation+siteMap.getName().concat("-object.xml");
							createXMLFile(fileLoc,objectXMLString);
							
							//Generating faces-config for each CPF Portlet 
							FacesConfigXML oFacesConfigXML = new FacesConfigXML();
							String configXMLString = oFacesConfigXML.generate(siteMap);
							fileLoc = xmlLocation.concat("faces-config.xml");
							createXMLFile(fileLoc,configXMLString);
																					
							//Generating portlet.xml
							PortletXML oPortletXML = new PortletXML();
							String portletXMLString = oPortletXML.generate(siteMap);
							fileLoc = xmlLocation.concat("portlet.xml");
							CPFPlugin.getDefault().log("File Location : "+fileLoc);
							createXMLFile(fileLoc,portletXMLString);
							
							//Generating jboss-portlet.xml
							JbossPortletXML oJbossPortletXML = new JbossPortletXML();
							String jbossPortletXMLString = oJbossPortletXML.generate(siteMap);
							fileLoc = xmlLocation.concat("jboss-portlet.xml");
							CPFPlugin.getDefault().log("File Location : "+fileLoc);
							createXMLFile(fileLoc,jbossPortletXMLString);
							
							//Generating portlet-instance.xml
							InstanceXML oInstanceXML = new InstanceXML();
							String instanceXMLString = oInstanceXML.generate(siteMap);
							fileLoc = xmlLocation.concat("portlet-instances.xml");
							CPFPlugin.getDefault().log("File Location : "+fileLoc);
							createXMLFile(fileLoc,instanceXMLString);
							
							//Packaging resource Bundle files
							createDir(xmlLocation+sepChar+"classes"+sepChar,"bundle");
							srcDir = absProjPath+sepChar+resourceBundleLoc;
							copyDir(srcDir,xmlLocation+"classes"+sepChar+"bundle"+sepChar);
				
							//Copying sitemap Utils to sitemap directory.Placing parallel to WEB-INF.
							try{
							
							String utilsPath=CPFPlugin.getDefault().fullPath("resources"+sepChar+"misc"+sepChar+"sitemap"+sepChar);
							String toDir=absProjPath.concat(sepChar).concat(siteMap.getName()).concat(sepChar);
							CPFPlugin.getDefault().log("utils Path Location : "+utilsPath);
							CPFPlugin.getDefault().log("going to call buildJar(utilsPath,toDir,\"utils.jar\")");
							buildJar(utilsPath,toDir,"utils.jar");
							CPFPlugin.getDefault().log("after calling buildJar(utilsPath,toDir,\"utils.jar\")");
							
							CPFPlugin.getDefault().log("toDir : " +toDir + "toDir : " + toDir);
							CPFPlugin.getDefault().log("going to call unJar(toDir,toDir)");
							unJar(toDir,toDir);
							CPFPlugin.getDefault().log("after calling unJar(toDir,toDir)");
							
							File utilFile= new File (toDir.concat("utils.jar"));
							utilFile.delete();
							
							}catch(NullPointerException npe){
							
								CPFPlugin.getDefault().error("sitemap directory in resources of plugins NOT FOUND !");
								npe.printStackTrace();
								//success = false;
							}							
							//Going to jar the sitemap files in WEB-INF folder and move war to EarContent
							String jarFileName = siteMap.getName()+".war";
							String srcLoc = absProjPath.concat(sepChar).concat(siteMap.getName()).concat(sepChar);
							CPFPlugin.getDefault().log("scrlocation : " + srcLoc + "\ndestLoc :" + destLoc + "\njarFileName: " + jarFileName);
							CPFPlugin.getDefault().log("going to call buildJar(srcLoc,destLoc,jarFileName)");
							buildJar(srcLoc,destLoc,jarFileName);
							CPFPlugin.getDefault().log("after calling buildJar(srcLoc,destLoc,jarFileName)");
																																		
							
						} catch (IOException e) {
							CPFPlugin.getDefault().log("22222 IOException");
								e.printStackTrace();
								CPFPlugin.getDefault().log("setting success = false run IOException");
								success = false;
						} catch (ClassNotFoundException e) {
							CPFPlugin.getDefault().log("3333 IOException");
								e.printStackTrace();
								CPFPlugin.getDefault().log("setting success = false run ClassNotFoundException");
								success = false;
						}
												
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
							CPFPlugin.getDefault().log("setting success = false in.close() IOException");
							success = false;
						}	
						
						File sitemapDir = new File(absProjPath.concat(sepChar).concat(siteMap.getName()).concat(sepChar));
						if(deleteAllFiles(sitemapDir)){
							CPFPlugin.getDefault().info("Temporary Directory for sitemap removed :"+sitemapDir);
						}else{
							CPFPlugin.getDefault().error("Temporary Directory for sitemap could not be removed :"+sitemapDir);
							
						}
						
						
					}//Loop traversing all siteMaps over.
					
							//Create META-INF in EarContent
							createDir(absProjPath.concat(sepChar+"EarContent"+sepChar),"META-INF");
					
							//Create application.xml and move to META-INF
							ApplicationXML oAppXML = new ApplicationXML();
							String appXMLString = oAppXML.generate(project);
							String appFileLoc = absProjPath.concat(sepChar+"EarContent"+sepChar).concat("META-INF"+sepChar);
							String appFile = appFileLoc.concat("application.xml");
							CPFPlugin.getDefault().log("Application File Location : "+appFile);
							createXMLFile(appFile,appXMLString);
														
							/*
							 * Step 1 : Bringing Common jars to tmpJar Directory.
							 * Unjar to commonJar and re-making the jar as common.jar.
							 */ 
							
							createDir(absProjPath,"tmpJar");
							createDir(absProjPath,"commonJar");
							String toJarLoc = absProjPath.concat(sepChar+"commonJar"+sepChar);
							String tmpJarLoc = absProjPath.concat(sepChar+"tmpJar"+sepChar);
							String entityJarLoc=absProjPath.concat(sepChar+"EJBContent"+sepChar);
							
							copyDir(entityJarLoc,tmpJarLoc);
														
							try{
								pluginPath=CPFPlugin.getDefault().fullPath("library"+sepChar+"gb-common.jar");
								CPFPlugin.getDefault().info("gb-common Plugin path : "+pluginPath);
								if(pluginPath != null){
								
									copyFile(pluginPath,tmpJarLoc,"gb-common.jar");
								
								}
							
							}catch(NullPointerException npe){
								CPFPlugin.getDefault().error("gb-common.jar path NOT FOUND !");
								npe.printStackTrace();
								CPFPlugin.getDefault().log("setting success = false");
								success = false;
							}
							//Step2 : Unjar all jars brought to tmpJar	
							CPFPlugin.getDefault().log("tmpJarLoc" + tmpJarLoc + "toJarLoc" + toJarLoc);
							CPFPlugin.getDefault().log("going to call unJar(tmpJarLoc,toJarLoc)");
							unJar(tmpJarLoc,toJarLoc);
							CPFPlugin.getDefault().log("after calling unJar(tmpJarLoc,toJarLoc)");
						
							createDir(toJarLoc,"META-INF");
							
							//Step3 : Moving persistence.xml to META-INF of common.jar
							try{
								
								pluginPath=CPFPlugin.getDefault().fullPath("resources"+sepChar+"misc"+sepChar+"persistence.xml");
								copyFile(pluginPath,toJarLoc.concat(sepChar+"META-INF"+sepChar),"persistence.xml");
								
								CPFPlugin.getDefault().info("persistence.xml path : "+pluginPath);
								
								String userName=null;
								
								//update user name in persistence to xml check if project preferences have it
								IScopeContext projectScope = new ProjectScope(project);
								IEclipsePreferences projectNode = projectScope
										.getNode("com.genband.sas.maps");
								if (projectNode != null) {
									userName = projectNode.get(PreferenceConstants.P_DB_USER, CPFPlugin
											.getDefault().getPreferenceStore().getString(
													PreferenceConstants.P_DB_USER));
								}
								
								// project preferences donot have it so ask user
								if (userName == null || userName.equals("")){
							
									UpdateUserDialog dialog=new UpdateUserDialog(shell);
									dialog.open();

									shell.setText("Update DataBase User Name ");
									if(dialog.okPressed()){
										userName=dialog.getUserName();
									}
								}	
									
								//if userName is not empty update it
									if(userName != null && !userName.equals("")){
										if (projectNode != null) {
											projectNode.put(PreferenceConstants.P_DB_USER,userName );
											projectNode.flush();
										}
										 String filePath=toJarLoc.concat(sepChar+"META-INF"+sepChar)+"persistence.xml";
										 File persistenceFile=new File(filePath);
										 updatePersistenceFile(persistenceFile, userName);
										}

								
							}catch(NullPointerException npex){
								CPFPlugin.getDefault().error("persistence.xml path NOT FOUND !");	
								npex.printStackTrace();
								CPFPlugin.getDefault().log("setting success = false");
								success = false;
							}
							
							//Step 4 : Build a jar of all un-jarred file in common location
							CPFPlugin.getDefault().log("toJarLoc : " +toJarLoc + "destLoc: " + destLoc);
							CPFPlugin.getDefault().log("going to call buildJar(toJarLoc,destLoc,\"common.jar\")");
							buildJar(toJarLoc,destLoc,"common.jar");
							CPFPlugin.getDefault().log("after calling buildJar(toJarLoc,destLoc,\"common.jar\")");
											
							
							//Remove temporary locations
							File jarCommonDir = new File(absProjPath.concat(sepChar+"commonJar"+sepChar));
							if(deleteAllFiles(jarCommonDir)){
								CPFPlugin.getDefault().info("Temporary Directory for common jar removed :"+jarCommonDir);
							}else{
								CPFPlugin.getDefault().error("Temporary Directory for common jar could not be removed :"+jarCommonDir);
								
							}
							File jarTmpDir = new File(absProjPath.concat(sepChar+"tmpJar"+sepChar));
							if(deleteAllFiles(jarTmpDir)){
								CPFPlugin.getDefault().info("Temporary Directory for common jar removed :"+jarTmpDir);
							}else{
								CPFPlugin.getDefault().error("Temporary Directory for common jar could not be removed :"+jarTmpDir);
							}
														
							try{
								
								pluginPath=CPFPlugin.getDefault().fullPath("library"+sepChar+"CPF_EJB.jar");
								CPFPlugin.getDefault().info("cpf_ejb Plugin path : "+pluginPath);
								if (pluginPath != null){
									copyFile(pluginPath,destLoc,"CPF_EJB.jar");
								}
							
							}catch(NullPointerException npex){
								CPFPlugin.getDefault().info("CPF_EJB.jar path NOT FOUND !");
								npex.printStackTrace();
								CPFPlugin.getDefault().log("setting success = false ");
								success = false;
							}
													
							/*
							 * **** Creating WebService WAR if WebService is Enabled. ****
							 */
							try{
							String webXMLFileLoc = absProjPath+sepChar+"WebContent"+sepChar+"WEB-INF"+sepChar+"web.xml";
							File webXMLFile = new File(webXMLFileLoc);
							if(webXMLFile.exists()){
								
								CPFPlugin.getDefault().info("web.xml found in WebContent . Going to package Webservices war");
								
								createDir(absProjPath,sepChar+"WebServices"+sepChar+dirName);
								String tempWSLoc = absProjPath.concat(sepChar+"WebServices"+sepChar).concat(dirName).concat(sepChar);
								CPFPlugin.getDefault().log("tempWSLoc : "+absProjPath.concat(sepChar+"WebServices"+sepChar).concat(dirName).concat(sepChar));
								
								String sourceDir=absProjPath+sepChar+"bin"+sepChar+webServiceLoc1;
								String destDirLoc = "classes".concat(sepChar).concat(webServiceLoc1);
								createDir(tempWSLoc,destDirLoc);
								copyDir(sourceDir,tempWSLoc.concat(destDirLoc).concat(sepChar));
								
								sourceDir=absProjPath+sepChar+"bin"+sepChar+webServiceLoc2;
								destDirLoc = "classes".concat(sepChar).concat(webServiceLoc2);
								createDir(tempWSLoc,destDirLoc);
								copyDir(sourceDir,tempWSLoc.concat(destDirLoc).concat(sepChar));
								
								copyFile(absProjPath+sepChar+"WebContent"+sepChar+"WEB-INF"+sepChar+"web.xml",tempWSLoc,"web.xml");

								//adding jboss-web.xml for authentication
								String webServicePath = CPFPlugin.getDefault().fullPath("resources"+sepChar+"misc"+sepChar+"web-service"+sepChar);
								copyFile(webServicePath + "jboss-web.xml", tempWSLoc, "jboss-web.xml");
								
								//Going to jar the WebServices files in WEB-INF folder and move war to EarContent
								String jarFileName = project.getName()+"-WS"+".war";
								String srcLoc = absProjPath+sepChar+"WebServices"+sepChar;
							
								CPFPlugin.getDefault().log("scrLoc : " + srcLoc + "\ndestLoc :" + destLoc + "\njarFileName: " + jarFileName);
								CPFPlugin.getDefault().log("going to call buildJar(srcLoc,destLoc,jarFileName)");
								buildJar(srcLoc,destLoc,jarFileName);
								CPFPlugin.getDefault().log("after calling buildJar(srcLoc,destLoc,jarFileName)");
								
								//Remove temporary Location. 
								
								File webserviceDir = new File(absProjPath.concat(sepChar).concat("WebServices").concat(sepChar));
								if(deleteAllFiles(webserviceDir)){
									CPFPlugin.getDefault().info("Temporary Directory for WebServices removed :" + webserviceDir);
								}else{
									CPFPlugin.getDefault().error("Temporary Directory for WebServices could not be removed :"+webserviceDir);
									
								}
			
							}
							}catch(Exception e){
								CPFPlugin.getDefault().error(" Exception while creating web-service war");
								CPFPlugin.getDefault().log("setting success = false while creating web-service war Exception");
								success = false;

								
							}
                                                        //Changes for Close Open Project : PR 50496

							if (System.getProperty("os.name").indexOf("Win") == 0){
									CPFPlugin.getDefault().log("Platform : Windows");
									
							}
							else{
							
							try{
								CPFPlugin.getDefault().log("Going to call project.close()");
								project.close(monitor);
								CPFPlugin.getDefault().log("project.close() over.");
								CPFPlugin.getDefault().log("Going to call project.open().");
								project.open(monitor);
								CPFPlugin.getDefault().log("project.close() over.");
								
							}catch(CoreException ce){
								CPFPlugin.getDefault().error("Error Occured while performing Close/Open Project.");
								ce.printStackTrace();
								CPFPlugin.getDefault().log("setting success = false while Close/Open project .");
								success = false;
							}
							}
							
							//Changes Over : PR 50496
					
				}//End for Project.
				csvWriter.updatePortalSecurity(project.getName());
			}
		}//end of if for selection
				
		CPFPlugin.getDefault().log("Exiting Run()..");
		
		
		//////////////////////////////////////
		MessageDialog message;
		String labels[] = new String[1];
		labels[0] = "OK";
		//Shell shell = window.getShell();
		if ( true == success ){
		message = new MessageDialog(shell, "Build Portals", null,
				"Build is complete.", 2, labels, 1);
		}else{
		CPFPlugin.getDefault().log("success = false ...So displaying unsuccessful message");
		message = new MessageDialog(shell, "Build Portals", null,
				"Build was not successful.", 1, labels, 1);
		
		}
		message.open();

		
		//////////////////////////////////////
	}catch(Exception e){
		MessageDialog message;
		String labels[] = new String[1];
		labels[0] = "OK";
		Shell shell = CPFPlugin.getDefault().getWorkbench()
		.getActiveWorkbenchWindow().getShell();
		//Shell shell = window.getShell();
		CPFPlugin.getDefault().log("Exception caught...So displaying unsuccessful message");		
		message = new MessageDialog(shell, "Build Portals", null,
				"Build was not successful.", 1, labels, 1);
		message.open();

		
	}
	}
	
	/*
	 * Jars complete contents under directory argSL.
	 * Moves this jar to destination location passed in argDL.
	 */
	
	public void buildJar(String argSL, String argDL, String jarName){
		
		jre = JavaRuntime.getDefaultVMInstall();
		File jdkHome = jre.getInstallLocation();
		String jdkPath = jdkHome.getPath();
		IPath jdkPATH = new Path(jdkPath);
		String jarSyntax="";
		String properties="cf";
		String destJar="";		
		boolean isWindows = false;

		if (System.getProperty("os.name").indexOf("Win") == 0)
			isWindows = true;

		if (! jdkPATH.isEmpty()) {
		
		//Handling for Windows and Linux differently
			
		if (isWindows) {
			jarSyntax = "\"" + jdkPATH.append("bin").append("jar").toString() + "\"";
			destJar = "\"" + argDL+jarName + "\"";
		}
		else{
			jarSyntax = jdkPATH.append("bin").append("jar").toString();
			destJar = argDL+jarName;
		}	
		
		try {
		
			File srcDirPath = new File(argSL);
			Process process = null;

			if (isWindows) {

				CPFPlugin.getDefault().info("in BuildJar(): Jar Command : "+jarSyntax+" "+properties+" "+destJar+" "+"*");
				CPFPlugin.getDefault().log("before  jar command...");
				process = Runtime.getRuntime().exec (jarSyntax + " " +
																properties + " " +
																	destJar + " " + "*",
																null,
																srcDirPath);
				CPFPlugin.getDefault().log("after jar command...");
			}
			else {

				process = Runtime.getRuntime().exec("ls", null, srcDirPath);

			  	process.waitFor();
			
			  	int status = process.exitValue ();
			
			  	BufferedInputStream bis = new BufferedInputStream (process.getInputStream());
			
			  	byte[]  b = new byte[1024];
			  	String listOfFiles= "";
			  	int count = 0;

			  	while ((count = bis.read (b)) != -1) {
			        listOfFiles += new String (b, 0, count);
			  	}
			
			  	bis.close ();
				CPFPlugin.getDefault().info ("status for ls = " + status);
			
				CPFPlugin.getDefault().info ("ls = " + listOfFiles);
			
			
				CPFPlugin.getDefault().info("in BuildJar(): Jar Command : "+jarSyntax+" "+properties+" "+destJar+" "+ listOfFiles);

				process = Runtime.getRuntime().exec (jarSyntax + " " +
										properties + " " +
											destJar + " " + listOfFiles,
									null,
									srcDirPath);


			}
			CPFPlugin.getDefault().log("before printing status ::: ");
			process.waitFor();
			int status = process.exitValue ();
			CPFPlugin.getDefault().info ("status for jar command = " + status);
					    
		}
		catch(IOException ioe) {
			CPFPlugin.getDefault().log("IOException");
			ioe.printStackTrace();
			//throw ioe;
			CPFPlugin.getDefault().log("setting success = false in buildJar IOException");
			success = false;
		}
		catch(InterruptedException intex) {
			CPFPlugin.getDefault().log("InterruptedException");
			intex.printStackTrace();
			//throw intex;
			CPFPlugin.getDefault().log("setting success = false in buildJar InterruptedException");
			success = false;
		}catch(Exception e){
			CPFPlugin.getDefault().log("Exception");
			//throw e;
			CPFPlugin.getDefault().log("setting success = false in buildJar exception");
			success = false;
		}
		}//end of isEmpty
		else{
			CPFPlugin.getDefault().error("JDK not found.Kindly use JDK as Java Runtime Runtime Environment.");
		}
				
	}
	
    public void unJar(String fromLoc, String toLoc) {
		
		jre=JavaRuntime.getDefaultVMInstall();
		File jdkHome=jre.getInstallLocation();
		String jdkPath=jdkHome.getPath();
		IPath jdkPATH=new Path(jdkPath);
	        String jarLoc = null;
		String jarSyntax = "";
		String property = "xf";	

		if(!jdkPATH.isEmpty()){
		
		if (System.getProperty("os.name").indexOf("Win") == 0){
			jarSyntax = "\"" + jdkPATH.append("bin").append("jar").toString() + "\"";
			
		}else{
			jarSyntax = jdkPATH.append("bin").append("jar").toString();
			
		}	        
   	
		File fromDir = new File(fromLoc);
		File toDir = new File(toLoc);
		File[] children = fromDir.listFiles(); 
		for (int i=0; i<children.length; i++) {
		
		if (System.getProperty("os.name").indexOf("Win") == 0){
			jarLoc = "\"" + fromLoc+children[i].getName() + "\"";
		}else{
			jarLoc = fromLoc+children[i].getName();
		}
				
		try {
					
		    CPFPlugin.getDefault().info("in unjar() : Jar Command : "+jarSyntax+" "+property+" "+jarLoc+" "+ toDir);
			
		    Process process= Runtime.getRuntime().exec(jarSyntax+" "+property+" "+jarLoc,null,toDir);

			process.waitFor();
		
		}
		catch(IOException ioe){
			ioe.printStackTrace();
			//throw ioe;
			CPFPlugin.getDefault().log("setting success = false in unjar io");
			success = false;
		}
		catch(InterruptedException intex){
			intex.printStackTrace();
			//throw intex;
			CPFPlugin.getDefault().log("setting success = false in unjar InterruptedException");
			success = false;
		}catch(Exception e){
			e.printStackTrace();
			//throw e;
			CPFPlugin.getDefault().log("setting success = false in unjar exception");
			success = false;
		}
		}
		}//end of isEmpty
		else{
			CPFPlugin.getDefault().error("JDK not found.Kindly use JDK as Java Runtime Runtime Environment.");
		}
				
	}
	
	public void createXMLFile(String fileName,String xmlString) {
		
		try{
		File xmlFile = new File(fileName);
	    FileOutputStream fop = new FileOutputStream(xmlFile);

	    if(xmlFile.exists()){
	      
	    	fop.write(xmlString.getBytes());
            fop.flush();
	        fop.close();
	        CPFPlugin.getDefault().info("createXMLFile() : The String for "+fileName+" has been written to File.");
	
	    }
        else
        	CPFPlugin.getDefault().error("The XML file does not exist ! : "+xmlFile);
	    CPFPlugin.getDefault().info("Exiting createXMLFile() ..");
	    
		}catch(FileNotFoundException fnf){
			CPFPlugin.getDefault().log("setting success = false in createXMLFile FileNotFoundException");
			success = false;
			fnf.printStackTrace();
			
		}catch(IOException io){
			CPFPlugin.getDefault().log("setting success = false in createXMLFile IOException");
			success = false;
			io.printStackTrace();
		}
		}
	
	public String getProjectPath(String projectName) {
		
		String platformPath="";
		String projectPath="";
				
		if(null != Platform.getLocation()){
			platformPath = Platform.getLocation().toOSString();
		}
		if(null != ResourcesPlugin.getWorkspace() && ( null != ResourcesPlugin.getWorkspace().getRoot()) 
				&& (null!=ResourcesPlugin.getWorkspace().getRoot().getProject(projectName))){
			projectPath = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName).getFullPath().toOSString();
		}
		CPFPlugin.getDefault().info("getProjectPath() : Project Path : "+platformPath+""+projectPath);
		CPFPlugin.getDefault().info("getProjectPath() : Exiting getProjectPath()");
		return platformPath.concat(projectPath).concat(sepChar);
				
	}
	/*
	 * @pPath Absolute Path where director needs to be created.
	 * @dName Directory Name to be created in the project path
	 */
	public void createDir(String pPath, String dName){
		
		File dirFile = new File(pPath.concat(dName));
		if(!dirFile.exists()) {
            deleteAllFiles(dirFile);
        }
		boolean success = (dirFile).mkdirs();
	    if (success) {
	    	CPFPlugin.getDefault().info("createDir () : Directory Created : " + dName);
	    }
	    
	}
	
	public boolean deleteAllFiles(File dir){
		
		
		if(!dir.exists()) {
            return true;
        }
        boolean res = true;
        if(dir.isDirectory()) {
            File[] files = dir.listFiles();
            for(int i = 0; i < files.length; i++) {
                res &= deleteAllFiles(files[i]);
            }
                res = dir.delete();//Delete directory itself
        } else {
        	   	res = dir.delete();
        }
        return res;
	
	}
	
	/*
	 * Moves only .xhtml Files
	 * @srcDir Source Directory of Files
	 * @destDir Destination Directory where files need to be moved.
	 */
	
   public void copyXHTMLFiles(String srcDir,String destDir){
		
		CPFPlugin.getDefault().log("Inside copyXHTMLFiles() ");
		File srcDirectory = new File(srcDir); 
		boolean exists = srcDirectory.exists();
	    if (!exists) {
	      // It returns false if File or directory does not exist
	    	CPFPlugin.getDefault().error("copyXHTMLFiles() : The file or directory you are searching does not exist : " + srcDirectory);
	        
	    }else{
	      
	        	FilenameFilter filter = new FilenameFilter() { 
	    		public boolean accept(File srcDirectory, String name) { 
				return name.contains(".xhtml"); 
				} 
				}; 
				
				File[] children = srcDirectory.listFiles(filter); 
				for (int i=0; i<children.length; i++) {
			
				CPFPlugin.getDefault().info("File : " + children[i]);
								
					try{
					
					BufferedInputStream inStream=new BufferedInputStream(new FileInputStream(children[i])); 
					BufferedOutputStream outStream=new BufferedOutputStream(new FileOutputStream(destDir+children[i].getName())); 
					byte[] bytes=new byte[1024];
					int count;
						while( (count=inStream.read(bytes))!=-1 ){
								outStream.write(bytes,0,count); 
						}
					outStream.close();
					inStream.close(); 
					}catch(Exception e){	
						e.printStackTrace();
					}
				
					}			
				
			}
			CPFPlugin.getDefault().info("Exiting copyXHTMLFiles()");
	    	
	    }
		
	   
   public void copyDir(String srcDir,String destDir){
		
		File srcDirectory = new File(srcDir); 
		boolean exists = srcDirectory.exists();
	    if (!exists) {
	      // It returns false if File or directory does not exist
	    	CPFPlugin.getDefault().error("copyDir() : The file you are searching does not exist : " + srcDir);
	        
	    }else{
	      
			File[] children = srcDirectory.listFiles(); 

				for (int i=0; i<children.length; i++) {
			
				CPFPlugin.getDefault().info("File : " + children[i]);
				
				try{
					BufferedInputStream inStream=new BufferedInputStream(new FileInputStream(children[i])); 
					BufferedOutputStream outStream=new BufferedOutputStream(new FileOutputStream(destDir+children[i].getName())); 
					byte[] bytes=new byte[1024];
					int count;
					while( (count=inStream.read(bytes))!=-1 ){
					outStream.write(bytes,0,count); 
					}
					outStream.close();
					inStream.close(); 
					}catch(Exception e){	
						e.printStackTrace();
					}
				
					}
			CPFPlugin.getDefault().info("Exiting copyDir()..");
	    	
	    }
		
	}
   
   public void copyFile(String filePath,String destDir,String fileName){
	   
	   CPFPlugin.getDefault().log("Copying File : "+fileName);
	   File fileDir = new File(filePath);
	   boolean exists = fileDir.exists();
	    if (!exists) {
	      // It returns false if File or directory does not exist
	    	CPFPlugin.getDefault().error("copyFile() : The file or directory you are searching does not exist : " + filePath);
	    }else{ 
		   try{
				File destFile=new File(destDir+fileName);
			   	BufferedInputStream inStream=new BufferedInputStream(new FileInputStream(fileDir)); 
				BufferedOutputStream outStream=new BufferedOutputStream(new FileOutputStream(destFile)); 
				byte[] bytes=new byte[1024];
				int count;
				while( (count=inStream.read(bytes))!=-1 ){
				outStream.write(bytes,0,count); 
				}
				outStream.close();
				inStream.close(); 
				}catch(Exception e){
					CPFPlugin.getDefault().log("setting success = false in copyFile Exception");
					success = false;
					e.printStackTrace();
			}
	    }
   
   }
   
   
   private void updatePersistenceFile(File persistenceFile ,String userName)
		throws Exception {

			FileReader reader = null;
			boolean elementAlreadyExists = false;

			CPFPlugin.getDefault().info("Updating  persistence.xml file: " +  persistenceFile);

			try {

				reader = new FileReader( persistenceFile);

				DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory
				.newInstance();

				DocumentBuilder docBulider = docBuilderFac.newDocumentBuilder();

				Document doc = docBulider.parse(new InputSource(reader));

				// Node root = doc.getDocumentElement();

				Element root = (Element)doc.getElementsByTagName("persistence").item(0);

				CPFPlugin.getDefault().info("RootElement is : " + root.getNodeName().toString());

				// reeta added
				NodeList nodeList = root.getElementsByTagName("persistence-unit");
				if (nodeList != null) {
					
                Node unitNode = nodeList.item(0);
                NodeList children = unitNode.getChildNodes();
				for (int j = 0; j < children.getLength(); j++) {
					Node node = children.item(j);
					if (node != null) {
						String name = node.getNodeName();
						CPFPlugin.getDefault().log(
								"The Node found is... "
								+ node.getNodeName());
						if (name.equals("properties")) {
							
							NodeList propchildren =node.getChildNodes();
							
							for (int k = 0; k < propchildren.getLength(); k++) {
								Node property = propchildren.item(k);
								
								if (property != null) {
									NamedNodeMap attr = property.getAttributes();
									CPFPlugin.getDefault().info("Attribute list is......."+attr);
									if(attr!=null){
										for(int l=0;l<attr.getLength();l++){
											if(attr.item(l).getNodeName().equals("name")){
												CPFPlugin.getDefault().info("Attribute with name node name found.......");
												
												if(attr.item(l).getNodeValue().equals("hibernate.default_schema")){
													CPFPlugin.getDefault().info("Attribute with name hibernate.default_schema already exits");
													elementAlreadyExists=true;
												    attr.item(l).setNodeValue(userName);
												    break;
											}
											}
										}
								 }	
								 }
								if(elementAlreadyExists){
									break;
								}
							}
							
							if(!elementAlreadyExists) {
							 Element subChild = doc.createElement("property");
							 subChild.setAttribute("name","hibernate.default_schema");
							 subChild.setAttribute("value", userName);
							 node.appendChild(subChild);
							 node.appendChild(doc.createTextNode("\n"));
							}
							
							//properties node founfd so break
							break;
						}
					}
				}
				}
				
				Source source = new DOMSource(doc);

				Result result = new StreamResult(persistenceFile);

				Transformer xformer = TransformerFactory.newInstance()

				.newTransformer();

				xformer.transform(source, result);

				CPFPlugin.getDefault().info("Done updating persistence.xml file: " + persistenceFile);
			}catch (Exception e) {

				CPFPlugin.getDefault().error("Got exception dealing with file I/O and DOM", e);

				throw e;

			}

			finally {

				if (reader != null) {

					try {

						reader.close();

					} catch (IOException e) {

						CPFPlugin.getDefault().error("Got exception while closing reader ...");

					}

				}

			}
   }
   	
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		
	}

}


