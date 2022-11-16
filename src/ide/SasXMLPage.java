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
	
	private static final String MESSAGE_HANDLER="message-handler".intern();
	private static final String HANDLER_NAME="handler-name".intern();
	private static final String HANDLER_CLASS="handler-class".intern();
	private static final String MESSAGE_HANDLER_MAPPING="message-handler-mapping".intern();
	private static final String RESOURCE_NAME="resource-name".intern();
	
	private static Element resFactoryMappig=null;
	
	private static SearchPattern SEARCH_PATTERN = null;
	
	
	private static final String INTERFACES ="com.baypackets.ase.resource.MessageHandler".intern();

	static{	
			SEARCH_PATTERN = SearchPattern.createPattern(INTERFACES, 
					IJavaSearchConstants.CLASS,
					IJavaSearchConstants.IMPLEMENTORS,
					SearchPattern.R_EXACT_MATCH);		
	}
	
	private ArrayList listeners = new ArrayList();
	private Text txtMessageHandler;
	private Button btnAdd;
	private CCombo comboHandListener;
	private CCombo comboResListener;
	protected Button btnAddMapping;
	private Button btnRemoveMapping;
	
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
		
        try{
			
			//get the file input associated with this file.... 
			IEditorInput input = this.getEditorInput();
			if(!(input instanceof FileEditorInput))
				return;
			
			//get the project assciated with this file...
			IProject project = ((FileEditorInput)input).getFile().getProject();
			
			//clear the listener list....
			listeners.clear();
			
			//Get the Class names...
			IdeUtils.getClassNames(project, SEARCH_PATTERN, listeners);
			
			//Add the listener names to the combo...
			comboListener.removeAll();
			comboListener.add("");
			for(int i=0; i<listeners.size();i++){
				comboListener.add(listeners.get(i).toString());
			}
			
		    NodeList servNodes= getModel().getDocument().getElementsByTagName(MESSAGE_HANDLER);
            
				if (servNodes != null&&servNodes.getLength()>0) {

					Node firstMessageHandler = servNodes.item(0);
					for (int i = 0; i < firstMessageHandler.getChildNodes().getLength(); i++) {
						Node messageHandChild=firstMessageHandler.getChildNodes().item(i);
						
					if (messageHandChild.getNodeName().equals(HANDLER_NAME)) {
						txtMessageHandler.setText(messageHandChild.getTextContent());
					}
					if (messageHandChild.getNodeName().equals(HANDLER_CLASS)) {
						comboListener.select(comboListener
								.indexOf(messageHandChild.getTextContent()));

					}
					}
			   }
				
				 
				 comboHandListener.add("");
	//			 comboResListener.add("");
						
				/*
				 * load the handler name and resource name defined already and select the first one to show in Combo box
				 */
						 servNodes= getModel().getDocument().getElementsByTagName(HANDLER_NAME);
						
						 if(servNodes!=null){
							for (int i = 0; i < servNodes.getLength(); i++) {
								 Node n=servNodes.item(i);
								 
								 if(comboHandListener.indexOf(n.getTextContent())==-1){
								   comboHandListener.add(n.getTextContent());
								 }
								
							}
						 }
						 servNodes= getModel().getDocument().getElementsByTagName(RESOURCE_NAME);
							
						 if(servNodes!=null){
							for (int i = 0; i < servNodes.getLength(); i++) {
								 Node n=servNodes.item(i);
								 
								 if(comboResListener.indexOf(n.getTextContent())==-1){
								   comboResListener.add(n.getTextContent());
								 }
								
							}
						 }
					 
						 servNodes= getModel().getDocument().getElementsByTagName(MESSAGE_HANDLER_MAPPING);
					      
							if (servNodes != null &&servNodes.getLength()>0) {

									Node messageHandler = servNodes.item(0);
									for (int j = 0; j < messageHandler.getChildNodes()
											.getLength(); j++) {								
										Node messageHandChild = messageHandler
												.getChildNodes().item(j);
										
										  if (messageHandChild.getNodeName().equals(
													RESOURCE_NAME)) {
												 SasPlugin.getDefault().log("Loading Resource name " +messageHandChild.getTextContent());
												 comboResListener.select(comboResListener.indexOf(messageHandChild
														.getTextContent()));
											}
										if (messageHandChild.getNodeName().equals(
												HANDLER_NAME)) {
											 SasPlugin.getDefault().log("Loading Handler name  " +messageHandChild.getTextContent());															
												comboHandListener.select(comboHandListener.indexOf(messageHandChild
														.getTextContent()));
												

										}
									}	
							}else{
								
											comboHandListener.select(0);
											comboResListener.select(0);
							}
							
							
							String resourceName = comboResListener.getText();
							String handlerName=comboHandListener.getText();
							
							if(resourceName.trim().equals("")||handlerName.equals(""))
							     btnRemoveMapping.setEnabled(false);
							else
								btnRemoveMapping.setEnabled(true);
									     
					 SasPlugin.getDefault().log("selecting CCCombos ");

			
		}catch(Exception e){
			SasPlugin.getDefault().log("Exception thrown loadContents() SasXMLPage.java..."+e);
		}
		}
	}
	
	private void createBody(IManagedForm managedForm) {
	
		FormToolkit toolkit = managedForm.getToolkit();
//		Composite body = managedForm.getForm().getBody();
//
//		TableWrapLayout layout = new TableWrapLayout();
//		layout.bottomMargin = 10;
//		layout.topMargin = 5;
//		layout.leftMargin = 10;
//		layout.rightMargin = 10;
//		layout.numColumns = 4;
//		layout.horizontalSpacing = 4;
//		body.setLayout(layout);
		

		Composite body = managedForm.getForm().getBody();
		TableWrapLayout layout = new TableWrapLayout();
		layout.bottomMargin = 5;
		layout.topMargin = 5;
		layout.leftMargin = 5;
		layout.rightMargin = 5;
		layout.numColumns = 2;
		layout.horizontalSpacing = 5;
		body.setLayout(layout);
		
		Section section = this.createStaticSection( toolkit, body, "Application Information");
		Composite container = toolkit.createComposite(section, SWT.NONE);
		this.createControls(container, toolkit);
		
		RAMessageHandlerSection rasec= new RAMessageHandlerSection(this, body);
		managedForm.addPart(rasec);

		section.setClient(container);
		section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
	}
	
	private void createControls(Composite composite, FormToolkit toolkit){
	//	System.out.println("Inside create Controls....");
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);
		
		//Create the Controls for the application name
		super.createLabel(toolkit, composite, "Name:");
		txtName =  super.createText(toolkit, composite, "");
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtName.setLayoutData(td);
		
		super.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);

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
		super.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
		
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
		super.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);

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
		super.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
		
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
		
		
		
		if(resFactoryMappig!=null){
			
	/*		  <message-handler>
		        <handler-name>CentrexSIPServlet1</handler-name>
		        <handler-class>com.agnity.service.centrex.servlet.CentrexSIPServlet</handler-class>
		    </message-handler>
			
		    <message-handler-mapping>
		        <handler-name>CentrexSIPServlet1</handler-name>
		        <resource-name>ro-ra</resource-name>
		    </message-handler-mapping>*/
			
			//Create the Controls for the listener class name
			super.createLabel(toolkit, composite, "Add Message Handler:");
			super.createEmptySpace(toolkit, composite, 3, TABLE_LAYOUT);
			//Create the Controls for the display name
			this.createLabel(toolkit, composite, "Name:");
			txtMessageHandler = super.createText(toolkit, composite, "");
			td = new TableWrapData(TableWrapData.FILL_GRAB);
			txtMessageHandler.setLayoutData(td);
			super.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
			//reeta added it
			txtMessageHandler.addListener(SWT.Modify , new Listener() {
				public void handleEvent(Event e) {
					String str=txtMessageHandler.getText();
					btnAdd.setEnabled(!str.trim().equals(""));
/*					if(!str.trim().equals("")){
						
					}*/
				}
			});
			
			//Create the Controls for the listener class name
			super.createLabel(toolkit, composite, "Message Handler:");
			comboListener = new CCombo(composite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
			toolkit.adapt(comboListener);
			td = new TableWrapData(TableWrapData.FILL_GRAB);
			comboListener.setLayoutData(td);
			super.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
			SelectionAdapter sl = new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					String listenerName = comboListener.getText();
					btnAdd.setEnabled(!listenerName.trim().equals(""));
					
					  NodeList servNodes= getModel().getDocument().getElementsByTagName(MESSAGE_HANDLER);
			            
					if (servNodes != null) {

						for (int i = 0; i < servNodes.getLength(); i++) {

							Node messageHandler = servNodes.item(i);
							String handlerName="";
							for (int j = 0; j < messageHandler.getChildNodes()
									.getLength(); j++) {								
								Node messageHandChild = messageHandler
										.getChildNodes().item(j);
								
								  if (messageHandChild.getNodeName().equals(
											HANDLER_NAME)) {
										 SasPlugin.getDefault().log("Handler name is node name " +messageHandChild.getTextContent());
										 handlerName=messageHandChild
												.getTextContent();
									}
								if (messageHandChild.getNodeName().equals(
										HANDLER_CLASS)) {
									 SasPlugin.getDefault().log("Handler class is node name " +messageHandChild.getTextContent());
									if (listenerName.equals(messageHandChild
											.getTextContent())) {									
										txtMessageHandler.setText(handlerName);
									}

								}
							}
						}
					}
				}
			};
			comboListener.addSelectionListener(sl);
			
			
			super.createEmptySpace(toolkit, composite, 1, BPFormPage.TABLE_LAYOUT);
			btnAdd = toolkit.createButton(composite, "Add", SWT.FLAT);
			super.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
			btnAdd.setEnabled(false);
			
			sl = new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					
					String handlerClass = comboListener.getText();
					String handlerName=txtMessageHandler.getText();
					
					if(handlerClass.trim().equals(""))
						return;
					
					Document doc = getModel().getDocument();
					
	                 NodeList servNodes= doc.getElementsByTagName(HANDLER_CLASS);
	                 Node existingParentNode=null;
	                 
					if (servNodes != null) {

						for (int i = 0; i < servNodes.getLength(); i++) {
							if (servNodes.item(i).getTextContent().equals(handlerClass)) {
								existingParentNode = servNodes.item(i)
										.getParentNode();

							}
						}
					}
   
	                 if(existingParentNode!=null){		
		                 SasPlugin.getDefault().log("The Handler for this class already exists  " +existingParentNode);
		                 
		                 doc.getDocumentElement().removeChild(existingParentNode);
	                 }
	                 
	                 Element listener = doc.createElement(MESSAGE_HANDLER);      
					listener.appendChild(doc.createTextNode("\n"));
					
					Element lclass = doc.createElement(HANDLER_NAME);
					lclass.appendChild(doc.createTextNode(handlerName));
					listener.appendChild(lclass);
					listener.appendChild(doc.createTextNode("\n"));
					
					lclass = doc.createElement(HANDLER_CLASS);
					lclass.appendChild(doc.createTextNode(handlerClass));
					listener.appendChild(lclass);
					listener.appendChild(doc.createTextNode("\n"));

