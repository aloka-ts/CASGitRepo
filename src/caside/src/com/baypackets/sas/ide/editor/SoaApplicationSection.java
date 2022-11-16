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
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.FileEditorInput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.IdeUtils;


public class SoaApplicationSection extends SectionPart {
	
	
	private static final String APPLICATION= "application".intern();
	private static final String APP_NAME = "app-name".intern();
	private static final String MAIN_CLASS = "main-class".intern();
	private static final String MAIN_METHOD = "main-method".intern();

	private BPFormPage page;
	
	 HashMap<String,java.util.List<String>> classes= new  HashMap<String,java.util.List<String>>();
	 
	public SoaApplicationSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR
				| Section.DESCRIPTION);
		this.page = page;

		//Create the UI.
		this.getSection().setText("Add New SOA Application");
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
			IProject project = ((FileEditorInput) input).getFile().getProject();

			//clear the listener list....
			classes.clear();

			classes=IdeUtils.getClasses(project);
			//Add the listener names to the combo...
			comboMainClass.removeAll();
			
			SasPlugin.getDefault().log("The classes obtanied are.."+classes);
			
			 if(classes.size()>0) {
				
				SasPlugin.getDefault().log("The classes size is.."+classes.size());
				Set cls=classes.keySet();
				Iterator itr=cls.iterator();
				while(itr.hasNext()){
					String className=(String)itr.next();
                    SasPlugin.getDefault().log("Add Class To MainClassCombo.."+className);
					comboMainClass.add(className);
			    }
				comboMainClass.select(0);
				
			}
			
			String mainClass=comboMainClass.getText();
			if(!mainClass.equals("")){
			   java.util.List<String> methods=classes.get(mainClass);
			   for(int i=0;i<methods.size();i++){
				comboMainMethod.add(methods.get(i));
				comboMainMethod.select(0);
			   }
			}

		} catch (Exception e) {
			SasPlugin.getDefault().log(
					"Exception thrown loadContents() SipAddserviceSection.java..."
							+ e);
		}
	}

	protected void createControls() {

		Section section = this.getSection();
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		SelectionListener sl = null;

		//Create a top level composite
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);

		//Associate this composite with the section.
		section.setClient(composite);

		GridData td = null;

		//Create the Controls for the application Name
		page.createLabel(toolkit, composite, "Application Name:");
		txtName = page.createText(toolkit, composite, "");
		td = new GridData(GridData.FILL_HORIZONTAL);
		td.widthHint=250;
		txtName.setLayoutData(td);
		this.txtName.addListener(SWT.Modify, new Listener() {
			public void handleEvent(Event e) {
				
				checkAddButton();
			}
		});

		//Create the controls for the application Class
		page.createLabel(toolkit, composite, "Main Class:");
		this.comboMainClass = new CCombo(composite,SWT.READ_ONLY|SWT.BORDER);
		td = new GridData(GridData.FILL_HORIZONTAL);
		td.widthHint=250;
//		td.heightHint=100;
		this.comboMainClass.setLayoutData(td);
		toolkit.adapt(this.comboMainClass);
		this.comboMainClass.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String mainClass=comboMainClass.getText();
				if(!mainClass.equals("")){
					java.util.List<String> methods=classes.get(mainClass);
					
					for(int i=0;i<methods.size();i++){
						comboMainMethod.add(methods.get(i));
						comboMainMethod.select(0);
					}
				}
				checkAddButton();
			}
		});


		//Create the controls for the application Class
		page.createLabel(toolkit, composite, "Main Method:");
		this.comboMainMethod = new CCombo(composite, SWT.BORDER | SWT.READ_ONLY);
		td = new GridData(GridData.FILL_HORIZONTAL);
		td.widthHint=250;
		this.comboMainMethod.setLayoutData(td);
		toolkit.adapt(this.comboMainMethod);
		this.comboMainMethod.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				
				checkAddButton();
			}
		});

