package com.genband.m5.maps.ide.wizard;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.jdt.internal.ui.dialogs.TypeSelectionDialog2;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

//import com.genband.sasprov.wizard.util.PersistableEntity;
import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.builder.CPFNature;
import com.genband.m5.maps.ide.model.*;
import com.genband.m5.maps.ide.model.util.*;
import com.genband.m5.maps.ide.preferences.PreferenceConstants;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name.
 */

public class CPFScreenMainPage extends NewTypeWizardPage {

	private ISelection selection;

	private CPFScreenCreationWizard wizard;

	java.util.List selectedEntities;

	Text jspFileName = null;

	String fileName = "";

	IPath jspFileLocation = null;

	public String projectName=null;

	private List classes;

	public Composite parentComposite = null;

	private CCombo cpfProject = null;

	private Combo baseEntityCombo = null;

	Label roleLabel = null;

	Button createAllow = null;

	Button viewAllow = null;

	Button modifyAllow = null;
	
	Button listAllow = null;
	
	Button deleteAllow = null;
	
	Composite composite = null;

	Table table = null;

	/**
	 * Constructor for CPFScreenMainPage.
	 * 
	 */
	public CPFScreenMainPage(ISelection selection,
			CPFScreenCreationWizard wizard) {
		super(false, "FirstPage");
		setTitle("New Provisioning Screen Creation");
		setDescription("Create a New Provisioning Screen");
		this.selection = selection;
		this.wizard = wizard;
	}

	private Listener locationModifyListener = new Listener() {
		public void handleEvent(Event e) {
			String jspFileLoc = jspLocationPathField.getText();
			jspFileLocation = new Path(jspFileLoc);
			CPFPlugin.getDefault().log(
					"The jsp file Field value is..." + jspFileLoc);
		}
	};

	// constants
	private static final int SIZING_TEXT_FIELD_WIDTH = 200;

