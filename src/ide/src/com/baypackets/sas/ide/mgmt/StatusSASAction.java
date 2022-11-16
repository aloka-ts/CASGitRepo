package com.baypackets.sas.ide.mgmt;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.baypackets.sas.ide.util.GetStatusSAS;
import com.baypackets.sas.ide.util.StatusASE;

public class StatusSASAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	private StatusASE statusASE = null;
	public void dispose() 
	{

	}

	public void init(IWorkbenchWindow window) 
	{
		this.window = window;
	}

	public void run(IAction action){
		try{
			String message = "";

			statusASE = StatusASE.getInstance();
			GetStatusSAS getStatusSAS = new GetStatusSAS();
			String btn[] = new String[]{"OK"};
			String host = statusASE.getAddress();
			
			boolean  running = getStatusSAS.getStatus(host);
			if(running){
				if (statusASE.isEmbeddedRunning())
					message = "The CAS instance is running at host " + host + " and is started from this IDE.";
				else if (statusASE.getAttach() > 0)
					message = "The CAS instance is running at host " + host + " and is attached to the IDE.";
				else
					message = "The CAS instance is running at host " + host +" and is neither embedded nor attached with the IDE.";
			}else{
				message = "The CAS instance is currently not running at host :" + host;
			}
			MessageDialog msg = new MessageDialog(window.getShell(), "AGNITY CAS Status",null, message, MessageDialog.INFORMATION, btn,0);
			msg.open();
		
		}catch(Exception e){
			String btn[] = new String[]{"OK"};
			MessageDialog msg = new MessageDialog(window.getShell(), "AGNITY CAS Status",null, "Error in getting the CAS Status :" + e.getMessage(), MessageDialog.ERROR, btn,0);
			msg.open();
		}
	}

	public void selectionChanged(IAction action, ISelection selection){
	}
}

