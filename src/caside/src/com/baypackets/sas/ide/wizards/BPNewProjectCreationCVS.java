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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.services.IEvaluationService;
import org.eclipse.ui.services.IServiceLocator;

import com.baypackets.sas.ide.SasPlugin;

public class BPNewProjectCreationCVS extends WizardNewProjectCreationPage {

	public BPNewProjectCreationCVS(String pageName) {
		super(pageName);
	}

	private String appName = "";

	private boolean useGitRepo = true;

	private Label labelName;

	private Text repoLocation;

	private Button buttonUseGitRepo;

	private boolean useExisting = false;
	private String selectedRepoLocation = "";

	private Listener listener = new Listener() {
		public void handleEvent(Event e) {
			selectedRepoLocation = repoLocation.getText();
			validatePage();
		}
	};

	private SelectionListener selectionListener = new SelectionAdapter() {

		public void widgetSelected(SelectionEvent event) {
			useGitRepo = buttonUseGitRepo.getSelection();
			useExisting = buttonExistGit.getSelection();

			if (useGitRepo) {
				enableControls();
			} else {
				disableControls();
			}

			if (useExisting) {
				buttonBrowse.setEnabled(true);
			} else {
				buttonBrowse.setEnabled(false);
			}
			setPageComplete(validatePage());
		}
	};

	private Button buttonExistGit;

	private Button buttonBrowse;

	private void disableControls() {

		repoLocation.setEnabled(false);
		buttonExistGit.setEnabled(false);

	}

	private void enableControls() {

		repoLocation.setEnabled(true);
		buttonExistGit.setEnabled(true);

	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		Composite composite = (Composite) getControl();
		composite.setFont(parent.getFont());
		composite.setLayout(new GridLayout());

		// Does this application uses SBB.
		buttonUseGitRepo = new Button(composite, 32);
		GridData gd = new GridData(768);
		buttonUseGitRepo.setLayoutData(gd);
		buttonUseGitRepo.setSelection(this.useGitRepo);
		buttonUseGitRepo.addSelectionListener(selectionListener);
		buttonUseGitRepo.setText("Use Git Respository ");

		this.createApplicationDataGroup(composite);

	}

