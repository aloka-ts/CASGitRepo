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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.w3c.dom.Node;

import com.baypackets.sas.ide.editor.model.ModelListener;

public class SipXMLPage extends BPFormPage implements ModelListener {

	private static final String ID = "sip_xml";
	private static final String TITLE = "Overview";
	
	private boolean isSip289Xml = false;

	public boolean isSip289Xml() {
		return isSip289Xml;
	}

	public void setSip289Xml(boolean isSip289Xml) {
		this.isSip289Xml = isSip289Xml;
	}
	
	public SipXMLPage(FormEditor editor) {
		super(editor, ID, TITLE);
	}

	public SipXMLPage() {
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
		TableWrapLayout layout = new TableWrapLayout();
		layout.bottomMargin = 5;
		layout.topMargin = 5;
		layout.leftMargin = 5;
		layout.rightMargin = 5;
		layout.numColumns = 2;
		layout.horizontalSpacing = 5;
		body.setLayout(layout);

		Composite left = toolkit.createComposite(body);
		layout = new TableWrapLayout();
		layout.verticalSpacing = 20;
		left.setLayout(layout);
		left.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		
		SipGeneralSection general = new SipGeneralSection(this, left,isSip289Xml);
		managedForm.addPart(general);
	
		SipContextParamSection context = new SipContextParamSection(this, left,isSip289Xml);
		managedForm.addPart(context);

		SipAddContextSection addContext = new SipAddContextSection(this, left,isSip289Xml);
		managedForm.addPart(addContext);
		
		Composite right = toolkit.createComposite(body);
		layout = new TableWrapLayout();
		layout.verticalSpacing = 20;
		right.setLayout(layout);
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		SipSessionConfigSection sessionConfig = new SipSessionConfigSection(this, right,isSip289Xml);
		managedForm.addPart(sessionConfig);
		
		SipProxyConfigSection proxyConfig = new SipProxyConfigSection(this, right,isSip289Xml);
		managedForm.addPart(proxyConfig);

		if (isSip289Xml) {
			Sip289ListenerSection listener = new Sip289ListenerSection(this,
					right);
			managedForm.addPart(listener);

			SipAdd289ListenerSection addListener = new SipAdd289ListenerSection(
					this, right);
			managedForm.addPart(addListener);

		} else {
			SipListenerSection listener = new SipListenerSection(this, right);
			managedForm.addPart(listener);

			SipAddListenerSection addListener = new SipAddListenerSection(this,
					right);
			managedForm.addPart(addListener);
		}
		
		//SipAddServletSection addServlet = new SipAddServletSection(this, right);
		//managedForm.addPart(addServlet);
	}
}
