package com.baypackets.ase.jmxmanagement;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.baypackets.ase.common.AseEventListener;
import com.baypackets.ase.common.Registry;
import com.baypackets.ase.container.AseContext;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.deployer.ApplicationDeployer;
import com.baypackets.ase.deployer.ResourceDeployer;
import com.baypackets.ase.sbbdeployment.SbbDeployer;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.emsagent.GenericComponentManager;
//import RSIEms.ServiceMgmtSession;

public class ServiceManagement implements ServiceManagementMBean {
	private static Logger logger = Logger.getLogger(ServiceManagement.class);

	private DeployerFactory deployerFactoryImpl;

	private ApplicationDeployer appDeployer;
	private ResourceDeployer resourceDeployer;
	private SbbDeployer sbbDeployer;

	private int status = 0;

	private Iterator initiallydeployed = null; // Iterator of Deployable Object

	public ServiceManagement() {
		if (logger.isInfoEnabled()) {
			logger.info("ServiceManagement constructor ");
		}
	}

	public void initialize() {
		deployerFactoryImpl = (DeployerFactory) Registry
				.lookup(DeployerFactory.class.getName());
		if (logger.isInfoEnabled()) {
			logger.info("The Service Management has been initialized");
		}

		appDeployer = (ApplicationDeployer) deployerFactoryImpl
				.getDeployer(DeployableObject.TYPE_SERVLET_APP);
		if (logger.isInfoEnabled()) {
			logger.info("The application deployer " + appDeployer);
		}

		resourceDeployer = (ResourceDeployer) deployerFactoryImpl
				.getDeployer(DeployableObject.TYPE_RESOURCE);
		if (logger.isInfoEnabled()) {
			logger.info("The Resource deployer " + resourceDeployer);
		}
		sbbDeployer = (SbbDeployer) deployerFactoryImpl
				.getDeployer(DeployableObject.TYPE_SBB);
		if (logger.isInfoEnabled()) {
			logger.info("The SBB deployer " + sbbDeployer);
		}

		status = 1;

		initiallydeployed = appDeployer.findAll();
		if (logger.isInfoEnabled()) {
			logger.info("Applications Deployed ===== > " + initiallydeployed);
		}

	}

	private Iterator All() {
		return appDeployer.findAll();
	}

