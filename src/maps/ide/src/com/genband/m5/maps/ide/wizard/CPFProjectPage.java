package com.genband.m5.maps.ide.wizard;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.StringTokenizer;

import java.io.InputStream;

import org.osgi.service.prefs.BackingStoreException;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import org.eclipse.swt.graphics.Image;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

import com.genband.m5.maps.common.CPFConstants;
import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.preferences.PreferenceConstants;

public class CPFProjectPage extends WizardPage {

	private CPFProjectWizard wizard;

	java.util.List<Locale> selectedLocales;

	Text projectName = null;

	String projName = null;

	CCombo naviCombo = null;

	String naviType = null;

	String projectLocation = null;

	Composite composite = null;

	private List languages;

	private Label image = null;

	int nColumns = 4;

	public Composite parentComposite = null;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public CPFProjectPage(CPFProjectWizard wizard) {
		super("FirstPage");
		setTitle("Create a new Portal Project");
		this.wizard = wizard;
		locales.add(Locale.UK);
		locales.add(Locale.US);
		locales.add(Locale.GERMANY);
		locales.add(Locale.JAPAN);
		locales.add(Locale.FRANCE);
		locales.add(Locale.CHINA);
		locales.add(Locale.CANADA);
		locales.add(Locale.ITALY);
		locales.add(Locale.KOREA);
		locales.add(Locale.TAIWAN);
	}

	private Listener locationModifyListener = new Listener() {
		public void handleEvent(Event e) {
			setPageComplete(validatePage());
			projectLocation = locationPathField.getText();
		}
	};

	private void sendErrorMessage(String message) {
		this.setErrorMessage(message);

	}

	// constants
	private static final int SIZING_TEXT_FIELD_WIDTH = 200;

	public void createControl(Composite parent) {

		CPFPlugin.getDefault().log("Creating control for CPFProjectWizard");
		parentComposite = parent;
		initializeDialogUnits(parent);
		composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());
	//	composite.getShell().setSize(400, 400);
		composite.getShell().setText("New CPF Project");

		// int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		Group group = new Group(composite, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = nColumns;
		group.setLayout(layout1);
		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		grid.horizontalSpan = nColumns;
		group.setLayoutData(grid);

		if (selectedLocales != null) {
			selectedLocales.clear();
		}
		if (selectedLocalesList != null) {
			selectedLocalesList.removeAll();
		}
		selectedLocales = new java.util.ArrayList<Locale>();

		new Label(group, SWT.LEFT | SWT.WRAP).setText("Project Name:");
		grid = new GridData(GridData.FILL_HORIZONTAL);
		projectName = new Text(group, SWT.SINGLE | SWT.BORDER);
		grid.grabExcessHorizontalSpace = true;
		grid.widthHint = this.SIZING_TEXT_FIELD_WIDTH;
		projectName.setLayoutData(grid);
		projectName.setTextLimit(80);
		projectName.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				projName = projectName.getText();
				setPageComplete(validate());
			}
		});

		createProjectLocationGroup(group);

		group = new Group(composite, GridData.FILL_HORIZONTAL);
		layout1 = new GridLayout();
		layout1.numColumns = nColumns;
		group.setLayout(layout1);
		grid = new GridData(GridData.FILL_HORIZONTAL);
		grid.horizontalSpan = nColumns;
		group.setLayoutData(grid);

		new Label(group, SWT.LEFT | SWT.WRAP).setText("Navigation Type:");
		grid = new GridData(GridData.FILL_HORIZONTAL);
		naviCombo = new CCombo(group, SWT.SINGLE | SWT.BORDER|SWT.READ_ONLY);
		grid.grabExcessHorizontalSpace = true;
		grid.widthHint = this.SIZING_TEXT_FIELD_WIDTH;
		naviCombo.setLayoutData(grid);
		naviCombo.setTextLimit(80);
		naviCombo.add(CPFConstants.NavigationType.NAVIGATION_TYPE_I.name());
