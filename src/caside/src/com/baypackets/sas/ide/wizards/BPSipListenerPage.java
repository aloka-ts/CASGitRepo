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
package com.baypackets.sas.ide.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.baypackets.sas.ide.SasPlugin;
import com.baypackets.sas.ide.editor.SipAdd289ListenerSection;
import com.baypackets.sas.ide.editor.XMLEditor;
import com.baypackets.sas.ide.editor.model.XMLModel;
import com.baypackets.sas.ide.editor.model.XmlMetaData;
import com.baypackets.sas.ide.util.IdeUtils;

/**
 * This class creates the SBB Listener
 */

public class BPSipListenerPage extends BPClassCreationPage {
	private static final String[] NULL_PARAMS = new String[0];
	private static final String INT_TYPE = "int".intern();
	private static final String TIMER_LIS = "javax.servlet.sip.TimerListener"
			.intern();
	private static final String APP_SESS_LIS = "javax.servlet.sip.SipApplicationSessionListener"
			.intern();
	private static final String SIP_SESSION_LIS = "javax.servlet.sip.SipSessionListener"
			.intern();
	private static final String SIP_ERROR_LIS = "javax.servlet.sip.SipErrorListener"
			.intern();
	private static final String SIP_APP_SESS_ACT_LIS = "javax.servlet.sip.SipApplicationSessionActivationListener"
			.intern();
	private static final String SIP_SESSION_ACTI_LIS = "javax.servlet.sip.SipSessionActivationListener"
			.intern();
	private static final String VOID_TYPE = "void".intern();

	private static final String TIMER_LIS_TIMEOUT_PARAM = "javax.servlet.sip.ServletTimer"
			.intern();

	// private static final String[] EXCEPTION_TYPES = new String[]
	// {RESOURCE_EXCEPTION};

	private static final String[] TIMER_LIS_TIMEOUT_PARAM_TYPES = new String[] { TIMER_LIS_TIMEOUT_PARAM };
	private static final String[] TIMER_LIS_TIMEOUT_NAMES = new String[] { "timer"
			.intern() };

	public BPSipListenerPage() {
		setTitle("New SIP Listener");
		setDescription("Creates a New SIP Event Listener Class");
	}

	protected void createCustomControls(Composite parent, int nColumns) {

		setSuperClass("java.lang.Object", true);
		this.createTimerControls(parent, nColumns);

	}

	protected void createTypeMembers(IType type, ImportsManager imports,
			IProgressMonitor monitor) throws CoreException {
		super.createTypeMembers(type, imports, monitor);
		if (bTimerListener) {
			super.createMethod(type, imports, monitor, "timeout",
					TIMER_LIS_TIMEOUT_PARAM_TYPES, TIMER_LIS_TIMEOUT_NAMES,
					NULL_PARAMS, VOID_TYPE, null);
		}

		if (bSipSessListener) {
			super.createMethod(type, imports, monitor, "sessionCreated",
					new String[] { "javax.servlet.sip.SipSessionEvent" },
					new String[] { "sessionEvent" }, NULL_PARAMS, VOID_TYPE,
					null);
			super.createMethod(type, imports, monitor, "sessionDestroyed",
					new String[] { "javax.servlet.sip.SipSessionEvent" },
					new String[] { "sessionEvent" }, NULL_PARAMS, VOID_TYPE,
					null);
			super.createMethod(type, imports, monitor,
					"sessionReadyToInvalidate",
					new String[] { "javax.servlet.sip.SipSessionEvent" },
					new String[] { "sessionEvent" }, NULL_PARAMS, VOID_TYPE,
					null);

		}
		if (bAppSessListener) {
			super.createMethod(
					type,
					imports,
					monitor,
					"sessionCreated",
					new String[] { "javax.servlet.sip.SipApplicationSessionEvent" },
					new String[] { "sessionEvent" }, NULL_PARAMS, VOID_TYPE,
					null);
			super.createMethod(
					type,
					imports,
					monitor,
					"sessionDestroyed",
					new String[] { "javax.servlet.sip.SipApplicationSessionEvent" },
					new String[] { "sessionEvent" }, NULL_PARAMS, VOID_TYPE,
					null);
			super.createMethod(
					type,
					imports,
					monitor,
					"sessionExpired",
					new String[] { "javax.servlet.sip.SipApplicationSessionEvent" },
					new String[] { "sessionEvent" }, NULL_PARAMS, VOID_TYPE,
					null);
			super.createMethod(
					type,
					imports,
					monitor,
					"sessionReadyToInvalidate",
					new String[] { "javax.servlet.sip.SipApplicationSessionEvent" },
					new String[] { "sessionEvent" }, NULL_PARAMS, VOID_TYPE,
					null);
		}

		if (bSipErrorListener) {
			super.createMethod(type, imports, monitor, "noAckReceived",
					new String[] { "javax.servlet.sip.SipErrorEvent" },
					new String[] { "errorEvent" }, NULL_PARAMS, VOID_TYPE, null);
			super.createMethod(type, imports, monitor, "noPrackReceived",
					new String[] { "javax.servlet.sip.SipErrorEvent" },
					new String[] { "errorEvent" }, NULL_PARAMS, VOID_TYPE, null);
		}
		if (bSessActiListener) {
			super.createMethod(type, imports, monitor, "sessionDidActivate",
					new String[] { "javax.servlet.sip.SipSessionEvent" },
					new String[] { "sessionEvent" }, NULL_PARAMS, VOID_TYPE,
					null);
			super.createMethod(type, imports, monitor, "sessionWillPassivate",
					new String[] { "javax.servlet.sip.SipSessionEvent" },
					new String[] { "sessionEvent" }, NULL_PARAMS, VOID_TYPE,
					null);
		}
		if (bAppSessActiListener) {
			super.createMethod(
					type,
					imports,
					monitor,
					"sessionDidActivate",
					new String[] { "javax.servlet.sip.SipApplicationSessionEvent" },
					new String[] { "sessionEvent" }, NULL_PARAMS, VOID_TYPE,
					null);
			super.createMethod(
					type,
					imports,
					monitor,
					"sessionWillPassivate",
					new String[] { "javax.servlet.sip.SipApplicationSessionEvent" },
					new String[] { "sessionEvent" }, NULL_PARAMS, VOID_TYPE,
					null);
		}

		if (monitor != null) {
			monitor.done();
		}
	}

