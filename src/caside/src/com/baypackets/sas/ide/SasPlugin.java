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
package com.baypackets.sas.ide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Element;

import com.baypackets.sas.ide.logger.filters.Filter;
import com.baypackets.sas.ide.logger.filters.FilterLoader;
import com.baypackets.sas.ide.logger.util.XmlUtils;

/**
 * The main plugin class to be used in the desktop.
 */
public class SasPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static SasPlugin plugin = new SasPlugin();
	private static int PORT = 0;
	private static int MAXSIZE = 0;
	private static int jmxurl = -1; //1---> use jmxmp, 0---> use rmiregistry
	private static int debugPORT = 0;
	private static int DELAY = 2000;
//	private ResourceBundle resourceBundle;
	private FontRegistry m_fontRegistry = new FontRegistry();
	private List m_savedFilters = new ArrayList();

	// Preferences constants
	private static final String LOG_FONT = "logwatcherFont";
	private static final String SAVED_FILTERS_FILE = "savedFilters.xml";
	private String myPluginID ="com.baypackets.sas.ide.SasPlugin";
	public static final String ASE_HOME = "ase.home";

	private static int CASSTARTUPTIME=0;
	/**
	 * The constructor.
	 */
	public SasPlugin() 
	{
		plugin = this;
		try {
//			resourceBundle = ResourceBundle.getBundle("com.baypackets.sas.ide.SasPluginResources");
//		}
//		catch (MissingResourceException x) {
//			resourceBundle = null;
//		}
		
	
			
		    File savedFilters = getSavedFiltersFile();
		    if (savedFilters.exists())
		    {
		        initSavedFilters(savedFilters);
		    }
        } 
		catch (Exception e)
        {
            log("Error loading Saved Filters", e);
        }     
	}		

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		if (getPreferenceStore().contains("logwatcherFont")) {
			m_fontRegistry.put("logwatcherFont", 
			PreferenceConverter.getFontDataArray(getPreferenceStore(), "logwatcherFont"));
		}
		
//			IPath classpathProp = new Path(
//					SasPlugin.fullPath("project_classpath.properties"));
//
//			File clasPropFile = classpathProp.toFile();
//			SasPlugin.getDefault().log(
//					"Reading the project_classpath.properties :" + clasPropFile);
//			Properties pathp = new Properties();
//			try {
//				pathp.load(new FileReader(clasPropFile));
//				String aseHome = pathp.getProperty(ASE_HOME);
//				
//				SasPlugin.getDefault().log("The property "+ASE_HOME+" is" + aseHome);
//				if (aseHome != null) {
//					IPath aseHomePath = new Path(aseHome);
//					JavaCore.setClasspathVariable("ASE_HOME",aseHomePath,null);
//				}
//			} catch (FileNotFoundException e) {
//				SasPlugin.getDefault().log(
//						"File not found project_classpath.properties :", e);
//				e.printStackTrace();
//			} catch (IOException e) {
//				SasPlugin.getDefault().log(
//						"IOException project_classpath.properties :", e);
//				e.printStackTrace();
//			}

	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static SasPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace()
	{
		return ResourcesPlugin.getWorkspace();
	}

	
