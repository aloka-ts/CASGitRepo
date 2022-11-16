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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.baypackets.sas.ide.SasPlugin;

public class SipAddInitParamSection extends SectionPart {
	
	private  String INIT_PARAM = "init-param".intern();
	private  String PARAM_NAME = "param-name".intern();
	private String PARAM_VALUE = "param-value".intern();
	private String DESCRIPTION = "description".intern();
		
	private BPFormPage page;
	private boolean isSip289Page=false;
	
	public SipAddInitParamSection(BPFormPage page, Composite parent,boolean sip289) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		
		if(sip289){
			isSip289Page=true;
			
			INIT_PARAM = "javaee:init-param".intern();
		    PARAM_NAME = "javaee:param-name".intern();
		    PARAM_VALUE = "javaee:param-value".intern();
			
		}
		
		//Create the UI.
		this.getSection().setText("Add Init Parameter");
		this.createControls();
	}
	
	protected void createControls(){
	
		Section section = this.getSection();
		FormToolkit toolkit = page.getManagedForm().getToolkit();

		//Create a top level composite
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		//Associate this composite with the section.
		section.setClient(composite);
	
		TableWrapData td = null;
		
		//Create the Controls for the param name
		page.createLabel(toolkit, composite, "Param Name:");
		txtParamName = page.createText(toolkit, composite, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtParamName.setLayoutData(td);
		txtParamName.addListener(SWT.Modify, new Listener(){
			public void handleEvent(org.eclipse.swt.widgets.Event event){
				checkAddButton();
			}
		});
//		BPFormControl ctrlName =  new BPFormControl(txtParamName);
//		BPFormListener listener = new BPFormListener(){
//			public void textChanged(){
//				try{
//					checkAddButton();
//					
//				}catch(Exception ex){
//					SasPlugin.getDefault().log("Exception thrown createControls() SipAddInitParamSection.java..."+ex);
//				}
//			}
//
//			public void selectionChanged() {
//			}
//			
//		};
//		ctrlName.setFormListener(listener);

		
		//Create the Controls for the param value
		page.createLabel(toolkit, composite, "Param Value:");
		txtParamValue = page.createText(toolkit, composite, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtParamValue.setLayoutData(td);
		txtParamValue.addListener(SWT.Modify, new Listener(){
			public void handleEvent(org.eclipse.swt.widgets.Event event){
				checkAddButton();
			}
		});
//		BPFormControl ctrlValue =  new BPFormControl(txtParamValue);
//		BPFormListener listener1 = new BPFormListener(){
//			public void textChanged(){
//				try{
//					checkAddButton();
//					
//				}catch(Exception ex){
//					SasPlugin.getDefault().log("Exception thrown createControls() SipAddInitParamSection.java..."+ex);
//				}
//			}
//
//			public void selectionChanged() {
//			}
//			
//		};
//		ctrlValue.setFormListener(listener1);
		
		//Create the Controls for the description

		if (!isSip289Page) {
			page.createLabel(toolkit, composite, "Description:");
			txtDescription = page.createText(toolkit, composite, "");
			td = new TableWrapData(TableWrapData.FILL_GRAB);
			txtDescription.setLayoutData(td);
		}
		
		page.createEmptySpace(toolkit, composite, 1, BPFormPage.TABLE_LAYOUT);
		btnAdd = toolkit.createButton(composite, "Add", SWT.FLAT);
		btnAdd.setEnabled(false);
		
		SelectionListener sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				
				String strPName = txtParamName.getText();
				String strPValue = txtParamValue.getText();
				String strDesc="";
				
				if(!isSip289Page){
				
					strDesc = txtDescription.getText();
				}
				
		  	if(parent == null || strPName.trim().equals("")) 
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
				
				if(!isSip289Page&&!strDesc.trim().equals("")){
					Element desc = doc.createElement(DESCRIPTION);
					desc.appendChild(doc.createTextNode(strDesc));
					cparam.appendChild(desc);
					cparam.appendChild(doc.createTextNode("\n"));
				}
				
				page.getModel().addChild(parent, cparam);
				
				txtParamName.setText("");
				txtParamValue.setText("");
				
				if(!isSip289Page)
				txtDescription.setText("");
				
				btnAdd.setEnabled(false);
			}
		};
		btnAdd.addSelectionListener(sl);
	}
	
	private void checkAddButton() {
		String paramName = txtParamName.getText();
		String paramValue = txtParamValue.getText();
		btnAdd.setEnabled( parent != null 
				&& ! paramName.trim().equals("")
				&& ! paramValue.trim().equals(""));

	}
	private Text txtParamName;
	private Text txtParamValue;
	private Text txtDescription;
	private Button btnAdd;
	
	private Element parent;

	protected Element getParent() {
		return parent;
	}

	protected void setParent(Element parent) {
		this.parent = parent;
	}
}
