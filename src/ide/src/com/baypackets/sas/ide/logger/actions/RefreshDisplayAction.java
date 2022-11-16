package com.baypackets.sas.ide.logger.actions;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.baypackets.sas.ide.logger.util.ImageUtils;
import com.baypackets.sas.ide.logger.views.SASDebugLoggerView;
import com.baypackets.sas.ide.logger.views.SASServerLoggerView;
import com.baypackets.sas.ide.logger.views.WatcherData;
import com.baypackets.sas.ide.SasPlugin;
public class RefreshDisplayAction extends Action{
	private SASServerLoggerView	m_view = null;
	private SASDebugLoggerView debug_view;
	private static ImageDescriptor IMAGE_DESC = null; 
	private final String SERVER_STOPPED="*** Server is not running ***";
	private final String FILE_DELETED = "*** File does not exist ***";
	public RefreshDisplayAction(SASServerLoggerView p)
	{
		m_view = p;
		setText("Clear");
		setToolTipText("Refresh CAS Server logs display");
		setImageDescriptor(IMAGE_DESC);
	}
	
	public RefreshDisplayAction(SASDebugLoggerView p)
	{
		debug_view = p;
		setText("Clear");
		setToolTipText("Refresh Sip Debug Logs display");
		setImageDescriptor(IMAGE_DESC);
	}
	
	public void run() {
		WatcherData entry =null;
		
        if(m_view!=null){
        	entry=m_view.getSelectedEntry();
        	
        	String txt=entry.getViewer().getTextWidget().getText();
       //     if(txt.equals("")||txt.indexOf(this.FILE_DELETED)!=-1||txt.indexOf(this.SERVER_STOPPED)!=-1){
            	SasPlugin.getDefault().log("Loading CAS Logger state to refresh");	
       	        m_view.refreshView();
      //      }
            
        } else if(debug_view!=null){
        	entry=debug_view.getSelectedEntry();
        	String txt=entry.getViewer().getTextWidget().getText();
     //   	 if(txt.equals("")||txt.indexOf(this.FILE_DELETED)!=-1||txt.indexOf(this.SERVER_STOPPED)!=-1){
            	SasPlugin.getDefault().log("Loading CAS debug Logger state to refresh");	
       	        debug_view.refreshView();
    //        }
        }
        
		
	}
	
	static {
		IMAGE_DESC = ImageUtils.createImageDescriptor("icons/RefreshIcon.bmp");
	}

}
