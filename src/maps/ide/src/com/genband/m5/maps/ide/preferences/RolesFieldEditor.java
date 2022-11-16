package com.genband.m5.maps.ide.preferences;

import java.util.*;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.genband.m5.maps.ide.CPFPlugin;

import org.osgi.service.prefs.BackingStoreException;

/**
 * A field editor for displaying and storing a list of strings. Buttons are
 * provided for adding items to the list and removing items from the list.
 */
public class RolesFieldEditor extends FieldEditor {
	private List roles;

	Text roleName;

	String rolName;

	Text roleDesc;

	String desc;

	java.util.List<String> selectedRoles;

	private java.util.List<Locale> locales = null;

	private IProject project = null;

	private boolean isPropertyPage;

	private static final String DEFAULT_SEPERATOR = ";";

	// The top-level control for the field editor.
	private Composite top;

	// The list of tags.
	private String seperator = DEFAULT_SEPERATOR;

	boolean isDefault = false;

	public RolesFieldEditor(String name, String labelText, Composite parent,
			boolean ispropertypg) {
		super(name, labelText, parent);
		this.isPropertyPage = ispropertypg;
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#adjustForNumColumns(int)
	 */
	protected void adjustForNumColumns(int numColumns) {
		((GridData) top.getLayoutData()).horizontalSpan = numColumns;
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid (Composite,
	 *      int)
	 */
	protected void doFillIntoGrid(Composite parent, int numColumns) {
		top = parent;

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = numColumns;
		top.setLayoutData(gd);

		Group group = new Group(top, GridData.FILL_HORIZONTAL);
		GridLayout layout1 = new GridLayout();
		layout1.numColumns = numColumns;
		group.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = numColumns;
		group.setLayoutData(gridD);
		group.setText("Define Roles:");
		group.setFont(parent.getFont());

		if (roles != null) {
			roles.removeAll();
		}

		selectedRoles = new java.util.ArrayList<String>();

		new Label(group, SWT.LEFT | SWT.WRAP).setText("Role Name:");
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		roleName = new Text(group, SWT.SINGLE | SWT.BORDER);
		roleName.setLayoutData(gridD);
		roleName.setTextLimit(80);
		roleName.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				rolName = roleName.getText();
			}
		});

