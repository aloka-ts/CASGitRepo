package com.baypackets.sas.ide.samples;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

import com.baypackets.sas.ide.descriptors.BPCASDeploymentDescriptor;
import com.baypackets.sas.ide.descriptors.BPSASDeploymentDescriptor;
import com.baypackets.sas.ide.descriptors.BPSIPDeploymentDescriptor;
import com.baypackets.sas.ide.util.BPCreateProject;
import com.baypackets.sas.ide.util.BPMessages;
import com.baypackets.sas.ide.util.BPProjectType;
import com.baypackets.sas.ide.SasPlugin;

public class BPB2buaApplication extends BasicNewProjectResourceWizard{

	private WizardNewProjectCreationPage newPage =null;
	private String projectName = null;
	
	public BPB2buaApplication() {
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
		createProject.initializeProjectParameters(projectName, BPProjectType.SIPProject);
			
		if(createProject.createProject()==false)
			return false;
		BPCreateB2bUAApplication b2buaApp = new BPCreateB2bUAApplication(projectName, getShell());
		if(!(b2buaApp.create()))
			return false;
		
		BPSIPDeploymentDescriptor sipDescriptor = new BPSIPDeploymentDescriptor(projectName);
		sipDescriptor.generateDescriptor(BPSampleApps.getInstance().B2bUA);
		
		
		BPCASDeploymentDescriptor sasDescriptor = new BPCASDeploymentDescriptor(projectName);
		
		sasDescriptor.generateCASDescriptor();
		
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
