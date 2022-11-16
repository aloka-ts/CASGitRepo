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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.model.ModelListener;

public class SipProxyConfigSection extends SectionPart implements ModelListener {
	
	private static final String PROXY_CONFIG = "proxy-config".intern();
	private   String PROXY_SEQ_TIMEOUT = "sequential-search-timeout".intern();
	private   String PROXY_TIMEOUT = "proxy-timeout".intern();
	
	private BPFormPage page;
	private boolean isSip289XML=false;
	
	public SipProxyConfigSection(BPFormPage page, Composite parent,boolean isSip289) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		int tStyle =  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
		if(toolkit != null){
			tStyle |= toolkit.getBorderStyle();
		}
		isSip289XML =isSip289;
		if(!isSip289XML){
			PROXY_TIMEOUT=PROXY_SEQ_TIMEOUT;
		}
		
		//Create the UI.
		this.getSection().setText("Proxy Configuration");
		this.createControls(this.getSection(), toolkit);
		
		//Associate with the Model...
		page.getModel().addModelListener(this);
		this.loadContents();
	}
	
	protected void loadContents(){
		
		String str = null;
		
		str = page.getModel().getChildText(PROXY_TIMEOUT);
		this.txtProxyTimeout.setText(str);
		
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
		if(isSip289XML){
			page.createLabel(toolkit, composite, "Proxy Timeout (in minutes):");
		}else{
		    page.createLabel(toolkit, composite, "Sequential Search Timeout (in minutes):");
		}
		txtProxyTimeout = page.createText(toolkit, composite, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtProxyTimeout.setLayoutData(td);
		//reeta adding it
		txtProxyTimeout.addListener(SWT.Modify , new Listener() {
			public void handleEvent(Event e) {
				String str=txtProxyTimeout.getText();
				if(!str.trim().equals("")){
					try{
						Integer.parseInt(str.trim());
					}catch(NumberFormatException nfe){
						str = "";
						MessageDialog.openInformation(page.getSite().getShell(), "Invalid Value", "Sequential Search Timeout should be a Numeric value" );
					}
				}
				
			}
		});
		//
		BPFormControl ctrlTimeout =  new BPFormControl(txtProxyTimeout);
		BPFormListener listener = new BPFormListener(){
			public void textChanged(){
				try{
					String str = txtProxyTimeout.getText();
					if(!str.trim().equals("")){
						try{
							Integer.parseInt(str.trim());
						}catch(NumberFormatException nfe){
							str = "";
							//reeta commented it
						//	MessageDialog.openInformation(page.getSite().getShell(), "Invalid Value", "Sequential Search Timeout should be a Numeric value" );
						}
					}
					
					if(str.trim().equals("")){
						Element elConfig = page.getModel().getChild(PROXY_CONFIG, false);
						Element elTimeout = page.getModel().getChild(PROXY_TIMEOUT, false);
						if(elConfig != null && elTimeout != null){
							page.getModel().removeChild(elTimeout);
							page.getModel().removeChild(elConfig);
						}
						return;
					}
					
					Element elConfig = page.getModel().getChild(PROXY_CONFIG, true);			
					Element elTimeout  = page.getModel().getChild(elConfig, PROXY_TIMEOUT, true);
					page.getModel().setText(elTimeout, str, false);
				}catch(Exception ex){
					SasPlugin.getDefault().log("Exception thrown createControls() SipProxyConfigSection.java..."+ex);
				}
			}

			public void selectionChanged() {
			}
			
		};
		ctrlTimeout.setFormListener(listener);
	}
	
	public void modelChanged(int action, Node data) {
		if(data != null && data.getNodeName().equals(PROXY_TIMEOUT)){
			String str = page.getModel().getText((Element)data);
			this.txtProxyTimeout.setText(action != ModelListener.REMOVE ? str : "");
		}
		
	}
	
	public Text txtProxyTimeout;
}
