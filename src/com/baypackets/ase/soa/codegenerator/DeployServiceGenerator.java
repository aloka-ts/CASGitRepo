//***********************************************************************************
//GENBAND, Inc. Confidential and Proprietary
//
//This work contains valuable confidential and proprietary
//information.
//Disclosure, use or reproduction without the written authorization of
//GENBAND, Inc. is prohibited. This unpublished work by GENBAND, Inc.
//is protected by laws of United States and other countries.
//If publication of work should occur the following notice shall
//apply:
//
//"Copyright 2007 GENBAND, Inc. All right reserved."
//***********************************************************************************
                                                                                                                        
                                                                                                                        
//***********************************************************************************
//
//      File:   DeployServiceGenerator.java
//
//      Desc:   This file defines code generator for service deployment operation
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  04/01/08        Initial Creation
//
//***********************************************************************************
                                                                                                                        
                                                                                                                        
package com.baypackets.ase.soa.codegenerator;
                                                                                                                        
import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.lang.reflect.*;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AppClassLoader;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.soa.iface.SoaContext;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;
import com.baypackets.ase.soa.codegenerator.exceptions.CodeGenerationFailedException;
import com.baypackets.ase.soa.common.*;
import com.baypackets.ase.soa.SoaContextImpl;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.soa.util.SoaUtils;
import com.baypackets.ase.soa.util.JibxDataBinder;
import com.baypackets.ase.soa.util.RuntimeCompiler;
import org.apache.log4j.Logger;

