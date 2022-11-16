/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.baypackets.sas.ide.logger.util;

import java.lang.reflect.Constructor;

import javax.management.MBeanOperationInfo;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.baypackets.ase.jmxmanagement.LogWatcherMBean;
import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.GetStatusSAS;
import com.baypackets.sas.ide.util.StatusASE;
import com.baypackets.sas.ide.logger.preferences.PrefsPage;

public class LoggingUtil {

	private static Class jmxmpConnectorClass = null;

	static {
		try {
			jmxmpConnectorClass = Class
					.forName("javax.management.remote.jmxmp.JMXMPConnector");
		} catch (ClassNotFoundException e) {
			SasPlugin.getDefault().log("The JMXMPConnector class not found");
		}
	}

	private static boolean stopServer = false;
	private static GetStatusSAS getSASStatus = null;

	private static int port = 14000;

	private static int JMXURL = 1;

	private static String SASAddress = null;

	private static JMXServiceURL url = null;
	private static MBeanServerConnection mbsc = null;
	private static String domain = null;
	private static ObjectName stdMBeanName = null;
	private static LogWatcherMBean proxy = null;

	private static final String SIP_DEBUG_LOG = "sipDebug.log";
	private static final String CAS_LOG = "CAS.log";

	public LoggingUtil() {
		getSASStatus = new GetStatusSAS();
		StatusASE statusSAS = StatusASE.getInstance();
		SASAddress = statusSAS.getAddress();
		JMXURL = SasPlugin.getJMXURL();
		port = SasPlugin.getPORT();
	}

	public void updateLoggingSettings() {

		String logLevel = SasPlugin.getDefault().getPreferenceStore()
				.getString(PrefsPage.LOG_LEVEL);

		if (logLevel != null && !logLevel.equals("")) {
			String levelInUp = logLevel.toUpperCase();
			setLogLevel(levelInUp);
			SasPlugin.getDefault().log("Setting Log Level to:" + levelInUp);
		}
		if (SasPlugin.getDefault().getPreferenceStore().getBoolean(
				PrefsPage.DEBUG_LOG)) {
			SasPlugin.getDefault().log("Enabling DebugLogs...............");
			setSipLogging("1");
		} else if (!SasPlugin.getDefault().getPreferenceStore().getBoolean(
				PrefsPage.DEBUG_LOG)) {
			setSipLogging("0");
			SasPlugin.getDefault().log("Disabling DebugLogs.... ");

		}

	}

