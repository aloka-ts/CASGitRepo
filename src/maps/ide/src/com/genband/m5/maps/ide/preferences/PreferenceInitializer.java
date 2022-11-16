package com.genband.m5.maps.ide.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.genband.m5.maps.ide.CPFPlugin;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = CPFPlugin.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_ROLES, "NPA;SPA");
		store.setDefault(PreferenceConstants.P_SERVER, "JBoss AS 4.2;");
		store.setDefault(PreferenceConstants.P_LOCALES,
				"English:United States;");
		store.setDefault(PreferenceConstants.OPERATION_ID,0);
	}

}
