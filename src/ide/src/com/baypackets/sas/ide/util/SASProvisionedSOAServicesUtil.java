package com.baypackets.sas.ide.util;
import java.util.Hashtable;
import java.util.ArrayList;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.jface.dialogs.MessageDialog;
import java.net.URI;

import com.baypackets.ase.jmxmanagement.ServiceManagementMBean;
import com.baypackets.sas.ide.SasPlugin;
import java.lang.reflect.Constructor;
import org.eclipse.swt.widgets.Shell;
public class SASProvisionedSOAServicesUtil {
	private static SASProvisionedSOAServicesUtil instance = null;
	private static Hashtable SOAProvServices = null;
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
	
	private SASProvisionedSOAServicesUtil()
	{
		SasPlugin.getDefault().log("Status of the Servlet Engine");
		
	}
	
	public static SASProvisionedSOAServicesUtil getInstance(GetStatusSAS sasStatus,Shell provisioingShell)
	{
		
		if(instance==null)
		{
			instance = new SASProvisionedSOAServicesUtil();
			getSASStatus=sasStatus;
			shell=provisioingShell;
			SOAProvServices = new Hashtable();
			statusASE = StatusASE.getInstance();
		}
		return instance;
		
	}
	public void setAddress(String addr)
	{
		address= addr;
	}
	
	
	public Hashtable getProvisioinedSOAServices()
	{
		return SOAProvServices;
	}
	
	
	public void setAllProvisionedSOAServices()
	{
		SOAProvServices = getAllProvisionedServices(address);
		
	}
	
	
	private synchronized Hashtable getAllProvisionedServices(String address)
	{
		JMXConnector jmxc =null;
		try
		 {	
			//int port =14000;	
			String apiName="listProvisionedServices";
			

			int port =SasPlugin.getPORT();
			int portsas = statusASE.getPORT();

			if(portsas!=0)
				port = portsas;
		 	JMXServiceURL url=null;	
			MBeanServerConnection mbsc=null;
			String domain =null;
			ObjectName stdMBeanName =null;
			
			Hashtable services =null;
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
			mbsc = jmxc.getMBeanServerConnection();
			domain = mbsc.getDefaultDomain();
			

		    stdMBeanName =new ObjectName(domain +":type=com.baypackets.ase.jmxmanagement.SOAServiceProvisioning,index=1");
			
					
				
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
	
	public void provisionService(String ServiceName,String version,String serviceLocation) {
		JMXConnector jmxc = null;
		try {
			if (getSASStatus.getStatus(address)) {
				
				
				int port =SasPlugin.getPORT();
				int portsas = statusASE.getPORT();

				if(portsas!=0)
					port = portsas;
				String signs[] = new String[] { "java.lang.String","java.lang.String","java.net.URI"};

				URI wsdlLocUri=new URI(serviceLocation.trim());
				Object params[] = { ServiceName,version,wsdlLocUri};

				JMXServiceURL url = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				ServiceManagementMBean proxy = null;
				String status = "";
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
									+ ":type=com.baypackets.ase.jmxmanagement.SOAServiceProvisioning,index=1");
					SasPlugin.getDefault().log("Invoking provisionService on Server!!!!!!!");
					proxy = (ServiceManagementMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									ServiceManagementMBean.class, false);
					status = mbsc.invoke(stdMBeanName, "provisionService",
							params, signs).toString();
					if (status.equals("false")) {
						String[] buttontxt = new String[] { "OK" };
						MessageDialog messageBox = new MessageDialog(shell, "Provisioing Service",
								null, ServiceName
										+ " Service Provisioing Failed on SAS running at "
										+ address,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}else if (status.equals("true")) {
						String[] buttontxt = new String[] { "OK" };
						MessageDialog messageBox = new MessageDialog(shell, "Provisioing Service",
								null, ServiceName
										+ " Service Provisioned Successfully on SAS running at "
										+ address,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}

				} catch (Exception exe) {
					String[] buttontxt = new String[] { "OK" };
					MessageDialog messageBox = new MessageDialog(shell, "Provisioing Service", null,
							ServiceName + " Service Provisioing Failed on SAS running at "
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
	
	
	public void updateService(String ServiceName,String version,String serviceLocation) {
		JMXConnector jmxc = null;
		try {
			if (getSASStatus.getStatus(address)) {
				
				
				int port =SasPlugin.getPORT();
				int portsas = statusASE.getPORT();

				if(portsas!=0)
					port = portsas;
				String signs[] = new String[] { "java.lang.String","java.lang.String","java.net.URI"};

				URI wsdlLocUri=new URI(serviceLocation.trim());
				Object params[] = { ServiceName,version,wsdlLocUri};

				JMXServiceURL url = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				ServiceManagementMBean proxy = null;
				String status = "";
				try {

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
									+ ":type=com.baypackets.ase.jmxmanagement.SOAServiceProvisioning,index=1");

					proxy = (ServiceManagementMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									ServiceManagementMBean.class, false);
					status = mbsc.invoke(stdMBeanName, "updateService",
							params, signs).toString();
					if (status.equals("false")) {
						String[] buttontxt = new String[] { "OK" };
						MessageDialog messageBox = new MessageDialog(shell, "Updating Service",
								null, ServiceName
										+ " Updating Service Failed on SAS running at "
										+ address,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}

				} catch (Exception exe) {
					String[] buttontxt = new String[] { "OK" };
					MessageDialog messageBox = new MessageDialog(shell, "Updating Service", null,
							ServiceName + "Updating Service Failed on SAS running at "
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
	
	
	public void removeService(String ServiceName) {
		JMXConnector jmxc = null;
		try {
			if (getSASStatus.getStatus(address)) {
				
				
				int port =SasPlugin.getPORT();
				int portsas = statusASE.getPORT();

				if(portsas!=0)
					port = portsas;
				String signs[] = new String[] { "java.lang.String" };

				Object params[] = { ServiceName };

				JMXServiceURL url = null;
				MBeanServerConnection mbsc = null;
				String domain = null;
				ObjectName stdMBeanName = null;
				ServiceManagementMBean proxy = null;
				String status = "";
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
									+ ":type=com.baypackets.ase.jmxmanagement.SOAServiceProvisioning,index=1");

					proxy = (ServiceManagementMBean) MBeanServerInvocationHandler
							.newProxyInstance(mbsc, stdMBeanName,
									ServiceManagementMBean.class, false);
					status = mbsc.invoke(stdMBeanName, "removeService",
							params, signs).toString();
					if (status.equals("false")) {
						String[] buttontxt = new String[] { "OK" };
						MessageDialog messageBox = new MessageDialog(shell, "Removing Service",
								null, ServiceName
										+ " Removing Service Failed on SAS running at "
										+ address,
								MessageDialog.INFORMATION, buttontxt, 0);
						messageBox.open();
					}

				} catch (Exception exe) {
					String[] buttontxt = new String[] { "OK" };
					MessageDialog messageBox = new MessageDialog(shell, "Removing Service", null,
							ServiceName + "Removing Service Failed on SAS running at "
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
