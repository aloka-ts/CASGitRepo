package com.baypackets.sas.ide.mgmt;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.baypackets.sas.ide.SasPlugin;
public class AbortSAS implements IWorkbenchWindowActionDelegate{

	private IWorkbenchWindow window = null;

	public void dispose() 
	{
	}

	public void init(IWorkbenchWindow window) 
	{
		this.window = window;
	}

	public void run(IAction action){
		try{
            String[] txb = new String[]{"OK"};

			boolean running = SASInstance.getInstance().isRunning();
			if(!running){
                MessageDialog ms = new MessageDialog(window.getShell(), "Abort AGNITY Embedded CAS", null, "Embedded CAS is not running.", MessageDialog.WARNING, txb,0);
                ms.open();
                return;
            }	

			SASInstance.getInstance().stop();

            running = SASInstance.getInstance().isRunning();
        	if(running){
				String[] tx = new String[]{"OK"};
                MessageDialog ms = new MessageDialog(window.getShell(), "Abort AGNITY Embedded CAS", null, "Embedded CAS Aborting Failure", MessageDialog.ERROR, tx,0);
				ms.open();
			}else{
				String[] tx = new String[]{"OK"};
				MessageDialog ms = new MessageDialog(window.getShell(), "Abort AGNITY Embedded CAS", null, "Embedded CAS Aborted Successfully", MessageDialog.INFORMATION, tx,0);
                ms.open();
			}
		}catch(Exception e){
			SasPlugin.getDefault().log(e.getMessage(), e);
			String[] tx = new String[]{"OK"};
			MessageDialog ms = new MessageDialog(window.getShell(), "Abort AGNITY Embedded CAS", null, e.getMessage(), MessageDialog.ERROR, tx,0);
			ms.open();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) 
	{
	}

}
