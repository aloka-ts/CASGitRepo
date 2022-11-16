package com.genband.m5.maps.ide.wizard;

import java.io.File;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import java.util.ArrayList;
import java.util.Locale;

import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;

public class DataModelImportDialog extends TitleAreaDialog {

	public DataModelImportDialog(Shell shell, String projectLocation) {
		super(shell);
		this.projectLocation = projectLocation;

	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Import Data Model Entity jar File");
	}

	private void sendErrorMessage(String message) {
		this.setErrorMessage(message);

	}

	public void create() {
		super.create();
		setTitle("Load the Entity jar file");
		setMessage("This jar file should contain the Entities");
	}

	public Control createDialogArea(Composite com) {

		this.com = com;
		GridLayout lay = new GridLayout();
		lay.numColumns = 3;
		com.setToolTipText("Import jar file");
		com.setLayout(lay);

		new Label(com, SWT.LEFT | SWT.WRAP).setText("Import");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		fileLocationField = new Text(com, SWT.BORDER);
		data.widthHint = 50;
		data.grabExcessHorizontalSpace = true;
		fileLocationField.setLayoutData(data);
		fileLocationField.setEditable(false);

		Button browseButton = new Button(com, SWT.PUSH);
		browseButton
				.setText(IDEWorkbenchMessages.ProjectLocationSelectionDialog_browseLabel);
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleLocationBrowseButtonPressed();
			}
		});

		browseButton.setEnabled(true);

		Group group = new Group(com, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 3;
		group.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 3;
		group.setLayoutData(gridD);
		group.setText("Imported jar files:");
		group.setFont(com.getFont());
		GridData gridData4 = new GridData(GridData.FILL_BOTH);
		jars = new List(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		gridData4.heightHint = 100;
		jars.setLayoutData(gridData4);

		Button remove = new Button(group, SWT.PUSH);
		remove.setText("remove");
		remove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				String[] jarfiles = jars.getSelection();
				if (jars.getSelection() != null) {
					for (int j = 0; j < jarfiles.length; j++) {
						jars.remove(jarfiles[j]);
						filesList.remove(jarfiles[j]);
					}
				}
			}
		});

		setButtonLayoutData(browseButton);
		Composite comp = (Composite) super.createDialogArea(com);
		return comp;

	}

	void handleLocationBrowseButtonPressed() {
		FileDialog dialog = new FileDialog(this.com.getShell());
		dialog.setText("Import jar file");
		if (!projectLocation.equals("")) { //$NON-NLS-1$
			File path = new File(projectLocation);
			if (path.exists())
				dialog.setFilterPath(new Path(projectLocation).toOSString());
		}

		String selectedFile = dialog.open();
		if (selectedFile != null && selectedFile.endsWith("jar")) {
			fileLocationField.setText(selectedFile);
			filesList.add(selectedFile);
			jars.add(selectedFile);
			this.sendErrorMessage(null);
		} else {
			this.sendErrorMessage("The selected file is not a jar file");
		}
	}

	public void okPressed() {
	
		fileLocationField.dispose();
		jars.dispose();
		com.dispose();
		this.close();
		super.okPressed();
	}

	public java.util.List getjarFiles() {
		return filesList;
	}

	public Composite com;

	String projectLocation = null;

	Text fileLocationField = null;

	List jars = null;

	java.util.List filesList = new ArrayList();

}