	private void createTimerControls(Composite parent, int nColumns) {
		// Create the label.
		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
		label.setText("Which B2B EVENT stubs would you like to create?");
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
		layout.numColumns = 3;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		group.setLayout(layout);

		btnTimerListener = this.createButton(group, "TimerListener",
				bTimerListener);
		btnAppSessListener = this.createButton(group,
				"SipApplictaionSessionListener", bAppSessListener);
		btnSipSessListener = this.createButton(group, "SipSessionListener",
				bSipSessListener);
		btnSipErrorListener = this.createButton(group, "SipErrorListener",
				bSipErrorListener);
		btnAppSessActiListener = this
				.createButton(group, "SipApplicationSessionActivationListener",
						bAppSessActiListener);
		btnSessActiListener = this.createButton(group,
				"SipSessionActivationListener", bSessActiListener);
	}

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

	private void checkSelection() {
		bTimerListener = btnTimerListener.getSelection();
		ArrayList interfaces = new ArrayList();
		if (bTimerListener) {

			interfaces.add(TIMER_LIS);

		}

		bSipErrorListener = btnSipErrorListener.getSelection();
		if (bSipErrorListener) {

			interfaces.add(SIP_ERROR_LIS);

		}

		bAppSessActiListener = btnAppSessActiListener.getSelection();
		if (bAppSessActiListener) {

			interfaces.add(SIP_APP_SESS_ACT_LIS);

		}
		bAppSessListener = btnAppSessListener.getSelection();
		if (bAppSessListener) {

			interfaces.add(APP_SESS_LIS);

		}
		bSipSessListener = btnSipSessListener.getSelection();
		if (bSipSessListener) {

			interfaces.add(SIP_SESSION_LIS);

		}

		bSessActiListener = btnSessActiListener.getSelection();

		if (bSessActiListener) {

			interfaces.add(SIP_SESSION_ACTI_LIS);

		}
		this.setSuperInterfaces(interfaces, false);

	}

	public void AddFieldToDescriptor(IProgressMonitor monitor) {
		// if(bTimerListener){
		String srcPath = super.getPackageFragmentRootText();
		String pacakge = super.getPackageText();
		String listName = super.getTypeName();

		String listClass = listName;

		if (pacakge != null && !pacakge.equals("")) {
			listClass = pacakge + "." + listName;
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

		IFile casDesc = webInfFolder.getFile("sip.xml");

		InputStream stream = null;
		Document sipDescriptor = null;
		try {
			DocumentBuilder docBuilder = XMLModel.FACTORY.newDocumentBuilder();
			docBuilder.setErrorHandler(XMLModel.ERROR_HANDLER);
			docBuilder.setEntityResolver(XmlMetaData.ENTITY_RESOLVER);
			stream = casDesc.getContents();
			sipDescriptor = docBuilder.parse(stream);
		} catch (Exception e) {
			String st[] = new String[] { "OK" };
			MessageDialog dia = new MessageDialog(this.getShell(),
					"Add Message handler", null, "No sip.xml descriptor found",
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
		XMLModel model = new XMLModel(sipDescriptor, casDesc.getName(), casDesc);

		Document doc = model.getDocument();

//		if (doc.getElementsByTagName(LISTENER) == null
//				|| doc.getElementsByTagName(LISTENER).getLength() == 0) {
			
			NodeList servNodes = doc.getElementsByTagName(SERVLET);
			Node firstServNode = null;

			if (servNodes != null && servNodes.getLength() != 0) {
				firstServNode = servNodes.item(0);
			}

			Element listener = doc.createElement(LISTENER);
			listener.appendChild(doc.createTextNode("\n"));

			Element lclass=null;
			if(IdeUtils.is289ProjectNature(IdeUtils.getProjectName(this))){
			 lclass = doc.createElement(SipAdd289ListenerSection.LISTENER_CLASS);
			}else{
				 lclass = doc.createElement(LISTENER_CLASS);
			}
			lclass.appendChild(doc.createTextNode(listClass));
			listener.appendChild(lclass);
			listener.appendChild(doc.createTextNode("\n"));

			if (firstServNode != null) {
				model.insertBefore(doc.getDocumentElement(), listener,
						firstServNode);
			} else {
				model.addChild(doc.getDocumentElement(), listener);
			}
			try {
				XMLEditor editor = new XMLEditor(model);
				editor.doSave(monitor);
			} catch (Exception e) {

			}
	//	}
		// }
	}

	private Button btnAppSessListener;
	private boolean bAppSessListener = false;

	private Button btnSipSessListener;
	private boolean bSipSessListener = false;

	private Button btnTimerListener;
	private boolean bTimerListener = false;

	private Button btnSipErrorListener;
	private boolean bSipErrorListener;
	private Button btnAppSessActiListener;
	private boolean bAppSessActiListener;
	// private Button btnSvltCtxtListener;
	// private boolean bSvltCtxtListener ;

	private boolean bSessActiListener;
	private Button btnSessActiListener;

	private static final String LISTENER = "listener".intern();
	private static final String LISTENER_CLASS = "listener-class".intern();
	private static String SERVLET = "servlet".intern();

}
