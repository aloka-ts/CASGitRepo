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

/**********************************************************************
 *
 *     Project:  CPFSupport
 *
 *     Package:  com.genband.m5.maps.ide.sitemap
 *
 *     File:     SiteMapCreationWizard.java
 *
 *     Desc:   	Creates a wizard to create a siteMap.
 *
 *	  Author 	 Reeta Aggarwal Date					Description
 *    ---------------------------------------------------------
 *	  AGNITY	 December 28, 2007		Initial Creation
 *
 **********************************************************************
 **/


package com.baypackets.sas.ide.alc;


import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;

import com.baypackets.sas.ide.SasPlugin;

/**
 * Create new sitemap Actually this file creates a wizard which is used to
 * create a new siteMap Using this wizard one can create a file having extension
 * .sitemap These files can be opened with the SiteMapEditor
 * 
 * @author Reeta Aggarwal
 */
public class AlcFileCreationWizard extends Wizard implements INewWizard {

	private CreationPage alcFileCreationPage;
	private static final String TEMPLATE_ALC=  "resources/alc/template.alcml";

	public void addPages() {
		// add pages to this wizard
		addPage(alcFileCreationPage);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// create pages for this wizard
		alcFileCreationPage = new CreationPage(workbench, selection);
	}

	/**
	 * This method will be invoked, when the "Finish" button is pressed.
	 */
	public boolean performFinish() {
		return alcFileCreationPage.finish();
	}

	/**
	 * This WizardPage is used to create a new SiteMap
	 */

	private class CreationPage extends org.eclipse.jface.wizard.WizardPage {
		private static final String DEFAULT_EXTENSION = "alcml";
		private final IWorkbench workbench;
		Composite composite = null;

		/**
		 * Create a new wizard page instance.
		 * 
		 * @param workbench
		 *            the current workbench
		 * @param selection
		 *            the current object selection
		 */
		CreationPage(IWorkbench workbench, IStructuredSelection selection) {
			super("AlcFileCreation");
			this.workbench = workbench;
			setTitle("Create a new " + " ALC XML File");
			setDescription("Create a new ." + DEFAULT_EXTENSION + " file for the ALC Application.");
	//		setFileExtension(DEFAULT_EXTENSION);
		}

		
		/*
		 * This function is called by performFinish() this does all the tasks
		 * that are to be accomplished when 'Finish' Button is clicked This
		 * function will open the newly created siteMap(file) and activate it
		 */
		boolean finish() {

			// create a new file
			
			IFile newFile = createNewFile();

			// open newly created siteMap(file) in the editor
			IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
					.getActivePage();
			if (newFile != null && page != null) {
				try {
					IDE.openEditor(page, newFile, true);
				} catch (PartInitException e) {
					e.printStackTrace();
					return false;
				}
			}
			return true;
		}
		
		
		