//		naviCombo.add(CPFConstants.NavigationType.NAVIGATION_TYPE_II.name());
		naviCombo.select(0);
		navigationType = CPFConstants.NavigationType.NAVIGATION_TYPE_I;
		naviCombo.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				naviType = naviCombo.getText();

				if (naviType
						.equals(CPFConstants.NavigationType.NAVIGATION_TYPE_I
								.name())) {
					navigationType = CPFConstants.NavigationType.NAVIGATION_TYPE_I;

						Image img=CPFPlugin.getDefault().getImageRegistry().get(navigationType
								.name());
						image.setImage(img);
				}		
				CPFPlugin.getDefault().log(
						"The navigation type is..." + navigationType);
			}
		});

	//		Image img=CPFPlugin.getDefault().getImageRegistry().get("sample1.GIF");
		    Image img=CPFPlugin.getDefault().getImageRegistry().get(navigationType
					.name());
			image = new Label(group, SWT.RIGHT | SWT.BORDER);
			image.setImage(img);
			CPFPlugin.getDefault().log("The image is ready to view ........");

		group = new Group(composite, GridData.FILL_HORIZONTAL);
		layout1 = new GridLayout();
		layout1.numColumns = 5;
		group.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 5;
		group.setLayoutData(gridD);
		group.setText("Internationalization:");
		group.setFont(parent.getFont());

		GridData gridData4 = new GridData();
		languages = new List(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		gridData4.horizontalIndent = 15;
		gridData4.heightHint = 150;
		gridData4.widthHint = 130; //145
		languages.setLayoutData(gridData4);
		languages.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event e) {
			String[] lang = languages.getSelection();
			if (languages.getSelection() != null) {
				for (int j = 0; j < lang.length; j++) {
					for (int i = 0; i < locales.size(); i++) {
						Locale loc = (Locale) locales.get(i);
						if ((loc.getDisplayLanguage() + "("
								+ loc.getDisplayCountry() + ")")
								.equals(lang[j])) {
							selectedLocales.add(loc);
							selectedLocalesList.add(lang[j]);
							languages.remove(lang[j]);
							CPFPlugin.getDefault().log(
									"Add Locale......." + lang[j]);
							getWizard().getContainer().updateButtons();
						}
					}
				}
			}
			setPageComplete(validate());
		}
		});

		gridData4 = new GridData();
		Button add = new Button(group, SWT.PUSH);
		add.setText(">>");
		add.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				String[] lang = languages.getSelection();
				if (languages.getSelection() != null) {
					for (int j = 0; j < lang.length; j++) {
						for (int i = 0; i < locales.size(); i++) {
							Locale loc = (Locale) locales.get(i);
							if ((loc.getDisplayLanguage() + "("
									+ loc.getDisplayCountry() + ")")
									.equals(lang[j])) {
								selectedLocales.add(loc);
								selectedLocalesList.add(lang[j]);
								languages.remove(lang[j]);
								CPFPlugin.getDefault().log(
										"Add Locale......." + lang[j]);
								getWizard().getContainer().updateButtons();
							}
						}
					}
				}
				setPageComplete(validate());
			}
		});

		gridData4 = new GridData();
		Button remove = new Button(group, SWT.PUSH);
		remove.setText("<<");
		remove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (selectedLocalesList.getSelection() != null) {
					String[] lang = selectedLocalesList.getSelection();
					for (int j = 0; j < lang.length; j++)
						for (int i = 0; i < selectedLocales.size(); i++) {
							Locale loc = (Locale) selectedLocales.get(i);
							CPFPlugin.getDefault().log(
									"Locale name is..." + loc
											+ "Locale to remove is..."
											+ lang[j]);
							if ((loc.getDisplayLanguage() + "("
									+ loc.getDisplayCountry() + ")")
									.equals(lang[j])) {
								selectedLocales.remove(loc);
								selectedLocalesList.remove(lang[j]);
								languages.add(lang[j]);
								CPFPlugin.getDefault().log(
										"remove Locale........**************"
												+ loc
												+ "Size of Locale list now is "
												+ selectedLocales.size());
								getWizard().getContainer().updateButtons();
							}
						}
				}
				setPageComplete(validate());
			}
		});

		

		GridData g = new GridData();
		selectedLocalesList = new List(group, SWT.MULTI | SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		g.heightHint = 150;
		g.widthHint = 130;
		selectedLocalesList.setLayoutData(g);
		selectedLocalesList.setEnabled(true);
		selectedLocalesList.addListener(SWT.MouseDoubleClick, new Listener() {
			public void handleEvent(Event e) {
				if (selectedLocalesList.getSelection() != null) {
					String[] lang = selectedLocalesList.getSelection();
					for (int j = 0; j < lang.length; j++)
						for (int i = 0; i < selectedLocales.size(); i++) {
							Locale loc = (Locale) selectedLocales.get(i);
							CPFPlugin.getDefault().log(
									"Locale name is..." + loc
											+ "Locale to remove is..."
											+ lang[j]);
							if ((loc.getDisplayLanguage() + "("
									+ loc.getDisplayCountry() + ")")
									.equals(lang[j])) {
								selectedLocales.remove(loc);
								selectedLocalesList.remove(lang[j]);
								languages.add(lang[j]);
								CPFPlugin.getDefault().log(
										"remove Locale........**************"
												+ loc
												+ "Size of Locale list now is "
												+ selectedLocales.size());
								getWizard().getContainer().updateButtons();
							}
						}
				}
				setPageComplete(validate());
			}
		});

		Button defaultLang = new Button(group, SWT.PUSH);
		defaultLang.setText("Set as Default");
		defaultLang.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				String lang = selectedLocalesList.getSelection()[0];
				if (selectedLocalesList.getSelection() != null) {

					for (int k = 0; k < selectedLocalesList.getItemCount(); k++) {

						if (selectedLocalesList.getItem(k).indexOf("(Default)") != -1) {
							String def = selectedLocalesList.getItem(k);
							String undef = def.substring(0, def.lastIndexOf("("));
							selectedLocalesList.setItem(k, undef);
						}
					}

					int j = selectedLocalesList.indexOf(lang);
					selectedLocalesList.setItem(j, lang + "(Default)");

					for (int i = 0; i < locales.size(); i++) {
						Locale loc = (Locale) locales.get(i);
						if ((loc.getDisplayLanguage() + "("
								+ loc.getDisplayCountry() + ")").equals(lang)) {
							defaultLocale = loc;
							CPFPlugin.getDefault().log(
									"The default local is...." + loc);

						}
					}

				}
			}
		});

		group = new Group(composite, GridData.FILL_HORIZONTAL);
		layout1 = new GridLayout();
		layout1.numColumns = nColumns;
		group.setLayout(layout1);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = nColumns;
		group.setLayoutData(gridD);
		group.setFont(parent.getFont());

		GridData gd = new GridData(SWT.RIGHT);
		Button advanced = new Button(group, SWT.PUSH);
		advanced.setText("Advanced");
		advanced.setLayoutData(gd);
		advanced.addListener(SWT.Selection, new Listener() {
			
			DataModelImportDialog dialog = null;

			public void handleEvent(Event e) {

				if(dialog==null){
					CPFPlugin.getDefault().log("Data model Dialog is null ");
				     dialog = new DataModelImportDialog(composite
						.getShell(), projectLocation);

				  Composite com = (Composite) dialog.createDialogArea(composite);
				   dialog.create();
				}
				
				dialog.open();

				if (dialog.getReturnCode() == Window.OK) {
					jarFilesList = dialog.getjarFiles();

					CPFPlugin.getDefault().log(
							"The jarfiles are ................"
									+ jarFilesList.size());
				}

			}
		});

		this.loadLanguagesInList();
		setControl(composite);
		setPageComplete(false);
	    Dialog.applyDialogFont(composite);
	}

	/**
	 * Creates the project location specification controls.
	 * 
	 * @languages parent the parent composite
	 */
	private final void createProjectLocationGroup(Composite parent) {

		Font font = parent.getFont();

		GridData d = new GridData(SWT.FILL);
		d.horizontalSpan = nColumns;
		final Button useDefaultsButton = new Button(parent, SWT.CHECK
				| SWT.RIGHT);
		useDefaultsButton
				.setText(IDEWorkbenchMessages.ProjectLocationSelectionDialog_useDefaultLabel);
		useDefaultsButton.setSelection(useDefaults);
		useDefaultsButton.setEnabled(false);
		useDefaultsButton.setLayoutData(d);
		useDefaultsButton.setFont(font);

		GridData buttonData = new GridData();
		buttonData.horizontalSpan = nColumns;
		useDefaultsButton.setLayoutData(buttonData);

		createUserSpecifiedProjectLocationGroup(parent, !useDefaults);

		SelectionListener listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				useDefaults = useDefaultsButton.getSelection();
				browseButton.setEnabled(!useDefaults);
				locationPathField.setEnabled(!useDefaults);
				locationLabel.setEnabled(!useDefaults);
				customLocationFieldValue = locationPathField.getText();
				if (useDefaults) {
					setLocationForSelection();
				} else {
					locationPathField.setText(customLocationFieldValue);
				}
			}
		};
		useDefaultsButton.addSelectionListener(listener);
	}

	/**
	 * Creates the project location specification controls.
	 * 
	 * @languages projectGroup the parent composite
	 * @languages enabled the initial enabled state of the widgets created
	 */
	private void createUserSpecifiedProjectLocationGroup(Composite parent,
			boolean enabled) {

		Font font = parent.getFont();

		// location label
		locationLabel = new Label(parent, SWT.NONE);
		locationLabel
				.setText(IDEWorkbenchMessages.ProjectLocationSelectionDialog_locationLabel);
		locationLabel.setEnabled(enabled);
		locationLabel.setFont(font);

		// project location entry field
		locationPathField = new Text(parent, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		data.grabExcessHorizontalSpace = true;
		locationPathField.setLayoutData(data);
		locationPathField.setEnabled(enabled);
		locationPathField.setFont(font);

//		// browse button
//		browseButton = new Button(parent, SWT.PUSH);
//		browseButton
//				.setText("Browse");
//		browseButton.addSelectionListener(new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent event) {
//				handleLocationBrowseButtonPressed();
//			}
//		});
//
//		browseButton.setEnabled(enabled);
//		browseButton.setFont(font);
//		setButtonLayoutData(browseButton);

		// Set the initial value first before listener
		// to avoid handling an event during the creation.
		if (initialLocationFieldValue == null) {
			locationPathField.setText(Platform.getLocation().toOSString());
			projectLocation = Platform.getLocation().toOSString();
			CPFPlugin.getDefault().log(
					" The Project Default Location is" + projectLocation);
		} else {
			locationPathField.setText(initialLocationFieldValue);
		}
		locationPathField.addListener(SWT.Modify, locationModifyListener);
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
		return ResourcesPlugin.getWorkspace().getRoot().getProject(get());
	}

	/**
	 * Returns the current project name as entered by the user, or its
	 * anticipated initial value.
	 * 
	 * @return the project name, its anticipated initial value, or
	 *         <code>null</code> if no project name is known
	 */
	public String get() {

		return getFieldValue();
	}

	/**
	 * Returns the value of the project name field with leading and trailing
	 * spaces removed.
	 * 
	 * @return the project name in the field
	 */
	private String getFieldValue() {
		if (projectName == null)
			return ""; //$NON-NLS-1$

		return projectName.getText().trim();
	}

	/**
	 * Returns the value of the project location field with leading and trailing
	 * spaces removed.
	 * 
	 * @return the project location directory in the field
	 */
	private String getProjectLocationFieldValue() {
		if (locationPathField == null)
			return ""; //$NON-NLS-1$

		return locationPathField.getText().trim();
	}

	/**
	 * Open an appropriate directory browser
	 */
	void handleLocationBrowseButtonPressed() {
		DirectoryDialog dialog = new DirectoryDialog(locationPathField
				.getShell());
//		dialog
//				.setMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_directoryLabel);
		
		dialog
    	.setMessage("Select the Directory ");

		String dirName = getProjectLocationFieldValue();
		if (!dirName.equals("")) { //$NON-NLS-1$
			File path = new File(dirName);
			if (path.exists())
				dialog.setFilterPath(new Path(dirName).toOSString());
		}

		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			customLocationFieldValue = selectedDirectory;
			locationPathField.setText(customLocationFieldValue);
		}
	}

	/**
	 * Sets the initial project name that this page will use when created. The
	 * name is ignored if the createControl(Composite) method has already been
	 * called. Leading and trailing spaces in the name are ignored.
	 * 
	 * @languages name initial project name for this page
	 */
	public void setInitial(String name) {
		if (name == null)
			initialProjectFieldValue = null;
		else {
			initialProjectFieldValue = name.trim();
			initialLocationFieldValue = getDefaultLocationForName(initialProjectFieldValue);
		}
	}

	/**
	 * Set the location to the default location if we are set to useDefaults.
	 */
	void setLocationForSelection() {
		if (useDefaults)
			locationPathField
					.setText(getDefaultLocationForName(getFieldValue()));
	}

	/**
	 * Returns whether this page's controls currently all contain valid values.
	 * 
	 * @return <code>true</code> if all controls are valid, and
	 *         <code>false</code> if at least one is invalid
	 */
	protected boolean validatePage() {
		CPFPlugin.getDefault().log("Inside Validate Page..");
		IWorkspace workspace = IDEWorkbenchPlugin.getPluginWorkspace();
		setErrorMessage(null);
		
//		String projectFieldContents = getFieldValue();
//		if (projectFieldContents.equals("")) { //$NON-NLS-1$
//			setErrorMessage(null);
//			setMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectNameEmpty);
//			return false;
//		}
//
//		IStatus nameStatus = workspace.validateName(projectFieldContents,
//				IResource.PROJECT);
//		if (!nameStatus.isOK()) {
//			setErrorMessage(nameStatus.getMessage());
//			return false;
//		}

//		String locationFieldContents = getProjectLocationFieldValue();
//
//		if (locationFieldContents.equals("")) { //$NON-NLS-1$
//			setErrorMessage(null);
//			setMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectLocationEmpty);
//			return false;
//		}
//
//		IPath path = new Path(""); //$NON-NLS-1$
//		if (!path.isValidPath(locationFieldContents)) {
//			setErrorMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectLocationEmpty);
//			return false;
//		}
//
//		IPath projectPath = new Path(locationFieldContents);
//		if (!useDefaults && Platform.getLocation().isPrefixOf(projectPath)) {
//			setErrorMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectLocationEmpty);
//			return false;
//		}
		
		if(!this.validate()){
			return false;
		}
		String locationFieldContents = getProjectLocationFieldValue();
		IPath projectPath = new Path(locationFieldContents);
		if(this.projName!=null&&!this.projName.equals("")){
		IProject handle = getProjectHandle();
//		if (handle.exists()) {
//			setErrorMessage(IDEWorkbenchMessages.WizardNewProjectCreationPage_projectExistsMessage);
//			return false;
//		}

		/*
		 * If not using the default value validate the location.
		 */
		if (!useDefaults()) {
			IStatus locationStatus = workspace.validateProjectLocation(handle,
					projectPath);
			if (!locationStatus.isOK()) {
				if(locationStatus.getMessage().indexOf("overlaps")==-1){
				setErrorMessage("The Project Location is not valid"); //$NON-NLS-1$
				    return false;
				}else{
					setErrorMessage(null); 	
					return true;
				}
			}
		}
	   }
//		setErrorMessage(null);
//		setMessage(null);
		return true;
	}

	/**
	 * Get the defualt location for the provided name.
	 * 
	 * @languages nameValue the name
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

	public void loadLanguagesInList() {
		String items = CPFPlugin.getDefault().getPreferenceStore().getString(
				PreferenceConstants.P_LOCALES);
		CPFPlugin.getDefault().log(
				"The the locales list from store is.." + items);

		if (items != null && !items.equals("")) {
			CPFPlugin.getDefault().log(
					"Setting list.........of locales." + items);
			setSelectedLanguagesList(items);
		}

		CPFPlugin.getDefault().log(
				"Selected locales list size is.........of locales." +selectedLocales.size() );
		CPFPlugin.getDefault().log(
				"Locales list size is.........of locales." +locales.size() );
	
		
	
		     locales.removeAll(selectedLocales);
			 for (int i = 0; i < locales.size(); i++) {
				 Locale loc=locales.get(i);
			 
			       String lang = loc.getDisplayLanguage();
			       String count = loc.getDisplayCountry();
			       languages.add(lang + "(" + count + ")");
			       CPFPlugin.getDefault().log("The Local lang added is...." + lang);
			 }
			 locales.addAll(selectedLocales);

		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean canFlipToNextPage() {
		return false;
	}

//	public String getLocationForJspFile() {
//		return ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() + "/"
//				+ this.getPackageFragmentRootText();
//	}

	public java.util.List getSelectedLanguages() {
		return selectedLocales;
	}

	public String getProjectName() {
		return projName;
	}

	boolean useDefaults = true;

	// initial value stores
	private String initialProjectFieldValue;

	private String initialLocationFieldValue;

	// the value the user has entered
	String customLocationFieldValue;

	Text Field;

	Text locationPathField;

	Label locationLabel;

	Button browseButton;

	Text account = null;

	java.util.List entityList;

	List selectedLocalesList;

	java.util.List jarFilesList = null;

	/**
	 * @uml.property name="dataModel"
	 */
	private java.util.List<IFile> dataModel;

	/**
	 * @uml.property name="defaultLocale"
	 */
	private Locale defaultLocale = Locale.US;

	/**
	 * @uml.property name="locales"
	 */
	private java.util.List<Locale> locales = new java.util.ArrayList<Locale>();

	private java.util.List<Locale> localesSupported = new java.util.ArrayList<Locale>();

	/**
	 * @uml.property name="name"
	 */
	private String name = "";

	/**
	 * @uml.property name="navigationType"
	 */
	private CPFConstants.NavigationType navigationType = CPFConstants.NavigationType.NAVIGATION_TYPE_I;

	/**
	 * Getter of the property <tt>dataModel</tt>
	 * 
	 * @return Returns the dataModel.
	 * @uml.property name="dataModel"
	 */
	public java.util.List<IFile> getDataModel() {
		return dataModel;
	}

	/**
	 * Getter of the property <tt>defaultLocale</tt>
	 * 
	 * @return Returns the defaultLocale.
	 * @uml.property name="defaultLocale"
	 */
	public Locale getDefaultLocale() {
		return this.defaultLocale;
	}

	/**
	 * Getter of the property <tt>locales</tt>
	 * 
	 * @return Returns the locales.
	 * @uml.property name="locales"
	 */
	public java.util.List<Locale> getLocales() {
		return locales;
	}

	/**
	 * Getter of the property <tt>name</tt>
	 * 
	 * @return Returns the name.
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * Getter of the property <tt>navigationType</tt>
	 * 
	 * @return Returns the navigationType.
	 * @uml.property name="navigationType"
	 */
	public CPFConstants.NavigationType getNavigationType() {
		return navigationType;
	}

	/**
	 * Setter of the property <tt>dataModel</tt>
	 * 
	 * @param dataModel
	 *            The dataModel to set.
	 * @uml.property name="dataModel"
	 */
	public void setDataModel(java.util.List<IFile> dataModel) {
		this.dataModel = dataModel;
	}

	/**
	 * Setter of the property <tt>defaultLocale</tt>
	 * 
	 * @param defaultLocale
	 *            The defaultLocale to set.
	 * @uml.property name="defaultLocale"
	 */
	public void setDefaultLocale(Locale defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	/**
	 * Setter of the property <tt>locales</tt>
	 * 
	 * @param locales
	 *            The locales to set.
	 * @uml.property name="locales"
	 */
	public void setLocales(java.util.List<Locale> locales) {
		this.locales = locales;
	}

	/**
	 * Setter of the property <tt>name</tt>
	 * 
	 * @param name
	 *            The name to set.
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Setter of the property <tt>navigationType</tt>
	 * 
	 * @param navigationType
	 *            The navigationType to set.
	 * @uml.property name="navigationType"
	 */
	public void setNavigationType(CPFConstants.NavigationType navigationType) {
		this.navigationType = navigationType;
	}

	public IPath getLocationPath() {
		IPath path = new Path(projectLocation);
		return path;
	}

	public java.util.List getJarFiles() {
		return jarFilesList;
	}

	// Parses the string into seperate list items and adds them to the list.
	private void setSelectedLanguagesList(String items) {
		String[] itemArray = parseString(items);
		String[] itemList = new String[itemArray.length];
		if (itemArray != null) {
			for (int i = 0; i < itemArray.length; i++) {
				int j = itemArray[i].indexOf(":");
				String lang = itemArray[i].substring(0, j);
				String country = itemArray[i].substring(j + 1, itemArray[i]
						.length());
				itemList[i] = lang + "(" + country + ")";

				for (int k = 0; k < locales.size(); k++) {
					if (locales.get(k).getDisplayCountry().equals(country)
							&& locales.get(k).getDisplayLanguage().equals(lang)) {
						selectedLocales.add(locales.get(k));
						selectedLocalesList.add(lang + "(" + country + ")");
						CPFPlugin.getDefault().log(
								"The language in the list was " + lang
										+ "the country in the list was "
										+ country);
					}
				}

			}
		}

	}

	public void storeProperties() {
		CPFPlugin.getDefault().log("Store the languages");
		if (this.getDefaultLocale() != null) {
			int i = selectedLocales.indexOf(this.getDefaultLocale());
			selectedLocales.remove(i);
			selectedLocales.add(0, this.getDefaultLocale());
		}

		String s = createListString(selectedLocales);
		if (s != null) {

			IScopeContext projectScope = new ProjectScope(getProjectHandle());
			IEclipsePreferences projectNode = projectScope
					.getNode("com.genband.sas.maps");
			if (projectNode != null) {
				CPFPlugin.getDefault().log(
						"The project node in CPFPropertyPage to save properties is.....PPPPPPP "
								+ projectNode);
				projectNode.put(PreferenceConstants.P_LOCALES, s);
				try {
					projectNode.flush();
				} catch (BackingStoreException e) {
					CPFPlugin.getDefault().log(
							"The backing store exception in storing properties of project.."
									+ e);
				}
				// do something with the value.
			}

		}

	}

	private String createListString(java.util.List<Locale> items) {
		StringBuffer path = new StringBuffer("");//$NON-NLS-1$

		for (int i = 0; i < items.size(); i++) {
			path.append(items.get(i).getDisplayLanguage() + ":"
					+ items.get(i).getDisplayCountry());
			path.append(";");
			CPFPlugin.getDefault().log(
					"The local added is....."
							+ items.get(i).getDisplayLanguage() + ":"
							+ items.get(i).getDisplayCountry());

		}
		
		  localsCodeList=new ArrayList<String>();
		
		  for (int i = 0; i < items.size(); i++) {
			String code=items.get(i).getLanguage() + "_"+ items.get(i).getCountry();
			localsCodeList.add(code);
			CPFPlugin.getDefault().log(
					"Add local codes for Resource bundle"+ code);
		}
		
		
		return path.toString();
	}

	/**
	 * Parses the single String representation of the list into an array of list
	 * items.
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
	
	
	/**
	 * Return true, if the file name entered in this page is valid.
	 */
	private boolean validate() {
		boolean value=true;
		  setErrorMessage(null);
		 if (this.projName==null||this.projName.equals("")) {
			 setErrorMessage("The 'Project' name can not be null"); 
		    value=false;
		 }
		 if(selectedLocalesList.getItemCount()==0){
			 setErrorMessage("The 'Locales' List is empty"); 
			 value=false; 
		 }
		 CPFPlugin.getDefault().log("Validating............value is"+value);
		return value; 
	}
	
	
	public java.util.List getLocalesCodes(){
		return localsCodeList;
	}
	
	java.util.List localsCodeList=null;
}
