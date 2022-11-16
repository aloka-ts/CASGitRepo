package com.baypackets.ase.jmxmanagement;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import java.util.*;
//import RSIEms.ServiceMgmtSession;

import com.baypackets.ase.servicemgmt.*;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.emsagent.EmsAgent;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import com.baypackets.ase.deployer.*;
import com.baypackets.ase.soa.fw.SoaFrameworkContext;
import com.baypackets.ase.spi.deployer.*;
import com.baypackets.ase.soa.deployer.SoaDeployer;
import javax.management.*;
import com.baypackets.emsagent.GenericComponentManager;
import java.io.*;

import com.baypackets.ase.startup.AseMain;
import java.util.Date;
import java.net.URL;
import java.util.HashMap;
public class SOAServiceManagement implements SOAServiceManagementMBean{
	private static Logger logger = Logger.getLogger(ServiceManagement.class);

	private DeployerFactory deployerFactoryImpl;

//	private SasDeployer sasDeployer;
	private SoaDeployer soaDeployer;

	private int status = 0;

	private Iterator initiallydeployed = null;	//Iterator of Deployable Object

	private Hashtable deployedByIDE = null;
	
	public SOAServiceManagement()
	{
		if(logger.isInfoEnabled() ){
		logger.info("SOAServiceManagement constructor ");
		}
	}

		

	public void initialize()
	{
		deployerFactoryImpl = (DeployerFactory)Registry.lookup(DeployerFactory.class.getName());
		if(logger.isInfoEnabled() ){
		logger.info("The Service Management has been initialized");
		}
//		sasDeployer = 	(SasDeployer)deployerFactoryImpl.getDeployer(1);
		soaDeployer = (SoaDeployer)deployerFactoryImpl.getDeployer(DeployableObject.TYPE_SOA_SERVLET);
		SoaFrameworkContext m_fwContext = (SoaFrameworkContext)Registry.lookup(Constants.NAME_SOA_FW_CONTEXT);

		logger.info("The SOA  deployer "+soaDeployer);

		status = 1;

		initiallydeployed = soaDeployer.findAll();

		logger.info("ALL DEPLOYED ===== > "+initiallydeployed);

		deployedByIDE =  new Hashtable();

		

	}
	private Iterator All()
	{
		return soaDeployer.findAll();
	}


