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

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.*;

public class DetachSASAction implements IWorkbenchWindowActionDelegate 
{
	
	private IWorkbenchWindow window;
	
	
	public void dispose(){
	}

	public void init(IWorkbenchWindow window) 
	{
		this.window = window;
	}

	public void run(IAction action) {
		try{
            String[] txb = new String[]{"OK"};
            
            boolean connected = SASInstance.getInstance().isConnected();
            if(!connected){
                MessageDialog ms = new MessageDialog(window.getShell(), "AGNITY CAS Detatch", null, "IDE is not currently attached with a CAS instance. So not doing anything.", MessageDialog.WARNING, txb,0);
                ms.open();
                return;
            }	

            SASInstance.getInstance().disconnect();

            connected = SASInstance.getInstance().isConnected();
            
        	if(connected){
				String[] tx = new String[]{"OK"};
                MessageDialog ms = new MessageDialog(window.getShell(), "AGNITY CAS Detatch", null, "Detatching the IDE from CAS failed", MessageDialog.ERROR, tx,0);
				ms.open();
			}else{
				String[] tx = new String[]{"OK"};
				//reeta added it
				StatusASE statusASE = StatusASE.getInstance();
				statusASE.setAddress("127.0.0.1");
				statusASE.setPORT(0);
				statusASE.setAttach(0); //
				MessageDialog ms = new MessageDialog(window.getShell(), "AGNITY CAS Detatch", null, "The IDE is detatched Successfully", MessageDialog.INFORMATION, tx,0);
                ms.open();
			}
		}catch(Exception e){
			SasPlugin.getDefault().log(e.getMessage(), e);
			String[] tx = new String[]{"OK"};
			MessageDialog ms = new MessageDialog(window.getShell(), "AGNITY CAS Detatch", null, "Error detatching the IDE :" +e.getMessage(), MessageDialog.ERROR, tx,0);
			ms.open();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {

	}

}

