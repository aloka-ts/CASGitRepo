/**
 * This class implements the SASUtil interface.
 */
package com.sas.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Properties;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import org.apache.log4j.Logger;
import com.baypackets.ase.jmxmanagement.SarFileByteArray;

/**
 * 
 */
public class SASUtilsImpl implements SASUtils {

	private static JMXConnector jmxc = null;
	private static MBeanServerConnection mbsc = null;
	private static ObjectName stdMBeanName = null;
	private static Logger logger = Logger.getLogger(SASUtilsImpl.class);
	private int MAXSIZE = 10000000;
	private static Class jmxmpConnectorClass = null;
	private static Properties props = new Properties();
	static {
		// load class
		try {
			jmxmpConnectorClass = Class
					.forName("javax.management.remote.jmxmp.JMXMPConnector");

			System.out.println("The Jmxmpconnector class loaded is "
					+ jmxmpConnectorClass);

		} catch (ClassNotFoundException e) {
			System.err.println("The JMXMPConnector class not found");

		}

		// loads the application.properties file.This file contains the value of
		// port and host on which SAS is running.
		System.out.println("loading application.properties");
		try {
			props
					.load(new FileInputStream(
							"resources\\application.properties"));
		} catch (FileNotFoundException e) {
			System.out.println("The application.properties file not found");			
		} catch (IOException e) {
			System.out.println("Unable to open application.properties file");			
		}

	}
	
	public static void reloadPropertiesFromPath(String loadFile){
		try {
			props
					.load(new FileInputStream(loadFile));
		} catch (FileNotFoundException e) {
			System.out.println("The application.properties file not found");			
		} catch (IOException e) {
			System.out.println("Unable to open application.properties file");			
		}
	}

	/**
	 * deploys and activates the service
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
			throws SASUtilException {

		final String METHOD_NAME = "deployAndActivateService";
		System.out.println("deployAndActivateService ========== > " + serviceName);

		boolean serviceStatus = false;
		try {
			try {
				// acquire bean and connect to SAS
				System.out.println(METHOD_NAME + "calling connectAndAcquireBean");
				connectAndAcquireBean();
			} catch (MalformedObjectNameException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (SecurityException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (NoSuchMethodException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (InstantiationException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IllegalAccessException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (InvocationTargetException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			}
		// deploy service
		boolean deployStatus = deployService(serviceName, serviceVersion,
					servicePriority, pathSAR);
			// if deploy successful then start service
			if (deployStatus) {
				// start service
				boolean startStatus = startService(serviceName, serviceVersion);
				// if start successful then activate service
				if (startStatus) {
					// activate service
					if (activateService(serviceName, serviceVersion)) {
						System.out.println(METHOD_NAME
								+ " The service has been successfully activated.");
						// set status as true
						serviceStatus = true;
					}

				}

			}
		} finally {
			// close JMX connection
			if (jmxc != null) {
				try {
					jmxc.close();
				} catch (IOException iox) {
					System.err.println("Unable to close JMX connection"
							+ iox.getMessage());
				}
			}

		}
		return serviceStatus;
	}

	/**
	 * deploys service
	 * 
	 * @param serviceName
	 * @param serviceVersion
	 * @param servicePriority
	 * @param pathSAR
	 * returns true on successfull deployment otherwise false
	 */
	public boolean deployService(String serviceName, String serviceVersion,
			String servicePriority, String pathSAR) {

		System.out.println("deployService ========== > " + serviceName);
		final String METHOD_NAME = "deployService";
		boolean deploySuccess = false;
		try {

			try {
				InputStream stream = new FileInputStream(pathSAR);
				byte[] bytes = new byte[MAXSIZE];
				stream.read(bytes);
				SarFileByteArray byteArray = new SarFileByteArray();
				byteArray.setByteArray(bytes);
				HashMap hash = new HashMap();
				hash.put("sar", byteArray);
				String signs[] = new String[] { "java.lang.String",
						"java.lang.String", "java.lang.String",
						"java.lang.String", "java.util.HashMap" };
				// paramateres to be set
				Object params[] = { serviceName, serviceVersion,
						servicePriority, pathSAR, hash };
				System.out.println("Service version is ===== >" + serviceVersion
						+ " Service Name " + serviceName + " Priority "
						+ servicePriority + " Path Sar " + pathSAR
						+ "HashTable is.." + hash);
				// invoke meothod on bean
				String status = mbsc.invoke(stdMBeanName, "redeploy", params,
						signs).toString();
				System.out.println(METHOD_NAME + "Deploymet status is ===== >"
						+ status);

				if (status.equals("true")) {
					System.out.println(METHOD_NAME + "deployment successfull");
					deploySuccess = true;

				} else {
					System.err.println(METHOD_NAME + "deployment failed");
				}

			} catch (IOException iox) {
				System.err.println("IOException Occuured : SAS is not running"
						+ iox.getMessage());

			} catch (Exception exe) {
				System.err.println(METHOD_NAME + "Exception Occuured in Inner try"
						+ exe.getMessage());

			}
		} catch (Exception e) {
			System.err.println(METHOD_NAME + "Exception Occuured " + e.getMessage());
		}
		return deploySuccess;
	}

