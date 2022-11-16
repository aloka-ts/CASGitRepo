package com.baypackets.sas.ide.soa.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.soa.views.ProvisionServiceDialog;
import com.baypackets.sas.ide.util.IdeUtils;
import com.baypackets.sas.ide.util.StatusASE;
import com.baypackets.sas.ide.util.GetStatusSAS;
import com.baypackets.sas.ide.util.SASProvisionedSOAServicesUtil;

public class ProvisionSOAServiceAction implements
		IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	private String serviceName = null;
	private String serviceWSDLLocation = null;
	String serviceVersion=null;
	GetStatusSAS getStatusSAS = null;
	boolean isEmbedded = false;
	String SASAddress = null;
	SASProvisionedSOAServicesUtil services;
	public void init(IWorkbenchWindow window) {

		this.window = window;
	}

	public void run(IAction action) {

		if (getStatusSAS == null)
			getStatusSAS = new GetStatusSAS();
		StatusASE statusSAS = StatusASE.getInstance();

		services = SASProvisionedSOAServicesUtil
				.getInstance(getStatusSAS, window.getShell());
		try {

			SASAddress = statusSAS.getAddress();
			if (statusSAS.getAttach() == 0)
				isEmbedded = true;
			else
				isEmbedded = false;

			//reeta modified it to not showtable when CAS is niether embedded not attached
			if ((statusSAS.isEmbeddedRunning() || statusSAS.getAttach() != 0)
					&& getStatusSAS.getStatus(SASAddress)) {
				services.setAddress(SASAddress);
			} 
			
			if(createProvisionPage()){
				String[] buttontxt = new String[] { "OK" };
				MessageDialog messageBox = new MessageDialog(window.getShell(), "Service Provisioing", null,
						" Service Provisioned Successfully on CAS running at "
								+ SASAddress, MessageDialog.INFORMATION,
						buttontxt, 0);
			}

		}

		catch (Exception e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			return;
		}

	}

	public void selectionChanged(IAction action, ISelection currentSelection) {
	}

	public void dispose() {

	}

	//***************************************************************************
	private boolean createProvisionPage() {

		boolean value=false;
		Shell shell = window.getShell();
		ProvisionServiceDialog dialog = new ProvisionServiceDialog(shell);

		dialog.open();

		shell.setText("AGNITY CAS SOA Remote Service Provisioning ");

		if (dialog.isCancelled()) {
			value=false;
		}else if(dialog.okPressed()){
			this.serviceName = dialog.getServiceName();
			this.serviceWSDLLocation = dialog.getServiceWSDLLocation();
			this.serviceVersion=dialog.getServiceVersion();
			try{
			services.provisionService(this.serviceName,this.serviceVersion,
					this.serviceWSDLLocation);
			value=true;
			}catch(Exception e){
				SasPlugin.getDefault().log(e.getMessage(), e);
				SasPlugin.getDefault().log(
						"The exception in provisioing Service" + e.toString());
				return false;
					
			}
			
		}
		return value;
	}

}
