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

import javax.xml.parsers.DocumentBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
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


public class BPRAResourceListenerPage extends BPClassCreationPage{
	
public static final String LINE_DELIMITER = "\n";
	
	private static final String[] NULL_PARAMS = new String[0];
	private static final String VOID_TYPE = "void".intern();
	private static final String RESOURCE_EXCEPTION = "com.baypackets.ase.resource.ResourceException".intern();
	private static final String EVENT_HANDLER_IF = "com.baypackets.ase.resource.ResourceListener".intern();
	private static final String EVENT = "com.baypackets.ase.resource.ResourceEvent".intern();

	
	private static final String[] EXCEPTION_TYPES = new String[] {RESOURCE_EXCEPTION};

	private static final String[] HANDLE_EVENT_PARAM_TYPES = new String[] {EVENT};
	private static final String[] HANDLE_EVENT_NAMES = new String[] {"event".intern()};

	public String typeName=null;
	
	public BPRAResourceListenerPage() {
		setTitle("New Resource Listener");
		setDescription("Creates a New Resource Listener Class");
	}
	
	protected void createTypeMembers(IType type, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
	
		super.createTypeMembers(type, imports, monitor);
			super.createMethod(type, imports, monitor, "handleEvent", HANDLE_EVENT_PARAM_TYPES, HANDLE_EVENT_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
	
		if (monitor != null) {
			monitor.done();
		}	
	}

	protected void createCustomControls(Composite composite, int nColumns) {
		
		ArrayList interfaces = new ArrayList();
		interfaces.add(EVENT_HANDLER_IF);
		setSuperClass("java.lang.Object", true);
		this.setSuperInterfaces(interfaces, false);

//		this.createRequestMethodControls(composite, nColumns);
		
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

		IFile casDesc = webInfFolder.getFile("cas.xml");

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
					"Add Message handler", null, "No cas.xml descriptor found",
					MessageDialog.WARNING, st, 0);
			dia.open();
			SasPlugin.getDefault().log(
					"Exception thrown by AddFieldToDescriptor() :BPRAResourceListenerPage.java"
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
			
		
			Element listener = doc.createElement(LISTENER);
			listener.appendChild(doc.createTextNode("\n"));

			Element lclass=doc.createElement(LISTENER_CLASS);
			lclass.appendChild(doc.createTextNode(listClass));
			listener.appendChild(lclass);
			listener.appendChild(doc.createTextNode("\n"));

		
				model.addChild(doc.getDocumentElement(), listener);
		try {
				XMLEditor editor = new XMLEditor(model);
				editor.doSave(monitor);
			} catch (Exception e) {

			}
	//	}
		// }
	}
	
//	
//	private void createRequestMethodControls(Composite parent, int nColumns){
//		//Create the label.
//		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
//		label.setText("Which request method stubs would you like to create?");
//		GridData gd1= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
//		gd1.horizontalSpan= nColumns;
//		label.setLayoutData(gd1);
//		
//		//Create an empty space.
//		this.createEmptySpace(parent, 1);
//		
//		//Create the controls from the second column.
//		Composite group = new Composite(parent, SWT.NONE);
//		GridData gd2= new GridData();
//		gd2.horizontalSpan= nColumns - 1;
//		group.setLayoutData(gd2);
//	
//		GridLayout layout= new GridLayout();
//		layout.makeColumnsEqualWidth= true;
//		layout.numColumns= 4;
//		layout.marginHeight= 0;
//		layout.marginWidth= 0;
//		group.setLayout(layout);
//		
//		btnDoPost = this.createButton(group, "doPost method", bPost);
//		btnDoGet = this.createButton(group, "doGet method", bGet);
//		btnDoPut = this.createButton(group, "doPut method", bPut);
//		btnDoDelete = this.createButton(group, "doDelete method", bDelete);
//		
//	}
//
//	
//	
//	private Button createButton(Composite parent, String text, boolean select){
//		Button btn = new Button(parent, SWT.CHECK | SWT.LEFT);
//		btn.setLayoutData(new GridData());
//		btn.setSelection(false);
//		btn.setText(text);
//		btn.setSelection(select);
//		btn.addSelectionListener(this.selectionListener);
//		return btn;
//		
//	}
//	
//	private SelectionListener selectionListener = new SelectionAdapter(){
//    	public void widgetSelected(SelectionEvent event) {
//    		checkSelection();
//	    }
//    };
//    
//    private void checkSelection(){
//    	bConstructors = btnConstructors.getSelection();
//		bAbstractMethods = btnAbstractMethods.getSelection();
//		bInit = btnInit.getSelection();
//		bDestroy = btnDestroy.getSelection();
//		bService = btnService.getSelection();
//		
//    }
//    
//
//    public void setAddPametersPage(AddHttpMappingAndInitParams page){
//    	nextPage=page;
//     }
//
//	private Button btnConstructors;
//	private boolean bConstructors = true;
//	private Button btnAbstractMethods;
//	private boolean bAbstractMethods = true;
//	
//	private AddHttpMappingAndInitParams nextPage;
//	
//	
//	private Button btnInit;
//	private boolean bInit = false;
//	private Button btnDestroy;
//	private boolean bDestroy = false;
//	private Button btnService;
//	private boolean bService = false;
	
	private static final String LISTENER = "listener".intern();
	private static final String LISTENER_CLASS = "listener-class".intern();
	

}
