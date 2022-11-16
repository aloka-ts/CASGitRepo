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
package com.baypackets.sas.ide.servicemanagement;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.BPSASServicesNature;
import com.baypackets.sas.ide.util.IdeUtils;

/**
 * This class builds the sar file
 * @author eclipse
 *
 */

public class BuildSARAction implements IWorkbenchWindowActionDelegate 
{


	private IWorkbenchWindow window;
	protected IVMInstall jre;
	private IProject contextProject = null;	
	private IProject[] contextProjectsSelected=null;
	private String ServiceName = null;
	private ArrayList listResourcesRoot = null;
		
	private String header = "";
	public void dispose() 
	{
	
	}

	public void init(IWorkbenchWindow window) 
	{
		
		this.window = window;
		header = "BuildSARAction";
		listResourcesRoot = new ArrayList();

	}

	public void run(IAction action) 
	{
		ISelection currentSelection = SasPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		
//		changed by reeta
//	    contextProject = IdeUtils.getProject(currentSelection);
		contextProjectsSelected=IdeUtils.getProject(currentSelection);
		
		if(contextProjectsSelected ==null)
		{
		  contextProjectsSelected= getWorkspace().getRoot().getProjects();
		}
		
		if(contextProjectsSelected ==null){
			String st[] = new String[]{"OK"}; 
			MessageDialog dia = new MessageDialog(window.getShell(), "Service Deployment", null, "Please select a Project ", MessageDialog.WARNING, st, 0);
		 
			dia.open();
			return;
		}
		
		   
			int len = contextProjectsSelected.length;
			SasPlugin.getDefault().log("The projects are ....."+contextProjectsSelected);
			if(len>0)
			{
				int indx = 0;
				for(indx=0;indx<len;indx++)
				{
					try {
						if(contextProjectsSelected[indx].isOpen()){
							ServiceName = contextProjectsSelected[indx].getName();
							
							
							if(contextProjectsSelected[indx].hasNature(BPSASServicesNature.NATURE_ID) 
									|| IdeUtils.isSASProject(contextProjectsSelected[indx]))
						  {
							contextProject = contextProjectsSelected[indx];
						
							buildSAR();
						  }else{
							  String[] buttontxt = new String[] { "OK" };

								MessageDialog messageBox = new MessageDialog(
										window.getShell(),
										"Service Building",
										null,
										ServiceName+" is not CAS Service !!!",
										MessageDialog.INFORMATION, buttontxt, 0);
								messageBox.open();
								return;
						  }
						}
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}
			
			 
			
			 
		
		

	}
//	changed by reeta
	public void selectionChanged(IAction action, ISelection currentSelection){
	}
	
	
	public static IWorkspace getWorkspace()
	{
		
		return ResourcesPlugin.getWorkspace();
	}
	
	public IProject[] getSelectedProject()
	{
		return contextProjectsSelected;
		
	}
	
	
	public void buildSAR() 
	{
		try
		{
			try
			{
				IProgressMonitor monitor = new NullProgressMonitor();
				contextProject.build(IncrementalProjectBuilder.FULL_BUILD, monitor);

			}
			catch(Exception e)
			{
				SasPlugin.getDefault().log(e.getMessage(), e);
			}
			
			IFile buildPropertiesFile = contextProject.getFile("build.properties");
			
			Properties buildProperties = new Properties();
			
			buildProperties.load(buildPropertiesFile.getContents());


			String webInfFileList = buildProperties.getProperty("WEB-INF/");
			
			String webInfLibFileList = buildProperties.getProperty("WEB-INF/lib");

            if(!validateAndCopyToWEBINF(webInfFileList,webInfLibFileList))
               return;
			
			String webInfClassesFileList = buildProperties.getProperty("WEB-INF/classes");
			
			if(!validateAndCopyToWEBINFClasses(webInfClassesFileList))
				return;
			
//			
//			String webInfLibFileList = buildProperties.getProperty("WEB-INF/lib");
//			
//			if(!validateAndCopyToWEBINFLib(webInfLibFileList))
//				return;
			
			//Reeta Added for SOA
			IFile soaFile = contextProject.getFile(new Path("WEB-INF").append("soa.xml"));
			
			
			//
			String rootFileList = buildProperties.getProperty(".");
			
			if(!validateAndCopyToRoot(rootFileList))
				return;
			
			
		
			
			IFolder rootfolder = contextProject.getFolder("WEB-INF");			
			
			String str1 = header+":buildSAR:Root Folder ==>"+rootfolder;
                	SasPlugin.getDefault().log(str1);

			jre= JavaRuntime.getDefaultVMInstall();		

			String str2 = header+":buildSAR:JRE ===>"+jre;
			SasPlugin.getDefault().log(str2);

			File jdkHome = jre.getInstallLocation();
			String jdkPath=jdkHome.getPath();
			IPath jdkPATH =new Path(jdkPath);


			//Building SAR File

			int lengthlistResourcesRoot = listResourcesRoot.size();

			String commands[] = new String[4+lengthlistResourcesRoot];

			
		
			// 17th April String commands[] = new String[4];
			commands[0]=jdkPATH.append("bin").append("jar").toString();
			commands[1]="cf";
			commands[2] =contextProject.getName()+".sar";
			commands[3] ="WEB-INF";

			//Added on 28th April

			String envp[] = new String[1];
			envp[0] = contextProject.getLocation().toOSString();
			File builtservice =null;
			try
			{
				IPath projectpath = contextProject.getLocation();
				builtservice = projectpath.toFile();
			}
			catch(Exception ex)
			{
				SasPlugin.getDefault().log(ex.getMessage(),ex);
			}
				

			int counter = 0;

			for(int i =0; i<listResourcesRoot.size();i++){
				String str = (String)listResourcesRoot.get(i);
				if(str==null)
					break;
				else{
					commands[4+i] = str;
					counter++;
				}
			}
			
			Process process = null;

			try{
				process = Runtime.getRuntime().exec(commands, null,builtservice);
				//InputStream stream = process.getInputStream();
			
				//process.waitFor();
			
				//stream.close();
//				reeta adding it for adding it to the deploy Service dialog list
				if(builtProjects.indexOf(contextProject.getName())==-1){
				 builtProjects.add(contextProject.getName());
				}else{
					builtProjects.remove(contextProject.getName());
					builtProjects.add(contextProject.getName());
				}
				MessageDialog.openInformation(window.getShell(),
					"Building the SAR file from the Project "+contextProject.getName(),
					ServiceName+".sar is built successfully");
			
			}catch(Exception e){

				if(process!=null)
					process.destroy();
				String str3 = header+":buildSAR:Exception in Making SAR"+e.toString();
                SasPlugin.getDefault().log(str3,e);
				SasPlugin.getDefault().log(e.getMessage(), e);
			}
		}catch(Exception e){
			String str3 = header+":buildSAR:Exception in Making SAR"+e.toString();
            SasPlugin.getDefault().log(str3,e);
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
	}
	
	private boolean validateAndCopyToWEBINFClasses(String WEBClassesList)
	{
		try
		{

			IFolder classesFolder = contextProject.getFolder(new Path("WEB-INF").append("classes"));

                        if(classesFolder.exists())
                                classesFolder.delete(true, null);

                        classesFolder.create(true, true, null);


			StringTokenizer st = new StringTokenizer(WEBClassesList,",");
			while(st.hasMoreTokens())
			{
				String file = st.nextToken();
				SasPlugin.getDefault().log("FILE === > "+file);
				
				boolean flag = true;				
				
				if(contextProject.getFile(file).exists())
				{
					flag = false;
					
					IFolder folder = contextProject.getFolder(new Path("WEB-INF").append("classes"));
					
					if(folder.exists())
						folder.delete(true, null);
					contextProject.getFile(file).copy(new Path("WEB-INF").append("classes"), true, null);
					
				}
				
				if((contextProject.getFolder(file).exists())&&(flag==true))
				{
					IFolder folder = contextProject.getFolder(new Path("WEB-INF").append("classes"));
					
					if(folder.exists())
						folder.delete(true, null);
					contextProject.getFolder(file).copy(new Path("WEB-INF").append("classes"), true, null);
				}
				
				
			}
			
		}
		catch(Exception e)
		{
			String str3 = header+":validateAndCopyToWEBINFClasses:"+e.toString();

                        SasPlugin.getDefault().log(str3,e);
			SasPlugin.getDefault().log(e.getMessage(), e);
			return false;
		}
		return true;
		
	}
	
//	private boolean validateAndCopyToWEBINFLib(String LibFileList)
//	{
//		try
//		{
//			
//			IFolder libFolder = contextProject.getFolder(new Path("WEB-INF").append("lib"));
//
//			if(libFolder.exists())
//				libFolder.delete(true, null);
//
//			libFolder.create(true, true, null);			
//
//			StringTokenizer st = new StringTokenizer(LibFileList,",");
//			while(st.hasMoreTokens())
//			{
//				String file = st.nextToken();
//				
//				IFile fileErlier = contextProject.getFile(new Path("WEB-INF").append("lib").append(file));
//				if(fileErlier.exists())
//					fileErlier.delete(true, null);
//				
//				IFolder folderErlier = contextProject.getFolder(new Path("WEB-INF").append("lib").append(file));
//				if(folderErlier.exists())
//					fileErlier.delete(true, null);
//				
//				boolean flag = true;
//				if(contextProject.getFile(file).exists())
//				{
//					flag = false;
//					
//					contextProject.getFile(file).copy(new Path("WEB-INF").append("lib").append(file), true, null);
//				}
//				
//				if((contextProject.getFolder(file).exists())&&(flag==true))
//				{
//					
//					contextProject.getFolder(file).copy(new Path("WEB-INF").append("lib").append(file), true, null);
//				}
//				
//				
//				
//			}
//			
//		}
//		catch(Exception e)
//		{
//			String str3 = header+":validateAndCopyToWEBINFLib:"+e.toString();
//
//                        SasPlugin.getDefault().log(str3,e);
//			SasPlugin.getDefault().log(e.getMessage(), e);
//			return false;
//		}
//		return true;
//		
//	}
	
	
	
	private boolean validateAndCopyToWEBINFLib(String LibFileList)
	{
		try
		{
			//for lib
			IFolder wsdlFolder = contextProject.getFolder(new Path("WEB-INF").append("lib"));

            if(wsdlFolder.exists()){
                   wsdlFolder.copy(contextProject.getFolder("lib").getFullPath(), true, null);
            }
        	IFolder wsdlFold = contextProject.getFolder("lib");
			
        	  
            //added for wsdl
            if(wsdlFold.exists())
            {
          	   wsdlFold.copy(contextProject.getFolder("WEB-INF").getFullPath().append("lib"), true, null);

            }
            wsdlFold.delete(true, null); 
            
            
            StringTokenizer st = new StringTokenizer(LibFileList,",");
			while(st.hasMoreTokens())
			{
				String file = st.nextToken();
				
				IFile fileErlier = contextProject.getFile(new Path("WEB-INF").append("lib").append(file));
				if(fileErlier.exists())
					fileErlier.delete(true, null);
				
				IFolder folderErlier = contextProject.getFolder(new Path("WEB-INF").append("lib").append(file));
				if(folderErlier.exists())
					fileErlier.delete(true, null);
				
				boolean flag = true;
				if(contextProject.getFile(file).exists())
				{
					flag = false;
					
					contextProject.getFile(file).copy(new Path("WEB-INF").append("lib").append(file), true, null);
				}
				
				if((contextProject.getFolder(file).exists())&&(flag==true))
				{
					
					contextProject.getFolder(file).copy(new Path("WEB-INF").append("lib").append(file), true, null);
				}
				
		}
		}
		catch(Exception e)
		{
			String str3 = header+":validateAndCopyToWEBINFLib:"+e.toString();

                        SasPlugin.getDefault().log(str3,e);
			SasPlugin.getDefault().log(e.getMessage(), e);
			return false;
		}
		return true;
		
	}
	
	
	private boolean validateAndCopyToWEBINF(String WEBFileList,String LibFileList)
	{
		try
		{
			IFolder folder = contextProject.getFolder("WEB-INF");

			if(folder.exists())
			{
				IFile sipXML = contextProject.getFile(new Path("WEB-INF").append("sip.xml"));
				IFile webXML = contextProject.getFile(new Path("WEB-INF").append("web.xml"));
				IFile sasXML = contextProject.getFile(new Path("WEB-INF").append("sas.xml"));
				IFile casXML = contextProject.getFile(new Path("WEB-INF").append("cas.xml"));
				IFile soaXML = contextProject.getFile(new Path("WEB-INF").append("soa.xml"));
				
				if(sipXML.exists())
				{
					sipXML.copy(contextProject.getFile("sip.xml.xml").getFullPath(), true, null);

				}

				if(webXML.exists())
				{
					webXML.copy(contextProject.getFile("web.xml.xml").getFullPath(), true, null);
				}

				if(sasXML.exists())
				{
					sasXML.copy(contextProject.getFile("sas.xml.xml").getFullPath(), true, null);
				}
				
				if(casXML.exists())
				{
					casXML.copy(contextProject.getFile("cas.xml.xml").getFullPath(), true, null);
				}
				
				if(soaXML.exists())
				{
					soaXML.copy(contextProject.getFile("soa.xml.xml").getFullPath(), true, null);
				}
				
				//for wsdl
				IFolder wsdlFolder = contextProject.getFolder(new Path("WEB-INF").append("wsdl"));

	            if(wsdlFolder.exists()){
	                   wsdlFolder.copy(contextProject.getFolder("wsdl").getFullPath(), true, null);
	            }
	            
	            //for alc 
	            IFolder xmlFolder = contextProject.getFolder(new Path("WEB-INF").append("xml"));
	           
	            if(xmlFolder.exists()){
	                   xmlFolder.copy(contextProject.getFolder("xml").getFullPath(), true, null);
	            }
	            
	            
	            
	          //for lib
				IFolder libFolder = contextProject.getFolder(new Path("WEB-INF").append("lib"));

	            if(libFolder.exists()){
	                   libFolder.copy(contextProject.getFolder("lib").getFullPath(), true, null);
	            }
	        	
				
				//
				folder.delete(true, null);
			}
			

			folder.create(true, true, null);

			IFile sipXML = contextProject.getFile("sip.xml.xml");
			IFile webXML = contextProject.getFile("web.xml.xml");
			IFile sasXML = contextProject.getFile("sas.xml.xml");
			IFile casXML = contextProject.getFile("cas.xml.xml");
			IFile soaXML = contextProject.getFile("soa.xml.xml");
			IFolder wsdlFold = contextProject.getFolder("wsdl");
			IFolder libFold = contextProject.getFolder("lib");
			IFolder xmlFold = contextProject.getFolder("xml");

			if(sipXML.exists())
                        {
                                   sipXML.copy(contextProject.getFolder("WEB-INF").getFullPath().append("sip.xml"), true, null);



                        }

				sipXML.delete(true,null);

                                if(webXML.exists())
                                {
                                        webXML.copy(contextProject.getFolder("WEB-INF").getFullPath().append("web.xml"), true, null);

                                }
				webXML.delete(true, null);

                                if(sasXML.exists())
                                {
                                        sasXML.copy(contextProject.getFolder("WEB-INF").getFullPath().append("sas.xml"), true, null);

                                }

				sasXML.delete(true, null); 
				
				 if(casXML.exists())
                 {
                         casXML.copy(contextProject.getFolder("WEB-INF").getFullPath().append("cas.xml"), true, null);

                 }

	             casXML.delete(true, null); 
	            
				
				  if(soaXML.exists())
                  {
                          soaXML.copy(contextProject.getFolder("WEB-INF").getFullPath().append("soa.xml"), true, null);

                  }
                  soaXML.delete(true, null); 
                  
                  
                  //added for wsdl
                  if(wsdlFold.exists())
                  {
                	   wsdlFold.copy(contextProject.getFolder("WEB-INF").getFullPath().append("wsdl"), true, null);

                  }
                  wsdlFold.delete(true, null); 
                  
                  //added for wsdl
                  if(libFold.exists())
                  {
                	   libFold.copy(contextProject.getFolder("WEB-INF").getFullPath().append("lib"), true, null);

                  }
                  libFold.delete(true, null); 
                  
                  
                  if(xmlFold.exists())
                  {
                	   xmlFold.copy(contextProject.getFolder("WEB-INF").getFullPath().append("xml"), true, null);

                  }
                  xmlFold.delete(true, null);   
                  

			
			StringTokenizer st = new StringTokenizer(WEBFileList,",");
			while(st.hasMoreTokens())
			{
				String file = st.nextToken();


				//  17th April IFile fileErlier = contextProject.getFile(new Path("WEB-INF").append(file));
				IFile fileErlier = contextProject.getFile(contextProject.getFolder("WEB-INF").getFullPath().append(file));
				if(fileErlier.exists())
					fileErlier.delete(true, null);
				
				//17th April IFolder folderErlier = contextProject.getFolder(new Path("WEB-INF").append(file));
				IFolder folderErlier = contextProject.getFolder(contextProject.getFolder("WEB-INF").getFullPath().append(file));
				if(folderErlier.exists())
					folderErlier.delete(true, null);
				
				
				if(contextProject.getFile(file).exists())
				{
					//17th April contextProject.getFile(file).copy(new Path("WEB-INF").append(file), true, null);
					contextProject.getFile(file).copy(contextProject.getFolder("WEB-INF").getFullPath().append(file), true, null);
				}
				
				if(contextProject.getFolder(file).exists())
				{
					//17th April contextProject.getFolder(file).copy(new Path("WEB-INF").append(file), true, null);
					contextProject.getFolder(file).copy(contextProject.getFolder("WEB-INF").getFullPath().append(file), true, null);
				}
				
				
			}
			
			
			  st = new StringTokenizer(LibFileList,",");
				while(st.hasMoreTokens())
				{
					String file = st.nextToken();
					
					IFile fileErlier = contextProject.getFile(new Path("WEB-INF").append("lib").append(file));
					if(fileErlier.exists())
						fileErlier.delete(true, null);
					
					IFolder folderErlier = contextProject.getFolder(new Path("WEB-INF").append("lib").append(file));
					if(folderErlier.exists())
						fileErlier.delete(true, null);
					
					boolean flag = true;
					if(contextProject.getFile(file).exists())
					{
						flag = false;
						
						contextProject.getFile(file).copy(new Path("WEB-INF").append("lib").append(file), true, null);
					}
					
					if((contextProject.getFolder(file).exists())&&(flag==true))
					{
						
						contextProject.getFolder(file).copy(new Path("WEB-INF").append("lib").append(file), true, null);
					}
					
			}
			
		}
		catch(Exception e)
		{
			String str3 = header+":validateAndCopyToWEBINF:"+e.toString();

                        SasPlugin.getDefault().log(str3,e);
			SasPlugin.getDefault().log(e.getMessage(), e);
			return false;
		}
		return true;
		
	}
	
	
	private boolean validateAndCopyToRoot(String RootFileList)
	{
		try
		{
			int i=0;
			StringTokenizer st = new StringTokenizer(RootFileList,",");
			while(st.hasMoreTokens())
			{
				String file = st.nextToken();

				String str3 = header+":validateAndCopyToRoot:FILE:"+file;

                                SasPlugin.getDefault().log(str3);
				listResourcesRoot.add(i,(String)file);

				i++;
				
			}
			
		}
		catch(Exception e)
		{
			String str3 = header+":validateAndCopyToRoot:"+e.toString();

                        SasPlugin.getDefault().log(str3,e);
			SasPlugin.getDefault().log(e.getMessage(), e);
			return false;
		}
		return true;
		
	}
	
	public static ArrayList getBuiltProjects(){
		return builtProjects;
	}

	private static ArrayList<String> builtProjects =new ArrayList<String>();
}
	