//					if(firstServNode!=null){
//						/* page.getModel().insertBefore(doc.getDocumentElement(),doc.createTextNode("\n"), firstServNode);	*/
//						 getModel().insertBefore(doc.getDocumentElement(),listener, firstServNode);				
//					}else{
						getModel().addChild(doc.getDocumentElement(), listener);
				//	}
					
				//	comboListener.setText("");
					btnAdd.setEnabled(false);
				}
			};
			btnAdd.addSelectionListener(sl);
			
			
			//add resource and handler mappings
//			
//			//Create the Controls for the listener class name
			super.createLabel(toolkit, composite, "Add Message Handler Mapping:");
			super.createEmptySpace(toolkit, composite, 3, TABLE_LAYOUT);
		//	Create the Controls for the display name
			this.createLabel(toolkit, composite, "Handler Name:");
			comboHandListener = new CCombo(composite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
			toolkit.adapt(comboHandListener);
			td = new TableWrapData(TableWrapData.FILL_GRAB);
			comboHandListener.setLayoutData(td);
			super.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
			sl = new SelectionAdapter(){

				public void widgetSelected(SelectionEvent e) {
					String listenerName = comboHandListener.getText();
					btnAddMapping.setEnabled(!listenerName.trim().equals(""));
					
					  NodeList servNodes= getModel().getDocument().getElementsByTagName(MESSAGE_HANDLER_MAPPING);
			            
						if (servNodes != null) {

							for (int i = 0; i < servNodes.getLength(); i++) {

								Node messageHandler = servNodes.item(i);
								String handlerName="";
								for (int j = 0; j < messageHandler.getChildNodes()
										.getLength(); j++) {								
									Node messageHandChild = messageHandler
											.getChildNodes().item(j);
									
									  if (messageHandChild.getNodeName().equals(
												RESOURCE_NAME)) {
											 SasPlugin.getDefault().log("Resource name is node name " +messageHandChild.getTextContent());
											 handlerName=messageHandChild
													.getTextContent();
										}
									if (messageHandChild.getNodeName().equals(
											HANDLER_NAME)) {
										 SasPlugin.getDefault().log("Handler name is node name " +messageHandChild.getTextContent());
										if (listenerName.equals(messageHandChild
												.getTextContent())) {									
											comboResListener.setText(handlerName);
											break;
										}

									}
								}
							}
						}
					
					
					
				}
			};
			comboHandListener.addSelectionListener(sl);
			
			//Create the Controls for the listener class name
			super.createLabel(toolkit, composite, "Resource Name:");
			comboResListener = new CCombo(composite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
			toolkit.adapt(comboResListener);
			td = new TableWrapData(TableWrapData.FILL_GRAB);
			comboResListener.setLayoutData(td);
			super.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
			sl = new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					String listenerName = comboResListener.getText();
					btnAddMapping.setEnabled(!listenerName.trim().equals(""));
					
					  NodeList servNodes= getModel().getDocument().getElementsByTagName(MESSAGE_HANDLER_MAPPING);
			            
					if (servNodes != null) {

						for (int i = 0; i < servNodes.getLength(); i++) {

							Node messageHandler = servNodes.item(i);
							String handlerName="";
							for (int j = 0; j < messageHandler.getChildNodes()
									.getLength(); j++) {								
								Node messageHandChild = messageHandler
										.getChildNodes().item(j);
								
								  if (messageHandChild.getNodeName().equals(
											HANDLER_NAME)) {
										 SasPlugin.getDefault().log("Handler name is node name " +messageHandChild.getTextContent());
										 handlerName=messageHandChild
												.getTextContent();
									}
								if (messageHandChild.getNodeName().equals(
										RESOURCE_NAME)) {
									 SasPlugin.getDefault().log("Resource name is node name " +messageHandChild.getTextContent());
									if (listenerName.equals(messageHandChild
											.getTextContent())) {									
										comboHandListener.setText(handlerName);
										break;
									}

								}
							}
						}
					}
				}
			};
			comboResListener.addSelectionListener(sl);
		
			
			super.createEmptySpace(toolkit, composite, 1, BPFormPage.TABLE_LAYOUT);
			btnAddMapping = toolkit.createButton(composite, "Add", SWT.FLAT);
			btnAddMapping.setEnabled(false);
			
			sl = new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					
					String resourceName = comboResListener.getText();
					String handlerName=comboHandListener.getText();
					
					if(resourceName.trim().equals(""))
						return;
					
					Document doc = getModel().getDocument();
					
	                 NodeList servNodes= doc.getElementsByTagName(MESSAGE_HANDLER_MAPPING);
	                 Node existingParentNode=null;
	                 

					if (servNodes != null) {

						for (int i = 0; i < servNodes.getLength(); i++) {

							Node messageHandler = servNodes.item(i);
							
							for (int j = 0; j < messageHandler.getChildNodes()
									.getLength(); j++) {								
								Node messageHandChild = messageHandler
										.getChildNodes().item(j);
								if (messageHandChild.getNodeName().equals(
										RESOURCE_NAME)) {		
									if (resourceName.equals(messageHandChild
											.getTextContent())) {	
										 SasPlugin.getDefault().log("Resource name already exits " +messageHandChild.getTextContent());
										existingParentNode=messageHandler;
										break;
									}

								}
							}
						}
					}
   
	                 if(existingParentNode!=null){		
		                 SasPlugin.getDefault().log("The Handler for this class already exists  " +existingParentNode);
		                 
		                 doc.getDocumentElement().removeChild(existingParentNode);
	                 }
	                 
	                 Element listener = doc.createElement(MESSAGE_HANDLER_MAPPING);      
					listener.appendChild(doc.createTextNode("\n"));
					
					Element lclass = doc.createElement(HANDLER_NAME);
					lclass.appendChild(doc.createTextNode(handlerName));
					listener.appendChild(lclass);
					listener.appendChild(doc.createTextNode("\n"));
					
					lclass = doc.createElement(RESOURCE_NAME);
					lclass.appendChild(doc.createTextNode(resourceName));
					listener.appendChild(lclass);
					listener.appendChild(doc.createTextNode("\n"));

