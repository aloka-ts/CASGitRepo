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

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.IdeUtils;
import com.baypackets.sas.ide.util.StatusASE;
/**
 * This class attaches SASIDE to the SIP Application Server 
 * 
 * @author eclipse
 *
 */

public class AttachSASAction implements IWorkbenchWindowActionDelegate  //, IJavaLaunchConfigurationConstants
{

	private IWorkbenchWindow window;
	
	public void dispose(){
	}

	public void init(IWorkbenchWindow window){
		this.window = window;
	}

	public void run(IAction action) {
		
		StatusASE statusASE = StatusASE.getInstance();
		
		//Check whether IDE is already running a CAS instance or attached to a CAS instance or starting a CAS instance 
		boolean running = SASInstance.getInstance().isRunning();
		boolean connected = SASInstance.getInstance().isConnected();
		if(running || connected){
			
			String strMessage = "The IDE is already running or attached to a CAS instance. So cannot attach it again...";
			
			if(connected){
				strMessage = "CAS cannot be attached as IDE is already attached to CAS running at "+statusASE.getAddress();
			}else if (running){
				strMessage = "There is already an embedded CAS instance running. So cannot attach the IDE to another CAS instance";
			}
				
			String[] tx = new String[]{"OK"};
            MessageDialog ms = new MessageDialog(window.getShell(), "AGNITY CAS Startup", null, strMessage, MessageDialog.ERROR, tx,0);
            ms.open();
            return;
		}
		
		AttachDialog dialog = new AttachDialog(window.getShell());
		dialog.open();
		
		if(dialog.isCancelled())
			return;
		
		String host = dialog.getHost();
		int debugPort = dialog.getDebugPort();
		int jmxPort = dialog.getJmxPort();
		
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType type = manager.getLaunchConfigurationType(IJavaLaunchConfigurationConstants.ID_REMOTE_JAVA_APPLICATION);
		ILaunchConfigurationWorkingCopy workingCopy;
		
		try{
			ILaunchConfiguration[] configurations = manager.getLaunchConfigurations(type);
			for (int i = 0; i < configurations.length; i++) {
				ILaunchConfiguration configuration = configurations[i];
				if (configuration.getName().equals("SAS")) {
					configuration.delete();
					break;
				}
			}
			
			workingCopy = type.newInstance(null,"SAS");
			
			ISelection selection = window.getSelectionService().getSelection();
			IProject[] projects = IdeUtils.getProject(selection);//edited by reeta
			IProject project=null;
			String projectName="";
			
			if(projects!=null){
			for(int i=0;i<projects.length;i++){
				project = projects[i];
			projectName = project != null ? project.getName() : "";
				
			}
			}
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName); //need to resolve later
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_CONNECTOR,IJavaLaunchConfigurationConstants.ID_SOCKET_ATTACH_VM_CONNECTOR);
			
			HashMap attrMap = new HashMap();
			attrMap.put("hostname", host);
			attrMap.put("port", ""+debugPort);
			workingCopy.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CONNECT_MAP, attrMap);
			
			
			ILaunchConfiguration configuration = workingCopy.doSave();
			ILaunch launch = configuration.launch(ILaunchManager.DEBUG_MODE, null, false);
			SASInstance.getInstance().setLaunch(launch);
			
			
			if(SASInstance.getInstance().isConnected()){
				statusASE.setAddress(host);
				statusASE.setEmbeddedRunning(false);
				statusASE.setPORT(jmxPort);
				statusASE.setAttach(2);
			
				//Now set it as the active perspective.
				//Set the debug perspective
				if (window != null){
					window.getWorkbench().showPerspective("org.eclipse.debug.ui.DebugPerspective", window);
				}
				//reeta added it on 7-08-2007
				String[] tx = new String[]{"OK"};
				MessageDialog ms = new MessageDialog(window.getShell(), "Attach Remote CAS", null, "Remote CAS attached Successfully", MessageDialog.INFORMATION, tx,0);
				ms.open();
				
			}
		}catch(Exception e){
			SasPlugin.getDefault().log(e.getMessage(), e);
			String[] tx = new String[]{"OK"};
			MessageDialog ms = new MessageDialog(window.getShell(), "AGNITY CAS Startup", null, "Not able to start AGNITY CAS", MessageDialog.ERROR, tx,0);
			ms.open();
		}
	}

	public void selectionChanged(IAction action, ISelection selection){
	}
}
