package com.genband.m5.maps.ide.wizard;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

import com.genband.m5.maps.ide.CPFPlugin;

public class CPFProjectWizard extends BasicNewResourceWizard {

	private CPFProjectPage firstPage;

	public void addPages() {
		firstPage = new CPFProjectPage(this);
		this.addPage(firstPage);
	}

	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		super.init(workbench, currentSelection);
	}

	public boolean performFinish() {
		CPFPlugin.getDefault().log("PERFORM FINISH CALLED.....");
		CPFPlugin.getDefault().log(
				"Path :" + this.firstPage.getProjectHandle().getFullPath());
		CPFPlugin.getDefault().log(
				"Navigation Type is:" + this.firstPage.getNavigationType());
		CPFPlugin.getDefault().log(
				"The default Locale is:"
						+ this.firstPage.getDefaultLocale());
		CPFPlugin.getDefault().log(
				"The selected Locales are:"
						+ this.firstPage.getSelectedLanguages().size());

		IRunnableWithProgress runnable = new CPFProjectCreation(this);
		IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(
				runnable);

		try {
			getContainer().run(false, true, op);
		} catch (InvocationTargetException e) {
			CPFPlugin.getDefault().log(e.getMessage(), e,-1);
			MessageDialog.openError(getShell(), "Project Creation Failed", e
					.getMessage());
			return false;
		} catch (InterruptedException e) {
			return false;
		}

		if (this.firstPage.getProjectHandle() == null)
			return false;

		this.selectAndReveal(firstPage.getProjectHandle());
		return true;
	}
	
	public CPFProjectPage getFirstPage() {
		return firstPage;
	}

}
