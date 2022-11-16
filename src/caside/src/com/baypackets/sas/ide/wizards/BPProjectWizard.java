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
package com.baypackets.sas.ide.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import com.baypackets.sas.ide.SasPlugin;

public class BPProjectWizard extends BasicNewResourceWizard
{

	private WizardNewProjectCreationPage firstPage;
	private BPProjectTypePage applicationTypePage;
	private BPJavaConfigPage javaConfigPage;
	
	public void addPages() 
	{
		//super.addPages();		// // by Neeraj
		
		//Create the first page and add it.
        	firstPage = new WizardNewProjectCreationPage("AGNITY CAS Project Wizard");// BPNewProjectCreationCVS("AGNITY CAS Project Wizard");
        	firstPage.setTitle("Create AGNITY CAS Project");
        	firstPage.setDescription("Creates a new AGNITY CAS project with the specified name");
        	this.addPage(firstPage);
        
        	//Create the project type page and add it
        	applicationTypePage = new BPProjectTypePage ("Select Project Type");
        	applicationTypePage.setTitle("Specify Application Type");
        	applicationTypePage.setDescription("Initializes the application for the selected application type");
        	this.addPage(applicationTypePage);
        	//Create the Java Configuration page and add it.
        	this.javaConfigPage = new BPJavaConfigPage(ResourcesPlugin.getWorkspace().getRoot(), this.firstPage);
        	this.addPage(javaConfigPage);
        
	}

	public void init(IWorkbench workbench, IStructuredSelection currentSelection) 
	{
		super.init(workbench, currentSelection);
	}

	public boolean performFinish() 
	{
		SasPlugin.getDefault().log("PERFORM FINISH CALLED.....");
		SasPlugin.getDefault().log("Path :" + this.firstPage.getLocationPath());
		SasPlugin.getDefault().log("Path :" + this.firstPage.getProjectHandle().getFullPath());
		
		
		IRunnableWithProgress runnable = new BPProjectCreation(this);
		IRunnableWithProgress op= new WorkspaceModifyDelegatingOperation(runnable);

		try 
		{
			getContainer().run(false, true, op);
		} 
		catch (InvocationTargetException e) 
		{
			SasPlugin.getDefault().log(e.getMessage(), e);
			MessageDialog.openError(getShell(), "Project Creation Failed", e.getMessage());
			return false;
		} 
		catch  (InterruptedException e) 
		{
			return false;
		}
		
		if(this.firstPage.getProjectHandle() == null)
			return false;
		
        	this.selectAndReveal(firstPage.getProjectHandle());
        	return true;
	}
		
	public WizardNewProjectCreationPage getFirstPage() 
	{
		return firstPage;
	}

	public BPJavaConfigPage getJavaConfigPage() 
	{
		return javaConfigPage;
	}

	public BPProjectTypePage getApplicationTypePage() 
	{
		return applicationTypePage;
	}
}
