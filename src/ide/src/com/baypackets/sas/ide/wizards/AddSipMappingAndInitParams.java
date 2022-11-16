//Author@Reeta Aggarwal
/*This class is used to add init and mapping parameters to sip.xml
 while creating a servlet this class is the second page for wizard for
 adding new servlet*/
package com.baypackets.sas.ide.wizards;

import java.util.Arrays;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.*;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import java.util.ArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.XMLEditor;
import com.baypackets.sas.ide.editor.model.XMLModel;
import com.baypackets.sas.ide.editor.model.XmlMetaData;
import javax.xml.parsers.DocumentBuilder;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import java.io.*;
import org.eclipse.core.runtime.IProgressMonitor;

public class AddSipMappingAndInitParams extends NewTypeWizardPage {

	private static final String SERVLET = "servlet".intern();

	private static final String SERVLET_NAME = "servlet-name".intern();

	private static final String SERVLET_CLASS = "servlet-class".intern();

	private static final String DESCRIPTION = "description".intern();

	private static final String DISPLAY_NAME = "display-name".intern();

	private static final String SERVLET_MAPPING = "servlet-mapping".intern();

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

	private static final String CONTAINS_IGNORE_CASE = "containsIgnoreCase"
			.intern();

	private static final String[] CONDITION = new String[] { AND, NOT, OR,
			EQUAL, CONTAINS, EXISTS, SUB_DOMAIN_OF };

	private static final java.util.List CONDITION_LIST = Arrays
			.asList(CONDITION);

	private static final String INIT_PARAM = "init-param".intern();

	private static final String PARAM_NAME = "param-name".intern();

	private static final String PARAM_VALUE = "param-value".intern();

	String[] specialChar = new String[] { ";", ",", ".", ":", "?", "{", "}",
			"[", "]", "(", ")", "/", "<", ">", "#", "$", "%", "^", "&", "*",
			"!", "@", "-", "+", "=", "|", "~", "`" };

	public AddSipMappingAndInitParams(BPClassWizard wizard) {
		super(true, "Add Params");
		setTitle("Add Mapping and Init Parameters to SipServlet");
		this.wizard = wizard;
		setDescription("This Data will be added to sip.xml");
	}

	protected void init() {
		paramName = new ArrayList(20);
		paramValue = new ArrayList(20);
		paramDesc = new ArrayList(20);
	}

	private Listener listener = new Listener() {
		public void handleEvent(Event e) {
			serName = servletName.getText();
			description = desc.getText();
			if (serName.equals("") || serName.indexOf(" ") != -1) {
				sendErrorMessage("Enter a valid Servlet Name.");
			} else {
				sendErrorMessage(null);
			}

			if (specialChar != null) {
				for (int i = 0; i < specialChar.length; i++) {
					int index = serName.indexOf(specialChar[i]);
					if (index != -1) {
						sendErrorMessage("Enter a valid Servlet Name.");
					}
				}

			} else {
				sendErrorMessage(null);
			}
		}
	};

	/**
	 * invoked from BPSipServletPage when a type name is entered
	 * from first page i.e BPSipServletPage
	 */
	public void setServletName(String serName) {
		servletName.setText(serName);
		servletClass = serName;
		String srcPath = this.wizard.getFirstPage()
				.getPackageFragmentRootText();
		pacakge = this.wizard.getFirstPage().getPackageText();
		int indunix = srcPath.indexOf("/");
		String projectName = null;
//		Extract project name from PackageFragmentRootText as per window or unix
		if (indunix != -1) {
			projectName = srcPath.substring(0, indunix);
		} else {
			projectName = srcPath;
		}
		
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				projectName);
		IFolder webInfFolder = project.getFolder("WEB-INF");

		IFile sipDesc = webInfFolder.getFile("sip.xml");

