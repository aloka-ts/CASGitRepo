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

package com.baypackets.sas.ide.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.XMLEditor;
import com.baypackets.sas.ide.editor.model.XMLModel;
import com.baypackets.sas.ide.editor.model.XmlMetaData;

public class BPRAMessageHandlerPage extends BPClassCreationPage {

	public static final String LINE_DELIMITER = "\n";

	private static final String[] NULL_PARAMS = new String[0];
	private static final String VOID_TYPE = "void".intern();
	private static final String RESOURCE_EXCEPTION = "com.baypackets.ase.resource.ResourceException"
			.intern();

	private static final String MESSAGE_HANDLER_IF = "com.baypackets.ase.resource.MessageHandler"
			.intern();
	private static final String RF_MESSAGE_HANDLER_IF = "com.baypackets.ase.ra.diameter.rf.RfMessageHandler"
			.intern();
	private static final String RO_MESSAGE_HANDLER_IF = "com.baypackets.ase.ra.diameter.ro.RoMessageHandler"
			.intern();
	private static final String SH_MESSAGE_HANDLER_IF = "com.baypackets.ase.ra.diameter.sh.ShMessageHandler"
			.intern();
	private static final String GY_MESSAGE_HANDLER_IF = "com.baypackets.ase.ra.diameter.gy.GyMessageHandler"
			.intern();

	private Map<String, String> resourceHandlerNameMapping = null;
	private Map<String, String> resourceFacoryNameMapping = null;

	private static final String RF_ACCOUNTIG_REQ = "com.baypackets.ase.ra.diameter.rf.RfAccountingRequest"
			.intern();
	private static final String[] RF_PARAM_TYPES = new String[] { RF_ACCOUNTIG_REQ };
	private static final String[] RF_PARAM_NAMES = new String[] { "accountingReq"
			.intern() };

	private static final String RO_ACCOUNTIG_REQ = " com.baypackets.ase.ra.diameter.ro.CreditControlRequest"
			.intern();
	private static final String[] RO_PARAM_TYPES = new String[] { RO_ACCOUNTIG_REQ };
	private static final String[] RO_PARAM_NAMES = new String[] { "ccRequest"
			.intern() };

	private static final String SH_REQ = " com.baypackets.ase.ra.diameter.sh.ShRequest"
			.intern();
	private static final String[] SH_PARAM_TYPES = new String[] { SH_REQ };
	private static final String[] SH_PARAM_NAMES = new String[] { "shRequest"
			.intern() };

	private static final String GY_CCR_REQ = " com.baypackets.ase.ra.diameter.gy.CreditControlRequest"
			.intern();
	private static final String[] GY_PARAM_TYPES = new String[] { GY_CCR_REQ };
	private static final String[] GY_PARAM_NAMES = new String[] { "ccRequest"
			.intern() };

	private static final String SERVLET_CONTEXT = "javax.servlet.ServletContext"
			.intern();
	private static final String MESSAGE = "com.baypackets.ase.resource.Message"
			.intern();

	private static final String[] EXCEPTION_TYPES = new String[] { RESOURCE_EXCEPTION };
	private static final String[] INIT_PARAM_TYPES = new String[] { SERVLET_CONTEXT };
	private static final String[] INIT_PARAM_NAMES = new String[] { "context"
			.intern() };

	private static final String[] HANDLE_MESSAGE_PARAM_TYPES = new String[] { MESSAGE };
	private static final String[] HANDLE_MESSAGE_NAMES = new String[] { "message"
			.intern() };

	private boolean bRf;

	private Button btnRo;

	private boolean bRo;

	private Button btnRf;

	private boolean bSh;

	private boolean bGy;

	private Button btnGy;

	private Button btnSh;

