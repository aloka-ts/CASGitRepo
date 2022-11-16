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
//      File:   SoapServer.java
//
//      Desc:   This file defines an interface to be implemented by a class which encapsulates
//			functionality of a particular SOAP server. 
//
//      Author                          Date            Description
//      ----------------------------------------------------------------------
//      Rajendra Singh                  18/12/07        Initial Creation
//
//***********************************************************************************

package com.baypackets.ase.soa.common;

import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.container.exceptions.UndeploymentFailedException;
import com.baypackets.ase.container.exceptions.ActivationFailedException;
import com.baypackets.ase.container.exceptions.DeactivationFailedException;
import com.baypackets.ase.container.exceptions.UpgradeFailedException;
import com.baypackets.ase.soa.codegenerator.exceptions.CodeGenerationFailedException;


public interface SoapServer	{

	void initialize();
	void start();
	String getSoapServerLibDir();
	void deployService(String serviceName,String serviceBundleUrl,ClassLoader classLoader) throws DeploymentFailedException;
	void undeployService(String serviceName) throws UndeploymentFailedException;
	void startService(String serviceName) throws ActivationFailedException;
	void stopService(String serviceName) throws DeactivationFailedException;
	void upgradeService(String serviceName,String serviceBundleUrl) throws UpgradeFailedException;
	String[] generateWsdlToJava(String baseUrl,String wsdlUrl,String wsName,
								String options) throws CodeGenerationFailedException;
	void generateJavaToWsdl(String baseUrl,String serviceClassName,String serviceName) throws CodeGenerationFailedException;
}
