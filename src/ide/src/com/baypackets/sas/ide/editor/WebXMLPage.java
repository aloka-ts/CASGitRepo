
//This class added by reeta for web.xml descriptor
package com.baypackets.sas.ide.editor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.w3c.dom.Node;

import com.baypackets.sas.ide.editor.model.ModelListener;
public class WebXMLPage extends BPFormPage implements ModelListener{
	
	private static final String ID = "web_xml";
	private static final String TITLE = "Overview";
	
	public WebXMLPage(FormEditor editor) {
		super(editor, ID, TITLE);
	}

	public WebXMLPage() {
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
		
		SipGeneralSection general = new SipGeneralSection(this, left);
		managedForm.addPart(general);
	
		SipContextParamSection context = new SipContextParamSection(this, left);
		managedForm.addPart(context);

		SipAddContextSection addContext = new SipAddContextSection(this, left);
		managedForm.addPart(addContext);
		
		Composite right = toolkit.createComposite(body);
		layout = new TableWrapLayout();
		layout.verticalSpacing = 20;
		right.setLayout(layout);
		right.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));

		SipSessionConfigSection sessionConfig = new SipSessionConfigSection(this, right);
		managedForm.addPart(sessionConfig);
		
	//	SipProxyConfigSection proxyConfig = new SipProxyConfigSection(this, right);
	//	managedForm.addPart(proxyConfig);

		SipListenerSection listener = new SipListenerSection(this, right);
		managedForm.addPart(listener);
		
		SipAddListenerSection addListener = new SipAddListenerSection(this, right);
		managedForm.addPart(addListener);
		
		//SipAddServletSection addServlet = new SipAddServletSection(this, right);
		//managedForm.addPart(addServlet);
	}

}
