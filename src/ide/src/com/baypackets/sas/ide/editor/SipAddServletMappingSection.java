package com.baypackets.sas.ide.editor;

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.eclipse.swt.layout.GridData;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.model.ModelListener;

public class SipAddServletMappingSection extends SectionPart implements ModelListener{
	
	private static final String SERVLET_MAPPING = "servlet-mapping".intern();
	private static final String SESSION_CONFIG ="session-config".intern();
	private static  String SERVLET_NAME = "servlet-name".intern();
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
	private static final String EQUAL_IGNORE_CASE = "equalIgnoreCase".intern();
	private static final String CONTAINS_IGNORE_CASE = "containsIgnoreCase".intern();
	private static boolean disableControls=false;
	
	private static final String[] OPERATORS = new String[] {
		AND, NOT, OR
	};
	
	private static final String[] CONDITION = new String[] {
		AND, NOT, OR, EQUAL, CONTAINS, EXISTS, SUB_DOMAIN_OF
	};
	
	private static final List CONDITION_LIST = Arrays.asList(CONDITION);
	
	private static final String[] VARIABLES = new String[] {
		"request.method".intern(),
		"request.uri".intern(),
		"request.uri.scheme".intern(),
		"request.uri.user".intern(),
		"request.uri.host".intern(),
		"request.uri.port".intern(),
		"request.uri.tel".intern(),
		"request.from".intern(),
		"request.from.uri".intern(),
		"request.from.uri.scheme".intern(),
		"request.from.uri.user".intern(),
		"request.from.uri.host".intern(),
		"request.from.uri.port".intern(),
		"request.from.display-name".intern(),
		"request.to".intern(),
		"request.to.uri".intern(),
		"request.to.uri.scheme".intern(),
		"request.to.uri.user".intern(),
		"request.to.uri.host".intern(),
		"request.to.uri.port".intern(),
		"request.to.display-name".intern()
	};
	
	private static final String[] CONDITIONS = new String[] {
		EQUAL, EQUAL_IGNORE_CASE, 
		CONTAINS, CONTAINS_IGNORE_CASE,
		EXISTS, SUB_DOMAIN_OF
	};
		
	private BPFormPage page;
	
	public SipAddServletMappingSection(BPFormPage page, Composite parent,boolean disableControls) {
		super(parent, page.getManagedForm().getToolkit(), Section.TITLE_BAR | Section.DESCRIPTION);
		this.page = page;
		this.disableControls=disableControls;
		
		SasPlugin
		.getDefault()
		.log(
				"SipAddServletMappingSection() "+((SipServletPage)page).isSip289Xml());
		
		if(((SipServletPage)page).isSip289Xml()){
			SERVLET_NAME="javaee:servlet-name".intern();
			
		}
		//Create the UI.
		this.getSection().setText("Add Servlet Mapping Rule");
		page.getModel().addModelListener(this);
		this.createControls();
	}
	
