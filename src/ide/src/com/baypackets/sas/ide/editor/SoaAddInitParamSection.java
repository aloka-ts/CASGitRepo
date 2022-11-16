package com.baypackets.sas.ide.editor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
//import org.eclipse.ui.forms.widgets.TableWrapData;
//import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.baypackets.sas.ide.SasPlugin;

public class SoaAddInitParamSection extends SectionPart {
	
	private static final String INIT_PARAM = "init-param".intern();
	private static final String PARAM_NAME = "param-name".intern();
	private static final String PARAM_VALUE = "param-value".intern();
//	private static final String DESCRIPTION = "description".intern();
		
	private BPFormPage page;
	
	public SoaAddInitParamSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		
		//Create the UI.
		this.getSection().setText("Add Init Parameter");
		this.createControls();
	}
	
	protected void createControls(){
	
		Section section = this.getSection();
		FormToolkit toolkit = page.getManagedForm().getToolkit();

		//Create a top level composite
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		//Associate this composite with the section.
		section.setClient(composite);
	
		GridData td = null;
		
		//Create the Controls for the param name
		page.createLabel(toolkit, composite, "Param Name:");
		txtParamName = page.createText(toolkit, composite, "");
		td = new GridData(GridData.FILL_HORIZONTAL);
		td.widthHint=285;
		txtParamName.setLayoutData(td);
		txtParamName.addListener(SWT.Modify, new Listener(){
			public void handleEvent(org.eclipse.swt.widgets.Event event){
				checkAddButton();
			}
		});
		
		//Create the Controls for the param value
		page.createLabel(toolkit, composite, "Param Value:");
		txtParamValue = page.createText(toolkit, composite, "");
		td = new GridData(GridData.FILL_HORIZONTAL);
		td.widthHint=285;
		txtParamValue.setLayoutData(td);
		txtParamValue.addListener(SWT.Modify, new Listener(){
			public void handleEvent(org.eclipse.swt.widgets.Event event){
				checkAddButton();
			}
		});
		
		page.createEmptySpace(toolkit, composite, 1, BPFormPage.GRID_LAYOUT);
		btnAdd = toolkit.createButton(composite, "Add", SWT.FLAT);
		btnAdd.setEnabled(false);
		
		SelectionListener sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				Element docElement = page.getModel().getDocument().getDocumentElement();
				String strPName = txtParamName.getText();
				String strPValue = txtParamValue.getText();
	//			String strDesc = txtDescription.getText();
				
		  	if(strPName.trim().equals("") || strPValue.trim().equals("")) 
					return;
				
				Document doc = page.getModel().getDocument();
				
				Element cparam = doc.createElement(INIT_PARAM);
				cparam.appendChild(doc.createTextNode("\n"));
				
				Element pname = doc.createElement(PARAM_NAME);
				pname.appendChild(doc.createTextNode(strPName));
				cparam.appendChild(pname);
				cparam.appendChild(doc.createTextNode("\n"));
				
				Element pvalue = doc.createElement(PARAM_VALUE);
				pvalue.appendChild(doc.createTextNode(strPValue));
				cparam.appendChild(pvalue);
				cparam.appendChild(doc.createTextNode("\n"));
				
//				if(!strDesc.trim().equals("")){
//					Element desc = doc.createElement(DESCRIPTION);
//					desc.appendChild(doc.createTextNode(strDesc));
//					cparam.appendChild(desc);
//					cparam.appendChild(doc.createTextNode("\n"));
//				}
				
				page.getModel().addChild(docElement, cparam);
				
				txtParamName.setText("");
				txtParamValue.setText("");
//				txtDescription.setText("");
				btnAdd.setEnabled(false);
			}
		};
		btnAdd.addSelectionListener(sl);
	}
	
	
	private void checkAddButton() {

		Element docElement = page.getModel().getDocument().getDocumentElement();
		String paramName = txtParamName.getText();
		String paramValue = txtParamValue.getText();
		btnAdd.setEnabled( ! paramName.trim().equals("")
				&& ! paramValue.trim().equals(""));

	}
	private Text txtParamName;
	private Text txtParamValue;
//	private Text txtDescription;
	private Button btnAdd;
	
	
}
