/*
 * Created on May 6, 2004
 *
 */
package com.baypackets.ase.spi.container;

import com.baypackets.ase.spi.deployer.DeployableObject;

/**
 */
public interface SasApplication extends DeployableObject{
	
	public boolean isDistributable();
	
	//public void registerStateChangeListener(AseAppStateListener listener);

	//public void unregisterStateChangeListener(AseAppStateListener listener);
	
	public String getDefaultHandlerName();
	
	public String getDefaultHandlerName(String name);
}
