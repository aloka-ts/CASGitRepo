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
public class DBFieldEditor extends FieldEditor {
	private List roles;

	Text roleName;

	String rolName;


	private IProject project = null;

	private boolean isPropertyPage;

	

	// The top-level control for the field editor.
	private Composite top;

	// The list of tags.

	boolean isDefault = false;

	public DBFieldEditor(String name, String labelText, Composite parent,
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
		group.setText("Define User Name:");
		group.setFont(parent.getFont());

		new Label(group, SWT.LEFT | SWT.WRAP).setText("User Name:");
		gridD = new GridData(GridData.FILL_HORIZONTAL);
		roleName = new Text(group, SWT.SINGLE | SWT.BORDER);
		roleName.setLayoutData(gridD);
		roleName.setTextLimit(80);
		roleName.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				rolName = roleName.getText();
			}
		});

	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	protected void doLoad() {
		String userName = getPreferenceStore().getString(getPreferenceName());
		CPFPlugin.getDefault().log(
				"The DB user name from the preference store is......" + userName);
		
		if (userName != null && !userName.equals("")) {
			if (this.isPropertyPage) {

				IScopeContext projectScope = new ProjectScope(
						getProjectHandle());
				IEclipsePreferences projectNode = projectScope
						.getNode("com.genband.sas.maps");
				if (projectNode != null) {
					CPFPlugin.getDefault().log(
							"The project node in CPFPropertyPage to save properties is.....PPPPPPP "
									+ projectNode);
					userName = projectNode
							.get(PreferenceConstants.P_DB_USER,
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

			roleName.setText(userName);

		}
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault() {
		String userName = getPreferenceStore().getDefaultString(
				getPreferenceName());
		isDefault = true;
		roleName.setText(userName);
		CPFPlugin.getDefault().log("Load the default languages");
	}

	
	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	protected void doStore() {
		CPFPlugin.getDefault().log("Store the DB User");
		String s=roleName.getText() ;
		if (s != null && this.isPropertyPage) {

			IScopeContext projectScope = new ProjectScope(getProjectHandle());
			IEclipsePreferences projectNode = projectScope
					.getNode("com.genband.sas.maps");
			if (projectNode != null) {
				CPFPlugin.getDefault().log(
						"The project node in CPFPropertyPage to save properties is.....PPPPPPP "
								+ projectNode);
				projectNode.put(PreferenceConstants.P_DB_USER, s);
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

	
}
