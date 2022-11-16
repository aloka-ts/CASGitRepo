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
package com.baypackets.sas.ide.util;
import java.util.Hashtable;
import java.util.ArrayList;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.baypackets.ase.jmxmanagement.ServiceManagementMBean;
import com.baypackets.sas.ide.SasPlugin;
import java.lang.reflect.Constructor;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
public class SASRegisteredSOAApplicationsUtil {
	
	private static SASRegisteredSOAApplicationsUtil instance = null;
	private static Hashtable SOARegisteredApps = null;
	private static GetStatusSAS getSASStatus=null;
	private static Shell shell=null;
	private static String address = null;
	private static StatusASE statusASE = null;
	private int JMXURL = 1;
	private static Class jmxmpConnectorClass=null;
	
	static{
		try{
			jmxmpConnectorClass = Class.forName("javax.management.remote.jmxmp.JMXMPConnector");
			SasPlugin.getDefault().log("The Jmxmpconnector class loaded is "+jmxmpConnectorClass);
		}catch(ClassNotFoundException e){
			SasPlugin.getDefault().log("The JMXMPConnector class not found");
		}
	}
	
	private SASRegisteredSOAApplicationsUtil()
	{
		SasPlugin.getDefault().log("Status of the Servlet Engine");
		
	}
	
	public static synchronized SASRegisteredSOAApplicationsUtil getInstance(GetStatusSAS sasStatus,Shell provisioingShell)
	{
		
		if(instance==null)
		{
			instance = new SASRegisteredSOAApplicationsUtil();
			 getSASStatus=sasStatus;
			shell=provisioingShell;
			SOARegisteredApps=new Hashtable();
			statusASE = StatusASE.getInstance();
		}
		return instance;
		
	}
	public void setAddress(String addr)
	{
		address= addr;
	}
	
	public Hashtable getRegsiteredSOAApps()
	{
		return this.SOARegisteredApps;
	}
	
	
	public void setAllRegisteredSOAApps()
	{
		this.SOARegisteredApps = getAllRegisteredApps(address);
	}
	
	private synchronized Hashtable getAllRegisteredApps(String address)
	{
		JMXConnector jmxc =null;
		try
		 {	
			//int port =14000;	
			String apiName="AllRegisteredSOAApplications";
			

			int port =SasPlugin.getPORT();
			int portsas = statusASE.getPORT();

			if(portsas!=0)
				port = portsas;
		 	JMXServiceURL url=null;	
			MBeanServerConnection mbsc=null;
			String domain =null;
			ObjectName stdMBeanName =null;
			
			Hashtable services =null;
			/*url =new JMXServiceURL("jmxmp",address ,port); reeta commented it and added following
				
			jmxc = JMXConnectorFactory.connect(url, null);*/
			
//			 Check if the JMXMP connector is available reeta adding it
			if(JMXURL==1)
			 {
			   url =new JMXServiceURL("jmxmp",address ,port);
			   Class[] paramTypes = { JMXServiceURL.class };
               Constructor cons = jmxmpConnectorClass.getConstructor(paramTypes);

			  Object[] args = { url };
			  Object theObject = cons.newInstance(args);
			  jmxc = (JMXConnector) theObject;
              jmxc.connect();
			 }else
			 {
				 url =new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+ address +":"+port+"/jmxsasserver");
				 jmxc = JMXConnectorFactory.connect(url, null);
			
			 }
			//reeta modified connection as per connector
			
			mbsc = jmxc.getMBeanServerConnection();
			domain = mbsc.getDefaultDomain();
			

		    stdMBeanName =new ObjectName(domain +":type=com.baypackets.ase.jmxmanagement.SOAAppRegistrationManagement,index=1");
			
					
				
			services = (Hashtable)mbsc.invoke(stdMBeanName ,apiName, null, null);
				
			jmxc.close();	
			if(services==null)
				return null;
			
			
			return services;
		 }
			