//	/**
//	 * Returns the string from the plugin's resource bundle, or 'key' if not
//	 * found.
//	 */
//	public static String getResourceString(String key)
//	{
//		ResourceBundle bundle = SasPlugin.getDefault().getResourceBundle();
//		try {
//			return bundle.getString(key);
//		}
//		catch (MissingResourceException e) {
//			return key;
//		}
//	}
//
//	/**
//	 * Returns the plugin's resource bundle,
//	 */
//	public ResourceBundle getResourceBundle()
//	{
//		return resourceBundle;
//	}

	protected void initializeDefaultPreferences(IPreferenceStore store)
	{
	//	super.internalInitializeDefaultPluginPreferences();
	//	store.setDefault("saveWatchers", false);
	}
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("com.baypackets.sas.ide", path);
	}
	
	
	/**
	 * Get a font from the plugin's font registry.
	 * 
	 * @param name Symbolic name of the font
	 * @return Font The requested font, or the default font if not found.
	 */
	public Font getFont(String name)
	{
		return m_fontRegistry.get(name);
	}

	public void putFont(String name, FontData[] data)
	{
		m_fontRegistry.put(name, data);
	}
	
	public void addSavedFilter(Filter filter)
	{
	    m_savedFilters.add(filter);
	    persistSavedFilters();
	}
	
	public List getSavedFilters()
	{
	    return Collections.unmodifiableList(m_savedFilters);
	}
	

    
    private void initSavedFilters(File file) throws Exception
    {
        FilterLoader loader = new FilterLoader();
        Vector filters = loader.loadFilters(new FileReader(file));
        m_savedFilters.addAll(filters);
    }
    
    private synchronized void persistSavedFilters()
    {
        File path = getSavedFiltersFile();
		try {
			org.w3c.dom.Document doc = XmlUtils.createDocument();
			Element watcher = doc.createElement("filters");
			doc.appendChild(watcher);
			for (Iterator iter = m_savedFilters.iterator(); iter.hasNext();) {
				Filter element = (Filter) iter.next();
				element.toXML(doc, watcher);
			}
			
			// Write to a file
			Source source = new DOMSource(doc); 
            Result result = new StreamResult(path);
            Transformer xformer = TransformerFactory.newInstance().newTransformer();
            xformer.transform(source, result);
		}
		catch (Exception e) {
			SasPlugin.getDefault().log("Error saving filters", e);
		}
    }

    private File getSavedFiltersFile()
    {
        IPath path = SasPlugin.getDefault().getStateLocation();
        SasPlugin.getDefault().log("The State Location is"+path);
		path = path.addTrailingSeparator();
		path = path.append(SAVED_FILTERS_FILE);
        return path.toFile();
    }
	
	/** This method give the absolute path of the resource which is queried by the key
	 * 
	 * @param entry
	 * @return
	 */
	public static String fullPath(String entry)
	{
        try
        {
            URL url = Platform.resolve(getDefault().getBundle().getEntry(entry));
            String path = url.getPath();
            	                
            return path;
        }
        catch(IOException _ex)
        {
        	_ex.printStackTrace();
            return null;
        }
    }
	
	public static int getPORT()
	{
		if(PORT!=0)
			return PORT;
		try
		{
			URL url = Platform.resolve(getDefault().getBundle().getEntry("conf"));
	       		String path = url.getPath();
	         	String filepath = new Path(path).append("bpconfig").toString();
	           	Properties conf = new Properties();
			FileInputStream fin = new FileInputStream(filepath);
			conf.load(fin);
			PORT = Integer.parseInt(conf.getProperty("PORT").trim());
			fin.close();
			conf = null;
			return PORT;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			PORT=14000;
			return PORT;
			
		}
		
	}
	
	public static int getFileSIZE()
	{
		if(MAXSIZE!=0)
			return MAXSIZE;
		try
		{
			URL url = Platform.resolve(getDefault().getBundle().getEntry("conf"));
			String path = url.getPath();
			String filepath = new Path(path).append("bpconfig").toString();
			Properties conf = new Properties();
			FileInputStream fin = new FileInputStream(filepath);
			conf.load(fin);
			MAXSIZE = Integer.parseInt(conf.getProperty("MAXSIZE").trim());
			fin.close();
			conf = null;
			return MAXSIZE;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			MAXSIZE = 100000;
			return MAXSIZE;
		}
	}
		
	public static int getSASStartupTime()
        {
		if(CASSTARTUPTIME!=0)
			return CASSTARTUPTIME;
		try
                {
                	URL url = Platform.resolve(getDefault().getBundle().getEntry("conf"));
                        String path = url.getPath();
                        String filepath = new Path(path).append("bpconfig").toString();
                        Properties conf = new Properties();
                        FileInputStream fin = new FileInputStream(filepath);
                        conf.load(fin);
                        CASSTARTUPTIME= Integer.parseInt(conf.getProperty("CASSTARTUPTIME").trim());
			fin.close();
			conf = null;
                        return CASSTARTUPTIME;
                }
                catch(Exception e)
                {
                        e.printStackTrace();
                        CASSTARTUPTIME = 20000;
                        return CASSTARTUPTIME;
                }
       }

        public static int getJMXURL()
        {	
		if(jmxurl!=-1)
			return jmxurl;
		try
                {
                        URL url = Platform.resolve(getDefault().getBundle().getEntry("conf"));
                        String path = url.getPath();
                        String filepath = new Path(path).append("bpconfig").toString();
                        Properties conf = new Properties();
                        FileInputStream fin = new FileInputStream(filepath);
                        conf.load(fin);
                        jmxurl= Integer.parseInt(conf.getProperty("JMXURL").trim());
			fin.close();
			conf = null;
                        return jmxurl;
                }
                catch(Exception e)
                {
                        e.printStackTrace();
			jmxurl = 1;
                        return jmxurl;
                }

       }

        public static int getDebugPort()
        {
		if(debugPORT!=0)
			return debugPORT;
		try
                {
                        URL url = Platform.resolve(getDefault().getBundle().getEntry("conf"));
                        String path = url.getPath();
                        String filepath = new Path(path).append("bpconfig").toString();
                        Properties conf = new Properties();
                        FileInputStream fin = new FileInputStream(filepath);
                        conf.load(fin);
                        debugPORT= Integer.parseInt(conf.getProperty("DEBUGPORT").trim());
			fin.close();
			conf = null;
                        return debugPORT;
                }
                catch(Exception e)
                {
                        e.printStackTrace();
                        debugPORT = 8000;
                        return debugPORT;
                }
       }
	public static int getDelay()
	{
		if(DELAY!=0)
			return DELAY;
		 try
                {
                        URL url = Platform.resolve(getDefault().getBundle().getEntry("conf"));
                        String path = url.getPath();
                        String filepath = new Path(path).append("bpconfig").toString();
                        Properties conf = new Properties();
                        FileInputStream fin = new FileInputStream(filepath);
                        conf.load(fin);
                        DELAY= Integer.parseInt(conf.getProperty("DELAY").trim());
                        fin.close();
                        conf = null;
			return DELAY;
                }
                catch(Exception e)
                {
                        e.printStackTrace();
                         DELAY= 2000;
                        return DELAY;
                }
	}

	public static IPath getBundlePath(){
		return JavaCore.getClasspathVariable("ECLIPSE_HOME").append("plugins").append(getDefault().getBundle().getSymbolicName()+"_"+getDefault().getBundle().getVersion().toString());
	}


	public  void log(String msg)
	{
		log(msg, null);
	}

	public  void log(String str, Exception e)
	{
		getLog().log(new Status(Status.INFO, myPluginID, Status.OK,str, e));
	}
}
