/*
 * EMSAdaptor.java
 *
 * Created on August 6, 2004, 4:51 PM
 */
package com.baypackets.ase.servicemgmt;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.util.Constants;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.ShutdownFailedException;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.deployer.DeployerFactoryImpl;
import com.baypackets.ase.jmxmanagement.SarFileByteArray;
import com.baypackets.ase.jmxmanagement.ServiceManagement;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeploymentListener;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.emsliteagent.EmsLiteServiceHandler;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.emsliteagent.EmsLiteAgent;
import com.baypackets.emsliteagent.FileByteArray;

/**
 * An instance of this class provides an interface to deploy and manage Servlet
 * applications on the SAS from the EMSLite console.
 */
public class EmsLiteAdaptor implements EmsLiteServiceHandler {

	private static Logger logger = Logger.getLogger(EmsLiteAdaptor.class);

	private Deployer appDeployer = null;
	
	private EmsLiteAgent emslAgent = BaseContext.getEmslagent();


	public boolean deployService(String serviceName, String version,
			String priority, String contextPath, HashMap m) {
		try
		{
			String ContextPath = contextPath;
			int appPriority = Integer.parseInt(priority);
			String appVersion = version;
			String appName = serviceName;
			if(logger.isInfoEnabled() ){
			logger.info("Information about the service to be deployed by wEMS on CAS");
			logger.info("ContextPath ==== >" +ContextPath);
			logger.info("AppPriority ==== > "+appPriority);
			logger.info("AppName ===== > "+appName);
			logger.info("AppVersion ==== > "+appVersion);
			}
			
			FileByteArray sarFileByteArrays = (FileByteArray)m.get("sar");
			
			byte[] bytes = sarFileByteArrays.getByteArray();
			InputStream inputStream = new ByteArrayInputStream(bytes);
			if(logger.isInfoEnabled() ){
			logger.info("In the Deployer METHOD");	
			}
			String deployedby = "CLIENT_IDE";
			AbstractDeployableObject deployableobject = (AbstractDeployableObject)appDeployer.deploy(appName, appVersion,appPriority, ContextPath, inputStream, deployedby);
			if(deployableobject!=null)
			{
				return true;
			}
			else
				return false;
		}
		catch(Exception e)
		{
			if(logger.isInfoEnabled() ){
			logger.info("Exception   ",e);
			}
			return false;
		}
	}

	public boolean undeployService(String serviceName, String version) {
		try
		{
			if(logger.isInfoEnabled() ){
			logger.info("In the undeploy method");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject)appDeployer.findByNameAndVersion(serviceName, version);

			if(deployableobject != null) {
			
				String serviceID = deployableobject.getId();
        
                appDeployer.undeploy(serviceID);
			if(logger.isInfoEnabled() ){
                logger.info("Service has been undeployed");
			}
            	return true;
			}
			if(logger.isDebugEnabled() ){
			logger.debug("No Service found with Deployment Name : "+serviceName+" and version : "+version);
			}
			return false;	
		}

		catch(Exception e)
		{
			if(logger.isDebugEnabled() ){
			logger.debug("Exception ",e);
			}
			return false;
		}
	}

	public boolean startService(String serviceName, String version) {
		try
		{
			if(logger.isInfoEnabled() ){
			logger.info("In the start method");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject)appDeployer.findByNameAndVersion(serviceName, version);

			if(deployableobject != null) {
				String serviceID = deployableobject.getId();
	
				appDeployer.start(serviceID);
				if(logger.isInfoEnabled() ){
				logger.info("Service has been started");
				}
				return true;
			}
			if(logger.isDebugEnabled() ){
			logger.debug("No Service found with Deployment Name : "+serviceName+" and version : "+version);
			}
			return false;

		}

		catch(Exception eee)
		{
			if(logger.isDebugEnabled() ){
			logger.debug("Exception ",eee);
			}

			return false;
		}
	}

	public boolean stopService(String serviceName, String version) {
		try
		{
			if(logger.isInfoEnabled() ){
			logger.info("In the Stop method ");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject)appDeployer.findByNameAndVersion(serviceName, version);
			if(deployableobject != null) {
				String serviceID = deployableobject.getId();
        
                appDeployer.stop(serviceID,true);
			if(logger.isInfoEnabled() ){
                logger.info("Service has been stopped");
			}
                return true;
			}
			if(logger.isDebugEnabled() ){
			logger.debug("No Service found with Deployment Name : "+serviceName+" and version : "+version);
			}
			return false;				
		}
		catch(Exception e)
		{
			if(logger.isDebugEnabled() ){
			logger.debug("Exception ",e);
			}
			return false;
		}
	}

	public boolean activateService(String serviceName, String version) {
		try
		{
			if(logger.isInfoEnabled() ){
			logger.info("In the activate method");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject)appDeployer.findByNameAndVersion(serviceName, version);

			if(deployableobject != null) {
				String serviceID = deployableobject.getId();

				appDeployer.activate(serviceID);
				if(logger.isInfoEnabled() ){
					logger.info("Service has been activated");
				}
				return true;
			}
			if(logger.isDebugEnabled() ){
				logger.debug("No Service found with Deployment Name : "+serviceName+" and version : "+version);
			}
			return false;
		}
		catch(Exception ee)
		{
			if(logger.isDebugEnabled() ){
			logger.debug("Exception ",ee);
			}

			return false;

		}
	}

	public boolean deactivateService(String serviceName, String version) {
		try
		{
			if(logger.isInfoEnabled() ){
			logger.info("In the Deactivate method");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject)appDeployer.findByNameAndVersion(serviceName, version);
			if(deployableobject != null) {
				String serviceID = deployableobject.getId();
	
				appDeployer.deactivate(serviceID);
				if(logger.isInfoEnabled() ){
				logger.info("Service has been deactivated");
				}
				return true;
			}
			if(logger.isDebugEnabled() ){
			logger.debug("No Service found with Deployment Name : "+serviceName+" and version : "+version);
			}
			return false;
		}

		catch(Exception e)
		{
			if(logger.isDebugEnabled() ){
			logger.debug("Exception ",e);
			}
			return false;
		}
	}


	public Deployer getApplicationDeployer() {
		return appDeployer;
	}

	public void setApplicationDeployer(Deployer deployer) {
		this.appDeployer = deployer;
	}
}
