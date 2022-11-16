package com.baypackets.sas.ide.servicemanagement;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import com.baypackets.sas.ide.SasPlugin;

public class CompileServiceAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;
	protected IVMInstall jre;
	private IPath workspaceLocation = null;	
	private IProject contextProject = null;
	
	private String contextPackage = null;
	private String ServiceName = null;	
	
	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub
		this.window = window;

		
	}

	public void run(IAction action) {
		// TODO Auto-generated method stub
		
		ISelection currentSelection = SasPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		
		 if(currentSelection instanceof IStructuredSelection)
	        {
	            IStructuredSelection structuredSelection = (IStructuredSelection)currentSelection;
	            Object selectedElement = structuredSelection.getFirstElement();
	            if(selectedElement instanceof IProject)
	            {
	                IProject prj = (IProject)selectedElement;
	                contextProject = prj;
	            }
	            if(selectedElement instanceof IJavaProject)
	            {
	                IProject prj = ((IJavaProject)selectedElement).getProject();
	                contextProject = prj;
	            }
	            if(selectedElement instanceof IPackageFragment)
	                try
	                {
	                    IPackageFragment pf = (IPackageFragment)selectedElement;
	                    contextProject = pf.getUnderlyingResource().getProject();
	                    contextPackage = pf.getElementName();
	                }
	                catch(JavaModelException _ex) { }
	            if(selectedElement instanceof ICompilationUnit)
	                try
	                {
	                    ICompilationUnit cu = (ICompilationUnit)selectedElement;
	                    contextProject = cu.getUnderlyingResource().getProject();
	                    contextPackage = cu.getPackageDeclarations()[0].getElementName();
	                }
	                catch(JavaModelException _ex) { }
	            if(selectedElement instanceof IFile)
	            {
	                IFile file = (IFile)selectedElement;
	                contextProject = file.getProject();
	            }
	            if(selectedElement instanceof IFolder)
	            {
	                IFolder folder = (IFolder)selectedElement;
	                contextProject = folder.getProject();
	            }
	        }
		
		 
		 if(contextProject ==null)
		 {
			 String st[] = new String[]{"OK"};
			 
			 
			 IProject SelectedProjects[] = getWorkspace().getRoot().getProjects();
			 
			 if(SelectedProjects.length>0)
				 contextProject = SelectedProjects[0];
			 else
			 {
			 
				 MessageDialog dia = new MessageDialog(window.getShell(), "Service Deployment", null, "No Project Selected, so No deployment", MessageDialog.WARNING, st, 0);
			 
				 dia.open();
				 return;
			 }
			 
		 }
		 
		 ServiceName = contextProject.toString().substring(2);
		
		 
		 
		 SasPlugin.getDefault().log("THE PROJECT SELCTED IS =====> "+contextProject);
		 
		 if(compileProject()==1)
		 {
			 String b[] = new String[]{"OK"};
			 MessageDialog dg = new MessageDialog(window.getShell(),"Service Compilation", null, "Compilation Successfull", MessageDialog.INFORMATION,b,0);
			 dg.open();
			 return;
			
			 
		 }
		 else
		 {
			 if(compileProject()==2)
			 {
				 String b[] = new String[]{"OK"};
				 MessageDialog dg = new MessageDialog(window.getShell(),"Service Compilation", null, "Service Compilation Cancelled", MessageDialog.INFORMATION,b,0);
				 dg.open();
				 return;
			 }
			 else
			 {
				 String b[] = new String[]{"OK"};
				 MessageDialog dg = new MessageDialog(window.getShell(),"Service Compilation", null, "Error in Compilation", MessageDialog.ERROR,b,0);
				 dg.open();
				 return;
				 
			 }
		 }
			 
		

	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}
	
	public static IWorkspace getWorkspace()
	{
		
		return ResourcesPlugin.getWorkspace();
	}
	
	public IProject getSelectedProject()
	{
		return contextProject;
		
	}
	
	public int compileProject()
	{
		try
		{
		
			String b[] = new String[]{"OK"};
			 MessageDialog dg = new MessageDialog(window.getShell(),"Service Compilation", null, "Compiling "+contextProject.getName()+" Project", MessageDialog.ERROR,b,0);
			 dg.open();
			 if(dg.getReturnCode()==0)
			 {
			
				 IProgressMonitor monitor = new NullProgressMonitor();
				 
				 contextProject.build(IncrementalProjectBuilder.FULL_BUILD, monitor);
				 
				 SasPlugin.getDefault().log("BUILDING COMPLETE");
				 return 1;
			 }
			 else
				 return 2;
		}
		catch(Exception e)
		{
			SasPlugin.getDefault().log("Exception thrown is ...."+e);
			return 0;
		}
		
	}
		
}
