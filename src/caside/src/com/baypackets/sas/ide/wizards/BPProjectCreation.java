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
package com.baypackets.sas.ide.wizards;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.ide.IDE;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.IdeUtils;

/**
	* This class creates the Project
*/

public class BPProjectCreation implements IRunnableWithProgress 
{

	private static final String DD_FIELD_NAME ="$NAME".intern();
	private static final String DD_FIELD_VERSION ="$VERSION".intern();
	private static final String DD_FIELD_PRIORITY ="$PRIORITY".intern();
	private static final String DD_FIELD_SBB ="<!--$SBB-->".intern();
	private static final String DD_FIELD_SOA_SERVICE="<!--$SERVICE-->".intern();
	private static final String DD_FIELD_SOA_APPLICATION="<!--$APPLICATION-->".intern();
	private static final String DD_FIELD_DIAM_RA="<!--$DIAMETER_RA-->";
	private static final String DD_FIELD_HTTP_RA="<!--$HTTP_RA-->";

	
	private static final String CAS_DD_NAME = "resources/descriptors/cas.xml";
	private static final String SIP_116_DD_NAME = "resources/descriptors/sip.xml";
	private static final String BUILD_XML = "conf/build.xml";
	private static final String SIP_289_DD_NAME = "resources/descriptors/sip_1.1.xml";
	private static final String WEB_DD_NAME = "resources/descriptors/web.xml";
	private static final String SOA_DD_NAME = "resources/descriptors/soa.xml";
	private static final String TEMPLATE_ALC=  "resources/alc/template.alcml";
	
	private static final String DIAMETER_RA_DESC=  "<resource-factory-mapping>"+"\r\n"+
	                                     "<factory-name>RoFactory</factory-name>"+"\r\n"+
			                              "<resource-name>ro-ra</resource-name>"+"\r\n"+
                                          "</resource-factory-mapping>"+"\r\n"+
			                              
			                              "<resource-factory-mapping>"+"\r\n"+
                                           "<factory-name>RfFactory</factory-name>"+"\r\n"+
                                           "<resource-name>rf-ra</resource-name>"+"\r\n"+
                                          "</resource-factory-mapping>"+"\r\n"+
                                           
											 "<resource-factory-mapping>"+"\r\n"+
										     "<factory-name>ShFactory</factory-name>"+"\r\n"+
										     "<resource-name>sh-ra</resource-name>"+"\r\n"+
										    "</resource-factory-mapping>"+"\r\n"+
										     
											 "<resource-factory-mapping>"+"\r\n"+
										     "<factory-name>GyFactory</factory-name>"+"\r\n"+
										     "<resource-name>gy-ra</resource-name>"+"\r\n"+
										    "</resource-factory-mapping>"+"\r\n";
	
	private static final String HTTP_RA_DESC=  "<resource-factory-mapping>"+"\r\n"+
                                             "<factory-name>HttpFactory</factory-name>"+"\r\n"+
                                                 "<resource-name>http-ra</resource-name>"+"\r\n"+
                                              "</resource-factory-mapping>"+"\r\n";
										     
											
//	private static final String BUILD_ALC_APP_XML = "resources/alc/build.xml";
	
	private static final String[] DD_FIELDS = {DD_FIELD_NAME, DD_FIELD_VERSION, DD_FIELD_PRIORITY, DD_FIELD_SBB,DD_FIELD_SOA_SERVICE,DD_FIELD_SOA_APPLICATION,DD_FIELD_DIAM_RA,DD_FIELD_HTTP_RA};
	
	private BPProjectWizard wizard = null;
	
	public BPProjectCreation(BPProjectWizard wizard) 
	{
		super();
		this.wizard = wizard;
	}

