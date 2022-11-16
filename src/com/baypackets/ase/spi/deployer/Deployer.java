package com.baypackets.ase.spi.deployer;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import com.baypackets.ase.common.exceptions.ShutdownFailedException;
import com.baypackets.ase.common.exceptions.StartupFailedException;
import com.baypackets.ase.container.exceptions.ActivationFailedException;
import com.baypackets.ase.container.exceptions.UpgradeFailedException;
import com.baypackets.ase.container.exceptions.DeactivationFailedException;
import com.baypackets.ase.container.exceptions.DeploymentFailedException;
import com.baypackets.ase.container.exceptions.UndeploymentFailedException;
import com.baypackets.ase.container.exceptions.RedeploymentFailedException;

public interface Deployer {
	
    public static int DEFAULT_PRIORITY = 5;

    public static final String CLIENT_EMS = "EMS";
    public static final String CLIENT_TELNET = "TELNET";
    public static final String CLIENT_HOTDEPLOY = "HOTDEPLOY";
	public static final String CLIENT_IDE = "IDE";			//Added for SAS IDE
	public static final String CLIENT_SOA_FW = "SOA_FW";
	public static final String CLIENT_SBB = "SBBDEPLOY";

	/**
     * Deploys a new application into the Servlet engine.  The values for the
     * "appName", "version", and "priority" parameters are overriden by the
     * values specified in the given archive's "sas.xml" deployment descriptor
     * file if one is present.
     *
     * @param appName  The name of the application to deploy.
     * @param version  The application's version number.
     * @param priority  The application's triggering priority.
     * @param contextPath  Identifies the application in an HTTP request
     * URI (ex. http://hostname/contextPath).  If null, the "appName" argument 
     * will be used as the context path.
     * @param archive  The URL of the archive file containing the classes and
     * resources that comprise the application being deployed.  This can also
     * be the URL of a local directory.
     * @return  A unique identifier for the upgraded app. 
     * @throws DeploymentFailedException if an error occurs while deploying
     * the application.
     */
    public DeployableObject deploy(String name, String version, int priority, String contextPath, InputStream stream, String deployedBy) throws DeploymentFailedException;    
    
	/**
	* Re-deploys an application 
	*/
	//Added for SAS IDE

   public DeployableObject redeploy(String name, String version, int priority, String contextPath, InputStream stream, String deployedBy) throws RedeploymentFailedException;    
    /**
     * This overloaded version of the "deploy" method extracts the values for
     * "appName", "version", and "priority" from the "sas.xml" deployment 
     * descriptor file contained in the given archive.  If "sas.xml" is not 
     * present in the archive, a DeploymentFailedException is thrown.
     */
    public DeployableObject deploy(InputStream stream, String deployedBy) throws DeploymentFailedException;

 //   public DeployableObject redeploy(InputStream stream, String deployedBy) throws DeploymentFailedException;	//Added for SASIDE
    	
    /**
     * Upgrades the specified application.
     *
     * @param appName  The name of the application to upgrade.
     * @param version  The upgraded application's version number.
     * @param priority  The application's new triggering priority.
     * @param archive  The URL of the archive file containing the classes and
     * resources that comprise the application being upgraded.  This can also
     * be the URL of a local directory.
     * @return  A unique identifier for the upgraded app. 
     * @throws UpgradeFailedException if an error occurs while upgrading
     * the app.
     */
    public DeployableObject upgrade(String appName, String version, int priority, InputStream stream) throws UpgradeFailedException;    
    
    
    /**
     * This overloaded version of the "upgrade" method extracts the values for
     * "appName", "version", and "priority" from the "sas.xml" deployment 
     * descriptor file contained in the given archive.  If "sas.xml" is not 
     * present in the archive, an UpgradeFailedException is thrown.
     */
    public DeployableObject upgrade(InputStream stream) throws UpgradeFailedException;
    
    /**
     * Undeploys the specified application from the Serlvet engine.
     *
     * @param appName  The name of the application to undeploy.
     * @throws UndeploymentFailedException if an error occurs while
     * undeploying the app.
     */
    public DeployableObject undeploy(String id) throws UndeploymentFailedException;
    
    
    /**
     * Starts up the specified application.  Starting an application entails
     * moving it into a state where it can begin servicing new requests.
     *
     * @param appName  The name of the application to startup.
     * @throws StartupFailedException if an error occurs while starting up
     * the application.
     */
    public DeployableObject start(String id) throws StartupFailedException;
    
    /**
     * Shuts down the specified application.  Stopping an app entails moving it
     * into a state where it will no longer service new requests.
     *
     * @param appName  The name of the application to shutdown.
     * @throws ShutdownFailedException if an error occurs while shutting
     * down the application.
     */
    public DeployableObject stop(String id, boolean immediate) throws ShutdownFailedException;
    
	public DeployableObject activate(String id) throws ActivationFailedException;
    
	public DeployableObject deactivate(String id) throws DeactivationFailedException;
    
	public DeployableObject findById(String id);
    
	public DeployableObject findByNameAndVersion(String name, String version);
    
    public Iterator findByName(String name);
    
    public Iterator findAll();

    /*
     * Returns a list of the deployed objects that are applications (as opposed to resources)
     * (Not a generic function, but needed for some operations) If the type was a bit
     * map and not a number from 1-3 then this would be generic
     */
    public List getAppNames();
    
	public void registerStateChangeListener(String id, DeploymentListener listener);

	public void unregisterStateChangeListener(String id, DeploymentListener listener);
	
	public File getDeployDirectory();
	
	public short getType();
	
}
