package com.baypackets.ase.jmxmanagement;

import java.util.HashMap;
import java.util.Hashtable;

public interface SOAServiceManagementMBean {
	public boolean deploy(String serviceName, String Version, String prority, String contextPath, HashMap m);
	public boolean redeploy(String serviceName, String Version, String prority, String contextPath, HashMap m);

	public boolean deploy(String serviceName, String Version, String prority, String contextPath);
	public boolean redeploy(String serviceName, String Version, String prority, String contextPath);

	public boolean undeploy(String serviceName);

	public boolean start(String serviceName);

	public boolean stop(String serviceName);

	public boolean activate(String serviceName);

	public boolean deactivate(String serviceName);

	/** This method gives all the services deployed on the SAS
	*/
	public Hashtable AllDeployedSOAServices();

	/** This method gives all the services deployed on the SAS by IDE
	*/

	public Hashtable AllDeployedSOAServicesIDE();

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
