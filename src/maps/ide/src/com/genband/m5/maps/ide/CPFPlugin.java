package com.genband.m5.maps.ide;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import com.genband.m5.maps.common.CPFConstants;
/**
 * The main plugin class to be used in the desktop.
 */
public class CPFPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static CPFPlugin plugin;
	private String myPluginID ="com.genband.m5.maps.CPFPlugin";
	private ImageRegistry imageRegistry = null;
	/**
	 * The constructor.
	 */
	public CPFPlugin() {
		plugin = this;
		getImageRegistry();
	}
	
	 protected ImageRegistry createImageRegistry() {
		        
		        //If we are in the UI Thread use that
		  if(Display.getCurrent() != null)
		               return new ImageRegistry(Display.getCurrent());
		          
		          if(PlatformUI.isWorkbenchRunning())
		              return new ImageRegistry(PlatformUI.getWorkbench().getDisplay());
		          
		          //Invalid thread access if it is not the UI Thread
		  //and the workbench is not created.
		  throw new SWTError(SWT.ERROR_THREAD_INVALID_ACCESS);
		      }
	 
	 
	 public ImageRegistry getImageRegistry() {
		         if (imageRegistry == null) {
		              imageRegistry = createImageRegistry();
		              this.log("getImage registery........."+imageRegistry);
		              intializeImageRegistery(imageRegistry);
		          }
		        
		          return imageRegistry;
		     }

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
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
	public static CPFPlugin getDefault() {
		return plugin;
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
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("com.genband.m5.maps", path);
	}
	
	
	
	
	protected void intializeImageRegistery(ImageRegistry reg){
		this.log("Initializing Image Registery!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		
		// For sitemap use
		reg.put("connection_d16.gif", getImageDescriptor("/icons/connection_d16.gif").createImage());	
		reg.put("connection_d24.gif", getImageDescriptor("/icons/connection_d24.gif").createImage());	
		reg.put("connection_s16.gif", getImageDescriptor("/icons/connection_s16.gif").createImage());	
		reg.put("connection_s24.gif", getImageDescriptor("/icons/connection_s24.gif").createImage());	
		reg.put("footer16.bmp", getImageDescriptor("/icons/footer16.bmp").createImage());
		reg.put("header16.bmp", getImageDescriptor("/icons/header16.bmp").createImage());
		reg.put("page16.bmp", getImageDescriptor("/icons/page16.bmp").createImage());
		reg.put("page16_0.bmp", getImageDescriptor("/icons/page16_0.bmp").createImage());
		reg.put("page16_1.bmp", getImageDescriptor("/icons/page16_1.bmp").createImage());
		reg.put("page16_2.bmp", getImageDescriptor("/icons/page16_2.bmp").createImage());
		reg.put("subpage16.bmp", getImageDescriptor("/icons/subpage16.bmp").createImage());
		reg.put("subpage16_0.bmp", getImageDescriptor("/icons/subpage16_0.bmp").createImage());
		reg.put("subpage16_1.bmp", getImageDescriptor("/icons/subpage16_1.bmp").createImage());
		reg.put("subpage16_2.bmp", getImageDescriptor("/icons/subpage16_2.bmp").createImage());
		reg.put("page216.bmp", getImageDescriptor("/icons/page216.gif").createImage());
		reg.put("page24.bmp", getImageDescriptor("/icons/page24.bmp").createImage());
		reg.put("subpage24.bmp", getImageDescriptor("/icons/subpage24.bmp").createImage());
		reg.put("page316.bmp", getImageDescriptor("/icons/page316.gif").createImage());
		reg.put("placeHolder16.bmp", getImageDescriptor("/icons/placeHolder16.bmp").createImage());
		reg.put("placeHolder24.bmp", getImageDescriptor("/icons/placeHolder24.bmp").createImage());
		reg.put("portlet16.bmp", getImageDescriptor("/icons/portlet16.bmp").createImage());
		reg.put("portlet16_0.bmp", getImageDescriptor("/icons/portlet16_0.bmp").createImage());
		reg.put("portlet16_1.bmp", getImageDescriptor("/icons/portlet16_1.bmp").createImage());
		reg.put("portlet16_2.bmp", getImageDescriptor("/icons/portlet16_2.bmp").createImage());
		reg.put("portlet24_0.bmp", getImageDescriptor("/icons/portlet24_0.bmp").createImage());
		reg.put("portlet24_1.bmp", getImageDescriptor("/icons/portlet24_1.bmp").createImage());
		reg.put("portlet24_2.bmp", getImageDescriptor("/icons/portlet24_2.bmp").createImage());
		reg.put("pageContentGroup16.bmp", getImageDescriptor("/icons/pageContentGroup16.bmp").createImage());
		reg.put("pageChildGroup16.bmp", getImageDescriptor("/icons/pageChildGroup16.bmp").createImage());
		
		//for sitemap and CPF Project wizards use
		reg.put("sitemap16.bmp", getImageDescriptor("/icons/sitemap16.bmp").createImage());
		reg.put("NAVIGATION_TYPE_I", getImageDescriptor("/icons/type_I.bmp").createImage());
		reg.put("navi2.gif", getImageDescriptor("/icons/navi2.gif").createImage());
		reg.put("sample1.gif", getImageDescriptor("/icons/sample1.gif").createImage());
		reg.put("sitemapPref.bmp", getImageDescriptor("/icons/sitemapPref.bmp").createImage());
		reg.put(CPFConstants.THEMES[0], getImageDescriptor("/icons/industrial.bmp").createImage());
		reg.put(CPFConstants.THEMES[2], getImageDescriptor("/icons/maple.bmp").createImage());
		reg.put(CPFConstants.THEMES[3], getImageDescriptor("/icons/nphalanx.bmp").createImage());
		reg.put(CPFConstants.THEMES[1], getImageDescriptor("/icons/ranaissance.bmp").createImage());
		reg.put(CPFConstants.THEMES[4], getImageDescriptor("/icons/mission-critical.bmp").createImage());
		reg.put(CPFConstants.LAYOUTS[0], getImageDescriptor("/icons/2cols_leftmenu.bmp").createImage());
		reg.put(CPFConstants.LAYOUTS[1], getImageDescriptor("/icons/3cols.bmp").createImage());
		
	}
	
	
	public  void log(String msg, int loglevel)
	{
		log(msg, null, loglevel);
	}
	
	public  void log(String msg)
	{
		log(msg, null, -1);
	}

	public  void info(String msg)
	{
		log(msg, null, IStatus.INFO);
	}

	public  void warn(String msg)
	{
		log(msg, null, IStatus.WARNING);
	}

	public  void error(String msg)
	{
		log(msg, null, IStatus.ERROR);
	}

	public  void error(String msg, Throwable t)
	{
		log(msg, t, IStatus.ERROR);
	}

	public  void log(String str, Throwable e, int loglevel)
	{
		if (loglevel == -1)
			loglevel = IStatus.INFO;
		
		getLog().log(new Status(loglevel, myPluginID, Status.OK,str, e));
	}
}