	/**
	 * starts service
	 * 
	 * @param serviceName
	 * returns true if start successfull otherwise false
	 */
	private boolean startService(String serviceName, String version) {
		final String METHOD_NAME = "start";
		System.out.println("startService ========== > " + serviceName);

		boolean startStatus = false;
		try {
			String signs[] = new String[] { "java.lang.String", "java.lang.String" };

			Object params[] = { serviceName, version };

			String status = "";
			try {
				// invoke method on bean
				status = mbsc.invoke(stdMBeanName, "start", params,
						signs).toString();
				if (status.equals("false")) {
					System.err.println(METHOD_NAME + "Service starting failed");

				} else if (status.equals("true")) {
					System.out.println(METHOD_NAME + "Application started successfully");
					startStatus = true;

				}

			} catch (IOException iox) {
				System.err.println(METHOD_NAME
						+ "IOException Occuured : SAS is not running"
						+ iox.getMessage());

			} catch (Exception exe) {
				System.err.println(METHOD_NAME + "Service starting failed"
						+ exe.getMessage());

			}
		} catch (Exception e) {

			System.err.println(METHOD_NAME + "Exception Occurred" + e.getMessage());
		}
		return startStatus;
	}

	/**
	 * activates Service
	 * 
	 * @param serviceName
	 *            returns true if activation successfull otherwise false
	 */
	private boolean activateService(String serviceName, String version) {
		final String METHOD_NAME = "activateService";
		System.out.println("activateService ========== > " + serviceName);
		boolean activateStatus = false;
		try {

			String signs[] = new String[] { "java.lang.String", "java.lang.String"  };

			Object params[] = { serviceName, version };

			String status = "";
			try {
				// invoke method on bean
				status = mbsc.invoke(stdMBeanName, "activate", params,
						signs).toString();
				if (status.equals("false")) {
					System.err.println(METHOD_NAME + "activation failed");

				} else if (status.equals("true")) {
					activateStatus = true;
					System.out.println(METHOD_NAME + "activation successfull");

				}

			} catch (IOException iox) {

				System.err.println(METHOD_NAME
						+ "IOException Occuured : SAS is not running" + iox);

			} catch (Exception exe) {

				System.err.println(METHOD_NAME + "Exception occured" + exe);
			}
		} catch (Exception e) {
			System.err.println(METHOD_NAME + "Exception occured" + e);
		}
		return activateStatus;
	}

