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
//      File:   DeployAppGenerator.java
//
//      Desc:   This file defines code generator for application deployment operation
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  04/01/08        Initial Creation
//
//***********************************************************************************
                                                                                                                        
                                                                                                                        
package com.baypackets.ase.soa.codegenerator;
                                                                                                                        
import java.io.File;
import java.net.URI;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.lang.reflect.*;
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
import com.baypackets.ase.soa.util.JibxDataBinder;
import com.baypackets.ase.soa.util.SoaUtils;
import com.baypackets.ase.soa.util.XsdFilter;
import com.baypackets.ase.soa.util.RuntimeCompiler;
import org.apache.log4j.Logger;

public class DeployAppGenerator implements Generator  {
	private static Logger logger = Logger.getLogger(DeployAppGenerator.class);
	private WebServiceDataObject wsDataObject;
	private SoapServer soapServer;
	private JibxDataBinder jibxDataBinder ;
	private String local_suffix = "LocalProxy";
	private String client_suffix = "ClientProxy";
	private RuntimeCompiler rc = new RuntimeCompiler();
	public Map generate(String baseUrl,String wsdl,String wsName,
						String soaContextName,ClassLoader cl)
						throws CodeGenerationFailedException	{
		if (logger.isDebugEnabled()) {
        	logger.debug("Base URL: " + baseUrl);
        	logger.debug("WSDL : " + wsdl);
        	logger.debug("Application Name: " + wsName);
        	logger.debug("SOA Context Name`: " + soaContextName);
        }

		SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
		SoaContextImpl soaContext =  (SoaContextImpl)soaFw.getSoaContext(soaContextName);
		jibxDataBinder = new JibxDataBinder();
		wsDataObject = soaContext.getWebServiceDetail();
		//AseSoaService soaService =  wsDataObject.getService(wsName);
		AseSoaApplication soaApp =  wsDataObject.getApplication();
		if(soaApp == null)	{
			throw new CodeGenerationFailedException("Application data is not specified");
		}
		boolean success = false;
		HashMap<URI,Class> hashMap = soaApp.getListenerUriApi();
		Set<Map.Entry<URI,Class>> s =hashMap.entrySet();
		Iterator itr = s.iterator();
		URI uri = null;
		Class listenerInterface = null;
		while(itr.hasNext())	{
			Map.Entry<URI,Class> entry = (Map.Entry) itr.next();
			uri = entry.getKey();
			listenerInterface = entry.getValue();
		}
		String wsNotificationInterface = null;
		String listenerClassImpl = null;
		Class notInterfaceClass = listenerInterface;
		if(listenerInterface != null)	{
			wsNotificationInterface = listenerInterface.getName();
			if (logger.isDebugEnabled()) {
        		logger.debug("wsDataObject: " + wsDataObject);
        		logger.debug("Application Listener Interface: " + listenerInterface);
        	}
			listenerClassImpl = soaApp.getListenerImpl(wsNotificationInterface);
		}

		if(cl instanceof AppClassLoader)	{
			if(logger.isDebugEnabled())
			logger.debug("cl is instance of AppClassLoader");
		}
		AppClassLoader appClassLoader = (AppClassLoader)cl;	

		String srcdir = baseUrl+File.separatorChar+SoaConstants.JAVA_SOURCE_DIR_NAME; 
		String binDir = baseUrl+File.separatorChar+SoaConstants.JAVA_CLASS_DIR_NAME;
		List<File> classpaths = null;
	
		ConfigRepository configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
		String soapServerName = configRep.getValue(SoaConstants.NAME_SOAP_SERVER); 
		soapServer = SoapServerFactory.getSoapServer(soapServerName);
		Map map = new HashMap();
		if(notInterfaceClass != null)	{
        	boolean bindingsGenerated = false;
			if(wsdl != null) {
				//generate binding.xml and data objects from XSD file
        		bindingsGenerated = generateBindingsFromXsd(baseUrl);
        		if(bindingsGenerated)   {
					//create folder with name 'gen' inside baseUrl folder
					String genDir = baseUrl + File.separatorChar + "gen";
					File gen = new File(genDir);
					if(gen.exists() == false)	{
						gen.mkdir();
					}
            		//compile generated custom data objects
            		success = rc.compile(srcdir,binDir,classpaths);
            		if(!success)    {
                		throw new CodeGenerationFailedException("Error in compiling generated java source files from XSD");
            		}
            		//Get absolute path of binding.xml file generated by sxd2jibx tool
            		File gensrcDir = new File(srcdir);
            		File[] bindingFiles = SoaUtils.listFilesAsArray(gensrcDir,new XsdFilter(".xml"),true);
            		File f = null;
            		boolean b = false;
            		for(int j=0;j<bindingFiles.length; j++) {
                		f = bindingFiles[j];
                		if(logger.isDebugEnabled()) {
                    		logger.debug("Going to modify binding file: " + f);
                		}
                		b = SoaUtils.modifyBindings(baseUrl, f.getAbsolutePath(), "junk");
            			if(!b)  {
                			throw new CodeGenerationFailedException("Failed to modify xsd2jibx generated binding file");
            			}
            			compileBindings(baseUrl,wsName,wsNotificationInterface);
            		}
        		}
			}else	{
				wsName = wsNotificationInterface.substring(wsNotificationInterface.lastIndexOf(AseStrings.PERIOD)+1);
            	//Generate wsdl file from service java interface class
            	// and construct new URI of generated wsdl file
            	generateWsdl(baseUrl,wsName,wsNotificationInterface);
            	//wsdl = baseUrl +File.separatorChar + SoaConstants.WSDL_DIR_NAME
            	wsdl = baseUrl +File.separatorChar + "gen"
                    +File.separatorChar + wsName + ".wsdl";
			}

		String stubSkelArr[] = generateWsSkeleton(baseUrl,wsdl,wsName);
		String skelClassName =  stubSkelArr[1];
		/*
        * Once custom data objects are available and compiled
        * we will use JiBX to generate and compile databindings.
        *
        if(bindingsGenerated)   {
            compileBindings(baseUrl,wsName,wsNotificationInterface);
        }
		*/

		String clientLsnrProxy = generateClientListenerProxy(baseUrl,notInterfaceClass,uri);
		String localLsnrProxy = generateLocalListenerProxy(baseUrl,notInterfaceClass,listenerClassImpl);
		if (logger.isDebugEnabled()) {
        	logger.debug("skelClassName: " + skelClassName);
        	logger.debug("clientLsnrProxy: " + clientLsnrProxy);
        	logger.debug("localLsnrProxy: " + localLsnrProxy);
        }
		//Add WEB-INF/classes directory to classpath of RuntimeCompiler
		List<File> appClasspath = new ArrayList<File>();
        try {
            appClasspath.add(new File(binDir));
        }catch(Exception e) {
            logger.error("Application classes directory WEB-INF/classess do no exist: " + e);
            throw new CodeGenerationFailedException("Error in reading Application classes directory WEB-INF/classess");
        }

		//Modify generated skeleton source file
		try {
            modifySkeleton(baseUrl,skelClassName,clientLsnrProxy,binDir,appClassLoader,appClasspath);
        }catch(Exception modException)  {
            logger.error("Failed to modify generated skeleton source file: " + modException);
            throw new CodeGenerationFailedException("Failed to modify generated skeleton source file");
        }

		//Now compile generated proxy java source files
		try	{
			classpaths = SoaUtils.getClassPath(appClasspath);
		}catch(Exception exp)	{
			logger.error("Exception in setting classpath for RuntimeCompiler: " + exp);
			throw new CodeGenerationFailedException("Error in setting classpath for RuntimeCompiler");		
		}

		success = rc.compile(srcdir,binDir,classpaths);
		if(!success)	{
			throw new CodeGenerationFailedException("Error in building proxy java source files");		
		}
		//Compilation is successful,so create instances of proxies and add them to Map
		Object localListenerProxy = null;
		Object clntListenerProxy = null;
		Object skeleton = null;

		try	{
		 	localListenerProxy = Class.forName(localLsnrProxy,false,cl).newInstance();	
		 	clntListenerProxy = Class.forName(clientLsnrProxy,false,cl).newInstance();	
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
		//Add generated objects into Map
		map.put(SoaConstants.PROXY_CLIENT_LISTENER,clntListenerProxy);
		map.put(SoaConstants.PROXY_LOCAL_LISTENER,localListenerProxy);
		}
		return map;
	
	}
 
	private String[] generateWsSkeleton(String baseUrl,String wsdl,String wsName)
									throws CodeGenerationFailedException	{
		 return soapServer.generateWsdlToJava(baseUrl,wsdl,wsName,SoaConstants.GENERATE_SOAP_SKELETON);
	}
	
	private void generateWsdl(String baseUrl,String wsName,String wsInterface)
									throws CodeGenerationFailedException    {
		//Use jibx2wsdl to generate bindings, XSD and WSDL file
        //soapServer.generateJavaToWsdl(baseUrl,wsInterface,wsName);
        jibxDataBinder.generateBindings(baseUrl,wsInterface);
        jibxDataBinder.compileBindings(baseUrl,wsInterface);

	}

	private void compileBindings(String baseUrl,String wsName,String wsInterface)
                                    throws CodeGenerationFailedException    {
        jibxDataBinder.compileBindings(baseUrl,wsInterface);
    }
                                                                                                                                     
                                                                                                                                     
    private boolean generateBindingsFromXsd(String baseUrl) throws CodeGenerationFailedException    {
        boolean xsdFound = false;
        try {
            //get a list of all .xsd files located in wsdlDir directory
            File wsdlDir = new File(baseUrl + "/WEB-INF/wsdl");
            File[] xsdFiles = wsdlDir.listFiles(new XsdFilter(".xsd"));
            if(xsdFiles == null) {
                return xsdFound;
            }
            if(logger.isDebugEnabled())   {
                File temp = null;
                for(int i=0;i<xsdFiles.length; i++) {
                    temp = xsdFiles[i];
                    logger.debug("Absolute path of XSD file is: " + temp.getAbsolutePath());
                }
            }
            File f = null;
            String targetDir = baseUrl + "/src";
            for(int i=0;i<xsdFiles.length; i++) {
                f = xsdFiles[i];
                jibxDataBinder.generateBindingsFromXsd(targetDir,f.getAbsolutePath());
                xsdFound = true;
            }
        }catch(Exception e) {
            logger.error("failed to generate bindings from XSD",e);
            throw new CodeGenerationFailedException(e.getMessage());
        }
        return xsdFound;
                                                                                                                                     
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
        try {
            src = SoaUtils.readFileAsString(filePath);
        }catch(Exception exp)   {
            logger.error("Exception: " +exp);
                                                                                                                                      
        }
        if(logger.isDebugEnabled()) {
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
        if(logger.isDebugEnabled()) {
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


	private String generateClientListenerProxy(String baseUrl,Class wsNotificationInterface,URI uri)    {
        ClassInspector inspector;
        ClientListenerProxyGenerator clientLsnrProxyGenerator;
        inspector = new ClassInspector();
        try {
            inspector.parse(wsNotificationInterface);
            clientLsnrProxyGenerator = new ClientListenerProxyGenerator();
			Map jetMap = new HashMap();
            jetMap.put("ClassInspector",inspector);
            jetMap.put("URI",uri.toString());	
            String clientLsnrProxy = clientLsnrProxyGenerator.generate(jetMap);
            //Now write this generated java source code into .java file on disk
            SoaUtils.writeJavaFileToDisk(baseUrl,inspector,clientLsnrProxy,SoaConstants.NAME_CLIENT_PROXY_FILE_SUFFIX);
        } catch(Exception exp) {
            //log error here
        }
                                                                                                                                     
        return inspector.getAbsoluteClassName() + client_suffix;
    }

	 private String generateLocalListenerProxy(String baseUrl,Class wsNotificationInterface,String listenerImplClass)    {
        ClassInspector inspector;
        LocalListenerProxyGenerator localLsnrProxyGenerator;
		if (logger.isDebugEnabled()) {
            logger.debug("Base URL: " + baseUrl);
            logger.debug("Notification Interface Name: " + wsNotificationInterface);
        }

        inspector = new ClassInspector();
        try {
            inspector.parse(wsNotificationInterface);
            localLsnrProxyGenerator = new LocalListenerProxyGenerator();
			Map jetMap = new HashMap();
            jetMap.put("ClassInspector",inspector);
            jetMap.put("ImplClass",listenerImplClass);
			if (logger.isDebugEnabled()) {
                logger.debug("Invoking generate() method on LocalListenerProxyGenerator");
            }
            String localLsnrProxy = localLsnrProxyGenerator.generate(jetMap);
			if (logger.isDebugEnabled()) {
                logger.debug("String returned by LocalListenerProxyGenerator: "+localLsnrProxy);
            }

            //Now write this generated java source code into .java file on disk
            SoaUtils.writeJavaFileToDisk(baseUrl,inspector,localLsnrProxy,SoaConstants.NAME_LOCAL_PROXY_FILE_SUFFIX);
			if (logger.isDebugEnabled()) {
                logger.debug("Local Listenet Proxy java source file written to filesystem");
            }

        } catch(Exception exp) {
            //log error here
			logger.error("Exception in generating LocalListener proxy: " + exp);
        }catch(Throwable th)    {
            logger.error("Exception in generating LocalListener proxy: " + th);
        }
                                                                                                                              
        return inspector.getAbsoluteClassName() + local_suffix;
    }

		
}
