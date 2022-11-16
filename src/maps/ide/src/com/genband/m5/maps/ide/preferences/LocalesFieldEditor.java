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
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;

import com.genband.m5.maps.ide.CPFPlugin;

/**
 * A field editor for displaying and storing a list of strings. Buttons are
 * provided for adding items to the list and removing items from the list.
 */
public class LocalesFieldEditor extends FieldEditor {
	private List languages;

	List selectedLocalesList;

	java.util.List<Locale> selectedLocales;

	private java.util.List<Locale> locales = null;

	private IProject project = null;

	private static final String DEFAULT_SEPERATOR = ";";

	private boolean isPropertyPage;

	// The top-level control for the field editor.
	private Composite top;

	// The list of tags.
	private String seperator = DEFAULT_SEPERATOR;

	public LocalesFieldEditor(String name, String labelText, Composite parent,
			boolean ispropertyPg) {
		super(name, labelText, parent);
		isPropertyPage = ispropertyPg;
		locales = new java.util.ArrayList<Locale>();
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


		CPFPlugin.getDefault().log(
				"The isPropertypage is..........." + isPropertyPage);
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
		layout1.numColumns = 4;
		group.setLayout(layout1);
		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		group.setLayoutData(gridD);
		group.setText("Select Locales:");
		group.setFont(parent.getFont());

		if (selectedLocales != null) {
			selectedLocales.clear();
		}
		if (selectedLocalesList != null) {
			selectedLocalesList.removeAll();
		}
		selectedLocales = new java.util.ArrayList<Locale>();

		GridData gridData4 = new GridData();
		languages = new List(group, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		gridData4.horizontalIndent = 10;
		gridData4.heightHint = 150;
		gridData4.widthHint = 145;
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
									"Add lang......." + lang[j]);
						}
					}
				}
			}
		}
		});
	
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
								System.out
										.println("Add lang........" + lang[j]);
							}
						}
					}
				}
			}
		});

		Button remove = new Button(group, SWT.PUSH);
		remove.setText("<<");
		remove.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (selectedLocalesList.getSelection() != null
						&& selectedLocalesList.getItemCount() > 1) {
					String[] lang = selectedLocalesList.getSelection();
					for (int j = 0; j < lang.length; j++)
						for (int i = 0; i < selectedLocales.size(); i++) {
							Locale loc = (Locale) selectedLocales.get(i);
							CPFPlugin.getDefault().log("Locale name is..." + loc
									+ "Entity to remove is..." + lang[j]);
							if ((loc.getDisplayLanguage() + "("
									+ loc.getDisplayCountry() + ")")
									.equals(lang[j])) {
								selectedLocales.remove(loc);
								selectedLocalesList.remove(lang[j]);
								languages.add(lang[j]);
								CPFPlugin.getDefault().log("remove attribute........**************"
												+ loc
												+ "Size of Entity list now is "
												+ selectedLocales.size());
							}
						}
				}
			}
		});

		GridData g = new GridData();
		selectedLocalesList = new List(group, SWT.MULTI | SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		// g.verticalSpan = 1;
		g.heightHint = 150;
		g.widthHint = 145;
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
		if (isPropertyPage) {
			IScopeContext projectScope = new ProjectScope(getProjectHandle());
			IEclipsePreferences projectNode = projectScope
					.getNode("com.genband.sas.maps");
			if (projectNode != null) {
				items = projectNode.get(PreferenceConstants.P_LOCALES,
						getPreferenceStore().getString(getPreferenceName()));
				// do something with the value.
			}
			CPFPlugin.getDefault().log(
					"The ProjectHandle for the property page is...."
							+ getProjectHandle().getName()
							+ " and project node is" + projectNode);

		}
		CPFPlugin.getDefault().log(
				"The locals from the preference store is......" + items);
		setList(items);
		this.loadLanguagesInList();

	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doLoadDefault()
	 */
	protected void doLoadDefault() {
		String items =getPreferenceStore().getDefaultString(getPreferenceName());
		 setList(items);
		CPFPlugin.getDefault().log("Load the default languages");
	}

	// Parses the string into seperate list items and adds them to the list.
	private void setList(String items) {
		String[] itemArray = parseString(items);
		String[] itemList = new String[itemArray.length];

		selectedLocales.clear();
		selectedLocalesList.removeAll();
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

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#doStore()
	 */
	protected void doStore() {
		CPFPlugin.getDefault().log("Store the languages");
		String s = createListString(selectedLocales);
		if (s != null && this.isPropertyPage) {

			IScopeContext projectScope = new ProjectScope(getProjectHandle());
			IEclipsePreferences projectNode = projectScope
					.getNode("com.genband.sas.maps");
			if (projectNode != null) {
				CPFPlugin.getDefault().log(
						"The project node in CPFPropertyPage to save properties is.....PPPPPPP "
								+ projectNode);
				projectNode.put(PreferenceConstants.P_LOCALES, s);
				// do something with the value.
			}

		} else {
			getPreferenceStore().setValue(getPreferenceName(), s);
		}
	}

	/**
	 * @see org.eclipse.jface.preference.FieldEditor#getNumberOfControls()
	 */
	public int getNumberOfControls() {
		// The button composite and the text field.
		return 2;
	}

	public void setProjectHandle(IProject container) {
		project = container;
	}

	public IProject getProjectHandle() {
		return project;
	}

	/**
	 * Creates the single String representation of the list that is stored in
	 * the preference store.
	 */
	private String createListString(java.util.List<Locale> items) {
		StringBuffer path = new StringBuffer("");//$NON-NLS-1$

		for (int i = 0; i < items.size(); i++) {
			path.append(items.get(i).getDisplayLanguage() + ":"
					+ items.get(i).getDisplayCountry());
			path.append(seperator);
			CPFPlugin.getDefault().log(
					"The local added is....."
							+ items.get(i).getDisplayLanguage() + ":"
							+ items.get(i).getDisplayCountry());

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
			StringTokenizer st = new StringTokenizer(stringList, seperator); //$NON-NLS-1$

			while (st.hasMoreElements()) {
				v.add(st.nextElement());
			}
		}
		return (String[]) v.toArray(new String[v.size()]);
	}

	

	public void loadLanguagesInList() {
		
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
}
