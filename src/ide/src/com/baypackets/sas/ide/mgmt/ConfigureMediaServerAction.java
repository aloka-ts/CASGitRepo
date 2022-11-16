package com.baypackets.sas.ide.mgmt;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
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

public class ConfigureMediaServerAction implements IWorkbenchWindowActionDelegate
{

	private IWorkbenchWindow window;
	
	public void dispose(){
	}

	public void init(IWorkbenchWindow window){
		this.window = window;
	}

	public void run(IAction action) {
		
		SasPlugin.getDefault().log("Inside Configure Media Server action");
				

		   IPath pathASEHOME = JavaCore.getClasspathVariable("ASE_HOME");
		    
		    IFileStore fileStore = EFS.getLocalFileSystem().getStore(pathASEHOME.append("conf"));
			fileStore = fileStore.getChild(new Path("media-server-config.xml"));

			  SasPlugin.getDefault().log("Media Server config file path found in file store "+fileStore);
			    IWorkbenchPage page=  window.getActivePage();
			    try {
			    	
			        IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(fileStore.getName()); 
			        page.openEditor(new FileStoreEditorInput (fileStore), 
			        		desc.getId()); 
		        
			    } catch (PartInitException e) {

			}

	}
	

	public void selectionChanged(IAction action, ISelection selection){
	}
}
