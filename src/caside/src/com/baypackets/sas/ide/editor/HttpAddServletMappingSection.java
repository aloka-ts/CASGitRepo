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
//author@reetaAggarwal
package com.baypackets.sas.ide.editor;

import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
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
import java.util.ArrayList;
import org.eclipse.core.resources.IProject;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.IEditorInput;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.IdeUtils;

public class HttpAddServletMappingSection extends SectionPart{
	
	private static final String SERVLET_MAPPING = "servlet-mapping".intern();
	private static final String SERVLET_NAME = "servlet-name".intern();
	private static final String URL_PATTERN = "url-pattern".intern();
	
private static final String SUPER_CLASS_NAME = "javax.servlet.http.HttpServlet".intern();
	
	private static SearchPattern SEARCH_PATTERN = 
		SearchPattern.createPattern(SUPER_CLASS_NAME, 
			IJavaSearchConstants.CONSTRUCTOR,
			IJavaSearchConstants.REFERENCES,
			SearchPattern.R_EXACT_MATCH);
	
	private ArrayList servlets = new ArrayList();	
	private BPFormPage page;
	private CCombo comboServlets;
	
	public HttpAddServletMappingSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		
		//Create the UI.
		this.getSection().setText("Add Servlet Mapping ");
		this.createControls();
		this.loadContents();
	}
	
	
	
protected void loadContents(){
		
		try{
			
			//get the file input associated with this file.... 
			IEditorInput input = page.getEditorInput();
			if(!(input instanceof FileEditorInput))
				return;
			
			//get the project assciated with this file...
			IProject project = ((FileEditorInput)input).getFile().getProject();
			
			//clear the listener list....
			servlets.clear();
			
			//Get the Class names...
			IdeUtils.getClassNames(project, SEARCH_PATTERN, servlets);
			
			//Add the listener names to the combo...
			comboServlets.removeAll();
			
			for(int i=0; i<servlets.size();i++){
				comboServlets.add(servlets.get(i).toString());
			}
			comboServlets.add("");
			comboServlets.select(0);
			
		}catch(Exception e){
			SasPlugin.getDefault().log("Exception thrown loadContents() HttpAddServletMappingSection.java..."+e);
		}
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
		page.createLabel(toolkit, composite, "Servlet Name:");
		
		comboServlets = new CCombo(composite, SWT.DROP_DOWN | SWT.READ_ONLY |SWT.BORDER);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		comboServlets.setLayoutData(td);
		toolkit.adapt(comboServlets);
		
		
	/*	txtParamName = page.createText(toolkit, composite, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtParamName.setLayoutData(td);
		BPFormControl ctrlName =  new BPFormControl(txtParamName);
		*/
//		BPFormControl ctrlName =  new BPFormControl(comboServlets);
//		BPFormListener listener = new BPFormListener(){
//			public void textChanged(){
//				try{
//					String str = comboServlets.getText();
//					btnAdd.setEnabled(parent != null && !str.trim().equals("")); 
//					
//					
//				}catch(Exception ex){
//					SasPlugin.getDefault().log("Exception thrown createControls() HttpAddServletMappingSection.java..."+ex);
//				}
//			}
//
//			public void selectionChanged() {
//			}
//			
//		};
//		ctrlName.setFormListener(listener);

		
		//Create the Controls for the param value
		page.createLabel(toolkit, composite, "Url Pattern");
		txtParamValue = page.createText(toolkit, composite, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtParamValue.setLayoutData(td);
		txtParamValue.addListener(SWT.Modify, new Listener(){
			public void handleEvent(org.eclipse.swt.widgets.Event event){
				try{
					String str = comboServlets.getText();
					btnAdd.setEnabled(parent != null && !str.trim().equals("")); 
					
					
				}catch(Exception ex){
					SasPlugin.getDefault().log("Exception thrown createControls() HttpAddServletMappingSection.java..."+ex);
				}
			}
		});
		
		
		page.createEmptySpace(toolkit, composite, 1, BPFormPage.TABLE_LAYOUT);
		btnAdd = toolkit.createButton(composite, "Add", SWT.FLAT);
		btnAdd.setEnabled(false);
		
		SelectionListener sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				
				String strPName = page.getModel().getChildText(parent, SERVLET_NAME); //replaced with below line
		//		String strPName = comboServlets.getText(); //reeta commented it
				String strPValue = txtParamValue.getText();
			
				
		  	if(parent == null || strPName.trim().equals("")) 
					return;
				
				Document doc = page.getModel().getDocument();
				
				Element cparam = doc.createElement(SERVLET_MAPPING );
				cparam.appendChild(doc.createTextNode("\n"));
				
				Element pname = doc.createElement(SERVLET_NAME);
				pname.appendChild(doc.createTextNode(strPName));
				cparam.appendChild(pname);
				cparam.appendChild(doc.createTextNode("\n"));
				
				Element pvalue = doc.createElement(URL_PATTERN);
				pvalue.appendChild(doc.createTextNode(strPValue));
				cparam.appendChild(pvalue);
				cparam.appendChild(doc.createTextNode("\n"));
				
				page.getModel().addChild(doc.getDocumentElement(), cparam);
				
				comboServlets.setText("");
				txtParamValue.setText("");
				btnAdd.setEnabled(false);
			}
		};
		btnAdd.addSelectionListener(sl);
	}
	
	protected void setSelection(Element selection) {
		
	}
	
//	private Text txtParamName;
	private Text txtParamValue;
	private Button btnAdd;
	
	private Element parent;

	protected Element getParent() {
		return parent;
	}

	protected void setParent(Element parent) {
		this.parent = parent;
	}
	
}
