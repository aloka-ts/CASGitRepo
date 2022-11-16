package com.baypackets.ase.container;

import com.baypackets.ase.container.exceptions.*;
import com.baypackets.ase.common.exceptions.*;
import java.util.Properties;

public interface WebContainer {

	public void initialize(Properties props) throws InitializationFailedException;

	public void start() throws StartupFailedException;

	public void stop() throws ShutdownFailedException;
	
	public void softStop();

	public WebContainerState getState();

	public void deploy(String appName, AseContext app) throws DeploymentFailedException;

	public void upgrade(String appName, AseContext app) throws UpgradeFailedException;

	public void undeploy(String appName) throws UndeploymentFailedException;

	public boolean isDeployed(String appName);

	public ClassLoader getClassLoader();

}


