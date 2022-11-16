package com.genband.m5.maps.ide.preferences;

import org.osgi.service.prefs.BackingStoreException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Combo;

import com.genband.m5.maps.ide.CPFPlugin;

public class ServerFieldEditor extends FieldEditor {
	private List languages;

	private Combo servers;

	String serverName = null;

	private static final String DEFAULT_SEPERATOR = ";";

	private IProject project = null;

	private boolean isPropertyPage;

	// The top-level control for the field editor.
	private Composite top;

	// The list of tags.
	private String seperator = DEFAULT_SEPERATOR;

	public ServerFieldEditor(String name, String labelText, Composite parent,
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
	 * @see org.eclipse.jface.preference.FieldEditor#doFillIntoGrid
	 * (Composite, int)
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

		new Label(group, SWT.LEFT | SWT.WRAP)
				.setText("Supported Application Server:");
		gd = new GridData(GridData.FILL_HORIZONTAL);
		servers = new Combo(group, SWT.SINGLE | SWT.BORDER);
		servers.setLayoutData(gd);
		/*	    servers.addListener(SWT.Modify, new Listener() {
		 public void handleEvent(Event e) {
		 serverName = servers.getText();
		 
		 
		 }
		 });*/

		CPFPlugin.getDefault().log("The Grid Has been filled");

	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoad()
	 */
	protected void doLoad() {
		String items = getPreferenceStore().getString(getPreferenceName());
		if (isPropertyPage) {
			IScopeContext projectScope = new ProjectScope(getProjectHandle());
			IEclipsePreferences projectNode = projectScope
					.getNode("com.genband.sas.maps");
			if (projectNode != null) {
				items = projectNode.get(PreferenceConstants.P_SERVER,
						getPreferenceStore().getString(getPreferenceName()));
				//do something with the value.
			}
			CPFPlugin.getDefault().log(
					"The ProjectHandle for the property page is..HHHHHHH"
							+ getProjectHandle().getName()
							+ " and project node is" + projectNode);

		}
		CPFPlugin.getDefault().log(
				"The Server from the preference store is......" + items);
		setList(items);
		CPFPlugin.getDefault().log("Load the servers");
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault() {
		String items = getPreferenceStore().getString(getPreferenceName());
		CPFPlugin.getDefault().log(
				"The Server from the preference store is......" + items);
		setList(items);
		CPFPlugin.getDefault().log("Load the servers");
	}

	// Parses the string into seperate list items and adds them to the list.
	private void setList(String items) {
		String[] itemArray = parseString(items);
		String[] itemList = new String[itemArray.length];
		if (itemArray != null) {
			for (int i = 0; i < itemArray.length; i++) {
				CPFPlugin.getDefault().log(
						"Adding server ...." + itemArray[i] + " to" + servers);
				servers.add(itemArray[i]);

			}
			servers.select(0);
			servers.setEnabled(false);
		}

	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	protected void doStore() {
		CPFPlugin.getDefault().log("Store the Server type");
		String s = createListString(servers.getItems());
		if (s != null && this.isPropertyPage) {

			IScopeContext projectScope = new ProjectScope(getProjectHandle());
			IEclipsePreferences projectNode = projectScope
					.getNode("com.genband.sas.maps");
			if (projectNode != null) {
				CPFPlugin.getDefault().log(
						"The project node in CPFPropertyPage to save properties is.....PPPPPPP "
								+ projectNode);
				projectNode.put(PreferenceConstants.P_SERVER, s);
				//do something with the value.
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
	 *  Creates the single String representation of the list
	 * that is stored in the preference store.
	 */
	private String createListString(String[] items) {
		StringBuffer path = new StringBuffer("");//$NON-NLS-1$

		for (int i = 0; i < items.length; i++) {
			path.append(items[i]);
			path.append(seperator);
			CPFPlugin.getDefault().log("The server added is" + items[i]);

		}
		return path.toString();
	}

	/**
	 *  Parses the single String representation of the list
	 * into an array of list items.
	 */
	private String[] parseString(String stringList) {
		ArrayList v = new ArrayList();
		if (stringList != null) {
			StringTokenizer st = new StringTokenizer(stringList, seperator); //$NON-NLS-1$

			while (st.hasMoreElements()) {
				v.add(st.nextElement());
			}
		}
		return (String[]) v.toArray(new String[v.size()]);
	}

}