		new Label(group, SWT.LEFT | SWT.WRAP).setText("Role Description:");
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		roleDesc = new Text(group, SWT.SINGLE | SWT.BORDER);
		roleDesc.setLayoutData(gridD);
		roleDesc.setTextLimit(80);
		roleDesc.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				desc = roleDesc.getText();
			}
		});

		Button add = new Button(group, SWT.PUSH);
		add.setText("Add");
		add.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (rolName != null) {
					if (desc != null && !desc.equals("")) {
						roles.add(rolName + "(" + desc + ")");
						selectedRoles.add(rolName + "(" + desc + ")");
					} else {
						roles.add(rolName);
						selectedRoles.add(rolName);
					}
                                        roleName.setText("");
					roleDesc.setText("");

				}
			}
		});

		group = new Group(top, GridData.FILL_HORIZONTAL);
		layout1 = new GridLayout();
		layout1.numColumns = numColumns;
		group.setLayout(layout1);
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = numColumns;
		group.setLayoutData(gridD);
		group.setText("Defined Roles:");
		group.setFont(parent.getFont());
		GridData gridData4 = new GridData();
		roles = new List(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		// gridData4.verticalSpan=2;
		gridData4.horizontalIndent = 10;
		gridData4.heightHint = 150;
		gridData4.widthHint = 145;
		roles.setLayoutData(gridData4);
		// this.loadLanguagesInList(languages);

		// GridData gr = new GridData(SWT.RIGHT);
		Button remove = new Button(group, SWT.PUSH);
		remove.setText("Remove");
		// addEntity.setLayoutData(gr);
		remove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {

				String[] role = roles.getSelection();
				if (roles.getSelection() != null) {
					for (int j = 0; j < role.length; j++) {

						if (!role[j].equals("NPA") && !role[j].equals("SPA")) {
							roles.remove(role[j]);
							selectedRoles.remove(role[j]);
							CPFPlugin.getDefault().log(
									"The selectedRoles list is......."
											+ selectedRoles.size());
						}

					}
				}
			}
		});

	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	protected void doLoad() {
		String items = getPreferenceStore().getString(getPreferenceName());
		CPFPlugin.getDefault().log(
				"The Roles from the preference store is......" + items);
		roles.removeAll();
		selectedRoles.clear();
		roles.add("NPA");
		roles.add("SPA");
		selectedRoles.add("NPA");
		selectedRoles.add("SPA");
		if (items != null && !items.equals("")) {
			if (this.isPropertyPage) {

				IScopeContext projectScope = new ProjectScope(
						getProjectHandle());
				IEclipsePreferences projectNode = projectScope
						.getNode("com.genband.sas.maps");
				if (projectNode != null) {
					CPFPlugin.getDefault().log(
							"The project node in CPFPropertyPage to save properties is.....PPPPPPP "
									+ projectNode);
					items = projectNode
							.get(PreferenceConstants.P_ROLES,
									getPreferenceStore().getString(
											getPreferenceName()));
					// do something with the value.
					try {
						projectNode.flush();
					} catch (BackingStoreException e) {
						CPFPlugin.getDefault().log(
								"The backing store exception in storing properties of project.."
										+ e);
					}
				}

			}

			setList(items);

		}
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault() {
		String items = getPreferenceStore().getDefaultString(
				getPreferenceName());
		isDefault = true;
		 roles.removeAll();
	    selectedRoles.clear();
		setList(items);
		CPFPlugin.getDefault().log("Load the default languages");
	}

	// Parses the string into seperate list items and adds them to the list.
	private void setList(String items) {
		String[] itemArray = parseString(items);

		if (itemArray != null || !itemArray.equals("")) {
			for (int i = 0; i < itemArray.length; i++) {
				selectedRoles.add(itemArray[i]);
				roles.add(itemArray[i]);

			}
		}
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	protected void doStore() {
		CPFPlugin.getDefault().log("Store the Roles");
		String s = createListString(selectedRoles);
		if (s != null && this.isPropertyPage) {

			IScopeContext projectScope = new ProjectScope(getProjectHandle());
			IEclipsePreferences projectNode = projectScope
					.getNode("com.genband.sas.maps");
			if (projectNode != null) {
				CPFPlugin.getDefault().log(
						"The project node in CPFPropertyPage to save properties is.....PPPPPPP "
								+ projectNode);
				projectNode.put(PreferenceConstants.P_ROLES, s);
				// do something with the value.
//				do something with the value.
				try {
					projectNode.flush();
				} catch (BackingStoreException e) {
					CPFPlugin.getDefault().log(
							"The backing store exception in storing properties of project.."
									+ e);
				}
			}

		} else {
			getPreferenceStore().setValue(getPreferenceName(), s);
		}
	}

	public void setProjectHandle(IProject container) {
		project = container;
	}

	public IProject getProjectHandle() {
		return project;
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	public int getNumberOfControls() {
		// The button composite and the text field.
		return 2;
	}

	/**
	 * Creates the single String representation of the list that is stored in
	 * the preference store.
	 */
	private String createListString(java.util.List items) {
		StringBuffer path = new StringBuffer("");//$NON-NLS-1$

		for (int i = 0; i < items.size(); i++) {
			path.append(items.get(i));
			path.append(seperator);
			CPFPlugin.getDefault().log(
					"The added roles are........." + items.get(i));
		}
		return path.toString();
	}

	/**
	 * Parses the single String representation of the list into an array of list
	 * items.
	 */
	private String[] parseString(String stringList) {
		StringTokenizer st = new StringTokenizer(stringList, seperator); //$NON-NLS-1$
		ArrayList v = new ArrayList();
		while (st.hasMoreElements()) {
			String str = (String) st.nextElement();
			if (!isDefault) {
				if (!str.equals("NPA") && !str.equals("SPA"))
					v.add(str);
			} else {
				v.add(str);
			}
		}
		return (String[]) v.toArray(new String[v.size()]);
	}

}
