
//This class added by reeta for web.xml descriptor
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

public class HttpServletPage  extends BPFormPage{
	
	private static final String ID = "http_servlet";
	private static final String TITLE = "Servlets";
	
	private HttpServletSection servletSection;
	private HttpAddServletSection addServletSection;
	private HttpInitParamSection initParamSection;
	private HttpAddInitParamSection addInitParamSection;
	private HttpServletMappingSection servletMappingSection;
	private HttpAddServletMappingSection addServletMappingSection;
	
	private Element element;
	
	public HttpServletPage(FormEditor editor) {
		super(editor, ID, TITLE);
	}

	public HttpServletPage() {
		super(ID, TITLE);
	}
		
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);

		this.fillBody(managedForm);
		managedForm.refresh();		
	}
	
	private void fillBody(IManagedForm managedForm){
		FormToolkit toolkit = managedForm.getToolkit();
		
		Composite body = managedForm.getForm().getBody();
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		layout.horizontalSpacing = 5;
		body.setLayout(layout);
		
		Composite top = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20;
		top.setLayout(layout);
		
		GridData td =new GridData(GridData.FILL_BOTH);
		td.horizontalSpan = 4;
		top.setLayoutData(td);
		
		servletSection = new HttpServletSection(this, top);
		managedForm.addPart(servletSection);
		
		Composite middleLeft = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20;
		middleLeft.setLayout(layout);
		
		addServletSection = new HttpAddServletSection(this, middleLeft);
		managedForm.addPart(addServletSection);
		
		Composite middleCenter = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20;
		middleCenter.setLayout(layout);
		
		td =new GridData(GridData.FILL_BOTH);
		td.horizontalSpan = 2;
		middleCenter.setLayoutData(td);
		
		initParamSection = new HttpInitParamSection(this, middleCenter);
		managedForm.addPart(initParamSection);
		
		Composite middleRight = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20; 
		middleRight.setLayout(layout);
		
		td =new GridData(GridData.FILL_BOTH);
		td.horizontalSpan = 1;
		middleRight.setLayoutData(td); //reeta added
		
		addInitParamSection = new HttpAddInitParamSection(this, middleRight);
		managedForm.addPart(addInitParamSection);
		
		Composite bottomLeft = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20;
		bottomLeft.setLayout(layout);
		
		td =new GridData(GridData.FILL_HORIZONTAL); //reeta made from fill both to beginning
		td.horizontalSpan = 2;  
		td.grabExcessHorizontalSpace = true;
		td.grabExcessVerticalSpace = true;
		bottomLeft.setLayoutData(td);
		
		servletMappingSection = new HttpServletMappingSection(this, bottomLeft);
		managedForm.addPart(servletMappingSection);
		
		Composite bottomRight = toolkit.createComposite(body);
		layout = new GridLayout();
		layout.verticalSpacing = 20;
		bottomRight.setLayout(layout);
		
		//reeta added
		td =new GridData(GridData.FILL);
		td.horizontalSpan = 1;
		bottomRight.setLayoutData(td);
		//
		addServletMappingSection = new HttpAddServletMappingSection(this, bottomRight);
		managedForm.addPart(addServletMappingSection);
		
		
	}
	
	public void selectionChanged(ISelection selection) {		
		if(!(selection instanceof IStructuredSelection))
			return;
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		if(obj instanceof Element){
			this.element = (Element)obj;
		}else{
			this.element = null;
		}
		
		if(initParamSection != null){
			initParamSection.setInput(this.element);
		}
		
		if(servletMappingSection != null){
			servletMappingSection.setParent(this.element);
			servletMappingSection.setInput(this.element);
		}
		
		if(addInitParamSection != null){
			addInitParamSection.setParent(this.element);
		}
		
		if(addServletMappingSection != null){
			addServletMappingSection.setParent(this.element);
		}
	}

	public void mappingChanged(ISelection selection){
		if(!(selection instanceof IStructuredSelection))
			return;
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		Element mappingSelection = null;
		if(obj instanceof Element){
			mappingSelection = (Element)obj;
		}	
		if(addServletMappingSection != null){
			addServletMappingSection.setSelection(mappingSelection);
		}
	}
	
	protected HttpAddInitParamSection getAddInitParamSection() {
		return addInitParamSection;
	}

	protected HttpAddServletSection getAddServletSection() {
		return addServletSection;
	}

	

}
