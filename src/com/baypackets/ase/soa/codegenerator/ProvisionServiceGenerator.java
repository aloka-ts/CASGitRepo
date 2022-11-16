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
//      File:   ProvisionServiceGenerator.java
//
//      Desc:   This file defines code generator for service provisioning operation
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
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AppClassLoader;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.ase.soa.iface.SoaContext;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;
import com.baypackets.ase.soa.codegenerator.exceptions.CodeGenerationFailedException;
import com.baypackets.ase.soa.common.*;
import com.baypackets.ase.soa.SoaContextImpl;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.soa.util.SoaUtils;
import com.baypackets.ase.soa.util.XsdFilter;
import com.baypackets.ase.soa.util.RuntimeCompiler;
import com.baypackets.ase.soa.util.JibxDataBinder;
import com.baypackets.ase.soa.codegenerator.proxy.BaseProxy;
import org.apache.log4j.Logger;

public class ProvisionServiceGenerator implements Generator  {
	private static Logger logger = Logger.getLogger(ProvisionServiceGenerator.class);
	private WebServiceDataObject wsDataObject;
	private JibxDataBinder jibxDataBinder ;
	private RuntimeCompiler rc = null;
	private SoapServer soapServer;
	private String remote_suffix = "RemoteProxy";

	public Map generate(String baseUrl,String wsdl,String wsName,
						String soaContextName,ClassLoader cl)
						throws CodeGenerationFailedException	{
		if (logger.isDebugEnabled()) {
        	logger.debug("Base URL: " + baseUrl);
        	logger.debug("WSDL : " + wsdl);
        	logger.debug("Service Name: " + wsName);
        	logger.debug("SOA Context Name`: " + soaContextName);
        }
		if((wsdl == null) || (wsdl.trim().length() == 0))	{
			throw new CodeGenerationFailedException("No WSDL has been specified");
		}

		SoaFrameworkContext soaFw = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);
		SoaContextImpl soaContext =  (SoaContextImpl)soaFw.getSoaContext(soaContextName);
		jibxDataBinder = new JibxDataBinder();
        rc = new RuntimeCompiler();
		wsDataObject = soaContext.getWebServiceDetail();
		AseSoaService soaService =  wsDataObject.getService(wsName);
		String wsInterface = soaService.getServiceApi();                                                             
		String uri = soaService.getServiceUri();                                                             
		//String wsNotificationInterface = soaService.getNotificationApi();
		if (logger.isDebugEnabled()) {
        	logger.debug("wsDataObject: " + wsDataObject);
        	logger.debug("Service Interface: " + wsInterface);
        	logger.debug("Service URI: " + uri);
        }

		/*If service interface is not specified in soa.xml or WSDL file
		* 1. Invoke AxisSoaperver.generateWsdlToJava() to generate interface & stub.
		* 2. Compile interface and Stub java source files.
		* 3. Invoke findClass() on AppClassLoader to get Class for service interface
		* 4. Use ClassInspector/Reflection on interface Class to generate Remote Proxy.
		*/
		String srcdir = baseUrl+File.separatorChar+SoaConstants.JAVA_SOURCE_DIR_NAME;
        String binDir = baseUrl+File.separatorChar+SoaConstants.JAVA_CLASS_DIR_NAME;
		String stubClassName = null;
		String interfaceClassName = null;
        List<File> classpaths = null;
       	List<File> appClasspath = new ArrayList<File>();
		boolean success = false;
        try {
        	appClasspath.add(new File(binDir));
        }catch(Exception e) {
        	logger.error("Application classes directory WEB-INF/classess do no exist: " + e);
        	throw new CodeGenerationFailedException("Error in reading Application classes directory WEB-INF/classess");
        }
        try {
        	classpaths = SoaUtils.getClassPath(appClasspath);
        }catch(Exception exp)   {
        	logger.error("Exception in setting classpath for RuntimeCompiler: " + exp);
        	throw new CodeGenerationFailedException("Error in setting classpath for RuntimeCompiler");
        }
        
