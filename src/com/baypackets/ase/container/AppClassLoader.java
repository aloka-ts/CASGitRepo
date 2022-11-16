/*
 * AppClassLoader.java
 *
 * Created on July 14, 2004, 10:14 AM
 */
package com.baypackets.ase.container;

import java.net.URLClassLoader;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.startup.AseClassLoader;
import com.baypackets.ase.soa.util.MemoryOutputJavaFileObject;
import com.baypackets.ase.soa.util.MemoryOutputJavaFileManager;
import com.baypackets.ase.util.Constants;

import org.apache.log4j.Logger;

import sun.misc.URLClassPath;

/**
 * Loads the classes and resources used in a Servlet application.
 */
public class AppClassLoader extends URLClassLoader {

	private static Logger logger = Logger.getLogger(AppClassLoader.class);        
    
	//BUG: 9936 TCAP FT Changes in App Class Loader
	private String appName = null;
	private List<ClassLoader> siblings = new ArrayList<ClassLoader>();
	
	//BUG: 7630
	private AseHost host = (AseHost) Registry.lookup(Constants.NAME_HOST);
	private String className = null;
	private URLClassPath ucp = null;
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}
	public List<ClassLoader> getSiblings() {
		return siblings;
	}

	public void setSiblings(List<ClassLoader> siblings) {
		this.siblings = siblings;
	}
	/**
     * Default constructor.
     */
    public AppClassLoader() {
        super(new URL[0]);
    }

	/**
	* Sets the parent class loader.
	*/
	public AppClassLoader(ClassLoader parent) {
		super(new URL[0], parent);
	}

    /**
     * Adds the specified URL to the list of repositories to be
     * searched when loading classes or resources.
     */
    public void addRepository(URL repository) {
    	if(ucp==null)
    	ucp = new URLClassPath(new URL[]{repository}); 
    	else
    		ucp.addURL(repository);
    	super.addURL(repository);
    }
    
    /**
	* finds a resource by name
	*/
    public URL findResource(String name)
    {
    	return ucp.findResource(name,false);
    }
    
	/**
	* Returns class from classloader.
	*/
	public Class findClass(String name) throws ClassNotFoundException	{
		Class c = null;
		if(logger.isDebugEnabled())	{
			logger.debug("Inside findClass() of AppClassLoader for AppName "+appName);
			logger.debug("classname: "+name);
		}
		MemoryOutputJavaFileObject jfo = MemoryOutputJavaFileManager.outputMap.get(name);
		if(jfo != null)	{
			byte[] bytes = (jfo.getOutputStream()).toByteArray();
			c = defineClass(name,bytes,0,bytes.length);
			if(logger.isDebugEnabled())	{
				logger.debug("Class being returned from Memory: "+c);
			}
		}else	{		
			if (appName != null && appName.equals(Constants.TCAP_PROVIDER_APP_NAME)){
				
				if(logger.isDebugEnabled())	{
					logger.debug("Trying to load the class for tcap-provider: "+ appName +" Class "+c);
				}
				List<ClassLoader> loaders = this.getSiblings();
				int size = loaders.size();
				for (int i=0;i<size;i++ ){
					AppClassLoader appClassLoader = (AppClassLoader) loaders.get(i);
					try {
						c = appClassLoader.loadClass(name);
						if(logger.isDebugEnabled())	{
							logger.debug("Class being returned from Listener's Class Loader: "+c);
						}
						break;
					}catch (ClassNotFoundException exp) {
						logger.error(" ClassNotFoundException Not able to load class from " + appClassLoader + "ClassName: "+name);
						if (i == (size-1))
							throw exp;
					}
				}
			}else{
				c =  super.findClass(name);	
			}
			
			if(logger.isDebugEnabled())	{
				logger.debug("Class being returned: "+c +" For appName "+appName);
			}			
		/*	try{
				c =  super.findClass(name);	
				if(logger.isDebugEnabled())	{
					logger.debug("Class being returned: "+c +" For appName "+appName);
				}			
			}catch (ClassNotFoundException e) {
				
				if(logger.isDebugEnabled())	{
					logger.debug("ClassNotFoundException :For appName "+appName +" trying to load from other loaders sbb/siblings of this loader if appname is tcap-provider");
				}	
				
				if (host != null && appName == null){
					
					if(logger.isDebugEnabled())	{
						logger.debug("Trying to load the class from SBB Class Loader: ");
					}
					AppClassLoader appclassLoader = (AppClassLoader) host.getLatestSbbCL();
					if (appclassLoader != null){
						if (appclassLoader.getClassName() != null && appclassLoader.getClassName().equals(name)){
							throw e;
						} else{
							appclassLoader.setClassName(name);
							c = appclassLoader.loadClass(name);
							if(logger.isDebugEnabled())	{
								logger.debug("Class being returned from SBB Class Loader: "+c);
							}
						}
					}else{
						throw e;	
					}
				}else if (appName != null && appName.equals(Constants.TCAP_PROVIDER_APP_NAME)){
					
					if(logger.isDebugEnabled())	{
						logger.debug("Trying to load the class for tcap-provider: "+ appName +" Class "+c);
					}
					List<ClassLoader> loaders = this.getSiblings();
					int size = loaders.size();
					for (int i=0;i<size;i++ ){
						AppClassLoader appClassLoader = (AppClassLoader) loaders.get(i);
						try {
							c = appClassLoader.loadClass(name);
							if(logger.isDebugEnabled())	{
								logger.debug("Class being returned from Listener's Class Loader: "+c);
							}
							break;
						}catch (ClassNotFoundException exp) {
							logger.error(" ClassNotFoundException Not able to load class from " + appClassLoader + "ClassName: "+name);
							if (i == (size-1))
								throw exp;
						}
					}
				}else {
					throw e;
				}
		}*/
	} 
		return c;
	}
}
