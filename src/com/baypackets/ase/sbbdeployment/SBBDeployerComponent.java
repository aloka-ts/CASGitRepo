package com.baypackets.ase.sbbdeployment;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.common.exceptions.ShutdownFailedException;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.deployer.AbstractDeployableObject;
import com.baypackets.ase.deployer.DeployerFactoryImpl;
import com.baypackets.ase.servicemgmt.ComponentDeploymentStatus;
import com.baypackets.ase.soa.common.SoaConstants;
import com.baypackets.ase.spi.deployer.DeployableObject;
import com.baypackets.ase.spi.deployer.Deployer;
import com.baypackets.ase.util.Constants;

import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;


public class SBBDeployerComponent implements MComponent	{

	private static final String DEPLOY_DIR = Constants.ASE_HOME+"/sbb/";
	private static Logger m_logger = Logger.getLogger(SBBDeployerComponent.class);
	private SbbDeployer m_sbbDeployer = null;
	private ComponentDeploymentStatus deploymentStatus = null;
	
	public void changeState(MComponentState state) throws UnableToChangeStateException {
		try {
			if (state.getValue() == MComponentState.LOADED)	{
				this.initialize();
			}
			if (state.getValue() == MComponentState.RUNNING) 	{
				this.start();
			}
			if (state.getValue() == MComponentState.STOPPED) 	{
				this.stop();
			}
		} catch(Exception e)	{
			m_logger.error("SBB Deployment Module changeState", e);
			throw new UnableToChangeStateException(e.getMessage());
		}
	}

	public void updateConfiguration(Pair[] configData, OperationType opType) 
	throws UnableToUpdateConfigException {
		//No Operation
	}


	private void initialize() throws Exception	{
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("Entering initialize() of SBB Self Deployment Framework");
		}
		DeployerFactoryImpl factory = 
			(DeployerFactoryImpl)Registry.lookup(Constants.NAME_DEPLOYER_FACTORY);
		m_sbbDeployer = (SbbDeployer) factory.getDeployer(DeployableObject.TYPE_SBB);
		m_sbbDeployer.initialize();
		if(m_logger.isDebugEnabled()) {
			m_logger.debug("SBB Deployment Framework Initialized successfully");
		}
	}

	private void start() throws StartupFailedException {
		m_sbbDeployer.start();
		this.deploySBB();
		synchronized (deploymentStatus) {
			deploymentStatus.setSbbDeployed(true);
			if(m_logger.isDebugEnabled())
				m_logger.debug("SBB deployed");
			if (deploymentStatus.isSysAppsDeployed()){
				if(m_logger.isDebugEnabled())
					m_logger.debug("notifying the Ems Adaptor");
				deploymentStatus.notify();
			}
		}
	}

	private void stop()	throws ShutdownFailedException {
		//No Operation
	}

	private void deploySBB() {
		try {
			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Deploying the SAS SBB");
			}

			File deployDir = new File(DEPLOY_DIR);
			if(! deployDir.exists()) {
				m_logger.error("Unable to deploy SAS SBB: Package not found:");
				m_logger.error("Package = " + deployDir.getAbsolutePath());
				return;
			}
			File sbbDeployable = new File (deployDir.getAbsolutePath().concat("/sbb-impl.jar"));

			if(sbbDeployable == null) {
				m_logger.error("SBB jar file not found in dir "+DEPLOY_DIR);
				return;
			}
			
			long lastMod = sbbDeployable.lastModified();
			Date timestamp = new Date(lastMod);

			InputStream stream = new FileInputStream(sbbDeployable);
			DeployerFactoryImpl factory = 
				(DeployerFactoryImpl)Registry.lookup(Constants.NAME_DEPLOYER_FACTORY);
			Deployer deployer = factory.getDeployer(DeployableObject.TYPE_SBB);
			AbstractDeployableObject deployableObj = (AbstractDeployableObject)deployer.
			                                                                    deploy (null,
			                                                                    		timestamp.toString(),
					                                                                    Deployer.DEFAULT_PRIORITY,
					                                                                    null,
					                                                                    stream,
					                                                                    Deployer.CLIENT_SBB);
			deployer.start(deployableObj.getId());


			if(m_logger.isDebugEnabled()) {
				m_logger.debug("Going to start SAS SBB");
			}

			deployer.activate(deployableObj.getId());

		} catch(Exception e) {
			m_logger.error("Unable to activate SAS SBB",e);
		}
	}
	
    public ComponentDeploymentStatus getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(ComponentDeploymentStatus deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
	}
}