	@SuppressWarnings("deprecation")
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException 
	{
		if (monitor == null)
		{
			monitor= new NullProgressMonitor();
		}
		try 
		{
			monitor.beginTask("Creating the Project .....", 1); 
			
			//Run the Java Project Creation.
			IRunnableWithProgress runnable = wizard.getJavaConfigPage().getRunnable();
			runnable.run(monitor);
			
			BPProjectTypePage appPage = wizard.getApplicationTypePage(); 
			
			//REETA added for alc
			
			if(appPage.isAlcService()){
			 wizard.getJavaConfigPage().addAlcNature(this.wizard.getFirstPage().getProjectHandle());
			}
			
			
			if(appPage.isAlcExtension()){
				wizard.getJavaConfigPage().addAlcExtensionNature(this.wizard.getFirstPage().getProjectHandle());
				return;
			}
			
			//Reeta added SAS Services nature for debugging and deploying
			if(!appPage.isAlcExtension()){
				wizard.getJavaConfigPage().addSASServicesNature(this.wizard.getFirstPage().getProjectHandle());
			}
			
			if(appPage.isSip289Application()){
				wizard.getJavaConfigPage().addJSR289ServicesNature(this.wizard.getFirstPage().getProjectHandle());
			}
			
			if(appPage.isSoaApplication()||appPage.isSoaService()){
				wizard.getJavaConfigPage().addSASSOAServicesNature(this.wizard.getFirstPage().getProjectHandle());
			}
			
		
			//**************** Neeraj Code
			
			
			
			
			String projectName = this.wizard.getFirstPage().getProjectName();
			
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			
			IFolder webInfFolder = project.getFolder("WEB-INF");
			
			if(!webInfFolder.exists())
				
				webInfFolder.create(true, true, monitor);
			
			IFolder classesFolder = project.getFolder(new Path("WEB-INF").append("classes"));
			
			if(!classesFolder.exists())
				classesFolder.create(true, true, monitor);
			
			IFolder libFolder = project.getFolder(new Path("WEB-INF").append("lib"));
			
			if(!libFolder.exists())
				libFolder.create(true, true, monitor);
			
			
			//added for SOA REETA
			if(appPage.isSoaService()||appPage.isSoaApplication()){
            
			IFolder wsdlFolder = project.getFolder(new Path("WEB-INF").append("wsdl"));
			if(!wsdlFolder.exists())
				wsdlFolder.create(true, true, monitor);
			
			}
			
			if(appPage.isAlcService()){
	            
				IFolder xmlFolder = project.getFolder(new Path("WEB-INF").append("xml"));
				if(!xmlFolder.exists())
					xmlFolder.create(true, true, monitor);
				
				if(xmlFolder.exists()){
					IFile file=xmlFolder.getFile("template.alcml");
					if(!file.exists()){
						 URL loc= Platform.resolve(SasPlugin.getDefault().getBundle().getEntry(TEMPLATE_ALC));
						 String path=loc.getPath();
		         		 InputStream stream = new FileInputStream(path);
		         		 file.create(stream, true, monitor);
		         		 SasPlugin.getDefault().log("Created template alc file");
					}
				
				}
				
//				IFile buildAlcAppfile = project.getFile("build.xml");
//				
//				if(!buildAlcAppfile.exists()){
//					URL loc= Platform.resolve(SasPlugin.getDefault().getBundle().getEntry(this.BUILD_ALC_APP_XML));
//				    String path=loc.getPath();
//        		    InputStream stream = new FileInputStream(path);
//        		    buildAlcAppfile.create(stream, true, monitor);
//        		    SasPlugin.getDefault().log("Created build.xml for alc project");
//				}
			
			
		}
			
			
			
			IFile buildPropertiesFile = project.getFile("build.properties");

			// Added on 23rd April by Neeraj
			try
			{
				ByteArrayOutputStream buildProp = new ByteArrayOutputStream();
                                Writer writerbProp = new OutputStreamWriter(buildProp);

                                writerbProp.write("");
                                writerbProp.close();
                                buildProp.close();
                                ByteArrayInputStream sourcebp = new ByteArrayInputStream(buildProp.toByteArray());
                                if(buildPropertiesFile.exists())
                                {
 	                               buildPropertiesFile.setContents(sourcebp,true,true,null);
                                }
                                else
                                       buildPropertiesFile.create(sourcebp, true, null);
			}
			catch(Exception e)
			{
				SasPlugin.getDefault().log(e.getMessage(), e);
			}


			//    Addition Finishesd




			
			//buildPropertiesFile.create(null, true, null); REETA added
			
			Properties buildProperties = new Properties();
			
			try
			{
				OutputStream outstream = new FileOutputStream(buildPropertiesFile.getRawLocation().toOSString());
				
				StringBuffer sourceFolders  = new StringBuffer();
				
				IClasspathEntry[] cps = this.wizard.getJavaConfigPage().getRawClassPath();
				//IFolder webInfFolder = null;
				for(int i=0; cps != null && i < cps.length ; i++)
				{
					if(cps[i] != null && cps[i].getEntryKind() == IClasspathEntry.CPE_SOURCE)
					{
						SasPlugin.getDefault().log("SOURCE FOLDER NAME ::::" + cps[i].getPath());
						
						String srcFolderPath = cps[i].getPath().segment(1);
						sourceFolders.append(srcFolderPath +",");
						
					}
				}
					
				int length = sourceFolders.length();
				String sourceFolderNames = sourceFolders.substring(0,length-1);
				
				//Taking 1 segment assuming that the out put folder will be /project/bin then 
				// out put folder will be 'bin'

				String outPutFolder = this.wizard.getJavaConfigPage().getOutputLocation().segment(1);
				buildProperties.setProperty("source",sourceFolderNames);
				
				
				buildProperties.setProperty("output",outPutFolder);
				
				buildProperties.store(outstream, "Project Specific Build Properties");
				buildProperties.clear();
				buildProperties.setProperty("WEB-INF/classes", outPutFolder);
				buildProperties.setProperty("WEB-INF/lib","");
				
				
				if(appPage.isSoaService()||appPage.isSoaApplication()){
				   buildProperties.setProperty("WEB-INF/wsdl","");
				}
				
				
				IPath pluginPath=SasPlugin.getBundlePath();
				buildProperties.setProperty("WEB-INF/", "");
				buildProperties.setProperty(".","");
				buildProperties.setProperty("CASIDE_PLUGIN_HOME",pluginPath.toOSString());
				buildProperties.setProperty("INSTALLROOT","build");
				buildProperties.setProperty("PROJECT_NAME",projectName);
				buildProperties.store(outstream, "SAR File Specific Build Properties");
				//openFile(buildPropertiesFile);	//NJADAUN 23rd April
				
				SasPlugin.getDefault().log("create build.xml with :" +pluginPath.toOSString());
				
				try
				{
					SasPlugin.getDefault().log("create build.xml :"  ); 
					StringBuffer buffer = this.getContents(BUILD_XML);
					this.processContents(buffer);
					IFile descriptor = project.getFile("build.xml");
					if(!descriptor.exists())
					{
						descriptor.create(new ByteArrayInputStream(buffer.toString().getBytes()), true, monitor);
					}
				}
				catch(Exception e)
				{
					SasPlugin.getDefault().log(e.getMessage(), e);
				}
					
				
			}
			catch(Exception e)
			{
				SasPlugin.getDefault().log(e.getMessage(), e);
			}
			
			SasPlugin.getDefault().log("Creating the sas.xml file... ");
			this.createDescriptor(CAS_DD_NAME, webInfFolder, "cas.xml", monitor);
			
			if(appPage.isSip116Application())
			{
				SasPlugin.getDefault().log("This is a SIP 116 Application. So creating a sip.xml file... ");
				this.createDescriptor(SIP_116_DD_NAME, webInfFolder, "sip.xml", monitor);
			}
			
			if(appPage.isSip289Application())
			{
				SasPlugin.getDefault().log("This is a SIP 289Application. So creating a sip.xml file... ");
				this.createDescriptor(SIP_289_DD_NAME, webInfFolder, "sip.xml", monitor);
			}
			
			if(appPage.isHttpApplication())	 //In place of appPage.isSipApplication())  by NJADAUN
			{
				SasPlugin.getDefault().log("This is a HTTP Application. So creating a web.xml file... ");
				this.createDescriptor(WEB_DD_NAME, webInfFolder, "web.xml", monitor);
			}
			
			if(appPage.isSoaService()||appPage.isSoaApplication())	 //In place of appPage.isSoaApplication())  by REETA
			{
				SasPlugin.getDefault().log("This is a HTTP Application. So creating a web.xml file... ");
				this.createDescriptor(SOA_DD_NAME, webInfFolder, "soa.xml", monitor);
			}
			
			
//			BPNewProjectCreationCVS firstPage=(BPNewProjectCreationCVS)this.wizard.getFirstPage();
//			if (firstPage.isUseGitRepo()&& firstPage.getSelectedRepoLocation()!=null) {
//					
//				SasPlugin.getDefault().log("Application is using Git ");
//				
//				Repository repository = new FileRepository(
//						firstPage.getSelectedRepoLocation()+ "/.git");
				//repository.create(arg0);
				
		
//				repositoryBuilder.setMustExist(false);
//			    Repository repository  = null;
//			
//			if(!firstPage.getUseExistingRepo()){	
//				
//				SasPlugin.getDefault().log("Create Repo  "+repository.getDirectory().getCanonicalPath());
//				repository.create(false);
//					SasPlugin.getDefault().log("donot Use Existing Repo and create a new " +firstPage.getSelectedRepoLocation());	
//					repositoryBuilder.set
//					repository=repositoryBuilder.create(new File(
//							firstPage.getSelectedRepoLocation()));
//				}else{			
//					repositoryBuilder =repositoryBuilder.findGitDir(new File(
//							firstPage.getSelectedRepoLocation()));
//					repository =repositoryBuilder.build();
//				}
	//		}
				
//				 SasPlugin.getDefault().log("Add  " +projectName +" to Repo "+repository.getDirectory());
//				 Git git = new Git(repository);	
//				
//				 IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(firstPage.getSelectedRepoLocation()+"/"+projectName));		
//				 
//				 IFileStore fileStoreWS = EFS.getLocalFileSystem().getStore(project.getLocation());
//				 
//				 SasPlugin.getDefault().log("Copy project from " +fileStoreWS +" to Repo "+fileStore);
//				 
//				 fileStoreWS.copy(fileStore, 0, null);
				 
			 //    git.add().addFilepattern("/"+projectName).call();
			     
//				 SasPlugin.getDefault().log("Create Git Project " +firstPage.getSelectedRepoLocation() + "/" + projectName);
				 
		//	     File projectFolder=new File(firstPage.getSelectedRepoLocation() + "/" + projectName);
			     
			//      FileRepositoryBuilder builder=new FileRepositoryBuilder();
			 //     repository=builder.setGitDir(projectFolder).readEnvironment().findGitDir().build();
			      
			//     if (repository.isBare()) {
			 //      SasPlugin.getDefault().log("Repo is bare");
			//       repository.create();
			 //      Git.open(projectFolder);
//			      AddCommand addC =git.add();
//			      SasPlugin.getDefault().log("Calling Add-command on git "+ git.getRepository().getDirectory() +" for "+projectName);
//			      addC.addFilepattern(projectName).call();
//			      CommitCommand commit = git.commit();
//			      commit.setMessage("initial commit").call();
			      
//			      RepositoryProvider.map(project, GitProvider.class.getName());
//		       //   autoIgnoreDerivedResources(project, monitor);
//		          project.refreshLocal(2, 
//		            new SubProgressMonitor(monitor, 50));
//		          
//		          IHandlerService handlerService = (IHandlerService)this.wizard.getSelection().getService(IHandlerService.class);
//		          try {
//		            handlerService.executeCommand("add.command", null);
//		            } catch (Exception ex) {
//		              throw new RuntimeException("add.command not found");
//		              // Give message
//		              }
//		          } 
		          
//			// Obtain IServiceLocator implementer, e.g. from PlatformUI.getWorkbench():
//				IServiceLocator serviceLocator = PlatformUI.getWorkbench();
//				// or a site from within a editor or view:
//				// IServiceLocator serviceLocator = getSite();
//
//				ICommandService commandService = (ICommandService) serviceLocator.getService(ICommandService.class);
//
//				try  { 
//				    // Lookup commmand with its ID
//				    Command command = commandService.getCommand("org.eclipse.egit.ui.command.shareProject");
//				  
//				    Map params=new HashMap();
//				    params.put("org.eclipse.egit.ui.command.projectNameParameter", projectName);
//				    
//				    @SuppressWarnings("deprecation")
//					ExecutionEvent EE=new ExecutionEvent(params, null, null);
//				    
//				    // Optionally pass a ExecutionEvent instance, default no-param arg creates blank event
//				    command.executeWithChecks(EE);
//				}catch(Exception e){
//					
//				}
				        
				    
//			      try {
//		               new ConnectProviderOperation(project, repository.getDirectory()).execute(monitor);
//		              } catch (CoreException e) {
//		                throw new InvocationTargetException(e);
//		              }
			   
//			      SubmoduleAddCommand sac= git.submoduleAdd(); 
//			      sac.setURI(fileStoreWS.toString());
//			      sac.setPath(File.separatorChar + projectName);
//			      repository = sac.call();
			    //  repository.close();//.init().setDirectory(projectFolder).call();
			 //    }
				
//			}else{
//				SasPlugin.getDefault().log("Application not using Git ");
//			}
//			
			
			
		}
		
	
		catch(Exception e)
		{
			SasPlugin.getDefault().log(e.getMessage(), e);
		} 
		finally 
		{
			monitor.done();
		}
	}
	
