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
			    	
			        IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(fileStore.getName()); 
			        page.openEditor(new FileStoreEditorInput (fileStore), 
			        		"editorSasXml"); 
		        
			    } catch (PartInitException e) {

			}

	}
	
	
//	    SasPlugin.getDefault().log("Data source file path found is "+path);
//    IFile sampleFile =  ResourcesPlugin.getWorkspace().getRoot().getWorkspace().getRoot().getFileForLocation(path);
//    SasPlugin.getDefault().log("created IFile  "+sampleFile);
//    
//    IEditorInput editorInput1 = new FileEditorInput(sampleFile);
//    IWorkbenchWindow window1=PlatformUI.getWorkbench().getActiveWorkbenchWindow();
//    IWorkbenchPage page1 = window1.getActivePage();
//    IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(sampleFile.getName());
//    try {
//    	 SasPlugin.getDefault().log("Opening editor for "+sampleFile+" and Editor input "+editorInput1);
//    	 
// //       IDE.openEditor(editorInput1, "org.eclipse.ui.DefaultTextEdtior");
//        
//    	 page1.openEditor(editorInput1,desc.getId());
//    	 SasPlugin.getDefault().log("editor is opened "+desc.getId());
//    	 
//    } catch (PartInitException e1) {
//
//        e1.printStackTrace();
//    } 
//	String name= new FileDialog(aShell, SWT.OPEN).open();
//	if (name == null)
//	    return;
//	IFileStore fileStore = EFS.getLocalFileSystem().getStore(new Path(filterPath));
//	fileStore = fileStore.getChild(names[i]);
//	if (!fileStore.fetchInfo().isDirectory() && fileStore.fetchInfo().exists()) {
//	    IWorkbenchPage page=  window.getActivePage();
//	    try {
//	        IDE.openEditorOnFileStore(page, fileStore);
//	    } catch (PartInitException e) {
//	        /* some code */
//	    }
//	}
	
//	For a file in the workspace you should use IFile. If you have a selection from Project Explorer or another view that should already be an IFile or can be adapted to an IFile.
//
//	If you just have a workspace relative path use ResourcesPlugin.getWorkspace().getRoot().getFile(path) (path would include a project).
//
//	To open the default editor for the file contents use
//
//	IDE.openEditor(page, file, true);
//	to open a specific editor use
//
//	IDE.openEditor(page, file, "editor id");
//	IDE is org.eclipse.ui.ide.IDE.

	public void selectionChanged(IAction action, ISelection selection){
	}
}
