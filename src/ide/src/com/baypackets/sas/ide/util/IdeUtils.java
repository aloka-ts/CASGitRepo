package com.baypackets.sas.ide.util;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.servicemanagement.BuildSARAction;

public class IdeUtils {

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
	
	public static void getClassNames(IProject project, SearchPattern pattern, List results){
		if(project == null)
			return;
		IJavaProject jProject = null;
		try{
			//get the java project associated with this project....
			jProject = JavaCore.create(project);
		
			//Create a Search Engine object....
			SearchEngine searchEngine = new SearchEngine();
			
			//Create a search requestor instance....
			SearchRequestor requestor = new SimpleRequestor(results);
			
			//Create the search scope and search participants....
			IJavaSearchScope scope = SearchEngine.createJavaSearchScope(jProject.getPackageFragmentRoots());
			SearchParticipant[] participants = new SearchParticipant[]{SearchEngine.getDefaultSearchParticipant()};
			
			//Now do the search....
			searchEngine.search(pattern, participants, scope, requestor, null);
		}catch(Exception e){
			SasPlugin.getDefault().log("Exception thrown getClassNames() IdeUtils.java..."+e);
		}finally {
			try{
				if(jProject != null)
					jProject.close();
			}catch(JavaModelException jme){
				SasPlugin.getDefault().log("Exception thrown getClassNames() while closing project IdeUtils.java..."+jme);
			}
		}
	}
	
	//	Create a Search Requestor implementation.
	public static class SimpleRequestor extends SearchRequestor{

		private List results;
		
		public SimpleRequestor(List results) {
			super();
			this.results = results;
		}

		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			SasPlugin.getDefault().log("Accept serach match ideutils...."+match);
			if(match != null && match.getElement() instanceof IType){
				IType type = (IType)match.getElement();
				String name = type.getFullyQualifiedName();
				if(results != null && !results.contains(name)){
					results.add(name);
				}
			}
			
