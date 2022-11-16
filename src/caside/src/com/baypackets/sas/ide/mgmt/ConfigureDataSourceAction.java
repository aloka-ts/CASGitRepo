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

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;

import com.baypackets.sas.ide.SasPlugin;
/**
 * This class attaches SASIDE to the SIP Application Server 
 * 
 * @author eclipse
 *
 */

public class ConfigureDataSourceAction implements IWorkbenchWindowActionDelegate
{

	private IWorkbenchWindow window;
	
	public void dispose(){
	}

	public void init(IWorkbenchWindow window){
		this.window = window;
	}

	public void run(IAction action) {
		
		SasPlugin.getDefault().log("Inside Configure data source action");
				

		   IPath pathASEHOME = JavaCore.getClasspathVariable("ASE_HOME");
		    
		    IFileStore fileStore = EFS.getLocalFileSystem().getStore(pathASEHOME.append("conf"));
			fileStore = fileStore.getChild(new Path("datasources.xml"));

			  SasPlugin.getDefault().log("Data source file path found in file store open with CAS editor "+fileStore);
			    IWorkbenchPage page=  window.getActivePage();
			    try {
			if (fileStore != null) {
				IEditorDescriptor desc = PlatformUI.getWorkbench()
						.getEditorRegistry()
						.getDefaultEditor(fileStore.getName());
				page.openEditor(new FileStoreEditorInput(fileStore),
						desc.getId());
				// page.openEditor(new FileStoreEditorInput (fileStore),
				// "editorSasXml");
			} else {
				String[] buttontxt = new String[] { "OK" };
				MessageDialog messageBox = new MessageDialog(
						this.window.getShell(),
						"Configure File",
						null,
						" The data source configuration file doesnot exist",
						MessageDialog.ERROR, buttontxt, 0);
				messageBox.open();
				return;
			}
		        
			    } catch (PartInitException e) {

			}

	}

	public void selectionChanged(IAction action, ISelection selection){
	}
}
