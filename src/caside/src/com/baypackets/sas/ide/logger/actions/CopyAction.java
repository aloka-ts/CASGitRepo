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

import com.baypackets.sas.ide.logger.util.ImageUtils;
import com.baypackets.sas.ide.logger.views.SASDebugLoggerView;
import com.baypackets.sas.ide.logger.views.SASServerLoggerView;
import com.baypackets.sas.ide.logger.views.WatcherData;

/**
 * Copies current selection to clipboard.
 */
public class CopyAction extends Action
{
	private SASServerLoggerView	m_view = null;
	private SASDebugLoggerView debug_view;
	private static ImageDescriptor IMAGE_DESC = null; 
	
	public CopyAction(SASServerLoggerView p)
	{
		m_view = p;
		
		setText("Copy");
		setToolTipText("Copy selected text to the clipboard");
		setImageDescriptor(IMAGE_DESC);
	}
	public CopyAction(SASDebugLoggerView p)
	{
		debug_view = p;
		
		setText("Copy");
		setToolTipText("Copy selected text to the clipboard");
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
			entry.getViewer().getTextWidget().copy();
		}
	}
	
	static {
		IMAGE_DESC = ImageUtils.createImageDescriptor("icons/copy_edit.gif");
	}
}
