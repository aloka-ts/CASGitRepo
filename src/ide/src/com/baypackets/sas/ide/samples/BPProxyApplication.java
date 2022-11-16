package com.baypackets.sas.ide.samples;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import com.baypackets.sas.ide.descriptors.BPCASDeploymentDescriptor;
import com.baypackets.sas.ide.descriptors.BPSIPDeploymentDescriptor;
import com.baypackets.sas.ide.util.BPCreateProject;
import com.baypackets.sas.ide.util.BPMessages;
import com.baypackets.sas.ide.util.BPProjectType;
import com.baypackets.sas.ide.SasPlugin;

public class BPProxyApplication extends Wizard implements INewWizard {

	private WizardNewProjectCreationPage newPage =null;
	private String projectName = null;
	public BPProxyApplication() {
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
	public boolean performFinish() 
	{
		// TODO Auto-generated method stub
		setprojectName();
		BPCreateProject createProject = new BPCreateProject();
		createProject.initializeProjectParameters(projectName, BPProjectType.SIPProject);
			
		if(createProject.createProject()==false)
			return false;
		BPCreateProxyApplication proxyApp = new BPCreateProxyApplication(projectName, getShell());
		if(!(proxyApp.create()))
			return false;
		
		BPSIPDeploymentDescriptor sipDescriptor = new BPSIPDeploymentDescriptor(projectName);
		sipDescriptor.generateDescriptor(BPSampleApps.getInstance().ProxyApp);
		
		
		BPCASDeploymentDescriptor casDescriptor = new BPCASDeploymentDescriptor(projectName);
		
		casDescriptor.generateCASDescriptor();
		
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
