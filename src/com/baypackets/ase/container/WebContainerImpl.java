package com.baypackets.ase.container;

import com.baypackets.ase.container.exceptions.*;
import com.baypackets.ase.common.*;
import com.baypackets.ase.common.exceptions.*;
import com.baypackets.ase.util.AseStrings;
import com.baypackets.ase.util.AseUtils;
import com.baypackets.ase.util.Constants;
import com.baypackets.ase.util.FileUtils;
import com.baypackets.ase.startup.AseClassLoader;
import com.baypackets.bayprocessor.agent.MComponent;
import com.baypackets.bayprocessor.agent.MComponentState;
import com.baypackets.bayprocessor.agent.OperationType;
import com.baypackets.bayprocessor.slee.common.ConfigRepository;
import com.baypackets.bayprocessor.slee.common.Pair;
import com.baypackets.bayprocessor.slee.common.UnableToChangeStateException;
import com.baypackets.bayprocessor.slee.common.UnableToUpdateConfigException;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.apache.log4j.*;

public class WebContainerImpl implements WebContainer, MComponent {

	private static Logger _logger = Logger.getLogger(WebContainerImpl.class);
	
	private WebContainer webContainer;
	private ClassLoader loader;
	private ConfigRepository config;
	private Object lock = new Object();

	public void changeState(MComponentState state) throws UnableToChangeStateException {
		try {
			if (state.getValue() == MComponentState.LOADED) {
				this.initialize(null);
			} else if (state.getValue() == MComponentState.RUNNING) {
				this.start();
			} else if (state.getValue() == MComponentState.STOPPED) {
				this.stop();
			} else if (state.getValue() == MComponentState.SOFT_STOP) {
				this.softStop();
			}
		} catch (Exception e) {
			String msg = "Error occurred while changing WebContainer component state: " + e.getMessage();
			_logger.error(msg, e);
			throw new UnableToChangeStateException(msg);
		}
	}

	public void updateConfiguration(Pair[] configData, OperationType opType) throws UnableToUpdateConfigException {
		// No op
	}

	public WebContainerState getState() {
		return this.webContainer != null ? this.webContainer.getState() : WebContainerState.STOPPED;
	}

	public void initialize(Properties props) throws InitializationFailedException {
		try {
			this.config = (ConfigRepository)Registry.lookup(Constants.NAME_CONFIG_REPOSITORY);

			String className = this.config.getValue(Constants.PROP_HTTP_CONTAINER_CLASS);
			
			if (className == null) {
				return;
			}
			this.webContainer = (WebContainer)Class.forName(className, true, this.getClassLoader()).newInstance();	
			this.webContainer.initialize(this.getWebContainerParams());
		} catch (Exception e) {
			String msg = "Error occurred while initializing WebContainer component: " + e.getMessage();
			_logger.error(msg, e);
			throw new InitializationFailedException(msg);
		}
	}

	private Properties getWebContainerParams() {
		Properties props = new Properties();
		props.setProperty(Constants.OID_BIND_ADDRESS, AseUtils.getIPAddress(this.config.getValue(Constants.OID_BIND_ADDRESS)));
		props.setProperty(Constants.OID_HTTP_FLOATING_IP, AseUtils.getIPAddressList(this.config.getValue(Constants.OID_HTTP_FLOATING_IP), true));
		props.setProperty(Constants.OID_HTTP_CONNECTOR_PORT, this.config.getValue(Constants.OID_HTTP_CONNECTOR_PORT));
		return props;
	}
	
	public ClassLoader getClassLoader() {
		try {
			if (this.loader == null) {
				synchronized (this.lock) {
					if (this.loader == null) {
						this.loader = new AseClassLoader(this.getWebContainerJars(), this.getClass().getClassLoader());
					}
				}
			}
			return this.loader;
		} catch (Exception e) {
			String msg = "Error occurred while creating the class loader for the web container components: " + e.getMessage();
			_logger.error(msg, e);
			throw new RuntimeException(msg);
		}
	}

	private URL[] getWebContainerJars() throws Exception {
		String jarDirs = this.config.getValue(Constants.PROP_HTTP_CONTAINER_JAR_DIRS);
		if (_logger.isDebugEnabled()) {

		_logger.debug("Directories to be searched for web container JAR files: " + jarDirs);
		}

		Collection jars = FileUtils.toUrls(FileUtils.findFiles(jarDirs, ",", Pattern.compile(".*.jar"), false));
		
		if (_logger.isDebugEnabled()) {
			StringBuffer buffer = new StringBuffer("Found the following web container JAR files:\n");
		
			Iterator iterator = jars.iterator();

			while (iterator.hasNext()) {
				buffer.append(iterator.next() + AseStrings.NEWLINE);
			}
			_logger.debug(buffer.toString());
		}

		return (URL[])jars.toArray(new URL[jars.size()]);
	}

	public void start() throws StartupFailedException {
		if (this.webContainer != null) {
			this.webContainer.start();
		}
	}

	public void stop() throws ShutdownFailedException {
		if (this.webContainer != null) {
			this.webContainer.stop();
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.baypackets.ase.container.WebContainer#softStop()
	 * This method is invoked when softshutdown notification is received
	 * from EMS
	 */
	public void softStop() {
		if (this.webContainer != null) {
			this.webContainer.softStop();
		}
	}

	public void deploy(String appName, AseContext app) throws DeploymentFailedException {
		if (this.webContainer != null) {
			this.webContainer.deploy(appName, app);
		}
	}

	public void upgrade(String appName, AseContext app) throws UpgradeFailedException {
		if (this.webContainer != null) {
			this.webContainer.upgrade(appName, app);
		}
	}

	public void undeploy(String appName) throws UndeploymentFailedException {
		if (this.webContainer != null) {
			this.webContainer.undeploy(appName);
		}
	}

	public boolean isDeployed(String appName) {
		return this.webContainer != null ? this.webContainer.isDeployed(appName) : false;
	}	

}


