package com.genband.m5.maps.mgmt;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.log4j.Logger;

public class JMXClientContext {

	private static final Logger log = Logger.getLogger (JMXClientContext.class);	
	
	public void invoke(final String name, String opName, List<String> opArgs) {

	    ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
	    log.debug ("Old CL: " + oldCL);
	    
		try {	   
		    
		    Thread.currentThread().setContextClassLoader( jmxClientCL );

		    Class clazz = jmxClientCL.loadClass("com.genband.m5.maps.mgmt.jmx.JMXUtility");
		    Method singletonM = clazz.getMethod("getInstance", null);
		    Object jmxAdaptor = singletonM.invoke(null, new Object[] {});
		    
		    log.debug(jmxAdaptor.getClass() + ", adaptorcl: " + jmxAdaptor.getClass().getClassLoader());
		    
		    Method minvoke = clazz.getMethod("invoke", new Class[] {String.class, String.class, List.class});
		    minvoke.invoke(jmxAdaptor, name, opName, opArgs);
			
		} catch (Exception e) {
			log.error ("exception in invoking jmx client", e);
		}
		
		finally {

	         Thread.currentThread().setContextClassLoader(oldCL);
		}
	}
	
	public void set(final String name, String theAttr, String theVal) {

	    ClassLoader oldCL = Thread.currentThread().getContextClassLoader();	
	    
		try {	     
		    
		    Thread.currentThread().setContextClassLoader( jmxClientCL );		
			
			Class clazz = jmxClientCL.loadClass("com.genband.m5.maps.mgmt.jmx.JMXUtility");
		    Method singletonM = clazz.getMethod("getInstance", null);
		    Object jmxAdaptor = singletonM.invoke(null, new Object[] {});
		    
		    log.debug(jmxAdaptor.getClass() + ", adaptorcl: " + jmxAdaptor.getClass().getClassLoader());
		    
		    Method mset = clazz.getMethod("set", new Class[] {String.class, String.class, String.class});
		    mset.invoke(jmxAdaptor, name, theAttr, theVal);

			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		finally {

	         Thread.currentThread().setContextClassLoader(oldCL);
		}
	}
	
	public String get(final String name, String attributeNames) {

	    ClassLoader oldCL = Thread.currentThread().getContextClassLoader();	
	    
	    String result = null;
	    
		try {	     
		    
		    Thread.currentThread().setContextClassLoader( jmxClientCL );		
			
			Class clazz = jmxClientCL.loadClass("com.genband.m5.maps.mgmt.jmx.JMXUtility");
		    Method singletonM = clazz.getMethod("getInstance", null);
		    Object jmxAdaptor = singletonM.invoke(null, new Object[] {});
		    
		    log.debug(jmxAdaptor.getClass() + ", adaptorcl: " + jmxAdaptor.getClass().getClassLoader());
		    
		    Method mget = clazz.getMethod("get", new Class[] {String.class, String.class});
		    Object r = mget.invoke(jmxAdaptor, name, attributeNames);
		    
		    if (null != r)
		    	result = r.toString();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		finally {

	         Thread.currentThread().setContextClassLoader(oldCL);
		}
		
		return result;
	}
	
	private static ClassLoader createCL () {
		
		URLClassLoader cl = null;
		try {

			
			String JBOSS_HOME = System.getProperty("JBOSS_HOME");
			log.debug ("jboss home: " + JBOSS_HOME);

			String GB_HOME = System.getProperty("INSTALLROOT");
			log.debug ("gb home: " + GB_HOME);
			
			URL url_client = new URL("file:////" + JBOSS_HOME + "/client/jbossall-client.jar");
			URL url_naming = new URL("file:////" + GB_HOME + "/gbjars/gb-jmx-client.jar");
						
			cl = new URLClassLoader (new URL[] {url_naming, url_client});


			log.debug ("parent cl: " + cl.getParent());
			
			log.debug ("classloader: " + cl + ", archives: " + cl.getURLs().length);
			
			for (int j = 0; j < cl.getURLs().length; j++) {

				log.debug (cl.getURLs()[j]);	
			}
			
			return cl;
			
		} catch (Exception e) {
			log.error ("exception in creating jmx client classloader", e);
		}
		return cl;
	}
	
	private static final ClassLoader jmxClientCL = createCL ();
	
}

