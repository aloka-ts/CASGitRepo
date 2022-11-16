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
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.model.ModelListener;

public class SipServletMappingSection extends SectionPart implements ModelListener {
	
	private static final String SERVLET_MAPPING = "servlet-mapping".intern();
	private static String SERVLET_NAME = "servlet-name".intern();
	private static final String PATTERN = "pattern".intern();
	private static final String AND = "and".intern();
	private static final String OR = "or".intern();
	private static final String NOT = "not".intern();
	private static final String EQUAL = "equal".intern();
	private static final String CONTAINS = "contains".intern();
	private static final String EXISTS = "exists".intern();
	private static final String SUB_DOMAIN_OF = "subdomain-of".intern();
	private static final String IGNORE_CASE = "ignore-case".intern();
	private static final String VAR = "var".intern();
	private static final String VALUE = "value".intern();
	
	private BPFormPage page;
	private Element parent;
	private static boolean disableControls=false;
	
	public SipServletMappingSection(BPFormPage page, Composite parent,boolean disableControls) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION );
		this.page = page;
		this.disableControls=disableControls;
		
		if(((SipServletPage)page).isSip289Xml()){
			SERVLET_NAME="javaee:servlet-name".intern();
			
		}
		//Create the UI.
		this.getSection().setText("SIP Servlet Mapping");
		this.createControls();
		
		//Associate with the Model...
		page.getModel().addModelListener(this);
	}
	
	public void setParent(Element input){
		this.parent  = input;
		this.updateList();
	}
	
	private void updateList(){
		ArrayList list = new ArrayList();
		if(parent != null ){
			String servletName = page.getModel().getChildText(parent, SERVLET_NAME);
			
			NodeList nodes = page.getModel().getChildren(SERVLET_MAPPING); 
			for(int i=0; nodes != null && i<nodes.getLength();i++){
				Element mapping = (Element)nodes.item(i);
				String tmpServletName = page.getModel().getChildText(mapping, SERVLET_NAME);
				
				if(!servletName.trim().equals("") 
						&& servletName.equals(tmpServletName)){
					list.add(mapping);
				}
			}
			
		}
		
		Element[] elements = new Element[list.size()];
		this.treeViewer.setInput(list.toArray(elements));
		this.treeViewer.refresh();

	}
	
	Composite composite =null;
	protected void createControls(){
	
		Section section = this.getSection();
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		
		//Create a top level composite
		 composite = toolkit.createComposite(section, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 6;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);
		
		//Associate this composite with the section.
		section.setClient(composite);
	
		int tStyle =  SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER;
		this.treeViewer = new TreeViewer(composite, tStyle);

		Tree tree = this.treeViewer.getTree();
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan  = 6;
		gd.verticalSpan = 10;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		tree.setLayoutData(gd);
		toolkit.adapt(tree);
		
		treeViewer.setContentProvider(new ServletMappingContentProvider());
		treeViewer.setLabelProvider(new ServletMappingLabelProvider());
		
		ISelectionChangedListener scl = new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if(!(selection instanceof IStructuredSelection))
					return;
				btnRemove.setEnabled(((IStructuredSelection)selection).getFirstElement() != null);
				
				if(page instanceof SipServletPage){
					((SipServletPage)page).mappingChanged(selection);
				}
			}
		};
		treeViewer.addSelectionChangedListener(scl);
		
		page.createEmptySpace(toolkit, composite, 5, BPFormPage.GRID_LAYOUT);
		btnRemove = toolkit.createButton(composite, "Remove", SWT.FLAT);
		btnRemove.setEnabled(false);
		SelectionListener sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = treeViewer.getSelection();
				if(!(selection instanceof IStructuredSelection))
					return;
				Object obj = ((IStructuredSelection)selection).getFirstElement();
				if(obj instanceof Node){
					page.getModel().removeChild((Node)obj);
					treeViewer.refresh(); //reeta added it
				}
			}
		};
		btnRemove.addSelectionListener(sl);
		
		toolkit.paintBordersFor(composite);
		
		if(disableControls){
			composite.setEnabled(false);
		}
	}
	
	public void modelChanged(int action, Node data) {
		
		SasPlugin.getDefault().log("SipServletMappingSection  modelChanged()");
		composite.setEnabled(!SipAddMainServletSection.mainServletDefined);
		if(data != null 
				&& data.getNodeName().equals(SERVLET_MAPPING)){
			this.updateList();
		}
	}
	
	private TreeViewer treeViewer;
	private Button btnRemove;
	
	public class ServletMappingContentProvider implements ITreeContentProvider{

		public Object[] getChildren(Object parentElement) {
			if(!(parentElement instanceof Element))
				return new Object[0];
			
			Element el = (Element)parentElement;
			ArrayList list = new ArrayList();
			String name = el.getTagName();
			if(name.equals(PATTERN) || name.equals(AND) 
					|| name.equals(OR) || name.equals(NOT)){
			
				NodeList nodeList = el.getChildNodes();
				for(int i=0; i<nodeList.getLength(); i++){
					Node childNode = nodeList.item(i);
					int type = childNode.getNodeType();
					if( type == Node.ELEMENT_NODE){
						list.add(childNode);
					}
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

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			
			if (!(inputElement instanceof Element[]))
				return new Object[0];
			
			Document doc = page.getModel().getDocument();
			Element[] mappings = (Element[]) inputElement;
			Element[] patterns = new Element[mappings.length];
			for(int i=0;i<mappings.length;i++){
				Element el = page.getModel().getChild(mappings[i], PATTERN, false);
				if(el == null){
					el = doc.createElement(PATTERN);
					mappings[i].appendChild(doc.createTextNode("\n"));
					mappings[i].appendChild(el);
					mappings[i].appendChild(doc.createTextNode("\n"));
				}
				patterns[i] = el;
			}
			return patterns;
		}
	} 
	
	public class ServletMappingLabelProvider extends LabelProvider {
		
		public String getText(Object element) {
			StringBuffer buffer = new StringBuffer();
			if(!(element instanceof Element))
				return buffer.toString();
			
			Element el = (Element)element;
			String strName = el.getNodeName();
			
			if(strName.equals(PATTERN) || strName.equals(AND)
					|| strName.equals(OR) || strName.equals(NOT)){
				buffer.append(strName);
			}else if(strName.equals(EQUAL) || strName.equals(CONTAINS)
					|| strName.equals(SUB_DOMAIN_OF)){
				buffer.append("$(");
				buffer.append(page.getModel().getChildText(el, VAR ));
				buffer.append(").");
				buffer.append(strName);
				
				String strIgnoreCase = el.getAttribute(IGNORE_CASE);
				strIgnoreCase = (strIgnoreCase == null) ? "" : strIgnoreCase;
				if(strIgnoreCase.equals("true")){
					buffer.append("IgnoreCase");
				}
				
				buffer.append("(\"");
				buffer.append(page.getModel().getChildText(el, VALUE ));
				buffer.append("\")");
			}else if (strName.equals(EXISTS)){
				buffer.append("$(");
				buffer.append(page.getModel().getChildText(el, VAR ));
				buffer.append(").exists()");
			}
			return buffer.toString();
		}
	}

	

}
