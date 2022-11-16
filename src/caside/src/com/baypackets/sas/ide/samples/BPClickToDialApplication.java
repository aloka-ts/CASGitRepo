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
package com.baypackets.sas.ide.samples;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import com.baypackets.sas.ide.descriptors.BPCASDeploymentDescriptor;
import com.baypackets.sas.ide.descriptors.BPSASDeploymentDescriptor;
import com.baypackets.sas.ide.descriptors.BPSIPDeploymentDescriptor;
import com.baypackets.sas.ide.descriptors.BPWEBDeploymentDescriptor;
import com.baypackets.sas.ide.util.BPCreateProject;
import com.baypackets.sas.ide.util.BPMessages;
import com.baypackets.sas.ide.util.BPProjectType;
import com.baypackets.sas.ide.SasPlugin;

public class BPClickToDialApplication extends BasicNewProjectResourceWizard{

	private WizardNewProjectCreationPage newPage =null;
	private String projectName = null;
	
	public BPClickToDialApplication() {
		super();
		this.setNeedsProgressMonitor(true);
		this.setWindowTitle(BPMessages.BPProjectWizardTitle);
		
		// TODO Auto-generated constructor stub
	}
	
	public void addPages()
	{
		newPage = new WizardNewProjectCreationPage("");
		try
		{
			newPage.setTitle(BPMessages.BPProjectWizardTitle);
			newPage.setDescription(BPMessages.BPProjectWizardDescription);
			this.addPage(newPage);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	public boolean performFinish() {
		// TODO Auto-generated method stub
		setprojectName();
		BPCreateProject createProject = new BPCreateProject();
		createProject.initializeProjectParameters(projectName, BPProjectType.ConvergedProject);
			
		if(createProject.createProject()==false)
			return false;
		BPCreateClickToDialApp clickToDialApp = new BPCreateClickToDialApp(projectName, getShell());
		if(!(clickToDialApp.create()))
			return false;
		
		BPSIPDeploymentDescriptor sipDescriptor = new BPSIPDeploymentDescriptor(projectName);
		sipDescriptor.generateDescriptor(BPSampleApps.getInstance().ClickToDial);
		
		
		BPCASDeploymentDescriptor casDescriptor = new BPCASDeploymentDescriptor(projectName);
		
		casDescriptor.generateCASDescriptor();
		
		BPWEBDeploymentDescriptor generatewebDescriptor = new BPWEBDeploymentDescriptor(projectName);
		
		generatewebDescriptor.generateDescriptor(BPSampleApps.getInstance().ClickToDial);
			
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}
	
	private void setprojectName()
	{
		this.projectName = newPage.getProjectName();
		SasPlugin.getDefault().log("Project Name === >"+projectName);
		
	}
	

}
