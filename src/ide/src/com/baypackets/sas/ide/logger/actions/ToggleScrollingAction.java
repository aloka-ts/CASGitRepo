package com.baypackets.sas.ide.logger.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.baypackets.sas.ide.logger.util.ImageUtils;
import com.baypackets.sas.ide.logger.views.SASDebugLoggerView;
import com.baypackets.sas.ide.logger.views.SASServerLoggerView;
import com.baypackets.sas.ide.logger.views.WatcherData;

/**
 * Toggles automatic scrolling.
 */
public class ToggleScrollingAction extends Action
{
	private SASServerLoggerView	m_view = null;
	private SASDebugLoggerView debug_view;
	private static ImageDescriptor IMAGE_DESC = null; 
	
	public ToggleScrollingAction(SASServerLoggerView p)
	{
		m_view = p;
		
		setText("Scroll Lock");
		setToolTipText("Scroll Lock");
		setImageDescriptor(IMAGE_DESC);
	}
	
	public ToggleScrollingAction(SASDebugLoggerView p)
	{
		debug_view = p;
		
		setText("Scroll Lock");
		setToolTipText("Scroll Lock");
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
			entry.setScroll(!isChecked());
		}
	}
	
	static {
		IMAGE_DESC = ImageUtils.createImageDescriptor("icons/toggle_scroll.gif");
	}
}
