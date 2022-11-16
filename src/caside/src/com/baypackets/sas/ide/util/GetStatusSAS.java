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

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import com.baypackets.ase.jmxmanagement.ServiceManagementMBean;
import com.baypackets.sas.ide.SasPlugin;

import java.util.*;
import java.lang.reflect.Constructor;

/**
 * This class returns the status of Sip Application Server.
 * It requires two arguments The IP Address as the Port on which the JMX is listening
 * @author eclipse
 *
 */

public class GetStatusSAS {
	private int port = 14000;

	private int JMXURL = 1;
	private static Class jmxmpConnectorClass=null;

	private StatusASE statusASE = null;
	
	static{
		try{
			jmxmpConnectorClass = Class.forName("javax.management.remote.jmxmp.JMXMPConnector");
			SasPlugin.getDefault().log("The Jmxmpconnector class loaded is "+jmxmpConnectorClass);
		}catch(ClassNotFoundException e){
			SasPlugin.getDefault().log("The JMXMPConnector class not found");
		}
	}

	public GetStatusSAS() {
		port = SasPlugin.getPORT();
		statusASE = StatusASE.getInstance();

		JMXURL = SasPlugin.getJMXURL();
		SasPlugin.getDefault().log("JMXURL === > " + JMXURL);
		SasPlugin.getDefault().log("PORT ===>" + port);
	}

	/**
	 * This method returns the status of the CAS running on address 'address'
	 * @param address	The machine on which CAS is .
	 * @return	It returns true if CAS is running else it returns false;
	 */
	public boolean getStatus(String address) {
		JMXConnector jmxc = null;
		try {
		//	SasPlugin.getDefault().log("getStatus ===>" + address);
			int portAttached = 0;
			if (statusASE.getPORT() == 0)
				portAttached = port;

			else
				portAttached = statusASE.getPORT();
			JMXServiceURL url = null;
			MBeanServerConnection mbsc = null;
			ServiceManagementMBean proxy = null;
			String domain = null;
			ObjectName stdMBeanName = null;
			
			// Check if the JMXMP connector is available reeta adding it
			if(JMXURL==1)
			 {
			 url =new JMXServiceURL("jmxmp",address ,portAttached);
			Class[] paramTypes = { JMXServiceURL.class };
            Constructor cons = jmxmpConnectorClass.getConstructor(paramTypes);

			Object[] args = { url };
			Object theObject = cons.newInstance(args);
			jmxc = (JMXConnector) theObject;
			jmxc.connect();
			 }else
			 {
				 url =new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+ address +":"+portAttached+"/jmxsasserver");
				 jmxc = JMXConnectorFactory.connect(url, null);
			
			 }
			
             // //reeta has added connection based on connector
			mbsc = jmxc.getMBeanServerConnection();
			domain = mbsc.getDefaultDomain();
			stdMBeanName = new ObjectName(
					domain + ":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");
			//   proxy = (ServiceManagementMBean)MBeanServerInvocationHandler.newProxyInstance(mbsc, stdMBeanName, ServiceManagementMBean.class, false);

			Integer status = null;
			int statusOfSAS = 0;

			status = (Integer) mbsc.invoke(stdMBeanName, "status", null, null);

			jmxc.close();

			if (status == null) {
				return false;
			}

			statusOfSAS = status.intValue();
			if (statusOfSAS == 1) {
				return true;

			} else {
				return false;

			}


		} catch (Exception e) {
			try {
				if (jmxc != null)
					jmxc.close();
			} catch (Exception ee) {
			}
			SasPlugin.getDefault().log(e.getMessage(), e);
//			SasPlugin
//					.getDefault()
//					.log(
//							"Exception in com.baypackets.sas.ide.util.GetSASStatus Class while getting status "
//									+ e.toString());
			return false;
		}

	}

	public boolean getStatusEmbedded(String address) {
		JMXConnector jmxc = null;
		try {
			int portEmbedded = SasPlugin.getPORT();
			JMXServiceURL url = null;
			MBeanServerConnection mbsc = null;
			ServiceManagementMBean proxy = null;
			String domain = null;
			ObjectName stdMBeanName = null;

			
			//jmxc = JMXConnectorFactory.connect(url, null); reeta commented it
			
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
             // //reeta has added connection based on connector
			mbsc = jmxc.getMBeanServerConnection();
			domain = mbsc.getDefaultDomain();
			stdMBeanName = new ObjectName(
					domain
							+ ":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");
		//	proxy = (ServiceManagementMBean) MBeanServerInvocationHandler
		//			.newProxyInstance(mbsc, stdMBeanName,
		//					ServiceManagementMBean.class, false);
			Integer status = null;
			int statusOfSAS = 0;
			status = (Integer) mbsc.invoke(stdMBeanName, "status", null, null);

			jmxc.close();

			if (status == null) {
				return false;
			}

			statusOfSAS = status.intValue();
			if (statusOfSAS == 1) {
				return true;

			} else {
				return false;

			}

		} catch (Exception e) {
			try {
				if (jmxc != null)
					jmxc.close();
			} catch (Exception ee) {
			}
			//SasPlugin.getDefault().log(e.getMessage(), e);
			SasPlugin
					.getDefault()
					.log(
							"Exception in com.baypackets.sas.ide.util.GetSASStatus Class while getting status "
									+ e.toString());
			return false;
		}
	}

}