        ConfigRepository configRep = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);
        String soapServerName = configRep.getValue(SoaConstants.NAME_SOAP_SERVER);
        soapServer = SoapServerFactory.getSoapServer(soapServerName);
		//generate binding.xml and data objects from XSD file
		boolean bindingsGenerated = generateBindingsFromXsd(baseUrl);
		if(bindingsGenerated)	{
			//create folder with name 'gen' inside baseUrl folder
        	 String genDir = baseUrl + File.separatorChar + "gen";
             File gen = new File(genDir);
             if(gen.exists() == false)   {
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
			for(int j=0;j<bindingFiles.length; j++)	{
				f = bindingFiles[j];
				if(logger.isDebugEnabled())	{
					logger.debug("Going to modify binding file: " + f);
				}
				b = SoaUtils.modifyBindings(baseUrl, f.getAbsolutePath(), "junk");
			}
			if(!b)	{
				throw new CodeGenerationFailedException("Failed to modify xsd2jibx generated binding file");
			}
		}
		if((wsInterface == null) || (wsInterface.trim().length() == 0))	{
			//throw new CodeGenerationFailedException("Service Interface is not specified");
			String stubSkelArr[] = generateWsStubAndInterface(baseUrl,wsdl,wsName);
        	stubClassName =  stubSkelArr[0];
       		interfaceClassName =  stubSkelArr[1];

			//Now compile generated service Interface and Stub java source files
        	success = rc.compile(srcdir,binDir,classpaths);
        	if(!success)    {
            	throw new CodeGenerationFailedException("Error in building interface and Stub java source files");
        	}
		}else	{
			// If service interface is specified in soa.xml file then Generate Stub only. 
			// Also no need for compilation here as we expect that
			// if service interface is specified in soa.xml then archive will contain
			// class file for service interface
			String stubSkelArr[] = generateWsStub(baseUrl,wsdl,wsName);
            stubClassName =  stubSkelArr[0];
			interfaceClassName =  wsInterface;

		}
			
	                                       
		Class svcInterfaceClass = null;
		if(cl instanceof AppClassLoader)	{
			if(logger.isDebugEnabled())
			logger.debug("cl is instance of AppClassLoader");
		}
		AppClassLoader appClassLoader = (AppClassLoader)cl;	
		try	{
			if(logger.isDebugEnabled())
			logger.debug("Invoke findClass() on AppClassLoader for Service Interface: "+interfaceClassName);
			svcInterfaceClass = ((AppClassLoader)cl).findClass(interfaceClassName.trim());
			if(logger.isDebugEnabled())
			logger.debug("Service Interface class name: "+svcInterfaceClass.getName());
		}catch(Exception exp)	{
			logger.error("Failed to get Interface classes from app classloader");
			logger.error("Exception is: "+exp);
		}
		/*
		if((wsdl == null) || (wsdl.trim().length() == 0))   {
			//parse wsInterface String to extract name of interface 
			// which will be used to generate WSDL and WS Stub
			int index = wsInterface.lastIndexOf('.')+1;
			wsName = wsInterface.substring(index);
			if(logger.isDebugEnabled())	{
				logger.debug("wsName to be used for generating WSDL file and Ws Stub: " +wsName);
			}
				
            //Generate wsdl file from service java interface class
            // and construct new URI of generated wsdl file
            generateWsdl(baseUrl,wsName,wsInterface);
            //wsdl = baseUrl +File.separatorChar + SoaConstants.WSDL_DIR_NAME
            wsdl = baseUrl +File.separatorChar + "gen"
                    +File.separatorChar + wsName + ".wsdl";
        }
		*/

		/*
		* Once service interface and data objects are available and compiled
		* we will use JiBX to generate and compile databindings.
		*/
		if(bindingsGenerated)	{
        	compileBindings(baseUrl,wsName,interfaceClassName.trim());
		}
		
		
		String stubSkelArr[] = generateWsStub(baseUrl,wsdl,wsName);
        stubClassName =  stubSkelArr[0];

		String remoteSvcProxy = generateRemoteServiceProxy(baseUrl,svcInterfaceClass,stubClassName);
		if (logger.isDebugEnabled()) {
        	logger.debug("remoteSvcProxy: " + remoteSvcProxy);
        }

		//Now compile generated Remote Service Proxy
		success = rc.compile(srcdir,binDir,classpaths);
		if(!success)	{
			throw new CodeGenerationFailedException("Error in building proxy java source files");		
		}
		//Compilation is successful,so create instances of proxies and add them to Map
		Map map = new HashMap();
		Class serviceInterface = null;
		Object remoteServiceProxy = null;
		Object stub = null;

		try	{
		 	serviceInterface = Class.forName(interfaceClassName,false,cl);	
		 	remoteServiceProxy = Class.forName(remoteSvcProxy,false,cl).newInstance();	
		 	//stub = Class.forName(stubClassName,false,cl).newInstance();	
		}catch(ClassNotFoundException clsExp)	{
			logger.error("ClassNotFoundException: " +clsExp);
			throw new CodeGenerationFailedException(clsExp.getMessage());		
		}catch(InstantiationException insExp)	{
			logger.error("InstantiationException: " +insExp);
			throw new CodeGenerationFailedException(insExp.getMessage());		
		}catch(IllegalAccessException illException)	{
			logger.error("IllegalAccessException: " +illException);
			throw new CodeGenerationFailedException(illException.getMessage());		
		}catch(Exception e)	{
			logger.error("Exception is: "+e.getMessage(),e);
			throw new CodeGenerationFailedException(e.getMessage());
		}


		//set Stub instance reference into remote service proxy
		/*
        try {
            Class c = remoteServiceProxy.getClass();
            Class[] parameterTypes = new Class[] { stub.getClass() };
            Method setStub;
            Object[] arguments = new Object[] { stub };
            setStub = c.getMethod("setStub", parameterTypes);
            setStub.invoke(remoteServiceProxy,arguments);
        } catch (NoSuchMethodException e) {
            logger.error("NoSuchMethodException: " +e);
        } catch (IllegalAccessException e) {
            logger.error("IllegalAccessException: " +e);
        } catch (InvocationTargetException e) {
            logger.error("InvocationTargetException: " +e);
        }catch(Exception e) {
            logger.error("Exception: " +e);
        }catch(Throwable th)    {
            logger.error("Throwable Exception: " +th);
        }
		*/
		try	{
			((BaseProxy)remoteServiceProxy).setURI(uri);
		}catch(Exception e)	{
			logger.error("Failed to set Remote Service URI on Service Proxy: " +e);
		}

		//Add generated objects into Map
		//Adding Class object for service Interface as firts entry in this Map.
		map.put(SoaConstants.GENERATE_SOAP_SERVICE_INTERFACE,serviceInterface);
		map.put(SoaConstants.PROXY_REMOTE_SERVICE,remoteServiceProxy);
		return map;
	
	}
 
	private String[] generateWsStubAndInterface(String baseUrl,String wsdl,String wsName)
									throws CodeGenerationFailedException	{
		 return soapServer.generateWsdlToJava(baseUrl,wsdl,wsName,SoaConstants.GENERATE_SOAP_SERVICE_INTERFACE);
	}
	
	private String[] generateWsStub(String baseUrl,String wsdl,String wsName)
                                    throws CodeGenerationFailedException    {
         return soapServer.generateWsdlToJava(baseUrl,wsdl,wsName,SoaConstants.GENERATE_SOAP_STUB);
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

	
	private boolean generateBindingsFromXsd(String baseUrl) throws CodeGenerationFailedException	{
		boolean xsdFound = false;
		try	{
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
		}catch(Exception e)	{
			logger.error("failed to generate bindings from XSD",e);
			throw new CodeGenerationFailedException(e.getMessage());
		}
		return xsdFound;
		
	}

	 private String generateRemoteServiceProxy(String baseUrl,Class svcInterface,String stubClass)    {
        ClassInspector inspector;
        RemoteServiceProxyGenerator remoteSvcProxyGenerator;
        inspector = new ClassInspector();
        try {
            inspector.parse(svcInterface);
            remoteSvcProxyGenerator = new RemoteServiceProxyGenerator();
			Map jetMap = new HashMap();
            jetMap.put("ClassInspector",inspector);
            jetMap.put("StubClass",stubClass);
            String remoteSvcProxy = remoteSvcProxyGenerator.generate(jetMap);
            //Now write this generated java source code into .java file on disk
            SoaUtils.writeJavaFileToDisk(baseUrl,inspector,remoteSvcProxy,SoaConstants.NAME_REMOTE_PROXY_FILE_NAME_SUFFIX);
        } catch(Exception exp) {
            //log error here
        }
                                                                                                                              
        return inspector.getAbsoluteClassName() + remote_suffix;
    }

		
}