		InputStream stream = null;
		try {
			DocumentBuilder docBuilder = XMLModel.FACTORY.newDocumentBuilder();
			docBuilder.setErrorHandler(XMLModel.ERROR_HANDLER);
			docBuilder.setEntityResolver(XmlMetaData.ENTITY_RESOLVER);
			stream = sipDesc.getContents();
			sipDescriptor = docBuilder.parse(stream);
		} catch (Exception e) {
			String st[] = new String[]{"OK"};
			MessageDialog dia = new MessageDialog(this.getShell(), "Add Sip Servlet", null, "No sip.xml descriptor found", MessageDialog.WARNING, st, 0);
			dia.open();
			SasPlugin.getDefault().log("Exception thrown by setServletName() :AddSipMappingAndInitParams.java" +e);
		} finally {
			try {
				if(stream!=null)
				stream.close();
			} catch (IOException e) {

			}
		}
		model = new XMLModel(sipDescriptor, sipDesc.getName(), sipDesc);
	}

	private void sendErrorMessage(String message) {
		this.setErrorMessage(message);

	}

	public void createControl(Composite parent) {

		initializeDialogUnits(parent);

		composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		int nColumns = 4;

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		new Label(composite, SWT.LEFT | SWT.WRAP).setText("Servlet Name:");

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		servletName = new Text(composite, SWT.SINGLE | SWT.BORDER);
		gridData.horizontalSpan = 4;
		servletName.setLayoutData(gridData);
		servletName.addListener(SWT.Modify, listener);
		servletName.setTextLimit(100);

		new Label(composite, SWT.LEFT | SWT.WRAP).setText("Description :");

		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
		desc = new Text(composite, SWT.SINGLE | SWT.BORDER);
		gridData1.horizontalSpan = 4;
		desc.setLayoutData(gridData1);
		desc.setTextLimit(100);
		desc.addListener(SWT.Modify, listener);

		GridData gridD = new GridData(GridData.FILL_HORIZONTAL);
		gridD.horizontalSpan = 4;
		Label lable = new Label(composite, SWT.LEFT | SWT.WRAP);
		lable.setText("Pattern Mapping:");
		lable.setLayoutData(gridD);

		GridData gridData3 = new GridData(GridData.FILL_BOTH); //FILL BOTH
		mappingViewer = new TreeViewer(composite, SWT.SINGLE | SWT.BORDER); //added
		mapping = mappingViewer.getTree();
		gridData3.horizontalSpan = 4;
		mapping.setLayoutData(gridData3);
		mappingViewer.setContentProvider(new SipMappingContentProvider());
		mappingViewer.setLabelProvider(new SipMappingLabelProvider());
		ISelectionChangedListener scl = new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if (!(selection instanceof IStructuredSelection))
					return;
				removeMapping.setEnabled(((IStructuredSelection) selection)
						.getFirstElement() != null);
				mappingChanged(selection);

			}
		};
		mappingViewer.addSelectionChangedListener(scl);

		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		addMapping = new Button(composite, SWT.PUSH);
		addMapping.setText("Add");
		addMapping.setLayoutData(gridData2);

		Menu listMenu = new Menu(composite.getShell(), SWT.POP_UP);

		MenuItem options1 = new MenuItem(listMenu, SWT.PUSH);
		options1.setText("&Condition");
		options1.addSelectionListener(new SelectionAdapter() {
			public AddSipMappingDialog dialog = new AddSipMappingDialog(
					composite.getShell());

			public void widgetSelected(SelectionEvent event) {

				Composite com = (Composite) dialog.createDialogArea(composite);
				dialog.create();
				dialog.open();
				if (dialog.getReturnCode() == Window.OK) {
					mapCondition = dialog.getcondition();
					mapVariable = dialog.getVariable();
					mapValue = dialog.getValue();
					//show mapping in tree
					if (isNewMapping() == true) {
						addMapping(sipDescriptor);
					} else {
						addToPattern(mappingSelection, sipDescriptor);
					}

					updateList();

				}
			}
		});
		MenuItem options2 = new MenuItem(listMenu, SWT.PUSH);
		options2.setText("&" + AND);
		options2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				// show mapping in tree
				operatorSelected = AND;
				if (isNewMapping() == true) {
					addMapping(sipDescriptor);
				} else {
					addToPattern(mappingSelection, sipDescriptor);
				}
				updateList();
			}
		});
		MenuItem options3 = new MenuItem(listMenu, SWT.PUSH);
		options3.setText("&" + OR);
		options3.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//show mapping in tree
				operatorSelected = OR;
				if (isNewMapping() == true) {
					addMapping(sipDescriptor);
				} else {
					addToPattern(mappingSelection, sipDescriptor);
				}
				updateList();
			}
		});
		MenuItem options4 = new MenuItem(listMenu, SWT.PUSH);
		options4.setText("&" + NOT);
		options4.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				//show mapping in tree
				operatorSelected = NOT;
				if (isNewMapping() == true) {
					addMapping(sipDescriptor);
				} else {
					addToPattern(mappingSelection, sipDescriptor);
				}
				updateList();
			}
		});
		addMapping.setMenu(listMenu);
		addMapping.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {

				if (serName.equals("")) {
					sendErrorMessage("Enter a Servlet Name First.");
				} else {
					sendErrorMessage(null);
					addMapping.getMenu().setVisible(true);

					//		  if(mappingViewer.getTree().getItemCount()==0){
					if (servletElementFlag == false) {
						addServletElement();
						servletElementFlag = true;
					}

				}

			}
		});

		GridData gridData7 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		removeMapping = new Button(composite, SWT.PUSH);
		removeMapping.setText("remove");
		removeMapping.setLayoutData(gridData7);
		removeMapping.setEnabled(false);

		removeMapping.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {

				sendErrorMessage(null);
				ISelection selection = mappingViewer.getSelection();
				if (!(selection instanceof IStructuredSelection))
					return;
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (obj instanceof Node) {

					model.removeChild((Node) obj); //uncommented now
					/*	NodeList nodes = model.getChildren(SERVLET_MAPPING);
					 for (int i = 0; nodes != null && i < nodes.getLength(); i++) {
					 Element mapping = (Element) nodes.item(i);
					 String tmpServletName = model.getChildText(mapping,
					 SERVLET_NAME);
					 if (!serName.trim().equals("")
					 && serName.equals(tmpServletName)) { //tmpServletName
					 model.removeChild((Node) obj);

					 }
					 } */// commented now
					updateList();
				}
			}

		});

		//control for Init Parameters

		//create an empty line
		GridData gr = new GridData(GridData.FILL_HORIZONTAL);
		gr.horizontalSpan = 4;
		Label l = new Label(composite, SWT.LEFT | SWT.WRAP);
		l.setLayoutData(gr);

		GridData grid = new GridData(GridData.FILL_HORIZONTAL);
		grid.horizontalSpan = 4;
		Label lab = new Label(composite, SWT.LEFT | SWT.WRAP);
		lab.setText("Init Parameters:");
		lab.setLayoutData(grid);

		GridData gridData4 = new GridData(GridData.FILL_BOTH);
		param = new List(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP);
		gridData4.horizontalSpan = 4;
		param.setLayoutData(gridData4);
		param.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (param.getSelection() != null) {
					removeInitParam.setEnabled(true);

				}
			}
		});

		GridData gridData5 = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		Button add1 = new Button(composite, SWT.PUSH);
		add1.setText("Add");
		add1.setLayoutData(gridData5);

		add1.addSelectionListener(new SelectionAdapter() {

			public AddInitParamsDialog dialog = new AddInitParamsDialog(
					composite.getShell());

			public void widgetSelected(SelectionEvent event) {

				Composite com = (Composite) dialog.createDialogArea(composite);
				dialog.create();
				dialog.open();

				if (dialog.getReturnCode() == Window.OK) {

					initParamName = dialog.getParamName();
					initParamValue = dialog.getParamValue();
					initParamDescr = dialog.getInitParamDescription();

					paramName.add(initParamName);
					paramValue.add(initParamValue);
					paramDesc.add(initParamDescr);

					// show params in list	
					param.add(initParamName);
				}

			}

		});

		GridData gridData8 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		removeInitParam = new Button(composite, SWT.PUSH);
		removeInitParam.setText("remove");
		removeInitParam.setLayoutData(gridData8);
		removeInitParam.setEnabled(false);

		removeInitParam.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {

				if (param.getSelection() != null) {

					int i = param.getSelectionIndex();

					if (paramName.get(i) != null) {
						paramName.remove(i);
						paramValue.remove(i);
						paramDesc.remove(i);
					}
					param.remove(i);
					removeInitParam.setEnabled(false);
				}
			}

		});

		setControl(composite);

		Dialog.applyDialogFont(composite);

	}

	public void AddFieldToDescriptor(IProgressMonitor monitor) {

		if (sipDescriptor != null) {
			//adding init params to descriptor
//			  if(mappingViewer.getTree().getItemCount()==0){
			//if mapping has not been added then the servlet element will not be added
			 //so add it here
			if (servletElementFlag == false) {
				addServletElement();
				servletElementFlag = true;
			}
			if (paramName.isEmpty() == false) {
				for (int i = 0; i < paramName.size(); i++) {
					String name = (String) paramName.get(i);
					String value = (String) paramValue.get(i);
					String desc = (String) paramDesc.get(i);

					Element cpara = sipDescriptor.createElement(INIT_PARAM);
					cpara.appendChild(sipDescriptor.createTextNode("\n"));
					Element pnam = sipDescriptor.createElement(PARAM_NAME);
					pnam.appendChild(sipDescriptor.createTextNode(name));
					cpara.appendChild(pnam);
					cpara.appendChild(sipDescriptor.createTextNode("\n"));

					Element pval = sipDescriptor.createElement(PARAM_VALUE);
					pval.appendChild(sipDescriptor.createTextNode(value));
					cpara.appendChild(pval);
					cpara.appendChild(sipDescriptor.createTextNode("\n"));

					if (!desc.trim().equals("")) {
						Element descri = sipDescriptor
								.createElement(DESCRIPTION);
						descri.appendChild(sipDescriptor.createTextNode(desc));
						cpara.appendChild(descri);
						cpara.appendChild(sipDescriptor.createTextNode("\n"));
					}

					servlet.appendChild(cpara);
					if (i != (paramName.size() - 1)) {
						servlet.appendChild(sipDescriptor.createTextNode("\n")); // added for space between multiple init params
					}
				}
			}
			if (paramName.isEmpty() == false) { //adding a space between last init param and servlet tag when Init parameters are defined
				servlet.appendChild(sipDescriptor.createTextNode("\n")); // added for space 
			}

			try {
				XMLEditor editor = new XMLEditor(model);
				editor.doSave(monitor);
			} catch (Exception e) {

			}

		} //end of if descriptor is not null
	} //end of method

	public void addMapping(Document doc) {
		SasPlugin.getDefault().log("The addMapping...............doc is"+doc);
		if (doc == null)
			return;
		SasPlugin.getDefault().log("The adding Mapping to the doc..............is");
		Element elMapping = doc.createElement(SERVLET_MAPPING);

		Element elServlet = doc.createElement(SERVLET_NAME);
		elServlet.appendChild(doc.createTextNode(serName));

		elMapping.appendChild(doc.createTextNode("\n"));
		elMapping.appendChild(elServlet);

		elPattern = doc.createElement(PATTERN);
		this.addToPattern(elPattern, doc);
		elMapping.appendChild(doc.createTextNode("\n"));
		elMapping.appendChild(elPattern);
		elMapping.appendChild(doc.createTextNode("\n"));

		model.addChild(doc.getDocumentElement(), elMapping);
		SasPlugin.getDefault().log("The added  Mapping to the doc..............is");
	}

	public void addToPattern(Element selected, Document doc) {
		Element oper = null;
		if (selected == null)
			return;

		if (!operatorSelected.trim().equals("")) {
			selected.appendChild(doc.createTextNode("\n"));
			oper = doc.createElement(operatorSelected.trim());
			selected.appendChild(oper);
			selected.appendChild(doc.createTextNode("\n"));
			operatorSelected = "";

		}

		if (!mapValue.equals("")) {
			if (!(mapVariable.trim().equals("") || mapCondition.trim().equals(
					""))) {
				Element elCondition = null;
				if (mapCondition.equals(EQUAL_IGNORE_CASE)) {
					elCondition = doc.createElement(EQUAL);
					elCondition.setAttribute(IGNORE_CASE, "true");
				} else if (mapCondition.equals(CONTAINS_IGNORE_CASE)) {
					elCondition = doc.createElement(CONTAINS);
					elCondition.setAttribute(IGNORE_CASE, "true");
				} else {
					elCondition = doc.createElement(mapCondition);
				}

				selected.appendChild(doc.createTextNode("\n"));
				selected.appendChild(elCondition);
				selected.appendChild(doc.createTextNode("\n"));

				Element elVar = doc.createElement(VAR);
				elVar.appendChild(doc.createTextNode(mapVariable));
				elCondition.appendChild(doc.createTextNode("\n"));
				elCondition.appendChild(elVar);

				if (!mapCondition.equals(EXISTS)) {
					Element elValue = doc.createElement(VALUE);
					elValue.appendChild(doc.createTextNode(mapValue));

					elCondition.appendChild(doc.createTextNode("\n"));
					elCondition.appendChild(elValue);
					elCondition.appendChild(doc.createTextNode("\n"));
				}

				mapValue = "";
				mapVariable = "";
				mapValue = "";

			}
		}
	}

	public void addServletElement() {

		if (sipDescriptor != null) {
			//  cretae Sip Descriptor.
			SasPlugin.getDefault().log("Adding servlet elemnet to the Descriptor..");
			servlet = sipDescriptor.createElement(SERVLET);
			servlet.appendChild(sipDescriptor.createTextNode("\n"));

			Element sname = sipDescriptor.createElement(SERVLET_NAME);
			sname.appendChild(sipDescriptor.createTextNode(serName));
			servlet.appendChild(sname);
			servlet.appendChild(sipDescriptor.createTextNode("\n"));

			if (!serName.trim().equals("")) {
				Element dname = sipDescriptor.createElement(DISPLAY_NAME);
				dname.appendChild(sipDescriptor.createTextNode(serName));
				servlet.appendChild(dname);
				servlet.appendChild(sipDescriptor.createTextNode("\n"));
			}

			if (!description.trim().equals("")) {
				Element desc = sipDescriptor.createElement(DESCRIPTION);
				desc.appendChild(sipDescriptor.createTextNode(description));
				servlet.appendChild(desc);
				servlet.appendChild(sipDescriptor.createTextNode("\n"));
			}

			Element sclass = sipDescriptor.createElement(SERVLET_CLASS);
			String servClass = "";
			if (!pacakge.trim().equals("")) {
				servClass = pacakge + "." + servletClass;
			} else {
				servClass = servletClass;
			}
			sclass.appendChild(sipDescriptor.createTextNode(servClass));
			servlet.appendChild(sclass);
			servlet.appendChild(sipDescriptor.createTextNode("\n"));

			model.addChild(sipDescriptor.getDocumentElement(), servlet);
			SasPlugin.getDefault().log("Added servlet elemnet to the Descriptor..");
		}
	}

	public class SipMappingContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			if (!(parentElement instanceof Element))
				return new Object[0];

			Element el = (Element) parentElement;
			ArrayList list = new ArrayList();
			String name = el.getTagName();
			if (name.equals(PATTERN) || name.equals(AND) || name.equals(OR)
					|| name.equals(NOT)) {

				NodeList nodeList = el.getChildNodes();
				for (int i = 0; i < nodeList.getLength(); i++) {
					Node childNode = nodeList.item(i);
					int type = childNode.getNodeType();
					if (type == Node.ELEMENT_NODE) {
						list.add(childNode);
					}
				}
			}

			return list.toArray();
		}

		public Object getParent(Object element) {
			if (!(element instanceof Node))
				return null;

			return ((Node) element).getParentNode();
		}

		public boolean hasChildren(Object element) {
			if (!(element instanceof Node))
				return false;
			Node node = (Node) element;
			return node.hasChildNodes();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		public Object[] getElements(Object inputElement) {
			Document doc = model.getDocument();
			Element[] mappings = (Element[]) inputElement;
			Element[] patterns = new Element[mappings.length];
			for (int i = 0; i < mappings.length; i++) {
				Element el = model.getChild(mappings[i], PATTERN, false);
				if (el == null) {
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

	public class SipMappingLabelProvider extends LabelProvider {

		public String getText(Object element) {
			StringBuffer buffer = new StringBuffer();
			if (!(element instanceof Element))
				return buffer.toString();

			Element el = (Element) element;
			String strName = el.getNodeName();

			if (strName.equals(PATTERN) || strName.equals(AND)
					|| strName.equals(OR) || strName.equals(NOT)) {
				buffer.append(strName);
			} else if (strName.equals(EQUAL) || strName.equals(CONTAINS)
					|| strName.equals(SUB_DOMAIN_OF)) {
				buffer.append("$(");
				buffer.append(model.getChildText(el, VAR));
				buffer.append(").");
				buffer.append(strName);

				String strIgnoreCase = el.getAttribute(IGNORE_CASE);
				strIgnoreCase = (strIgnoreCase == null) ? "" : strIgnoreCase;
				if (strIgnoreCase.equals("true")) {
					buffer.append("IgnoreCase");
				}

				buffer.append("(\"");
				buffer.append(model.getChildText(el, VALUE));
				buffer.append("\")");
			} else if (strName.equals(EXISTS)) {
				buffer.append("$(");
				buffer.append(model.getChildText(el, VAR));
				buffer.append(").exists()");
			}
			return buffer.toString();
		}
	}

	public void mappingChanged(ISelection selection) {
		if (!(selection instanceof IStructuredSelection))
			return;
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj instanceof Element) {
			mappingSelection = (Element) obj;
		}
		addMapping.setEnabled(true); //to clear previous disabling

		String selectionName = mappingSelection.getTagName();
		if (selectionName.equals(AND) || selectionName.equals(OR)) {
			addMapping.setEnabled(true);
		}

		if (selectionName.equals(PATTERN) || selectionName.equals(NOT)) {
			NodeList children = mappingSelection.getChildNodes();
			for (int i = 0; children != null && i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child != null && child.getNodeType() == Node.ELEMENT_NODE
						&& CONDITION_LIST.contains(child.getNodeName())) {
					addMapping.setEnabled(false);
					break;
				}
			}

		}

		if (selectionName.equals(EQUAL) || selectionName.equals(CONTAINS)
				|| selectionName.equals(EXISTS)
				|| selectionName.equals(SUB_DOMAIN_OF)) {
			addMapping.setEnabled(false);
		}

	}

	public boolean isNewMapping() {
		boolean value = true;
		NodeList nodes = model.getChildren(SERVLET_MAPPING);
		Object[] elements = new Object[nodes.getLength()];
		for (int i = 0; nodes != null && i < nodes.getLength(); i++) {
			Element mapping = (Element) nodes.item(i);
			String tmpServletName = model.getChildText(mapping, SERVLET_NAME);
            SasPlugin.getDefault().log("IsNewMapping() :The servlet name is "+serName +"The temp servlet name is..."+tmpServletName);
			if (!serName.trim().equals("") && serName.equals(tmpServletName)) {
				value = false;
			} 
//				else {
//				value = true;
		}
	
		return value;
	}

	public void updateList() {
		ArrayList list = new ArrayList();
		NodeList servlets = model.getChildren(SERVLET);
		NodeList nodes = model.getChildren(SERVLET_MAPPING);
		for (int i = 0; nodes != null && i < nodes.getLength(); i++) {
			Element mapping = (Element) nodes.item(i);
			String tmpServletName = model.getChildText(mapping, SERVLET_NAME);
			if (!serName.trim().equals("") && serName.equals(tmpServletName)) { //tmpServletName
				list.add(mapping);
			}
		}

		Element[] elements = new Element[list.size()];
		this.mappingViewer.setInput(list.toArray(elements));
		this.mappingViewer.refresh();

	}

	Composite composite = null;

	String initParamName = null;

	String initParamValue = null;

	String initParamDescr = "";

	String mapCondition = "";

	String mapVariable = "";

	String mapValue = "";

	List param = null;

	Tree mapping = null;

	Menu listMenu = null;

	Text servletName = null;

	Text displayName = null;

	String serName = "";

	Button addMapping = null;

	Text desc = null;

	String description = null;

	//array lists for Init params

	ArrayList paramName = null;

	ArrayList paramValue = null;

	ArrayList paramDesc = null;

	BPClassWizard wizard = null;

	Button removeMapping = null;

	Button removeInitParam = null;

	public AddInitParamsDialog m_dialog = null;

	String pacakge = "";

	TreeViewer mappingViewer = null;

	String operatorSelected = "";

	Element elPattern = null;

	XMLModel model = null;

	Element mappingSelection = null;

	Document sipDescriptor = null;

	String servletClass = null;

	Element servlet = null;

	boolean servletElementFlag = false;

}
