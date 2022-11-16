package com.baypackets.sas.ide.editor;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.FileEditorInput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.util.IdeUtils;

public class SipAddServletSection extends SectionPart {
	
	private static final String SERVLET = "servlet".intern();
	private static  String SERVLET_CLASS = "servlet-class".intern();
	private static  String SERVLET_NAME = "servlet-name".intern();
	private static  String DISPLAY_NAME = "display-name".intern();
	private static  String DESCRIPTION = "description".intern();
	private static  String LOAD_ON_STARTUP = "load-on-startup".intern();
	
	private static final String SUPER_CLASS_NAME = "javax.servlet.sip.SipServlet".intern();
	
	private static SearchPattern SEARCH_PATTERN = 
		SearchPattern.createPattern(SUPER_CLASS_NAME, 
			IJavaSearchConstants.CONSTRUCTOR,
			IJavaSearchConstants.REFERENCES,
			SearchPattern.R_EXACT_MATCH);

	private BPFormPage page;
	private ArrayList servlets = new ArrayList();
	
	public SipAddServletSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		
		if(((SipServletPage)page).isSip289Xml()){
			
			 SERVLET_CLASS = "javaee:servlet-class".intern();
			 SERVLET_NAME = "javaee:servlet-name".intern();
		     DISPLAY_NAME = "javaee:display-name".intern();
			 DESCRIPTION = "javaee:description".intern();
		     LOAD_ON_STARTUP = "javaee:load-on-startup".intern();
			
		}
		//Create the UI.
		this.getSection().setText("Add New SIP Servlet");
		this.createControls();
		
