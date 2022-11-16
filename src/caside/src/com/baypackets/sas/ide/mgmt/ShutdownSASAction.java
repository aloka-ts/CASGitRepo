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
package com.baypackets.sas.ide.mgmt;

import java.lang.reflect.Constructor;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.baypackets.ase.jmxmanagement.ServiceManagementMBean;
import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.GetStatusSAS;
import com.baypackets.sas.ide.util.StatusASE;

public class ShutdownSASAction implements IWorkbenchWindowActionDelegate 
{
	private static Class jmxmpConnectorClass=null;
	static{
		try{
			jmxmpConnectorClass = Class.forName("javax.management.remote.jmxmp.JMXMPConnector");
			SasPlugin.getDefault().log("The Jmxmpconnector class loaded is "+jmxmpConnectorClass);
		}catch(ClassNotFoundException e){
			SasPlugin.getDefault().log("The JMXMPConnector class not found");
		}
	}
	private IWorkbenchWindow window;	
	
	
	public void dispose() {
	}

	public void init(IWorkbenchWindow window){
		this.window = window;
	}

	public void run(IAction action){
		String btn[] = null;
		StatusASE statusASE = StatusASE.getInstance();
		try{
			btn = new String[]{"OK"};
			
			String host = statusASE.getAddress();
			int port =  SasPlugin.getPORT();
			int JMXURL = SasPlugin.getJMXURL();
			
			GetStatusSAS getStatusSAS = new GetStatusSAS();
			if(getStatusSAS.getStatus(host)){
				shutdownSAS(host, port, JMXURL);
				MessageDialog msg = new MessageDialog(window.getShell(), "AGNITY CAS Graceful Shutdown",null, "Graceful Shutdown initiated for CAS running on host " + host + " was shutdown successfully.", MessageDialog.INFORMATION, btn,1);
                msg.open();
                return;
			}else{
				MessageDialog msg = new MessageDialog(window.getShell(), "AGNITY CAS Graceful Shutdown",null, "CAS is not running at host :" + host, MessageDialog.WARNING, btn,1);
				msg.open();
				return;
			}
			
		}catch(Exception e){
			SasPlugin.getDefault().log(e.getMessage(), e);
			MessageDialog msg = new MessageDialog(window.getShell(), "AGNITY CAS Graceful Shutdown",null, "CAS shutdown Failed :" +e.getMessage(), MessageDialog.ERROR, btn,1);
			msg.open();
			return;
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
	
	public void shutdownSAS(String host, int port, int JMXURL)
	{
		JMXConnector jmxc = null;
		try
		{
			JMXServiceURL url=null;
			
			if(JMXURL==1)
			{
				url =new JMXServiceURL("jmxmp",host ,port);
				//reeta added following code
				Class[] paramTypes = { JMXServiceURL.class };
	            Constructor cons = jmxmpConnectorClass.getConstructor(paramTypes);

	        	Object[] args = { url };
				Object theObject = cons.newInstance(args);
			    jmxc = (JMXConnector) theObject;
	            jmxc.connect();
	            //
			}
			else
			{
				url =new JMXServiceURL("service:jmx:rmi:///jndi/rmi://"+ host +":"+port+"/jmxsasserver");
				//reeta ddded it
				jmxc = JMXConnectorFactory.connect(url, null);
			}
			SasPlugin.getDefault().log("JMXServiceURL===== >"+url);	
			//jmxc = JMXConnectorFactory.connect(url, null); reeta commented it
			
			SasPlugin.getDefault().log("JMXConnector ========== > "+jmxc);
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			SasPlugin.getDefault().log("MBeanServerConnection========== > "+mbsc);
			String domain = mbsc.getDefaultDomain();
			
			ObjectName stdMBeanName =new ObjectName(domain +":type=com.baypackets.ase.jmxmanagement.ServiceManagement,index=1");
			MBeanServerInvocationHandler.newProxyInstance(mbsc, stdMBeanName, ServiceManagementMBean.class, false);
			try{
			mbsc.invoke(stdMBeanName ,"stopserver", null, null);
			}catch(javax.management.remote.generic.ConnectionClosedException c){
				SasPlugin.getDefault().log(c.getMessage(), c);

			}
			jmxc.close();
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
		}

	}
	
	public static IWorkspace getWorkspace()
	{
	
		return ResourcesPlugin.getWorkspace();
		
	}
	
	


}