		 catch(Exception e)
		 {
			if(jmxc!=null)
			try
			{
				jmxc.close();
			}
			catch(Exception ee)
			{}
			 SasPlugin.getDefault().log(e.getMessage(), e);
			 return null;
		 }

	}
	
	
	public void registerApplication(String application,String ServiceName,String applicationUrl) {
		JMXConnector jmxc = null;
		try {
			if (getSASStatus.getStatus(address)) {
				
				
				int port =SasPlugin.getPORT();
				int portsas = statusASE.getPORT();

				if(portsas!=0)
					port = portsas;
				String signs[] = new String[] { "java.lang.String" ,"java.lang.String","java.lang.String"};

				Object params[] = {  application,ServiceName,applicationUrl };

				JMXServiceURL url = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				ServiceManagementMBean proxy = null;
				String deploystatus = "";
				try {

					//jmxc = JMXConnectorFactory.connect(url, null); reeta commented it

					//					 Check if the JMXMP connector is available reeta adding it
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", address, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + address
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					//reeta modified connection as per connector
					mbsc = jmxc.getMBeanServerConnection();
					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.SOAAppRegistrationManagement,index=1");

					proxy = (ServiceManagementMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									ServiceManagementMBean.class, false);
					deploystatus = mbsc.invoke(stdMBeanName, "registerApplication",
							params, signs).toString();
					if (deploystatus.equals("false")) {
						String[] buttontxt = new String[] { "OK" };
						MessageDialog messageBox = new MessageDialog(shell, "Registering Application",
								null, ServiceName
										+ " Application Registeration Failed on SAS running at "
										+ address,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}

				} catch (Exception exe) {
					String[] buttontxt = new String[] { "OK" };
					MessageDialog messageBox = new MessageDialog(shell, "Service Starting", null,
							ServiceName + " Application Registeration Failed on SAS running at "
									+ address, MessageDialog.INFORMATION,
							buttontxt, 0);
					messageBox.open();
					SasPlugin.getDefault().log(exe.getMessage(), exe);

				} finally {
					if (jmxc != null)
						jmxc.close();
				}

			}

			else
				return;

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}
	
	public void updateApplication(String appName) {
		JMXConnector jmxc = null;
		try {
			if (getSASStatus.getStatus(address)) {
				
				
				int port =SasPlugin.getPORT();
				int portsas = statusASE.getPORT();

				if(portsas!=0)
					port = portsas;
				String signs[] = new String[] { "java.lang.String" };

				Object params[] = { appName };

				JMXServiceURL url = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				ServiceManagementMBean proxy = null;
				String deploystatus = "";
				try {

					//jmxc = JMXConnectorFactory.connect(url, null); reeta commented it

					//					 Check if the JMXMP connector is available reeta adding it
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", address, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + address
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					//reeta modified connection as per connector
					mbsc = jmxc.getMBeanServerConnection();
					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.SOAAppRegistrationManagement,index=1");

					proxy = (ServiceManagementMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									ServiceManagementMBean.class, false);
					deploystatus = mbsc.invoke(stdMBeanName, "updateApplication",
							params, signs).toString();
					if (deploystatus.equals("false")) {
						String[] buttontxt = new String[] { "OK" };
						MessageDialog messageBox = new MessageDialog(shell, "updating Application",
								null, appName
										+ " Updating Application Falied on SAS running at "
										+ address,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}

				} catch (Exception exe) {
					String[] buttontxt = new String[] { "OK" };
					MessageDialog messageBox = new MessageDialog(shell, "updating Application", null,
							appName + " Updating Application Falied on SAS running at "
									+ address, MessageDialog.INFORMATION,
							buttontxt, 0);
					messageBox.open();
					SasPlugin.getDefault().log(exe.getMessage(), exe);

				} finally {
					if (jmxc != null)
						jmxc.close();
				}

			}

			else
				return;

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}
	
	
	public void removeApplication(String appName) {
		JMXConnector jmxc = null;
		try {
			if (getSASStatus.getStatus(address)) {
				
				
				int port =SasPlugin.getPORT();
				int portsas = statusASE.getPORT();

				if(portsas!=0)
					port = portsas;
				String signs[] = new String[] { "java.lang.String" };

				Object params[] = { appName };

				JMXServiceURL url = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				ServiceManagementMBean proxy = null;
				String deploystatus = "";
				try {

					//jmxc = JMXConnectorFactory.connect(url, null); reeta commented it

					//					 Check if the JMXMP connector is available reeta adding it
					if (JMXURL == 1) {
						url = new JMXServiceURL("jmxmp", address, port);
						Class[] paramTypes = { JMXServiceURL.class };
						Constructor cons = jmxmpConnectorClass
								.getConstructor(paramTypes);

						Object[] args = { url };
						Object theObject = cons.newInstance(args);
						jmxc = (JMXConnector) theObject;
						jmxc.connect();
					} else {
						url = new JMXServiceURL(
								"service:jmx:rmi:///jndi/rmi://" + address
										+ ":" + port + "/jmxsasserver");
						jmxc = JMXConnectorFactory.connect(url, null);

					}
					//reeta modified connection as per connector
					mbsc = jmxc.getMBeanServerConnection();
					domain = mbsc.getDefaultDomain();

					stdMBeanName = new ObjectName(
							domain
									+ ":type=com.baypackets.ase.jmxmanagement.SOAAppRegistrationManagement,index=1");

					proxy = (ServiceManagementMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									ServiceManagementMBean.class, false);
					deploystatus = mbsc.invoke(stdMBeanName, "removeApplication",
							params, signs).toString();
					if (deploystatus.equals("false")) {
						String[] buttontxt = new String[] { "OK" };
						MessageDialog messageBox = new MessageDialog(shell, "Removing Application",
								null, appName
										+ " Application Removal Failed on SAS running at "
										+ address,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}

				} catch (Exception exe) {
					String[] buttontxt = new String[] { "OK" };
					MessageDialog messageBox = new MessageDialog(shell, "Removing Application", null,
							appName + " Application Removal Failed on SAS running at "
									+ address, MessageDialog.INFORMATION,
							buttontxt, 0);
					messageBox.open();
					SasPlugin.getDefault().log(exe.getMessage(), exe);

				} finally {
					if (jmxc != null)
						jmxc.close();
				}

			}

			else
				return;

		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
		}

	}
	

}