public class DeployServiceGenerator implements Generator  {
	private static Logger logger = Logger.getLogger(DeployServiceGenerator.class);
	private WebServiceDataObject wsDataObject;
	private SoapServer soapServer;
	private JibxDataBinder jibxDataBinder ;
	private RuntimeCompiler rc = null;
	private String suffix = "Proxy";
	private String remote_suffix = "RemoteProxy";
	private String client_suffix = "ClientProxy";
	public Map generate(String baseUrl,String wsdl,String wsName,
						String soaContextName,ClassLoader cl)
						throws CodeGenerationFailedException	{
		if (logger.isDebugEnabled()) {
        	logger.debug("Base URL: " + baseUrl);
        	logger.debug("WSDL : " + wsdl);
        	logger.debug("Service Name: " + wsName);
        	logger.debug("SOA Context Name`: " + soaContextName);
        	logger.debug("Setting wsdl to empty string to force jibx generate new WSDL and bindings");
        }
		wsdl = "";

		SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
		SoaContextImpl soaContext =  (SoaContextImpl)soaFw.getSoaContext(soaContextName);
		Object deployableObject = soaContext.getDeployableObject();
		AbstractDeployableObject deployable = null;
		if(deployableObject != null)	{
			deployable = (AbstractDeployableObject)deployableObject;
		}
		jibxDataBinder = new JibxDataBinder();
		rc = new RuntimeCompiler();
		wsDataObject = soaContext.getWebServiceDetail();
		AseSoaService soaService =  wsDataObject.getService(wsName);
		String wsInterface = soaService.getServiceApi();
		String wsNotificationInterface = soaService.getNotificationApi();
		String wsImplClassName = soaService.getImplClassName();
		if (logger.isDebugEnabled()) {
        	logger.debug("wsDataObject: " + wsDataObject);
        	logger.debug("Service Interface: " + wsInterface);
        	logger.debug("Service Notification Interface: " + wsNotificationInterface);
        	logger.debug("Service Impl class: " + wsImplClassName);
        }

		Class svcInterfaceClass = null;
		Class notInterfaceClass = null;
		if(cl instanceof AppClassLoader)	{
			if(logger.isDebugEnabled())
			logger.debug("cl is instance of AppClassLoader");
		}
		AppClassLoader appClassLoader = (AppClassLoader)cl;	
		try	{
			if(logger.isDebugEnabled())
			logger.debug("Invoke findClass() on AppClassLoader for Service Interface: "+wsInterface);
			svcInterfaceClass = ((AppClassLoader)cl).findClass(wsInterface.trim());
			if(logger.isDebugEnabled())
			logger.debug("Service Interface class name: "+svcInterfaceClass.getName());
			if(wsNotificationInterface != null)	{
			if(logger.isDebugEnabled())
				logger.debug("Invoke findClass() on AppClassLoader for Notification Interface: "+wsNotificationInterface);
				notInterfaceClass = ((AppClassLoader)cl).findClass(wsNotificationInterface.trim());
				if(logger.isDebugEnabled())
				logger.debug("Notification Interface class name: "+notInterfaceClass.getName());
			}
		}catch(Exception exp)	{
			logger.error("Failed to get Interface classes from app classloader");
			logger.error("Exception is: "+exp);
		}	
		

		String srcdir = baseUrl+File.separatorChar+SoaConstants.JAVA_SOURCE_DIR_NAME; 
		String binDir = baseUrl+File.separatorChar+SoaConstants.JAVA_CLASS_DIR_NAME;
		List<File> classpaths = null;
	
		ConfigRepository configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String soapServerName = configRep.getValue(SoaConstants.NAME_SOAP_SERVER); 
		soapServer = SoapServerFactory.getSoapServer(soapServerName);
		if((wsdl == null) || (wsdl.trim().length() == 0))	{
			//Generate wsdl file from service java interface class
			// and construct new URI of generated wsdl file
			wsName = wsInterface.substring(wsInterface.lastIndexOf(AseStrings.PERIOD)+1);
			generateWsdl(baseUrl,wsName,wsInterface);
			//wsdl = baseUrl +File.separatorChar + SoaConstants.WSDL_DIR_NAME 
			wsdl = baseUrl +File.separatorChar + "gen" 
					+File.separatorChar + wsName + ".wsdl";
		}
		String stubSkelArr[] = generateWsSkeleton(baseUrl,wsdl,wsName);
		//String stubClassName =  stubSkelArr[0];
		String stubClassName =  null;
		String skelClassName =  stubSkelArr[1];
		String svcProxy = generateServiceProxy(baseUrl,svcInterfaceClass,wsImplClassName);
		String remoteLsnrProxy = null;
		String clientLsnrProxy = null;
		if(wsNotificationInterface != null) {
			wsName = wsNotificationInterface.substring(wsNotificationInterface.lastIndexOf(AseStrings.PERIOD)+1);
			generateWsdl(baseUrl,wsName,wsNotificationInterface);
			//wsdl = baseUrl +File.separatorChar + SoaConstants.WSDL_DIR_NAME 
			wsdl = baseUrl +File.separatorChar + "gen" 
					+File.separatorChar + wsName + ".wsdl";
			deployable.setListenerWsdlPath(wsdl);
			String stubArr[] = generateWsStub(baseUrl,wsdl,wsName);
			stubClassName =  stubArr[0];
			remoteLsnrProxy = generateRemoteListenerProxy(baseUrl,notInterfaceClass,stubClassName);
			clientLsnrProxy = generateClientListenerProxy(baseUrl,notInterfaceClass);
		}
		if (logger.isDebugEnabled()) {
        	logger.debug("stubClassName: " + stubClassName);
        	logger.debug("skelClassName: " + skelClassName);
        	logger.debug("svcProxy: " + svcProxy);
        	logger.debug("remoteLsnrProxy: " + remoteLsnrProxy);
        	logger.debug("clientLsnrProxy: " + clientLsnrProxy);
        }
		//Add WEB-INF/classes directory to classpath of RuntimeCompiler
		List<File> appClasspath = new ArrayList<File>();
		try	{
			appClasspath.add(new File(binDir));
		}catch(Exception e)	{
			logger.error("Application classes directory WEB-INF/classess do no exist: " + e);
            throw new CodeGenerationFailedException("Error in reading Application classes directory WEB-INF/classess");
		}

		//Add otherjars and bpjars to classpath of RuntimeCompiler
		try	{
			classpaths = SoaUtils.getClassPath(appClasspath);
		}catch(Exception exp)	{
			logger.error("Exception in setting classpath for RuntimeCompiler: " + exp);
			throw new CodeGenerationFailedException("Error in setting classpath for RuntimeCompiler");		
		}

		//Modify generated skeleton source file
		try	{
			//SoaUtils.modifySkeleton(baseUrl,skelClassName,svcProxy);
			modifySkeleton(baseUrl,skelClassName,svcProxy,binDir,appClassLoader,classpaths);
		}catch(Exception modException)	{
			logger.error("Failed to modify generated skeleton source file: " + modException);
            throw new CodeGenerationFailedException("Failed to modify generated skeleton source file");
		}

		//Now compile generated proxy java source files
		boolean success = rc.compile(srcdir,binDir,classpaths);
		if(!success)	{
			throw new CodeGenerationFailedException("Error in building proxy java source files");		
		}
		//Compilation is successful,so create instances of proxies and add them to Map
		Map map = new HashMap();
		Object serviceProxy = null;
		Object remListenerProxy = null;
		Object clntListenerProxy = null;
		//Object stub = null;
		Class stub = null;
		Object skeleton = null;

		try	{
		 	serviceProxy = Class.forName(svcProxy,false,cl).newInstance();
			if(wsNotificationInterface != null) {	
		 		remListenerProxy = Class.forName(remoteLsnrProxy,false,cl).newInstance();	
		 		clntListenerProxy = Class.forName(clientLsnrProxy,false,cl).newInstance();	
		 		stub = Class.forName(stubClassName,false,cl);	
			}
		 	skeleton = Class.forName(skelClassName,false,cl).newInstance();	
		}catch(ClassNotFoundException clsExp)	{
			logger.error("ClassNotFoundException: " +clsExp);
			throw new CodeGenerationFailedException(clsExp.getMessage());		
		}catch(InstantiationException insExp)	{
			logger.error("InstantiationException: " +insExp);
			throw new CodeGenerationFailedException(insExp.getMessage());		
		}catch(IllegalAccessException illException)	{
			logger.error("IllegalAccessException: " +illException);
			throw new CodeGenerationFailedException(illException.getMessage());		
		}
		/*
		if(wsNotificationInterface != null) {
			//set Stub instance reference into remote listener proxy
			try	{
				Class c = remListenerProxy.getClass();
    			Class[] parameterTypes = new Class[] { stub.getClass() };
    			Method setStub;
    			Object[] arguments = new Object[] { stub };
      			setStub = c.getMethod("setStub", parameterTypes);
      			setStub.invoke(remListenerProxy,arguments);
    		} catch (NoSuchMethodException e) {
				logger.error("NoSuchMethodException: " +e);
    		} catch (IllegalAccessException e) {
				logger.error("IllegalAccessException: " +e);
    		} catch (InvocationTargetException e) {
				logger.error("InvocationTargetException: " +e);
			}catch(Exception e)	{
				logger.error("Exception: " +e);

			}catch(Throwable th)	{
				logger.error("Throwable Exception: " +th);

			}
		}
		*/

		map.put(SoaConstants.PROXY_SERVICE,serviceProxy);
		if(wsNotificationInterface != null) {
			map.put(SoaConstants.PROXY_REMOTE_LISTENER,remListenerProxy);
			map.put(SoaConstants.PROXY_CLIENT_LISTENER,clntListenerProxy);
		}
		return map;
	
	}
 
