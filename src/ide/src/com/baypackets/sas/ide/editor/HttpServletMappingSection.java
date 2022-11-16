//author@reetaAggarwal
package com.baypackets.sas.ide.editor;
import java.util.ArrayList;

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

public class HttpServletMappingSection extends SectionPart implements ModelListener{
	
	private static final String SERVLET_MAPPING = "servlet-mapping".intern();
	private static final String SERVLET_NAME = "servlet-name".intern();
	private static final String URL_PATTERN = "url-pattern".intern();
	
	private BPFormPage page;
	private Element parent;
	
	public HttpServletMappingSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION );
		this.page = page;
		
		//Create the UI.
		this.getSection().setText("Http Servlet Mapping");
		this.createControls();
		
		//Associate with the Model...
		page.getModel().addModelListener(this);
	}
	
	public void setParent(Element input){
		this.parent  = input;
		
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
		layout.numColumns = 4;
		composite.setLayout(layout);
		
		//Associate this composite with the section.
		section.setClient(composite);
	
		
		this.paramViewer = new TableViewer(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL 
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.BORDER);

		Table table = this.paramViewer.getTable();
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan  = 12;
		table.setLayoutData(gd);
		toolkit.adapt(paramViewer.getTable());
		
		TableColumn column =  null;
		column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Servlet Name");
		column.setWidth(125);
		
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Url Pattern");
		column.setWidth(125); 
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		paramViewer.setContentProvider(new HttpServletMappingContentProvider());
		paramViewer.setLabelProvider(new HttpServletMappingLabelProvider());
		
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
				&& ( data.getNodeName().equals(SERVLET_MAPPING)
						|| (data.getParentNode() != null 
								&& data.getParentNode().getNodeName().equals(SERVLET_MAPPING) ) ) ){
			this.paramViewer.refresh();
		}
	}
	
	private TableViewer paramViewer;
	private Button btnRemove;
	
	
	public class HttpServletMappingContentProvider implements IStructuredContentProvider{

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			
			//if (!(inputElement instanceof Element))
			//	return new Object[0];
			String servletName = page.getModel().getChildText(parent, SERVLET_NAME);
			
			NodeList nodes = page.getModel().getChildren(SERVLET_MAPPING);
			Object[] elements = new Object[nodes.getLength()];
			for(int i=0; nodes != null && i<nodes.getLength();i++){
				Element mapping = (Element)nodes.item(i);
				String tmpServletName = page.getModel().getChildText(mapping, SERVLET_NAME);
				
				if(!servletName.trim().equals("") 
						&& servletName.equals(tmpServletName)){
					//list.add(mapping);
					elements[i]=mapping;
				}else{
					elements[i]= new Object[0];
				}
			}
			
		return elements;
		
	} 
	
	}
	public class HttpServletMappingLabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String txt = "";
			if(!(element instanceof Element))
				return txt;
			
			Element cparam = (Element) element;
			
			switch(columnIndex){
				case 0:
					Element pname = page.getModel().getChild(cparam, SERVLET_NAME, false); 
					txt = pname != null ? page.getModel().getText(pname) : "";
					break;
				case 1:
					Element pvalue = page.getModel().getChild(cparam, URL_PATTERN, false); 
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
