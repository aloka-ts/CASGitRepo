package com.baypackets.ase.spi.deployer;

import java.net.URL;

import javax.servlet.sip.SipApplicationSession;

import com.baypackets.ase.common.exceptions.ShutdownFailedException;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.container.exceptions.ActivationFailedException;
import com.baypackets.ase.container.exceptions.DeactivationFailedException;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.container.exceptions.UndeploymentFailedException;

public interface DeployableObject {
	public static final short STATE_UNINSTALLED = 1;
	public static final short STATE_INSTALLED = 2;
	public static final short STATE_STOPPING =3;
	public static final short STATE_READY = 4;
	public static final short STATE_ACTIVE = 5;
	public static final short STATE_ERROR = 6;
	public static final short STATE_UPGRADE = 7;		
	
	public static final short TYPE_SAS_APPLICATION = 1;
	public static final short TYPE_SYSAPP = 2;
	public static final short TYPE_RESOURCE = 3;
	public static final short TYPE_SERVLET_APP = 4;
	public static final short TYPE_PURE_SOA = 5;
	public static final short TYPE_SOA_SERVLET = 6; // contains both Servlet and SOA
	public static final short TYPE_SOA_REMOTE_SERVICE = 7;
	public static final short TYPE_SIMPLE_SOA_APP = 8;
	public static final short TYPE_SOAP_SERVER = 9;
	public static final short TYPE_SBB = 10;
	
	public String getId();
	
	public short getType();
	
	public String getObjectName();
	
	public String getDeploymentName();
	
	public String getVersion();

	public int getPriority();
	
	public short getState();
	
	public String getDeployedBy();
	
	public String getDisplayInfo();
	
	public String getStatusString();
	
	public String getFullVersion();
	
	public ClassLoader getClassLoader();
	
	public URL getArchive();
	
	public String getContextPath();
	
	public long getDeployTime();
	
	public void setDeployedBy(String client);
		
	public void deploy() throws DeploymentFailedException;
	
	public void start() throws StartupFailedException;
	
	public void activate() throws ActivationFailedException;
	
	public void deactivate()  throws DeactivationFailedException;
	
	public void stop(boolean immediate) throws ShutdownFailedException;
	
	public void undeploy() throws UndeploymentFailedException;
	
	/* Set the flag for the old application whcih is upgraded*/

	/* Say there are two applications with versions V1 and V2
		and V2 is upgraded version
		then upgraded state for V1 will be true and for V2 will be false */
	public void setUpgradeState(boolean flag);
	
	public boolean getUpgradeState();

	// This method is added for developing RA application router
	public SipApplicationSession createApplicationSession(String protocol, String sessionId);
	
}
