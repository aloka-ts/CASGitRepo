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
package com.baypackets.sas.ide.editor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.w3c.dom.Node;

import com.baypackets.sas.ide.editor.model.ModelListener;
public class SoaXMLPage extends BPFormPage implements ModelListener{
	private static final String ID = "soa_xml";
	private static final String TITLE = "General";
	
	public SoaXMLPage (FormEditor editor) {
		super(editor, ID, TITLE);
	}

	public SoaXMLPage () {
		super(ID, TITLE);
	}

	public void modelChanged(int action, Node data) {
	}
	
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		
		super.getModel().addModelListener(this);
		this.createBody(managedForm);
		
		managedForm.refresh();		
	}
	private void createBody(IManagedForm managedForm){
		
		FormToolkit toolkit = managedForm.getToolkit();
		
		Composite body = managedForm.getForm().getBody();
		GridLayout layout = new GridLayout();
		layout.marginBottom=5;
		layout.marginTop=5;
		layout.marginLeft=5;
		layout.marginRight=5;
		layout.numColumns = 2;
		layout.horizontalSpacing = 5;
		body.setLayout(layout);

		Composite left = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20;
		left.setLayout(layout);
		left.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		SoaGeneralSection general = new SoaGeneralSection(this, left);
		managedForm.addPart(general);
	
		// same as that of sip/http
		SoaInitParamSection initParamSection = new SoaInitParamSection(this, left);
		managedForm.addPart(initParamSection);
		
		 SoaAddInitParamSection addInitParamSection = new SoaAddInitParamSection(this, left);
		 managedForm.addPart(addInitParamSection);

		
	}

}
