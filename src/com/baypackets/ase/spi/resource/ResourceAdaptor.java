package com.baypackets.ase.spi.resource;

import java.io.IOException;

import com.baypackets.ase.resource.ResourceException;
import com.baypackets.ase.spi.container.SasMessage;
import com.baypackets.ase.spi.container.SasMessageCallback;
import com.baypackets.ase.spi.deployer.DeployableObject;

public interface ResourceAdaptor extends SasMessageCallback {
	
	public static final short ROLE_ACTIVE = 1;
	public static final short ROLE_STANDBY = 2;
	
	public void init(ResourceContext context) throws ResourceException;
	
	public void start() throws ResourceException;
	
	public void stop() throws ResourceException;
	
	public void configurationChanged(String name, Object value)	throws ResourceException;
	
	public void roleChanged (String clusterId, String subsystemId, short role);
	
	public void sendMessage(SasMessage message) throws IOException;

	public void registerApp(DeployableObject ctx);

	public void unregisterApp(DeployableObject ctx);
}
