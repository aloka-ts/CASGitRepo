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
/*This class is used to add init and mapping parameters to sip.xml
 while creating a servlet this class is the second page for wizard for
 adding new servlet*/
package com.baypackets.sas.ide.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.XMLEditor;
import com.baypackets.sas.ide.editor.model.XMLModel;
import com.baypackets.sas.ide.editor.model.XmlMetaData;

public class AddSip289InitParams extends NewTypeWizardPage {

	public static final String SERVLET = "servlet".intern();
	public static final String SERVLET_CLASS = "javaee:servlet-class".intern();
	public static final String SERVLET_NAME = "javaee:servlet-name".intern();
	public static final String LOAD_ON_STARTUP = "javaee:load-on-startup".intern();

	private static final String INIT_PARAM = "javaee:init-param".intern();

	private static final String PARAM_NAME = "javaee:param-name".intern();

	private static final String PARAM_VALUE = "javaee:param-value".intern();

	String[] specialChar = new String[] { ";", ",", ".", ":", "?", "{", "}",
			"[", "]", "(", ")", "/", "<", ">", "#", "$", "%", "^", "&", "*",
			"!", "@", "-", "+", "=", "|", "~", "`" };
	
	private IProject projectHandle=null;

	public AddSip289InitParams(BPClassWizard wizard,BPSipServletPage firstPage) {
		super(true, "Add Params");
		setTitle("Add Mapping and Init Parameters to SipServlet");
		this.wizard = wizard;
		projectHandle= firstPage.getJavaProject().getProject();
		setDescription("This Data will be added to sip.xml");
	}

	protected void init() {
		paramName = new ArrayList(20);
		paramValue = new ArrayList(20);
	}

	private Listener listener = new Listener() {
		public void handleEvent(Event e) {
			serName = servletName.getText();
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
		servletName.setEditable(false);
//
//		new Label(composite, SWT.LEFT | SWT.WRAP).setText("Description :");
//
//		GridData gridData1 = new GridData(GridData.FILL_HORIZONTAL);
//		desc = new Text(composite, SWT.SINGLE | SWT.BORDER);
//		gridData1.horizontalSpan = 4;
//		desc.setLayoutData(gridData1);
//		desc.setTextLimit(100);
//		desc.addListener(SWT.Modify, listener);

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
					composite.getShell(),true);

			public void widgetSelected(SelectionEvent event) {

				Composite com = (Composite) dialog.createDialogArea(composite);
				dialog.create();
				dialog.open();

				if (dialog.getReturnCode() == Window.OK) {

					initParamName = dialog.getParamName();
					initParamValue = dialog.getParamValue();

					paramName.add(initParamName);
					paramValue.add(initParamValue);

					// show params in list	
					paramList.add(initParamName+"="+initParamValue);
					SasPlugin.getDefault().log("Added :AddSip289InitParams.java" +initParamName);
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
					SasPlugin.getDefault().log("For Removing Selection index is :AddSip289InitParams.java" +i);
					SasPlugin.getDefault().log("For Removing Object at index is :AddSip289InitParams.java" +paramName.get(i));

					if (i!=-1 && paramName.get(i) != null) {
					    SasPlugin.getDefault().log("Removing init param from list :AddSip289InitParams.java" +paramName);
						paramName.remove(i);
						paramValue.remove(i);
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
			//		String desc = (String) paramDesc.get(i);

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



	

	Composite composite = null;

	String initParamName = null;

	String initParamValue = null;

	String initParamDescr = "";

	List paramList = null;

	Menu listMenu = null;

	Text servletName = null;

	Text displayName = null;

	String serName = "";



	ArrayList paramName = null;

	ArrayList paramValue = null;


	BPClassWizard wizard = null;

	Button removeInitParam = null;

	public AddInitParamsDialog m_dialog = null;

	String pacakge = "";
	XMLModel model = null;

	Document sipDescriptor = null;

	String servletClass = null;

	Element servlet = null;

	boolean servletElementFlag = false;

}
