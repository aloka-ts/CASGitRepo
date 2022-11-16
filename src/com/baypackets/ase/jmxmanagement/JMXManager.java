package com.baypackets.ase.jmxmanagement;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

import com.baypackets.ase.common.Registry;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.BaseContext;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;

public class JMXManager implements MComponent {
	private static Logger logger = Logger.getLogger(JMXManager.class);

	private int port = 14001; // The JMX Port

	private String localHost = null;

	private int JMXMP = 1; // 1 for JMXMP otherwise we will use rmiregistry
	private JMXConnectorServer cs = null;
	private Process rmiProcess = null;

	private ConfigRepository configRep;

	public JMXManager() {
		if (logger.isInfoEnabled()) {
			logger.info("JMXManager constructor  ");
		}
	}

	/**
	 * Changes the Component State to the state indicated by the argument
	 * passed. The states are changed according to the priority values.
	 **/
	public void changeState(MComponentState state)
			throws UnableToChangeStateException {
		try {
			if (state.getValue() == MComponentState.LOADED) {
				this.initialize();
			} else if (state.getValue() == MComponentState.RUNNING) {
				this.start();
			} else if (state.getValue() == MComponentState.STOPPED) {
				this.stop();
			}
		} catch (Exception e) {
			logger.error("Unable to start the service Management.", e);
			throw new UnableToChangeStateException(e.getMessage());
		}
	}

	public void updateConfiguration(Pair[] configData, OperationType opType)
			throws UnableToUpdateConfigException {
		// No op.
	}

	public void initialize() {
		if (logger.isInfoEnabled()) {
			logger.info("In the initialization of the JMXManager");
		}
		
		ConfigRepository m_configRepository = (ConfigRepository) Registry
				.lookup(Constants.NAME_CONFIG_REPOSITORY);

		try {
			
			this.localHost=AseUtils.getIPAddress(BaseContext
					.getConfigRepository().getValue(
							Constants.OID_BIND_ADDRESS));
//			this.localHost = InetAddress.getLocalHost().getHostAddress()
//					.toString();
			if (logger.isInfoEnabled()) {
				logger.info("Local Host for bind address ==== >" + localHost);
			}
		} catch (Exception e) {
			logger.error("Exception in getting IP address", e);
		}
		try {
			

			 String portString =
			((String)m_configRepository.getValue(Constants.PROP_SELF_JMX_PORT)).trim();
			 String jmxmpURL =
			 ((String)m_configRepository.getValue(Constants.PROP_SELF_JMX_URL)).trim();

//			String aseHome = (String) m_configRepository
//					.getValue(Constants.PROP_ASE_HOME);
//			if (logger.isInfoEnabled()) {
//				logger.info("ASEHOME ====> " + aseHome);
//			}
//			String fileName = aseHome + "/conf/jmxide.properties";
//
//			Properties jmxProperties = new Properties();
//
//			InputStream jmxideStream = new FileInputStream(fileName);
//
//			jmxProperties.load(jmxideStream);

//			String portString = (String) jmxProperties.getProperty("JMXPORT")
//					.trim();
//
			port = Integer.parseInt(portString);
//
//			String jmxmpURL = (String) jmxProperties.getProperty("JMXURL")
//					.trim();

			JMXMP = Integer.parseInt(jmxmpURL);
			if (logger.isInfoEnabled()) {
				logger.info("JMXSERVER INOF : PORT == > " + port + ": URL : "
						+ JMXMP);
			}

			if (JMXMP == 0) {
				try {
					Runtime runtime = Runtime.getRuntime();
					if (logger.isInfoEnabled()) {
						logger.info("STarting RMI REGISTRY AT PORT ==>" + port);
					}

					rmiProcess = runtime.exec("rmiregistry " + port);
				} catch (Exception e) {
					logger.error("RMI REGISTRY CANT BE STARTED", e);
				}
			}
		} catch (Exception e) {
			logger.error("Exception in reading jmxide.properties file", e);
		}

	}

	public void start() {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("In the start method of the JMXManager");
			}

			MBeanServer mbeanServer = MBeanServerFactory.createMBeanServer();
			if (logger.isInfoEnabled()) {
				logger.info("MBeanServer===== > " + mbeanServer);
			}
			String domain = mbeanServer.getDefaultDomain();
			if (logger.isInfoEnabled()) {
				logger.info("Default Domain==== > " + domain);
			}
			String mbeanClassName = "com.baypackets.ase.jmxmanagement.ServiceManagement";

			String logWatcherMBeanClassName = "com.baypackets.ase.jmxmanagement.LogWatcher";

			String soaServiceMBeanClassName = "com.baypackets.ase.jmxmanagement.SOAServiceManagement";

			String soaServiceProvMBeanClassName = "com.baypackets.ase.jmxmanagement.SOAServiceProvisioning";

			String mbeanObjectNameStr = domain + ":type=" + mbeanClassName
					+ ",index=1";

			String logWatchermbeanObjectNameStr = domain + ":type="
					+ logWatcherMBeanClassName + ",index=1";

			String soaServicembeanObjectNameStr = domain + ":type="
					+ soaServiceMBeanClassName + ",index=1";

