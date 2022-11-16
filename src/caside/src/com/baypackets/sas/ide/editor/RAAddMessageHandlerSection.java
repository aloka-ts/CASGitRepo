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
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.model.ModelListener;
import com.baypackets.sas.ide.util.IdeUtils;

public class RAAddMessageHandlerSection extends SectionPart implements ModelListener {
	
	
	private BPFormPage page;
	
	private static final String MESSAGE_HANDLER="message-handler".intern();
	private static final String HANDLER_NAME="handler-name".intern();
	private static final String HANDLER_CLASS="handler-class".intern();
	private static final String MESSAGE_HANDLER_MAPPING="message-handler-mapping".intern();
	private static final String RESOURCE_NAME="resource-name".intern();
	

	
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
	private CCombo comboListener;
	
	public RAAddMessageHandlerSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		
		//Create the UI.
		this.getSection().setText("Message Handlers");
		this.createControls();
		
		this.loadContents();
		//Associate with the Model...
		page.getModel().addModelListener(this);
		
		
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
			listeners.clear();
			
			//Get the Class names...
			IdeUtils.getClassNames(project, SEARCH_PATTERN, listeners);
			
			//Add the listener names to the combo...
			comboListener.removeAll();
			comboListener.add("");
			for(int i=0; i<listeners.size();i++){
				comboListener.add(listeners.get(i).toString());
			}
				 
				 comboHandListener.add("");
				 comboResListener.add("");
						
				/*
				 * load the handler name and resource name defined already and select the first one to show in Combo box
				 */
				 NodeList servNodes= page.getModel().getDocument().getElementsByTagName(HANDLER_NAME);
						
						 if(servNodes!=null){
							for (int i = 0; i < servNodes.getLength(); i++) {
								 Node n=servNodes.item(i);
								 
								 if(comboHandListener.indexOf(n.getTextContent())==-1){
								   comboHandListener.add(n.getTextContent());
								 }
								
							}
						 }
						 servNodes= page.getModel().getDocument().getElementsByTagName(RESOURCE_NAME);
							
						 if(servNodes!=null){
							for (int i = 0; i < servNodes.getLength(); i++) {
								 Node n=servNodes.item(i);
								 
								 if(comboResListener.indexOf(n.getTextContent())==-1){
								   comboResListener.add(n.getTextContent());
								 }
								
							}
						 }
					 
						 servNodes= page.getModel().getDocument().getElementsByTagName(MESSAGE_HANDLER_MAPPING);
					      
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
									     
					 SasPlugin.getDefault().log("selecting CCCombos ");

			
		}catch(Exception e){
			SasPlugin.getDefault().log("Exception thrown loadContents() SasXMLPage.java..."+e);
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
	
		
		//Create the Controls for the listener class name
		page.createLabel(toolkit,composite, "Add Message Handler:");
		page.createEmptySpace(toolkit,composite, 1, BPFormPage.TABLE_LAYOUT);
		//Create the Controls for the display name
		page.createLabel(toolkit, composite, "Name:");
		txtMessageHandler = toolkit.createText(composite, "");
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtMessageHandler.setLayoutData(td);
//		page.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
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
		page.createLabel(toolkit, composite, "Message Handler:");
		comboListener = new CCombo(composite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		toolkit.adapt(comboListener);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		comboListener.setLayoutData(td);
//		page.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
		SelectionAdapter sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				String listenerName = comboListener.getText();
				btnAdd.setEnabled(!listenerName.trim().equals(""));
				
			}
		};
		comboListener.addSelectionListener(sl);
		
		
		page.createEmptySpace(toolkit, composite, 1, BPFormPage.TABLE_LAYOUT);
		btnAdd = toolkit.createButton(composite, "Add", SWT.FLAT);
//		page.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
		btnAdd.setEnabled(false);
		
		sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				
				String handlerClass = comboListener.getText();
				String handlerName=txtMessageHandler.getText();
				
				if(handlerClass.trim().equals("")|| handlerName.trim().equals(""))
					return;
				
				Document doc = page.getModel().getDocument();
				
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
	                 
	                 
	                 /*
	                  * get the handler name of existing message handler
	                  */
	                 String existingHandlerName="";
	                 servNodes=existingParentNode.getChildNodes();
	                 
	                 if (servNodes != null) {

							for (int i = 0; i < servNodes.getLength(); i++) {

								Node messageHandler = servNodes.item(i);
														
									if (messageHandler.getNodeName().equals(
											HANDLER_NAME)) {		
										existingHandlerName=messageHandler.getTextContent();
											break;
									}								
							}
						}
	                 
	                 /*
	                  * Remove the existing message handler and replace with new 
	                  */
	                 page.getModel().removeChild(existingParentNode);
	                 
	                 /*
	                  * remove the mapping of the existing the handler name which is given new name
	                  */   	                                  
	                 servNodes= doc.getElementsByTagName(MESSAGE_HANDLER_MAPPING);
	                 
	             	ArrayList<Node> needToRemove= new ArrayList<Node>();
					if (servNodes != null) {

						for (int i = 0; i < servNodes.getLength(); i++) {

							Node messageHandler = servNodes.item(i);
							
							for (int j = 0; j < messageHandler.getChildNodes()
									.getLength(); j++) {								
								Node messageHandChild = messageHandler
										.getChildNodes().item(j);
								if (messageHandChild.getNodeName().equals(
										HANDLER_NAME)) {		
									if (existingHandlerName.equals(messageHandChild
											.getTextContent())) {	
										 SasPlugin.getDefault().log("Mapping with old Handler for this class already exists  ");
										 needToRemove.add(messageHandler);
										 page.getModel().setText(messageHandChild, handlerName, false);//messageHandChild.setNodeValue(handlerName);
									}

								}
							}
						}
						
						for(Node n:needToRemove){
							 SasPlugin.getDefault().log("RAAddMessageHandlerSection modelChanged() Removing Handler Mapping" +n.getTextContent());
							page.getModel().removeChild(n);
						}
					}

                 }
                
                 /*
                  * Add new handler 
                  */
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

