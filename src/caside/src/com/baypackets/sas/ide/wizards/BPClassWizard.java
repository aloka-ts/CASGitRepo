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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import com.baypackets.sas.ide.SasPlugin;

public class BPClassWizard extends Wizard implements INewWizard {

	private IWorkbench fWorkbench;
	private IStructuredSelection fSelection;

	private NewTypeWizardPage firstPage;
	private AddHttpMappingAndInitParams httpSecondPage;
	private AddSipMappingAndInitParams sipSecondPage;
	private BPRAMessageHandlerPage raMessageHandler;
	private AddSip289InitParams sip289SecondPage;
	private BPRAResourceListenerPage raResListenerPage;
	
	public BPClassWizard() {
		super();
	}
	
	/*(non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#canRunForked()
	 */
	protected boolean canRunForked() {
		return !firstPage.isEnclosingTypeSelected();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		firstPage.createType(monitor); // use the full progress monitor
	}
		
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	public boolean performFinish(){
		IRunnableWithProgress op= new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				
				if (monitor == null) {
					monitor= new NullProgressMonitor();
				}
				try {
					monitor.beginTask("Creating the New Class .....", 1); 
					finishPage(monitor);
					//reeta added it
					if(httpSecondPage!=null){
					httpSecondPage.AddFieldToDescriptor(monitor);
					}
					if(sipSecondPage!=null){
						sipSecondPage.AddFieldToDescriptor(monitor);
					}
					if(sip289SecondPage!=null){
						sip289SecondPage.AddFieldToDescriptor(monitor);
					}
					if(raMessageHandler!=null){
						raMessageHandler.AddFieldToDescriptor(monitor);
					}
					if(firstPage!=null && firstPage instanceof BPSipListenerPage){
						((BPSipListenerPage)firstPage).AddFieldToDescriptor(monitor);
					}
					if(raResListenerPage!=null){
						raResListenerPage.AddFieldToDescriptor(monitor);
					}
					//
				} catch (CoreException e){
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
		IRunnableWithProgress runnable= new WorkspaceModifyDelegatingOperation(op);
		try {
			getContainer().run(canRunForked(), true, runnable);
		} catch (InvocationTargetException e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			return false;
		} catch  (InterruptedException e) {
			SasPlugin.getDefault().log(e.getMessage(), e);
			return false;
		}
		
		IResource resource= firstPage.getModifiedResource();
		if (resource != null) {
			selectAndReveal(resource);
			openResource((IFile) resource);
		}
		
		return true;
	}

	public IJavaElement getCreatedElement() {
		return firstPage.getCreatedType();
	}
	
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		fWorkbench= workbench;
		fSelection= currentSelection;
	}
	
	public IStructuredSelection getSelection() {
		return fSelection;
	}

	public IWorkbench getWorkbench() {
		return fWorkbench;
	}
	
	protected void selectAndReveal(IResource newResource) {
		BasicNewResourceWizard.selectAndReveal(newResource, fWorkbench.getActiveWorkbenchWindow());
	}
	
	protected void openResource(final IFile resource) {
		final IWorkbenchWindow window= this.fWorkbench.getActiveWorkbenchWindow();
		if (window != null && window.getActivePage() != null) {
			final Display display= getShell().getDisplay();
			if (display != null) {
				display.asyncExec(new Runnable() {
					public void run() {
						try {
							IDE.openEditor(window.getActivePage(), resource, true);
						} catch (PartInitException e) {
							SasPlugin.getDefault().log(e.getMessage(), e);
						}
					}
				});
			}
		}
	}

	public NewTypeWizardPage getFirstPage() {
		return firstPage;
	}

	void setFirstPage(NewTypeWizardPage firstPage) {
		this.firstPage = firstPage;
	}
	
	
//reeta added below code
	public  AddHttpMappingAndInitParams getHttpSecondPage() {
		return this.httpSecondPage;
	}

	void setHttpSecondPage(AddHttpMappingAndInitParams secPage) {
		this.httpSecondPage= secPage;
	}
	

	public  AddSipMappingAndInitParams getSipSecondPage() {
		return this.sipSecondPage;
	}

	void setSipSecondPage(AddSipMappingAndInitParams param) {
		this.sipSecondPage= param;
	}
	
	void setSip289SecondPage(AddSip289InitParams param) {
		this.sip289SecondPage= param;
	}
	
	void setMessageHandlerPage(BPRAMessageHandlerPage raMessageHandler){
		this.raMessageHandler=raMessageHandler;
	}
	public  BPRAMessageHandlerPage getMessageHandlerPage() {
		return this.raMessageHandler;
	}
	
	void setResourceListenerPage(BPRAResourceListenerPage raResListenerPage){
		this.raResListenerPage=raResListenerPage;
	}
	public  BPRAResourceListenerPage getResourceListenerPage() {
		return this.raResListenerPage;
	}


//	
	
}
