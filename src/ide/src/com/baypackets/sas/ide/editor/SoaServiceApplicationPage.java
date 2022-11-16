package com.baypackets.sas.ide.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.baypackets.sas.ide.editor.model.ModelListener;

public class SoaServiceApplicationPage extends BPFormPage implements
		ModelListener {
	private static final String ID = "soa_xml";
	private static final String TITLE = "Service-Application";
	private SoaServiceListSection listServiceSection;
	private SoaAddServiceSection addServiceSection;
	private SoaApplicationSection soaApplicationSection;
	private SoaApplicationListenerSection appListenerSection;
	private SoaAddApplicationListenerSection addAppListenerSection;
	private Element element;

	public SoaServiceApplicationPage(FormEditor editor) {
		super(editor, ID, TITLE);
	}

	public SoaServiceApplicationPage() {
		super(ID, TITLE);
	}

	public void modelChanged(int action, Node data) {
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
		layout.numColumns = 3; //make it 3
		layout.horizontalSpacing = 5;
		body.setLayout(layout);

		Composite top = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20;
		top.setLayout(layout);

		GridData td = new GridData(GridData.FILL_BOTH);
		td.horizontalSpan = 3; //make it 3
		top.setLayoutData(td);

		listServiceSection = new SoaServiceListSection(this, top);
		managedForm.addPart(listServiceSection);

		Composite middleLeft = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20;
		middleLeft.setLayout(layout);

		this.addServiceSection = new SoaAddServiceSection(this, middleLeft);
		managedForm.addPart(this.addServiceSection);

		Composite middleCenter = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20;
		middleCenter.setLayout(layout);

		td = new GridData(GridData.FILL_BOTH);
		td.horizontalSpan = 1;
		middleCenter.setLayoutData(td);

		this.soaApplicationSection = new SoaApplicationSection(this,
				middleCenter);
		managedForm.addPart(this.soaApplicationSection);

		Composite middleRight = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20;
		middleRight.setLayout(layout);

		td = new GridData(GridData.FILL_BOTH);
		td.horizontalSpan = 1;
		middleRight.setLayoutData(td); //reeta added

		this.addAppListenerSection = new SoaAddApplicationListenerSection(this,
				middleRight);
		managedForm.addPart(this.addAppListenerSection);

		Composite bottomLeft = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20;
		bottomLeft.setLayout(layout);

		td = new GridData(GridData.FILL_BOTH);
		td.horizontalSpan = 2;
		td.grabExcessHorizontalSpace = true;
		td.grabExcessVerticalSpace = true;
		bottomLeft.setLayoutData(td);

		appListenerSection = new SoaApplicationListenerSection(this, bottomLeft);
		managedForm.addPart(appListenerSection);

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
	}

	public void mappingChanged(ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return;
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		Element mappingSelection = null;
		if (obj instanceof Element) {
			mappingSelection = (Element) obj;
		}
	}

}
