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
public class SASDeployementSOAServicesUtil 
{
	
	private static SASDeployementSOAServicesUtil instance = null;
	private static Hashtable SOADeployedServices=null;
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
	
	private SASDeployementSOAServicesUtil()
	{
		SasPlugin.getDefault().log("Status of the Servlet Engine");
		
	}
	
	public static synchronized SASDeployementSOAServicesUtil getInstance()
	{
		
		if(instance==null)
		{
			instance = new SASDeployementSOAServicesUtil();
			SOADeployedServices=new Hashtable();
			statusASE = StatusASE.getInstance();
		}
		return instance;
		
	}
	public void setAddress(String addr)
	{
		address= addr;
	}
	
	public Hashtable getDeployedSOAServices()
	{
		return this.SOADeployedServices;
	}
	
	
	public void setAllDeployedSOAServices()
	{
		this.SOADeployedServices = getAllDeployedServices(address);
		
	}
	
	private synchronized Hashtable getAllDeployedServices(String address)
	{
		JMXConnector jmxc =null;
		try
		 {	
			//int port =14000;	
			String apiName="AllDeployedSOAServices";
			

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
			//reeta modified connection as per connector
			
			mbsc = jmxc.getMBeanServerConnection();
			domain = mbsc.getDefaultDomain();
			

		    stdMBeanName =new ObjectName(domain +":type=com.baypackets.ase.jmxmanagement.SOAServiceManagement,index=1");
			
					
				
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