	public BPRAMessageHandlerPage() {
		setTitle("New Message Handler");
		setDescription("Creates a New Message Handler Class");
		resourceHandlerNameMapping = new HashMap<String, String>();
		resourceHandlerNameMapping.put(RF_MESSAGE_HANDLER_IF, RF_RA);
		resourceHandlerNameMapping.put(RO_MESSAGE_HANDLER_IF, RO_RA);
		resourceHandlerNameMapping.put(SH_MESSAGE_HANDLER_IF, SH_RA);
		resourceHandlerNameMapping.put(GY_MESSAGE_HANDLER_IF, GY_RA);
		
		resourceFacoryNameMapping = new HashMap<String, String>();
		resourceFacoryNameMapping.put(RF_RA,RF_FACTORY);
		resourceFacoryNameMapping.put(RO_RA,RO_FACTORY);
		resourceFacoryNameMapping.put(SH_RA,SH_FACTORY);
		resourceFacoryNameMapping.put(GY_RA,GY_FACTORY);
		
	}

	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {

		super.createTypeMembers(type, imports, monitor);
		super.createMethod(type, imports, monitor, "init", INIT_PARAM_TYPES,
				INIT_PARAM_NAMES, NULL_PARAMS, VOID_TYPE, null);
		super.createMethod(type, imports, monitor, "handleMessage",
				HANDLE_MESSAGE_PARAM_TYPES, HANDLE_MESSAGE_NAMES,
				EXCEPTION_TYPES, VOID_TYPE, null);
		super.createMethod(type, imports, monitor, "destroy", NULL_PARAMS,
				NULL_PARAMS, NULL_PARAMS, VOID_TYPE, null);

		if (bRf) {

			super.createMethod(type, imports, monitor,
					"handleEventRecordRequest", RF_PARAM_TYPES, RF_PARAM_NAMES,
					EXCEPTION_TYPES, VOID_TYPE, null);
			super.createMethod(type, imports, monitor,
					"handleStartRecordRequest", RF_PARAM_TYPES, RF_PARAM_NAMES,
					EXCEPTION_TYPES, VOID_TYPE, null);
			super.createMethod(type, imports, monitor,
					"handleInterimRecordRequest", RF_PARAM_TYPES,
					RF_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
			super.createMethod(type, imports, monitor,
					"handleStopRecordRequest", RF_PARAM_TYPES, RF_PARAM_NAMES,
					EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if (bRo) {

			super.createMethod(type, imports, monitor, "handleEventCCRRequest",
					RO_PARAM_TYPES, RO_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE,
					null);
			super.createMethod(type, imports, monitor,
					"handleInitialCCRRequest", RO_PARAM_TYPES, RO_PARAM_NAMES,
					EXCEPTION_TYPES, VOID_TYPE, null);
			super.createMethod(type, imports, monitor,
					"handleInterimCCRRequest", RO_PARAM_TYPES, RO_PARAM_NAMES,
					EXCEPTION_TYPES, VOID_TYPE, null);
			super.createMethod(type, imports, monitor,
					"handleTerminationCCRRequest", RO_PARAM_TYPES,
					RO_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if (bSh) {

			super.createMethod(type, imports, monitor, "doUDR", SH_PARAM_TYPES,
					SH_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
			super.createMethod(type, imports, monitor, "doPUR", SH_PARAM_TYPES,
					SH_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
			super.createMethod(type, imports, monitor, "doSNR", SH_PARAM_TYPES,
					SH_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}

		if (bGy) {

			super.createMethod(type, imports, monitor, "handleEventCCRRequest",
					GY_PARAM_TYPES, GY_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE,
					null);
			super.createMethod(type, imports, monitor,
					"handleInitialCCRRequest", GY_PARAM_TYPES, GY_PARAM_NAMES,
					EXCEPTION_TYPES, VOID_TYPE, null);
			super.createMethod(type, imports, monitor,
					"handleInterimCCRRequest", GY_PARAM_TYPES, GY_PARAM_NAMES,
					EXCEPTION_TYPES, VOID_TYPE, null);
			super.createMethod(type, imports, monitor,
					"handleTerminationCCRRequest", GY_PARAM_TYPES,
					GY_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}

		if (monitor != null) {
			monitor.done();
		}
	}

	protected void createCustomControls(Composite composite, int nColumns) {

		ArrayList interfaces = new ArrayList();
		interfaces.add(MESSAGE_HANDLER_IF);
		setSuperClass("java.lang.Object", true);
		this.setSuperInterfaces(interfaces, true);

		this.createRequestMethodControls(composite, nColumns);

	}

	private void createRequestMethodControls(Composite parent, int nColumns) {
		// Create the label.
		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
		label.setText("Which Resource Message Handler you want to create?");
		GridData gd1 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd1.horizontalSpan = nColumns;
		label.setLayoutData(gd1);

		// Create an empty space.
		this.createEmptySpace(parent, 1);

		// Create the controls from the second column.
		Composite group = new Composite(parent, SWT.NONE);
		GridData gd2 = new GridData();
		gd2.horizontalSpan = nColumns - 1;
		group.setLayoutData(gd2);

		GridLayout layout = new GridLayout();
		layout.makeColumnsEqualWidth = true;
		layout.numColumns = 4;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		group.setLayout(layout);

		btnSh = this.createButton(group, "Sh", bSh);
		btnRf = this.createButton(group, "Rf", bRf);
		btnRo = this.createButton(group, "Ro", bRo);
		btnGy = this.createButton(group, "Gy", bGy);
		// btnHttp = this.createButton(group, "HTTP", bHttp);

	}

	//
	//
	//
	private Button createButton(Composite parent, String text, boolean select) {
		Button btn = new Button(parent, SWT.CHECK | SWT.LEFT);
		btn.setLayoutData(new GridData());
		btn.setSelection(false);
		btn.setText(text);
		btn.setSelection(select);
		btn.addSelectionListener(this.selectionListener);
		return btn;

	}

	private SelectionListener selectionListener = new SelectionAdapter() {
		public void widgetSelected(SelectionEvent event) {
			checkSelection();
		}
	};

	private Document casDescriptor;

	private String pacakge;

	private XMLModel model;

	private String messageHandlerName;

	private String handlerClass;

	private void checkSelection() {
		bRf = btnRf.getSelection();
		bRo = btnRo.getSelection();
		bSh = btnSh.getSelection();
		bGy = btnGy.getSelection();
		// bHttp = btnHttp.getSelection();
		ArrayList interfaces = new ArrayList();
		this.setSuperInterfaces(interfaces, true);

		if (bRf) {
			interfaces.add(RF_MESSAGE_HANDLER_IF);
		}
		if (bRo) {
			interfaces.add(RO_MESSAGE_HANDLER_IF);
		}
		if (bSh) {
			interfaces.add(SH_MESSAGE_HANDLER_IF);
		}
		if (bGy) {
			interfaces.add(GY_MESSAGE_HANDLER_IF);

		}
		this.setSuperInterfaces(interfaces, true);

	}

	public void AddFieldToDescriptor(IProgressMonitor monitor) {

		String srcPath = super.getPackageFragmentRootText();
		pacakge = super.getPackageText();
		messageHandlerName = super.getTypeName();

		handlerClass = messageHandlerName;

		if (pacakge != null && !pacakge.equals("")) {
			handlerClass = pacakge + "." + messageHandlerName;
		}
		int indunix = srcPath.indexOf("/");
		String projectName = null;
		// Extract project name from PackageFragmentRootText as per window or
		// unix
		if (indunix != -1) {
			projectName = srcPath.substring(0, indunix);
		} else {
			projectName = srcPath;
		}

		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(projectName);
		IFolder webInfFolder = project.getFolder("WEB-INF");

		IFile casDesc = webInfFolder.getFile("cas.xml");

		InputStream stream = null;
		try {
			DocumentBuilder docBuilder = XMLModel.FACTORY.newDocumentBuilder();
			docBuilder.setErrorHandler(XMLModel.ERROR_HANDLER);
			docBuilder.setEntityResolver(XmlMetaData.ENTITY_RESOLVER);
			stream = casDesc.getContents();
			casDescriptor = docBuilder.parse(stream);
		} catch (Exception e) {
			String st[] = new String[] { "OK" };
			MessageDialog dia = new MessageDialog(this.getShell(),
					"Add Message handler", null, "No cas.xml descriptor found",
					MessageDialog.WARNING, st, 0);
			dia.open();
			SasPlugin.getDefault().log(
					"Exception thrown by AddFieldToDescriptor() :BPRAMessageHandlerPage.java"
							+ e);
		} finally {
			try {
				if (stream != null)
					stream.close();
			} catch (IOException e) {

			}
		}
		model = new XMLModel(casDescriptor, casDesc.getName(), casDesc);

		addMapping(casDescriptor, monitor);
	}

	public void addMapping(Document doc, IProgressMonitor monitor) {
		SasPlugin.getDefault().log("The addMapping...............doc is" + doc);
		if (doc == null)
			return;

		SasPlugin
				.getDefault()
				.log("The adding message handler to the cas.xml.............BPRAMessageHandlerPage.java"
						+ " HandlerName "
						+ messageHandlerName
						+ " Class "
		
						+ handlerClass);
		
		java.util.List<String> superInterfaces = this.getSuperInterfaces();
		
		Element listener=null;
		Element lclass=null;
		
		for (String handler : superInterfaces) {
			String resourceName = resourceHandlerNameMapping.get(handler);
			if (resourceName != null) {
				String factoryname = resourceFacoryNameMapping
						.get(resourceName);
				if (factoryname != null) {
					listener = doc.createElement(RESURCE_FACTORY_MAPPING);
					listener.appendChild(doc.createTextNode("\n"));

					lclass = doc.createElement(FACTORY_NAME);
					lclass.appendChild(doc.createTextNode(factoryname));
					listener.appendChild(lclass);
					listener.appendChild(doc.createTextNode("\n"));

					lclass = doc.createElement(RESOURCE_NAME);
					lclass.appendChild(doc.createTextNode(resourceName));
					listener.appendChild(lclass);
					listener.appendChild(doc.createTextNode("\n"));
					model.addChild(doc.getDocumentElement(), listener);
				}
			}
		}
		
		 listener = doc.createElement(MESSAGE_HANDLER);
		listener.appendChild(doc.createTextNode("\n"));

		 lclass = doc.createElement(HANDLER_NAME);
		lclass.appendChild(doc.createTextNode(messageHandlerName));
		listener.appendChild(lclass);
		listener.appendChild(doc.createTextNode("\n"));

		lclass = doc.createElement(HANDLER_CLASS);
		lclass.appendChild(doc.createTextNode(handlerClass));
		listener.appendChild(lclass);
		listener.appendChild(doc.createTextNode("\n"));

		model.addChild(doc.getDocumentElement(), listener);
		SasPlugin
				.getDefault()
				.log("The adding message handler mapping for selected protocols to cas.xml.............BPRAMessageHandlerPage.java");


		for (String handler : superInterfaces) {

			String resourceName = resourceHandlerNameMapping.get(handler);
			if (resourceName != null) {
				listener = doc.createElement(MESSAGE_HANDLER_MAPPING);
				listener.appendChild(doc.createTextNode("\n"));

				lclass = doc.createElement(HANDLER_NAME);
				lclass.appendChild(doc.createTextNode(messageHandlerName));
				listener.appendChild(lclass);
				listener.appendChild(doc.createTextNode("\n"));

				lclass = doc.createElement(RESOURCE_NAME);
				lclass.appendChild(doc.createTextNode(resourceName));
				listener.appendChild(lclass);
				listener.appendChild(doc.createTextNode("\n"));
				model.addChild(doc.getDocumentElement(), listener);
			}
		}

		SasPlugin.getDefault().log(
				"save the descriptor..............BPRAMessageHandlerPage.java");

		try {
			XMLEditor editor = new XMLEditor(model);
			editor.doSave(monitor);
		} catch (Exception e) {

		}
	}

	public static final String MESSAGE_HANDLER_MAPPING = "message-handler-mapping"
			.intern();
	private static final String RESOURCE_NAME = "resource-name".intern();
	private static final String MESSAGE_HANDLER = "message-handler".intern();
	private static final String HANDLER_NAME = "handler-name".intern();
	private static final String HANDLER_CLASS = "handler-class".intern();
	private static final String RESURCE_FACTORY_MAPPING="resource-factory-mapping";
	private static final String FACTORY_NAME="factory-name";
	private static final String RO_RA = "ro-ra";
	private static final String RF_RA = "ro-ra";
	private static final String SH_RA = "sh-ra";
	private static final String GY_RA = "gy-ra";
	private static final String RO_FACTORY = "RoFactory";
	private static final String RF_FACTORY = "RfFactory";
	private static final String SH_FACTORY = "ShFactory";
	private static final String GY_FACTORY = "GyFactory";
	
	
	
//	"<resource-factory-mapping>"+"\r\n"+
//    "<factory-name>RoFactory</factory-name>"+"\r\n"+
//     "<resource-name>ro-ra</resource-name>"+"\r\n"+
//     "</resource-factory-mapping>"+"\r\n"+

	// <message-handler>
	// <handler-name>test</handler-name>
	// <handler-class>TestMessageHandler</handler-class>
	// </message-handler>
	//
	// <message-handler-mapping>
	// <handler-name>test</handler-name>
	// <resource-name>ro-ra</resource-name>
	// </message-handler-mapping>

}
