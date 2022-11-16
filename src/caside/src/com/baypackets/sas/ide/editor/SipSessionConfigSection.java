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

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.model.ModelListener;

public class SipSessionConfigSection extends SectionPart implements ModelListener {
	
	private static final String SESSION_CONFIG = "session-config".intern();
	private   String SESSION_TIMEOUT = "session-timeout".intern();
	private   String JAVAEE_SESSION_TIMEOUT = "javaee:session-timeout".intern();
	
	private BPFormPage page;
	private boolean isSip289Page=false;
	
	public SipSessionConfigSection(BPFormPage page, Composite parent,boolean isSip289) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		int tStyle =  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
		if(toolkit != null){
			tStyle |= toolkit.getBorderStyle();
		}
		
		if (!(page instanceof WebXMLPage) && isSip289) {
			isSip289Page = true;
			SESSION_TIMEOUT=JAVAEE_SESSION_TIMEOUT;
		}
		
		//Create the UI.
		this.getSection().setText("Application Session Configuration");
		this.createControls(this.getSection(), toolkit);
		
		//Associate with the Model...
		page.getModel().addModelListener(this);
		this.loadContents();
	}
	
	protected void loadContents(){
		
		String str = null;
		
		str = page.getModel().getChildText(SESSION_TIMEOUT);
		this.txtSessionTimeout.setText(str);
		
	}

	
	protected void createControls(Section section, FormToolkit toolkit){
	
		//Create a top level composite
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		//Associate this composite with the section.
		section.setClient(composite);
	
		TableWrapData td = null;
		//Create the Controls for the display name
		page.createLabel(toolkit, composite, "Session Timeout (in minutes):");
		txtSessionTimeout = page.createText(toolkit, composite, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtSessionTimeout.setLayoutData(td);
		//reeta added it
		txtSessionTimeout.addListener(SWT.Modify , new Listener() {
			public void handleEvent(Event e) {
				String str=txtSessionTimeout.getText();
				if(!str.trim().equals("")){
					try{
						Integer.parseInt(str.trim());
					}catch(NumberFormatException nfe){
						str = "";
						MessageDialog.openInformation(page.getSite().getShell(), "Invalid Value", "Session Timeout should be a Numeric value" );
					}
				}
				
			}
		});
		//
		BPFormControl ctrlTimeout =  new BPFormControl(txtSessionTimeout);
		BPFormListener listener = new BPFormListener(){
			public void textChanged(){
				try{
					String str = txtSessionTimeout.getText();
					if(!str.trim().equals("")){
						try{
							Integer.parseInt(str.trim());
						}catch(NumberFormatException nfe){
							str = "";
							//reeta commented it
							//MessageDialog.openInformation(page.getSite().getShell(), "Invalid Value", "Application Session Timeout should be a Numeric value" );
						}
					}
					
					if(str.trim().equals("")){
						Element elConfig = page.getModel().getChild(SESSION_CONFIG, false);
						Element elTimeout = page.getModel().getChild(SESSION_TIMEOUT, false);
						if(elConfig != null && elTimeout != null){
							page.getModel().removeChild(elTimeout);
							page.getModel().removeChild(elConfig);
						}
						return;
					}
					
					Element elConfig = page.getModel().getChild(SESSION_CONFIG, true);			
					Element elTimeout  = page.getModel().getChild(elConfig, SESSION_TIMEOUT, true);
					page.getModel().setText(elTimeout, str, false);
				}catch(Exception ex){
					SasPlugin.getDefault().log("Exception thrown createControls() SipSessionConfigSection.java..."+ex);
				}
			}

			public void selectionChanged() {
			}
			
		};
		ctrlTimeout.setFormListener(listener);
	}
	
	public void modelChanged(int action, Node data) {
		if(data != null && data.getNodeName().equals(SESSION_TIMEOUT)){
			String str = page.getModel().getText((Element)data);
			this.txtSessionTimeout.setText(action != ModelListener.REMOVE ? str : "");
		}
		
	}
	
	private Text txtSessionTimeout;
}