	private String[] generateWsSkeleton(String baseUrl,String wsdl,String wsName)
									throws CodeGenerationFailedException	{
		 return soapServer.generateWsdlToJava(baseUrl,wsdl,wsName,SoaConstants.GENERATE_SOAP_SKELETON);
	}
	
	private String[] generateWsStub(String baseUrl,String wsdl,String wsName)
									throws CodeGenerationFailedException	{
		 return soapServer.generateWsdlToJava(baseUrl,wsdl,wsName,SoaConstants.GENERATE_SOAP_STUB);
	}
	private void generateWsdl(String baseUrl,String wsName,String wsInterface)
									throws CodeGenerationFailedException    {
		//Use jibx2wsdl to generate bindings, XSD and WSDL file
		//soapServer.generateJavaToWsdl(baseUrl,wsInterface,wsName);
		jibxDataBinder.generateBindings(baseUrl,wsInterface);
		jibxDataBinder.compileBindings(baseUrl,wsInterface);
	}

	 private void modifySkeleton(String baseDir, String skeleton,String svcProxy,
                                 String outputDir,ClassLoader loader,List<File> classpaths) throws Exception {
		String src = null;
		Class skeletonClass = null;
		String proxyClassName = svcProxy;
        String filePath = baseDir + File.separatorChar + "src" + File.separatorChar
                            + skeleton.replace('.', File.separatorChar) + ".java";
        if(logger.isDebugEnabled()) {
			logger.debug("baseDir: " +baseDir );
			logger.debug("skeleton: " +skeleton );
			logger.debug("loader: " +loader );
			logger.debug("classpaths: " +classpaths );
            logger.debug("Original Skeleton file path: " + filePath);
        }
		try	{
			src = SoaUtils.readFileAsString(filePath);
		}catch(Exception exp)	{
			logger.error("Exception: " +exp);

		}
		if(logger.isDebugEnabled())	{
			logger.debug("Original generated Skeleton java source: " + src);
		}
		boolean success = rc.compileJavaSourceFile(skeleton,src,outputDir,classpaths);
        if(!success)    {
            throw new CodeGenerationFailedException("Error in compiling generated skeleton");
        }
        //Now create an instance of Skeleton class 
        try {
            skeletonClass = Class.forName(skeleton,true,loader);
        }catch(Exception exp)   {
            logger.error("Exception: " + exp);
            throw new CodeGenerationFailedException("Failed to load skeleton class file");
        }catch(Throwable th)    {
            logger.error("Exception: " + th);
            throw new CodeGenerationFailedException("Failed to load skeleton class file");
                                                                                                                           
        }
		if(logger.isDebugEnabled())	{
			logger.debug("Package for instance of Skeleton class: " + skeletonClass.getPackage());
			logger.debug("Class Name for instance of Skeleton class: " + skeletonClass.getName());
			logger.debug("Package for instance of Skeleton class: " + skeletonClass.getPackage().getName());
		}
		ClassInspector inspector;
        SkeletonModifierGenerator skelModifier;
        inspector = new ClassInspector();
        try {
            inspector.parse(skeletonClass);
			if(logger.isDebugEnabled()) {
           	 	logger.debug("Package returned by Inspector: " + inspector.getPackageName());
            	logger.debug("Class Name returned by Inspector: " + inspector.getClassName());
        	}
            skelModifier = new SkeletonModifierGenerator();
            Map jetMap = new HashMap();
            jetMap.put("ClassInspector",inspector);
            jetMap.put("ProxyClass",proxyClassName);
            String modifiedSkel = skelModifier.generate(jetMap);
            //Now write this generated java source code into .java file on disk
            SoaUtils.writeJavaFileToDisk(baseDir,inspector,modifiedSkel,".java");
        } catch(Exception exp) {
           logger.error("Error in modifying generated skeleton: " + exp);
			throw new CodeGenerationFailedException("Failed to modify generated skeleton"); 
        }


			
	}

