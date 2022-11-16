package com.genband.m5.maps.ide.sitemap.util;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.model.ModelEntity;
import com.genband.m5.maps.ide.sitemap.model.SiteMap;


public class SiteMapUtil {

	/**
	 * @param args
	 */
	public static void readSiteMap() {
		// TODO Auto-generated method stub
		ObjectInputStream input = null;
		try {
			System.out.println("" + getSiteMapPath());
			File f = new File("D:\\Vandana\\installers\\eclipse 3.3.x\\eclipse\\plugins\\com.genband.m5.maps\\src\\com\\genband\\m5\\maps\\sitemap\\4.sitemap");
			
			System.out.println("dfsdfg");
			input = new ObjectInputStream(
					new BufferedInputStream(new FileInputStream(f)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			SiteMap sitemap = (SiteMap) input.readObject();
			System.out.println("sitemap name is : " + sitemap.getName());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static String getSiteMapPath() {
		
		try {
			IFolder sitemapFolder = ResourcesPlugin.getWorkspace().getRoot()
			.getProject("123").getFolder("WebContent").getFolder("WEB-INF").getFolder("sitemap");
			IPath path = ResourcesPlugin.getWorkspace().getRoot().getLocation();
			System.out.println("workspace  " + ResourcesPlugin.getWorkspace());
			System.out.println("sitemap folder : " + sitemapFolder);
			String platformPath = Platform.getLocation().toOSString();
			System.out.println("abcd " + platformPath);
			IFile sitemapFile = sitemapFolder.getFile("1.sitemap");
			System.out.println("sitemap path is : " + sitemapFile);
			ObjectInputStream in = new ObjectInputStream(sitemapFile.getContents());
			SiteMap siteMap = (SiteMap) in.readObject();
			System.out.println("" + siteMap.getName());
			return siteMap.getName() + "Roles: " + siteMap.getRoles();
			/*IFolder resFolder = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(page1.getProjectName()).getFolder(".resources");
			String platformPath = Platform.getLocation().toOSString();
			IFolder baseEntFolder = null;
			String baseEntityName = page2.getBaseEntity().getName();

			if (baseEntityName != null) {

				IFolder portalFolder = resFolder.getFolder("portal");

				if (!portalFolder.exists()) {
					CPFPlugin.getDefault()
							.log("Portal folder donot Exist!!!!!");
					resourcesPath = null;
				} else if (portalFolder.exists()) {
					baseEntFolder = portalFolder.getFolder(baseEntityName);
					if (!baseEntFolder.exists()) {
						resourcesPath = null;
					} else if (baseEntFolder.exists()) {
						resourcesPath = platformPath
								+ baseEntFolder.getFullPath().toOSString();
					}
				}
			}
*/
		} catch (Exception c) {
			System.out.println("exception caught");

		}
		return null;
	}
	public static List<IFile> getSiteMapFiles(String p_projectName) {
		SiteMap siteMap = null;
		
		try {
			IFolder sitemapFolder = null;
			if(null != ResourcesPlugin.getWorkspace()
					&& ( null != ResourcesPlugin.getWorkspace().getRoot()) 
					&& (null!=ResourcesPlugin.getWorkspace().getRoot().getProject(p_projectName))
					&& (null!= ResourcesPlugin.getWorkspace().getRoot()
							.getProject(p_projectName).getFolder("WebContent"))
					&& (null != ResourcesPlugin.getWorkspace().getRoot()
							.getProject(p_projectName).getFolder("WebContent").getFolder("WEB-INF"))
					&& (null != ResourcesPlugin.getWorkspace().getRoot()
							.getProject(p_projectName).getFolder("WebContent").getFolder("WEB-INF").getFolder("sitemap"))){
				sitemapFolder = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(p_projectName).getFolder("WebContent").getFolder("WEB-INF").getFolder("sitemap");
				
			}
			String platformPath = null;
			if(null != Platform.getLocation()){
				platformPath = Platform.getLocation().toOSString();
			}
			if ( null != platformPath && null != sitemapFolder){
				String siteMapFolderPath = sitemapFolder.getFullPath().toOSString();
			
			siteMapFolderPath = platformPath+siteMapFolderPath;
			//System.out.println("SiteMap folder path is : " + siteMapFolderPath);
			File file = new File(siteMapFolderPath);
			IFile sitemapFile = null;
			List<IFile> siteMapFiles = new ArrayList();
			//System.out.println("file is : " + file);
			//System.out.println("file exists: " + file.exists());
			//System.out.println("is directiory : " + file.isDirectory());
			//System.out.println("No. of files : " + file.listFiles().length);
			for( int i = 0 ; i < file.listFiles().length; i++){
				String fileName = file.listFiles()[i].getName();
				sitemapFile = sitemapFolder.getFile(fileName);
				siteMapFiles.add(sitemapFile);
				//sitemapFile.getContents();
				//ObjectInputStream in = new ObjectInputStream(sitemapFile.getContents());
				//siteMap = (SiteMap) in.readObject();
				//System.out.println("SiteMap name is : " + siteMap.getName() + " \nNo. of pages: " + siteMap.getNoOfPages());
				//in.close();	
			}
			return siteMapFiles;
			}
		} catch (Exception c) {
			System.out.println("exception caught");

		}
		return null;
	}

	
	// Function is defined due to PR 49914 (ClassLoading issue)

	public static List<String> getExtrnalJars(String p_projectName) {
		
		try {
			IFolder ejbContentFolder = null;
			if(null != ResourcesPlugin.getWorkspace()
					&& ( null != ResourcesPlugin.getWorkspace().getRoot()) 
					&& (null!=ResourcesPlugin.getWorkspace().getRoot().getProject(p_projectName))
					&& (null!= ResourcesPlugin.getWorkspace().getRoot()
							.getProject(p_projectName).getFolder("EJBContent"))){
				ejbContentFolder = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(p_projectName).getFolder("EJBContent");
				
			

				String path = Platform.getLocation().toOSString()
						+ ejbContentFolder.getFullPath().toOSString();
				URL[] jarUrls=null;
				File file = new File(path);
				File[] files = file.listFiles();
				List<String> jarFilesPath = new ArrayList();
				CPFPlugin.getDefault().log(
						"The file Obtained is...." + file
								+ " The list of files is.." + files);
			
				if (files != null) {
					jarUrls=new URL[files.length];
					CPFPlugin
							.getDefault()
							.log(
									"Input file is a directory. Call the method recursively.");
					for (int i = 0; i < files.length; i++) {
						String fileName = files[i].getName();
						if (fileName.endsWith(".jar")) {
						CPFPlugin.getDefault().log(
										"The Entity jar file issssss..." + fileName +" Absolute path is .."+files[i].getAbsolutePath());
								jarUrls[i]=new URL("file:///"+files[i].getAbsolutePath());
								

							
								CPFPlugin.getDefault().log(
										"The Entity jar file issssss..." + fileName +" Absolute path is .."+files[i].getAbsolutePath());
								
								jarFilesPath.add(files[i].getAbsolutePath());
								
						}
					}
					
				
				
			}
			
			return jarFilesPath;
			}
		} catch (Exception c) {
			System.out.println("exception caught");

		}
		return null;
	}

}
