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

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baypackets.sas.ide.editor.model.ModelListener;

public class XMLMasterPart extends SectionPart implements ModelListener{
	
	private BPFormPage page;
	
	public XMLMasterPart(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
	}
	
	public void init(){
		this.getSection().setText("Tree View");
		this.createTreeClient();
		
		page.getModel().addModelListener(this);
	}
	
	public void createTreeClient(){
		
		Section section = this.getSection();
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		
		//Create a top level composite
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		composite.setLayout(layout);
		
		//Associate this composite with the section.
		section.setClient(composite);
		
		//Create the tree viewer instance....
		int tStyle =  SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL;
		if(toolkit != null){
			tStyle |= toolkit.getBorderStyle();
		}
		treeViewer = new TreeViewer(composite, tStyle);
		this.contentProvider = new TreeContentProvider(page.getModel().getDocument());
		this.labelProvider = new TreeLabelProvider();
		treeViewer.setContentProvider(this.contentProvider);
		treeViewer.setLabelProvider(this.labelProvider);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan  = 4;
		treeViewer.getControl().setLayoutData(gd);
		toolkit.adapt(treeViewer.getTree());
		
		Document doc = page.getModel().getDocument();
		treeViewer.setInput(doc);

		ISelectionChangedListener scl = new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection(); 
				btnRemove.setEnabled(selection != null);
				page.getManagedForm().fireSelectionChanged(XMLMasterPart.this, selection);
			}
		};
		treeViewer.addSelectionChangedListener(scl);
				
		//Create Empty Space for 2 cells
		page.createEmptySpace(toolkit, composite, 2, BPFormPage.GRID_LAYOUT);
		
		//Create the Remove Button
		this.btnRemove = page.createButton(toolkit, composite, "Remove", BPFormPage.GRID_LAYOUT);
		SelectionListener listener = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = treeViewer.getSelection();
				if(!(selection instanceof IStructuredSelection))
					return;
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				if(obj instanceof Node){
					page.getModel().removeChild((Node)obj);
				}
			}
		};
		this.btnRemove.addSelectionListener(listener);
		
		//Create the Refresh button
		this.btnRefresh = page.createButton(toolkit, composite, "Refresh", BPFormPage.GRID_LAYOUT);
		listener = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				treeViewer.refresh();
			}
		};
		this.btnRefresh.addSelectionListener(listener);
		
		toolkit.paintBordersFor(composite);
	}
	
	private static class TreeContentProvider implements ITreeContentProvider {

		private Document doc;
		
		public TreeContentProvider(Document doc) {
			super();
			this.doc = doc;
		}

		public Object[] getChildren(Object parentElement) {
			if(!(parentElement instanceof Node))
				return new Object[0];
			
			Node node = (Node)parentElement;
			ArrayList list = new ArrayList();
			NodeList nodeList = node.getChildNodes();
			for(int i=0; i<nodeList.getLength(); i++){
				Node childNode = nodeList.item(i);
				int type = childNode.getNodeType(); 
				if( type == Node.ELEMENT_NODE ||
						type == Node.COMMENT_NODE || 
						type == Node.DOCUMENT_TYPE_NODE){
					list.add(childNode);
				}
			}
			
			return list.toArray();
		}

		public Object getParent(Object element) {
			if(!(element instanceof Node))
				return null;
			
			return ((Node)element).getParentNode();
		}

		public boolean hasChildren(Object element) {
			if(!(element instanceof Node))
				return false;
			Node node = (Node)element;
			return node.hasChildNodes();
		}

		public Object[] getElements(Object inputElement) {
			if(inputElement instanceof Document){
				return this.getChildren(doc);
			}
			return new Object[0];
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
		
	}
	
	public class TreeLabelProvider extends LabelProvider {
		public String getText(Object element) {
			
			String text = (element instanceof Node) ? ((Node)element).getNodeName() : null;
			return text != null ? text : super.getText(element);
		}
	}
	
	private TreeViewer treeViewer;
	private TreeContentProvider contentProvider;
	private TreeLabelProvider labelProvider;
	
	private Button btnRemove;
	private Button btnRefresh;

	
	public void modelChanged(int action, Node data) {
		this.treeViewer.refresh();
	}
}
