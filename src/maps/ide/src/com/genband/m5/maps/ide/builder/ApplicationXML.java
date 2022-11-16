package com.genband.m5.maps.ide.builder;

import com.genband.m5.maps.ide.sitemap.model.SiteMap;
import java.util.List;
import java.net.URL;
import java.net.URLClassLoader;
import com.genband.m5.maps.ide.model.CPFScreen;
import com.genband.m5.maps.ide.MyObjectInputStream;
import java.net.MalformedURLException;
import org.eclipse.core.resources.IFile;
import java.io.ObjectInputStream;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.resources.IProject;
import com.genband.m5.maps.ide.sitemap.util.SiteMapUtil;
import java.io.File;
import org.eclipse.core.runtime.Platform;
import com.genband.m5.maps.ide.CPFPlugin;
import org.eclipse.core.resources.ResourcesPlugin;
import java.io.IOException;

public class ApplicationXML
{
  protected static String nl;
  public static synchronized ApplicationXML create(String lineSeparator)
  {
    nl = lineSeparator;
    ApplicationXML result = new ApplicationXML();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "" + NL + "<!--" + NL + "/**********************************************************************" + NL + "*\t GENBAND, Inc. Confidential and Proprietary" + NL + "*" + NL + "* This work contains valuable confidential and proprietary " + NL + "* information." + NL + "* Disclosure, use or reproduction without the written authorization of" + NL + "* GENBAND, Inc. is prohibited.  This unpublished work by GENBAND, Inc." + NL + "* is protected by the laws of the United States and other countries." + NL + "* If publication of the work should occur the following notice shall " + NL + "* apply:" + NL + "* " + NL + "* \"Copyright 2007 GENBAND, Inc.  All rights reserved.\"" + NL + "**********************************************************************/" + NL + "-->" + NL + "<application version=\"1\">" + NL + "  <description>Common Provisioning Framework</description>" + NL + "  <display-name>CPF</display-name>" + NL + "  <module>" + NL + "        <java>common.jar</java>" + NL + "  </module>" + NL + "  <module>" + NL + "    <ejb>CPF_EJB.jar</ejb>" + NL + "  </module>";
  protected final String TEXT_2 = NL + "  <module>" + NL + "        <web>" + NL + "            <web-uri>";
  protected final String TEXT_3 = "</web-uri>" + NL + "            <context-root>";
  protected final String TEXT_4 = "</context-root>" + NL + "        </web>" + NL + "  </module>        ";
  protected final String TEXT_5 = "</context-root>" + NL + "        </web>" + NL + "  </module>";
  protected final String TEXT_6 = "      " + NL + "</application> ";
  protected final String TEXT_7 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	//SiteMap object will come to this file as an argument
	//List<IFile> siteMapFiles = (List<IFile>)argument;
	IProject app_project = (IProject)argument;
	List<IFile> siteMapFiles = SiteMapUtil.getSiteMapFiles(app_project.getName());
	IFile sitemapFile = null;
	SiteMap siteMap = null;
	String sepChar = File.separator;

	String projectName = app_project.getName();

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
	String absProjPath = platformPath.concat(projectPath).concat(sepChar);

    stringBuffer.append(TEXT_1);
    
					// Changes related to PR 49914 (ClassLoading issue) starts
					List<String> externalJarsPath = SiteMapUtil.getExtrnalJars(app_project.getName());
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

for ( int i = 0 ; i < siteMapFiles.size() ; i++ ) {
	sitemapFile = siteMapFiles.get(i);
	ObjectInputStream in = null;
				
	try {
			//in = new ObjectInputStream(sitemapFile.getContents());
			in = new MyObjectInputStream(loader,sitemapFile.getContents());
			// Changed due to PR 49914 (ClassLoading issue)
			} catch (IOException e) {
					e.printStackTrace();
			} catch (CoreException e) {
					e.printStackTrace();
			}
			try {
				
				String fileLoc="";
				siteMap = (SiteMap)in.readObject();

    stringBuffer.append(TEXT_2);
    stringBuffer.append( siteMap.getName().concat(".war") );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( siteMap.getName().concat(".war") );
    stringBuffer.append(TEXT_4);
    
		} catch (IOException e) {
				e.printStackTrace();
		} catch (ClassNotFoundException e) {
				e.printStackTrace();
			
		}
						
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	

}
	String webXMLFileLoc = absProjPath+sepChar+"WebContent"+sepChar+"WEB-INF"+sepChar+"web.xml";
	File webXMLFile = new File(webXMLFileLoc);
	if(webXMLFile.exists()){

    stringBuffer.append(TEXT_2);
    stringBuffer.append( app_project.getName().concat("-WS.war") );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( app_project.getName().concat("-WS") );
    stringBuffer.append(TEXT_5);
    
	}
	
	

    stringBuffer.append(TEXT_6);
    stringBuffer.append(TEXT_7);
    return stringBuffer.toString();
  }
}
