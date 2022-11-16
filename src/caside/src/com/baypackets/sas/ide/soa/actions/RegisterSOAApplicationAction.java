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
package com.baypackets.sas.ide.soa.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.soa.views.RegisterServiceDialog;
import com.baypackets.sas.ide.util.SASDeployementSOAServicesUtil;
import com.baypackets.sas.ide.util.SASRegisteredSOAApplicationsUtil;
import com.baypackets.sas.ide.util.StatusASE;
import com.baypackets.sas.ide.util.GetStatusSAS;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;

public class RegisterSOAApplicationAction implements
		IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	String serviceName = null;
	String applicationName = null;
	GetStatusSAS getSASStatus = null;
	boolean isEmbedded = false;
	java.util.List<String> servicesList = null;
	String SASAddress = null;
	String appURL = null;
	
	SASRegisteredSOAApplicationsUtil util=null;

	public void init(IWorkbenchWindow window) {

		this.window = window;
	}

	public void run(IAction action) {
		SASDeployementSOAServicesUtil services = SASDeployementSOAServicesUtil
				.getInstance();
		StatusASE statusSAS = StatusASE.getInstance();
		if (getSASStatus == null)
			getSASStatus = new GetStatusSAS();

		try {

			SASAddress = statusSAS.getAddress();

			if (statusSAS.getAttach() == 0)
				isEmbedded = true;
			else
				isEmbedded = false;

			//reeta modified it to not showtable when CAS is niether embedded not attached
			if ((statusSAS.isEmbeddedRunning() || statusSAS.getAttach() != 0)
					&& getSASStatus.getStatus(SASAddress)) {
				services.setAddress(SASAddress);
				services.setAllDeployedSOAServices();
			}
		}

		catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			return;
		}

		try {

			Hashtable ASEServices = services.getDeployedSOAServices();
			Set serv = ASEServices.keySet();
			Iterator itr = serv.iterator();
			servicesList = new ArrayList<String>();
			while (itr.hasNext()) {
				String servicename = (String) itr.next();
				servicesList.add(servicename);
			}
		} catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			SasPlugin.getDefault().log(
					"The exception in show table is " + e.toString());

		}

		if ((statusSAS.isEmbeddedRunning() || statusSAS.getAttach() != 0)
				&& getSASStatus.getStatus(SASAddress)) {
			util = SASRegisteredSOAApplicationsUtil
					.getInstance(getSASStatus, window.getShell());
			util.setAddress(SASAddress);
		} 
		
		if(createRegisterPage()){
			String[] buttontxt = new String[] { "OK" };
			MessageDialog messageBox = new MessageDialog(window.getShell(), "Application Registeration", null,
					" Application Registered Successfully on CAS running at "
							+ SASAddress, MessageDialog.INFORMATION,
					buttontxt, 0);
		}

	}

	public void selectionChanged(IAction action, ISelection currentSelection) {
	}

	public void dispose() {

	}

	//***************************************************************************
	private boolean createRegisterPage() {
		
		Shell shell=this.window.getShell();
        RegisterServiceDialog dialog = new RegisterServiceDialog(shell);
        dialog.setServiceNameList(servicesList);
        dialog.open();
        shell.setText("AGNITY CAS SOA Application Registeration");

		if (dialog.isCancelled()) {
			return false;
		} else {
			this.serviceName = dialog.getServiceName();
			this.appURL = dialog.getApplicationURL();
			this.applicationName=dialog.getApplicationName();
			try{
			util.registerApplication(applicationName, serviceName, appURL);
			return true;
			}catch(Exception e){
			SasPlugin.getDefault().log(e.getMessage(), e);
			SasPlugin.getDefault().log(
					"The exception in  reistering App" + e.toString());
			return false;
				
			}
			
		}
	}

}
