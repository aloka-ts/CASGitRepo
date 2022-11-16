package com.genband.m5.maps.ide.wizard;

import java.io.File;
import java.io.FileInputStream;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
import org.eclipse.swt.widgets.Spinner;
import java.util.ArrayList;
import java.util.Locale;
import org.eclipse.core.runtime.NullProgressMonitor;

import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.builder.CPFNature;
import com.genband.m5.maps.ide.model.CPFPortletPreference;

public class AdvancedJSPPrefDialog extends TitleAreaDialog {

	CPFScreenCreationWizard wizard;
	Shell shell;

	public AdvancedJSPPrefDialog(Shell shell, CPFScreenCreationWizard wizard) {
		super(shell);
		this.wizard = wizard;
		this.shell=shell;

	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Screen Preferences");
		newShell.setToolTipText("Define the prefences for the new jsp screen");
		//	this.setMessage("Define screen Preferences");
	}

	private void sendErrorMessage(String message) {
		this.setErrorMessage(message);

	}

	public void create() {
		super.create();
		setTitle("Define screen Preferences");
		setMessage("Define the jsp screen preferences");
	}

	public Control createDialogArea(Composite parent) {

		Composite com = parent;
		GridLayout lay = new GridLayout();
		lay.numColumns = 4;
		com.setToolTipText("Jsp Preferences");
		com.setLayout(lay);

		groupM = new Group(com, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = 4;
		groupM.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		groupM.setLayoutData(gridD);
		groupM.setText("JSP Preferences");

		Group group = new Group(groupM, GridData.FILL_HORIZONTAL);
		layout1 = new GridLayout();
		layout1.numColumns = 4;
		group.setLayout(layout1);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		group.setLayoutData(gridD);
		group.setText("JSP Details");

		preferences = new CPFPortletPreference();
		new Label(group, SWT.LEFT | SWT.WRAP).setText("Title");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		final Text title = new Text(group, SWT.BORDER);
		data.horizontalSpan = 3;
		title.setLayoutData(data);
		
		if(!titleStr.equals("")){
			title.setText(titleStr);
			preferences.setTitle(titleStr);
		}else{
			title.setText(jspName);
			preferences.setTitle(jspName);
		}
		
		title.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				titleStr= title.getText();
				preferences.setTitle(jspName);

			}
		});

		new Label(group, SWT.LEFT | SWT.WRAP).setText("Pagination:");
		data = new GridData(GridData.FILL_HORIZONTAL);
		pagiSpin = new Spinner(group, SWT.SINGLE | SWT.BORDER);
		pagiSpin.setMinimum(0);
		pagiSpin.setMaximum(100);
		pagiSpin.setSelection(pagination);
		preferences.setPagination(pagination);
		pagiSpin.setIncrement(1);
		data.horizontalSpan = 3;
		data.widthHint = 160;
		pagiSpin.setLayoutData(data);

		pagiSpin.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				pagination = pagiSpin.getSelection();
				preferences.setPagination(pagination);
				CPFPlugin.getDefault().log(
						"The Pagination value of screen is.." + pagination);

			}
		});
//		group = new Group(groupM, GridData.FILL_HORIZONTAL);
//		layout1 = new GridLayout();
//		layout1.numColumns = 4;
//		group.setLayout(layout1);
//		gridD = new GridData(GridData.FILL_HORIZONTAL);
//		gridD.horizontalSpan = 3;
//		group.setLayoutData(gridD);
//		group.setText("Window Modes:");
//
//		gridD = new GridData(GridData.FILL_HORIZONTAL);
//		gridD.horizontalSpan = 4;
//		minMode = new Button(group, SWT.BORDER | SWT.CHECK);
//		minMode.setLayoutData(gridD);
//		minMode.setText("Min");
//		minMode.setSelection(true);
		windowModesList.add(CPFConstants.WindowMode.MINIMIZE);
//		minMode.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event e) {
//				if (minMode.getSelection()) {
//					if (windowModesList
//							.indexOf(CPFConstants.WindowMode.MINIMIZE) == -1) {
//						windowModesList.add(CPFConstants.WindowMode.MINIMIZE);
//						preferences.setWindowModes(windowModesList);
//					} else {
//						if (windowModesList
//								.indexOf(CPFConstants.WindowMode.MINIMIZE) != -1) {
//							windowModesList
//									.remove(CPFConstants.WindowMode.MINIMIZE);
//							preferences.setWindowModes(windowModesList);
//						}
//					}
//				}
//			}
//		});
//
//		gridD = new GridData(GridData.FILL_HORIZONTAL);
//		gridD.horizontalSpan = 4;
//		maxMode = new Button(group, SWT.BORDER | SWT.CHECK);
//		maxMode.setLayoutData(gridD);
//		maxMode.setText("Max");
//		maxMode.setSelection(true);
		windowModesList.add(CPFConstants.WindowMode.MAXIMIZE);