	public boolean deploy(String ServiceName, String version, String priority,
			String contextPath, HashMap map) {
		try {

			String ContextPath = contextPath;
			int appPriority = Integer.parseInt(priority);
			String appVersion = version;
			String appName = ServiceName;
			if (logger.isInfoEnabled()) {
				logger.info("Information about the service to be deployed by IDE on SAS");
				logger.info("ContextPath ==== >" + ContextPath);
				logger.info("AppPriority ==== > " + appPriority);
				logger.info("AppName ===== > " + appName);
				logger.info("AppVersion ==== > " + appVersion);
			}

			SarFileByteArray sarFileByteArrays = (SarFileByteArray) map
					.get("sar");
			byte[] bytes = sarFileByteArrays.getByteArray();
			InputStream inputStream = new ByteArrayInputStream(bytes);
			if (logger.isInfoEnabled()) {
				logger.info("In the Deployer METHOD JMX");
			}
			String deployedby = "CLIENT_IDE";
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) appDeployer
					.deploy(appName, appVersion, appPriority, ContextPath,
							inputStream, deployedby);
			if (deployableobject != null) {
				return true;
			} else
				return false;
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception   ", e);
			}
			return false;
		}
	}

	public boolean redeploy(String ServiceName, String version,
			String priority, String contextPath, HashMap map) {
		try {

			String ContextPath = contextPath;
			int appPriority = Integer.parseInt(priority);
			String appVersion = version;
			String appName = ServiceName;
			if (logger.isInfoEnabled()) {
				logger.info("Information about the service to be deployed by IDE on SAS");
				logger.info("ContextPath ==== >" + ContextPath);
				logger.info("AppPriority ==== > " + appPriority);
				logger.info("AppName ===== > " + appName);
				logger.info("AppVersion ==== > " + appVersion);
			}

			InputStream inputStream = null;
			SarFileByteArray sarFileByteArrays = (SarFileByteArray) map
					.get("sar");
			if (sarFileByteArrays == null) {
				
				if (logger.isInfoEnabled()) {
					logger.info("create file stream from context path ..");
				}
				
				inputStream = new FileInputStream(ContextPath);
			} else {
				
				if (logger.isInfoEnabled()) {
					logger.info("create file stream from sarbytearray from map ..");
				}
				byte[] bytes = sarFileByteArrays.getByteArray();
				inputStream = new ByteArrayInputStream(bytes);
			}
			
			if (logger.isInfoEnabled()) {
				logger.info("In the Deployer METHOD JMX");
			}

			/*
			 * For an alcml applicatin this path should not be null
			 */
			if(map
			.get("alcmlPathOnserver")!=null){
				
				String alcmlFilePath = (String) map
						.get("alcmlPathOnserver");
				
				if (logger.isInfoEnabled()) {
					logger.info("It seems ALCML application The alcml file path on CAS server machine is .."+alcmlFilePath);
				}
				
				
				if (map.get("alcmlFiles") != null) {
					AlcmlFileByteArray[] alcmlFiles = (AlcmlFileByteArray[]) map
							.get("alcmlFiles");

				if (alcmlFiles != null) {
		
					if (alcmlFilePath == null)
						return false;
					File file = new File(alcmlFilePath);
					file.mkdirs();
					for (AlcmlFileByteArray fileArr : alcmlFiles) {
						FileOutputStream out = new FileOutputStream(
								alcmlFilePath + "/" + fileArr.getFileName());
						out.write(fileArr.getByteArray());
						out.close();
					}
				}
			}
			}

			String deployedby = "CLIENT_IDE";
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) appDeployer
					.redeploy(appName, appVersion, appPriority, ContextPath,
							inputStream, deployedby);

			if (deployableobject != null) {
				return true;
			} else
				return false;
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception   ", e);
			}
			return false;
		}
	}

	public boolean redeploy(String ServiceName, String version,
			String priority, String contextPath) {
		try {

			String ContextPath = contextPath;
			int appPriority = Integer.parseInt(priority);
			String appVersion = version;
			String appName = ServiceName;
			if (logger.isInfoEnabled()) {
				logger.info("Information about the service to be deployed by IDE on SAS");
				logger.info("ContextPath ==== >" + ContextPath);
				logger.info("AppPriority ==== > " + appPriority);
				logger.info("AppName ===== > " + appName);
				logger.info("AppVersion ==== > " + appVersion);

			}
			InputStream inputStream = new FileInputStream(ContextPath);

			String deployedby = "CLIENT_IDE";
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) appDeployer
					.redeploy(appName, appVersion, appPriority, ContextPath,
							inputStream, deployedby);

			if (deployableobject != null) {
				return true;
			} else
				return false;
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception   ", e);
			}
			return false;
		}
	}

	public boolean deploy(String ServiceName, String version, String priority,
			String contextPath) {
		try {

			String ContextPath = contextPath;
			int appPriority = Integer.parseInt(priority);
			String appVersion = version;
			String appName = ServiceName;
			if (logger.isInfoEnabled()) {
				logger.info("Information about the service to be deployed by IDE on SAS");
				logger.info("ContextPath ==== >" + ContextPath);
				logger.info("AppPriority ==== > " + appPriority);
				logger.info("AppName ===== > " + appName);
				logger.info("AppVersion ==== > " + appVersion);
			}

			InputStream inputStream = new FileInputStream(ContextPath);

			String deployedby = "CLIENT_IDE";
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) appDeployer
					.redeploy(appName, appVersion, appPriority, ContextPath,
							inputStream, deployedby);

			if (deployableobject != null) {
				return true;
			} else
				return false;
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info("Exception   ", e);
			}
			return false;
		}
	}

	public boolean activate(String depName, String ver) {

		try {
			if (logger.isInfoEnabled()) {
				logger.info("In the activate method");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) appDeployer
					.findByNameAndVersion(depName, ver);

			if (deployableobject != null) {
				String serviceID = deployableobject.getId();

				appDeployer.activate(serviceID);
				if (logger.isInfoEnabled()) {
					logger.info("JMX Service has been activated");
				}
				return true;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("No Service found with Deployment Name : "
						+ depName + " and version : " + ver);
			}
			return false;
		} catch (Exception ee) {
			if (logger.isDebugEnabled()) {
				logger.debug("Exception ", ee);
			}

			return false;

		}
	}

	public boolean start(String depName, String ver) {

		try {
			if (logger.isInfoEnabled()) {
				logger.info("In the start method");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) appDeployer
					.findByNameAndVersion(depName, ver);

			if (deployableobject != null) {
				String serviceID = deployableobject.getId();

				appDeployer.start(serviceID);
				if (logger.isInfoEnabled()) {
					logger.info("JMX Service has been started");
				}
				return true;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("No Service found with Deployment Name : "
						+ depName + " and version : " + ver);
			}
			return false;

		}

		catch (Exception eee) {
			if (logger.isDebugEnabled()) {
				logger.debug("Exception ", eee);
			}

			return false;
		}
	}

	public boolean stop(String depName, String ver) {

		try {
			if (logger.isInfoEnabled()) {
				logger.info("In the Stop method ");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) appDeployer
					.findByNameAndVersion(depName, ver);
			if (deployableobject != null) {
				String serviceID = deployableobject.getId();

				appDeployer.stop(serviceID, true);
				if (logger.isInfoEnabled()) {
					logger.info("JMX Service has been stopped");
				}
				return true;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("No Service found with Deployment Name : "
						+ depName + " and version : " + ver);
			}
			return false;
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Exception ", e);
			}
			return false;
		}
	}

	public boolean undeploy(String depName, String ver) {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("In the undeploy method");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) appDeployer
					.findByNameAndVersion(depName, ver);

			if (deployableobject != null) {

				String serviceID = deployableobject.getId();

				appDeployer.undeploy(serviceID);
				if (logger.isInfoEnabled()) {
					logger.info("JMX Service has been undeployed");
				}
				return true;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("No Service found with Deployment Name : "
						+ depName + " and version : " + ver);
			}
			return false;
		}

		catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Exception ", e);
			}
			return false;
		}

	}

	public boolean deactivate(String depName, String ver) {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("In the Deactivate method");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) appDeployer
					.findByNameAndVersion(depName, ver);
			if (deployableobject != null) {
				String serviceID = deployableobject.getId();

				appDeployer.deactivate(serviceID);
				if (logger.isInfoEnabled()) {
					logger.info("JMX service has been deactivated");
				}
				return true;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("No Service found with Deployment Name : "
						+ depName + " and version : " + ver);
			}
			return false;
		}

		catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Exception ", e);
			}
			return false;
		}
	}

	// public Hashtable AllServicesIDE()
	// {
	// Hashtable table = new Hashtable();
	//
	// table = deployedByIDE;
	// logger.info("THE SERVICES DEPLOYED BY IDE ====> "+table);
	//
	// return table;
	// }

	public Hashtable AllServices() {
		Iterator itr = null;

		// itr = initiallydeployed;
		Hashtable services = new Hashtable();

		itr = All();
		// while(initiallydeployed.hasNext())
		while (itr.hasNext()) {
			AbstractDeployableObject object = (AbstractDeployableObject) itr
					.next();

			String name = object.getName();

			String status = object.getStatusString();

			String deployedby = object.getDeployedBy();

			String appInfo = object.getDisplayInfo();

			Hashtable info = new Hashtable();

			info.put("STATUS", status);
			info.put("DEPLOYEDBY", deployedby);

			info.put("INFO", appInfo);

			services.put(name, info);
		}
		return services;

	}

	public void stopserver() {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("Just going to stop the SIP APPlication Server");
			}
			GenericComponentManager componentManager = (GenericComponentManager) Registry
					.lookup(Constants.NAME_COMPONENT_MANAGER);

			MComponentState stopState = new MComponentState(
					MComponentState.STOPPED);

			componentManager.changeState(stopState);
			if (logger.isInfoEnabled()) {
				logger.info("Stopping the ASE Engine");
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);

		} finally {
			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("Shutdown Completed for ASE Server at "
						+ new Date());
			}
			System.exit(1);
		}

	}

	public int status() {
		return this.status;

	}

	public boolean deployservice(String id) {
		try {
			Iterator itr = All();

			while (itr.hasNext()) {
				AbstractDeployableObject object = (AbstractDeployableObject) itr
						.next();

				String name = object.getName();
				if (name.equals(id)) {
					try {
						object.deploy();
						return true;
					} catch (Exception e) {
						logger.error("Unable to deploy the service ", e);
						return false;
					}
				} else
					continue;
			}
			return false;
		} catch (Exception e) {
			logger.error("Error in Deploying Service", e);
			return false;
		}
	}

	public boolean stopservice(String id) {
		try {
			Iterator itr = All();

			while (itr.hasNext()) {
				AbstractDeployableObject object = (AbstractDeployableObject) itr
						.next();

				String name = object.getName();
				if (name.equals(id)) {
					try {
						// object.stop(true);
						String serviceID = object.getId();
						appDeployer.stop(serviceID, true);
						return true;
					} catch (Exception e) {
						logger.error("Unable to stop the service", e);
						return false;
					}
				} else
					continue;
			}
			return false;
		} catch (Exception e) {
			logger.error("Error in stopping", e);
			return false;
		}
	}

	public boolean startservice(String id) {

		try {
			Iterator itr = All();

			while (itr.hasNext()) {
				AbstractDeployableObject object = (AbstractDeployableObject) itr
						.next();

				String name = object.getName();
				if (name.equals(id)) {

					// object.start();
					String serviceID = object.getId();
					appDeployer.start(serviceID);
					return true;

				} else
					continue;
			}
			return false;
		} catch (Exception e) {
			logger.error("Error in Starting Service ", e);
			return false;
		}
	}

	public boolean undeployservice(String id) {
		try {
			Iterator itr = All();

			while (itr.hasNext()) {
				AbstractDeployableObject object = (AbstractDeployableObject) itr
						.next();

				String name = object.getName();
				if (name.equals(id)) {
					String serviceID = object.getId();

					appDeployer.undeploy(serviceID);
					// object.undeploy();
					return true;
				} else
					continue;
			}
			return false;
		} catch (Exception e) {
			logger.error("Error in Undeploying services", e);
			return false;
		}
	}

	public boolean activateservice(String id) {
		try {
			Iterator itr = All();

			while (itr.hasNext()) {
				AbstractDeployableObject object = (AbstractDeployableObject) itr
						.next();

				String name = object.getName();
				if (name.equals(id)) {
					// object.activate();
					String serviceID = object.getId();
					appDeployer.activate(serviceID);

					return true;
				} else
					continue;
			}
			return false;
		} catch (Exception e) {
			logger.error("Error in Activation ", e);
			return false;
		}
	}

	public boolean deactivateservice(String id) {
		try {
			Iterator itr = All();

			while (itr.hasNext()) {
				AbstractDeployableObject object = (AbstractDeployableObject) itr
						.next();

				String name = object.getName();
				if (name.equals(id)) {
					// object.deactivate(id);
					// object.deactivate();
					String serviceID = object.getId();
					appDeployer.deactivate(serviceID);
					return true;
				} else
					continue;
			}

			return false;
		} catch (Exception e) {
			logger.error("Error in Deactivation ", e);
			return false;
		}
	}

	@Override
	public String statusSBB() {
		String status = null;
		Iterator itr = sbbDeployer.findAll();
		if (itr.hasNext()) {
			AbstractDeployableObject deployable = (AbstractDeployableObject) itr
					.next();
			status = "SBB was last updated on " + deployable.getVersion();
		}
		return status;
	}

	@Override
	public boolean upgradeSBB(String contextPath) {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("In the JMX upgradeSBB(String) method.");

				logger.info("Information about the service to be deployed by IDE on SAS");
				logger.info("ContextPath ==== >" + contextPath);
			}

			InputStream inputStream = new FileInputStream(contextPath);

			AbstractDeployableObject deployableobject = (AbstractDeployableObject) sbbDeployer
					.upgrade(inputStream);

			if (deployableobject != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info(
						"n exception occured inside upgradeSBB(String) method : ",
						e);
			}
			return false;
		}
	}

	@Override
	public boolean upgradeSBB(HashMap map) {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("In the JMX upgradeSBB(HashMap) method.");
			}
			SarFileByteArray sarFileByteArrays = (SarFileByteArray) map
					.get("jar");
			byte[] bytes = sarFileByteArrays.getByteArray();
			InputStream inputStream = new ByteArrayInputStream(bytes);

			AbstractDeployableObject deployableobject = (AbstractDeployableObject) sbbDeployer
					.upgrade(inputStream);

			if (deployableobject != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info(
						"An exception occured inside upgradeSBB(HashMap) method : ",
						e);
			}
			return false;
		}
	}

	@Override
	public boolean activateResource(String resourceName, String version) {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("Inside JMX activateResource method.");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) resourceDeployer
					.findByNameAndVersion(resourceName, version);
			if (deployableobject != null) {
				String serviceID = deployableobject.getId();
				resourceDeployer.activate(serviceID);
				if (logger.isInfoEnabled()) {
					logger.info("Resource has been activated through the JMX interface.");
				}
				return true;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("No resource found with name : " + resourceName
						+ " and version : " + version);
			}
			return false;
		} catch (Exception ex) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"An execption occured during activation of resource on SAS : ",
						ex);
			}
			return false;
		}
	}

	@Override
	public boolean deactivateResource(String resourceName, String version) {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("IInside JMX deactivateResource method.");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) resourceDeployer
					.findByNameAndVersion(resourceName, version);
			if (deployableobject != null) {
				String serviceID = deployableobject.getId();
				resourceDeployer.deactivate(serviceID);
				if (logger.isInfoEnabled()) {
					logger.info("Resource has been activated through the JMX interface.");
				}
				return true;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("No resource found with name : " + resourceName
						+ " and version : " + version);
			}
			return false;
		} catch (Exception ex) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"An execption occured during deactivation of resource on SAS : ",
						ex);
			}
			return false;
		}
	}

	@Override
	public boolean deployResource(HashMap map) {
		try {
			SarFileByteArray sarFileByteArrays = (SarFileByteArray) map
					.get("jar");
			byte[] bytes = sarFileByteArrays.getByteArray();
			InputStream inputStream = new ByteArrayInputStream(bytes);

			String deployedby = "CLIENT_IDE";
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) resourceDeployer
					.deploy(inputStream, deployedby);
			if (deployableobject != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			if (logger.isInfoEnabled()) {
				logger.info(
						"An execption occured during deployment of resource on SAS : ",
						e);
			}
			return false;
		}
	}

	@Override
	public boolean deployResource(String contextPath) {
		try {
			InputStream inputStream = new FileInputStream(contextPath);

			String deployedby = "CLIENT_IDE";
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) resourceDeployer
					.deploy(inputStream, deployedby);

			if (deployableobject != null) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			logger.info(
					"An execption occured during deployment of resource on SAS ",
					e);
			return false;
		}
	}

	@Override
	public boolean startResource(String resourceName, String version) {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("Inside JMX startResource method.");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) resourceDeployer
					.findByNameAndVersion(resourceName, version);
			if (deployableobject != null) {
				String serviceID = deployableobject.getId();
				resourceDeployer.start(serviceID);
				if (logger.isInfoEnabled()) {
					logger.info("Resource has been started through the JMX interface");
				}
				return true;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("No resource found with name : " + resourceName
						+ " and version : " + version);
			}
			return false;
		} catch (Exception ex) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"An execption occured during start of resource on SAS : ",
						ex);
			}
			return false;
		}
	}

	@Override
	public boolean stopResource(String resourceName, String version) {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("Inside JMX stopResource method.");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) resourceDeployer
					.findByNameAndVersion(resourceName, version);
			if (deployableobject != null) {
				String serviceID = deployableobject.getId();
				resourceDeployer.stop(serviceID, true);
				if (logger.isInfoEnabled()) {
					logger.info("Resource has been stopped through the JMX interface");
				}
				return true;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("No resource found with name : " + resourceName
						+ " and version : " + version);
			}
			return false;
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"An execption occured during stop of resource on SAS : ",
						e);
			}
			return false;
		}
	}

	@Override
	public boolean undeployResource(String resourceName, String version) {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("Inside JMX undeployResource method.");
			}
			AbstractDeployableObject deployableobject = (AbstractDeployableObject) resourceDeployer
					.findByNameAndVersion(resourceName, version);
			if (deployableobject != null) {
				String serviceID = deployableobject.getId();
				resourceDeployer.undeploy(serviceID);
				if (logger.isInfoEnabled()) {
					logger.info("Resource has been undeployed through the JMX interface.");
				}
				return true;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("No resource found with Name : " + resourceName
						+ " and version : " + version);
			}
			return false;
		}

		catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug(
						"An execption occured during undeploy of resource on SAS : ",
						e);
			}
			return false;
		}
	}

	@Override
	public boolean triggerActivityTest() {
		if (logger.isDebugEnabled()) {
			logger.debug("triggerActivityTest starts...");
		}
		Iterator itr = appDeployer.find(DeployableObject.TYPE_SYSAPP,
				"tcap-provider", null);
		AseContext tcapContext = (AseContext) itr.next();
		Iterator iterator = tcapContext.getListeners(AseEventListener.class)
				.iterator();
		for (; iterator != null && iterator.hasNext();) {
			AseEventListener listener = (AseEventListener) iterator.next();
			try {
				listener.handleEvent(null);
			} catch (Throwable th) {
				logger.error(th.getMessage(), th);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("triggerActivityTest ends...");
		}
		return false;

	}
}
