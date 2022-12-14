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
package com.baypackets.sas.ide.logger.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.baypackets.sas.ide.logger.filters.Filter;

/**
 * Allow a filter to saved.
 */
public class SaveFilterDialog extends Dialog
{
    Filter m_filter;
    Text m_text;
    
    public SaveFilterDialog(Shell parentShell, Filter filter)
    {
        super(parentShell);
        m_filter = filter;
    }
    
    /**
	 * Override to set the title of the dialog.
	 */
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setText("Save Filter");
	}
	
	/**
	 * Create and layout the SWT controls for the dialog
	 */
	protected Control createDialogArea(Composite parent)
	{
		Composite composite = (Composite) super.createDialogArea(parent);
		GridData gridData;
	
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		layout.marginWidth = 10;
		composite.setLayout(layout);
	
		//
		// Find text row
		//
		new Label(composite, SWT.NONE).setText("Enter a description:");
		m_text = new Text(composite, SWT.BORDER);
		gridData = new GridData();
		gridData.widthHint = 200;
		m_text.setLayoutData(gridData);
		m_text.setText(m_filter.getDescription());
		
		return composite;
	}
	
	/**
	 * Add the filters
	 */
	protected void okPressed()
	{
	    m_filter.setDescription(m_text.getText());
	    super.okPressed();
	}
   
}
