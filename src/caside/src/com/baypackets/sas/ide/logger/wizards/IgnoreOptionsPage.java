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
package com.baypackets.sas.ide.logger.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * The options page for the "Ignore" filter action.
 */
public class IgnoreOptionsPage extends WizardPage
{
	/**
	 * Constructor for IgnoreOptionsPage.
	 * @param pageName
	 */
	public IgnoreOptionsPage(String pageName)
	{
		super(pageName);
	}

	/**
	 * Constructor for IgnoreOptionsPage.
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public IgnoreOptionsPage(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
	}

	public void createControl(Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		
		GridData gridData;
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 20;
		composite.setLayout(layout);
	
		new Label(composite, SWT.NONE).setText("No options available");
		
		setPageComplete(true);
	}

    public IWizardPage getNextPage()
    {
        return null;
    }

	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#getTitle()
	 */
	public String getTitle()
	{
		return "There are no options for this action.";
	}

}