	Composite group =null;
	protected void createControls(){
	
		Section section = this.getSection();
		FormToolkit toolkit = page.getManagedForm().getToolkit();
		
		group = new Composite(section, SWT.NONE);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 4;
		group.setLayout(layout);
		toolkit.adapt(group);
		
		//Associate this composite with the section.
		section.setClient(group);
		
		TableWrapData td = null;
		
		btnOperator = toolkit.createButton(group,"Add Operator", SWT.RADIO);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 3;
		btnOperator.setLayoutData(td);
		btnOperator.addSelectionListener(sl);
		
		comboOperator = new CCombo(group, SWT.DROP_DOWN | SWT.READ_ONLY|SWT.BORDER);
		toolkit.adapt(comboOperator);
		comboOperator.setEnabled(false);
		comboOperator.addSelectionListener(sl);
		
	
		for(int i=0; i<OPERATORS.length;i++){
			comboOperator.add(OPERATORS[i]);	
		}
		comboOperator.add("");
		
		btnCondition = toolkit.createButton(group,"Add Condition", SWT.RADIO);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 4;
		btnCondition.setLayoutData(td);
		btnCondition.addSelectionListener(sl);
		
		comboVariable = new CCombo(group, SWT.DROP_DOWN | SWT.READ_ONLY|SWT.BORDER);
		toolkit.adapt(comboVariable);
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		td.colspan = 2;
		comboVariable.setLayoutData(td);
		comboVariable.setEnabled(false);
		comboVariable.addSelectionListener(sl);
		
		
		for(int i=0; i<VARIABLES.length;i++){
			comboVariable.add(VARIABLES[i]);	
		}
		
		comboVariable.add("");
		
		comboCondition = new CCombo(group, SWT.DROP_DOWN | SWT.READ_ONLY |SWT.BORDER);
		toolkit.adapt(comboCondition);
		comboCondition.setEnabled(false);
		comboCondition.addSelectionListener(sl);
		
		
		for(int i=0; i<CONDITIONS.length;i++){
			comboCondition.add(CONDITIONS[i]);	
		}
		comboCondition.add("");
		
		//txtValue = toolkit.createText(group, "", SWT.BORDER | SWT.SINGLE);reeta commented it
		
		///reeta added it
		txtValue = page.createText(toolkit, group, "");
		td = new TableWrapData(TableWrapData.FILL_GRAB);
		txtValue.setEnabled(false);
		txtValue.setLayoutData(td);
		//
	
		page.createEmptySpace(toolkit, group, 2, BPFormPage.TABLE_LAYOUT);
		btnAddNew = toolkit.createButton(group,"Add New Mapping", SWT.FLAT);
		SelectionListener listener = new SelectionAdapter(){

			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				addNew();
				//reeta adding it
				btnAddNew.setEnabled(false);
			}
		};
		btnAddNew.addSelectionListener(listener);
		btnAddNew.setEnabled(false);
		
