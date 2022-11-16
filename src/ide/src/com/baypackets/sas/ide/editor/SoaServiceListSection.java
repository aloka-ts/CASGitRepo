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

public class SoaServiceListSection extends SectionPart implements ModelListener {
	
	
	private static final String SERVICE = "service".intern();
	private static final String SERVICE_NAME = "service-name".intern();
	private static final String ANNOTATED = "annotated".intern();
	private static final String SERVICE_API = "service-api".intern();
	private static final String SERVICE_IMPL = "service-impl".intern();
	private static final String SERVICE_PATH = "service-path".intern();
	private static final String NOTIFICATION_API = "notification-api".intern();
	
	private BPFormPage page;
	
	public SoaServiceListSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		
		this.init();
	}
	
	public void init(){
		//Create the UI.
		this.getSection().setText("SOA Services");
		this.createControls();
		
		//Associate with the Model...
		page.getModel().addModelListener(this);
		this.loadContents();
	}
	
	protected void loadContents(){

		Element docElement = page.getModel().getDocument().getDocumentElement();
		this.servletViewer.setInput(docElement);
		this.servletViewer.refresh();
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
		
		this.servletViewer = new TableViewer(composite, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL 
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION | SWT.BORDER);

		Table table = this.servletViewer.getTable();
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan  = 10;
		table.setLayoutData(gd);
		toolkit.adapt(servletViewer.getTable());
		
		TableColumn column =  null;
		column = new TableColumn(table, SWT.LEFT, 0);
		column.setText("Service Name");
		column.setWidth(200);
		
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText("Service API");
		column.setWidth(250);
		
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText("Annotated");
		column.setWidth(125);
		
		column = new TableColumn(table, SWT.LEFT, 3);
		column.setText("Service Impl");
		column.setWidth(250);
		
//		column = new TableColumn(table, SWT.LEFT, 4);
//		column.setText("Service Path");
//		column.setWidth(200);
		
		column = new TableColumn(table, SWT.LEFT, 4);
		column.setText("Notification API");
		column.setWidth(250);
		
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		servletViewer.setContentProvider(new ServletContentProvider());
		servletViewer.setLabelProvider(new ServletLabelProvider());
		
		ISelectionChangedListener scl = new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if(!(selection instanceof IStructuredSelection))
					return;
				btnRemove.setEnabled(((IStructuredSelection)selection).getFirstElement() != null);

				if(page instanceof SoaServiceApplicationPage){
					((SoaServiceApplicationPage)page).selectionChanged(selection);
				}
			}
		};
		servletViewer.addSelectionChangedListener(scl);
		
		page.createEmptySpace(toolkit, composite, 3, BPFormPage.GRID_LAYOUT);
		btnRemove = toolkit.createButton(composite, "Remove", SWT.FLAT);
		btnRemove.setEnabled(false);
		SelectionListener sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = servletViewer.getSelection();
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
				&& ( data.getNodeName().equals(SERVICE)
						|| (data.getParentNode() != null 
								&& data.getParentNode().getNodeName().equals(SERVICE) ) ) ){
			this.servletViewer.refresh();
		}
	}
	
	private TableViewer servletViewer;
	private Button btnRemove;
	
	public class ServletContentProvider implements IStructuredContentProvider{

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			
			if (!(inputElement instanceof Element))
				return new Object[0];
			NodeList list = page.getModel().getChildren( (Element)inputElement, SERVICE);
			Object[] elements = new Object[list.getLength()];
			for(int i=0;i<list.getLength();i++){
				elements[i] = list.item(i);
			}
			return elements;
		}
		
	} 
	
	public class ServletLabelProvider implements ITableLabelProvider{

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			String txt = "";
			if(!(element instanceof Element))
				return txt;
			
			Element service = (Element) element;
			
			switch(columnIndex){
				case 0:
					Element sname = page.getModel().getChild(service, SERVICE_NAME, false); 
					txt = sname != null ? page.getModel().getText(sname) : "";
					break;
				case 1:
					Element sclass = page.getModel().getChild(service, SERVICE_API, false); 
					txt = sclass != null ? page.getModel().getText(sclass) : "";
					break;
				case 2:
					sclass = page.getModel().getChild(service, SERVICE_API, false); 
					txt=sclass !=null ? sclass.getAttribute(ANNOTATED): "";
					break;	
				case 3:
					Element los = page.getModel().getChild(service, SERVICE_IMPL, false); 
					txt = los != null ? page.getModel().getText(los) : "";
					break;
//				case 4:
//					Element dname = page.getModel().getChild(service, SERVICE_PATH, false); 
//					txt = dname != null ? page.getModel().getText(dname) : "";
//					break;
				case 4:
					Element desc = page.getModel().getChild(service, NOTIFICATION_API, false); 
					txt = desc != null ? page.getModel().getText(desc) : "";
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
