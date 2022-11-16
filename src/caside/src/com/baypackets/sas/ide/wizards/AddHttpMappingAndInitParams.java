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
//Author@Reeta Aggarwal
/* This class is used for adding init parameters and Http parameters
 * to the Http Servlet.This class servers as the Secong page for HttpServletWizard
 * for adding New Servlet
 */
package com.baypackets.sas.ide.wizards;

import org.eclipse.swt.events.SelectionAdapter;
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
import java.util.ArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.baypackets.sas.ide.editor.XMLEditor;
import com.baypackets.sas.ide.editor.model.XMLModel;
import com.baypackets.sas.ide.editor.model.XmlMetaData;
import javax.xml.parsers.DocumentBuilder;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import java.io.*;

import org.eclipse.core.runtime.IProgressMonitor;
import com.baypackets.sas.ide.SasPlugin;

public class AddHttpMappingAndInitParams extends NewTypeWizardPage {

	private static final String SERVLET = "servlet".intern();

	private static final String SERVLET_NAME = "servlet-name".intern();

	private static final String DESCRIPTION = "description".intern();

	private static final String DISPLAY_NAME = "display-name".intern();

	private static final String SERVLET_CLASS = "servlet-class".intern();

	private static final String SERVLET_MAPPING = "servlet-mapping".intern();

	private static final String URL_PATTERN = "url-pattern".intern();

	private static final String INIT_PARAM = "init-param".intern();

	private static final String PARAM_NAME = "param-name".intern();

	private static final String PARAM_VALUE = "param-value".intern();

	String[] specialChar = new String[] { ";", ",", ".", ":", "?", "{", "}",
			"[", "]", "(", ")", "/", "<", ">", "#", "$", "%", "^", "&", "*",
			"!", "@", "-", "+", "=", "|", "~", "`" };

	public AddHttpMappingAndInitParams(BPClassWizard wizard) {
		super(true, "Add Params");
		setTitle("Add Mapping and Init Parameters to Http Servlet");
		this.wizard = wizard;
		setDescription("This Data will be added to web.xml");
	}

