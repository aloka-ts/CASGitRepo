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

import org.eclipse.core.resources.IProject;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
//import org.eclipse.ui.forms.widgets.TableWrapData;
//import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.FileEditorInput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.IdeUtils;

public class SoaAddServiceSection extends SectionPart {
	private static final String SERVICE = "service".intern();
	private static final String SERVICE_NAME = "service-name".intern();
	private static final String ANNOTATED = "annotated".intern();
	private static final String SERVICE_API = "service-api".intern();
	private static final String SERVICE_IMPL = "service-impl".intern();
//	private static final String SERVICE_PATH = "service-path".intern();
	private static final String NOTIFICATION_API = "notification-api".intern();
	IProject project=null;

	private static final String SUPER_CLASS_NAME = "javax.service.sip.Sipservice"
			.intern();

	private static SearchPattern SEARCH_PATTERN = SearchPattern.createPattern(
			SUPER_CLASS_NAME, IJavaSearchConstants.CONSTRUCTOR,
			IJavaSearchConstants.REFERENCES, SearchPattern.R_EXACT_MATCH);

	private BPFormPage page;
	private ArrayList<String> services = new ArrayList<String>();
	private ArrayList<String> servicesImpl = new ArrayList<String>();

	public SoaAddServiceSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR
				| Section.DESCRIPTION);
		this.page = page;

		//Create the UI.
		this.getSection().setText("Add New SOA Service");
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
			services.clear();

			//Get the Class names...
		//	IdeUtils.getClassNames(project, SEARCH_PATTERN, services);
			services=IdeUtils.getInterfaces(project);

			//Add the listener names to the combo...
			comboServiceApi.removeAll();
//			comboServiceApi.add("");
			
			comboNotiApi.removeAll();
//			comboNotiApi.add("");
			
			for (int i = 0; i < services.size(); i++) {
				comboServiceApi.add(services.get(i).toString());
				comboNotiApi.add(services.get(i).toString());
				comboServiceApi.select(0);
				comboNotiApi.select(0);
				
			}
			
			if(!comboServiceApi.getText().equals("")){
				servicesImpl=IdeUtils.getInterfaceImplementors(project, comboServiceApi.getText());
				SasPlugin.getDefault().log("Services impls loaded are..."+servicesImpl);

				comboServiceImpl.removeAll();
				for (int i = 0; i < servicesImpl.size(); i++) {
					comboServiceImpl.add(servicesImpl.get(i).toString());
					comboServiceImpl.select(0);
					
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

		//Create the Controls for the service Name
		page.createLabel(toolkit, composite, "Service Name:");
		txtName = page.createText(toolkit, composite, "");
		td = new GridData(GridData.FILL_HORIZONTAL);
		td.widthHint=250;
		txtName.setLayoutData(td);
		
		
//		//Create the Controls for the service Name
//		page.createLabel(toolkit, composite, "Service Path:");
//		servicePath = page.createText(toolkit, composite, "");
//		td = new GridData(GridData.FILL_HORIZONTAL);
//		td.widthHint=250;
//		servicePath.setLayoutData(td);
//		servicePath.addListener(SWT.Modify, new Listener() {
//			public void handleEvent(Event e) {
//				
//				checkAddButton();
//			}
//		});

		//Create the controls for the service Class
		page.createLabel(toolkit, composite, "Service API:");
		this.comboServiceApi = new  CCombo(composite, SWT.BORDER | SWT.READ_ONLY);
		td = new GridData(GridData.FILL_HORIZONTAL);
		td.widthHint=250;
		this.comboServiceApi.setLayoutData(td);
		toolkit.adapt(this.comboServiceApi);
		comboServiceApi.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String api = comboServiceApi.getText();
				
				if(!api.equals("")){
				servicesImpl=IdeUtils.getInterfaceImplementors(project,api);
				SasPlugin.getDefault().log("Services impls loaded for selected interfcae are..."+servicesImpl);

				comboServiceImpl.removeAll();
				for (int i = 0; i < servicesImpl.size(); i++) {
					comboServiceImpl.add(servicesImpl.get(i).toString());
					
				}
				}
				checkAddButton();
			}
		});
		
		//Create the Controls for the service Load on startup...
		page.createLabel(toolkit, composite, "Annotated:");
		btnLOS = toolkit.createButton(composite, "", SWT.CHECK);

		//Create the controls for the service Class
		page.createLabel(toolkit, composite, "Service Impl:");
		this.comboServiceImpl = new CCombo(composite, SWT.BORDER | SWT.READ_ONLY);
		td = new GridData(GridData.FILL_HORIZONTAL);
		td.widthHint=250;
		this.comboServiceImpl.setLayoutData(td);
		toolkit.adapt(this.comboServiceImpl);
		comboServiceImpl.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				
				checkAddButton();
			}
		});

