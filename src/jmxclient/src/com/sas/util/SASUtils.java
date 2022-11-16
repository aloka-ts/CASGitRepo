package com.sas.util;

/**
 * Interface that exposes the utility methods 
 * required to interact with SAS over JMX.
 */
public interface SASUtils {
	/**
	 *deploys and activates the service
	 * 
	 * @param serviceName
	 *@param serviceVersion
	 *@param servicePriority
	 *@param pathSAR
	 *@throws SASUtilException
	 *             returns true on successfull deployment and activation
	 *             otherwise false
	 */
	public boolean deployAndActivateService(String serviceName,
			String serviceVersion, String servicePriority, String pathSAR)
			throws SASUtilException;
	
	public boolean deployService(String serviceName,
			String serviceVersion, String servicePriority, String pathSAR)
			throws SASUtilException;

	/**
	 * undeploys and deactivates the service
	 * 
	 * @param serviceName
	 *@throws SASUtilException
	 *             returns true on successfull undeployment and deactivation
	 *             otherwise false
	 */
	public boolean UndeployAndDeActivateService(String serviceName,
			String serviceVersion) throws SASUtilException;

	/**
	 * returns the running status of the SAS
	 * 
	 *@param host
	 *@param port
	 *@throws SASUtilException
	 *             returns true if running false otherwise
	 */
	public boolean statusSAS(String host, int port) throws SASUtilException;

	/**
	 * This method upgrades the SBB deployed on SAS
	 * 
	 * @param pathSAR
	 * @throws SASUtilException
	 * returns true on successful upgrade
	 * otherwise false
	 */
	public boolean upgradeSBB(String pathJAR) throws SASUtilException;

	/**
	 * This method returns the information about when the SBB was last upgraded
	 * @throws SASUtilException
	 * returns timestamp of last SBB upgrade 
	 */
	public String statusSBB() throws SASUtilException;
	
	/**
	 * This method deploys and activates a resource on SAS
	 * 
	 * @param serviceName
	 * @param serviceVersion
	 * @param servicePriority
	 * @param pathSAR
	 * @throws SASUtilException
	 * returns true on successful deployment and activation
	 * otherwise false
	 */
	public boolean deployAndActivateResource(String resourceName,
			String resourceVersion, String pathJAR)
			throws SASUtilException;	
	
	/**
	 * This method undeploys and deactivates a resource on SAS
	 * 
	 * @param serviceName
	 * @throws SASUtilException
	 * returns true on successful deactivation and undeploy
	 * otherwise false
	 */
	public boolean UndeployAndDeActivateResource(String resourceName,
			String resourceVersion) throws SASUtilException;

	/**
	 * This method triggers the Activity Test on Tcap-Provider deployed on SAS
	 * 
	 * @throws SASUtilException
	 * returns true on successful execution of Activity Test
	 * otherwise false
	 */
	public void triggerActivityTest() throws SASUtilException;
}
