package com.baypackets.sas.ide.logger.actions;

import java.io.File;
import java.util.Vector;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.logger.dialogs.EditLoggerDialog;
import com.baypackets.sas.ide.logger.util.ImageUtils;
import com.baypackets.sas.ide.logger.views.SASDebugLoggerView;
import com.baypackets.sas.ide.logger.views.SASServerLoggerView;
import com.baypackets.sas.ide.logger.views.WatcherData;

/**
 * Edits the currently active Watcher.
 */
public class EditLoggerSettingsAction extends Action
{
	private SASServerLoggerView	m_view = null;
	private SASDebugLoggerView debug_view;
	private static ImageDescriptor IMAGE_DESC = null; 
	private static String SAS_LOGS="sasLogs";
	private static String DEBUG_LOGS="debugLogs";
	
	public EditLoggerSettingsAction(SASServerLoggerView p)
	{
		m_view = p;
		setText("Edit Watcher");
		setToolTipText("Edit CAS Server Logs Settings");
		setImageDescriptor(IMAGE_DESC);
	}
	
	public EditLoggerSettingsAction(SASDebugLoggerView p)
	{
		debug_view = p;
		setText("Edit CAS Debug Logger");
		setToolTipText("Edit Sip Debug Logs Settings");
		setImageDescriptor(IMAGE_DESC);
	}
	
	
	public void run() {
		
         WatcherData entry =null;
         if(m_view!=null){
        	 entry=m_view.getSelectedEntry();
         } else if(debug_view!=null){
        	 entry=debug_view.getSelectedEntry();
         }
         if (entry != null) {
			 int topIndex = entry.getViewer().getTopIndex();
			 int caret = entry.getViewer().getTextWidget().getCaretOffset();
			 EditLoggerDialog d=null;
			  if(m_view!=null){
				 d = new EditLoggerDialog(m_view.getComposite().getShell(),SAS_LOGS); 
			 }else if(debug_view!=null){
                 d = new EditLoggerDialog(debug_view.getComposite().getShell(),DEBUG_LOGS);
             }
            SasPlugin.getDefault().log("The WatcherData object returned for editing is,....."+entry);
             Vector tempFilters = new Vector();
            
             
             if(entry.getFilters()!=null)
             tempFilters.addAll(entry.getFilters());
             
             d.setFilters(tempFilters);
             d.setInterval(entry.getWatcher().getInterval());
             d.setNumLines(entry.getWatcher().getNumLines());
             if (d.open() == Window.OK) {
            	 if(m_view!=null){
                 m_view.editWatcher(entry,d.getInterval(), d.getNumLines(), d.getFilters());
            	 }else if(debug_view!=null){
            		 debug_view.editWatcher(entry,d.getInterval(), d.getNumLines(), d.getFilters());
            	 }
                 entry.getViewer().refresh();
				 entry.getViewer().setTopIndex(topIndex);
				 entry.getViewer().getTextWidget().setCaretOffset(caret);	
             }
         }
     }
	
	static {
		IMAGE_DESC = ImageUtils.createImageDescriptor("icons/edit.gif");
	}
}
