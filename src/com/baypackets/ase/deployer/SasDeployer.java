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
//      File:   SasDeployer.java
//      Desc:   This class extends the DeployerImpl. It will be used as 
//              entry point for different Adaptors to envokes methods on them.
//
//      Author                          Date            Description
//      Suresh Kr. Jangir               17/12/07        Initial Creation
//
//***********************************************************************************


package com.baypackets.ase.deployer;

import org.apache.log4j.Logger;

import java.net.URI;
import java.io.InputStream;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.ShutdownFailedException;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.container.exceptions.ActivationFailedException;
import com.baypackets.ase.container.exceptions.UpgradeFailedException;
import com.baypackets.ase.container.exceptions.DeactivationFailedException;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.container.exceptions.UndeploymentFailedException;
import com.baypackets.ase.container.exceptions.RedeploymentFailedException;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.spi.deployer.DeploymentListener;
import com.baypackets.ase.spi.deployer.DeployerFactory;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.soa.deployer.SoaDeployer;
import com.baypackets.ase.soa.common.SoaConstants;
import com.baypackets.bayprocessor.slee.common.BaseContext;



/**
 *
 * @author Suresh Kr. Jangir
 */


public class SasDeployer extends DeployerImpl {

	private static Logger m_logger = Logger.getLogger(SasDeployer.class);
	private static  File DEPLOY_DIR = new File(Constants.ASE_HOME, Constants.FILE_HOST_DIR);
	
	private SoaDeployer m_soaDeployer = null;
	private ApplicationDeployer m_appDeployer = null;
	DeployerFactoryImpl factory = null;
	
	public SasDeployer() {
		
		String appDeployDir = BaseContext.getConfigRepository().getValue(
				Constants.PROP_APP_DEPLOY_DIR);
		
		if (appDeployDir != null && !appDeployDir.isEmpty()) {
			DEPLOY_DIR = new File(appDeployDir);
		}
	}

	public void initialize() throws Exception {
		super.initialize();
		
		factory = (DeployerFactoryImpl)Registry.lookup(Constants.NAME_DEPLOYER_FACTORY);
		this.m_soaDeployer = (SoaDeployer)factory.getDeployer(DeployableObject.TYPE_SOA_SERVLET);
		this.m_appDeployer = (ApplicationDeployer)factory.getDeployer(DeployableObject.TYPE_SERVLET_APP);
	}

	 public void start() throws StartupFailedException {
		////no need to invoke start() on super()
		this.started = true;
	 }



	public DeployableObject deploy(String name, String version, int priority, String contextPath, InputStream stream, String deployedBy) throws DeploymentFailedException {
		
		DeployableObject deployable = null;	
		int type;
		InputStream inStream = null;
		try {
			byte[] binary = getByteArray(stream);
			type = this.findType(name, binary);
			inStream = new ByteArrayInputStream(binary);
			if(m_logger.isDebugEnabled()) {

			m_logger.debug("Deployer Type: "+type);
			}
			if(type == DeployableObject.TYPE_PURE_SOA ||
				type == DeployableObject.TYPE_SOA_SERVLET ||
				type == DeployableObject.TYPE_SIMPLE_SOA_APP) {
				if(m_logger.isDebugEnabled()) {

				m_logger.debug("Delegate to SoaDeployer");
				}
				synchronized (this.m_soaDeployer){
				deployable = this.m_soaDeployer.
							deploy(name,version,priority,contextPath,inStream,deployedBy);
				}
			} else {
				if(m_logger.isDebugEnabled()) {

				m_logger.debug("Delegate to AppDeployer");
				}
				synchronized (this.m_appDeployer){
				deployable = this.m_appDeployer.
							deploy(name,version,priority,contextPath, inStream,deployedBy);
				}
			}
		} catch(Exception e) {
			throw new DeploymentFailedException(e.getMessage());
		}
		return deployable;
	}


	public DeployableObject redeploy(String name, String version, int priority, String contextPath, InputStream stream, String deployedBy) throws RedeploymentFailedException {

		return this.getDeployer(name).redeploy(name,version,priority,contextPath,stream,deployedBy);
	}

	public DeployableObject deploy(InputStream stream, String deployedBy) throws DeploymentFailedException {
		if(m_logger.isDebugEnabled()) {

		m_logger.debug("Entering deploy method of SasDeployer with 2 arg ");
		}
		DeployableObject deployable = null;
		int type;
		InputStream inStream = null;
		try {
			byte[] binary = getByteArray(stream);
			
			if(!this.isCasXmlExist(binary)){
			if(!this.isSasXmlExist(binary)){
					throw new DeploymentFailedException("Error: ..archive file should contain sas.xml / cas.xml  or use 4 argument command ");
			}
			}
			
			type = this.findType(null, binary);
			inStream = new ByteArrayInputStream(binary);
			if(type == DeployableObject.TYPE_PURE_SOA || 
				type == DeployableObject.TYPE_SOA_SERVLET ||
				type == DeployableObject.TYPE_SIMPLE_SOA_APP) {
				synchronized (this.m_soaDeployer){
				deployable = this.m_soaDeployer.deploy(inStream,deployedBy,type);
				}
			} else {
				synchronized (this.m_appDeployer){
				deployable = this.m_appDeployer.deploy(inStream,deployedBy);
				}
			}
		} catch(Exception e) {
			m_logger.error("Unable to assign Type",e);
			throw new DeploymentFailedException(e.getMessage());
		}
		return deployable;
	}

	public DeployableObject upgrade(String appName, String version, int priority, InputStream stream) throws UpgradeFailedException {
		return this.getDeployerByName(appName). upgrade(appName,version,priority,stream);
	}


