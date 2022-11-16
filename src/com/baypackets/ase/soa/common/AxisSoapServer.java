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
//      File:   AxisSoapServer.java
//
//      Desc:   This file defines wrapper class for Axis SOAP server and implements SoapServer
//			interface. 
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  18/12/07        Initial Creation
//		Suresh Kr. Jangir				08/01/08		Added deployService(),undeployService()
//														startService() and stopService() method.
//
//***********************************************************************************

package com.baypackets.ase.soa.common;

import java.util.Vector;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.net.URLClassLoader;

import org.apache.axis2.deployment.DeploymentEngine;
import org.apache.axis2.context.ConfigurationContext;
import org.apache.axis2.context.ConfigurationContextFactory;
import org.apache.axis2.description.AxisService;
import org.apache.axis2.description.AxisServiceGroup;
import org.apache.log4j.Logger;
import org.apache.axis2.wsdl.WSDL2Java;
import org.apache.ws.java2wsdl.Java2WSDL;

import com.baypackets.ase.startup.AseMain;
import com.baypackets.ase.startup.AseClassLoader;
import com.baypackets.ase.util.FileUtils;
import com.baypackets.ase.deployer.DeployerFactoryImpl;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.container.exceptions.UndeploymentFailedException;
import com.baypackets.ase.container.exceptions.ActivationFailedException;
import com.baypackets.ase.container.exceptions.DeactivationFailedException;
import com.baypackets.ase.container.exceptions.UpgradeFailedException;
import com.baypackets.ase.soa.codegenerator.exceptions.CodeGenerationFailedException;
import com.baypackets.ase.soa.util.SoaUtils;

public class AxisSoapServer implements SoapServer	{

	private static Logger m_logger = Logger.getLogger(AxisSoapServer.class);
	private static final String DEPLOY_DIR = Constants.ASE_HOME+"/soapserver/"+Constants.NAME_SOAP_SERVER_AXIS;
	private File m_axisDir = new File(DEPLOY_DIR + "/WEB-INF/services/");
	private List m_deployedSvcNameList = new ArrayList(); 