	private String generateServiceProxy(String baseUrl,Class wsInterface,String svcImplClass)	{
		ClassInspector inspector;
		ServiceProxyGenerator svcProxyGenerator;
		if (logger.isDebugEnabled()) {
        	logger.debug("Base URL: " + baseUrl);
        	logger.debug("Service Interface Name: " + wsInterface);
        }
		inspector = new ClassInspector();
		try {
			inspector.parse(wsInterface);
			svcProxyGenerator = new ServiceProxyGenerator();
			if (logger.isDebugEnabled()) {
            	logger.debug("Invoking generate() method on ServiceProxyGenerator");
			}
			Map jetMap = new HashMap();
            jetMap.put("ClassInspector",inspector);
            jetMap.put("ImplClass",svcImplClass);

			String svcProxy = svcProxyGenerator.generate(jetMap);
			if (logger.isDebugEnabled()) {
            	logger.debug("String returned by ServiceProxyGenerator: "+svcProxy);
			}
			//Now write this generated java source code into .java file on disk
			SoaUtils.writeJavaFileToDisk(baseUrl,inspector,svcProxy,SoaConstants.NAME_PROXY_FILE_SUFFIX);
			if (logger.isDebugEnabled()) {
            	logger.debug("Service Proxy java source file written to filesystem");
			}
		} catch(Exception exp) {
        	logger.error("Exception in generating service proxy: " + exp);
		}catch(Throwable th)	{
			logger.error("Exception in generating service proxy: " + th);
		}

		return inspector.getAbsoluteClassName() + suffix;
	}

	private String generateRemoteListenerProxy(String baseUrl,Class wsNotificationInterface,String stubClass)	{
		ClassInspector inspector;
		RemoteListenerProxyGenerator lsnrProxyGenerator;
		inspector = new ClassInspector();
		try {
			inspector.parse(wsNotificationInterface);
			lsnrProxyGenerator = new RemoteListenerProxyGenerator();
			Map jetMap = new HashMap();
			jetMap.put("ClassInspector",inspector);
			jetMap.put("StubClass",stubClass);
			String lsnrProxy = lsnrProxyGenerator.generate(jetMap);
			//Now write this generated java source code into .java file on disk
			SoaUtils.writeJavaFileToDisk(baseUrl,inspector,lsnrProxy,SoaConstants.NAME_REMOTE_PROXY_FILE_NAME_SUFFIX);
		} catch(Exception exp) {
			//log error here	
		}

		return inspector.getAbsoluteClassName() + remote_suffix;
	}

	private String generateClientListenerProxy(String baseUrl,Class wsNotificationInterface)	{
		ClassInspector inspector;
		ClientListenerProxyGenerator clientLsnrProxyGenerator;
		inspector = new ClassInspector();
		try {
			inspector.parse(wsNotificationInterface);
			clientLsnrProxyGenerator = new ClientListenerProxyGenerator();
			Map jetMap = new HashMap();
            jetMap.put("ClassInspector",inspector);

			String clientLsnrProxy = clientLsnrProxyGenerator.generate(jetMap);
			//Now write this generated java source code into .java file on disk
			SoaUtils.writeJavaFileToDisk(baseUrl,inspector,clientLsnrProxy,SoaConstants.NAME_CLIENT_PROXY_FILE_SUFFIX);
		} catch(Exception exp) {
			//log error here	
		}

		return inspector.getAbsoluteClassName() + client_suffix;
	}
		
}
