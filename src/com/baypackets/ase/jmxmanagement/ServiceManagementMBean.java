package com.baypackets.ase.jmxmanagement;
import java.util.HashMap;
import java.util.Hashtable;

public interface ServiceManagementMBean
{
	//Methods for Service Management
	
	/**	This method deploys the service
	*/
	public boolean deploy(String serviceName, String Version, String prority, String contextPath, HashMap m);
	public boolean redeploy(String serviceName, String Version, String prority, String contextPath, HashMap m);

	public boolean deploy(String serviceName, String Version, String prority, String contextPath);
	public boolean redeploy(String serviceName, String Version, String prority, String contextPath);

	public boolean undeploy(String serviceName, String version);

	public boolean start(String serviceName, String version);

	public boolean stop(String serviceName, String version);

	public boolean activate(String serviceName, String version);

	public boolean deactivate(String serviceName, String version);

	/** This method gives all the services deployed on the SAS
	 */
	public Hashtable AllServices();

	// Methods for Resource Management

	public boolean deployResource(HashMap m);

	public boolean deployResource(String contextPath);

	public boolean undeployResource(String resourceName, String version);

	public boolean startResource(String resourceName, String version);

	public boolean stopResource(String resourceName, String version);

	public boolean activateResource(String resourceName, String version);

	public boolean deactivateResource(String resourceName, String version);

	// Methods for LIVE SBB UPGRADE (BUG 6765)

	public String statusSBB();

	public boolean upgradeSBB (String contextPath);

	public boolean upgradeSBB (HashMap m);
	
	// Method to trigger Activity Test
	
	public boolean triggerActivityTest();


	//Methods for SAS Management

	public void  stopserver();

	public int status();

	public boolean startservice(String id);
	public boolean stopservice(String ide);
	public boolean undeployservice(String ide);
	public boolean deployservice(String ide);
	public boolean activateservice(String ide);
	public boolean deactivateservice(String ide);
}