//		maxMode.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event e) {
//				if (maxMode.getSelection()) {
//					if (windowModesList
//							.indexOf(CPFConstants.WindowMode.MAXIMIZE) == -1) {
//						windowModesList.add(CPFConstants.WindowMode.MAXIMIZE);
//						preferences.setWindowModes(windowModesList);
//					}
//				} else {
//					if (windowModesList
//							.indexOf(CPFConstants.WindowMode.MAXIMIZE) != -1) {
//						windowModesList
//								.remove(CPFConstants.WindowMode.MAXIMIZE);
//						preferences.setWindowModes(windowModesList);
//					}
//				}
//			}
//		});
//
//		gridD = new GridData(GridData.FILL_HORIZONTAL);
//		gridD.horizontalSpan = 4;
//		normalMode = new Button(group, SWT.BORDER | SWT.CHECK);
//		normalMode.setLayoutData(gridD);
//		normalMode.setText("Normal");
//		normalMode.setSelection(true);
		windowModesList.add(CPFConstants.WindowMode.NORMAL);
//		normalMode.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event e) {
//				if (normalMode.getSelection()) {
//					if (windowModesList.indexOf(CPFConstants.WindowMode.NORMAL) == -1) {
//						windowModesList.add(CPFConstants.WindowMode.NORMAL);
//						preferences.setWindowModes(windowModesList);
//					}
//				} else {
//					if (windowModesList.indexOf(CPFConstants.WindowMode.NORMAL) != -1) {
//						windowModesList.remove(CPFConstants.WindowMode.NORMAL);
//						preferences.setWindowModes(windowModesList);
//					}
//				}
//			}
//		});
//		
//		;
//		
//
//		Label lb = new Label(group, SWT.LEFT | SWT.WRAP);
//		lb.setText("Default Window Mode:");
//		data = new GridData(GridData.FILL_HORIZONTAL);
//		data.horizontalSpan = 4;
//		lb.setLayoutData(data);
//
//		gridD = new GridData(GridData.FILL_HORIZONTAL);
//		gridD.horizontalSpan = 4;
//		normalDefault = new Button(group, SWT.BORDER | SWT.RADIO);
//		normalDefault.setLayoutData(gridD);
//		normalDefault.setText("Normal");
//		normalDefault.setSelection(true);
		preferences
		.setDefaultWindowMode(CPFConstants.WindowMode.NORMAL);