			String soaServiceProvmbeanObjectNameStr = domain + ":type="
					+ soaServiceProvMBeanClassName + ",index=1";
			if (logger.isInfoEnabled()) {
				logger.info("mbeanObjectNameStr==========> "
						+ mbeanObjectNameStr);
				logger.info("LogWatchermbeanObjectNameStr==========> "
						+ logWatchermbeanObjectNameStr);
				logger.info("soaServicembeanObjectNameStr==========> "
						+ soaServicembeanObjectNameStr);
				logger.info("soaServiceProvmbeanObjectNameStr==========> "
						+ soaServiceProvmbeanObjectNameStr);
			}

			// ObjectName mbeanObjectName =
			// createSimpleMBean(mbeanServer,mbeanClassName,mbeanObjectNameStr);

			ObjectName mbeanObjectName = new ObjectName(mbeanObjectNameStr);

			ObjectName logWatchermbeanObjectName = new ObjectName(
					logWatchermbeanObjectNameStr);

			ObjectName soaServicembeanObjectName = new ObjectName(
					soaServicembeanObjectNameStr);

			ObjectName soaServiceProvmbeanObjectName = new ObjectName(
					soaServiceProvmbeanObjectNameStr);
			if (logger.isInfoEnabled()) {
				logger.info("mbeanObjectName====> " + mbeanObjectName);

				logger.info("logWatchermbeanObjectName====> "
						+ logWatchermbeanObjectName);

				logger.info("soaServicembeanObjectName====> "
						+ soaServicembeanObjectName);

				logger.info("soaServiceProvmbeanObjectName====> "
						+ soaServiceProvmbeanObjectName);
			}

			ServiceManagement manage = new ServiceManagement();

			LogWatcher watcher = new LogWatcher();

			SOAServiceManagement soamanage = new SOAServiceManagement();

			SOAServiceProvisioning prov = new SOAServiceProvisioning();

			manage.initialize();
			watcher.intialize();
			soamanage.initialize();
			prov.intialize();

			if (logger.isInfoEnabled()) {
				logger.info("Service Management has been initialized");
				logger.info("LogWatcher has been initialized");
				logger.info("SOAServiceManagement has been initialized");
				logger.info("SOAServiceProvisioning has been initialized");

				logger.info("ServiceManagement ============= > " + manage);
				logger.info("LogWatcher ============= > " + watcher);
				logger.info("SOAServiceManagement ============= > " + soamanage);
				logger.info("SOAServiceProvisioning ============= > " + prov);
			}

			mbeanServer.registerMBean(manage, mbeanObjectName);
			mbeanServer.registerMBean(watcher, logWatchermbeanObjectName);
			mbeanServer.registerMBean(soamanage, soaServicembeanObjectName);
			mbeanServer.registerMBean(prov, soaServiceProvmbeanObjectName);

			JMXServiceURL url = null;

			if (JMXMP == 1) {

				url = new JMXServiceURL("jmxmp", localHost, port);
			} else {

				// JMXServiceURL url = new
				// JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:14001/server");
				url = new JMXServiceURL(
						"service:jmx:rmi:///jndi/rmi://localhost:" + port
								+ "/jmxsasserver");

				// logger.info("STarting RMI REGISTRY AT PORT ==>"+port);

				/*
				 * try { Runtime runtime = Runtime.getRuntime();
				 * 
				 * rmiProcess = runtime.exec("rmiregistry "+port); url = new
				 * JMXServiceURL
				 * ("service:jmx:rmi:///jndi/rmi://localhost:"+port+
				 * "/jmxsasserver"); } catch(Exception e) {
				 * logger.error("RMI REGISTRY CANT BE STARTED",e); }
				 */
			}
			if (logger.isInfoEnabled()) {
				logger.info("JMXServiceURL====== > " + url);
			}

			cs = JMXConnectorServerFactory.newJMXConnectorServer(url, null,
					mbeanServer);

			cs.start();
			if (logger.isInfoEnabled()) {
				logger.info("Waiting FOR EVER JMX");
			}
		} catch (Exception e) {

			if (logger.isDebugEnabled()) {
				logger.debug(e);
			}
		}

	}

	public void stop() {
		logger.info("In the stop method of the Service Management");

		try {
			cs.stop(); // 15th April 2006by NJADAUN
			if (logger.isInfoEnabled()) {
				logger.info("Stopiing the JMX Manager");
			}

			if (rmiProcess != null)
				rmiProcess.destroy();
		}

		catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Exception  ", e);
			}

		}
	}

	/*
	 * private static ObjectName createSimpleMBean(MBeanServer mbs, String
	 * mbeanClassName, String mbeanObjectNameStr) {
	 * 
	 * logger.info("In the CreateSimpleMBean method");
	 * 
	 * try { ObjectName mbeanObjectName =
	 * ObjectName.getInstance(mbeanObjectNameStr); if(logger.isInfoEnabled() ){
	 * logger.info("mbeanObjectName===== > "+mbeanObjectName); }
	 * mbs.createMBean(mbeanClassName,mbeanObjectName);
	 * 
	 * return mbeanObjectName;
	 * 
	 * }
	 * 
	 * catch(Exception e) { if(logger.isInfoEnabled() ){
	 * logger.info("Exception  ",e); }
	 * 
	 * return null; } }
	 */
}
