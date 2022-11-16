package com.baypackets.ase.activemq;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.control.AseRoles;
import com.baypackets.ase.control.ClusterManager;
import com.baypackets.ase.control.PartitionInfo;
import com.baypackets.ase.control.RoleChangeListener;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.InitializationFailedException;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

public class EmbeddedActiveMQ implements RoleChangeListener, MComponent {

	private static Logger logger = Logger.getLogger(EmbeddedActiveMQ.class);
	private short role ;
	ConfigRepository m_configRepository;
	String osName;
	Runtime runtime;
	String aseHome;
	String activeMQHome;
	String brokerPath;


	/**
	 * This method is invoked to update the state of this component.
	 * If the value of the given "state" parameter is LOADED, then
	 * this object's "initialize" method will be called.
	 */
	public void changeState(MComponentState state) throws UnableToChangeStateException {
		if (logger.isDebugEnabled()) {
			logger.debug("changeState(): Setting component state to: " + state.getValue());
		}

		try {
			if (state.getValue() == MComponentState.LOADED) 
				this.initialize();
			else if (state.getValue() == MComponentState.STOPPED)
				this.killActiveMQ();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new UnableToChangeStateException(e.getMessage());
		}
	}

	public void updateConfiguration(Pair[] arg0, OperationType arg1)
	throws UnableToUpdateConfigException {
		//No op
	}

	/**
	 * Register with the ClusterManager so that this component will
	 *          be notified whenever the system's cluster role has changed.
	 */
	public void initialize() throws InitializationFailedException {
		if (logger.isDebugEnabled()) {
			logger.debug("initialize() called...");
		}

		try {
			m_configRepository = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

			// Register with the ClusterManager...
			ClusterManager clusterManager = (ClusterManager)Registry.lookup(Constants.NAME_CLUSTER_MGR);
			clusterManager.registerRoleChangeListener(this,Constants.RCL_ACTIVEMQ_PRIORITY);
			
			osName = System.getProperty(AseStrings.OS_NAME);
			runtime = Runtime.getRuntime();
			aseHome = (String)m_configRepository.getValue(Constants.PROP_ASE_HOME);
			activeMQHome = aseHome + "/Common/thirdParty/apache-activemq-5.3.0/bin/";
			brokerPath = (String)m_configRepository.getValue(Constants.PROP_JMS_BROKER_PATH);
		} catch (Exception e) {
			String msg = "Error occurred during Embedded ActiveMQ Intialization: " + e.getMessage();
			logger.error(msg, e);
			throw new InitializationFailedException(msg);
		}
	}

	public void roleChanged(String clusterId, PartitionInfo partitionInfo) {
		if (logger.isDebugEnabled()) {
			logger.debug("roleChanged(): role is changed to " + partitionInfo.getRole());
		}
		this.role=partitionInfo.getRole();
		int runBroker;
		
		try{
			runBroker = Integer.parseInt(m_configRepository.getValue(Constants.PROP_JMS_RUNBROKER_FLAG));
		}catch(NumberFormatException ne) {
			runBroker = 0;
			logger.error("Error in parsing jms.run.broker flag");
		}
		
		if (role == AseRoles.ACTIVE) {

			if(runBroker == 1) {
				if (logger.isInfoEnabled()) {
				logger.info("JMS RunBroker flag is set to true. So starting JMS Broker");
				}
				brokerPath = brokerPath.replace('\\','/');
				String logDirectory = (String)m_configRepository.getValue(Constants.OID_LOGS_DIR);
				if(osName.startsWith(AseStrings.OS_WINDOWS)) {
					try {
						ProcessBuilder pb = new ProcessBuilder(activeMQHome+"run_activemq.bat", activeMQHome, brokerPath);
						pb.start();
					} catch (IOException e) {
						logger.error("Exception in starting JMS Broker" + e);
					}
				}
				else if(osName.equals(AseStrings.OS_SUN) ||(osName.equals(AseStrings.OS_LINUX))) {
					try {
						ProcessBuilder pb = new ProcessBuilder(activeMQHome+"run_activemq.sh", logDirectory, activeMQHome, brokerPath);
						pb.start();
					} catch (IOException e) {
						logger.error("Exception in starting JMS Broker" + e);
					}
				}
			}
			else {
				if (logger.isInfoEnabled()) {
					logger.info("JMS RunBroker flag is set to false. Returning");
				}
			}
		}
		else if(role == AseRoles.STANDBY) {
			if(runBroker == 1) {
				if (logger.isInfoEnabled()) {
					logger.info("Role Changed to standby. So killing active JMS broker");
				}
				aseHome = (String)m_configRepository.getValue(Constants.PROP_ASE_HOME);
				activeMQHome = aseHome + "/Common/thirdParty/apache-activemq-5.3.0/bin/";

				if(osName.equals(AseStrings.OS_SUN) ||(osName.equals(AseStrings.OS_LINUX))) {
					try {
						runtime.exec(activeMQHome+"kill_activemq.sh");
					} catch (IOException e) {
						logger.error("Exception in killing JMS Broker" + e);
					}
				}
			}
		}
	}

	private void killActiveMQ() {
		int runBroker = 0;
		try{
			runBroker = Integer.parseInt(m_configRepository.getValue(Constants.PROP_JMS_RUNBROKER_FLAG));
		}catch(NumberFormatException ne) {
			runBroker = 0;
			logger.error("Error in parsing jms.run.broker flag");
		}
		if(runBroker == 1) {
			if (logger.isDebugEnabled()) {
				logger.debug("Since component is in stopped state, Killing any active JMS broker");
			}
			if(osName.equals(AseStrings.OS_SUN) ||(osName.equals(AseStrings.OS_LINUX))) {
				try {
					runtime.exec(activeMQHome+"kill_activemq.sh");
				} catch (IOException e) {
					logger.error("Exception in killing JMS Broker" + e);
				}
			}
		}
	}
}