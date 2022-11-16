package com.genband.m5.maps.ide.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import com.genband.m5.maps.ide.CPFPlugin;
import com.genband.m5.maps.ide.properties.*;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */
public class CPFPreferenceLocalesPage extends CommonFieldEditorPage
	implements IWorkbenchPreferencePage {

	private LocalesFieldEditor list;
	public CPFPreferenceLocalesPage() {
		super(GRID);
		setPreferenceStore(CPFPlugin.getDefault().getPreferenceStore());
		setDescription("Define the Locales for Portal Project.");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors(){
		list = new LocalesFieldEditor(
				PreferenceConstants.P_LOCALES,
				"",
				getFieldEditorParent() ,isPropertyPage());

			addField(list);
			if(isPropertyPage()){
			  IProject con=(IProject)this.getElement();
			  list.setProjectHandle(con);
			}
		
	}
	
	protected String getPageId() {
        return "com.genband.m5.maps.ide.preferences.CPFPreferenceLocalesPage";
      }

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

}
