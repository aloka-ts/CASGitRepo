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
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import com.baypackets.sas.ide.SasPlugin;
import java.lang.reflect.Constructor;
public class SASServices 
{
	
	private static SASServices instance = null;
	private static Hashtable Services = null;
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
	
	private SASServices()
	{
		SasPlugin.getDefault().log("Status of the Servlet Engine");
		
	}
	
	public static synchronized SASServices getInstance()
	{
		
		if(instance==null)
		{
			instance = new SASServices();
			Services = new Hashtable();
            statusASE = StatusASE.getInstance();
		}
		return instance;
		
	}
	public void setAddress(String addr)
	{
		address= addr;
	}
	
	public Hashtable getServices()
	{
		return Services;
	}
	
	
	public void setAllServices()
	{
		Services = getAllServices(address);
		
	}
	
	
	public synchronized Hashtable getAllServices(String address)
	{
		JMXConnector jmxc =null;
		try
		 {	
			//int port =14000;	
			String apiName="AllServices";
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
			

		    stdMBeanName =new ObjectName(domain +":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");
			
					
				
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
}