	/**
	 * undeploys and deactivates the service
	 * 
	 * @param serviceName
	 *@throws SASUtilException
	 *             returns true on successfull undeployment and deactivation
	 *             otherwise false
	 */
	public boolean UndeployAndDeActivateService(String serviceName,
			String serviceVersion) throws SASUtilException {
		final String METHOD_NAME = "UndeployAndDeActivateService";
		logger
				.debug("UndeployAndDeActivateService ========== > "
						+ serviceName);

		boolean operationStatus = false;
		try {
			try {
				// acquire bean and connect to SAS
				connectAndAcquireBean();
			} catch (MalformedObjectNameException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (SecurityException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (NoSuchMethodException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (InstantiationException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IllegalAccessException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (InvocationTargetException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			}

			// deactivate
			if (deactivateService(serviceName, serviceVersion)) {
				// if deactivation successful then stop service
				if (stopService(serviceName, serviceVersion)) {
					// if service stoppped then undeploy service
					if (undeployService(serviceName, serviceVersion)) {
						logger
								.debug(METHOD_NAME
										+ " THE service "
										+ serviceName
										+ " has been deactivated and undeployed successfully");
						operationStatus = true;
					}

				}

			}
		} finally {
			// close the jmx connection
			if (jmxc != null) {
				try {
					jmxc.close();
				} catch (IOException iox) {
					System.err.println(METHOD_NAME
							+ " Unable to close JMX connection"
							+ iox.getMessage());
				}
			}

		}

		return operationStatus;

	}

	/**
	 * deactivates the service
	 * 
	 * @param serviceName
	 *@throws SASUtilException
	 *             returns true on successfull deactivation otherwise false
	 */
	private boolean deactivateService(String serviceName, String version) {

		final String METHOD_NAME = "deactivateService";
		System.out.println("deactivateService ========== > " + serviceName);
		boolean deactivationStatus = false;
		try {

			String signs[] = new String[] { "java.lang.String", "java.lang.String" };
			// creating params
			Object params[] = { serviceName, version };
			String status = "";
			try {
				status = mbsc.invoke(stdMBeanName, "deactivate", params,
						signs).toString();
				if (status.equals("false")) {
					System.out.println(METHOD_NAME + " The service " + serviceName
							+ " deactivation Failed");
				} else if (status.equals("true")) {
					System.out.println(METHOD_NAME + " deactivation successfull");
					deactivationStatus = true;

				}

			} catch (IOException iox) {
				System.err.println(METHOD_NAME
						+ "IOException Occuured : SAS is not running" + iox.getMessage());
			}

			catch (Exception exe) {
				System.err.println(METHOD_NAME + "Exception occured " + exe.getMessage());

			}
		} catch (Exception e) {
			System.err.println(METHOD_NAME + "Exception occured" + e.getMessage());
		}
		return deactivationStatus;
	}

	/**
	 * undeploys the service
	 * 
	 * @param serviceName
	 * returns true on successful undeployment otherwise false
	 */
	private boolean undeployService(String serviceName, String version) {
		final String METHOD_NAME = "undeployService";
		System.out.println("undeployService ========== > " + serviceName);
		boolean undeploystatus = false;
		try {
			String signs[] = new String[] { "java.lang.String" , "java.lang.String"};
			Object params[] = { serviceName, version };
			String status = "";
			try {
				// invoke the method on bean
				status = mbsc.invoke(stdMBeanName, "undeploy", params,
						signs).toString();
				if (status.equals("false")) {
					System.out.println(METHOD_NAME + "Undeployment failed ");

				} else if (status.equals("true")) {
					System.out.println(METHOD_NAME + "Undeployment Success ");
					undeploystatus = true;
				}

			} catch (IOException iox) {
				System.err.println(METHOD_NAME
						+ "IOException Occuured : SAS is not running" + iox.getMessage());
			}

			catch (Exception exe) {
				System.err.println(METHOD_NAME + "undeployment failed due to " + exe.getMessage());
			}
		} catch (Exception e) {
			System.err.println(METHOD_NAME + "undeployment failed Exception occured "
					+ e.getMessage());
		}
		return undeploystatus;
	}

	/**
	 * stops the service
	 * 
	 * @param serviceName
	 *            returns true if service stopped otherwise false
	 */
	private boolean stopService(String serviceName, String version) {
		final String METHOD_NAME = "stopService";
		System.out.println("stopService ========== > " + serviceName);
		boolean stopstatus = false;
		try {

			String signs[] = new String[] { "java.lang.String" , "java.lang.String"};

			Object params[] = { serviceName, version };
			String status = "";
			try {
				// invoke the method on bean
				status = mbsc
						.invoke(stdMBeanName, "stop", params, signs)
						.toString();
				if (status.equals("false")) {
					System.out.println(METHOD_NAME + "stop service failed");

				} else if (status.equals("true")) {
					System.out.println(METHOD_NAME + "stop service success");
					stopstatus = true;

				}

			} catch (IOException iox) {
				System.err.println(METHOD_NAME
						+ "IOException Occuured : SAS is not running" + iox.getMessage());

			} catch (Exception exe) {
				System.err.println(METHOD_NAME
						+ "Exception occured stop unsuccessfull" + exe.getMessage());

			}
		} catch (Exception e) {
			System.err.println(METHOD_NAME + "Exception occured stop unsuccessful"
					+ e.getMessage());

		}
		return stopstatus;
	}

	/**
	 * returns the running status of the SAS
	 * 
	 *@param host
	 *@param port
	 *@throws SASUtilException
	 *             returns true if running false otherwise
	 */
	public boolean statusSAS(String host, int port) throws SASUtilException {
		final String METHOD_NAME = "statusSAS";
		System.out.println("StatusSAS ========== > host:" + host + "port:" + port);
		try {
			try {
				// connect and acquire bean
				connectAndAcquireBean();
			} catch (MalformedObjectNameException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (SecurityException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (NoSuchMethodException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (InstantiationException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IllegalAccessException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (InvocationTargetException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			}
			Integer statusInt = null;
			int statusOfSAS = 0;
			// invoke method on bean
			statusInt = (Integer) mbsc.invoke(stdMBeanName, "status", null,
					null);
			if (statusInt == null) {
				return false;
			}

			statusOfSAS = statusInt.intValue();
			if (statusOfSAS == 1) {
				return true;

			} else {
				return false;

			}

		} catch (IOException iox) {
			System.err.println(METHOD_NAME
					+ "IOException Occuured : SAS is not running" + iox.getMessage());
			return false;
		} catch (Exception e) {
			System.err.println(METHOD_NAME + "Exception occured "
					+ e.getMessage());
			return false;
		} finally {

			if (jmxc != null) {
				try {
					jmxc.close();
				} catch (IOException iox) {
					System.err.println(METHOD_NAME + "Unable to close JMX connection"
							+ iox.getMessage());
				}
			}

		}
	}

	/**
	 * To upgrade SBB exiting on SAS  
	 * 
	 * @param pathJAR
	 * returns true on successful deployment otherwise false
	 */
	public boolean upgradeSBB(String pathJAR) throws SASUtilException {

		System.out.println("upgradeSBB ========== > " + pathJAR);
		final String METHOD_NAME = "upgradeSBB";
		boolean upgradeSuccess = false;
		try {
			// acquire bean and connect to SAS
			System.out.println(METHOD_NAME + "calling connectAndAcquireBean");
			connectAndAcquireBean();
		} catch (MalformedObjectNameException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (SecurityException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (NoSuchMethodException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (InstantiationException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (IllegalAccessException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (InvocationTargetException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		}

		try {
			InputStream stream = new FileInputStream(pathJAR);
			byte[] bytes = new byte[MAXSIZE];
			stream.read(bytes);
			SarFileByteArray byteArray = new SarFileByteArray();
			byteArray.setByteArray(bytes);
			HashMap hash = new HashMap();
			hash.put("jar", byteArray);
			String signs[] = new String[] { "java.util.HashMap" };
			// paramateres to be set
			Object params[] = { hash };
			// invoke meothod on bean
			String status = mbsc.invoke(stdMBeanName, METHOD_NAME, params,
					signs).toString();
			System.out.println(METHOD_NAME + " Upgrade status is ===== >"
					+ status);

			if (status.equals("true")) {
				System.out.println(METHOD_NAME + " Upgrade Successful.");
				upgradeSuccess = true;

			} else {
				System.err.println(METHOD_NAME + " Upgrade Failed.");
			}

		} catch (IOException iox) {
			System.err.println("IOException Occuured : SAS is not running"
					+ iox.getMessage());

		} catch (Exception exe) {
			System.err.println(METHOD_NAME + "An Exception Occured during SBB upgrade."
					+ exe.getMessage());

		}

		return upgradeSuccess;
	}
	
	/**
	 * Makes a JMX connection and acquires the bean
	 * 
	 * @thorws IOException, SecurityException, NoSuchMethodException,
	 *         IllegalArgumentException, InstantiationException,
	 *         IllegalAccessException, InvocationTargetException,
	 *         MalformedObjectNameException
	 * 
	 */
	private static void connectAndAcquireBean() throws IOException,
			SecurityException, NoSuchMethodException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, MalformedObjectNameException {
		// ip address of SAS
		String SASAddress;
		// port on which SAS is running
		int port;
		final String METHOD_NAME = "connectAndAcquireBean";
		port = Integer.valueOf(props.getProperty("port"));
		SASAddress = props.getProperty("host");

		JMXServiceURL url = new JMXServiceURL("jmxmp", SASAddress, port);
		Class[] paramTypes = { JMXServiceURL.class };
		Constructor cons = jmxmpConnectorClass.getConstructor(paramTypes);

		Object[] args = { url };
		Object theObject = cons.newInstance(args);
		jmxc = (JMXConnector) theObject;
		jmxc.connect();

		System.out.println(METHOD_NAME + "JMXConnector ========== > " + jmxc);

		mbsc = jmxc.getMBeanServerConnection();
		System.out.println(METHOD_NAME + "MBeanServerConnection========== > " + mbsc);
		// fetch domain
		String domain = mbsc.getDefaultDomain();

		stdMBeanName = new ObjectName(
				domain
						+ ":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");

	}

	@Override
	public String statusSBB() throws SASUtilException {
		System.out.println("========== Status of SBB ========== ");
		final String METHOD_NAME = "statusSBB";
		try {
			// acquire bean and connect to SAS
			System.out.println(METHOD_NAME + "calling connectAndAcquireBean");
			connectAndAcquireBean();
		} catch (MalformedObjectNameException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (SecurityException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (IllegalArgumentException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (NoSuchMethodException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (InstantiationException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (IllegalAccessException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		} catch (InvocationTargetException e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());
		}

		String status=null;
		try {
			//String signs[] = new String[] { "java.lang.String" , "java.util.HashMap" };
			// paramateres to be set
			//Object params[] = { pathJAR, hash };
			// invoke meothod on bean
			status = mbsc.invoke(stdMBeanName, METHOD_NAME, null,
					null).toString();
			System.out.println(METHOD_NAME + " Status of SBB is ===== >"
					+ status);
			
		} catch(Exception ex) {
			System.err.println(METHOD_NAME + "An Exception Occured during SBB status retrival."
																			+ ex.getMessage());
		}
		return status;
	}

	
	/**
	 * deploys and activates the resource
	 * 
	 * @param serviceName
	 *@param serviceVersion
	 *@param servicePriority
	 *@param pathSAR
	 *@throws SASUtilException
	 *             returns true on successfull deployment and activation
	 *             otherwise false
	 */
	public boolean deployAndActivateResource(String resourceName, String resourceVersion, String pathJAR)
			throws SASUtilException {

		final String METHOD_NAME = "deployAndActivateResource";
		System.out.println("Entering deployAndActivateService");

		boolean serviceStatus = false;
		try {
			try {
				// acquire bean and connect to SAS
				System.out.println(METHOD_NAME + "calling connectAndAcquireBean");
				connectAndAcquireBean();
			} catch (MalformedObjectNameException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (SecurityException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (NoSuchMethodException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (InstantiationException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IllegalAccessException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (InvocationTargetException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			}
		// deploy resource
		boolean deployStatus = deployResource(pathJAR);
			// if deploy successful then start service
			if (deployStatus) {
				// start service
				boolean startStatus = startResource(resourceName, resourceVersion);
				// if start successful then activate service
				if (startStatus) {
					// activate service
					if (activateResource(resourceName, resourceVersion)) {
						System.out.println(METHOD_NAME
								+ " The service has been successfully activated.");
						// set status as true
						serviceStatus = true;
					}

				}

			}
		} finally {
			// close JMX connection
			if (jmxc != null) {
				try {
					jmxc.close();
				} catch (IOException iox) {
					System.err.println("Unable to close JMX connection"
							+ iox.getMessage());
				}
			}

		}
		return serviceStatus;
	}

	/**
	 * Deploys resource
	 * 
	 * @param serviceName
	 * @param serviceVersion
	 * @param servicePriority
	 * @param pathSAR
	 * returns true on successfull deployment otherwise false
	 */
	public boolean deployResource(String pathJAR) {

		System.out.println("Inside deployResource method");
		final String METHOD_NAME = "deployResource";
		boolean deploySuccess = false;
		try {

			try {
				InputStream stream = new FileInputStream(pathJAR);
				byte[] bytes = new byte[MAXSIZE];
				stream.read(bytes);
				SarFileByteArray byteArray = new SarFileByteArray();
				byteArray.setByteArray(bytes);
				HashMap hash = new HashMap();
				hash.put("jar", byteArray);
				String signs[] = new String[] { "java.util.HashMap" };
				// paramateres to be set
				Object params[] = { hash };
				// invoke meothod on bean
				String status = mbsc.invoke(stdMBeanName, "deployResource", params,
						signs).toString();
				System.out.println(METHOD_NAME + "Deploymet status is ===== >"
						+ status);

				if (status.equals("true")) {
					System.out.println(METHOD_NAME + "Deployment Successful");
					deploySuccess = true;

				} else {
					System.err.println(METHOD_NAME + "Deployment Failed");
				}

			} catch (IOException iox) {
				System.err.println("IOException Occuured : SAS is not running"
						+ iox.getMessage());

			} catch (Exception exe) {
				System.err.println(METHOD_NAME + "Exception Occuured in Inner try"
						+ exe.getMessage());

			}
		} catch (Exception e) {
			System.err.println(METHOD_NAME + "Exception Occuured " + e.getMessage());
		}
		return deploySuccess;
	}

	/**
	 * starts service
	 * 
	 * @param serviceName
	 * returns true if start successful otherwise false
	 */
	private boolean startResource(String resourceName, String resourceVersion) {
		final String METHOD_NAME = "startResource";
		System.out.println("startResource ========== > " + resourceName);

		boolean startStatus = false;
		try {
			String signs[] = new String[] { "java.lang.String", "java.lang.String" };

			Object params[] = { resourceName, resourceVersion };

			String status = "";
			try {
				// invoke method on bean
				status = mbsc.invoke(stdMBeanName, "startResource", params,
						signs).toString();
				if (status.equals("false")) {
					System.err.println(METHOD_NAME + "Resource starting failed");

				} else if (status.equals("true")) {
					System.out.println(METHOD_NAME + "Resource started successfully");
					startStatus = true;

				}

			} catch (IOException iox) {
				System.err.println(METHOD_NAME
						+ "IOException Occuured : SAS is not running"
						+ iox.getMessage());

			} catch (Exception exe) {
				System.err.println(METHOD_NAME + "Service starting failed"
						+ exe.getMessage());

			}
		} catch (Exception e) {

			System.err.println(METHOD_NAME + "Exception Occurred" + e.getMessage());
		}
		return startStatus;
	}

	/**
	 * activates Service
	 * 
	 * @param serviceName
	 *            returns true if activation successfull otherwise false
	 */
	private boolean activateResource(String resourceName, String resourceVersion) {
		final String METHOD_NAME = "activateResource";
		System.out.println("activateResource ========== > " + resourceName);
		boolean activateStatus = false;
		try {

			String signs[] = new String[] { "java.lang.String", "java.lang.String"  };

			Object params[] = { resourceName, resourceVersion };

			String status = "";
			try {
				// invoke method on bean
				status = mbsc.invoke(stdMBeanName, "activateResource", params,
						signs).toString();
				if (status.equals("false")) {
					System.err.println(METHOD_NAME + "Resource Activation Failed");

				} else if (status.equals("true")) {
					activateStatus = true;
					System.out.println(METHOD_NAME + "Resource successfully activated");

				}

			} catch (IOException iox) {

				System.err.println(METHOD_NAME
						+ "IOException Occuured : SAS is not running" + iox);

			} catch (Exception exe) {

				System.err.println(METHOD_NAME + "Exception occured" + exe);
			}
		} catch (Exception e) {
			System.err.println(METHOD_NAME + "Exception occured" + e);
		}
		return activateStatus;
	}

	/**
	 * undeploys and deactivates the service
	 * 
	 * @param serviceName
	 *@throws SASUtilException
	 *             returns true on successfull undeployment and deactivation
	 *             otherwise false
	 */
	public boolean UndeployAndDeActivateResource(String resourceName, 
							String resourceVersion) throws SASUtilException {
		final String METHOD_NAME = "UndeployAndDeActivateResource";
		logger
				.debug("UndeployAndDeActivateResource ========== > "
						+ resourceName);

		boolean operationStatus = false;
		try {
			try {
				// acquire bean and connect to SAS
				connectAndAcquireBean();
			} catch (MalformedObjectNameException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (SecurityException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (NoSuchMethodException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (InstantiationException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IllegalAccessException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (InvocationTargetException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			}

			// deactivate
			if (deactivateResource(resourceName, resourceVersion)) {
				// if deactivation successful then stop service
				if (stopResource(resourceName, resourceVersion)) {
					// if service stoppped then undeploy service
					if (undeployResource(resourceName, resourceVersion)) {
						logger
								.debug(METHOD_NAME
										+ " THE resource "
										+ resourceName
										+ " has been deactivated and undeployed successfully");
						operationStatus = true;
					}

				}

			}
		} finally {
			// close the jmx connection
			if (jmxc != null) {
				try {
					jmxc.close();
				} catch (IOException iox) {
					System.err.println(METHOD_NAME
							+ " Unable to close JMX connection"
							+ iox.getMessage());
				}
			}

		}

		return operationStatus;

	}

	/**
	 * deactivates the service
	 * 
	 * @param serviceName
	 *@throws SASUtilException
	 *             returns true on successfull deactivation otherwise false
	 */
	private boolean deactivateResource(String resourceName, String resourceVersion) {

		final String METHOD_NAME = "deactivateResource";
		System.out.println("deactivateResource ========== > " + resourceName);
		boolean deactivationStatus = false;
		try {

			String signs[] = new String[] { "java.lang.String", "java.lang.String" };
			// creating params
			Object params[] = { resourceName, resourceVersion };
			String status = "";
			try {
				status = mbsc.invoke(stdMBeanName, "deactivateResource", params,
						signs).toString();
				if (status.equals("false")) {
					System.out.println(METHOD_NAME + " The service " + resourceName
							+ " Resource Deactivation Failed");
				} else if (status.equals("true")) {
					System.out.println(METHOD_NAME + " Resource successfully deactivated.");
					deactivationStatus = true;

				}

			} catch (IOException iox) {
				System.err.println(METHOD_NAME
						+ "IOException Occuured : SAS is not running" + iox.getMessage());
			}

			catch (Exception exe) {
				System.err.println(METHOD_NAME + "Exception occured " + exe.getMessage());

			}
		} catch (Exception e) {
			System.err.println(METHOD_NAME + "Exception occured" + e.getMessage());
		}
		return deactivationStatus;
	}

	/**
	 * undeploys the service
	 * 
	 * @param serviceName
	 * returns true on successful undeployment otherwise false
	 */
	private boolean undeployResource(String resourceName, String resourceVersion) {
		final String METHOD_NAME = "undeployResource";
		System.out.println("undeployResource ========== > " + resourceName);
		boolean undeploystatus = false;
		try {
			String signs[] = new String[] { "java.lang.String" , "java.lang.String"};
			Object params[] = { resourceName, resourceVersion };
			String status = "";
			try {
				// invoke the method on bean
				status = mbsc.invoke(stdMBeanName, "undeployResource", params,
						signs).toString();
				if (status.equals("false")) {
					System.out.println(METHOD_NAME + "Resource could not be undeployed ");

				} else if (status.equals("true")) {
					System.out.println(METHOD_NAME + "Resource successfully undeployed ");
					undeploystatus = true;
				}

			} catch (IOException iox) {
				System.err.println(METHOD_NAME
						+ "IOException Occuured : SAS is not running" + iox.getMessage());
			}

			catch (Exception exe) {
				System.err.println(METHOD_NAME + "undeployment failed due to " + exe.getMessage());
			}
		} catch (Exception e) {
			System.err.println(METHOD_NAME + "undeployment failed Exception occured "
					+ e.getMessage());
		}
		return undeploystatus;
	}

	/**
	 * stops the service
	 * 
	 * @param serviceName
	 *            returns true if service stopped otherwise false
	 */
	private boolean stopResource(String resourceName, String resourceVersion) {
		final String METHOD_NAME = "stopResource";
		System.out.println("stopResource ========== > " + resourceName);
		boolean stopstatus = false;
		try {

			String signs[] = new String[] { "java.lang.String" , "java.lang.String"};

			Object params[] = { resourceName, resourceVersion };
			String status = "";
			try {
				// invoke the method on bean
				status = mbsc
						.invoke(stdMBeanName, "stopResource", params, signs)
						.toString();
				if (status.equals("false")) {
					System.out.println(METHOD_NAME + "Resource could not be stopped.");

				} else if (status.equals("true")) {
					System.out.println(METHOD_NAME + "Resource stopped successfully.");
					stopstatus = true;

				}

			} catch (IOException iox) {
				System.err.println(METHOD_NAME
						+ "IOException Occuured : SAS is not running" + iox.getMessage());

			} catch (Exception exe) {
				System.err.println(METHOD_NAME
						+ "Exception occured stop unsuccessful" + exe.getMessage());

			}
		} catch (Exception e) {
			System.err.println(METHOD_NAME + "Exception occured stop unsuccessful"
					+ e.getMessage());

		}
		return stopstatus;
	}

	@Override
	public void triggerActivityTest() throws SASUtilException {
		final String METHOD_NAME = "triggerActivityTest";
		System.out.println("========== triggerActivityTest ========== > ");
		
		try {
			try {
				// acquire bean and connect to SAS
				System.out.println(METHOD_NAME + "calling connectAndAcquireBean");
				connectAndAcquireBean();
			} catch (MalformedObjectNameException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (SecurityException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IllegalArgumentException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IOException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (NoSuchMethodException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (InstantiationException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (IllegalAccessException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			} catch (InvocationTargetException e) {
				System.err.println(e.getMessage());
				throw new SASUtilException(e.getMessage());
			}
			// Trigger activity test
			mbsc.invoke(stdMBeanName, METHOD_NAME, null, null);
			System.out.println("AT executed successfully");
		} catch(Exception e) {
			System.err.println(e.getMessage());
			throw new SASUtilException(e.getMessage());			
		} finally {
			// close JMX connection
			if (jmxc != null) {
				try {
					jmxc.close();
				} catch (IOException iox) {
					System.err.println("Unable to close JMX connection"
							+ iox.getMessage());
				}
			}
		}
	}
}
