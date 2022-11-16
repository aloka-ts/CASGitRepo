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
//      File:   CodeGenerator.java
//
//      Desc:   This is a implemented as Singleton and defines interfaces to be used
//			by other modules or classes.
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  19/12/07        Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.soa.codegenerator;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.URI;
import java.net.URL;
import java.io.File;
import org.apache.log4j.Logger;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.soa.util.SoaUtils;
import com.baypackets.ase.soa.util.RuntimeCompiler;
import com.baypackets.ase.container.AppClassLoader;
import com.baypackets.ase.soa.codegenerator.exceptions.CodeGenerationFailedException;

public class CodeGenerator	{
	private static Logger logger = Logger.getLogger(CodeGenerator.class);
	private static CodeGenerator codeGen = new CodeGenerator();
	private Generator generator = null;	

	private CodeGenerator()	{

	}

	public static CodeGenerator getInstance()	{
		return codeGen;
	}

	public Map generateCode(String baseUrl,String wsdl,int opType,int wsType,
						String wsContextName,String wsName,
						ClassLoader cl)	throws CodeGenerationFailedException {
		if(logger.isDebugEnabled())	{
			logger.debug("Received request to generate code with : " );
			logger.debug(" baseUrl :" + baseUrl);
			logger.debug("wsdl :" +wsdl );
			logger.debug("opType :" +opType );
			logger.debug("wsType :" +wsType );
			logger.debug("wsContextName :" +wsContextName );
			logger.debug("wsName :" +wsName );
			logger.debug("ClassLoader :" +cl );
		}
		Map map = null;
		//use GeneratorFactory to create and return an appropriate Generator
		generator = GeneratorFactory.getGenerator(opType,wsType);
		if(generator != null)	{
			map = generator.generate(baseUrl,wsdl,wsName,wsContextName,cl);
		}
		return map;
	}

	public Object generateClientProxy(Class wsInterfaceClass,URI serviceUri) throws CodeGenerationFailedException	{
		if(logger.isDebugEnabled())	{
			logger.debug("Received request to generate client proxy for: " +wsInterfaceClass.getName());
		}
		Object clientProxy = null;
		ClassLoader cl = wsInterfaceClass.getClassLoader();
		ClassInspector inspector = new ClassInspector();
		ClientProxyGenerator clientProxyGenerator;
		String wsInterfaceName = wsInterfaceClass.getName();
		String src = null;

		try {
            inspector.parse(wsInterfaceClass);
            clientProxyGenerator = new ClientProxyGenerator();
			Map jetMap = new HashMap();
            jetMap.put("ClassInspector",inspector);
            jetMap.put("ServiceURI",serviceUri.toString());
            src = clientProxyGenerator.generate(jetMap);
        } catch(Exception exp) {
            //log error here
			logger.error("Exception in generating client Proxy: "+exp);
			throw new CodeGenerationFailedException(exp.getMessage());
        }

	
		String proxyClassName = wsInterfaceName +"Proxy";

		RuntimeCompiler rc = new RuntimeCompiler();
		List<File> appClasspath = new ArrayList<File>();

		AppClassLoader appLoader = (AppClassLoader) cl;
		URL[] urls = appLoader.getURLs();
		File f = null;
		String outputDir = null;
		String url = null;
		for(int i =0; i<urls.length;i++)	{
			try	{
				url = urls[i].getPath();
				if(logger.isDebugEnabled())	{
					logger.debug("Current classloader URL: " + url);
				}
				if(url.endsWith("WEB-INF/classes/"))	{
					outputDir = url;
				}
				f = new File(url);
				appClasspath.add(f);
			}catch(Exception exp)	{
				logger.error("Error in adding base URL to classpath");
			}
		}
		List<File> classpaths = null;
		try {
            classpaths = SoaUtils.getClassPath(appClasspath);
        }catch(Exception exp)   {
            logger.error("Exception in setting classpath for RuntimeCompiler: " + exp);
            throw new CodeGenerationFailedException("Error in setting classpath for RuntimeCompiler");
        }
        //boolean success = rc.compileSource(proxyClassName,src,cl,classpaths);
        boolean success = rc.compileJavaSourceFile(proxyClassName,src,outputDir,classpaths);
        if(!success)    {
            throw new CodeGenerationFailedException("Error in generating client proxy");
        }
		//Now create an instance of service proxy
		AppClassLoader loader = null;
		if(cl instanceof AppClassLoader)	{
			loader = (AppClassLoader)cl;
		}
		try	{
			clientProxy = (Class.forName(proxyClassName,true,loader)).newInstance();
		}catch(Exception exp)	{
			logger.error("Exception: " + exp);
			throw new CodeGenerationFailedException("Failed to generate client service proxy");
		}catch(Throwable th)	{
			logger.error("Exception: " + th);
			throw new CodeGenerationFailedException("Failed to generate client service proxy");

		}


		return clientProxy;
	}
}