			if(match != null && match.getElement() instanceof IMethod){
				IMethod method = (IMethod)match.getElement();
				IType type = method.getDeclaringType();
				if(type != null){
					String name = type.getFullyQualifiedName();
					if(results != null && !results.contains(name)){
						results.add(name);
					}
				}
			}
		}
		
	};
	
	//changed by reeta
	public static IProject[] getProject(ISelection currentSelection){
		IProject contextProject = null;
		 IProject projectSelected[]=null;
		
		if(currentSelection instanceof IStructuredSelection && !currentSelection.isEmpty())
		{
			IStructuredSelection structuredSelection = (IStructuredSelection)currentSelection;
			int NoOfSlections=structuredSelection.size();
		  
		    Object projectsArray[]=structuredSelection.toArray();
		    IResource projectsObtained[]=new IResource[NoOfSlections];
		    projectSelected=new IProject[NoOfSlections];
		    
		   for(int i=0;i<NoOfSlections;i++){
			  Object selectedElement=projectsArray[i];
			  
			  if(selectedElement instanceof IProject)
			    {
			    	IProject prj = (IProject)selectedElement;
			        contextProject = prj;
			    }
			            
			    if(selectedElement instanceof IJavaProject)
			    {
			    	IProject prj = ((IJavaProject)selectedElement).getProject();
			        contextProject = prj;
			    }
			     
			    if(selectedElement instanceof IPackageFragment)
			    try
			    {
			    	IPackageFragment pf = (IPackageFragment)selectedElement;
			        contextProject = pf.getUnderlyingResource().getProject();
			    }
			    
			    catch(JavaModelException _ex) 
			    { }
			    if(selectedElement instanceof ICompilationUnit)
			    try
			    { 
			    	ICompilationUnit cu = (ICompilationUnit)selectedElement;
			        contextProject = cu.getUnderlyingResource().getProject();
			    }		            
			    catch(JavaModelException _ex) 
			    { }
			     
			    if(selectedElement instanceof IFile)
			    {
			    	IFile file = (IFile)selectedElement;
			        contextProject = file.getProject();
			    }
			    
			    if(selectedElement instanceof IFolder)
			    {
			    	IFolder folder = (IFolder)selectedElement;
			        contextProject = folder.getProject();
			    }
		    	
			     projectSelected[i]=contextProject;
		    }
		  
		    
	    }
		
		return projectSelected;
	}
	
	
	
	   
	   public static ArrayList<String> getInterfaces(IProject project) throws ClassNotFoundException{
		   
		   File directory=null;
		   ArrayList<String> result = new ArrayList<String>();		
		   bin=Platform.getLocation().toOSString()+project.getFolder("bin").getFullPath().toOSString();
		   if(isWindows()){
			   bin=bin+"\\";
		   }else{
			   bin=bin+"/";  
		   }
		   SasPlugin.getDefault().log("The bin path found is!!! " + bin);
		   
		  //get the bin directory 
		   try {
			    directory=new File(bin);
			   } catch(NullPointerException x) {
			     throw new ClassNotFoundException(bin+" does not exists");
			}
			
		try{		
				if(directory.exists()) {
					 //get the loader
					 URLClassLoader loader = new URLClassLoader(
								new URL[] { new URL("file:///"
										+ bin) },Thread.currentThread()
										.getContextClassLoader());
					 SasPlugin.getDefault().log("Loader is.... " + loader);   
					 
					// Get the list of the files contained in the bin
					 File[] files=directory.listFiles();
					 SasPlugin.getDefault().log("The No. of Files/directories found in bin are " + files.length); 
					 
					 for(int i=0; i<files.length; i++) {
						 addInterfcaes(files[i],result,loader) ;
					 }
					
				} else {
					SasPlugin.getDefault().log("bin Directory does not exist!!!!!!!");
				}	
			
		}catch(Exception e){
			SasPlugin.getDefault().log("IdeUtil The excption is thrown by getInterfaces() "+e.toString());
		}
				
				return result;
	   }
	   
	   
	   
	    private static void addInterfcaes(File directory, ArrayList result,
			URLClassLoader loader) {
		
		try {
			String pacakName = null;
			String pacakge="";
			String fileName ="";
			SasPlugin.getDefault().log("addInterfaces listfiles is..."+directory.listFiles());
			if(directory.listFiles()==null){
				 pacakName = directory.getPath();
				  fileName = directory.getName();
				if (fileName.endsWith(".class")) {
					//find the pacakage for windows /linux					
					 if(pacakName.equals(bin.concat(fileName))){
						 pacakge=""; 
					}else{
						if(isWindows()){
							String subpack = pacakName.substring(pacakName
									.indexOf("bin\\") + 4, pacakName.length());
							String pk = subpack.substring(0, subpack.lastIndexOf("\\"));
							pacakge = pk.replace("\\", ".");
						}else{
							String subpack = pacakName.substring(pacakName
									.indexOf("bin/") + 4, pacakName.length());
							String pk = subpack.substring(0, subpack.lastIndexOf("/"));
							pacakge = pk.replace("/", ".");
						}
					}
//					SasPlugin.getDefault().log("The Pacakage is" + pacakge);
					String loadClass = "";
					// removes the .class extension
					if (!pacakge.equals("")) {
						loadClass = pacakge
								+ '.'
								+ fileName
										.substring(0, (fileName.length() - 6));
					} else {
						loadClass = fileName.substring(0,
								(fileName.length() - 6));
					}

					SasPlugin.getDefault().log(
							"loading class with name.... " + loadClass);
					Class cl = loader.loadClass(loadClass);

					//Add if this class is an interface
					if (cl.isInterface()) {
						SasPlugin.getDefault().log(
								"Add Interface .... " + cl.getName());
						result.add(cl.getName());
					}
				}
			}else {
			File[] files = directory.listFiles();
			SasPlugin.getDefault().log(
					"The No. of Files found in current (directory) is "
							+ files.length);
		
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				SasPlugin.getDefault().log("The File obtained is " + files[i]);
				pacakName = files[i].getPath();
				fileName = files[i].getName();
				
				if (fileName.endsWith(".class")) {
					//find the pacakage for windows /linux
					 if(pacakName.equals(bin.concat(fileName))){
						 pacakge="";
					}else{
						if(isWindows()){
							String subpack = pacakName.substring(pacakName
									.indexOf("bin\\") + 4, pacakName.length());
							String pk = subpack.substring(0, subpack.lastIndexOf("\\"));
							pacakge = pk.replace("\\", ".");
						}else{
							String subpack = pacakName.substring(pacakName
									.indexOf("bin/") + 4, pacakName.length());
							String pk = subpack.substring(0, subpack.lastIndexOf("/"));
							pacakge = pk.replace("/", ".");
						}
					}
//					SasPlugin.getDefault().log("The Pacakage is" + pacakge);
					String loadClass = "";
					// removes the .class extension
					if (!pacakge.equals("")) {
						loadClass = pacakge
								+ '.'
								+ fileName
										.substring(0, (fileName.length() - 6));
					} else {
						loadClass = fileName.substring(0,
								(fileName.length() - 6));
					}

					SasPlugin.getDefault().log(
							"loading class with name.... " + loadClass);
					Class cl = loader.loadClass(loadClass);

					//Add if this class is an interface
					if (cl.isInterface()) {
						SasPlugin.getDefault().log(
								"Add Interface .... " + cl.getName());
						result.add(cl.getName());
					}
				} else {
					addInterfcaes(files[i], result, loader);
				}

			}
		  }
		} catch (Exception e) {
			SasPlugin.getDefault().log(
					"IdeUtil The excption is thrown by getInterfaces() "
							+ e.toString());
		}
	}
	    
	    
	    
	    public static ArrayList<String> getInterfaceImplementors(IProject project,
			String interfaceName) {
		ArrayList<String> implemntors = new ArrayList<String>();
		SearchPattern SEARCH_PATTERN = SearchPattern.createPattern(
				interfaceName, IJavaSearchConstants.CLASS,
				IJavaSearchConstants.IMPLEMENTORS, SearchPattern.R_EXACT_MATCH);
		getClassNames(project, SEARCH_PATTERN, implemntors);
		return implemntors;
	}
	    
	    
	    public static ArrayList<String> getALCInterfaceImplementors(IProject project) {
	    	String interfaceName="com.genband.ase.alc.alcml.ALCServiceInterface.ALCServiceInterface".intern();
	    	  SasPlugin.getDefault().log("Inside getALCInterfaceImplementors.........");
			ArrayList<String> implemntors = new ArrayList<String>();
			SearchPattern SEARCH_PATTERN = SearchPattern.createPattern(
					interfaceName, IJavaSearchConstants.CLASS,
					IJavaSearchConstants.IMPLEMENTORS, SearchPattern.R_EXACT_MATCH);
			getClassNames(project, SEARCH_PATTERN, implemntors);
			return implemntors;
		}
		    
		    
	    
	    public static  HashMap<String,List<String>> getClasses(IProject project) throws ClassNotFoundException{
			   File directory=null;
			   HashMap<String,List<String>> result = new  HashMap<String,List<String>>();		
			   bin=Platform.getLocation().toOSString()+project.getFolder("bin").getFullPath().toOSString();
			   if(isWindows()){
				   bin=bin+"\\";  
			   }else{
				   bin=bin+"/";  
			   }
			   SasPlugin.getDefault().log("The bin path found is!!! " + bin);
			  //get the bin directory 
			   try {
				    directory=new File(bin);
				   } catch(NullPointerException x) {
				     throw new ClassNotFoundException(bin+" does not exists");
				}
				
			try{		
					if(directory.exists()) {
						 //get the loader
						 URLClassLoader loader = new URLClassLoader(
									new URL[] { new URL("file:///"
											+ bin) },Thread.currentThread()
											.getContextClassLoader());
						 SasPlugin.getDefault().log("Loader is.... " + loader);   
						 
						// Get the list of the files contained in the bin
						 File[] files=directory.listFiles();
						 SasPlugin.getDefault().log("The No. of Files/directories found in bin are " + files.length); 
						 
						 for(int i=0; i<files.length; i++) {
							 addClasses(files[i],result,loader);
						 }
						
					} else {
						SasPlugin.getDefault().log("bin Directory does not exist!!!!!!!");
					}	
				
			}catch(Exception e){
				SasPlugin.getDefault().log("IdeUtil The excption is thrown by getInterfaces() "+e.toString());
			}
					
					return result;
		   }
	    
	    
	    private static void addClasses(File directory, HashMap result,
			URLClassLoader loader) {
		try {
			String pacakName = null;
			String pacakge="";
			String fileName ="";
			SasPlugin.getDefault().log("addInterfaces listfiles is..."+directory.listFiles());
			if(directory.listFiles()==null){
				 pacakName = directory.getPath();
				  fileName = directory.getName();
				if (fileName.endsWith(".class")) {
					  SasPlugin.getDefault().log("Its a class file its name is... " + fileName);  
						
						 if(pacakName.equals(bin.concat(fileName))){
							pacakge="";
						}else{
							if(isWindows()){
								String subpack = pacakName.substring(pacakName
										.indexOf("bin\\") + 4, pacakName.length());
								String pk = subpack.substring(0, subpack.lastIndexOf("\\"));
								pacakge = pk.replace("\\", ".");
							}else{
								String subpack = pacakName.substring(pacakName
										.indexOf("bin/") + 4, pacakName.length());
								String pk = subpack.substring(0, subpack.lastIndexOf("/"));
								pacakge = pk.replace("/", ".");
							}
						}
						// removes the .class extension
						String loadClass = "";
						if (!pacakge.equals("")) {
							loadClass = pacakge
									+ '.'
									+ fileName
											.substring(0, (fileName.length() - 6));
						} else {
							loadClass = fileName.substring(0,
									(fileName.length() - 6));
						}

						SasPlugin.getDefault().log(
								"loading class with name.... " + loadClass);
						Class cl = loader.loadClass(loadClass);

						//Add if not an interface
						if (!cl.isInterface()) {
							SasPlugin.getDefault().log(
									"Add Class .... " + cl.getName());
							Method[] methods = cl.getDeclaredMethods();
							List<String> methdList = new ArrayList<String>();

							//Load the method of this class
							if (methods != null) {
								for (int j = 0; j < methods.length; j++) {
									methdList.add(methods[j].getName());
								}
								result.put(cl.getName(), methdList);
							}
						}

					}
			}else {
			File[] files = directory.listFiles();
			SasPlugin.getDefault().log(
					"The No. of Files found are  obtained is " + files.length);
		
			for (int i = 0; i < files.length; i++) {
				// we are only interested in .class files
				SasPlugin.getDefault().log("The File obtained is " + files[i]);
				pacakName = files[i].getPath();
				SasPlugin.getDefault().log("The File path or package name is " + pacakName);
				fileName = files[i].getName();
				if (fileName.endsWith(".class")) {
				  SasPlugin.getDefault().log("Its a class file its name is... " + fileName);  
					
					 if(pacakName.equals(bin.concat(fileName))){
						pacakge="";
					}else{
						if(isWindows()){
							String subpack = pacakName.substring(pacakName
									.indexOf("bin\\") + 4, pacakName.length());
							String pk = subpack.substring(0, subpack.lastIndexOf("\\"));
							pacakge = pk.replace("\\", ".");
						}else{
							String subpack = pacakName.substring(pacakName
									.indexOf("bin/") + 4, pacakName.length());
							String pk = subpack.substring(0, subpack.lastIndexOf("/"));
							pacakge = pk.replace("/", ".");
						}
					}
					// removes the .class extension
					String loadClass = "";
					if (!pacakge.equals("")) {
						loadClass = pacakge
								+ '.'
								+ fileName
										.substring(0, (fileName.length() - 6));
					} else {
						loadClass = fileName.substring(0,
								(fileName.length() - 6));
					}

					SasPlugin.getDefault().log(
							"loading class with name.... " + loadClass);
					Class cl = loader.loadClass(loadClass);

					//Add if not an interface
					if (!cl.isInterface()) {
						SasPlugin.getDefault().log(
								"Add Class .... " + cl.getName());
						Method[] methods = cl.getDeclaredMethods();
						List<String> methdList = new ArrayList<String>();

						//Load the method of this class
						if (methods != null) {
							for (int j = 0; j < methods.length; j++) {
								methdList.add(methods[j].getName());
							}
							result.put(cl.getName(), methdList);
						}
					}

				} else {
					addClasses(files[i], result, loader);
				}

			}
			
			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(
					"IdeUtil The excption is thrown by getInterfaces() "
							+ e.toString());
		}
	}
	    
	    
	    public  static ArrayList getAllProjects()
		{
			
			ArrayList listOfProjects = new ArrayList();
			try {
			
			IProject [] projects =ResourcesPlugin.getWorkspace().getRoot().getProjects();
			ArrayList<String> builtProject=BuildSARAction.getBuiltProjects();
			if(builtProject.size()!=0){
			   for(int k=builtProject.size()-1;k>=0;k--){
				   String builtStr=builtProject.get(k);
				   IProject builtpro=ResourcesPlugin.getWorkspace().getRoot().getProject(builtStr);
				   if (builtpro.isOpen()
							&& (builtpro.hasNature(BPSASServicesNature.NATURE_ID)  || IdeUtils.isSASProject(builtpro))) {
				  listOfProjects.add(builtStr);
				   }
			   }
			}
			for (int i = 0; i < projects.length; i++) {
				if (projects[i].isOpen()){
					if(projects[i].hasNature(BPSASServicesNature.NATURE_ID) || IdeUtils.isSASProject(projects[i])) {
					if(listOfProjects.indexOf(projects[i].getName())==-1)
					    listOfProjects.add(projects[i].getName());
				} else if (projects[i].getDescription().hasNature(BPSASServicesNature.VTP_NATURE_ID)) {
					if(listOfProjects.indexOf(projects[i].getName())==-1)
					    listOfProjects.add(projects[i].getName());
				}
				}
			}
			
			} catch (CoreException c) {
				SasPlugin.getDefault().log(
						"The Core exception was thrown while lisitng SAS Projects"
								+ c);
			}
			return listOfProjects;
			}
	    
	    public  static ArrayList getSOAProjects()
		{
			
			ArrayList listOfProjects = new ArrayList();
			try {
			
			IProject [] projects =ResourcesPlugin.getWorkspace().getRoot().getProjects();
			ArrayList<String> builtProject=BuildSARAction.getBuiltProjects();
			if(builtProject.size()!=0){
			   for(int k=builtProject.size()-1;k>=0;k--){
				   String builtStr=builtProject.get(k);
				   IProject builtpro=ResourcesPlugin.getWorkspace().getRoot().getProject(builtStr);
				   if (builtpro.isOpen()
						   && ( (builtpro.hasNature(BPSASServicesNature.NATURE_ID)
							   && builtpro.hasNature(BPSASSOAServicesNature.NATURE_ID))
								 || (isSASProject(builtpro) && isSOAProject(builtpro)) 
								 ) )  {
				  listOfProjects.add(builtStr);
				   }
			   }
			}
			for (int i = 0; i < projects.length; i++) {
				if (projects[i].isOpen()
						&& ( (projects[i].hasNature(BPSASServicesNature.NATURE_ID)
								   && projects[i].hasNature(BPSASSOAServicesNature.NATURE_ID))
									 || (isSASProject(projects[i]) && isSOAProject(projects[i])) 
									 ) ){
					if(listOfProjects.indexOf(projects[i].getName())==-1)
					    listOfProjects.add(projects[i].getName());
				}
			}
			} catch (CoreException c) {
				SasPlugin.getDefault().log(
						"The Core exception was thrown while lisitng SAS Projects"
								+ c);
			}
			return listOfProjects;
			}
		   	  
	    
	    public  static ArrayList getNonSOAProjects()
		{
			
			ArrayList listOfProjects = new ArrayList();
			try {
			
			IProject [] projects =ResourcesPlugin.getWorkspace().getRoot().getProjects();
			ArrayList<String> builtProject=BuildSARAction.getBuiltProjects();
			if(builtProject.size()!=0){
			   for(int k=builtProject.size()-1;k>=0;k--){
				   String builtStr=builtProject.get(k);
				   IProject builtpro=ResourcesPlugin.getWorkspace().getRoot().getProject(builtStr);
				   if (builtpro.isOpen()
						    && ! builtpro.hasNature(BPSASSOAServicesNature.NATURE_ID) 
							&&  ( builtpro.hasNature(BPSASServicesNature.NATURE_ID) 
								 || (isSASProject(builtpro) && !isSOAProject(builtpro)) ))  {
				  listOfProjects.add(builtStr);
				   }
			   }
			}
			for (int i = 0; i < projects.length; i++) {
				if (projects[i].isOpen()
						&&! projects[i].hasNature(BPSASSOAServicesNature.NATURE_ID) 
						&&  ( projects[i].hasNature(BPSASServicesNature.NATURE_ID) 
							 || (isSASProject(projects[i]) && !isSOAProject(projects[i])) 
							 ) ) {
					if(listOfProjects.indexOf(projects[i].getName())==-1)
					    listOfProjects.add(projects[i].getName());
				}
			}
			} catch (CoreException c) {
				SasPlugin.getDefault().log(
						"The Core exception was thrown while lisitng SAS Projects"
								+ c);
			}
			return listOfProjects;
			}
	    
	    
	    public static boolean isSASProject(IProject project){
	    	IFile sasFile = project.getFile(new Path("WEB-INF").append("sas.xml"));
			return sasFile.exists();
	    }
	    
	    public static boolean isSOAProject(IProject project){
	    	IFile sasFile = project.getFile(new Path("WEB-INF").append("soa.xml"));
			return sasFile.exists();
	    }
	  public static boolean isWindows(){
	  if (System.getProperty("os.name").indexOf("Win") == 0)
          return true;
	  else
		  return false;
	  }
	  private static  String bin="";
	  
}