	public void initialize() {
		try {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Deploying the Axis SOAP Server");
			}

			File deployDir = new File(DEPLOY_DIR);
			if(! deployDir.exists()) {
				m_logger.error("Unable to deploy Axis SOAP Server: Package not found:");
				m_logger.error("Package ="+deployDir.getAbsolutePath());
				return;
			}
			File axisDeployable = null;
			File[] files = deployDir.listFiles();
			for(int i=0 ; i<files.length ; i++) {
				if((files[i].isFile()) && (files[i].getName().endsWith("war"))) {
					axisDeployable = files[i];
				}
			}
	
			if(axisDeployable == null) {
				m_logger.error("Axis war file not found in dir "+DEPLOY_DIR);
				return;
			}
			
			InputStream stream = new FileInputStream(axisDeployable);
			DeployerFactoryImpl factory = 
						(DeployerFactoryImpl)Registry.lookup(Constants.NAME_DEPLOYER_FACTORY);
			Deployer deployer = factory.getDeployer(DeployableObject.TYPE_SOAP_SERVER);
			AbstractDeployableObject deployableObj = (AbstractDeployableObject)deployer.
													deploy(Constants.NAME_SOAP_SERVER_AXIS,
														SoaConstants.SOAP_SERVER_AXIS_VERSION,
														Deployer.DEFAULT_PRIORITY,
														null,
														stream,
														Deployer.CLIENT_SOA_FW);
			deployer.start(deployableObj.getId());
		} catch(Exception e) {
			m_logger.error("Unable to deploy Axis SOAP Server",e);
		}
	}

	public void start() {
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Going to start Axis SOAP server");
		}

		try {
			DeployerFactoryImpl factory =
						(DeployerFactoryImpl)Registry.lookup(Constants.NAME_DEPLOYER_FACTORY);
			Deployer deployer = factory.getDeployer(DeployableObject.TYPE_SOAP_SERVER);
			Iterator it = deployer.findByName(Constants.NAME_SOAP_SERVER_AXIS);
			if(it.hasNext()){
				DeployableObject app = (DeployableObject) it.next();
				deployer.activate(app.getId());
			}
		} catch(Exception e) {
			m_logger.error("Unable to activate Axis SOAP Server",e);
		}
	}

	public void deployService(String serviceName, String serviceBundleUrl, ClassLoader classLoader)
							 throws DeploymentFailedException	{
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Going to Deploy the service "+serviceName+" on Axis SOAP Server");
			m_logger.debug("ServiceBundle Location "+serviceBundleUrl+" ClassLoader= "+classLoader);
		}

		try {
			m_deployedSvcNameList.add(serviceName);

			//Creating Service aar file
			File aarFile = SoaUtils.createJar(serviceBundleUrl, serviceBundleUrl+ "/"+ serviceName+".aar",null, null, null);
			
			File copiedFile = FileUtils.move(aarFile, m_axisDir);
			if (m_logger.isDebugEnabled()) {
				m_logger.debug("Copied .aar filename is = "+ copiedFile);
			}
			Thread.sleep(10000);//sleep for 10 seconds
		} catch(Exception e) {
			m_logger.error("Unable to deploy service on Axis name = "+serviceName, e);
			throw new DeploymentFailedException("Unable to deploy service on Axis",e);
		}

	}

	public void undeployService(String serviceName)
							 throws UndeploymentFailedException	{
		
		if (m_deployedSvcNameList.remove(serviceName)) {
			File aarFile = new File(m_axisDir + File.separator + serviceName +".aar");
			if(aarFile.exists()) {
				if(m_logger.isDebugEnabled())
				m_logger.debug("Going to undeploy service [" + serviceName + "] from axis soapserver" );
				try {
					FileUtils.delete(aarFile);
					Thread.sleep(1000);
				}catch (Exception e) {
					m_logger.debug(e.getMessage(), e);
				}
			}
		}
	}

   	public void startService(String serviceName) throws ActivationFailedException	{

	}

   	public void stopService(String serviceName) throws DeactivationFailedException	{

	}

   	public void upgradeService(String serviceName,String serviceBundleUrl)
									throws UpgradeFailedException	{

	}
	
	public String getSoapServerLibDir()	{
		return  DEPLOY_DIR+ File.separatorChar + "WEB-INF" +File.separatorChar + "lib";
	}
	
   	public String[] generateWsdlToJava(String baseUrl,String wsdlUrl,String wsName,String options)
							 throws CodeGenerationFailedException	{
		Vector cmdOptions = new Vector();
		String[] arr = new String[2];
		String meta_inf = "META-INF";
		String bindingFile = baseUrl + File.separatorChar + "gen" + File.separatorChar + "binding.xml";
		String pkg = SoaConstants.NAME_CUSTOM_PKG_STUB_SKEL + wsName;
		//add switches for stub only
		cmdOptions.add("-uri");
		cmdOptions.add(wsdlUrl);
		cmdOptions.add("-sn");	
		cmdOptions.add(wsName);
		cmdOptions.add("-s");
		cmdOptions.add("-p");
		cmdOptions.add(pkg);
		//adding additional flags for jibx databinding
		cmdOptions.add("-d");
        cmdOptions.add("jibx");
		File f = new File(bindingFile);
		if(f.exists())	{
        	cmdOptions.add("-Ebindingfile");
        	cmdOptions.add(bindingFile);
		}
		cmdOptions.add("-uw");
		cmdOptions.add("-o");
		cmdOptions.add(baseUrl);
		cmdOptions.add("-sd");
		cmdOptions.add("-R");
		cmdOptions.add(meta_inf);
		
		//add switch for generating service interface and service stub
		if(options.equalsIgnoreCase(SoaConstants.GENERATE_SOAP_SERVICE_INTERFACE))	{
			//For service interface generation from thirdparty provided WSSDL file
			// we may have to remove option added above for jibx data binding

			/*cmdOptions.remove("-d");
        	cmdOptions.remove("jibx");
        	cmdOptions.remove("-Ebindingfile");
        	cmdOptions.remove(bindingFile);
			*/	
			cmdOptions.add("-ss");
			//cmdOptions.add("-ssi");
			cmdOptions.add("-g");
		}
		//add switch for skeleton only
		if(options.equalsIgnoreCase(SoaConstants.GENERATE_SOAP_SKELETON))	{
			cmdOptions.add("-ss");
		}
		//add switch for both stub and skeleton
		if(options.equalsIgnoreCase(SoaConstants.GENERATE_SOAP_BOTH))	{
            cmdOptions.add("-ss");
            cmdOptions.add("-g");
		}
		int size = cmdOptions.size();
		String[] cmdLineArgs = new String[size];
		for(int i=0;i<size;i++)	{
			cmdLineArgs[i] = (String)cmdOptions.get(i);
		}
		if(m_logger.isDebugEnabled())	{
			m_logger.debug("Invoking WSDL2Java with options: " + cmdOptions);
		}
		try	{
			WSDL2Java.main(cmdLineArgs);
		}catch(Exception exp)	{
			m_logger.error("Exception is: " +exp,exp);
			throw new CodeGenerationFailedException(exp.getMessage());
		}
		arr[0] = pkg +"." + wsName +"Stub";
		if(options.equalsIgnoreCase(SoaConstants.GENERATE_SOAP_SERVICE_INTERFACE))  {
			//arr[1] = pkg +"." + wsName +"SkeletonInterface";
			arr[1] = pkg +"." + wsName;
		}else	{	
			arr[1] = pkg +"." + wsName +"Skeleton";
		}

		return arr;
		

	}

   	public void generateJavaToWsdl(String baseUrl,String serviceClassName,String serviceName)
								 throws CodeGenerationFailedException	{
		Vector cmdOptions = new Vector();
        //add switches for stub only
        cmdOptions.add("-cn");
        cmdOptions.add(serviceClassName);
        cmdOptions.add("-sn");
        cmdOptions.add(serviceName);
        cmdOptions.add("-o");
        cmdOptions.add(baseUrl+"/wsdl");
        cmdOptions.add("-of");
        cmdOptions.add(serviceName+".wsdl");
        cmdOptions.add("-l");
        cmdOptions.add("http://<ipaddress>:port/axis2/services/servicename");

		int size = cmdOptions.size();
        String[] cmdLineArgs = new String[size];
        for(int i=0;i<size;i++) {
            cmdLineArgs[i] = (String)cmdOptions.get(i);
        }
        try {
            Java2WSDL.main(cmdLineArgs);
        }catch(Exception exp)   {
            throw new CodeGenerationFailedException(exp.getMessage());
        }
	}
}