	public void createControl(Composite parent) {
		parentComposite = parent;
		initializeDialogUnits(parent);
		composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
		composite.getShell().setText("New Screen");
		composite.getShell().setSize(500, 400);
		composite.pack();
		int nColumns = 4;

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

		if (selectedEntities != null) {
			selectedEntities.clear();
		}
		if (selectedEntityList != null) {
			selectedEntityList.removeAll();
		}
		selectedEntities = new java.util.ArrayList();

		new Label(group, SWT.LEFT | SWT.WRAP).setText("Portal Project Name:");
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		cpfProject = new CCombo(group, SWT.SINGLE | SWT.BORDER|SWT.READ_ONLY);
		//		gridD.horizontalSpan = nColum
		gridD.grabExcessHorizontalSpace = true;
		gridD.widthHint = this.SIZING_TEXT_FIELD_WIDTH;
		cpfProject.setLayoutData(gridD);

		IProject[] resources = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		try {
			for (int i = 0; i < resources.length; i++) {
				if (resources[i].isOpen()
						&& resources[i].hasNature(CPFNature.NATURE_ID)) {
					cpfProject.add(resources[i].getName());
					cpfProject.select(0);
					projectName = cpfProject.getItem(0);
					CPFPlugin.getDefault().log("The PROJECT NAME IS!!!!!!!!!!!!!!"+projectName);
				}
			}
		
			if(projectName==null)
				this.setErrorMessage("There is no Potal Project in the Workspace");
		} catch (CoreException c) {
			CPFPlugin.getDefault().log(
					"The Core exception was thrown while lisitng CPFprojects"
							+ c);
		}

		cpfProject.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				projectName = cpfProject.getText();
				CPFPlugin.getDefault().log(
						"The Project selected is....." + projectName);
				jspLocationPathField.setText(ResourcesPlugin.getWorkspace()
						.getRoot().getProject(projectName).getFolder(
								"WebContent").getFullPath().toOSString());
				jspFileLocation = ResourcesPlugin.getWorkspace().getRoot()
						.getProject(projectName).getFolder("WebContent")
						.getFullPath();

				// Updating Roles Table as per the The new Project Selected
				if (table != null) {
					table.dispose();
					if(!showListFlag){
					createRolesTable(tablegroup);
					createRoleItems();
					}else{
						createRolesTable(tablegroup);
					    createListAndDeleteRoleItems();
					}
					tablegroup.layout();
				}

				validatePage();
				getWizard().getContainer().updateButtons();

			}
		});

		group = new Group(composite, GridData.FILL_HORIZONTAL);
		layout1 = new GridLayout();
		layout1.numColumns = nColumns;
		group.setLayout(layout1);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = nColumns;
		group.setLayoutData(gridD);
		group.setText("Select the Interface for Screen:");

		GridData gr = new GridData(SWT.LEFT);
		final Button webIf = new Button(group, SWT.CHECK);
		webIf.setText("Web Interface");
		infList.add(CPFConstants.InterfaceType.PORTLET);
		wizard.getCPFScreen().setInterfaceType(infList);
		webIf.setLayoutData(gr);
		webIf.setSelection(true);
		webIf.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				if (webIf.getSelection()) {
					CPFPlugin.getDefault().log("Web Interface Selected......");
					if (infList.indexOf(CPFConstants.InterfaceType.PORTLET) == -1) {
						infList.add(CPFConstants.InterfaceType.PORTLET);
						wizard.getCPFScreen().setInterfaceType(infList);
					}
					locationLabel.setEnabled(true);
					jspNameLabel.setEnabled(true);
					jspFileName.setEnabled(true);
					jspLocationPathField.setEnabled(true);
					browseButton.setEnabled(true);
					if(showListFlag){
						advanced.setEnabled(true);
					}
				} else if (!webIf.getSelection()) {
					CPFPlugin.getDefault()
							.log("Web Interface UnSelected......");
					locationLabel.setEnabled(false);
					jspNameLabel.setEnabled(false);
					jspFileName.setEnabled(false);
					jspLocationPathField.setEnabled(false);
					browseButton.setEnabled(false);
					advanced.setEnabled(false);
					if (infList.indexOf(CPFConstants.InterfaceType.PORTLET) != -1)
						infList.remove(CPFConstants.InterfaceType.PORTLET);
				}
				validatePage();
				getWizard().getContainer().updateButtons();
			}
			

		});

		gr = new GridData(SWT.LEFT);
		final Button soapIf = new Button(group, SWT.CHECK);
		soapIf.setText("Soap Interface");
		soapIf.setLayoutData(gr);
		soapIf.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				if (soapIf.getSelection()) {
					CPFPlugin.getDefault().log("Soap Interface Selected......");
					if (infList.indexOf(CPFConstants.InterfaceType.WEB_SERVICE) == -1) {
						infList.add(CPFConstants.InterfaceType.WEB_SERVICE);
						wizard.getCPFScreen().setInterfaceType(infList);
						//advanced.setEnabled(false);
					}
				} else if (!soapIf.getSelection()) {
					CPFPlugin.getDefault().log(
							"Soap Interface UnSelected......");
					if (infList.indexOf(CPFConstants.InterfaceType.WEB_SERVICE) != -1)
						infList.remove(CPFConstants.InterfaceType.WEB_SERVICE);
					//if (showListFlag == true)
						//advanced.setEnabled(true);
				}
				validatePage();
				getWizard().getContainer().updateButtons();
			}
			
		});

		group = new Group(composite, GridData.FILL_HORIZONTAL);
		layout1 = new GridLayout();
		layout1.numColumns = nColumns;
		group.setLayout(layout1);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = nColumns;
		group.setLayoutData(gridD);
		group.setText("Select Screen Type:");

		GridData gridDa = new GridData(SWT.NONE);
		create = new Button(group, SWT.RADIO);
		gridDa.horizontalSpan = nColumns;
		create.setText("Create/Modify/View");
		create.setLayoutData(gridDa);
		create.setSelection(true);
		create.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				if (create.getSelection()) {
					createAndUpdate = true;
					showListFlag = false;
					listAndDel.setSelection(false);
					viewType = CPFConstants.ViewType.DETAILS_VIEW;
					advanced.setEnabled(false);
					if (table != null) {
						table.dispose();
						createRolesTable(tablegroup);
						createRoleItems();
						tablegroup.layout();
					}
				}
				
				validatePage();
				getWizard().getContainer().updateButtons();
			}
		});

		GridData gridData = new GridData(SWT.NONE);
		listAndDel = new Button(group, SWT.RADIO);
		gridData.horizontalSpan = nColumns;
		listAndDel.setText("List/Delete");
		listAndDel.setLayoutData(gridData);
		listAndDel.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event event) {
				if (listAndDel.getSelection()) {
					
					showListFlag=true;
					createAndUpdate = false;
					create.setSelection(false);
					viewType = CPFConstants.ViewType.LIST;
					
					if(webIf.getSelection())
					  advanced.setEnabled(true);
					
					if (table != null) {
						table.dispose();
						createRolesTable(tablegroup);
					    createListAndDeleteRoleItems();
						tablegroup.layout();
					}
				}else{
					advanced.setEnabled(false);
				}
				
				validatePage();
				getWizard().getContainer().updateButtons();

			}
		});

		jspNameLabel = new Label(group, SWT.LEFT | SWT.WRAP);
		jspNameLabel.setText("XHTML File Name:");
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		jspFileName = new Text(group, SWT.SINGLE | SWT.BORDER);
		grid.horizontalSpan = 3;
		grid.grabExcessHorizontalSpace = true;
		grid.widthHint = this.SIZING_TEXT_FIELD_WIDTH;
		jspFileName.setLayoutData(grid);
		jspFileName.setTextLimit(150);
		jspFileName.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				fileName = jspFileName.getText();
				CPFPlugin.getDefault().log(
						"The jsp fileName is....... " + fileName);
				validatePage();
				getWizard().getContainer().updateButtons();
			}
		});

		createJspFileLocationGroup(group);

		tablegroup = new Group(composite, GridData.FILL_HORIZONTAL);
		layout1 = new GridLayout();
		layout1.numColumns = 4;
		tablegroup.setLayout(layout1);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		tablegroup.setLayoutData(gridD);
		tablegroup.setText("Set User Roles:");
		tablegroup.setRedraw(true);
		tablegroup.pack(true);

		this.createRolesTable(tablegroup);
		this.createRoleItems();

		GridData gd = new GridData(SWT.RIGHT);
		advanced = new Button(composite, SWT.PUSH);
		gd.verticalIndent=5;
		advanced.setText("Advanced");
		advanced.setLayoutData(gd);
		advanced.setEnabled(false);
		advanced.addListener(SWT.Selection, new Listener() {

			AdvancedJSPPrefDialog dialog = null;
			public void handleEvent(Event e) {

				if(dialog==null){
				dialog = new AdvancedJSPPrefDialog(composite
						.getShell(), wizard);
				dialog.setJspName(fileName);
				Composite com = (Composite) dialog.createDialogArea(composite);
				dialog.create();
				}
				dialog.setJspName(fileName);
			    dialog.open();

				if (dialog.getReturnCode() == Window.OK) {
					portletPreferences = dialog.getPortletPreferences();
				}

			}
		});

	//	getWizard().getContainer().updateButtons();
		setControl(composite);

		Dialog.applyDialogFont(composite);
	}

	/**
	 * Creates the project location specification controls.
	 * 
	 * @classes projectGroup the parent composite
	 * @classes enabled the initial enabled state of the widgets created
	 */
	private void createJspFileLocationGroup(Composite parent) {

		Font font = parent.getFont();

		// location label
		locationLabel = new Label(parent, SWT.NONE);
		locationLabel.setText("Create In:");
		locationLabel.setFont(font);

		// project location entry field
		jspLocationPathField = new Text(parent, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		data.grabExcessHorizontalSpace = true;
		jspLocationPathField.setLayoutData(data);
		jspLocationPathField.setFont(font);
		jspLocationPathField.setEnabled(true);
		jspLocationPathField.setEditable(false);
		// browse button
		browseButton = new Button(parent, SWT.PUSH);
		browseButton
				.setText(IDEWorkbenchMessages.ProjectLocationSelectionDialog_browseLabel);
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				jspLocationPathField.setEnabled(true);
				handleLocationBrowseButtonPressed();
			}
		});
		browseButton.setFont(font);
		setButtonLayoutData(browseButton);

		// Set the initial value first before listener
		// to avoid handling an event during the creation.
		if (initialLocationFieldValue == null){
			if(projectName!=null){
			jspLocationPathField.setText(ResourcesPlugin.getWorkspace()
					.getRoot().getProject(projectName).getFolder("WebContent")
					.getFullPath().toOSString());
			jspFileLocation = ResourcesPlugin.getWorkspace().getRoot().getProject(
					projectName).getFolder("WebContent").getFullPath();
			jspLocationPathField.addListener(SWT.Modify, locationModifyListener);
			}
		}else{
			if(projectName!=null){
			jspLocationPathField.setText(initialLocationFieldValue);
		jspLocationPathField.addListener(SWT.Modify, locationModifyListener);
		jspFileLocation = ResourcesPlugin.getWorkspace().getRoot().getProject(
				projectName).getFolder("WebContent").getFullPath();
			}
		}
	}

	/**
	 * Creates a project resource handle for the current project name field
	 * value.
	 * <p>
	 * This method does not create the project resource; this is the
	 * responsibility of <code>IProject::create</code> invoked by the new
	 * project resource wizard.
	 * </p>
	 * 
	 * @return the new project resource handle
	 */
	public IProject getProjectHandle() {
		if(getProjectName()!=null)
		return ResourcesPlugin.getWorkspace().getRoot().getProject(
				getProjectName());
		else
			return null;
	}

	/**
	 * Returns the current project name as entered by the user, or its
	 * anticipated initial value.
	 * 
	 * @return the project name, its anticipated initial value, or
	 *         <code>null</code> if no project name is known
	 */
	public String getProjectName() {
		return getProjectNameFieldValue();
	}

	/**
	 * Returns the value of the project name field with leading and trailing
	 * spaces removed.
	 * 
	 * @return the project name in the field
	 */
	private String getProjectNameFieldValue() {
		if (projectName == null)
			return null;
			//cpfProject.getItem(0);
		else
			return projectName;
	}

	/**
	 * Returns the value of the project location field with leading and trailing
	 * spaces removed.
	 * 
	 * @return the project location directory in the field
	 */
	private String getProjectLocationFieldValue() {
		if (jspLocationPathField == null)
			return ""; //$NON-NLS-1$

		return jspLocationPathField.getText().trim();
	}

	void handleLocationBrowseButtonPressed() {

	  if(this.getProjectHandle()!=null){
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(
				jspLocationPathField.getShell(), this.getProjectHandle()
						.getFolder("WebContent"), false,
				"Select the Project to configure");
		dialog.setTitle("Select XHTML File Location");
		//    dialog.s
		dialog.showClosedProjects(false);
		dialog.setMessage("Select the XHTML File Location in the Portal Project");
		if (dialog.open() == Window.OK) {
			Object[] obj = dialog.getResult();
			IPath fol = (IPath) obj[0];
			IFolder folder = this.getProjectHandle().getFolder(fol);
			CPFPlugin.getDefault().log(
					"The Jsp Folder name is...." + folder.getName()
							+ "path to folder is.." + fol.toOSString());

			if (folder != null) {
				customLocationFieldValue = fol.toOSString();
				jspLocationPathField.setText(customLocationFieldValue);

			}
		}
		}
	}

	/**
	 * Sets the initial project name that this page will use when created. The
	 * name is ignored if the createControl(Composite) method has already been
	 * called. Leading and trailing spaces in the name are ignored.
	 * 
	 * @classes name initial project name for this page
	 */