	protected void createApplicationDataGroup(Composite composite) {
		Group group = new Group(composite, 0);
		group.setText("Git Respository Location:");

		GridLayout gl = new GridLayout();
		gl.numColumns = 4;
		GridData gd = new GridData(1808);
		// gd.horizontalSpan = 2;
		group.setLayout(gl);
		group.setLayoutData(gd);

		// Does this application uses SBB.
		buttonExistGit = new Button(group, 32);
		gd = new GridData(768);
		gd.widthHint = 250;
		gd.horizontalSpan = 4;
		buttonExistGit.setLayoutData(gd);
		buttonExistGit.setSelection(this.useExisting);
		buttonExistGit.addSelectionListener(selectionListener);
		buttonExistGit.setText("Use Existing :");

		// Application Name label.
		gd = new GridData(768);
		labelName = new Label(group, SWT.NONE);
		labelName.setText("Location:");
		labelName.setFont(composite.getFont());
		gd.horizontalSpan = 1;
		labelName.setLayoutData(gd);

		// Application Name text field.
		repoLocation = new Text(group, SWT.BORDER);
		gd = new GridData(768);
		gd.widthHint = 300;
		gd.horizontalSpan = 2;

		repoLocation.setLayoutData(gd);
		repoLocation.setFont(composite.getFont());
		repoLocation.addListener(SWT.Modify, listener);

		// Does this application uses SBB.
		buttonBrowse = new Button(group, SWT.PUSH);
		gd = new GridData(768);
		gd.horizontalSpan = 1;
		buttonBrowse.setLayoutData(gd);
		buttonBrowse.setSelection(this.useExisting);

		buttonBrowse.setText("Browse:");
		buttonBrowse.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {

				// DirectoryDialog dialog = new DirectoryDialog(buttonBrowse
				// .getShell(), 268435456);
				// dialog.setMessage(IDEWorkbenchMessages.ProjectLocationSelectionDialog_directoryLabel);
				//
				// // dialog.setFilterPath(dirName);
				//
				// selectedRepoLocation = dialog.open();
				// repoLocation.setText(selectedRepoLocation);

				// Obtain IServiceLocator implementer, e.g. from
				// PlatformUI.getWorkbench():
				IServiceLocator serviceLocator = PlatformUI.getWorkbench();
				// or a site from within a editor or view:
				// IServiceLocator serviceLocator = getSite();

				ICommandService commandService = (ICommandService) serviceLocator
						.getService(ICommandService.class);
				// IHandlerService hService=(IHandlerService) serviceLocator
				// .getService(IHandlerService.class);

				IEvaluationService eService = (IEvaluationService) serviceLocator
						.getService(IEvaluationService.class);
				try {

					SasPlugin.getDefault().log(
							" Going to EXECUTE share project command for "
									+ ((BPProjectWizard) getWizard())
											.getFirstPage().getProjectName());
					// Lookup commmand with its ID
					Command command = commandService
							.getCommand("org.eclipse.egit.ui.command.shareProject");

					// org.eclipse.egit.ui.command.shareProject

					// org.eclipse.egit.ui.internal.commands.ProjectNameParameterValues
					// pp=new
					// org.eclipse.egit.ui.internal.commands.ProjectNameParameterValues();

					if (command.isEnabled()) {
						SasPlugin.getDefault().log(
								" The command is enabled" + command
										+ " Category is  "
										+ command.getCategory());
					}

					Map params = new HashMap();
					params.put(
							"org.eclipse.egit.ui.command.projectNameParameter",
							((BPProjectWizard) getWizard()).getFirstPage()
									.getProjectName());

					SasPlugin.getDefault().log(
							"activeShell is "
									+ ((BPProjectWizard) getWizard())
											.getWorkbench()
											.getActiveWorkbenchWindow()
											.getShell());

					SasPlugin.getDefault().log(
							"activeWorkbenchWindow is "
									+ ((BPProjectWizard) getWizard())
											.getWorkbench()
											.getActiveWorkbenchWindow());

					// org.eclipse.jdt.core.eval.IEvaluationContext
					// ec=((IJavaProject)((BPProjectWizard)
					// getWizard()).getFirstPage().getProjectHandle()).newEvaluationContext();
					//
					EvaluationContext evaluationContext = new EvaluationContext(
							null, ((BPProjectWizard) getWizard())
									.getFirstPage().getProjectHandle());
					// SasPlugin.getDefault().log("add variabls to evalution context ");

					// evaluationContext.addVariable("activeShell",
					// ((BPProjectWizard) getWizard())
					// .getWorkbench()
					// .getActiveWorkbenchWindow()
					// .getShell());
					// evaluationContext.addVariable("activeWorkbenchWindow",
					// ((BPProjectWizard) getWizard()).getWorkbench()
					// .getActiveWorkbenchWindow());

					SasPlugin.getDefault().log(
							"create execution event   " + eService);

					IEvaluationContext ecCurrent = eService.getCurrentState(); // hService.createContextSnapshot(true);
					EvaluationContext ec = new EvaluationContext(ecCurrent,
							((BPProjectWizard) getWizard()).getFirstPage()
									.getProjectHandle());

					ec.addVariable("activeShell",
							((BPProjectWizard) getWizard()).getWorkbench()
									.getActiveWorkbenchWindow().getShell());
					ec.addVariable("activeWorkbenchWindow",
							((BPProjectWizard) getWizard()).getWorkbench()
									.getActiveWorkbenchWindow());

					SasPlugin.getDefault()
							.log(" Evaluation context is   " + ec);

					ExecutionEvent EE = new ExecutionEvent(command, params,
							null, ec);

					SasPlugin.getDefault().log(
							" EXECUTE it using event   " + EE);

					// SasPlugin.getDefault().log("add variabls to evalution context "+((EvaluationContext)EE.getApplicationContext()).getVariable("activeShell"));

					// Event e=new Event();
					//
					// hService.createContextSnapshot(true);
					// .createExecutionEvent(command, new Event());

					// Optionally pass a ExecutionEvent instance, default
					// no-param arg creates blank event

					SasPlugin.getDefault().log(
							" RUN EXECUTE command........." + command);
					SasPlugin.getDefault().log(
							"get variabls from evalution context "
									+ ec.getVariable("activeShell"));
					command.executeWithChecks(EE);

					SasPlugin.getDefault().log(" command EXECUTED ********* ");
				} catch (Exception e) {

				}

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// TODO Auto-generated method stub

			}

		});

	}

	public boolean validatePage() {

		this.setErrorMessage(null);
		return true;
	}

	public String getAppName() {
		return appName;
	}

	public boolean isUseGitRepo() {
		return useGitRepo;
	}

	public String getSelectedRepoLocation() {
		return selectedRepoLocation;
	}

	public boolean getUseExistingRepo() {
		return useExisting;
	}

}