//		//Create the Controls for the service Name
//		page.createLabel(toolkit, composite, "Service Path:");
//		servicePath = page.createText(toolkit, composite, "");
//		td = new TableWrapData(TableWrapData.FILL_GRAB);
//		servicePath.setLayoutData(td);
//		servicePath.addListener(SWT.Selection, new Listener() {
//			public void handleEvent(Event e) {
//				
//				checkAddButton();
//			}
//		});

		//Create the controls for the service Class
		page.createLabel(toolkit, composite, "Notification API:");
		this.comboNotiApi = new CCombo(composite, SWT.BORDER | SWT.READ_ONLY);
		td.widthHint=250;
		this.comboNotiApi.setLayoutData(td);
		toolkit.adapt(this.comboNotiApi);
		
		comboNotiApi.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				
				checkAddButton();
			}
		});

		page.createEmptySpace(toolkit, composite, 1, BPFormPage.GRID_LAYOUT);
		btnAdd = toolkit.createButton(composite, "Add", SWT.FLAT);
		btnAdd.setEnabled(false);

		sl = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				String serviceName = txtName.getText();
				String serviceApiClass = comboServiceApi.getText();
				String serviceApiImplClass = comboServiceImpl.getText();
//				String path = servicePath.getText();
				String notiapi = comboNotiApi.getText();
				boolean los = btnLOS.getSelection();

				if ( serviceApiClass.trim().equals(""))//||path.trim().equals("")
					return;
				Document doc = page.getModel().getDocument();

				Element docElement = doc.getDocumentElement();
				Element service = page.getModel().getChild(docElement, SERVICE,
						false);

				//Remove the already existing service with same name as adding new service name
				if (service != null) {
					NodeList nList= service.getChildNodes();
					for(int i=0;i<nList.getLength();i++){
						Node node=nList.item(i);
						 if(node.getNodeName().equals(SERVICE_NAME)){
							 String name=node.getTextContent();
							 SasPlugin.getDefault().log("The Service node found is.."+nList.item(i).getNodeName()+ "The Node Value "+name   +"SeviceName is" +serviceName);
							if(name.equals(serviceName)){
								SasPlugin.getDefault().log("The Service with this Name already exists so removing old one");
								docElement.removeChild(service);	
							}
									
									
						 }
					}
					
					
				}

				service = doc.createElement(SERVICE);
				service.appendChild(doc.createTextNode("\n"));

				Element sname = doc.createElement(SERVICE_NAME);
				sname.appendChild(doc.createTextNode(serviceName));
				service.appendChild(sname);
				service.appendChild(doc.createTextNode("\n"));
				
				
