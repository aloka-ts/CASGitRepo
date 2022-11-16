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

import org.eclipse.core.resources.IProject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.part.FileEditorInput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.eclipse.swt.widgets.Combo;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.model.ModelListener;
import com.baypackets.sas.ide.util.IdeUtils;
import java.util.ArrayList;

public class SoaAddApplicationListenerSection extends SectionPart {
	
	private static final String LISTENER = "listener".intern();
	private static final String LISTENER_API = "listener-api".intern();
	private static final String LISTENER_URI = "listener-impl".intern();
	private static final String LISTENER_IMPL= "listener-uri".intern();
	private static final String APPLICATION= "application".intern();
	private ArrayList listeners = new ArrayList();	
	private ArrayList listenersImpl = new ArrayList();	
	private BPFormPage page;
	IProject project=null;
	public SoaAddApplicationListenerSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		
		//Create the UI.
		this.getSection().setText("Add Application Listener");
		this.createControls();
		this.loadContents();
	}
	
	protected void loadContents() {

		try {

			//get the file input associated with this file.... 
			IEditorInput input = page.getEditorInput();
			if (!(input instanceof FileEditorInput))
				return;

			//get the project assciated with this file...
			project = ((FileEditorInput) input).getFile().getProject();

			//clear the listener list....
			listeners.clear();
			listenersImpl.clear();
			//Get the Class names...
		//	IdeUtils.getClassNames(project, SEARCH_PATTERN, services);
			listeners=IdeUtils.getInterfaces(project);

			//Add the listener names to the combo...
			comboListenerApi.removeAll();
			comboListenerImpl.removeAll();
//			comboListenerApi.add("");
			for (int i = 0; i < listeners.size(); i++) {
				comboListenerApi.add(listeners.get(i).toString());
				comboListenerApi.select(0);
			}
			
			if(!comboListenerApi.getText().equals("")){
				listenersImpl=IdeUtils.getInterfaceImplementors(project,comboListenerApi.getText());
				SasPlugin.getDefault().log("Services impls loaded for selected interfcae are..."+listenersImpl);

				
				for (int i = 0; i < listenersImpl.size(); i++) {
					comboListenerImpl.add(listenersImpl.get(i).toString());
					comboListenerImpl.select(0);
				}
			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(
					"Exception thrown loadContents() SipAddserviceSection.java..."
							+ e);
		}
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
		page.createLabel(toolkit, composite, "Listener API:");
		comboListenerApi  = new CCombo(composite, SWT.BORDER | SWT.READ_ONLY);
		td = new GridData(GridData.FILL_HORIZONTAL);
		td.widthHint=250;
		comboListenerApi.setLayoutData(td);
		toolkit.adapt(comboListenerApi);
		comboListenerApi.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String api = comboListenerApi.getText();
				
				if(!api.equals("")){
				listenersImpl=IdeUtils.getInterfaceImplementors(project,api);
				SasPlugin.getDefault().log("Services impls loaded for selected interfcae are..."+listenersImpl);

				comboListenerImpl.removeAll();
				for (int i = 0; i < listenersImpl.size(); i++) {
					comboListenerImpl.add(listenersImpl.get(i).toString());
					comboListenerImpl.select(0);
				}
				}
				checkAddButton();
			}
		});

		
		//Create the Controls for the param value
		page.createLabel(toolkit, composite, "Listener Impl:");
		comboListenerImpl = new CCombo(composite, SWT.BORDER | SWT.READ_ONLY);
		td = new GridData(GridData.FILL_HORIZONTAL);
		td.widthHint=250;
		comboListenerImpl.setLayoutData(td);
		toolkit.adapt(comboListenerImpl);
		
		comboListenerImpl.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				
				checkAddButton();
			}
		});
		
		//Create the Controls for the description
		page.createLabel(toolkit, composite, "Listener URI:");
		comboListenerUri =  page.createText(toolkit, composite, "");
		td.widthHint=250;
		td = new GridData(GridData.FILL_HORIZONTAL);
		comboListenerUri.setLayoutData(td);
		comboListenerUri.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				
				checkAddButton();
			}
		});
		
		page.createEmptySpace(toolkit, composite, 1, BPFormPage.GRID_LAYOUT);
		btnAdd = toolkit.createButton(composite, "Add", SWT.FLAT);
		btnAdd.setEnabled(false);
		
		SelectionListener sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				
				String listenerApi = comboListenerApi.getText();
				String listenerImpl = comboListenerImpl.getText();
				String listenerUri = comboListenerUri.getText();
				

				Element docElement = page.getModel().getDocument().getDocumentElement();
				Element parent = page.getModel().getChild(docElement, APPLICATION,
						false);
				
		  	if(parent == null || listenerApi.trim().equals("")||listenerImpl.trim().equals("")||listenerUri.trim().equals("")) 
					return;
				
				Document doc = page.getModel().getDocument();
				
				Element cparam = doc.createElement(LISTENER);
				cparam.appendChild(doc.createTextNode("\n"));
				
				Element pname = doc.createElement(LISTENER_API);
				pname.appendChild(doc.createTextNode(listenerApi));
				cparam.appendChild(pname);
				cparam.appendChild(doc.createTextNode("\n"));
				
				Element pvalue = doc.createElement(LISTENER_IMPL);
				pvalue.appendChild(doc.createTextNode(listenerImpl));
				cparam.appendChild(pvalue);
				cparam.appendChild(doc.createTextNode("\n"));
				
			   Element desc = doc.createElement(LISTENER_URI);
			   desc.appendChild(doc.createTextNode(listenerUri));
			   cparam.appendChild(desc);
			   cparam.appendChild(doc.createTextNode("\n"));
				
//			   SasPlugin.getDefault().log("Adding Listener Child to the Doc"+cparam + "Pareent is.."+parent);
				
				page.getModel().addChild(parent, cparam);
				
				page.getModel().fireModelChanged(ModelListener.MODIFY, cparam);
				
				comboListenerApi.setText("");
				comboListenerImpl.setText("");
				comboListenerUri.setText("");
				btnAdd.setEnabled(false);
			}
		};
		btnAdd.addSelectionListener(sl);
	}
	
	
	
	private void checkAddButton() {

		Element docElement = page.getModel().getDocument().getDocumentElement();
		Element parent = page.getModel().getChild(docElement, this.APPLICATION,
				false);
		String listenerApi = comboListenerApi.getText();
		String listenerImpl = comboListenerImpl.getText();
		String listenerUri=comboListenerUri.getText();
		btnAdd.setEnabled(parent!=null
				&& !listenerApi.trim().equals("")
				&& !listenerImpl.trim().equals("")
				&& !listenerUri.trim().equals(""));
		
		SasPlugin.getDefault().log("The listener parent found is...."+parent +" Api is:"+listenerApi +" Impl :"+listenerImpl
				+" Uri: "+listenerUri);

	}
	private CCombo comboListenerApi;
	private CCombo comboListenerImpl;
	private Text comboListenerUri;
	private Button btnAdd;
	
	private Element parent;

	protected Element getParent() {
		return parent;
	}

	protected void setParent(Element parent) {
		this.parent = parent;
	}
}