	protected void createDescriptor(String resName, IFolder directory, String fileName, IProgressMonitor monitor)
	{
		try
		{
			SasPlugin.getDefault().log("createDescriptor :" + resName); 
			StringBuffer buffer = this.getContents(resName);
			this.processContents(buffer);
			IFile descriptor = directory.getFile(fileName);
			if(!descriptor.exists())
			{
				descriptor.create(new ByteArrayInputStream(buffer.toString().getBytes()), true, monitor);
			}
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
	}
	
	protected StringBuffer getContents(String name) throws Exception
	{
		StringBuffer buffer = new StringBuffer();
		String path = SasPlugin.fullPath(name);
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

	public void processContents(StringBuffer buffer)
	{
		
		if(buffer == null)
			return;
		
		BPProjectTypePage appPage = wizard.getApplicationTypePage();
		String[] ddFieldValues = new String[DD_FIELDS.length];
		for(int i=0; i<DD_FIELDS.length;i++)
		{
			if(DD_FIELDS[i].equals(DD_FIELD_NAME)){
				ddFieldValues[i] = appPage.getAppName();
			}else if (DD_FIELDS[i].equals(DD_FIELD_VERSION)){
				ddFieldValues[i] = appPage.getAppVersion();
			}else if (DD_FIELDS[i].equals(DD_FIELD_PRIORITY)){
				ddFieldValues[i] = ""+appPage.getAppPriority();
			}else if (DD_FIELDS[i].equals(DD_FIELD_SBB)){
				ddFieldValues[i] = appPage.isUseSbb() ? "<sbb/>" : "";
			}else if (DD_FIELDS[i].equals(this.DD_FIELD_SOA_SERVICE)){
				ddFieldValues[i] = appPage.isSoaService() ? "<service>"+"\r\n"+"    <service-name>"+appPage.getAppName()+"</service-name>"+"\r\n"+"    </service>" : "";
			}else if (DD_FIELDS[i].equals(this.DD_FIELD_SOA_APPLICATION)){
					ddFieldValues[i] = appPage.isSoaApplication() ? "<application>"+"\r\n"+"    <app-name>"+appPage.getAppName()+"</app-name>"+"\r\n"+"    </application>" : "";	
			}
//			else if (DD_FIELDS[i].equals(DD_FIELD_DIAM_RA)){
//					ddFieldValues[i] = appPage.isDiameterRAApplication() ? DIAMETER_RA_DESC : "";
//			}
			else if (DD_FIELDS[i].equals(DD_FIELD_HTTP_RA)){
					ddFieldValues[i] = appPage.isHttpRAApplication() ? HTTP_RA_DESC : "";
			}else
				 ddFieldValues[i] = "";
		}

		
		for(int i=0; i<DD_FIELDS.length;i++)
		{
			 SasPlugin.getDefault().log("Replacing DD_FIELDS[i] with  "+ ddFieldValues[i]);
			buffer = IdeUtils.replace(buffer, DD_FIELDS[i], ddFieldValues[i], 0, true);
		}
	}

	public void openFile(IFile file)
	{
		try
		{
			Display display = wizard.getShell().getDisplay();
		        final IFile ffile = file;
		        if(display != null)
	 	           display.asyncExec(new Runnable() {

		                public void run()
		                {
		                    try
		                    {
		                        IWorkbenchWindow activeWindow = SasPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		                        IWorkbenchPage activePage = activeWindow.getActivePage();
		                        if(activePage != null)
		                            IDE.openEditor(activePage, ffile, true);
		                    }
		                    catch(Throwable e)
		                    {
		                        SasPlugin.getDefault().log("Exception is thrown by openFile bpProjectCreation.java "+e);
		                    }
		                }

		            });

				
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
	}	
	
	
}

