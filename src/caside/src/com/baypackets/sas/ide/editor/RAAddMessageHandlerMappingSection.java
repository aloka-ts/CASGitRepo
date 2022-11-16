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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.model.ModelListener;

public class RAAddMessageHandlerMappingSection extends SectionPart implements ModelListener {
	
	public static final String MESSAGE_HANDLER_MAPPING = "message-handler-mapping".intern();
	private static final String HANDLER_NAME = "handler-name".intern();
	private static final String RESOURCE_NAME = "resource-name".intern();
	
	private BPFormPage page;
	
	public RAAddMessageHandlerMappingSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		
		//Create the UI.
		this.getSection().setText("Message Handlers");
		this.createControls();
		
		//Associate with the Model...
		page.getModel().addModelListener(this);
		
		Element docElement = page.getModel().getDocument().getDocumentElement();
		this.setInput(docElement);
	}
	
	public void setInput(Object input){

		this.paramViewer.setInput(input);
		this.paramViewer.refresh();
	}
	
	protected void createControls(){
	
		Section section = this.getSection();
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		
		//Create a top level composite
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		//Associate this composite with the section.
		section.setClient(composite);
	
		
		this.paramViewer = new TableViewer(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL 
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.BORDER);

		Table table = this.paramViewer.getTable();
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan  = 8;
		table.setLayoutData(gd);
		toolkit.adapt(paramViewer.getTable());
		
		TableColumn column =  null;
		column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Handler Name");
		column.setWidth(100);
		
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Handler Class");
		column.setWidth(100);

		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		paramViewer.setContentProvider(new MessageHandlerContentProvider());
		paramViewer.setLabelProvider(new MessageHandlerLabelProvider());
		
		ISelectionChangedListener scl = new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if(!(selection instanceof IStructuredSelection))
					return;
				btnRemove.setEnabled(((IStructuredSelection)selection).getFirstElement() != null);
			}
		};
		paramViewer.addSelectionChangedListener(scl);
		
		page.createEmptySpace(toolkit, composite, 3, BPFormPage.GRID_LAYOUT);
		btnRemove = toolkit.createButton(composite, "Remove", SWT.FLAT);
		btnRemove.setEnabled(false);
		SelectionListener sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = paramViewer.getSelection();
				if(!(selection instanceof IStructuredSelection))
					return;
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				if(obj instanceof Node){
					page.getModel().removeChild((Node)obj);
				}
			}
		};
		btnRemove.addSelectionListener(sl);
	
		
	}
	
	public void modelChanged(int action, Node data) {
		
		if(data != null 
				&& ( data.getNodeName().equals(MESSAGE_HANDLER_MAPPING)
						|| (data.getParentNode() != null 
								&& data.getParentNode().getNodeName().equals(MESSAGE_HANDLER_MAPPING) ) ) ){
			this.paramViewer.setInput(page.getModel().getDocument().getDocumentElement());
			this.paramViewer.refresh();
		}
	}
	
	private TableViewer paramViewer;
	private Button btnRemove;
	
	
	public class MessageHandlerContentProvider implements IStructuredContentProvider{

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			
			if (!(inputElement instanceof Element))
				return new Object[0];
			NodeList list = page.getModel().getChildren( (Element)inputElement, MESSAGE_HANDLER_MAPPING);
			Object[] elements = new Object[list.getLength()];
			for(int i=0;i<list.getLength();i++){
				elements[i] = list.item(i);
			}
			return elements;
		}
		
	} 
	
	public class MessageHandlerLabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String txt = "";
			 SasPlugin.getDefault().log("MessageHandlerLabelProvider getColumnText .."+element);
			 
			if(!(element instanceof Element))
				return txt;
			
			Element cparam = (Element) element;
			
			switch(columnIndex){
				case 0:
					Element pname = page.getModel().getChild(cparam, HANDLER_NAME, false); 
					txt = pname != null ? page.getModel().getText(pname) : "";
					break;
				case 1:
					Element pvalue = page.getModel().getChild(cparam, RESOURCE_NAME, false); 
					txt = pvalue != null ? page.getModel().getText(pvalue) : "";
					break;
				
			}
			return txt;
		}

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}
	}

	

}
