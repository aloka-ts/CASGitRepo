/**
 * Created on Dec 24, 2010
 */
package com.baypackets.ase.deployer;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.servlet.Servlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.annotation.SipApplication;
import javax.servlet.sip.annotation.SipApplicationKey;
import javax.servlet.sip.annotation.SipListener;
import javax.servlet.sip.annotation.SipServlet;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseContainer;
import com.baypackets.ase.container.AseAnnotationInfo;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.container.AseWrapper;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.container.exceptions.AseAnnotationProcessingException;

/**
 * The class AseAnnotationProcessor implements all the logic for processing
 * annotations in a SAS application
 * 
 */
public class AseAnnotationProcessor {

	private static Logger _logger = Logger
			.getLogger(AseAnnotationProcessor.class);
	private AseContext m_ctx;
	private Method sipAppKey;
	private ClassLoader clsLoader;	
	private String appNameFromDD;
	private Package sipAppAnnotatedPackage;
	private boolean isPackageAnnotationFound;

	AseAnnotationProcessor(AseContext context) {
		m_ctx = context;
		isPackageAnnotationFound = false;
		sipAppAnnotatedPackage = null;
		clsLoader=m_ctx.getClassLoader();
		appNameFromDD=m_ctx.getObjectName();
	}

	/**
	 * @param context
	 *            Application Context Object
	 */
	public void processAnnotation() throws AseAnnotationProcessingException {

		File classesDir = new File(m_ctx.getUnpackedDir(), "WEB-INF/classes");
		File jarDir=new File(m_ctx.getUnpackedDir(), "WEB-INF/lib");
		boolean isEnableLibAnnotation=m_ctx.isEnableLibAnnotation();
		File[] list = classesDir.listFiles();
		if(list != null) {
			for (int i = 0; i < list.length; i++) {
				scan(list[i]);
			}
		}
		if (isEnableLibAnnotation && jarDir != null) {
			File[] jarFiles = jarDir.listFiles();
			if (jarFiles != null) {
				for (File jarFile : jarFiles) {
					if (jarFile.getAbsolutePath().endsWith(".jar")) {
						processJar(jarFile.getAbsolutePath());
					}
				}
			}
		}

	}

