package com.genband.m5.maps.ide.sitemap;
import java.io.Reader;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IProject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.sitemap.util.XmlUtils;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.xml.sax.InputSource;
import java.io.File;
public class SiteMapPersister {
	

	private Map<String, java.util.List<String>> sitemapRolesMap;
	private String themeType=CPFConstants.THEMES[2];
	private String layoutType=CPFConstants.LAYOUTS[0];
	private String fileName="";
	private static final String VIEW = "View";
	private static final String SITEMAP_PERSISTER = "sitemap.xml";

	private static final String VIEW_RECURSIVELY = "View Recursively";
	
	public void toXML(Document doc, Node node)
	{
	    Element sitemap = doc.createElement("sitemap");
//	    watcher.appendChild(XmlUtils.createElementWithText(doc, "file", getWatcher().getFilename()));
	    sitemap.appendChild(XmlUtils.createElementWithText(doc, "filename", this.getFileName())); 
	    sitemap.appendChild(XmlUtils.createElementWithText(doc, "theme", themeType));
	    sitemap.appendChild(XmlUtils.createElementWithText(doc, "layout", layoutType));
	 
	    
	    Element roles = doc.createElement("roles");
	    Element view= doc.createElement("view");
//	    Element viewRecu= doc.createElement("view-recursive");
	    
	    java.util.List<String> viewRoles=sitemapRolesMap.get(this.VIEW);
//	    java.util.List<String> viewRecurRoles=sitemapRolesMap.get(this.VIEW_RECURSIVELY);
	    
	    if(viewRoles!=null){
	    for(int i=0;i<viewRoles.size();i++){
	    	String role=viewRoles.get(i);
	    	roles.appendChild(XmlUtils.createElementWithText(doc, "view", role));
	    	
	    }
	    }
	    
//	    if(viewRecurRoles!=null){
//	    for(int i=0;i<viewRecurRoles.size();i++){
//	    	String role=viewRecurRoles.get(i);
//	    	roles.appendChild(XmlUtils.createElementWithText(doc, "view-recursive", role));
//	    	
//	    }
//	    }
//	    roles.appendChild(view);
//	    roles.appendChild(viewRecu);
	    sitemap.appendChild(roles);
	    node.appendChild(sitemap);
	}
	
	
	
	public void loadSitemap(String projectName,String sitemapFileName) throws Exception
	{
		String path =Platform.getLocation().toOSString()+getProjectHandle(projectName).getFullPath().append(".resources").append("sitemap")
		.append(SITEMAP_PERSISTER).toOSString();
		
		File file=new File(path);
		if(file.exists()){
			CPFPlugin.getDefault().log("Persister file Exists so loadiong site map for this file"+sitemapFileName);	
		FileReader r=new FileReader(file);
		org.w3c.dom.Document doc = createDocument(r);
		loadSitemap(doc,sitemapFileName);
		}
	}

	public void loadSitemap(org.w3c.dom.Document doc,String sitemapFileName)
	{
		NodeList sitemapNodes = doc.getElementsByTagName("sitemap");
		CPFPlugin.getDefault().log("No of sitemaps persisted for this project are.."+sitemapNodes.getLength(),IStatus.INFO);
		
			for (int i = 0; i < sitemapNodes.getLength(); i++) {
				Node node = sitemapNodes.item(i);
				loadSitemap(node,sitemapFileName);
		}
			
			this.sitemapRolesMap=new HashMap<String, java.util.List<String>>();
			
			if(!this.viewRolesList.isEmpty()){
			  this.sitemapRolesMap.put(this.VIEW, this.viewRolesList);
			 
			}
//			if(!this.viewRecurRolesList.isEmpty()){
//				 this.sitemapRolesMap.put(this.VIEW_RECURSIVELY, this.viewRecurRolesList);
//			}
//			
			
		
	}
	
	
	private void loadSitemap(Node sitemapNode,String sitemapFileName){
		
		viewRolesList=new 	java.util.ArrayList<String>();
		viewRecurRolesList=new java.util.ArrayList<String>();
		NodeList children = sitemapNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			String name = node.getNodeName();
			if(name.equals("filename")){
				fileName = node.getFirstChild().getNodeValue();	
				CPFPlugin.getDefault().log("LoadSitemap file  obtained is..."+fileName);	
			}
			
			if(fileName.equals(sitemapFileName)){
				if (name.equals("theme")) {
					themeType = node.getFirstChild().getNodeValue();
					CPFPlugin.getDefault().log("LoadSitemap file  theme obtained is..."+themeType);	
				}
				else if (name.equals("layout")) {
					layoutType = node.getFirstChild().getNodeValue();
					CPFPlugin.getDefault().log("LoadSitemap file  layout obtained is..."+layoutType);	
				}
				else if (name.equals("roles")) {
					children = node.getChildNodes();
					for (int j = 0; j < children.getLength(); j++) {
						Node role = children.item(j);
						name = role.getNodeName();
						
						if(name.equals("view")){
								String viewRol=role.getFirstChild().getNodeValue();
								CPFPlugin.getDefault().log("Sitemap view roles is ontained is..."+viewRol);	
								viewRolesList.add(viewRol);
								
					}
//								else if(name.equals("view-recursive")){
//							    String viewRecurRol=role.getFirstChild().getNodeValue();
//							    CPFPlugin.getDefault().log("Sitemap view Recursive roles is ontained is..."+viewRecurRol);	
//								viewRecurRolesList.add(viewRecurRol);
//								
//					    }
					}
					
				}
			}
					
			
		}
	}
	
	
	/**
	 * Create a Document with content based on the content of the given Reader.
	 */
	protected org.w3c.dom.Document createDocument(Reader r) throws Exception
	{
		org.w3c.dom.Document document;
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder parser = factory.newDocumentBuilder();
			document = parser.parse(new InputSource(r));
			return document;
		}
		catch (Exception e) {
			throw e;
		}
	}
	
	public Map<String, java.util.List<String>> getSitemapRolesMap() {
		return sitemapRolesMap;
	}
	public void setSitemapRolesMap(
			Map<String, java.util.List<String>> sitemapRolesMap) {
		this.sitemapRolesMap = sitemapRolesMap;
	}
	public String getThemeType() {
		return themeType;
	}
	public void setThemeType(String themeType) {
		this.themeType = themeType;
	}
	public String getLayoutType() {
		return layoutType;
	}
	public void setLayoutType(String layoutType) {
		this.layoutType = layoutType;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public IProject getProjectHandle(String projectName) {
		if (projectName != null)
			return ResourcesPlugin.getWorkspace().getRoot().getProject(
					projectName);
		else
			return null;
	}
	
	
	
	
	java.util.List<String> viewRolesList=null;
	java.util.List<String> viewRecurRolesList=null;
	
	


}