	protected void init() {
		paramName = new ArrayList(20);
		paramValue = new ArrayList(20);
		paramDesc = new ArrayList(20);
		urlPtrn = new ArrayList(20);
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
	 * This method is invoked from the BpHttpServletPage when the type name is
	 * entered from the first page to set the
	 */
	public void setServletNameAndMapping(String serName) {
		servletName.setText(serName);
		String mappingView = "/" + serName + "\n";
		// show pattern in list
		urlPattern = "/" + serName;
		// added for setting the mapping after it has entered fully
		if (mapping.getItemCount() != 0 && (!urlPtrn.isEmpty())) {
			mapping.removeAll();
			urlPtrn.clear();
		}
		servletClass = serName;
		urlPtrn.add(urlPattern);
		mapping.add(urlPattern);

		String srcPath = this.wizard.getFirstPage()
				.getPackageFragmentRootText();
		pacakge = this.wizard.getFirstPage().getPackageText();
		int indunix = srcPath.indexOf("/");
		String projectName = null;
		// Extract project name from PackageFragmentRootText as per window or
		// unix
		if (indunix != -1) {
			projectName = srcPath.substring(0, indunix);
		}else {
			projectName = srcPath;
		}

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				projectName);
		IFolder webInfFolder = project.getFolder("WEB-INF");

		IFile webDesc = webInfFolder.getFile("web.xml");

		InputStream stream = null;
		try {
			DocumentBuilder docBuilder = XMLModel.FACTORY.newDocumentBuilder();
			docBuilder.setErrorHandler(XMLModel.ERROR_HANDLER);
			docBuilder.setEntityResolver(XmlMetaData.ENTITY_RESOLVER);
			stream = webDesc.getContents();
			webDescriptor = docBuilder.parse(stream);
		} catch (Exception e) {
			String st[] = new String[] { "OK" };
			MessageDialog dia = new MessageDialog(this.getShell(),
					"Add Http Servlet", null, "No web.xml descriptor found",
					MessageDialog.WARNING, st, 0);
			dia.open();
			SasPlugin.getDefault().log(
					"Exception thrown by setServletNameAndMapping() :AddHttpMappingAndInitParams.java"
							+ e);
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {

			}
		}
		model = new XMLModel(webDescriptor, webDesc.getName(), webDesc);
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
		lable.setText("URL Mapping:");
		lable.setLayoutData(gridD);

		GridData gridData3 = new GridData(GridData.FILL_BOTH);
		mapping = new List(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP);
		gridData3.horizontalSpan = 4;
		mapping.setLayoutData(gridData3);
		mapping.addListener(SWT.Selection, new Listener() {

			public void handleEvent(Event e) {
				if (mapping.getSelection() != null) {
					removeMapping.setEnabled(true);

				}
			}
		});

		GridData gridData2 = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		Button add = new Button(composite, SWT.PUSH);
		add.setText("Add");
		add.setLayoutData(gridData2);

		add.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent event) {

				sendErrorMessage(null); // To clear any error message set by
										// remove mapping
				org.eclipse.jface.dialogs.InputDialog dialog = new org.eclipse.jface.dialogs.InputDialog(
						composite.getShell(), "URL Mapping", "Pattern:", "",
						null);
				dialog.create();
				dialog.open();
				if (dialog.getReturnCode() == Window.OK) {
					urlPattern = dialog.getValue();
					urlPtrn.add(urlPattern);
					// show mapping in list
					mapping.add(urlPattern);

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

				if (mapping.getSelection() != null) {

					if (mapping.getItemCount() == 1) {
						sendErrorMessage("There should at least one mapping for a Http Servlet");
					} else {
						sendErrorMessage(null); // Clear Error Meaage
						int selectionIndex = mapping.getSelectionIndex();
						urlPtrn.remove(selectionIndex);
						// remove mapping from list
						mapping.remove(selectionIndex);
						removeMapping.setEnabled(false);
					}
				}

			}

		});

		// control for Init Parameters

		// create an empty line
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
		paramList = new List(composite, SWT.MULTI | SWT.BORDER | SWT.WRAP);
		gridData4.horizontalSpan = 4;
		paramList.setLayoutData(gridData4);
		paramList.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (paramList.getSelection() != null) {
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
					composite.getShell(),false);

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
					paramList.add(initParamName+"="+initParamValue+",Description :"+initParamDescr);
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

				if (paramList.getSelection() != null) {

					int i = paramList.getSelectionIndex();
					if (i!=-1 && paramName.get(i) != null) {
						paramName.remove(i);
						paramValue.remove(i);
						paramDesc.remove(i);
					}
					paramList.remove(i);
					removeInitParam.setEnabled(false);
				}
			}

		});

		setControl(composite);

		Dialog.applyDialogFont(composite);

	}

	public void AddFieldToDescriptor(IProgressMonitor monitor) {
		
		if (webDescriptor != null) {
			// create Web Descriptor.
			Element servlet = webDescriptor.createElement(SERVLET);
			servlet.appendChild(webDescriptor.createTextNode("\n"));

			Element sname = webDescriptor.createElement(SERVLET_NAME);
			sname.appendChild(webDescriptor.createTextNode(serName));
			servlet.appendChild(sname);
			servlet.appendChild(webDescriptor.createTextNode("\n"));

//			if (!serName.trim().equals("")) {
//				Element dname = webDescriptor.createElement(DISPLAY_NAME);
//				dname.appendChild(webDescriptor.createTextNode(serName));
//				servlet.appendChild(dname);
//				servlet.appendChild(webDescriptor.createTextNode("\n"));
//			}

			if (!description.trim().equals("")) {
				Element desc = webDescriptor.createElement(DESCRIPTION);
				desc.appendChild(webDescriptor.createTextNode(description));
				servlet.appendChild(desc);
				servlet.appendChild(webDescriptor.createTextNode("\n"));
			}

			Element sclass = webDescriptor.createElement(SERVLET_CLASS);

			String servClass = "";
			if (!pacakge.trim().equals("")) {
				servClass = pacakge + "." + servletClass;
			} else {
				servClass = servletClass;
			}
			sclass.appendChild(webDescriptor.createTextNode(servClass));
			servlet.appendChild(sclass);
			servlet.appendChild(webDescriptor.createTextNode("\n"));

			// adding init params to descriptor
			if (paramName.isEmpty() == false) {
				for (int i = 0; i < paramName.size(); i++) {
					String name = (String) paramName.get(i);
					String value = (String) paramValue.get(i);
					String desc = (String) paramDesc.get(i);

					Element cpara = webDescriptor.createElement(INIT_PARAM);
					cpara.appendChild(webDescriptor.createTextNode("\n"));
					Element pnam = webDescriptor.createElement(PARAM_NAME);
					pnam.appendChild(webDescriptor.createTextNode(name));
					cpara.appendChild(pnam);
					cpara.appendChild(webDescriptor.createTextNode("\n"));

					Element pval = webDescriptor.createElement(PARAM_VALUE);
					pval.appendChild(webDescriptor.createTextNode(value));
					cpara.appendChild(pval);
					cpara.appendChild(webDescriptor.createTextNode("\n"));

					if (!desc.trim().equals("")) {
						Element descri = webDescriptor
								.createElement(DESCRIPTION);
						descri.appendChild(webDescriptor.createTextNode(desc));
						cpara.appendChild(descri);
						cpara.appendChild(webDescriptor.createTextNode("\n"));
					}

					servlet.appendChild(cpara);
					if (i != (paramName.size() - 1)) {
						servlet.appendChild(webDescriptor.createTextNode("\n")); // added
																					// for
																					// space
																					// between
																					// multiple
																					// init
																					// params
					}
				}
			}
			if (paramName.isEmpty() == false) {
				servlet.appendChild(webDescriptor.createTextNode("\n")); // added
																			// for
																			// space
																			// between
																			// last
																			// (/init-param)
																			// tag
																			// and
																			// (/servlet)
																			// tag
			}
			model.addChild(webDescriptor.getDocumentElement(), servlet);

			// adding mappings to descriptor
			Element cparam = null;
			if (urlPtrn.isEmpty() == false) {
				for (int i = 0; i < urlPtrn.size(); i++) {
					urlPattern = (String) urlPtrn.get(i);
					cparam = webDescriptor.createElement(SERVLET_MAPPING);
					cparam.appendChild(webDescriptor.createTextNode("\n"));

					Element pname = webDescriptor.createElement(SERVLET_NAME);
					pname.appendChild(webDescriptor.createTextNode(serName));
					cparam.appendChild(pname);
					cparam.appendChild(webDescriptor.createTextNode("\n"));

					Element pvalue = webDescriptor.createElement(URL_PATTERN);
					pvalue
							.appendChild(webDescriptor
									.createTextNode(urlPattern));
					cparam.appendChild(pvalue);
					cparam.appendChild(webDescriptor.createTextNode("\n"));
					model.addChild(webDescriptor.getDocumentElement(), cparam); // add
																				// to
																				// web
																				// descriptor

				}

			}

			try {
				XMLEditor editor = new XMLEditor(model);
				editor.doSave(monitor);
			} catch (Exception e) {

			}

		}

	}

	Composite composite = null;

	String initParamName = null;

	String initParamValue = null;

	String initParamDescr = "";

	String urlPattern = null;

	List paramList = null;

	List mapping = null;

	Text servletName = null;

	String serName = "";

	Text displayName = null;

	Text desc = null;

	String description = null;

	ArrayList paramName = null;

	ArrayList paramValue = null;

	ArrayList paramDesc = null;

	ArrayList urlPtrn = null;

	BPClassWizard wizard = null;

	Button removeMapping = null;

	Button removeInitParam = null;

	public AddInitParamsDialog m_dialog = null;

	String pacakge = "";

	String servletClass = "";
	
	Document webDescriptor = null;
	XMLModel model=null;
}
