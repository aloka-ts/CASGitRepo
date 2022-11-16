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

import java.util.ArrayList;

import org.eclipse.core.internal.events.NodeIDMap;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.FileEditorInput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.model.ModelListener;
import com.baypackets.sas.ide.util.IdeUtils;

public class SasXMLPage extends BPFormPage implements ModelListener {

	private static final String ID = "sas_xml";
	private static final String TITLE_NEW = "AGNITY CAS Descriptor";
	private static final String TITLE = "AGNITY SAS Descriptor";
	
	private static final String NAME = "name".intern();
	private static final String VERSION = "version".intern();
	private static final String PRIORITY = "priority".intern();
	private static final String SBB = "sbb".intern();
	private static Element resFactoryMappig=null;
	
	
	
	public SasXMLPage(FormEditor editor,String desc) {
		super(editor, ID, "AGNITY "+desc+" Descriptor");
	}

	public SasXMLPage() {
		super(ID, TITLE_NEW);
	}

	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		
		super.getModel().addModelListener(this);
		
		resFactoryMappig= super.getModel().getChild("resource-factory-mapping");
		
		this.createBody(managedForm);
		
		SasPlugin.getDefault().log("Form Content is created!!!!!!");
		
		this.loadContents();
		SasPlugin.getDefault().log("Form Content is loaded!!!!!!");
		managedForm.refresh();	
		SasPlugin.getDefault().log("createFormContent leaving!!!!!!");
	}
	
	protected void loadContents(){
	
		String str = null;
		
		str = super.getModel().getChildText(NAME);
		this.txtName.setText(str);
		
		str = super.getModel().getChildText(VERSION);
		this.txtVersion.setText(str);
		
		str = super.getModel().getChildText(PRIORITY);
		comboPriority.setText(str);
		
		boolean select = super.getModel().getChild(SBB) != null;
		this.btnSbb.setSelection(select);
	
		if(resFactoryMappig!=null){
			
			SasPlugin.getDefault().log("ResourceFactory found is" +resFactoryMappig);
		}
	}
	
	private void createBody(IManagedForm managedForm) {
	
		FormToolkit toolkit = managedForm.getToolkit();
		

		Composite body = managedForm.getForm().getBody();
		TableWrapLayout layout = new TableWrapLayout();
		layout.bottomMargin = 5;
		layout.topMargin = 5;
		layout.leftMargin = 5;
		layout.rightMargin = 5;
		layout.numColumns = 2;
		layout.horizontalSpacing = 10;
		body.setLayout(layout);
		
		Section section = this.createStaticSection( toolkit, body, "Application Information");
		Composite container = toolkit.createComposite(section, SWT.NONE);
		this.createControls(container, toolkit);
		
		if(resFactoryMappig!=null){
			RAAddMessageHandlerSection raddsec= new RAAddMessageHandlerSection(this, body);
			managedForm.addPart(raddsec);
			
			RAMessageHandlerSection rasec= new RAMessageHandlerSection(this, body);
			managedForm.addPart(rasec);

			RAMessageHandlerMappingSection ramsec= new RAMessageHandlerMappingSection(this, body);
			managedForm.addPart(ramsec);
		}
		
		
		
     	section.setClient(container);
//		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
	}
	
	private void createControls(Composite composite, FormToolkit toolkit){
	//	System.out.println("Inside create Controls....");
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 20;
		composite.setLayout(layout);
		
		//Create the Controls for the application name
		super.createLabel(toolkit, composite, "Name:");
		txtName =  super.createText(toolkit, composite, "");
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtName.setLayoutData(td);
		
	//	super.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);

		BPFormControl ctrlName =  new BPFormControl(txtName);
		BPFormListener listener = new BPFormListener(){
			public void textChanged(){
				try{
					String str = txtName.getText();
					Element element  = getModel().getChild(NAME, true);
					getModel().setText(element, str, true );
				}catch(Exception ex){
					SasPlugin.getDefault().log("The Exception thrown by textchnaged() of Name control SasXMLPage.java  " +ex);
				}
			}

			public void selectionChanged() {
			}
		};
		ctrlName.setFormListener(listener);
		
		//Create the controls for the application version
		super.createLabel(toolkit, composite,"Version:");
		txtVersion = super.createText(toolkit, composite, "");
		TableWrapData td1 = new TableWrapData(TableWrapData.FILL_GRAB);
		txtVersion.setLayoutData(td1);
//		super.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
		
		BPFormControl ctrlVersion =  new BPFormControl(txtVersion);
		listener = new BPFormListener(){
			public void textChanged(){
				try{
					String str = txtVersion.getText();
					Element element  = getModel().getChild(VERSION, true);
					getModel().setText(element, str, true);
				}catch(Exception ex){
					SasPlugin.getDefault().log("The Exception thrown by textChanged()  on Version: control SasXMLPage.java  " +ex);
				}
			}
			public void selectionChanged() {
			}
		};
		ctrlVersion.setFormListener(listener);
		
		//Create the controls for the application version
		super.createLabel(toolkit, composite, "Priority:");
		comboPriority = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		for(int i=1; i<=10; i++){
			comboPriority.add(""+i);
		}
		toolkit.adapt(comboPriority);
//		super.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);

		BPFormControl ctrlPriority =  new BPFormControl(comboPriority);
		listener = new BPFormListener(){
			public void textChanged(){
			}
			public void selectionChanged() {
				try{
					String str = comboPriority.getText();
					Element element  = getModel().getChild(PRIORITY, true);
					getModel().setText(element, str, true);
				}catch(Exception ex){
					SasPlugin.getDefault().log("The Exception thrown by textChanged()  on Priority: control SasXMLPage.java  " +ex);
				}	
			}
		};
		ctrlPriority.setFormListener(listener);
		
		//Create the controls for the whether to use SBBs or not.
		super.createLabel(toolkit, composite, "Use Service Building Blocks:");
		btnSbb = toolkit.createButton(composite, "", SWT.CHECK);
//		super.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
		
		BPFormControl ctrlSbb =  new BPFormControl(btnSbb);
		listener = new BPFormListener(){
			public void textChanged(){
				//not needed
			}
			public void selectionChanged() {
				try{
					if(btnSbb.getSelection()){
						getModel().getChild(SBB, true);
					}else{
						getModel().removeChild(SBB);
					}
				}catch(Exception ex){
					SasPlugin.getDefault().log("The Exception thrown by selectionChanged()  on Use Service Building Blocks: control SasXMLPage.java  " +ex);
				}	
			}
		};
		ctrlSbb.setFormListener(listener);

	}
	
	public void modelChanged(int action, Node data) {
		if(data != null && data.getNodeName().equals(NAME)){
			String str = getModel().getText((Element)data);
			this.txtName.setText(action != ModelListener.REMOVE ? str : "");
		}
		
		if(data != null && data.getNodeName().equals(VERSION)){
			String str = getModel().getText((Element)data);
			this.txtVersion.setText(action != ModelListener.REMOVE ? str : "");
		}
		
		if(data != null && data.getNodeName().equals(PRIORITY)){
			String str = getModel().getText((Element)data);
			this.comboPriority.setText(action != ModelListener.REMOVE ? str : "");
		}
		
		if(data != null && data.getNodeName().equals(SBB)){
			boolean select = (action != ModelListener.REMOVE);  
			this.btnSbb.setSelection(select);
		}
					
	}
	
	private Text txtName;
	private Text txtVersion;
	private Combo comboPriority;
	private Button btnSbb;

}
