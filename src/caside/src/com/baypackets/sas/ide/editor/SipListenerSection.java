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

import com.baypackets.sas.ide.editor.model.ModelListener;

public class SipListenerSection extends SectionPart implements ModelListener {
	
	private static final String LISTENER = "listener".intern();
	private static final String LISTENER_CLASS = "listener-class".intern();
	
	private BPFormPage page;
	
	public SipListenerSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		
		//Create the UI.
		this.getSection().setText("Listeners");
		this.createControls();
		
		//Associate with the Model...
		page.getModel().addModelListener(this);
		this.loadContents();
	}
	
	protected void loadContents(){

		Element docElement = page.getModel().getDocument().getDocumentElement();
		this.listenerViewer.setInput(docElement);
		this.listenerViewer.refresh();
	}

	
	protected void createControls(){
	
		Section section = this.getSection();
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		
		//Create a top level composite
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);
		
		//Associate this composite with the section.
		section.setClient(composite);
		
		this.listenerViewer = new TableViewer(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL 
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.BORDER);

		Table table = this.listenerViewer.getTable();
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan  = 10;
		table.setLayoutData(gd);
		toolkit.adapt(listenerViewer.getTable());
		
		TableColumn column =  null;
		column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Listener Class");
		column.setWidth(300);
		
		table.setHeaderVisible(false);
		table.setLinesVisible(false);
		
		listenerViewer.setContentProvider(new ListenerContentProvider());
		listenerViewer.setLabelProvider(new ListenerLabelProvider());
		
		
		ISelectionChangedListener scl = new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if(!(selection instanceof IStructuredSelection))
					return;
				btnRemove.setEnabled(((IStructuredSelection)selection).getFirstElement() != null);
			}
		};
		listenerViewer.addSelectionChangedListener(scl);
		
		page.createEmptySpace(toolkit, composite, 3, BPFormPage.GRID_LAYOUT);
		btnRemove = toolkit.createButton(composite, "Remove", SWT.FLAT);
		btnRemove.setEnabled(false);
		SelectionListener sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = listenerViewer.getSelection();
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
				&& ( data.getNodeName().equals(LISTENER)
						|| (data.getParentNode() != null 
								&& data.getParentNode().getNodeName().equals(LISTENER) ) ) ){
			this.listenerViewer.refresh();
		}
	}
	
	private TableViewer listenerViewer;
	private Button btnRemove;
	
	public class ListenerContentProvider implements IStructuredContentProvider{

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			
			if (!(inputElement instanceof Element))
				return new Object[0];
			NodeList list = page.getModel().getChildren( (Element)inputElement, LISTENER);
			Object[] elements = new Object[list.getLength()];
			for(int i=0;i<list.getLength();i++){
				elements[i] = list.item(i);
			}
			return elements;
		}
		
	} 
	
	public class ListenerLabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String txt = "";
			if(!(element instanceof Element))
				return txt;
			
			Element listener = (Element) element;
			
			switch(columnIndex){
				case 0:
					Element cname = page.getModel().getChild(listener, LISTENER_CLASS, false); 
					txt = cname != null ? page.getModel().getText(cname) : "";
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
