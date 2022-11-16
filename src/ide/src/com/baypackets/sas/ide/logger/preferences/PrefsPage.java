package com.baypackets.sas.ide.logger.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.preference.FontFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import com.baypackets.sas.ide.logger.util.LoggingUtil;

import com.baypackets.sas.ide.SasPlugin;

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
public class PrefsPage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage
{
	// TODO: Put in a global constants class.
	public static final String DEBUG_LOG = "debuglog";
	public static final String LOG_LEVEL = "loglevel";
	private BooleanFieldEditor DebugLogEditor=null;
	RadioGroupFieldEditor LogLevelEditor=null;
	boolean isLoggingUpdated=false;
	

	public PrefsPage()
	{
		super(GRID);
		setPreferenceStore(SasPlugin.getDefault().getPreferenceStore());
		setDescription("CAS Server and Sip Debug Log Preferences:");
		initializeDefaults();
	}
	
	/**
	 * Sets the default values of the preferences.
	 */
	private void initializeDefaults()
	{
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(DEBUG_LOG,true);
		SasPlugin.getDefault().log("PareferencePage initializeDefaults()..Setting default log level as.."+"trace and debug log as"+SasPlugin.getDefault().getPreferenceStore().getBoolean(DEBUG_LOG));
		store.setDefault(LOG_LEVEL,"trace");
	
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors()
	{
		SasPlugin.getDefault().log("craete Filed Editors PareferencePage");
//		addField(new BooleanFieldEditor(SAVE_WATCHERS, 
//				 "&Restore previously open watchers on restart", getFieldEditorParent()));
		
		addField(new FontFieldEditor("logwatcherFont", "Log Font:", getFieldEditorParent()));
		
		DebugLogEditor=new BooleanFieldEditor(DEBUG_LOG, 
				 "&Enable CAS debug Log", getFieldEditorParent());
		addField(DebugLogEditor);
		
		LogLevelEditor=new RadioGroupFieldEditor(LOG_LEVEL,"Log Level",1,new String[][]{ {"ALARM" ,"alarm"},{"ERROR","error"},{"WARNING","warning"},{"TRACE","trace"},{"PRINT","print"}},getFieldEditorParent(),true);
		addField(LogLevelEditor);
		
	}
	
	public void init(IWorkbench workbench)
	{
	}
	
	
	public boolean performOk() {
		DebugLogEditor.store();
		LogLevelEditor.store();
		if(!isLoggingUpdated){
			LoggingUtil loggingUtil=new LoggingUtil();
		  loggingUtil.updateLoggingSettings();
		  isLoggingUpdated=true;
		}else{
			isLoggingUpdated=false;
		}
		return super.performOk();
	}

}