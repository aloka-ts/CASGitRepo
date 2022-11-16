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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.w3c.dom.Element;

import com.baypackets.sas.ide.SasPlugin;

public class SipServletPage extends BPFormPage {

	private static final String ID = "sip_servlet";
	private static final String TITLE = "Servlets";

	// private SipServletSection servletSection;
	private SipAddMainServletSection mainServletSection;
	private SipAddServletSection addServletSection;
	private SipInitParamSection initParamSection;
	private SipAddInitParamSection addInitParamSection;
	private SipServletMappingSection servletMappingSection;
	private SipAddServletMappingSection addServletMappingSection;
	private boolean isSip289Xml = false;

	public boolean isSip289Xml() {
		return isSip289Xml;
	}

	public void setSip289Xml(boolean isSip289Xml) {
		this.isSip289Xml = isSip289Xml;
	}

	private Element element;

	public SipServletPage(FormEditor editor) {
		super(editor, ID, TITLE);
	}

	public SipServletPage() {
		super(ID, TITLE);
	}

	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		this.fillBody(managedForm);
		managedForm.refresh();
	}

	private void fillBody(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();

		Composite body = managedForm.getForm().getBody();

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing = 5;
		body.setLayout(layout);

		Composite top = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 10;
		top.setLayout(layout);

		GridData td = new GridData(GridData.FILL_HORIZONTAL);
		td.horizontalSpan = 3;
		top.setLayoutData(td);

		if (isSip289Xml) {
			SipServlet289Section servletSection = new SipServlet289Section(
					this, top);
			managedForm.addPart(servletSection);
		} else {
			SipServletSection servletSection = new SipServletSection(this, top);
			managedForm.addPart(servletSection);
		}

		if (isSip289Xml) {

			Composite middle = toolkit.createComposite(body);
			layout = new GridLayout();
			layout.verticalSpacing = 10;
			layout.horizontalSpacing=10;
			layout.numColumns=1;
			middle.setLayout(layout);

			SasPlugin.getDefault().log("Creating MainServlet Section for ..");
			mainServletSection = new SipAddMainServletSection(this, middle);
			managedForm.addPart(mainServletSection);

			SasPlugin.getDefault().log(
					"MainServlet is defined?? .."
							+ mainServletSection.mainServletDefined);
		}

		Composite middleLeft = toolkit.createComposite(body);
		layout = new GridLayout();
//		layout.verticalSpacing = 20;
		layout.verticalSpacing=10;
		layout.horizontalSpacing=10;
		layout.numColumns=1;
		middleLeft.setLayout(layout);

		addServletSection = new SipAddServletSection(this, middleLeft);
		managedForm.addPart(addServletSection);

		Composite middleCenter = toolkit.createComposite(body);
		layout = new GridLayout();
//		layout.verticalSpacing = 20;
		layout.verticalSpacing=10;
		layout.horizontalSpacing=10;
		layout.numColumns=1;
		middleCenter.setLayout(layout);

		td = new GridData(GridData.FILL_BOTH);
		td.horizontalSpan = 1;
		middleCenter.setLayoutData(td);

		initParamSection = new SipInitParamSection(this, middleCenter,
				isSip289Xml);
		managedForm.addPart(initParamSection);

		Composite middleRight = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20;
		middleRight.setLayout(layout);

		td = new GridData(GridData.FILL_BOTH);
		td.horizontalSpan = 3;
		middleRight.setLayoutData(td); // reeta added

		addInitParamSection = new SipAddInitParamSection(this, middleRight,
				isSip289Xml);
		managedForm.addPart(addInitParamSection);

		Composite bottomLeft = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20;
		bottomLeft.setLayout(layout);

		td = new GridData(GridData.FILL_BOTH);
		td.horizontalSpan = 2;
		td.grabExcessHorizontalSpace = true;
		td.grabExcessVerticalSpace = true;
		bottomLeft.setLayoutData(td);

		if (!isSip289Xml) {
			servletMappingSection = new SipServletMappingSection(this,
					bottomLeft, mainServletSection.mainServletDefined);
			managedForm.addPart(servletMappingSection);

			Composite bottomRight = toolkit.createComposite(body);
			layout = new GridLayout();
			layout.verticalSpacing = 20;
			bottomRight.setLayout(layout);

			addServletMappingSection = new SipAddServletMappingSection(this,
					bottomRight, mainServletSection.mainServletDefined);
			managedForm.addPart(addServletMappingSection);
		}

	}

	public void selectionChanged(ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return;
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj instanceof Element) {
			this.element = (Element) obj;
		} else {
			this.element = null;
		}

		if (initParamSection != null) {
			initParamSection.setInput(this.element);
		}

		// if (mainServletSection != null) {
		// mainServletSection.setInput(this.element);
		// }

		if (servletMappingSection != null) {
			servletMappingSection.setParent(this.element);
		}

		if (addInitParamSection != null) {
			addInitParamSection.setParent(this.element);
		}

		if (addServletMappingSection != null) {
			addServletMappingSection.setParent(this.element);
		}
	}

	public void mappingChanged(ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return;
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		Element mappingSelection = null;
		if (obj instanceof Element) {
			mappingSelection = (Element) obj;
		}
		if (addServletMappingSection != null) {
			addServletMappingSection.setSelection(mappingSelection);
		}
	}

	protected SipAddInitParamSection getAddInitParamSection() {
		return addInitParamSection;
	}

	protected SipAddServletSection getAddServletSection() {
		return addServletSection;
	}

}
