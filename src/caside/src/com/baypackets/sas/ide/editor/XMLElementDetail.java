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

import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;

import com.baypackets.sas.ide.SasPlugin;

public class XMLElementDetail extends AbstractFormPart implements IDetailsPage{

	private BPFormPage page;
	
	public XMLElementDetail(BPFormPage page) {
		super();
		this.page = page;
	}

	public void createContents(Composite parent) {
		this.createDetailContents(parent);
	}

	public void selectionChanged(IFormPart part, ISelection selection) {
		
		if(!(selection instanceof IStructuredSelection))
			return;
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		if(obj instanceof Element){
			this.element = null;
			Element tmp = (Element)obj;
			if(page.getModel().isCData(tmp.getTagName())){
				this.txtContents.setEnabled(true);
				this.txtContents.setText(page.getModel().getText(tmp));
			}else{
				this.txtContents.setEnabled(false);
				this.txtContents.setText("");
			}
			
			this.comboChild.removeAll();
			List children = page.getModel().getChildrenNames(tmp.getTagName());
			if(children == null || children.size() == 0){
				this.comboChild.setEnabled(false);
				this.btnChild.setEnabled(false);
			}else{
				this.comboChild.setEnabled(true);
				this.btnChild.setEnabled(true);
				this.comboChild.add("");
				for(int i=0; i<children.size();i++){
					comboChild.add(""+children.get(i));
				}
		
			}
			this.element = tmp;
		}else{
			this.element = null;
		}
	}
	
	protected void createDetailContents(Composite parent){
		TableWrapLayout layout = new TableWrapLayout();
		layout.topMargin = 0;
		layout.leftMargin = 5;
		layout.rightMargin = 0;
		layout.bottomMargin = 0;
		parent.setLayout(layout);

		this.createTextSection(parent);
		this.createCommentSection(parent);
		this.createChildSection(parent);
	}
	
	private void createTextSection(Composite parent){
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		
		Section section = toolkit.createSection(parent, Section.TITLE_BAR|Section.DESCRIPTION);
		section.clientVerticalSpacing = 2;
		section.marginHeight = 5;		
		section.marginWidth = 5;
		section.setText("Text");
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.grabHorizontal = true;
		section.setLayoutData(td);
		
		Composite client = toolkit.createComposite(section);
		TableWrapLayout layout = new TableWrapLayout();
		client.setLayout(layout);
		
		this.txtContents = page.createText(toolkit, client, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtContents.setLayoutData(td);
		
		BPFormControl ctrlContents = new BPFormControl(txtContents);
		BPFormListener listener = new BPFormListener(){
			public void textChanged(){
				try{
					String str = txtContents.getText();
					if(element != null){
						page.getModel().setText(element, str, false);
					}
				}catch(Exception ex){
					SasPlugin.getDefault().log("Exception thrown createTextSection() XMLElementDetail.java..."+ex);
				}
			}

			public void selectionChanged() {
			}
			
		};
		ctrlContents.setFormListener(listener);
		
		toolkit.paintBordersFor(client);
		section.setClient(client);
	}
	
	private void createCommentSection(Composite parent){
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		
		Section section = toolkit.createSection(parent, Section.TITLE_BAR|Section.DESCRIPTION);
		section.clientVerticalSpacing = 2;
		section.marginHeight = 5;		
		section.marginWidth = 5;
		section.setText("Add Comment (above)");
		TableWrapData td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.grabHorizontal = true;
		section.setLayoutData(td);
	
		Composite client = toolkit.createComposite(section);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 4;
		client.setLayout(layout);
	
		this.txtComments = page.createText(toolkit, client, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 3;
		txtComments.setLayoutData(td);

		this.btnComments = page.createButton(toolkit, client, "Add");
		BPFormControl ctrlComments = new BPFormControl(btnComments);
		BPFormListener listener = new BPFormListener(){
			public void textChanged(){
			}

			public void selectionChanged() {
				try{
					String strComment = txtComments.getText();
					if(element != null && !strComment.trim().equals("")){
						Comment comment = page.getModel().getDocument().createComment(strComment);
						page.getModel().addComment(element, comment);
					}
					txtComments.setText("");
				}catch(Exception e){
					SasPlugin.getDefault().log("Exception thrown createCommentSection() XMLElementDetail.java..."+e);
				}
			}
		};
		ctrlComments.setFormListener(listener);
		
		toolkit.paintBordersFor(client);
		section.setClient(client);
	}

	private void createChildSection(Composite parent){
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		
		Section section = toolkit.createSection(parent, Section.TITLE_BAR|Section.DESCRIPTION);
		section.clientVerticalSpacing = 2;
		section.marginHeight = 5;		
		section.marginWidth = 5;
		section.setText("Add Child");
		TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
		td.grabHorizontal = true;
		section.setLayoutData(td);
		
		Composite client = toolkit.createComposite(section);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 4;
		client.setLayout(layout);
		
		this.comboChild = new CCombo(client, SWT.DROP_DOWN | SWT.READ_ONLY |SWT.BORDER);
		toolkit.adapt(this.comboChild);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 3;
		this.comboChild.setLayoutData(td);
		
		this.btnChild = page.createButton(toolkit, client, "Add");
		BPFormControl ctrlChild = new BPFormControl(btnChild);
		BPFormListener listener = new BPFormListener(){
			public void textChanged(){
			}

			public void selectionChanged() {
				String childName = comboChild.getText();
				if(element != null && !childName.trim().equals("")){
					Element child = page.getModel().getDocument().createElement(childName);
					page.getModel().addChild(element, child);
				}
				comboChild.setText("");
			}
		};
		ctrlChild.setFormListener(listener);
		
		toolkit.paintBordersFor(client);
		section.setClient(client);
	}

	private Element element;
	private Text txtContents;

	private Text txtComments;
	private Button btnComments;
	
	private CCombo comboChild;
	private Button btnChild;
	
}