//		page.createEmptySpace(toolkit, composite, 1, BPFormPage.TABLE_LAYOUT);
		btnAdd = toolkit.createButton(composite, "Add", SWT.FLAT);
		btnAdd.setEnabled(false);

		sl = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				String appName = txtName.getText();
				String mainClass = comboMainClass.getText();
				String mainMethod = comboMainMethod.getText();

				if (mainClass.trim().equals(""))
					return;
				Document doc = page.getModel().getDocument();

				Element docElement = doc.getDocumentElement();
				Element application = page.getModel().getChild(docElement, APPLICATION,
						false);

				if (application != null) {
					docElement.removeChild(application);
				}

				application = doc.createElement(APPLICATION);
				application.appendChild(doc.createTextNode("\n"));

				Element appname = doc.createElement(APP_NAME);
				appname.appendChild(doc.createTextNode(appName));
				application.appendChild(appname);
				application.appendChild(doc.createTextNode("\n"));

				Element mClass = doc.createElement(MAIN_CLASS);
			    mClass.appendChild(doc.createTextNode(mainClass));
				application.appendChild(mClass);
				application.appendChild(doc.createTextNode("\n"));


				Element mMethod = doc.createElement(MAIN_METHOD);
				mMethod.appendChild(doc.createTextNode(mainMethod));
				application.appendChild( mMethod);
				application.appendChild(doc.createTextNode("\n"));

				page.getModel().addChild(doc.getDocumentElement(), application);

				txtName.setEnabled(false);
				comboMainClass.setEnabled(false);
				comboMainMethod.setEnabled(false);
				btnAdd.setEnabled(false);
				btnChange.setEnabled(true);
			}
		};
		btnAdd.addSelectionListener(sl);
		
//		page.createEmptySpace(toolkit, composite, 1, BPFormPage.TABLE_LAYOUT);
		this.btnChange = toolkit.createButton(composite, "Update", SWT.PUSH|SWT.RIGHT);
		this.btnChange.setEnabled(false);
		
		this.btnChange.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				txtName.setEnabled(true);
				comboMainClass.setEnabled(true);
				comboMainMethod.setEnabled(true);
				btnChange.setEnabled(false);
			}
		});

		//fill the Service Section with the descriptor data if there is already one defined
		loadServiceData();
	}

	private void checkAddButton() {
		String serviceApiImplClass = comboMainClass.getText();
		btnAdd.setEnabled(!serviceApiImplClass.trim().equals(""));

	}

	protected void loadServiceData() {

		Element docElement = page.getModel().getDocument().getDocumentElement();
		Element application = page.getModel().getChild(docElement, this.APPLICATION,
				false);

		if (application != null) {
//			System.out.println("The Application Element found is " +application);
			Element svcname = page.getModel().getChild(application,
					this.APP_NAME, false);
			Element mainClass = page.getModel().getChild(application,
					this.MAIN_CLASS, false);
			
			Element mainMethod = page.getModel().getChild(application,
					this.MAIN_METHOD, false);

            
			if(svcname!=null){
			String appName=page.getModel().getText(svcname);
			txtName.setText(appName);
//			System.out.println("The Application name found in descriptor is" +appName);
			}
			if(mainClass!=null){
			String mainClss=page.getModel().getText(mainClass);
///			System.out.println("The Application Main Class found in descriptor is" +mainClss);
			comboMainClass.setText(mainClss);
			}
			if(mainMethod!=null){
			String mainMthd=page.getModel().getText(mainMethod);
//			System.out.println("The Application Main Method found in descriptor is" +mainMthd);
			comboMainMethod.setText(mainMthd);
			}

			txtName.setEnabled(false);
			comboMainClass.setEnabled(false);
			comboMainMethod.setEnabled(false);
            btnChange.setEnabled(true);

		}

	}

	private Text txtName;
	private CCombo comboMainClass;
	private CCombo comboMainMethod;
	private Button btnAdd;
	private Button btnChange;
	boolean isUpdate=false;

}
