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

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.logger.filters.Filter;

/**
 * Show all previously saved filters and allow them to be added to the filter list
 */
public class AddSavedFilterDialog extends Dialog
{
    private List m_filterList;
    private java.util.List m_allFilters;
    private ArrayList m_filtersToAdd = new ArrayList();
    
    public AddSavedFilterDialog(Shell parentShell)
    {
        super(parentShell);
    }
    
    /**
	 * Override to set the title of the dialog.
	 */
	protected void configureShell(Shell shell)
	{
		super.configureShell(shell);
		shell.setText("Add Saved Filter");
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
		new Label(composite, SWT.NONE).setText("Add the selected filters");
		m_filterList = new List(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		gridData = new GridData(GridData.GRAB_HORIZONTAL);
		gridData.widthHint = 300;
		gridData.heightHint = 100;
		m_filterList.setLayoutData(gridData);
		
		m_allFilters = SasPlugin.getDefault().getSavedFilters();
		for (Iterator iter = m_allFilters.iterator(); iter.hasNext();) {
		    Filter filter = (Filter) iter.next();
		    
		    m_filterList.add(filter.getDescription());
		}
		
		return composite;
	}
	
	/**
	 * Add the filters
	 */
	protected void okPressed()
	{
	    int[] selections = m_filterList.getSelectionIndices();
	    for (int i = 0; i < selections.length; i++)
        {
            m_filtersToAdd.add(m_allFilters.get(selections[i]));
        }
	    super.okPressed();
	}
	
    public ArrayList getFiltersToAdd()
    {
        return m_filtersToAdd;
    }
}