	public boolean deploy(String ServiceName, String version, String priority,String contextPath, HashMap map)
	{
		try
		{
			
			String ContextPath = contextPath;
			int appPriority = Integer.parseInt(priority);
			String appVersion = version;
			String appName = ServiceName;
			if(logger.isInfoEnabled() ){
			logger.info("Information about the service to be deployed by IDE on SAS");
			logger.info("ContextPath ==== >" +ContextPath);
			logger.info("AppPriority ==== > "+appPriority);
			logger.info("AppName ===== > "+appName);
			logger.info("AppVersion ==== > "+appVersion);
			}
			SarFileByteArray sarFileByteArrays = (SarFileByteArray)map.get("sar");
			byte[] bytes = sarFileByteArrays.getByteArray();
			InputStream inputStream = new ByteArrayInputStream(bytes);


			String deployedby = "CLIENT_IDE";
			AbstractDeployableObject deployableobject = (AbstractDeployableObject)soaDeployer.deploy(appName, appVersion,appPriority, ContextPath, inputStream, deployedby);
			if(deployableobject!=null)
			{
				deployedByIDE.put(ServiceName,deployableobject);
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

        public boolean redeploy(String ServiceName, String version, String priority, String contextPath, HashMap map)
        {
                try
                {

                        String ContextPath = contextPath;
                        int appPriority = Integer.parseInt(priority);
                        String appVersion = version;
                        String appName = ServiceName;
				if(logger.isInfoEnabled() ){
                        logger.info("Information about the service to be deployed by IDE on SAS");
                        logger.info("ContextPath ==== >" +ContextPath);
                        logger.info("AppPriority ==== > "+appPriority);
                        logger.info("AppName ===== > "+appName);
                        logger.info("AppVersion ==== > "+appVersion);
				}

			SarFileByteArray sarFileByteArrays = (SarFileByteArray)map.get("sar");
			byte[] bytes = sarFileByteArrays.getByteArray();
			InputStream inputStream = new ByteArrayInputStream(bytes);
	

                        String deployedby = "CLIENT_IDE";
                        AbstractDeployableObject deployableobject = (AbstractDeployableObject)soaDeployer.redeploy(appName, appVersion,appPriority, ContextPath, inputStream, deployedby);

                        if(deployableobject!=null)
                        {
                                deployedByIDE.put(ServiceName,deployableobject);
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

        public boolean redeploy(String ServiceName, String version, String priority, String contextPath)
        {
                try
                {
                        
                        String ContextPath = contextPath;
                        int appPriority = Integer.parseInt(priority);
                        String appVersion = version;
                        String appName = ServiceName;
				if(logger.isInfoEnabled() ){
                        logger.info("Information about the service to be deployed by IDE on SAS");
                        logger.info("ContextPath ==== >" +ContextPath);
                        logger.info("AppPriority ==== > "+appPriority);
                        logger.info("AppName ===== > "+appName);
                        logger.info("AppVersion ==== > "+appVersion);
				}


                        InputStream inputStream = new FileInputStream(ContextPath);


                        String deployedby = "CLIENT_IDE";
                        AbstractDeployableObject deployableobject = (AbstractDeployableObject)soaDeployer.redeploy(appName, appVersion,appPriority, ContextPath, inputStream, deployedby);

                        if(deployableobject!=null)
                        {
                                deployedByIDE.put(ServiceName,deployableobject);
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

        public boolean deploy(String ServiceName, String version, String priority, String contextPath)
        {
                try
                {
                        
                        String ContextPath = contextPath;
                        int appPriority = Integer.parseInt(priority);
                        String appVersion = version;
                        String appName = ServiceName;
				if(logger.isInfoEnabled() ){
                        logger.info("Information about the service to be deployed by IDE on SAS");
                        logger.info("ContextPath ==== >" +ContextPath);
                        logger.info("AppPriority ==== > "+appPriority);
                        logger.info("AppName ===== > "+appName);
                        logger.info("AppVersion ==== > "+appVersion);
				}
                        InputStream inputStream = new FileInputStream(ContextPath);
                        

                        String deployedby = "CLIENT_IDE";
                        AbstractDeployableObject deployableobject = (AbstractDeployableObject)soaDeployer.redeploy(appName, appVersion,appPriority, ContextPath, inputStream, deployedby);

                        if(deployableobject!=null)
                        {
                                deployedByIDE.put(ServiceName,deployableobject);
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





	public boolean activate(String ServiceName)
	{

		try
		{
			

			AbstractDeployableObject deployableobject = (AbstractDeployableObject)deployedByIDE.get(ServiceName);

			String serviceID = deployableobject.getId();

			soaDeployer.activate(serviceID);
			if(logger.isInfoEnabled() ){
			logger.info("JMX Service has been activated");
			}
			return true;
		}
		catch(Exception ee)
		{
			if(logger.isDebugEnabled() ){
			logger.debug("Exception ",ee);
			}
			return false;

		}
	}
	
	

	public boolean start(String ServiceName)
	{

		try
		{
			

			AbstractDeployableObject deployableobject = (AbstractDeployableObject)deployedByIDE.get(ServiceName);

			String serviceID = deployableobject.getId();

			soaDeployer.start(serviceID);
			if(logger.isInfoEnabled() ){
			logger.info("JMX Service has been started");
			}
			return true;

		}

		catch(Exception eee)
		{
			if(logger.isDebugEnabled() ){
			logger.debug("Exception ",eee);
			}

			return false;
		}
	}

	

	public boolean stop(String ServiceName)
	{

		try
		{

			AbstractDeployableObject deployableobject = (AbstractDeployableObject)deployedByIDE.get(ServiceName);

			String serviceID = deployableobject.getId();
        
            soaDeployer.stop(serviceID,true);
			if(logger.isInfoEnabled() ){
			logger.info("JMX Service has been stopped");
			}
			return true;
		}
		catch(Exception e)
		{
			if(logger.isDebugEnabled() ){
			logger.debug("Exception ",e);
			}
			return false;
		}
	}

	public boolean undeploy(String ServiceName)
	{
		try
		{
			if(logger.isInfoEnabled() ){
			logger.info("In the undeploy method");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject)deployedByIDE.get(ServiceName);
			String serviceID = deployableobject.getId();
        
            soaDeployer.undeploy(serviceID);
			if(logger.isInfoEnabled() ){
			logger.info("JMX Service has been undeployed");
			}
			return true;
		}

		catch(Exception e)
		{
			if(logger.isDebugEnabled() ){
			logger.debug("Exception ",e);
			}
			return false;
		}

	}

	public boolean deactivate(String ServiceName)
	{
		try
		{
			

			AbstractDeployableObject deployableobject = (AbstractDeployableObject)deployedByIDE.get(ServiceName);

			String serviceID = deployableobject.getId();

			soaDeployer.deactivate(serviceID);
			if(logger.isInfoEnabled() ){
			logger.info("JMX service has been deactivated");
			}
			return true;
		}

		catch(Exception e)
		{
			if(logger.isDebugEnabled() ){
			logger.debug("Exception ",e);
			}

			return false;
		}
	}

	public Hashtable AllDeployedSOAServicesIDE()
	{
		Hashtable table = new Hashtable();

		table =  deployedByIDE;
		if(logger.isInfoEnabled() ){
		logger.info("THE SERVICES DEPLOYED BY IDE ====> "+table);
		}
		return table;
	}

	public Hashtable AllDeployedSOAServices()
	{
		Iterator itr = null;

		//itr  = initiallydeployed;
		Hashtable services = new Hashtable();

		itr = All();
		//while(initiallydeployed.hasNext())
		while(itr.hasNext())
		{
			AbstractDeployableObject object = (AbstractDeployableObject)itr.next();

			String name = object.getName();

			String status = object.getStatusString();

			String deployedby = object.getDeployedBy();
			
			String appInfo = object.getDisplayInfo();
			
			String appType=null;
			if(object.getType()==DeployableObject.TYPE_SIMPLE_SOA_APP){
				appType="Simple SOA Application";
			}else if(object.getType()==DeployableObject.TYPE_PURE_SOA){
				appType="Pure SOA";
			}else if(object.getType()==DeployableObject.TYPE_SOA_SERVLET){
				appType="SOA With Servlet(Http or Sip)";
			}

			Hashtable info = new Hashtable();

			info.put("STATUS",status);
			info.put("DEPLOYEDBY",deployedby);
			info.put("TYPE",appType);
			info.put("INFO",appInfo);
			
			services.put(name,info);
		}
		return services;

		
	}

	public void stopserver()
	{
		try
		{
			if(logger.isInfoEnabled() ){
			logger.info("Just going to stop the SIP APPlication Server");
			}
			GenericComponentManager componentManager = (GenericComponentManager)Registry.lookup(Constants.NAME_COMPONENT_MANAGER);
			
			MComponentState stopState = new MComponentState(MComponentState.STOPPED);
			
                        componentManager.changeState(stopState);
			if(logger.isInfoEnabled() ){
			logger.info("Stopping the ASE Engine");
			}

        	}
		catch(Exception e)
		{
                	logger.error(e.getMessage(), e);
			
        	}
		finally
		{
                        if (logger.isEnabledFor(Level.INFO))
			{
                                logger.info("Shutdown Completed for ASE Server at "+ new Date());
                        }
                	System.exit(1);
        	}      

	}

	public int status()
	{
		return this.status;

	}
	public boolean deployservice(String id)
	{
		try
		{
			Iterator itr = All();

			while(itr.hasNext())
                	{
                        	AbstractDeployableObject object = (AbstractDeployableObject)itr.next();

                        	String name = object.getName();
				if(name.equals(id))
				{
					try
					{
						object.deploy();
						return true;
					}
					catch(Exception e)
					{
						logger.error("Unable to deploy the service ",e);
						return false;
					}
				}
				else
					continue;
			}
			return false;
		}
		catch(Exception e)
		{
			logger.error("Error in Deploying Service",e);
			return false;
		}
	}

 	public boolean stopservice(String id)
        {
		try
		{
                	Iterator itr = All();

               	 	while(itr.hasNext())
                	{
                        	AbstractDeployableObject object = (AbstractDeployableObject)itr.next();

                        	String name = object.getName();
                        	if(name.equals(id))
                        	{
					try
					{
                                	//	object.stop(true);
                                		soaDeployer.stop(object.getId(), true);
                                		return true;
					}
					catch(Exception e)
					{
						logger.error("Unable to stop the service",e);
						return false;
					}
                        	}
                        	else
                        		continue;
                	}
			return false;
		}
		catch(Exception e)
		{
			logger.error("Error in stopping",e);
			return false;
		}
        }

	public boolean startservice(String id)
        {
        
		try
		{ 
			Iterator itr = All();

                	while(itr.hasNext())
                	{
                        	AbstractDeployableObject object = (AbstractDeployableObject)itr.next();

                        	String name = object.getName();
                        	if(name.equals(id))
                        	{
			
                                	//object.start();
                        		soaDeployer.start(object.getId());
                                	return true;

                        	}
                        	else
                        		continue;
                	}
			return false;
        	}
		catch(Exception e)
		{
			logger.error("Error in Starting Service ",e);
			return false;
		}
	}

	public boolean undeployservice(String id)
        {
		try
		{
                	Iterator itr = All();

                	while(itr.hasNext())
                	{
                        	AbstractDeployableObject object = (AbstractDeployableObject)itr.next();

                        	String name = object.getName();
                        	if(name.equals(id))
                        	{
                                //	object.undeploy();
                                	soaDeployer.undeploy(object.getId());
                                	return true;
                        	}
                        	else
                        		continue;
                	}
			return false;
		}
		catch(Exception e)
		{
			logger.error("Error in Undeploying services",e);
			return false;
		}
        }

	public boolean activateservice(String id)
        {
		try
		{
                	Iterator itr = All();

                	while(itr.hasNext())
                	{
                        	AbstractDeployableObject object = (AbstractDeployableObject)itr.next();

                        	String name = object.getName();
                        	if(name.equals(id))
                        	{
                                //	object.activate();
                        		soaDeployer.activate(object.getId());
                                	return true;
                        	}
                        	else
                        		continue;
                	}
			return false;
        	}
		catch(Exception e)
		{
			logger.error("Error in Activation ",e);
			return false;
		}
	}

	public boolean deactivateservice(String id)
        {
		try
		{
                	Iterator itr = All();

                	while(itr.hasNext())
                	{
                        	AbstractDeployableObject object = (AbstractDeployableObject)itr.next();

                        	String name = object.getName();
                        	if(name.equals(id))
                        	{
                                	//object.deactivate(id);
                                //	object.deactivate();
                        		soaDeployer.deactivate(object.getId());
                                	return true;
                        	}
                        	else
                        		continue;
                	}

			return false;
		}
		catch(Exception e)
		{
			logger.error("Error in Deactivation ",e);
			return false;
		}
        }

}