//		normalDefault.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event e) {
//				if (normalDefault.getSelection())
//					preferences
//							.setDefaultWindowMode(CPFConstants.WindowMode.NORMAL);
//
//			}
//		});
//
//		gridD = new GridData(GridData.FILL_HORIZONTAL);
//		gridD.horizontalSpan = 4;
//		maxDefault = new Button(group, SWT.BORDER | SWT.RADIO);
//		maxDefault.setLayoutData(gridD);
//		maxDefault.setText("Max");
//		maxDefault.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event e) {
//				if (maxDefault.getSelection())
//					preferences
//							.setDefaultWindowMode(CPFConstants.WindowMode.MAXIMIZE);
//			}
//		});

		group = new Group(groupM, GridData.FILL_HORIZONTAL);
		layout1 = new GridLayout();
		layout1.numColumns = 4;
		group.setLayout(layout1);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 3;
		group.setLayoutData(gridD);
		group.setText("Portlet Modes:");

		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		viewMode = new Button(group, SWT.CHECK);
		viewMode.setLayoutData(gridD);
		viewMode.setText("View");
		viewMode.setEnabled(false);
		viewMode.setSelection(true);
		portletModesList.add(CPFConstants.PortletMode.VIEW);
		preferences.setPortletModes(portletModesList);
		viewMode.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				if (viewMode.getSelection()) {
					if (portletModesList.indexOf(CPFConstants.PortletMode.VIEW) == -1) {
						portletModesList.add(CPFConstants.PortletMode.VIEW);
						preferences.setPortletModes(portletModesList);
					}

				} else {
					if (portletModesList.indexOf(CPFConstants.PortletMode.VIEW) != -1) {
						portletModesList.remove(CPFConstants.PortletMode.VIEW);
						preferences.setPortletModes(portletModesList);
					}
				}
			}
		});

		//             gridD = new GridData(GridData.FILL_HORIZONTAL);
		//		     gridD.horizontalSpan=4;
		//		     editMode= new Button(group, SWT.BORDER | SWT.CHECK);
		//		     editMode.setLayoutData(gridD);
		//		     editMode.setText("Edit");
		//             editMode.addListener(SWT.Modify, new Listener() {
		//					public void handleEvent(Event e) {
		//						
		////						Text text = (Text) editor3.getEditor();
		//				//		windowModesList.add(CPFConstants.WindowMode.);
		//				//		preferences.setWindowModes()
		//					}
		//				});

		helpMode = new Button(group, SWT.CHECK);
		helpMode.setText("Help");
		helpMode.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				if (helpMode.getSelection()) {

					if (portletModesList.indexOf(CPFConstants.PortletMode.HELP) == -1) {
						portletModesList.add(CPFConstants.PortletMode.HELP);
						preferences.setPortletModes(portletModesList);
					}

					preferences.setHelpSupported(true);
					helpFiles.setEnabled(true);
					browseButton.setEnabled(true);

				} else {

					if (portletModesList.indexOf(CPFConstants.PortletMode.HELP) != -1) {
						portletModesList.remove(CPFConstants.PortletMode.HELP);
						preferences.setPortletModes(portletModesList);
					}

					preferences.setHelpSupported(false);
					helpFiles.setEnabled(false);
					browseButton.setEnabled(false);

				}
			}
		});

		data = new GridData();
		helpFiles = new Text(group, SWT.BORDER);
		data.widthHint = 250;
		helpFiles.setLayoutData(data);
		helpFiles.setEnabled(false);
		helpFiles.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {

				String text = helpFiles.getText();
				//	preferences.set
			}
		});
		browseButton = new Button(group, SWT.PUSH);
		browseButton.setEnabled(false);
		browseButton
				.setText(IDEWorkbenchMessages.ProjectLocationSelectionDialog_browseLabel);
		
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleLocationBrowseButtonPressed();
			}
		});

		setButtonLayoutData(browseButton);
		this.com= (Composite) super.createDialogArea(com);
		return com;

	}

	void handleLocationBrowseButtonPressed() {
		FileDialog dialog = new FileDialog(this.com.getShell());
		dialog.setText("Load help files");
        dialog.setFilterPath(Platform.getLocation().toOSString());
		File path = new File(Platform.getLocation().toOSString());
		if (path.exists())
			dialog.setFilterPath(new Path(Platform.getLocation().toOSString())
					.toOSString());

		String selectedFile = dialog.open();

		if (selectedFile != null) {
			createHelpContentsInProject(selectedFile);
			helpFiles.setText(selectedFile);
			this.sendErrorMessage(null);
		}
		//               else {
		//					this.sendErrorMessage("The selected file is not a jar file");
		//				 }

	}

	public void createHelpContentsInProject(String selectedFile) {
		CPFPlugin.getDefault().log("Selected help file is..." + selectedFile);
		NullProgressMonitor monitor = null;
		IFolder helpContFolder = this.wizard.getFirstPage().getProjectHandle()
				.getFolder(new Path("WebContent").append("help"));
		
	

	//	String paths[] = selectedFile.split("/");
		File  fl=new  File(selectedFile);
		String fileName=fl.getName();
	//	String fileName = paths[paths.length - 1];
		CPFPlugin.getDefault().log("Help file Name is..."+fileName);

		if (!helpContFolder.exists()) {
			try {
				CPFPlugin.getDefault().log("Creating help folder...");
				helpContFolder.create(true, true, monitor);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			monitor = new NullProgressMonitor();
			try {
				
				if (helpContFolder.exists()) {
					CPFPlugin.getDefault().log("Help folder Exits...");
					IFile helpFile = helpContFolder.getFile(fileName);
						IPath helpFilePath = new Path(selectedFile);
						FileInputStream stream = new FileInputStream(
								helpFilePath.toFile());
						CPFPlugin.getDefault().log("Creating help file...");
						helpFile.create(stream, true, monitor);
						preferences.setHelpJsp(fileName);
				}
			} catch (Exception c) {
				CPFPlugin.getDefault().log(
						"The Core exception is thrown while creating help folder"
								+ c);
			}

	}

	public CPFPortletPreference getPortletPreferences() {
		return preferences;

	}

	public void okPressed() {
		CPFPlugin.getDefault().log("OK Pressed for JSP Preferences");
		com.dispose();
		groupM.dispose();
		pagiSpin.dispose();
        viewMode.dispose();
        helpMode.dispose();
//        minMode.dispose();
//        maxMode.dispose();
//        normalMode.dispose();
        helpFiles.dispose();
        browseButton.dispose();
  //      maxDefault.dispose();
   //     normalDefault.dispose();
        this.close();
        super.okPressed();
	}
	
	public void cancelPressed(){
		CPFPlugin.getDefault().log("cancel Pressed for JSP Preferences");
        com.dispose();
		groupM.dispose();
		pagiSpin.dispose();
        viewMode.dispose();
        helpMode.dispose();
//        minMode.dispose();
//        maxMode.dispose();
//        normalMode.dispose();
        helpFiles.dispose();
        browseButton.dispose();
//        maxDefault.dispose();
 //       normalDefault.dispose();
        this.close();
        super.cancelPressed();
	}

	public java.util.List getjarFiles() {
		return filesList;
	}
	
	
	public void setJspName(String jsp){
		jspName=jsp;
	}

	public Composite com;

	Text fileLocationField = null;

	List jars = null;

	Spinner pagiSpin;

	int pagination=10;

	Button viewMode;

	Button editMode;

	Button helpMode;
//
//	Button minMode;
//
//	Button maxMode;
//
//	Button normalMode;

	Text helpFiles;

	Button browseButton;
//
//	Button maxDefault;
//
//	Button normalDefault;
	
	Group groupM;
	
	String jspName="";
	
	String titleStr="";
	
	CPFPortletPreference preferences=null;

	java.util.List<CPFConstants.WindowMode> windowModesList = new ArrayList<CPFConstants.WindowMode>();

	java.util.List<CPFConstants.PortletMode> portletModesList = new ArrayList<CPFConstants.PortletMode>();

	java.util.List filesList = new ArrayList();

}