//				if(firstServNode!=null){
//					/* page.getModel().insertBefore(doc.getDocumentElement(),doc.createTextNode("\n"), firstServNode);	*/
//					 getModel().insertBefore(doc.getDocumentElement(),listener, firstServNode);				
//				}else{
					page.getModel().addChild(doc.getDocumentElement(), listener);
			//	}
				
			    txtMessageHandler.setText("");
				comboListener.setText("");
				btnAdd.setEnabled(false);
			}
		};
		btnAdd.addSelectionListener(sl);
		
		
		//add resource and handler mappings
//		
//		//Create the Controls for the listener class name
		page.createLabel(toolkit, composite, "Add Message Handler Mapping:");
		page.createEmptySpace(toolkit, composite, 1, BPFormPage.TABLE_LAYOUT);
	//	Create the Controls for the display name
		page.createLabel(toolkit, composite, "Handler Name:");
		comboHandListener = new CCombo(composite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		toolkit.adapt(comboHandListener);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		comboHandListener.setLayoutData(td);
//		page.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
		sl = new SelectionAdapter(){

			public void widgetSelected(SelectionEvent e) {
				String listenerName = comboHandListener.getText();
				btnAddMapping.setEnabled(!listenerName.trim().equals(""));
				
			}
		};
		comboHandListener.addSelectionListener(sl);
		
		//Create the Controls for the listener class name
		page.createLabel(toolkit, composite, "Resource Name:");
		comboResListener = new CCombo(composite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		toolkit.adapt(comboResListener);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		comboResListener.setLayoutData(td);
//		page.createEmptySpace(toolkit, composite, 2, TABLE_LAYOUT);
		sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				String listenerName = comboResListener.getText();
				btnAddMapping.setEnabled(!listenerName.trim().equals(""));
				

			}
		};
		comboResListener.addSelectionListener(sl);
	
		
		page.createEmptySpace(toolkit, composite, 1, BPFormPage.TABLE_LAYOUT);
		btnAddMapping = toolkit.createButton(composite, "Add", SWT.FLAT);
		btnAddMapping.setEnabled(false);
		
		sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				
				String resourceName = comboResListener.getText();
				String handlerName=comboHandListener.getText();
				
				if(resourceName.trim().equals(""))
					return;
				
				Document doc = page.getModel().getDocument();
				
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
									 SasPlugin.getDefault().log("Resource name already exits rremove mapping" +messageHandChild.getTextContent());
									  page.getModel().removeChild(messageHandler);
									break;
								}

							}
						}
					}
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

//				if(firstServNode!=null){
//					/* page.getModel().insertBefore(doc.getDocumentElement(),doc.createTextNode("\n"), firstServNode);	*/
//					 getModel().insertBefore(doc.getDocumentElement(),listener, firstServNode);				
//				}else{
					page.getModel().addChild(doc.getDocumentElement(), listener);
			//	}
				
			//	comboListener.setText("");
					btnAddMapping.setEnabled(false);
			}
		};
		btnAddMapping.addSelectionListener(sl);
		

	}
	
	
	public void modelChanged(int action, Node data) {
		
		if(data != null 
				&& ( data.getNodeName().equals(MESSAGE_HANDLER)
						|| (data.getParentNode() != null 
								&& data.getParentNode().getNodeName().equals(MESSAGE_HANDLER) ) )){ 
				String handlerName="";
				for (int j = 0; j < data.getChildNodes()
						.getLength(); j++) {								
					Node messageHandChild = data
							.getChildNodes().item(j);
					
					  if (messageHandChild.getNodeName().equals(
								HANDLER_NAME)) {
							 SasPlugin.getDefault().log("RAAddMessageHandlerSection modelChanged() Handler name is" +messageHandChild.getTextContent());
							 handlerName=messageHandChild
									.getTextContent();
							 /**
								 * Update hander name for mapping control as well
								 */
					if (action == ModelListener.REMOVE) {
						if (comboHandListener.indexOf(handlerName) != -1){
							comboHandListener.remove(handlerName);
							comboHandListener.setText("");
						}
					} else {

						if (comboHandListener.indexOf(handlerName) == -1) {
							comboHandListener.add(handlerName);
						}

					}
						}
				}
		}
	}

}