//					if(firstServNode!=null){
//						/* page.getModel().insertBefore(doc.getDocumentElement(),doc.createTextNode("\n"), firstServNode);	*/
//						 getModel().insertBefore(doc.getDocumentElement(),listener, firstServNode);				
//					}else{
						getModel().addChild(doc.getDocumentElement(), listener);
				//	}
					
				//	comboListener.setText("");
						btnAddMapping.setEnabled(false);
				}
			};
			btnAddMapping.addSelectionListener(sl);
			
//			super.createEmptySpace(toolkit, composite, 1, BPFormPage.TABLE_LAYOUT);
				btnRemoveMapping = toolkit.createButton(composite, "Remove", SWT.FLAT);
				
				sl = new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e) {
						
						String resourceName = comboResListener.getText();
						String handlerName=comboHandListener.getText();
						
						if(resourceName.trim().equals(""))
							return;
						
						Document doc = getModel().getDocument();
						
		                 NodeList servNodes= doc.getElementsByTagName(MESSAGE_HANDLER_MAPPING);
		                 Node existingParentNode=null;
		                 

						if (servNodes != null) {

							for (int i = 0; i < servNodes.getLength(); i++) {

								Node messageHandler = servNodes.item(i);
								
								for (int j = 0; j < messageHandler.getChildNodes()
										.getLength(); j++) {								
									Node messageHandChild = messageHandler
											.getChildNodes().item(j);
									if (messageHandChild.getNodeName().equals(
											RESOURCE_NAME)) {		
										if (resourceName.equals(messageHandChild
												.getTextContent())) {	
											 SasPlugin.getDefault().log("Resource name already exits " +messageHandChild.getTextContent());
											existingParentNode=messageHandler;
											break;
										}
                                     
									}
								}
							}
						}
	   
		                 if(existingParentNode!=null){		
			                 SasPlugin.getDefault().log("The Handler for this class already exists  " +existingParentNode);
			                 
			                 doc.getDocumentElement().removeChild(existingParentNode);
		                 }             
							btnRemoveMapping.setEnabled(false);
					}
				};
				btnRemoveMapping.addSelectionListener(sl);
		}
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
	private CCombo comboListener;
}
