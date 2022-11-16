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
package com.baypackets.sas.ide.samples;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.ide.IDE;

import com.baypackets.sas.ide.SasPlugin;

public class BPCreateB2bUAApplication {
	
	private String projectName = null;
	private Shell shell = null;
	private IProject project = null;
	private BPSampleApps sampleApps =null;
	public BPCreateB2bUAApplication(String projectName, Shell shell)
	{
		this.projectName = projectName;
		this.shell = shell;
		sampleApps = BPSampleApps.getInstance();
	}
	
	
	public boolean create()
	{
		IJavaProject myProject = null;		
		try
		{
			this.project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			myProject = JavaCore.create(project);
			sampleApps.addSASServicesNature(this.project);
			SasPlugin.getDefault().log("JavaProject====>"+myProject);
			myProject.open(null);			
			IJavaModel model = myProject.getJavaModel();
			model.open(null);			
			IPath projectLocation = project.getLocation();			
			SasPlugin.getDefault().log("Project Location===>"+projectLocation);			
			String sourcePath = projectLocation.append("java").toString();			
			SasPlugin.getDefault().log("SourcePath====>"+sourcePath);			
			IPath srcPath = new Path(sourcePath);			
			SasPlugin.getDefault().log("SourcePath====>"+srcPath);			
			IJavaElement element = myProject.findPackageFragmentRoot(srcPath);	
			SasPlugin.getDefault().log("Package fragment root ====>"+element);
			
			//Creating com.baypackets.clicktodial.servlets;
			StringTokenizer tokenizer = new StringTokenizer(sampleApps.getB2bUAPackage(),".");			
			int index =0;		
			IPath pathSource = new Path("java");			
			IPath sbbListenerSource = new Path("java");			
			SasPlugin.getDefault().log("PATHSOURCE====>"+pathSource);			
			Hashtable table =new Hashtable();
			
			while(tokenizer.hasMoreTokens())
			{
				String packag = tokenizer.nextToken();				
				SasPlugin.getDefault().log("Package===>"+packag);
				String strkey = ""+index;
				table.put(strkey,packag);
				index++;								
			}
			
			SasPlugin.getDefault().log("The PACKAGE name is "+table);
			int noOfTokens = table.size();
			SasPlugin.getDefault().log("SIZE OF PACKAGE ARRAY =====>"+noOfTokens);
			for(int i=0;i<noOfTokens;i++)
			{
				String key1 =""+i;					
				SasPlugin.getDefault().log("KEY1===>"+key1);								
				String packagepath1 = (String)table.get(key1);									
				pathSource = pathSource.append(packagepath1);
				sbbListenerSource = sbbListenerSource.append(packagepath1);
				String pack = pathSource.toString();
				IFolder folder = project.getFolder(pack);
				if(folder.exists())
		        {
		        	SasPlugin.getDefault().log("Folder "+folder +"exists");
		        }
		        else
		        {
		        	SasPlugin.getDefault().log("Creating the "+folder);
		        	folder.create(true,true,null);
		        }
		     	
			}
				
		
			
			createServlets(pathSource);

			 try
                        {

                                IFile buildProperties =  project.getFile("build.properties");
                                openFile(buildProperties);
                        }
                        catch(Exception e)
                        {
                        }

			
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
		
		public void openFile(IFile file)
		{
			try
			{
				
				Display display = shell.getDisplay();
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
		                        SasPlugin.getDefault().log("Exception thrown by openFile() of BpCreateB2bUAApplication.java"+ e);
		                    }
		                }

		            });

				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		public void createServlets(IPath pathSource)
		{
			try
			{
				String className = sampleApps.getB2bUAServletName();
				
					
					String sourceCode = pathSource.append(className).toString();
					SasPlugin.getDefault().log("The Class NAMEEEE =====>"+className);
					IFile fileSource = null;
					fileSource = project.getFile(sourceCode+".java");
					ByteArrayOutputStream baosSipXML = new ByteArrayOutputStream();
					Writer writerSourceXML = new OutputStreamWriter(baosSipXML);
	    	 
					writerSourceXML.write("");
					writerSourceXML.close();
					baosSipXML.close();
					ByteArrayInputStream sourceproject = new ByteArrayInputStream(baosSipXML.toByteArray());
					if(fileSource.exists())
					{
						fileSource.setContents(sourceproject,true,true,null);
					}
					else
						fileSource.create(sourceproject, true, null);
					
					createCode(fileSource, className);
					openFile(fileSource);
					
			
				
			
			
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		
		
		private void createCode(IFile fileSource, String className)
		{
			try
			  {
				  ByteArrayOutputStream baosSipXML = new ByteArrayOutputStream();
				  Writer writerSipXML = new OutputStreamWriter(baosSipXML);

				  String b2bsbbfile = SasPlugin.fullPath("resources");
			  
				  String filepath = new Path(b2bsbbfile).append("sampleapps").append("b2bua").append(className+".java").toString();
			  
				  FileReader fr = new FileReader(filepath);
				  BufferedReader br = new BufferedReader(fr);
				  StringBuffer buffer = new StringBuffer();
				  String string ="";
				  while((string = br.readLine())!=null)
				  {
					  buffer.append("\n"+string);
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

				  if(fileSource.exists())
				  {
	              	fileSource.setContents(sourceproject,true,true,null);
				  }
				  else
	              	fileSource.create(sourceproject, true, null);
				  
				  IJavaElement javaElement = JavaCore.create(fileSource);
					
				  SasPlugin.getDefault().log("JAVAELEMNT=====>"+javaElement);
					
				  ICompilationUnit unit = (ICompilationUnit)javaElement;
						
				  unit.open(null);

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
			

}