		btnAddSelection = toolkit.createButton(group,"Add to Selection", SWT.FLAT);
		listener = new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				addToSelection(selection);
				Element elMapping = getMappingElement(selection);
				if(elMapping != null){
					page.getModel().fireModelChanged(ModelListener.MODIFY, elMapping);
				}
			}
		};
		btnAddSelection.addSelectionListener(listener);
		btnAddSelection.setEnabled(false);
		
		if(disableControls){
			group.setEnabled(false);
		}
	}
	
	private Button btnOperator;
	private Button btnCondition;
	
	private CCombo comboOperator;
	private CCombo comboVariable;
	private CCombo comboCondition;
	private Text txtValue;
	
	private Button btnAddNew;
	private Button btnAddSelection;
	
	private Element parent;
	private Element selection;
	private boolean canAddToSelection;
	
	private SelectionListener sl = new SelectionAdapter(){

		public void widgetSelected(SelectionEvent e) {
			super.widgetSelected(e);
			checkSelection();
		}
	};
	
	protected Element getParent() {
		return parent;
	}

	protected void setParent(Element parent) {
		this.parent = parent;
	}

	protected Element getSelection() {
		return selection;
	}

	protected void setSelection(Element selection) {
		this.selection = selection;
		//reeta added it to clear any previous disabling of AddToSelection button
		canAddToSelection=true;
		if(selection == null){
			  SasPlugin
	  			.getDefault()
	  			.log(
	  					"Selection is null");
			this.canAddToSelection = false;
		}else{
			String selectionName = selection.getTagName();
			
			 SasPlugin
	  			.getDefault()
	  			.log(
	  					"SelectionName  is "+selectionName);
			
			if(selectionName.equals(AND) || 
					selectionName.equals(OR)){
				this.canAddToSelection = true;
			}
			
			if(selectionName.equals(PATTERN) || 
					selectionName.equals(NOT)){
				
				NodeList children = selection.getChildNodes();
				for(int i=0; children !=null && i<children.getLength(); i++ ){
					Node child = children.item(i);
					if(child != null && child.getNodeType() == Node.ELEMENT_NODE  
							&& CONDITION_LIST.contains(child.getNodeName())){
						this.canAddToSelection = false;
						break;
					}
				}
				
			}
			
			if (selectionName.equals(EQUAL) || selectionName.equals(CONTAINS)
					|| selectionName.equals(EXISTS)
					|| selectionName.equals(SUB_DOMAIN_OF)) {
				this.canAddToSelection = false;
			}
			
		}
		this.checkSelection();
	}
	
	private void checkSelection(){
		
		 SasPlugin
			.getDefault()
			.log("Entering checkSelection....");
		//enable if Add Operator button is enabled
		comboOperator.setEnabled(btnOperator.getSelection());
		
		//enable if Add Condition button is enabled
		
		comboVariable.setEnabled(btnCondition.getSelection());
		comboCondition.setEnabled(btnCondition.getSelection());
		txtValue.setEnabled(btnCondition.getSelection());
	
		boolean validInput = false;
		boolean alreadyexist =false;
		if(btnOperator.getSelection()){
			validInput = !comboOperator.getText().trim().equals("");
		}
		
		if(btnCondition.getSelection()){
			validInput = !(comboVariable.getText().trim().equals("")   //modified to && instead of ||
					  && !comboCondition.getText().trim().equals("")
					  && !txtValue.getText().trim().equals("") ); 
		}
		
        //reeta commenting it btnAddNew.setEnabled(this.parent != null && validInput);
        // reeta dding it enabling addnew button if there is not already any sip servlet
		//mapping for selected servlet
          NodeList nodes = page.getModel().getChildren(SERVLET_MAPPING);
          
          if(nodes!=null && nodes.getLength()==0){
        	  SasPlugin
  			.getDefault()
  			.log(
  					"Nodes length is zero......"+this.parent +" valid "+validInput);
        	  
        	  btnAddNew.setEnabled(this.parent != null && validInput);
        	  SasPlugin
    			.getDefault()
    			.log(
    					"btnAddNew......"+btnAddNew.getEnabled());
          }else{
		   
        	  for (int i = 0; nodes != null && i < nodes.getLength(); i++) {
        		  
		       Element mapping = (Element) nodes.item(i);
		       
		       String tmpServletName = page.getModel().getChildText(mapping,
		       SERVLET_NAME);
		       String servletName = page.getModel().getChildText(parent, SERVLET_NAME);
		   
		      if (!servletName.trim().equals("")
		         && servletName.equals(tmpServletName)) { //tmpServletName
			    alreadyexist =true;
			    break;
             }
		  }
        	  
        	  if(!alreadyexist){
             	 btnAddNew.setEnabled(true);  
              }else{
            	  btnAddNew.setEnabled(false); 
              }
        }
		 //
		btnAddSelection.setEnabled(this.canAddToSelection && validInput);
	}
	
	private void addToSelection(Element selected){
		if(selected == null)
			return;
		
		Document doc = page.getModel().getDocument();
		if(btnOperator.getSelection()){
			String operator = comboOperator.getText();
			if(!operator.trim().equals("")){
				selected.appendChild(doc.createTextNode("\n"));
				selected.appendChild(doc.createElement(operator.trim()));
				selected.appendChild(doc.createTextNode("\n"));
			}
			comboOperator.setText("");
		}
		
		if(btnCondition.getSelection()){
			String var = comboVariable.getText();
			String condition = comboCondition.getText();
			String value = txtValue.getText();
			
			if(!(var.trim().equals("") || condition.trim().equals(""))){
				Element elCondition = null;
				if(condition.equals(EQUAL_IGNORE_CASE)){
					elCondition = doc.createElement(EQUAL);
					elCondition.setAttribute(IGNORE_CASE, "true");
				}else if(condition.equals(CONTAINS_IGNORE_CASE)){
					elCondition = doc.createElement(CONTAINS);
					elCondition.setAttribute(IGNORE_CASE, "true");
				}else{
					elCondition = doc.createElement(condition);
				}
				selected.appendChild(doc.createTextNode("\n"));
				selected.appendChild(elCondition);
				selected.appendChild(doc.createTextNode("\n"));
				
				Element elVar = doc.createElement(VAR);
				elVar.appendChild(doc.createTextNode(var));
				elCondition.appendChild(doc.createTextNode("\n"));
				elCondition.appendChild(elVar);
				
				if(!condition.equals(EXISTS)){
					Element elValue = doc.createElement(VALUE);
					elValue.appendChild(doc.createTextNode(value));
				
					elCondition.appendChild(doc.createTextNode("\n"));
					elCondition.appendChild(elValue);
					elCondition.appendChild(doc.createTextNode("\n"));
				}
				
				comboVariable.setText("");
				comboCondition.setText("");
				txtValue.setText("");
			}
		}
	}
	
	private void addNew(){
		if(parent == null)
			return;
		
		
		String servletName = page.getModel().getChildText(parent, SERVLET_NAME);
		
		Document doc = page.getModel().getDocument();
		
		//check if mapping already exists so remove it
		checkifMappingAlreadyExists(doc.getDocumentElement(), servletName);
		
		Element elMapping = doc.createElement(SERVLET_MAPPING);
		
		Element elServlet = doc.createElement(SERVLET_NAME);
		elServlet.appendChild(doc.createTextNode(servletName));
		
		elMapping.appendChild(doc.createTextNode("\n"));
		elMapping.appendChild(elServlet);
		
		Element elPattern = doc.createElement(PATTERN);
		this.addToSelection(elPattern);
		elMapping.appendChild(doc.createTextNode("\n"));
		elMapping.appendChild(elPattern);
		elMapping.appendChild(doc.createTextNode("\n"));
		
		  NodeList servNodes= doc.getElementsByTagName(SESSION_CONFIG);
          Node sessConfigNode=null;
          
          if(servNodes!=null && servNodes.getLength()!=0){
        	  sessConfigNode= servNodes.item(0);
          }
        
          if(sessConfigNode!=null){
        	   /* page.getModel().insertBefore(doc.getDocumentElement(),doc.createTextNode("\n"), sessConfigNode);	*/
				page.getModel().insertBefore(doc.getDocumentElement(),elMapping, sessConfigNode);				
			}else{
				page.getModel().addChild(doc.getDocumentElement(), elMapping);
			}
		
	}
	
	private Element getMappingElement(Element child){
		Element elMapping = null;
		
		Node parent = child.getParentNode();
		while (parent != null){
			if(parent.getNodeType() == Node.ELEMENT_NODE 
					&& parent.getNodeName().equals(SERVLET_MAPPING)){
				elMapping = (Element) parent;
				break;
			}
			parent = parent.getParentNode();
		}
		return elMapping;
	}
	
	
	//reeta
	private void checkifMappingAlreadyExists(Element root,String servletName){
		boolean mappingAlreadyExists = false;
		// reeta added
		NodeList nodeList = root.getElementsByTagName("servlet-mapping");

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
									"The Servlet Mapping found is "
									+ node.getTextContent());
							if (name.equals("servlet-name")) {
								if (node.getTextContent().equals(servletName)) {
									mappingAlreadyExists = true;
									SasPlugin
									.getDefault()
									.log(
											"Mapping alredy exits so removing this eelement from the sip.xml");
									root.removeChild(servletNode);
									break;
								}
								break; // servlet-name chid found for this
								// servlet node
							}
						}
					}
				}
				if (mappingAlreadyExists) {
					break;
				}
			}
			
		}
	}

	@Override
	public void modelChanged(int action, Node data) {
		SasPlugin.getDefault().log("SipAddServletMappingSection  modelChanged()");
		group.setEnabled(!SipAddMainServletSection.mainServletDefined);
	}
}