//				Element sclass = doc.createElement(SERVICE_PATH);
//				sclass.appendChild(doc.createTextNode(path));
//				service.appendChild(sclass);
//				service.appendChild(doc.createTextNode("\n"));

				Element desc = doc.createElement(SERVICE_API);
				desc.appendChild(doc.createTextNode(serviceApiClass));
				service.appendChild(desc);
				service.appendChild(doc.createTextNode("\n"));

				if (los) {
					//	Attr elLoc = doc.createAttribute(ANNOTATED);
					desc.setAttribute(ANNOTATED, "true");
				} else {
					desc.setAttribute(ANNOTATED, "false");
				}

				Element dname = doc.createElement(SERVICE_IMPL);
				dname.appendChild(doc.createTextNode(serviceApiImplClass));
				service.appendChild(dname);
				service.appendChild(doc.createTextNode("\n"));

				

				Element api= doc.createElement(NOTIFICATION_API);
				api.appendChild(doc.createTextNode(notiapi));
				service.appendChild(api);
				service.appendChild(doc.createTextNode("\n"));
				
				
				txtName.setEnabled(true);
				comboServiceApi.setText("");
				btnLOS.setSelection(false);
				comboServiceImpl.setText("");
//				servicePath.setText("");
				comboNotiApi.setText("");
				txtName.setText("");
				page.getModel().addChild(doc.getDocumentElement(), service);

				btnAdd.setEnabled(false);
//				btnChange.setEnabled(true);
			}
		};
		btnAdd.addSelectionListener(sl);

//		this.btnChange = toolkit.createButton(composite, "Change", SWT.FLAT);
//		this.btnChange.setEnabled(false);
//		sl = new SelectionAdapter() {
//			public void widgetSelected(SelectionEvent e) {
//				txtName.setEnabled(true);
//				comboServiceApi.setEnabled(true);
//				btnLOS.setEnabled(true);
//				comboServiceImpl.setEnabled(true);
//				servicePath.setEnabled(true);
//				comboNotiApi.setEnabled(true);
//			}
//		};

		//fill the Service Section with the descriptor data if there is already one defined
//		loadServiceData();
	}

	private void checkAddButton() {
		String serviceApiClass = comboServiceApi.getText();
	//	String path = servicePath.getText();


		btnAdd.setEnabled(!serviceApiClass.trim().equals("")); //!path.trim().equals("")

	}

//	protected void loadServiceData() {
//
//		Element docElement = page.getModel().getDocument().getDocumentElement();
//		Element service = page.getModel().getChild(docElement, this.SERVICE,
//				false);
//
//		if (service != null) {
//			Element svcname = page.getModel().getChild(service,
//					this.SERVICE_NAME, false);
//			Element svcapi = page.getModel().getChild(service,
//					this.SERVICE_API, false);
//			Element svcimpl = page.getModel().getChild(service,
//					this.SERVICE_IMPL, false);
//			Element svcpath = page.getModel().getChild(service,
//					this.SERVICE_PATH, false);
//			Element notiApi = page.getModel().getChild(service,
//					this.NOTIFICATION_API, false);
//
//			String value = svcapi.getAttribute(this.ANNOTATED);
//
//			if (value.equals("true")) {
//				btnLOS.setSelection(true);
//			} else if (value.equals("false")) {
//				btnLOS.setSelection(false);
//			}
//
//			txtName.setText(page.getModel().getText(svcname));
//			comboServiceApi.setText(page.getModel().getText(svcapi));
//			comboServiceImpl.setText(page.getModel().getText(svcimpl));
//			servicePath.setText(page.getModel().getText(svcpath));
//			comboNotiApi.setText(page.getModel().getText(notiApi));
//
//			txtName.setEnabled(false);
//			comboServiceApi.setEnabled(false);
//			btnLOS.setEnabled(false);
//			comboServiceImpl.setEnabled(false);
//			servicePath.setEnabled(false);
//			comboNotiApi.setEnabled(false);
//
//			btnChange.setEnabled(true);
//
//		}
//
//	}

	private Text txtName;
	private CCombo comboServiceApi;
	private CCombo comboServiceImpl;
//	private Text servicePath;
	private CCombo comboNotiApi;
	private Button btnLOS;
	private Button btnAdd;
//	private Button btnChange;

}
