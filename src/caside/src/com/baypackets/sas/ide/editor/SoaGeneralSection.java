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
import java.awt.Event;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
//import org.eclipse.ui.forms.widgets.TableWrapData;
//import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.model.ModelListener;
public class SoaGeneralSection extends SectionPart implements ModelListener{
	private static final String DISPLAY_NAME = "display-name".intern();
	private static final String DESCRIPTION = "description".intern();
	
	private BPFormPage page;
	
	public SoaGeneralSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		int tStyle =  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
		if(toolkit != null){
			tStyle |= toolkit.getBorderStyle();
		}
		
		//Create the UI.
		this.getSection().setText("General Information");
		
		this.createControls(this.getSection(), toolkit);
		
		//Associate with the Model...
		page.getModel().addModelListener(this);
		//this.loadContents();
	}
	
	protected void loadContents(){
		
		String str = null;
		
		str = page.getModel().getChildText(DISPLAY_NAME);
		this.txtDisplayName.setText(str);
		
		str = page.getModel().getChildText(DESCRIPTION);
		this.txtDescription.setText(str);
		
	}

	
	protected void createControls(Section section, FormToolkit toolkit){
	
		//Create a top level composite
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		//Associate this composite with the section.
		section.setClient(composite);
	
		GridData td = null;
		//Create the Controls for the display name
		page.createLabel(toolkit, composite, "Display Name:");
		txtDisplayName = page.createText(toolkit, composite, "");
		td = new GridData(GridData.FILL_HORIZONTAL);
		td.widthHint=285;
		txtDisplayName.setLayoutData(td);	
		
		String str = page.getModel().getChildText(DISPLAY_NAME);
		this.txtDisplayName.setText(str);
		
		txtDisplayName.addListener(SWT.Modify, new Listener(){
			public void handleEvent(org.eclipse.swt.widgets.Event event){
				checkUpdateButton();
			}
		});
		
		
		
	
		
		page.createLabel(toolkit, composite, "Description:");
		txtDescription = page.createText(toolkit, composite, "");
		td = new GridData(GridData.FILL_HORIZONTAL);
		td.widthHint=285;
		txtDescription.setLayoutData(td);
		
		str = page.getModel().getChildText(DESCRIPTION);
		this.txtDescription.setText(str);
		
		txtDescription.addListener(SWT.Modify, new Listener(){
			public void handleEvent(org.eclipse.swt.widgets.Event event){
				checkUpdateButton();
			}
		});
		
		page.createEmptySpace(toolkit, composite, 1, BPFormPage.GRID_LAYOUT);
		btnAdd = toolkit.createButton(composite, "update", SWT.FLAT);
		btnAdd.setEnabled(false);	
		btnAdd.addListener(SWT.Selection, new Listener(){
			public void handleEvent(org.eclipse.swt.widgets.Event event){
				try{
					String str = txtDescription.getText();
					Element docElement = page.getModel().getDocument().getDocumentElement();

					Element element = page.getModel().getChild(docElement,DESCRIPTION,
							true);
					page.getModel().setText(element, str, true);	
					
					str = txtDisplayName.getText();
				    element = page.getModel().getChild(docElement,DISPLAY_NAME,
							true);
						page.getModel().setText(element, str, true);
					
				}catch(Exception ex){
					SasPlugin.getDefault().log("Exception thrown Description control handleEvent() SoaGeneralSection.java..."+ex);
				}
			}
		});
		
	}
	
	
	private void checkUpdateButton(){
		String str = txtDisplayName.getText();
		String str1 = txtDescription.getText();
		if(!str.equals("")||!str1.equals("")){
			btnAdd.setEnabled(true);
		}
	}
	
	//doing nothing
	public void modelChanged(int action, Node data) {

	}
	
	private Text txtDisplayName;
	private Text txtDescription;
	private Button btnDistributable;
	private Button btnAdd;

}
