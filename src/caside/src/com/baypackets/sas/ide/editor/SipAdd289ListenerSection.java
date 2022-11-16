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
import com.baypackets.sas.ide.util.IdeUtils;

public class SipAdd289ListenerSection extends SectionPart {
	
	public static final String LISTENER = "listener".intern();
	public static final String LISTENER_CLASS = "javaee:listener-class".intern();
	private static String SERVLET = "servlet".intern();
	
	/**
	private static final String[] INTERFACES = new String[] {
		"javax.servlet.sip.TimerListener".intern() };
		**/
	
	private static final String[] INTERFACES = new String[] {
		"javax.servlet.sip.SipApplicationSessionListener".intern(),
		"javax.servlet.sip.SipSessionListener".intern(),
		"javax.servlet.sip.SipSessionAttributeListener".intern(),
		"javax.servlet.sip.SipErrorListener".intern(),
		"javax.servlet.sip.TimerListener".intern(),
		"javax.servlet.ServletContextListener".intern(),
		"javax.servlet.ServletContextAttributeListener".intern(),
		"javax.servlet.ServletRequestListener".intern(),
		"javax.servlet.ServletRequestAttributeListener".intern()
	};
	
	private static SearchPattern SEARCH_PATTERN = null;
	static{
	
		SearchPattern[] patterns = new SearchPattern[INTERFACES.length];
		for(int i=0; i<INTERFACES.length;i++){
			patterns[i] = SearchPattern.createPattern(INTERFACES[i], 
					IJavaSearchConstants.CLASS,
					IJavaSearchConstants.IMPLEMENTORS,
					SearchPattern.R_EXACT_MATCH);
		}
		
		if(patterns.length > 0){
			SearchPattern pattern = patterns[0];
			for(int i=1; i<patterns.length;i++){
				pattern = SearchPattern.createOrPattern(pattern, patterns[i]);
			}
			SEARCH_PATTERN = pattern;
		}
	}
	
	private BPFormPage page;
	private ArrayList listeners = new ArrayList();
	
	public SipAdd289ListenerSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		
		//Create the UI.
		this.getSection().setText("Add Listener");
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
			listeners.clear();
			
			//Get the Class names...
			IdeUtils.getClassNames(project, SEARCH_PATTERN, listeners);
			
			//Add the listener names to the combo...
			comboListener.removeAll();
			comboListener.add("");
			for(int i=0; i<listeners.size();i++){
				comboListener.add(listeners.get(i).toString());
			}
			
		}catch(Exception e){
			SasPlugin.getDefault().log("Exception thrown loadContents() SipAddListenerSection.java..."+e);
		}
	}
	
	protected void createControls(){
	
		Section section = this.getSection();
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		SelectionListener sl = null;
		
		//Create a top level composite
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		//Associate this composite with the section.
		section.setClient(composite);
	
		TableWrapData td = null;
		
		//Create the Controls for the listener class name
		page.createLabel(toolkit, composite, "Listener Class:");
		comboListener = new CCombo(composite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		toolkit.adapt(comboListener);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		comboListener.setLayoutData(td);
		sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				String listenerName = comboListener.getText();
				btnAdd.setEnabled(!listenerName.trim().equals(""));
			}
		};
		comboListener.addSelectionListener(sl);
				
		page.createEmptySpace(toolkit, composite, 1, BPFormPage.TABLE_LAYOUT);
		btnAdd = toolkit.createButton(composite, "Add", SWT.FLAT);
		btnAdd.setEnabled(false);
		
		sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				
				String listenerName = comboListener.getText();
				if(listenerName.trim().equals(""))
					return;
				
				Document doc = page.getModel().getDocument();
				
                 NodeList servNodes= doc.getElementsByTagName(SERVLET);
                 Node firstServNode=null;
                 
                 if(servNodes!=null && servNodes.getLength()!=0){
                	 firstServNode= servNodes.item(0);
                 }
                         
				Element listener = doc.createElement(LISTENER);
				listener.appendChild(doc.createTextNode("\n"));
				
				Element lclass = doc.createElement(LISTENER_CLASS);
				lclass.appendChild(doc.createTextNode(listenerName));
				listener.appendChild(lclass);
				listener.appendChild(doc.createTextNode("\n"));

				if(firstServNode!=null){
					/* page.getModel().insertBefore(doc.getDocumentElement(),doc.createTextNode("\n"), firstServNode);	*/
					 page.getModel().insertBefore(doc.getDocumentElement(),listener, firstServNode);				
				}else{
					page.getModel().addChild(doc.getDocumentElement(), listener);
				}
				
				comboListener.setText("");
				btnAdd.setEnabled(false);
			}
		};
		btnAdd.addSelectionListener(sl);
	}
	
	private CCombo comboListener;
	private Button btnAdd;
}