	public void setLogLevel(String level) {
		JMXConnector jmxc = null;
		try {
			String signs[] = new String[] { "java.lang.String" };

			Object params[] = { level };

			if (getSASStatus.getStatus(SASAddress)) {
				JMXServiceURL url = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				LogWatcherMBean proxy = null;
				try {
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", SASAddress, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + SASAddress
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					// reeta modified connection as per connector
					mbsc = jmxc.getMBeanServerConnection();
					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.LogWatcher,index=1");

					proxy = (LogWatcherMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									LogWatcherMBean.class, false);
					mbsc.invoke(stdMBeanName, "changeLogLevel", params, signs);

				} catch (Exception exe) {
					SasPlugin.getDefault().log(exe.getMessage(), exe);

				} finally {
					if (jmxc != null)
						jmxc.close();
				}
			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
	}

	private void setSipLogging(String enable) {
		JMXConnector jmxc = null;
		try {
			String signs[] = new String[] { "java.lang.String" };

			Object params[] = { enable };

			if (getSASStatus.getStatus(SASAddress)) {
				JMXServiceURL url = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				LogWatcherMBean proxy = null;
				try {
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", SASAddress, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + SASAddress
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					// reeta modified connection as per connector
					mbsc = jmxc.getMBeanServerConnection();
					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.LogWatcher,index=1");
					SasPlugin.getDefault().log(
							"Change Sip Logging*******************.... "
									+ enable);
					proxy = (LogWatcherMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									LogWatcherMBean.class, false);
					mbsc
							.invoke(stdMBeanName, "changeSipLogging", params,
									signs);

				} catch (Exception exe) {
					SasPlugin.getDefault().log(exe.getMessage(), exe);

				} finally {
					if (jmxc != null)
						jmxc.close();
				}
			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
	}

	public String getLogsUsingJMX(String readerNum, String fileName) {
		JMXConnector jmxc = null;
		String logLine = null;
		try {
			if (readerNum.equals("")) {
				return null;
			}
			String signs[] = new String[] { "java.lang.String" };

			Object params[] = { readerNum };

			if (getSASStatus.getStatus(SASAddress)) {
				stopServer = false;
				String apiName = null;

				if (fileName.equals(CAS_LOG)) {
					apiName = "readContainerLogs";
				} else if (fileName.equals(SIP_DEBUG_LOG)) {
					apiName = "readContainerSipLogs";
				}
				try {
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", SASAddress, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + SASAddress
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					// reeta modified connection as per connector
					mbsc = jmxc.getMBeanServerConnection();
					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.LogWatcher,index=1");

					proxy = (LogWatcherMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									LogWatcherMBean.class, false);

					logLine = (String) mbsc.invoke(stdMBeanName, apiName,
							params, signs);
//					SasPlugin.getDefault().log("Long line returned from JMXXXXXXXXXX is..."+logLine);

				} catch (Exception exe) {
					SasPlugin.getDefault().log(exe.getMessage(), exe);

				} finally {
					if (jmxc != null) {
						try {
							jmxc.close();
						} catch (Exception e) {

						}
					}
				}
			} else {
				logLine = null;
				stopServer = true;
			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
		return logLine;
	}

	public long getLogFileSizeUsingJMX(String fileName) {
		JMXConnector jmxc = null;
		Long fileSize;
		long size = 0;
		try {
			String signs[] = new String[] { "java.lang.String" };

			Object params[] = { fileName };

			if (getSASStatus.getStatus(SASAddress)) {
				try {
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", SASAddress, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + SASAddress
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					// reeta modified connection as per connector
					mbsc = jmxc.getMBeanServerConnection();
					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.LogWatcher,index=1");

					proxy = (LogWatcherMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									LogWatcherMBean.class, false);
					fileSize = (Long) mbsc.invoke(stdMBeanName,
							"getLogFileSize", params, signs);

					// SasPlugin.getDefault().log(
					// "The fileSize returned from jmx is...." + fileSize);
					size = fileSize;

				} catch (Exception exe) {
					SasPlugin.getDefault().log(exe.getMessage(), exe);

				} finally {
					if (jmxc != null)
						jmxc.close();
				}
			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage() + e.getCause());
		}
		return size;
	}

	public String openReader(String fileName, boolean isSkip,String numOfLinesSkip) {

		SasPlugin.getDefault().log(
				"Inside run() of LoggingUtil The SASAddess is" + SASAddress);
		SasPlugin.getDefault().log(
				"Inside run() of LoggingUtil The SAS Port is" + port);
		SasPlugin.getDefault().log(
				"Inside run() of LoggingUtil The JMXURL is" + JMXURL);
		SasPlugin.getDefault().log(
				"Opening Reader for file!!!!!!!!!!!!!" + fileName);
		JMXConnector jmxc = null;
		String readerNum = "";
		try {
			String signs[] = new String[] { "java.lang.String", "boolean","java.lang.String" };

			Object params[] = { fileName, isSkip, numOfLinesSkip };

			if (getSASStatus.getStatus(SASAddress)) {
				try {
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", SASAddress, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + SASAddress
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					// reeta modified connection as per connector
					mbsc = jmxc.getMBeanServerConnection();
					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.LogWatcher,index=1");

					proxy = (LogWatcherMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									LogWatcherMBean.class, false);

					MBeanOperationInfo info[] = mbsc.getMBeanInfo(stdMBeanName)
							.getOperations();
					for (int i = 0; i < info.length; i++) {
						SasPlugin.getDefault().log(
								"openReader():The operation supported is....."
										+ info[i].getName());
					}
					readerNum = mbsc.invoke(stdMBeanName, "openReader", params,
							signs).toString();

					SasPlugin.getDefault().log(
							"Reader has been opened!!!!!!!!!!!!!" + fileName
									+ "Index returned is.." + readerNum);

				} catch (Exception exe) {
					SasPlugin.getDefault().log(exe.getMessage(), exe);

				} finally {
					if (jmxc != null)
						jmxc.close();
				}
			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			e.printStackTrace();
		}
		return readerNum;
	}

	public void closeReader(String fileName, String readerNum) {
		SasPlugin.getDefault().log(
				"Closing Reader for file!!!!!!!!!!!!!" + fileName
						+ " For Reader index " + readerNum);
		JMXConnector jmxc = null;
		Long fileSize;
		long size = 0;
		try {
			String signs[] = new String[] { "java.lang.String",
					"java.lang.String" };

			Object params[] = { fileName, readerNum };

			if (getSASStatus.getStatus(SASAddress)) {

				try {
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", SASAddress, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + SASAddress
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					// reeta modified connection as per connector
					mbsc = jmxc.getMBeanServerConnection();
					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.LogWatcher,index=1");

					proxy = (LogWatcherMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									LogWatcherMBean.class, false);
					mbsc.invoke(stdMBeanName, "closeReader", params, signs);

				} catch (Exception exe) {
					SasPlugin.getDefault().log(exe.getMessage(), exe);

				} finally {
					if (jmxc != null)
						jmxc.close();
				}
			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}
	}

	public boolean isLogFileExist(String fileName) {
		// SasPlugin.getDefault().log(
		// "Check if File Exists!!!!!!!!!!!!!" + fileName);
		JMXConnector jmxc = null;
		boolean isexist = false;

		try {
			String signs[] = new String[] { "java.lang.String" };

			Object params[] = { fileName };

			if (getSASStatus.getStatus(SASAddress)) {
				try {
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", SASAddress, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + SASAddress
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					// reeta modified connection as per connector
					mbsc = jmxc.getMBeanServerConnection();
					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.LogWatcher,index=1");

					proxy = (LogWatcherMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									LogWatcherMBean.class, false);

					String exists = mbsc.invoke(stdMBeanName, "isFileExist",
							params, signs).toString();

					if (exists.equals("true")) {
						isexist = true;
					} else if (exists.equals("false")) {
						isexist = false;
					}

				} catch (Exception exe) {
					SasPlugin.getDefault().log(exe.getMessage(), exe);

				} finally {
					if (jmxc != null)
						jmxc.close();
				}
			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			e.printStackTrace();
		}
		return isexist;
	}

	public boolean isServerStopped() {
		if (getSASStatus.getStatus(SASAddress)) {
			return false;
		} else {
			return true;
		}
	}

}