	/**
	 * reads the @SipServlet Annotation from a class
	 * 
	 * @param context
	 * @param s_Annotation
	 */
	private void processServletAnnotation(Class<?> currClass,
			SipServlet s_Annotation) throws AseAnnotationProcessingException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Processing Servlet Annotation in the class:"+currClass.getCanonicalName());
		}
		
		String appName = s_Annotation.applicationName();

		// throw exception if different application name in DD and annotation
		if (appNameFromDD != null && appName != null && !appName.equals("") && !appName.equals(appNameFromDD) ) {
			throw new AseAnnotationProcessingException("Application Name in DD and annotation is different for the servlet");
		}
		
		// set application name
		if (appNameFromDD == null && appName != null && !appName.equals("")) {
			m_ctx.setObjectName(appName);
		}

		//throw exception if still no application name for servlet
		//[JSR289: servlet in other package has to specify the application name explicitly to be bound into the same application]
		if ((appNameFromDD == null || "".equals(appNameFromDD)) && 
				( appName == null || "".equals(appName) )
				&& !currClass.getPackage().equals(sipAppAnnotatedPackage))
			throw new AseAnnotationProcessingException("No Application Name given for the servlet");
		
		boolean existInDD = false;
		AseContainer[] children = m_ctx.findChildren();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				AseWrapper servlet = (AseWrapper) children[i];
				if(servlet != null && servlet.getServlet() != null) {
					if (servlet.getServlet().getClass().getCanonicalName().equals(
							currClass.getCanonicalName())) {
						existInDD = true;

						// servlet name must be set already

						// set loadOnStartup if required
						if (servlet.getLoadOnStartup() == null || servlet.getLoadOnStartup().intValue() < 0) {
							servlet.setLoadOnStartup(s_Annotation.loadOnStartup());
						}
						if (_logger.isDebugEnabled()) {
							_logger.debug("Servlet Data: "+ m_ctx.getObjectName() + " " +  servlet.getName() + " " +  servlet.getLoadOnStartup());
						}
						return;
					}
				}
			}
		}
		if (existInDD == false) {
			String servletName = s_Annotation.name();
			if(servletName == null || servletName.equals(""))
				servletName = currClass.getSimpleName();
			AseWrapper wrapper = new AseWrapper(servletName);

			// If this is the first Servlet in the descriptor, set
			// it as the default handler for the application.
			if (children == null || children.length == 0) {
				m_ctx.setDefaultHandlerName(s_Annotation.name());
			}

			m_ctx.addChild(wrapper);

			try {
				Servlet servlet = (Servlet) currClass.newInstance();
				wrapper.setServlet(servlet);
			} catch (Exception ex) {
				throw new AseAnnotationProcessingException(ex);
			}

			wrapper.setLoadOnStartup(s_Annotation.loadOnStartup());

			if (_logger.isDebugEnabled()) {
				_logger.debug("Servlet Data: "+ m_ctx.getObjectName() + " " +  wrapper.getName() + " " +  wrapper.getLoadOnStartup());
			}

		}

	}


	/**
	 * reads the @SipListener Annotation from a class
	 * 
	 * @param context
	 * @param l_Annotation
	 * @throws Exception
	 */
	private void processListenerAnnotation(Class<?> currClass,
			SipListener l_Annotation) throws AseAnnotationProcessingException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Processing Listener Annotation in the class:"+currClass.getCanonicalName());
		}
		
		String appName = l_Annotation.applicationName();
		
		// throw exception if different application name in DD and annotation
		if (appNameFromDD != null && appName != null
				&& !appName.equals("") && !appName.equals(appNameFromDD) ) {
			throw new AseAnnotationProcessingException(
					"Application Name in DD and annotation is different for the listener.");
		}

		// set application name
		if (appNameFromDD == null && appName != null && !appName.equals("")) {
			m_ctx.setObjectName(appName);
		}
		
		//throw exception if still no application name for listener
		//[JSR289: listener in other package has to specify the application name explicitly to be bound into the same application]
		if ((appNameFromDD == null || "".equals(appNameFromDD)) && 
				( appName == null || "".equals(appName) )
				&& !currClass.getPackage().equals(sipAppAnnotatedPackage)) 
			throw new AseAnnotationProcessingException("No Application Name given for the listener");		
		
		Iterator listeners = m_ctx.getListeners(EventListener.class).iterator();
		if (listeners != null) {
			for (; listeners.hasNext();) {
				EventListener listener = (EventListener) listeners.next();
				if (listener != null) {
					if (listener.getClass().getCanonicalName().equals(
							currClass.getCanonicalName())) {
						return;
						}
					}
				}
			}

		try {
			m_ctx.addListener(currClass);
		} catch (Exception ex) {
			throw new AseAnnotationProcessingException(ex);
		}
		if (_logger.isDebugEnabled()) {
			_logger.debug("Listener added to application: "+ m_ctx.getObjectName());
		}
	}

	/**
	 * reads the @Application Annotation from a class
	 * 
	 * @param context
	 * @param a_Annotation
	 */
	private void processApplicationAnnotation(SipApplication a_Annotation)
			throws AseAnnotationProcessingException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Processing Application Annotation in the class.");
		}
		
		String appName = a_Annotation.name();
		
		// throw exception if different application name in DD and annotation
		if (appNameFromDD != null && appName != null
				&& !appName.equals("") && !appName.equals(appNameFromDD) ) {
			throw new AseAnnotationProcessingException(
					"Name in DD and annotation is different for this application");
		}
		
		// set application name
		if (appNameFromDD == null && appName != null && !appName.equals(""))
			m_ctx.setObjectName(appName);
	
		//throw exception if still no application name for servlet
		if (m_ctx.getObjectName() == null || m_ctx.getObjectName().equals(""))
			throw new AseAnnotationProcessingException("No Name given for this application");
		
		// throw exception if different main servlet in DD and annotation
		if (m_ctx.getMainServlet() != null && a_Annotation.mainServlet() != null 
				&& !"".equals(a_Annotation.mainServlet())
				&& !(m_ctx.getMainServlet().equals(a_Annotation.mainServlet())))
			throw new AseAnnotationProcessingException(
					"Main Servlet for this application is different in DD and annotation");

		//throw exception if main servlet in annotation and servlet-mapping in DD
		if (m_ctx.isServletMapPresent() && 
				( (m_ctx.getMainServlet() != null && !"".equals(m_ctx.getMainServlet())) 
						|| ( a_Annotation.mainServlet() != null && !"".equals(a_Annotation.mainServlet()) ) ) )
			throw new AseAnnotationProcessingException(
					"Main Servlet and servlet-mapping cannot co-exist");
		
		if (m_ctx.getDescription() == null)
			m_ctx.setDescription(a_Annotation.description());

		if (m_ctx.getServletContextName() == null)
			m_ctx.setDisplayName(a_Annotation.displayName());
		if (m_ctx.getServletContextName() == null || m_ctx.getServletContextName().equals(""))
			m_ctx.setDisplayName(m_ctx.getObjectName());

		if (m_ctx.getLargeIcon() == null)
			m_ctx.setLargeIcon(a_Annotation.largeIcon());

		if (m_ctx.getSmallIcon() == null)
			m_ctx.setSmallIcon(a_Annotation.smallIcon());

		if (m_ctx.getMainServlet() == null)
			m_ctx.setMainServlet(a_Annotation.mainServlet());
		

		if (m_ctx.isDistributable() == false)
			m_ctx.setDistributable(a_Annotation.distributable());

		if (m_ctx.getAppSessionTimeout() == Constants.DEFAULT_SESSION_TIMEOUT)
			m_ctx.setAppSessionTimeout(a_Annotation.sessionTimeout());

		if (m_ctx.getSequentialSearchTimeout() == 0)
			m_ctx.setSequentialSearchTimeout(a_Annotation.proxyTimeout());
		
		if (_logger.isDebugEnabled()) {
			_logger.debug("Annotation Read. Application: "+ m_ctx.getObjectName() + ". Display Name: " +  m_ctx.getServletContextName());
		}
	}

	/**
	 * reads the @SipApplicationKey Annotation from a class
	 * 
	 * @param currClass
	 */
	private void processApplicationKeyAnnotation(Class<?> currClass)
			throws AseAnnotationProcessingException {

		try {
			Method[] methods = currClass.getMethods();
			for (Method method : methods) {
				SipApplicationKey keyAnnotation = method.getAnnotation(SipApplicationKey.class);
				if (keyAnnotation != null) {
					if (_logger.isDebugEnabled()) {
						_logger.debug("Processing ApplicationKey Annotation in the class:"+currClass.getCanonicalName());
					}
					
					String appName = keyAnnotation.applicationName();
					
					// throw exception if different application name in DD and annotation
					if (appNameFromDD != null && appName != null
							&& !appName.equals("") && !appName.equals(appNameFromDD) ) {
						throw new AseAnnotationProcessingException(
								"Application Name in DD and annotation is different");
					}
					
					// set application name
					if (appNameFromDD == null && appName != null && !appName.equals(""))
						m_ctx.setObjectName(appName);
					
					//throw exception if still no application name for key annotation
					if ((appNameFromDD == null || "".equals(appNameFromDD)) && 
							( appName == null || "".equals(appName) )
							&& !currClass.getPackage().equals(sipAppAnnotatedPackage))
						throw new AseAnnotationProcessingException("No Application Name given for the SipApplicationKey Method");
				
					
					if (!Modifier.isStatic(method.getModifiers())
							|| !Modifier.isPublic(method.getModifiers())) {
						throw new AseAnnotationProcessingException(
								"A method annotated with the @SipApplicationKey annotation MUST be public and static");
					}
					if (!method.getGenericReturnType().equals(String.class)) {
						throw new AseAnnotationProcessingException(
								"A method annotated with the @SipApplicationKey annotation MUST return a String");
					}
					Type[] types = method.getGenericParameterTypes();
					if (types.length != 1
							|| !types[0].equals(SipServletRequest.class)) {
						throw new AseAnnotationProcessingException(
								"A method annotated with the @SipApplicationKey annotation MUST have a single argument of type SipServletRequest");
					}
					if (this.sipAppKey != null) {
						throw new IllegalStateException(
								"More than one SipApplicationKey annotated method is not allowed.");
					}
					this.sipAppKey = method;
					if (_logger.isDebugEnabled()) {
						_logger
								.debug("the following @SipApplicationKey annotated method has been found "
										+ method.toString());
					}

					AseAnnotationInfo annotationInfo = new AseAnnotationInfo();
					annotationInfo.setAnnotatedClass(currClass);
					annotationInfo.setMethodName(method.getName());
					m_ctx.setApplicationKeyAnnoInfo(annotationInfo);
					break;
				}
			}
		} catch (Exception e) {
			throw new AseAnnotationProcessingException(e.getMessage());
		}
	}

	private void scan(File folder) throws AseAnnotationProcessingException {
		File[] files = folder.listFiles();
		if (files != null) {
			for (int j = 0; j < files.length; j++) {
				if (files[j].isDirectory()) {
					scan(files[j]);
				 } else {
					 String path=files[j].getAbsolutePath();
					 if(path.endsWith(".class"))
						 processClass(path);
				}
			}
		}
	}

	private void processClass(String file)
			throws AseAnnotationProcessingException {
		if (_logger.isDebugEnabled()) {
			_logger.debug("Check File : " + file);
		}

		int classesIndex = file.toLowerCase().lastIndexOf("classes/");
		if (classesIndex < 0)
			classesIndex = file.toLowerCase().lastIndexOf("classes\\");
		classesIndex += "classes/".length();
		String classpath = file.substring(classesIndex);
		classpath = classpath.replace('/',AseStrings.CHAR_DOT).replace('\\', AseStrings.CHAR_DOT);
		if (classpath.endsWith(".class"))
			classpath = classpath.substring(0, classpath.length() - 6);
		if (classpath.startsWith(AseStrings.PERIOD))
			classpath = classpath.substring(1);
		if (_logger.isDebugEnabled()) {
			_logger.debug("Class to be checked for Annotations: " + classpath);
		}

		if (classpath == null)
			return;

		Class<?> classFile=null;
		try {
			classFile = Class.forName(classpath, false, clsLoader);
		} catch (ClassNotFoundException e) {
			if (_logger.isDebugEnabled()) {
				_logger.debug("ClassNotFound Exception for: " + classpath);
			}
		}

		if (classFile == null)
			return;

		checkClassForAnnotations(classFile);
	}

	private void checkClassForAnnotations(Class<?> classFile)
			throws AseAnnotationProcessingException {
		
		// process @SipApplication annotation
		// This condition processes the SipApplicationAnnotation only if it
		// has not found package annotation in that package
		// No need to check every class file for SipApplicationAnnotation in
		// a package as this annotation can only be defined in
		// package-info.java file
		if (!isPackageAnnotationFound
				|| (isPackageAnnotationFound && !sipAppAnnotatedPackage
						.equals(classFile.getPackage()))) {
			Annotation[] annotations = classFile.getPackage().getAnnotations();
			for (Annotation pkgAnnotation : annotations) {
				if (pkgAnnotation instanceof SipApplication) {
					SipApplication applicationAnnotation = (SipApplication) pkgAnnotation;
					// Exception is thrown if another SipApplication
					// annotation is found in another package
					if (isPackageAnnotationFound) {
						throw new AseAnnotationProcessingException(
								"Only one SipApplication annotation can be defined in one .sar or .war file");
					}
					if (_logger.isDebugEnabled()) {
						_logger.debug("Package Annotation for application: "+ applicationAnnotation.name());
					}
					isPackageAnnotationFound = true;
					sipAppAnnotatedPackage = classFile.getPackage();
					processApplicationAnnotation(applicationAnnotation);
				}
			}
		}
		
		try {			
			// process class level (@SipServlet and @SipListener) annotations
			Annotation[] annotations = classFile.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation instanceof SipServlet) {
					SipServlet servletAnnotation = (SipServlet) annotation;
					if ((javax.servlet.sip.SipServlet.class)
							.isAssignableFrom(classFile))
						processServletAnnotation(classFile, servletAnnotation);
				} else if (annotation instanceof SipListener) {
					SipListener listenerAnnotation = (SipListener) annotation;
					if ((java.util.EventListener.class)
							.isAssignableFrom(classFile))
						processListenerAnnotation(classFile, listenerAnnotation);
				}
			}

			// process @SipApplicationKey annotation
			processApplicationKeyAnnotation(classFile);

		} catch (Exception ex) {
			throw new AseAnnotationProcessingException(ex);
		}
	}
	
	private void processJar(String path) throws AseAnnotationProcessingException {
		if(_logger.isDebugEnabled()) {
    		_logger.debug("scanning jar " + path + " for annotations");
    	}
		try {
			JarFile jar =new JarFile(path);
			Enumeration<JarEntry> jarEntries = jar.entries();
			while (jarEntries.hasMoreElements()) {
				JarEntry jarEntry = jarEntries.nextElement();
				String entryName = jarEntry.getName();
								
				if(entryName.endsWith(".class")) {
					String className =  entryName.substring(0, entryName.indexOf(".class"));
					className = className.replace('/', '.');
					className = className.replace('\\', '.');
					try {
		    	    	Class<?> clazz = Class.forName(className, false, clsLoader);
		    	    	checkClassForAnnotations(clazz);
		    		} catch (Throwable e) {
		    			_logger.debug("Failed to parse annotations for class " + className);
		    			if(_logger.isDebugEnabled()) {
		    				_logger.debug("Failed to parse annotations for class " + className, e);
		    				throw new AseAnnotationProcessingException(e.getMessage());
		    			}
		    		}
				}
			}
		} catch (IOException e) {
			throw new AseAnnotationProcessingException("couldn't read the following jar file for parsing annotations " + path, e);
		} 
	}
	
}