		this.loadContents();
	}
	
	protected void loadContents(){
		
		try{
			
			//get the file input associated with this file.... 
			IEditorInput input = page.getEditorInput();
			if(!(input instanceof FileEditorInput))
				return;
			
			//get the project assciated with this file...
			IProject project = ((FileEditorInput)input).getFile().getProject();
			
			//clear the listener list....
			servlets.clear();
			
			//Get the Class names...
			IdeUtils.getClassNames(project, SEARCH_PATTERN, servlets);
			SasPlugin.getDefault().log("The servlet loaded list is.."+servlets);
			//Add the listener names to the combo...
			comboServlets.removeAll();
			
			for(int i=0; i<servlets.size();i++){
				comboServlets.add(servlets.get(i).toString());
			}
			comboServlets.add("");
			comboServlets.select(0);
			
		}catch(Exception e){
			SasPlugin.getDefault().log("Exception thrown loadContents() SipAddServletSection.java..."+e);
		}
	}
	
	protected void createControls(){
	
		Section section = this.getSection();
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		SelectionListener sl = null;
		
		//Create a top level composite
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		
		//Associate this composite with the section.
		section.setClient(composite);
	
		TableWrapData td = null;
		
		//Create the Controls for the Servlet Name
		page.createLabel(toolkit, composite, "Servlet Name:");
		txtName = page.createText(toolkit, composite, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtName.setLayoutData(td);
		txtName.addListener(SWT.Modify, new Listener(){
			public void handleEvent(org.eclipse.swt.widgets.Event event){
				checkAddButton();
			}
		});
//		BPFormControl ctrlName =  new BPFormControl(txtName);
//		BPFormListener listener = new BPFormListener(){
//			public void textChanged(){
//				checkAddButton();
//			}
//			public void selectionChanged() {
//			}
//			
//		};
//		ctrlName.setFormListener(listener);
		
		//Create the controls for the Servlet Class
		page.createLabel(toolkit, composite, "Servlet Class:");
		comboServlets = new CCombo(composite, SWT.DROP_DOWN | SWT.READ_ONLY |SWT.BORDER);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		comboServlets.setLayoutData(td);
		toolkit.adapt(comboServlets);
		comboServlets.addListener(SWT.Selection, new Listener(){
			public void handleEvent(org.eclipse.swt.widgets.Event event){
				checkAddButton();
			}
		});
//		BPFormControl ctrlClass =  new BPFormControl(comboServlets);
//		listener = new BPFormListener(){
//			public void textChanged(){
//			}
//			public void selectionChanged() {
//				checkAddButton();
//			}
//		};
//		ctrlClass.setFormListener(listener);
		
		//Create the Controls for the Servlet Load on startup...
		page.createLabel(toolkit, composite, "Load On Startup:");
		btnLOS = toolkit.createButton(composite, "", SWT.CHECK);
		
		//Create the Controls for the Servlet Display Name
		page.createLabel(toolkit, composite, "Display Name:");
		txtDisplayName = page.createText(toolkit, composite, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtDisplayName.setLayoutData(td);
		
		//Create the Controls for the Servlet Description
		page.createLabel(toolkit, composite, "Description:");
		txtDescription = page.createText(toolkit, composite, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtDescription.setLayoutData(td);
		
		page.createEmptySpace(toolkit, composite, 1, BPFormPage.TABLE_LAYOUT);
		btnAdd = toolkit.createButton(composite, "Add", SWT.FLAT);
		btnAdd.setEnabled(false);
		
		sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				
				String servletName = txtName.getText();
				String servletClass = comboServlets.getText();
				String displayName = txtDisplayName.getText();
				String description = txtDescription.getText();
				boolean los = btnLOS.getSelection();
				
				if(servletName.trim().equals("") || servletClass.trim().equals(""))
					return;
				
				
				Document doc = page.getModel().getDocument();
				checkifServletAlreadyExists(doc.getDocumentElement(), servletName);
				
				Element servlet = doc.createElement(SERVLET);
				servlet.appendChild(doc.createTextNode("\n"));
				
				Element sname = doc.createElement(SERVLET_NAME);
				sname.appendChild(doc.createTextNode(servletName));
				servlet.appendChild(sname);
				servlet.appendChild(doc.createTextNode("\n"));
				
				if(!displayName.trim().equals("")){
					Element dname = doc.createElement(DISPLAY_NAME);
					dname.appendChild(doc.createTextNode(displayName));
					servlet.appendChild(dname);
					servlet.appendChild(doc.createTextNode("\n"));
				}
				
				if(!description.trim().equals("")){
					Element desc = doc.createElement(DESCRIPTION);
					desc.appendChild(doc.createTextNode(description));
					servlet.appendChild(desc);
					servlet.appendChild(doc.createTextNode("\n"));
				}
				
				Element sclass = doc.createElement(SERVLET_CLASS);
				sclass.appendChild(doc.createTextNode(servletClass));
				servlet.appendChild(sclass);
				servlet.appendChild(doc.createTextNode("\n"));
				
				if(los){
					Element elLoc = doc.createElement(LOAD_ON_STARTUP);
					elLoc.appendChild(doc.createTextNode("1"));
					servlet.appendChild(elLoc);
					servlet.appendChild(doc.createTextNode("\n"));
				}
				
				page.getModel().addChild(doc.getDocumentElement(), servlet);
				
				txtName.setText("");
				comboServlets.setText("");
				btnLOS.setSelection(false);
				txtDisplayName.setText("");
				txtDescription.setText("");
				btnAdd.setEnabled(false);
			}
		};
		btnAdd.addSelectionListener(sl);
	}
	
	private void checkAddButton(){
		String servletName = txtName.getText();
		String servletClass = comboServlets.getText();
		btnAdd.setEnabled(!servletName.trim().equals("") 
				&& !servletClass.trim().equals(""));
	}
	
	//reeta
	private void checkifServletAlreadyExists(Element root,String servletName){
		boolean serviceAlreadyExists = false;
		NodeList nodeList = root.getElementsByTagName("servlet");

		if (nodeList != null) {
			for (int i = 0; i < nodeList.getLength(); i++) {

				Node servletNode = nodeList.item(i);
				if (servletNode != null) {
					NodeList children = servletNode.getChildNodes();
					for (int j = 0; j < children.getLength(); j++) {
						Node node = children.item(j);
						if (node != null) {
							String name = node.getNodeName();
							SasPlugin.getDefault().log(
									"The Servlet found is "
									+ node.getTextContent());

							if (name.equals("servlet-name")) {
								if (node.getTextContent().equals(
										servletName)) {
									serviceAlreadyExists = true;
									SasPlugin
									.getDefault()
									.log(
											"Servlet alredy exits so removing this element from the sip.xml");
									root.removeChild(servletNode);
									break;
								}
								break; // servlet-name chid found for this
								// servlet node
							}
						}
					}

				}
				if (serviceAlreadyExists) {
					break;
				}
			}

		}
	}
	
	private Text txtName;
	private CCombo comboServlets;
	private Text txtDisplayName;
	private Text txtDescription;
	private Button btnLOS;
	private Button btnAdd;
	
}