//	public void setInitialProjectName(String name) {
//		if (name == null)
//			initialProjectFieldValue = null;
//		else {
//			initialProjectFieldValue = name.trim();
//			initialLocationFieldValue = getDefaultLocationForName(initialProjectFieldValue);
//		}
//	}

	/**
	 * Set the location to the default location if we are set to useDefaults.
	 */
	void setLocationForSelection() {
		if (useDefaults)
			jspLocationPathField
					.setText(getDefaultLocationForName(getProjectNameFieldValue()));
	}

	/**
	 * Get the defualt location for the provided name.
	 * 
	 * @classes nameValue the name
	 * @return the location
	 */
	private String getDefaultLocationForName(String nameValue) {
		IPath defaultPath = Platform.getLocation().append(nameValue);
		return defaultPath.toOSString();
	}

	/**
	 * Returns the useDefaults.
	 * 
	 * @return boolean
	 */
	public boolean useDefaults() {
		return useDefaults;
	}

	public void createRolesTable(Group group) {
		CPFPlugin.getDefault().log(
				"CREATE roles tableeeeeeeeeeee ............." + group);
		table = new Table(group, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION
				| SWT.HIDE_SELECTION | SWT.V_SCROLL);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		gridD.heightHint = 50;
		
		if(showListFlag){
		    gridD.widthHint=420;
		}else{
			gridD.widthHint=400;
		}
		table.setSize(gridD.widthHint, 50);
		table.setLayoutData(gridD);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		// Create 3 columns
		if(showListFlag){
			// Create 2 columns
			for (int i = 0; i < 3; i++) {
				TableColumn column = new TableColumn(table, SWT.NULL);
				if (i == 0)
					column.setText("Role Name");
				if (i == 1)
					column.setText("List");
				if (i == 2)
					column.setText("Delete");
				column.setWidth(140);
			}
			
		  }else{
			  for (int i = 0; i < 4; i++) {
					TableColumn column = new TableColumn(table, SWT.NULL);
					if (i == 0)
						column.setText("Role Name");
					if (i == 1)
						column.setText("Create");
					if (i == 2)
						column.setText("View");
					if (i == 3)
						column.setText("Modify");
					column.setWidth(100);
				}
			  
		  }

	}

	public void createRoleItems() {
		roles = getRolesList();
		 screenRolesMap = new HashMap<CPFConstants.OperationType, java.util.List<String>>();
		 modifyRoles = new java.util.ArrayList<String>();
         viewRoles = new java.util.ArrayList<String>();
         createRoles = new java.util.ArrayList<String>();
         
    if(roles!=null){
		for (int j = 0; j < roles.length; j++) {
			// Create the rows
			final TableItem item = new TableItem(table, SWT.NULL);
			CPFPlugin.getDefault().log("Adding Item ......" + item,
					IStatus.INFO);

			CPFPlugin.getDefault().log(
					"item.getParent Display ............."
							+ item.getParent().getDisplay());

			// Create an editor object to use for text editing

			CPFPlugin.getDefault().log("Adding Role ......" + roles[j]);
			final TableEditor editor = new TableEditor(table);
			editor.grabHorizontal = true;
			roleLabel = new Label(table, SWT.NULL);

			roleLabel.setText(roles[j]);
			roleLabel.setToolTipText(roles[j]);
			item.setText(0, roles[j]);
			editor.setEditor(roleLabel, item, 0);

			// Create an editor object to use for text editing
			final TableEditor editor3 = new TableEditor(table);
			editor3.grabHorizontal = true;
			CPFPlugin.getDefault().log("Adding editor ............." + editor3);
			createAllow = new Button(table, SWT.CHECK);
			
			// Create an editor object to use for text editing
			final TableEditor editor2 = new TableEditor(table);
			editor2.grabHorizontal = true;
			viewAllow = new Button(table, SWT.CHECK);
			
			//				 Create an editor object to use for text editing
			final TableEditor editor1 = new TableEditor(table);
			editor1.grabHorizontal = true;
			modifyAllow = new Button(table, SWT.CHECK);
			
			
			//adding listeners to the editors
			createAllow.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event e) {

					Button bt = (Button) editor3.getEditor();
					TableItem item = editor3.getItem();
					String txt = item.getText(0);
					CPFPlugin.getDefault().log(
							"The table item selected is...." + item
									+ "The Role on item is..." + txt);
					if (bt.getSelection()) {
						CPFPlugin.getDefault().log("Create Selected.........");
						if (createRoles.indexOf(txt) == -1){
							CPFPlugin.getDefault().log(
									"Adding role to create............." + txt);
						createRoles.add(txt);
						}
						
						//enable view also
						if(viewRoles.indexOf(txt)==-1){
						((Button)editor2.getEditor()).setSelection(true);
						viewRoles.add(txt);
						screenRolesMap.put(CPFConstants.OperationType.VIEW,
								viewRoles);
						}
						
					} else if (!bt.getSelection()) {
						CPFPlugin.getDefault()
								.log("Create UnSelected.........");
						if (createRoles.indexOf(txt) != -1){
							CPFPlugin.getDefault().log(
									"Removing role from create............."
											+ txt);
						createRoles.remove(txt);
						}
					}
					screenRolesMap.put(CPFConstants.OperationType.CREATE,
							createRoles);
					if(createRoles.isEmpty()){
						screenRolesMap.remove(CPFConstants.OperationType.CREATE);
					}
					wizard.getCPFScreen().setMappedRoles(screenRolesMap);
					validatePage();
					getWizard().getContainer().updateButtons();
				}
			});

			editor3.setEditor(createAllow, item, 1);
			
			
			viewAllow.addListener(SWT.Selection, new Listener() {

				public void handleEvent(Event e) {
					Button bt = (Button) editor2.getEditor();
					String txt = editor2.getItem().getText(0);
					
					CPFPlugin.getDefault().log(
							"The table item selected is...."
									+ editor2.getItem()
									+ "The Role on item is..." + txt);
					if (bt.getSelection()) {
						CPFPlugin.getDefault().log("View Selected.........");
						if (viewRoles.indexOf(txt) == -1){
							CPFPlugin.getDefault().log(
									"Adding role to View............" + txt);
						viewRoles.add(txt);
						}

				   } else if (!bt.getSelection()) {
						
						//disable create also
						if(createRoles.indexOf(txt)!=-1){
						((Button)editor3.getEditor()).setSelection(false);
						createRoles.remove(txt);
						screenRolesMap.put(CPFConstants.OperationType.CREATE,
								createRoles);
						}
						
//						//disable modify also
						if(modifyRoles.indexOf(txt)!=-1){
						((Button)editor1.getEditor()).setSelection(false);
						modifyRoles.remove(txt);
						screenRolesMap.put(CPFConstants.OperationType.MODIFY,
								modifyRoles);
						}	
						
						CPFPlugin.getDefault().log("View UnSelected.........");
						if (viewRoles.indexOf(txt) != -1){
							CPFPlugin.getDefault()
									.log(
											"Removing role from View............"
													+ txt);
						viewRoles.remove(txt);
						}
					}
					  screenRolesMap.put(CPFConstants.OperationType.VIEW,
							viewRoles);
					  if(viewRoles.isEmpty()){
							screenRolesMap.remove(CPFConstants.OperationType.VIEW);
						}
					wizard.getCPFScreen().setMappedRoles(screenRolesMap);
					validatePage();
					getWizard().getContainer().updateButtons();
				}
			});

			   editor2.setEditor(viewAllow, item, 2);
			   
			   
			  modifyAllow.addListener(SWT.Selection, new Listener() {

				public void handleEvent(Event e) {

					Button bt = (Button) editor1.getEditor();
					String txt = editor1.getItem().getText(0);
					CPFPlugin.getDefault().log(
							"The table item selected is...."
									+ editor2.getItem()
									+ "The Role on item is..." + txt);
					if (bt.getSelection()) {
						    //enable view also
						if(viewRoles.indexOf(txt)==-1){
							((Button)editor2.getEditor()).setSelection(true);  //added now
							viewRoles.add(txt);
							screenRolesMap.put(CPFConstants.OperationType.VIEW,
									viewRoles);
						}
							
						CPFPlugin.getDefault().log("Modify Selected.........");
						if (modifyRoles.indexOf(txt) == -1){
							CPFPlugin.getDefault().log(
									"Adding role to modify..........." + txt);
						 modifyRoles.add(txt);
						}
					} else if (!bt.getSelection()) {
						CPFPlugin.getDefault()
								.log("Modify UnSelected.........");
						if (modifyRoles.indexOf(txt) != -1){
							CPFPlugin.getDefault().log(
									"Removing role from modify..........."
											+ txt);
						modifyRoles.remove(txt);
						}
					}
					
					screenRolesMap.put(CPFConstants.OperationType.MODIFY,
							modifyRoles);
					if(modifyRoles.isEmpty()){
						screenRolesMap.remove(CPFConstants.OperationType.MODIFY);
					}
					wizard.getCPFScreen().setMappedRoles(screenRolesMap);
					validatePage();
					getWizard().getContainer().updateButtons();
				}
			});
			 editor1.setEditor(modifyAllow, item, 3);
		}
		table.setVisible(true);
		
      }
	}
	
	
	public void createListAndDeleteRoleItems() {
		  roles = getRolesList();
		  screenRolesMap = new HashMap<CPFConstants.OperationType, java.util.List<String>>();
		  listRoles = new java.util.ArrayList<String>();
          deleteRoles = new java.util.ArrayList<String>();
    if(roles!=null){
		for (int j = 0; j < roles.length; j++) {
			// Create the rows
			final TableItem item = new TableItem(table, SWT.NULL);
			CPFPlugin.getDefault().log("Adding Item ......" + item,
					IStatus.INFO);

			CPFPlugin.getDefault().log(
					"item.getParent Display ............."
							+ item.getParent().getDisplay());

			// Create an editor object to use for text editing

			CPFPlugin.getDefault().log("Adding Role ......" + roles[j]);
			final TableEditor editor = new TableEditor(table);
			editor.grabHorizontal = true;
			roleLabel = new Label(table,SWT.NULL);

			roleLabel.setText(roles[j]);
			roleLabel.setToolTipText(roles[j]);
			item.setText(0, roles[j]);
			editor.setEditor(roleLabel, item, 0);

			// Create an editor object 
			final TableEditor editor2 = new TableEditor(table);
			editor2.grabHorizontal = true;
			listAllow = new Button(table, SWT.CHECK);
			
			//	 Create an editor object 
			final TableEditor editor1 = new TableEditor(table);
			editor1.grabHorizontal = true;
			deleteAllow = new Button(table, SWT.CHECK);
			
			
			//adding listeners to the editors
			
			listAllow.addListener(SWT.Selection, new Listener() {

				public void handleEvent(Event e) {
					Button bt = (Button) editor2.getEditor();
					String txt = editor2.getItem().getText(0);
					
					CPFPlugin.getDefault().log(
							"The table item selected is...."
									+ editor2.getItem()
									+ "The Role on item is..." + txt);
					if (bt.getSelection()) {
						CPFPlugin.getDefault().log("List Selected.........");
						if (listRoles.indexOf(txt) == -1){
							CPFPlugin.getDefault().log(
									"Adding role to List............" + txt);
						listRoles.add(txt);
						
						}
						
					} else if (!bt.getSelection()) {
						
//						//disable delete also
						if (deleteRoles.indexOf(txt) != -1){
						((Button) editor1.getEditor()).setSelection(false);
                        deleteRoles.remove(txt);
						screenRolesMap.put(CPFConstants.OperationType.DELETE,
									deleteRoles);
						}						
						CPFPlugin.getDefault().log("List UnSelected.........");
						if (listRoles.indexOf(txt) != -1){
							CPFPlugin.getDefault()
									.log(
											"Removing role from List............"
													+ txt);
						listRoles.remove(txt);
						}
					}
					screenRolesMap.put(CPFConstants.OperationType.LIST ,
								listRoles);
					
					if(listRoles.isEmpty()){
						screenRolesMap.remove(CPFConstants.OperationType.LIST);
					}
					wizard.getCPFScreen().setMappedRoles(screenRolesMap);
					validatePage();
					getWizard().getContainer().updateButtons();
				}
			});

			editor2.setEditor(listAllow, item, 1);	
			
			deleteAllow.addListener(SWT.Selection, new Listener() {

				public void handleEvent(Event e) {

					Button bt = (Button) editor1.getEditor();
					String txt = editor1.getItem().getText(0);
					CPFPlugin.getDefault().log(
							"The table item selected is...."
									+ editor2.getItem()
									+ "The Role on item is..." + txt);
					if (bt.getSelection()) {
						    
						    //enable list also
						if (listRoles.indexOf(txt) == -1){
							((Button)editor2.getEditor()).setSelection(true);  //added now
							listRoles.add(txt);
							screenRolesMap.put(CPFConstants.OperationType.LIST ,
									listRoles);
						}	
							
						CPFPlugin.getDefault().log("Delete Selected.........");
						if (deleteRoles.indexOf(txt) == -1){
							CPFPlugin.getDefault().log(
									"Adding role to delete..........." + txt);
						   deleteRoles.add(txt);
						}
					} else if (!bt.getSelection()) {
						CPFPlugin.getDefault()
								.log("Delete UnSelected.........");
						if (deleteRoles.indexOf(txt) != -1){
							CPFPlugin.getDefault().log(
									"Removing role from delete..........."
											+ txt);
						deleteRoles.remove(txt);
						}
					}
					screenRolesMap.put(CPFConstants.OperationType.DELETE ,
								deleteRoles);
				
					if(deleteRoles.isEmpty()){
						screenRolesMap.remove(CPFConstants.OperationType.DELETE);
					}
					wizard.getCPFScreen().setMappedRoles(screenRolesMap);
					validatePage();
					getWizard().getContainer().updateButtons();
				}
				
			});
			editor1.setEditor(deleteAllow, item, 2);	
			

		}
		table.setVisible(true);
		
      }
	}

	public boolean canFlipToNextPage() {

		return canFlipToNext;
	}
	
	
	private void validatePage(){
		 setErrorMessage(null); 
		if (this.projectName != null){
			
			     if(this.infList.size()==0){
				    
			    	canFlipToNext=false;
				    setErrorMessage("Select at least one of the Interface type for Provisioning Screen"); 
			     
			     }else if(this.infList.contains(CPFConstants.InterfaceType.PORTLET)){
					
			    	 if(fileName!=null&&!fileName.equals("")){
			           this.validateRoleMap();
					    
					 } else{
						 setErrorMessage("The Provisioning Screen Name can not be empty"); 
					}
					
				} else if(this.infList.contains(CPFConstants.InterfaceType.WEB_SERVICE)){
					 this.validateRoleMap();	
				}
			     
		}
		
		CPFPlugin.getDefault().log(
				"Flip to next page is ... " + canFlipToNext + "Interfcae list flag is"+infList
						+ "Project Name::" + projectName +"FileName is " +fileName +"Screen roles map is "+screenRolesMap);
	}
	
	
	private void validateRoleMap(){
		 if(screenRolesMap.isEmpty()){
				CPFPlugin.getDefault().log("Role Map is Empty..");
	    	 canFlipToNext=false;
	    	 setErrorMessage("None of the 'Role' is selected for Provisioning Screen"); 
	     }else {
	    	 for(java.util.List list:screenRolesMap.values()){
		    	 if(list.isEmpty()){
		    		 canFlipToNext=false; 
		    		 setErrorMessage("None of the 'Role' is selected for Provisioning Screen"); 
		    	 }else{
		    		 canFlipToNext=true;
		    		 setErrorMessage(null);
		    		 break;
		    	 }
		    } 
	     }
	}

	public String getLocationForJspFile() {
		return this.getWorkspaceRoot().getLocation().toString() + "/"
				+ this.getPackageFragmentRootText();
	}

	public boolean isShowListTrue() {
		return showListFlag;
	}

	public boolean iscreateAndUpdate() {
		return createAndUpdate;
	}

	public String getJSPfileName() {
		return fileName;
	}

	public IPath getJSPfileLocation() {
		return jspFileLocation;
	}

	public CPFConstants.ViewType getViewType() {
		return viewType;
	}

	public Map getScreenRolesMap() {
		return screenRolesMap;
	}

	public java.util.List getInterfaceTypeList() {
		return infList;
	}

	public CPFPortletPreference getPortletPrefernces() {
		return portletPreferences;
	}

	//	 Parses the string into seperate list items and adds them to the list.
	private String[] getRolesList() {
		String items = CPFPlugin.getDefault().getPreferenceStore().getString(
				PreferenceConstants.P_ROLES);
		String[] itemList=null;
	   if(getProjectHandle()!=null){
		IScopeContext projectScope = new ProjectScope(getProjectHandle());
		IEclipsePreferences projectNode = projectScope
				.getNode("com.genband.sas.maps");
		if (projectNode != null) {
			CPFPlugin.getDefault().log(
					"The project node in CPFPropertyPage to save properties is..... "
							+ projectNode);
			items = projectNode.get(PreferenceConstants.P_ROLES, CPFPlugin
					.getDefault().getPreferenceStore().getString(
							PreferenceConstants.P_ROLES));
			// do something with the value.
		}
		CPFPlugin.getDefault().log("The Roles list is.." + items);
		String[] itemArray = parseString(items);
		itemList = new String[itemArray.length];
		if (itemArray != null) {
			for (int i = 0; i < itemArray.length; i++) {

				String role = itemArray[i];
				String roleName = null;
				if (role.indexOf("(") != -1) {
					roleName = role.substring(0, role.indexOf("("));
				} else {
					roleName = itemArray[i];
				}
				itemList[i] = roleName;
			}

		}
		}
		//set roles list to pass it to the attributes property dialog page to the third page
		return itemList;
	}
	
	
	public  java.util.List<String> getRolesforList(){
		
       return this.listRoles;
	}
	
	public  java.util.List<String> getRolesforModify(){
		
			return this.modifyRoles;
	}
	
	public  java.util.List<String> getRolesforView(){
	    return this.viewRoles;
		
	}
	
	public  java.util.List<String> getRolesforDelete(){
	    return this.deleteRoles;
		
	}
	
	public  java.util.List<String> getRolesforCreate(){
	    return this.createRoles;
		
	}
	

	public String[] returnRolesList() {
		return roles;
	}

	/**
	 *  Parses the single String representation of the list
	 * into an array of list items.
	 */
	private String[] parseString(String stringList) {
		ArrayList v = new ArrayList();
		if (stringList != null) {
			StringTokenizer st = new StringTokenizer(stringList, ";"); //$NON-NLS-1$

			while (st.hasMoreElements()) {
				v.add(st.nextElement());
			}
		}
		return (String[]) v.toArray(new String[v.size()]);
	}

	boolean useDefaults = true;

	// initial value stores
	private String initialProjectFieldValue;

	private String initialLocationFieldValue;

	// the value the user has entered
	String customLocationFieldValue;

	CPFConstants.ViewType viewType = CPFConstants.ViewType.DETAILS_VIEW;

	Text projectNameField;

	Text jspLocationPathField;

	Label locationLabel;

	Button browseButton;

	boolean showListFlag = false;

	boolean createAndUpdate = true;

	Button listAndDel = null;

	Button create = null;

	java.util.List entityList;

	List selectedEntityList;

	protected String[] roles;

	CPFPortletPreference portletPreferences;

	Button advanced;

	Map<CPFConstants.OperationType, java.util.List<String>> screenRolesMap =null;

	java.util.List<CPFConstants.InterfaceType> infList = new java.util.ArrayList<CPFConstants.InterfaceType>();
	java.util.List<String> modifyRoles = null;

	java.util.List<String> viewRoles = null;

	java.util.List<String> createRoles =null;
	
	java.util.List<String> listRoles =null;
	
	java.util.List<String> deleteRoles =null;

	Group tablegroup;

	Label jspNameLabel;
	
	boolean canFlipToNext = false;

}