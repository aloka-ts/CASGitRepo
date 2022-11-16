/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.baypackets.sas.ide.logger.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.baypackets.sas.ide.logger.dialogs.FindDialog;
import com.baypackets.sas.ide.logger.util.ImageUtils;
import com.baypackets.sas.ide.logger.views.SASDebugLoggerView;
import com.baypackets.sas.ide.logger.views.SASServerLoggerView;
import com.baypackets.sas.ide.logger.views.WatcherData;

/**
 * Displays a Find dialog box, similar to the default Eclipse Find dialog.
 */
public class FindAction extends Action
{
	private SASServerLoggerView	m_view = null;
	private SASDebugLoggerView debug_view;
	private static ImageDescriptor IMAGE_DESC = null; 
	
	public FindAction(SASServerLoggerView p)
	{
		m_view = p;
		
		setText("Find...");
		setToolTipText("Find in log file");
		setImageDescriptor(IMAGE_DESC);
	}
	
	public FindAction(SASDebugLoggerView p)
	{
		debug_view = p;
		
		setText("Find...");
		setToolTipText("Find in log file");
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

			FindDialog d = new FindDialog(m_view.getComposite().getShell(), entry.getViewer().getFindReplaceTarget());
			d.open();
		}
	}
	
	static {
		IMAGE_DESC = ImageUtils.createImageDescriptor("icons/search.gif");
	}
}
