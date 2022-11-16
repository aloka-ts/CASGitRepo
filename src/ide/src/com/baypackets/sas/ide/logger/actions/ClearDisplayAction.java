package com.baypackets.sas.ide.logger.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;

import com.baypackets.sas.ide.logger.util.ImageUtils;
import com.baypackets.sas.ide.logger.views.SASDebugLoggerView;
import com.baypackets.sas.ide.logger.views.SASServerLoggerView;
import com.baypackets.sas.ide.logger.views.WatcherData;

/**
 * Clears the text area dislaying the log file.
 */
public class ClearDisplayAction extends Action
{
	private SASServerLoggerView	m_view = null;
	private SASDebugLoggerView debug_view;
	private static ImageDescriptor IMAGE_DESC = null; 
	
	public ClearDisplayAction(SASServerLoggerView p)
	{
		m_view = p;
		setText("Clear");
		setToolTipText("Clear SAS Server logs display");
		setImageDescriptor(IMAGE_DESC);
	}
	
	public ClearDisplayAction(SASDebugLoggerView p)
	{
		debug_view = p;
		setText("Clear");
		setToolTipText("Clear Sip Debug Logs display");
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
			entry.getWatcher().clear();
            entry.getViewer().setDocument(new Document(""));
		}	
	}
	
	static {
		IMAGE_DESC = ImageUtils.createImageDescriptor("icons/clear.gif");
	}
}