	public DeployableObject upgrade(InputStream stream) throws UpgradeFailedException {
		DeployableObject deployable = null;
		int type;
		InputStream inStream = null;
		try {
			byte[] binary = getByteArray(stream);
			type = this.findType(null, binary);
			inStream = new ByteArrayInputStream(binary);
			if(type == DeployableObject.TYPE_PURE_SOA || type == DeployableObject.TYPE_SOA_SERVLET) {
				synchronized (this.m_soaDeployer){
				deployable = this.m_soaDeployer.upgrade(inStream);
				}
			} else {
				synchronized (this.m_appDeployer) {
				deployable = this.m_appDeployer.upgrade(inStream);
				}
			}
			//deployableMap.put(deployable.getObjectName(), new Integer(type));
		} catch(Exception e) {
			m_logger.error("Unable to assign Type",e);
			throw new UpgradeFailedException(e.getMessage());
		}
		return deployable;
	}

	public DeployableObject undeploy(String id) throws UndeploymentFailedException {
		Deployer deployer = this.getDeployer(id);
		if(deployer == null) {
			throw new UndeploymentFailedException("Unable to find Deployer");
		}
		synchronized (deployer){
			
		return deployer.undeploy(id);
		}
	}

	public DeployableObject start(String id) throws StartupFailedException {
		Deployer deployer = this.getDeployer(id);
		if(deployer == null) {
			throw new StartupFailedException("Unable to find Deployer");
		}
		synchronized (deployer){
			DeployableObject dobj = deployer.start(id);
		return dobj;
		}
	}


	public DeployableObject stop(String id, boolean immediate) throws ShutdownFailedException {
		Deployer deployer = this.getDeployer(id);
		if(deployer == null) {
			throw new ShutdownFailedException("Unable to find Deployer");
		}
		synchronized (deployer){
			
		return deployer.stop(id, immediate);
		
		}
	}

	
	public DeployableObject activate(String id) throws ActivationFailedException {
		Deployer deployer = this.getDeployer(id);
		if(deployer == null) {
			throw new ActivationFailedException("Unable to find Deployer");
		}
		synchronized (deployer){
			
		return deployer.activate(id);
		}
	}

	public DeployableObject deactivate(String id) throws DeactivationFailedException {
		Deployer deployer = this.getDeployer(id);
		if(deployer == null) {
			throw new DeactivationFailedException("Unable to find Deployer");
		}
		synchronized (deployer){
			
		return deployer.deactivate(id);
		}
	}

	public DeployableObject findById(String id) {
		Deployer deployer = this.getDeployer(id);
		if(deployer == null) {
			return null;
		} 
		return deployer.findById(id);
	}
	
	public DeployableObject findByNameAndVersion(String name, String version) {
		Deployer deployer = this.getDeployerByName(name);
		if(deployer == null) {
			return null;
		}
		return deployer.findByNameAndVersion(name, version);
	}

	public Iterator findByName(String name) {
		// Find on both the deployers
		Deployer deployer = this.getDeployerByName(name);
		if(deployer == null) {
			return null;
		}
		return deployer.findByName(name);
		

	}

	public Iterator findAll() {
		Iterator soa = m_soaDeployer.findAll();
		
		Iterator app = m_appDeployer.findAll();
		List returnList = new ArrayList();
		while(soa.hasNext()) {
			returnList.add(soa.next());
		}
		while(app.hasNext()) {
			returnList.add(app.next());
		}
		return returnList.iterator();

	}

	public List getAppNames() {
		List soaApp =null;
		List apps =null;
		
		synchronized (this.m_soaDeployer){
			
		soaApp = m_soaDeployer.getAppNames();
		}
		synchronized (this.m_appDeployer){
			
		apps = m_appDeployer.getAppNames();
		}
		soaApp.addAll(apps);
		return soaApp;

	}

	public void registerStateChangeListener(String id, DeploymentListener listener) {
		// No Implementation

	}

	public void unregisterStateChangeListener(String id, DeploymentListener listener) {
		// No Implementation
	}


	public short getType() {
		return DeployableObject.TYPE_SAS_APPLICATION;

	}

	// This method shd not be invoked in deploy. 
	private Deployer getDeployer(String id) {
		int type = this.findType(id);
               
		Deployer deployer = null;
		if(type == DeployableObject.TYPE_SOA_SERVLET || 
			type == DeployableObject.TYPE_PURE_SOA || 
			type == DeployableObject.TYPE_SIMPLE_SOA_APP) {
			deployer = this.m_soaDeployer;
		} else if(type == DeployableObject.TYPE_SERVLET_APP) {
			deployer = this.m_appDeployer;
		} else {
			m_logger.error("Deployer Not Found: Unknown Application name");
		}
		return deployer;
	}

	private Deployer getDeployerByName(String name) {
		int type = this.findTypeByName(name);
                              
		Deployer deployer = null;
		if(type == DeployableObject.TYPE_SOA_SERVLET || 
			type == DeployableObject.TYPE_PURE_SOA || 
			type == DeployableObject.TYPE_SIMPLE_SOA_APP) {
			deployer = this.m_soaDeployer;
		} else if(type == DeployableObject.TYPE_SERVLET_APP) {
			deployer = this.m_appDeployer;
		} else {
			m_logger.error("Deployer Not Found: Unknown Application name");
		}
		return deployer;
	}	


	public String[] getDDNames() {
		String[] ddNames = new String[5];
		return ddNames;
	}

	public AbstractDeployableObject createDeployableObject() {
		return null;
	}

	public String getDAOClassName() {
		return null;
	}

	public File getDeployDirectory() {
		return DEPLOY_DIR;
	}


	private String createId(String name, String version, int type) {
		return name+"_"+version+"_"+type;
	}

	//private String getName(String id) {
	//	return id.substring(0,id.indexOf("_"));
	//}
}

	


