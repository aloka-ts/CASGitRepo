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
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.baypackets.sas.ide.logger.filters.AddTaskAction;

public class AddTaskOptionsPage extends WizardPage
{
	private Text		m_descText		= null;
	private Combo		m_priorityCombo	= null;

	  /** 
	 * Constructor for AddTaskOptionsPage.
	 * @param pageName
	 */
	public AddTaskOptionsPage(String pageName)
	{
		super(pageName);
	}

	/**
	 * Constructor for AddTaskOptionsPage.
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public AddTaskOptionsPage(String pageName, String title, ImageDescriptor titleImage)
	{
		super(pageName, title, titleImage);
	}

	public String getDescription()
	{
		return m_descText.getText();	
	}

	public int getPriority()
	{
		switch (m_priorityCombo.getSelectionIndex()) {
			case 0:
				return AddTaskAction.HIGH;
			case 1:
				return AddTaskAction.NORMAL;
			case 2:
				return AddTaskAction.LOW;
			default:
				return AddTaskAction.NORMAL;
		}
	}

	public void createControl(Composite parent) 
	{
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		
		GridData gridData;
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 20;
		composite.setLayout(layout);
	
		//
		// First row
		//
		new Label(composite, SWT.NONE).setText("Description:");
		m_descText = new Text(composite, SWT.BORDER);
		m_descText.setTextLimit(200);
		gridData = new GridData();
		gridData.widthHint = 200;
		m_descText.setLayoutData(gridData);
		m_descText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e)
			{
				setPageComplete(validatePage());
			}
		});
		
		//
		// Second row
		//
		new Label(composite, SWT.NONE).setText("Priority:");
		m_priorityCombo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		m_priorityCombo.add("High");
		m_priorityCombo.add("Normal");
		m_priorityCombo.add("Low");
		m_priorityCombo.select(1);
		
		
		setDescription("Choose the description and priority of the todo task to be created.");
		setPageComplete(false);
	}


	protected boolean validatePage()
	{
		return (m_descText.getText().length() > 0);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.IDialogPage#getTitle()
	 */
	public String getTitle()
	{
		return "Add Todo Task";
	}

}
