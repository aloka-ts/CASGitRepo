package com.baypackets.sas.ide.editor;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.osgi.internal.serviceregistry.ServiceUse;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
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
import com.baypackets.sas.ide.editor.model.ModelListener;
import com.baypackets.sas.ide.util.IdeUtils;

public class SipAddMainServletSection extends SectionPart implements ModelListener {
	

	private static final String SERVLET_SELECTION = "servlet-selection".intern();
	private static final String MAIN_SERVLET = "main-servlet".intern();
	private static final String LISTENER = "listener".intern();
	public static boolean mainServletDefined=false;
	private static String SERVLET = "servlet".intern();
	
	/**
	private static final String[] INTERFACES = new String[] {
		"javax.servlet.sip.TimerListener".intern() };
		**/
	
	private static final String SUPER_CLASS_NAME = "javax.servlet.sip.SipServlet".intern();
	
	private static SearchPattern SEARCH_PATTERN = 
		SearchPattern.createPattern(SUPER_CLASS_NAME, 
			IJavaSearchConstants.CONSTRUCTOR,
			IJavaSearchConstants.REFERENCES,
			SearchPattern.R_EXACT_MATCH);

	private ArrayList servlets = new ArrayList();
	
	
	private BPFormPage page;
	
	public SipAddMainServletSection(BPFormPage page, Composite parent) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		int tStyle =  SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL;
		if(toolkit != null){
			tStyle |= toolkit.getBorderStyle();
		}
		
		//Create the UI.
		this.getSection().setText("Main Servlet");
		this.createControls();
		
		//Associate with the Model...
		page.getModel().addModelListener(this);
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
			
			//Add the listener names to the combo...
			mainServNameCombo.removeAll();
			mainServNameCombo.add("");
			for(int i=0; i<servlets.size();i++){
				mainServNameCombo.add(servlets.get(i).toString());
			}
			
			Element servSelect = page.getModel().getChild(SERVLET_SELECTION, false);
			 if(servSelect!=null){
				 String mainSer = page.getModel().getChildText(servSelect,
					       MAIN_SERVLET);
				 if(mainSer!=null){
					 mainServNameCombo.setText(mainSer); 
					 mainServletDefined=true;
				 }
			 }
			
		}catch(Exception e){
			SasPlugin.getDefault().log("Exception thrown loadContents() SipAddMainServletSection.java..."+e);
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
		
		//Create the Controls for the listener class name
		page.createLabel(toolkit, composite, "Main Servlet:");
		mainServNameCombo = new CCombo(composite, SWT.DROP_DOWN | SWT.READ_ONLY | SWT.BORDER);
		toolkit.adapt(mainServNameCombo);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		mainServNameCombo.setLayoutData(td);
//		sl = new SelectionAdapter(){
//			public void widgetSelected(SelectionEvent e) {
//				String listenerName = mainServNameCombo.getText();
//				btnAdd.setEnabled(!listenerName.trim().equals(""));
//			}
//		};
//		mainServNameCombo.addSelectionListener(sl);
//				
//		page.createEmptySpace(toolkit, composite, 1, BPFormPage.TABLE_LAYOUT);
//		btnAdd = toolkit.createButton(composite, "Add", SWT.FLAT);
//		btnAdd.setEnabled(false);
		
		sl = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				
				String listenerName = mainServNameCombo.getText();
				
				SasPlugin.getDefault().log("SipAddMainServletSelection widgetSelected" +listenerName);
				if(listenerName.trim().equals("")){
					Element servSelect = page.getModel().getChild(SERVLET_SELECTION, false);
					if(servSelect != null){
						page.getModel().removeChild(servSelect);
					}
					return;
				}
				
				Element servSelect = page.getModel().getChild(SERVLET_SELECTION, false);
				if(servSelect != null){
					page.getModel().removeChild(servSelect);
				}
				Document doc = page.getModel().getDocument();
				
				 Node firstServNode=null;
				NodeList servNodes = doc.getElementsByTagName(LISTENER);
				
				if (servNodes == null || servNodes.getLength() == 0) {
					servNodes = doc.getElementsByTagName(SERVLET);
				}
                               
                 if(servNodes!=null && servNodes.getLength()!=0){
                	 firstServNode= servNodes.item(0);
                 }
                        
				Element listener = doc.createElement(SERVLET_SELECTION);
				listener.appendChild(doc.createTextNode("\n"));
				
				Element lclass = doc.createElement(MAIN_SERVLET);
				lclass.appendChild(doc.createTextNode(listenerName));
				listener.appendChild(lclass);
				listener.appendChild(doc.createTextNode("\n"));

				if(firstServNode!=null){
					/* page.getModel().insertBefore(doc.getDocumentElement(),doc.createTextNode("\n"), firstServNode);	*/
					 page.getModel().insertBefore(doc.getDocumentElement(),listener, firstServNode);				
				}else{
					page.getModel().addChild(doc.getDocumentElement(), listener);
				}
				
			}
		};
		
		mainServNameCombo.addSelectionListener(sl);
	}
	

	public void modelChanged(int action, Node data) {
		SasPlugin.getDefault().log("SipAddMainServletSelection modelChanged()..."+data.getNodeName());
		
		if(data != null && data.getNodeName().equals(SERVLET_SELECTION)){
			  String str = page.getModel().getChildText((Element)data,
				       MAIN_SERVLET);
			SasPlugin.getDefault().log("SipAddMainServletSelection modelChanged()..."+str +" with Action "+ModelListener.REMOVE);
			this.mainServNameCombo.setText(action != ModelListener.REMOVE ? str : "");
			if(this.mainServNameCombo.getText().equals("")){
				mainServletDefined=false;
			}else{
				mainServletDefined=true;
			}
			SasPlugin.getDefault().log("SipAddMainServletSelection modelChanged()... mainServletDefined "+mainServletDefined+" Text is "+this.mainServNameCombo.getText());
		}
		
	}
	
	private CCombo mainServNameCombo;
}