		public void createControl(Composite parent) {
			// super.createControl(parent);
			initializeDialogUnits(parent);
			int nColumns = 4;
			composite = new Composite(parent, SWT.NONE);
			composite.setFont(parent.getFont());
			composite.getShell().setText("ALC XML File Creation");
			composite.pack();
			GridLayout layout = new GridLayout();
			layout.numColumns = nColumns;
			composite.setLayout(layout);

			Group group = new Group(composite, GridData.FILL_HORIZONTAL);
			GridLayout layout1 = new GridLayout();
			layout1.numColumns = 4;
			group.setLayout(layout1);
			GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
			gridD.horizontalSpan = 4;
			group.setLayoutData(gridD);
			group.setText("Select ALC Project and Enter ALC XML File Name:");
			new Label(group, SWT.LEFT | SWT.WRAP).setText("ALC Project Name:");
			gridD = new GridData(GridData.FILL_HORIZONTAL);
			final CCombo alcProject = new CCombo(group, SWT.SINGLE | SWT.BORDER);
			// gridD.horizontalSpan = nColumns;
			gridD.grabExcessHorizontalSpace = true;
			gridD.horizontalSpan = 3;
			alcProject.setLayoutData(gridD);

			IProject[] resources = ResourcesPlugin.getWorkspace().getRoot()
					.getProjects();
			try {
				for (int i = 0; i < resources.length; i++) {
					if (resources[i].isOpen()
							&& resources[i].hasNature(AlcNature.NATURE_ID)) {
						alcProject.add(resources[i].getName());
						alcProject.select(0);
						projectName = alcProject.getItem(0);
						SasPlugin.getDefault().log(
								"The PROJECT NAME IS!!!!!!!!!!!!!!"
										+ projectName);
					}
				}

				if (projectName == null)
					this
							.setErrorMessage("There is no ALC Project in the Workspace");
			} catch (CoreException c) {
				SasPlugin.getDefault().log(
						"The Core exception was thrown while lisitng alcProjects"
								+ c);
			}

			alcProject.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {
					projectName = alcProject.getText();
					//ProjectUtil.setProjectName(projectName);
					SasPlugin.getDefault().log(
							"The Project selected is....." + projectName);

				}
			});
			new Label(group, SWT.LEFT | SWT.WRAP).setText("ALC File Name");
			GridData grid = new GridData(GridData.FILL_HORIZONTAL);
			final Text tFileName = new Text(group, SWT.SINGLE | SWT.BORDER);
			grid.horizontalSpan = 3;
			grid.grabExcessHorizontalSpace = true;
			grid.widthHint = this.SIZING_TEXT_FIELD_WIDTH;
			tFileName.setLayoutData(grid);
			tFileName.setTextLimit(80);
			tFileName.addListener(SWT.Modify, new Listener() {
				public void handleEvent(Event e) {
					fileName = tFileName.getText();
					setPageComplete(validatePage());
				}
			});
			getWizard().getContainer().updateButtons();
			setControl(composite);
			// setVisible(true);
			setPageComplete(false);
			Dialog.applyDialogFont(composite);
			
		}

		protected InputStream getInitialContents() {
			// SiteMap siteMapDiagram = (SiteMap) createDefaultContent();
			InputStream stream = null;
			try {
		
				      URL template=SasPlugin.getDefault().getBundle().getEntry(TEMPLATE_ALC);
				      if(template!=null){
					  URL loc= Platform.resolve(template);
					  String path=loc.getPath();
					  SasPlugin.getDefault().log("The template file path to create Alc file is.." +path);
	         		  stream = new FileInputStream(path);
			          }
						
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
			return stream;
		}

		public String getFileName() {
			return fileName;
		}

		

		protected IFile createFileHandle() {
			if(fileName.endsWith(this.DEFAULT_EXTENSION)){
				fileName=fileName.substring(0 ,fileName.indexOf("."));
			}
			return getProjectHandle().getFile(
					new Path("WEB-INF").append("xml").append(fileName).addFileExtension(
									this.DEFAULT_EXTENSION));
		}

		public IProject getProjectHandle() {
			if (projectName != null)
				return ResourcesPlugin.getWorkspace().getRoot().getProject(
						projectName);
			else
				return null;
		}

		public IFile createNewFile() {
			IFile newFile = null;
			// create the new file and cache it if successful

			final IFile newFileHandle = createFileHandle();
			final InputStream initialContents = getInitialContents();
			IRunnableWithProgress runnable = new PerformAlcFileCreation(newFileHandle ,initialContents);
			IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation (runnable);

			try {
				getContainer().run(true, true, op);
			} catch (InterruptedException e) {
				return null;
			} catch (InvocationTargetException e) {
				if (e.getTargetException() instanceof CoreException) {
					ErrorDialog
							.openError(
									getContainer().getShell(),
									IDEWorkbenchMessages.WizardNewFileCreationPage_errorTitle,
									null, // no special message
									((CoreException) e.getTargetException())
											.getStatus());
				} else {
					// CoreExceptions are handled above, but unexpected runtime
					// exceptions and errors may still occur.
					SasPlugin.getDefault().log(
							"createNewFile() InvocationTarget Excrption"); //$NON-NLS-1$
					MessageDialog.openError(getContainer().getShell(),
							"FileCreationFailed", e.getTargetException()
									.getMessage());
				}
				return null;
			}

			newFile = newFileHandle;

			return newFile;
		}

		class PerformAlcFileCreation implements IRunnableWithProgress {
			 
			 private IFile newFileHandle;
		     private InputStream initialContents ;
			
		     public PerformAlcFileCreation(IFile newFileHandle,InputStream initialContents){
				 this.newFileHandle = newFileHandle;
				  this.initialContents = initialContents;
			}
			  
			  public void run(IProgressMonitor monitor) throws InvocationTargetException,
				InterruptedException {
				try {
					
					monitor
							.beginTask(
									IDEWorkbenchMessages.WizardNewFileCreationPage_progress,
									2000);
					try {
						createFile(newFileHandle, initialContents,
								new SubProgressMonitor(monitor, 1000));
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} finally {
					monitor.done();
				}
			} 
			  
			  protected void createFile(IFile fileHandle, InputStream contents,
					               IProgressMonitor monitor) throws CoreException {
					           if (contents == null) {
					              contents = new ByteArrayInputStream (new byte[0]);
					          }
					           try {
					              // Create a new file resource in the workspace
					                  IPath path = fileHandle.getFullPath();
					                  IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
					                  int numSegments = path.segmentCount();
					                  if (numSegments > 2
					                          && !root.getFolder(path.removeLastSegments(1)).exists()) {
					                      // If the direct parent of the path doesn't exist, try to
					  // create the
					  // necessary directories.
					  for (int i = numSegments - 2; i > 0; i--) {
					                          IFolder folder = root.getFolder(path
					                                   .removeLastSegments(i));
					                           if (!folder.exists()) {
					                               folder.create(false, true, monitor);
					                           }
					                       }
					                   }
					                  fileHandle.create(contents, false, monitor);
					              
					          } catch (CoreException e) {
					               // If the file already existed locally, just refresh to get contents
					   if (e.getStatus().getCode() == IResourceStatus.PATH_OCCUPIED) {
					                   fileHandle.refreshLocal(IResource.DEPTH_ZERO, null);
					               } else {
					                   throw e;
					               }
					           }
					  
					           if (monitor.isCanceled()) {
					               throw new OperationCanceledException();
					          }
					       }
		}

		
		/**
		 * Return true, if the file name entered in this page is valid.
		 */
		private boolean validateFilename() {
			boolean value=true;
			SasPlugin.getDefault().log("Validating............Filename is"+fileName);
			  setErrorMessage(null);
			 if (fileName==null||fileName.equals("")) {
				 setErrorMessage("The 'file' name can not be null"); 
			     value=false;
			 }else if (-1 != fileName.indexOf('.')&&!fileName.endsWith(DEFAULT_EXTENSION)) {
			     setErrorMessage("The 'file' name must have extension ."+ DEFAULT_EXTENSION);
			     value=false;
			 }
			 
			 
			 
			 com.baypackets.sas.ide.SasPlugin.getDefault().log("Validating............value is"+value );
			return value;
		}

		protected boolean validatePage() {
			return validateFilename();
		}


		protected String[] roles;
		String projectName = null;
		Table table = null;
		String fileName = null;
		Group tablegroup = null;

		private static final int SIZING_TEXT_FIELD_WIDTH = 200;

	
		
		
		
		
	}

}
