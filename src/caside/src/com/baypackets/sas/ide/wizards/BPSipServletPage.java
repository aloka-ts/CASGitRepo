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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.core.runtime.IStatus;
public class BPSipServletPage extends BPClassCreationPage {
	
	public static final String LINE_DELIMITER = "\n";
	
	private static final String[] NULL_PARAMS = new String[0];
	private static final String VOID_TYPE = "void".intern();
	private static final String IO_EXCEPTION = "java.io.IOException".intern();
	private static final String SERVLET_EXCEPTION = "javax.servlet.ServletException".intern();
	private static final String SERVLET_REQUEST = "javax.servlet.ServletRequest".intern();
	private static final String SERVLET_RESPONSE = "javax.servlet.ServletResponse".intern();
	private static final String SIP_SERVLET_REQUEST = "javax.servlet.sip.SipServletRequest".intern();
	private static final String SIP_SERVLET_RESPONSE = "javax.servlet.sip.SipServletResponse".intern();
	
	private static final String[] EXCEPTION_TYPES = new String[] {SERVLET_EXCEPTION, IO_EXCEPTION};
	private static final String[] REQ_PARAM_TYPES = new String[] {SIP_SERVLET_REQUEST};
	private static final String[] REQ_PARAM_NAMES = new String[] {"request".intern()};

	private static final String[] RESP_PARAM_TYPES = new String[] {SIP_SERVLET_RESPONSE};
	private static final String[] RESP_PARAM_NAMES = new String[] {"response".intern()};

	private static final String[] SERV_PARAM_TYPES = new String[] {SERVLET_REQUEST, SERVLET_RESPONSE};
	private static final String[] SERV_PARAM_NAMES = new String[] {"request".intern(), "response".intern()};
	public String typeName=null; //reeta added it
	private AddSipMappingAndInitParams nextPage;
	public BPSipServletPage() {
		setTitle("New SIP Servlet");
		setDescription("Creates a New SIP Servlet Class");
	}
	
	
//	this method is added by reeta for setting servlet name to the AddSipMappingAndInitParams page
	protected IStatus typeNameChanged(){
		typeName=this.getTypeName();
		IStatus status=super.typeNameChanged();
		if(nextPage!=null){	
		   nextPage.setServletName(typeName);
		}
		if(next289Page!=null){	
			next289Page.setServletName(typeName);
			}
		return status;
	}
	
	protected void createTypeMembers(IType type, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
		
		super.createTypeMembers(type, imports, monitor);
		//Create life Cycle methods.
		if(this.bInit){
			super.createMethod(type, imports, monitor, "init", NULL_PARAMS, NULL_PARAMS, new String[]{SERVLET_EXCEPTION}, VOID_TYPE, null);
		}
		if(this.bDestroy){
			super.createMethod(type, imports, monitor, "destroy", NULL_PARAMS, NULL_PARAMS, NULL_PARAMS, VOID_TYPE, null);
		}

		//Create the Service method.
		if(this.bService){
			super.createMethod(type, imports, monitor, "service", SERV_PARAM_TYPES, SERV_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}

		//Create Request methods.....
		if(this.bRequest){
			super.createMethod(type, imports, monitor, "doRequest", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bInvite){
			super.createMethod(type, imports, monitor, "doInvite", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bAck){
			super.createMethod(type, imports, monitor, "doAck", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bOptions){
			super.createMethod(type, imports, monitor, "doOptions", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bBye){
			super.createMethod(type, imports, monitor, "doBye", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bCancel){
			super.createMethod(type, imports, monitor, "doCancel", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bRegister){
			super.createMethod(type, imports, monitor, "doRegister", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bSubscribe){
			super.createMethod(type, imports, monitor, "doSubscribe", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bNotify){
			super.createMethod(type, imports, monitor, "doNotify", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bMessage){
			super.createMethod(type, imports, monitor, "doMessage", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bInfo){
			super.createMethod(type, imports, monitor, "doInfo", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bPrack){
			super.createMethod(type, imports, monitor, "doPrack", REQ_PARAM_TYPES, REQ_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}

		//Create the response methods
		if(this.bResponse){
			super.createMethod(type, imports, monitor, "doResponse", RESP_PARAM_TYPES, RESP_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.b1xx){
			super.createMethod(type, imports, monitor, "doProvisionalResponse", RESP_PARAM_TYPES, RESP_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.b2xx){
			super.createMethod(type, imports, monitor, "doSuccessResponse", RESP_PARAM_TYPES, RESP_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.b3xx){
			super.createMethod(type, imports, monitor, "doRedirectResponse", RESP_PARAM_TYPES, RESP_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
		if(this.bErrorResp){
			super.createMethod(type, imports, monitor, "doErrorResponse", RESP_PARAM_TYPES, RESP_PARAM_NAMES, EXCEPTION_TYPES, VOID_TYPE, null);
		}
	
		//Call the super class's implementation....
		super.createInheritedMethods(type, this.bConstructors, this.bAbstractMethods, imports, new SubProgressMonitor(monitor, 1));
	
		if (monitor != null) {
			monitor.done();
		}	
	}

	protected void createCustomControls(Composite composite, int nColumns) {
		
		setSuperClass("javax.servlet.sip.SipServlet", true);
		
		this.createCommonMethodControls(composite, nColumns);
		this.createRequestMethodControls(composite, nColumns);
		this.createResponseMethodControls(composite, nColumns);
	}
	
	private void createCommonMethodControls(Composite parent, int nColumns){
		//Create the label.
		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
		label.setText("Which common method stubs would you like to create?");
		GridData gd1= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd1.horizontalSpan= nColumns;
		label.setLayoutData(gd1);
		
		//Create an empty space.
		this.createEmptySpace(parent, 1);
		
		//Create the controls from the second column.
		Composite group = new Composite(parent, SWT.NONE);
		GridData gd2= new GridData();
		gd2.horizontalSpan= nColumns - 1;
		group.setLayoutData(gd2);
	
		GridLayout layout= new GridLayout();
		layout.makeColumnsEqualWidth= true;
		layout.numColumns= 3;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		group.setLayout(layout);
		
		btnConstructors = this.createButton(group, "Constructors from super class", bConstructors);
		btnAbstractMethods = this.createButton(group, "Inherited Abstract Methods", bAbstractMethods);
		btnInit = this.createButton(group, "init method", bInit);
		btnDestroy = this.createButton(group, "destroy method", bDestroy);
		btnService = this.createButton(group, "service method", bService);
	}
	
	private void createRequestMethodControls(Composite parent, int nColumns){
		//Create the label.
		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
		label.setText("Which request method stubs would you like to create?");
		GridData gd1= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd1.horizontalSpan= nColumns;
		label.setLayoutData(gd1);
		
		//Create an empty space.
		this.createEmptySpace(parent, 1);
		
		//Create the controls from the second column.
		Composite group = new Composite(parent, SWT.NONE);
		GridData gd2= new GridData();
		gd2.horizontalSpan= nColumns - 1;
		group.setLayoutData(gd2);
	
		GridLayout layout= new GridLayout();
		layout.makeColumnsEqualWidth= true;
		layout.numColumns= 4;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		group.setLayout(layout);
		
		btnDoInvite = this.createButton(group, "doInvite method", bInvite);
		btnDoAck = this.createButton(group, "doAck method", bAck);
		btnDoOptions = this.createButton(group, "doOptions method", bOptions);
		btnDoBye = this.createButton(group, "doBye method", bBye);
		btnDoCancel  = this.createButton(group, "doCancel method", bCancel);
		btnDoRegister = this.createButton(group, "doRegister method", bRegister);
		btnDoSubscribe = this.createButton(group, "doSubscribe method", bSubscribe);
		btnDoNotify = this.createButton(group, "doNotify method", bNotify);
		btnDoMessage = this.createButton(group, "doMessage method", bMessage);
		btnDoInfo = this.createButton(group, "doInfo method", bInfo);
		btnDoPrack  = this.createButton(group, "doPrack method", bPrack);
		btnRequest  = this.createButton(group, "doRequest method", bRequest);
	}

	private void createResponseMethodControls(Composite parent, int nColumns){
		//Create the label.
		Label label = new Label(parent, SWT.LEFT | SWT.WRAP);
		label.setText("Which response method stubs would you like to create?");
		GridData gd1= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd1.horizontalSpan= nColumns;
		label.setLayoutData(gd1);
		
		//Create an empty space.
		this.createEmptySpace(parent, 1);
		
		//Create the controls from the second column.
		Composite group = new Composite(parent, SWT.NONE);
		GridData gd2= new GridData();
		gd2.horizontalSpan= nColumns - 1;
		group.setLayoutData(gd2);
	
		GridLayout layout= new GridLayout();
		layout.makeColumnsEqualWidth= true;
		layout.numColumns= 3;
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		group.setLayout(layout);
		
		btnDo1xx = this.createButton(group, "doProvisionalResponse method", b1xx);
		btnDo2xx = this.createButton(group, "doSuccessResponse method", b2xx);
		btnDo3xx = this.createButton(group, "doRedirectResponse method", b3xx);
		btnErrorResp = this.createButton(group, "doErrorResponse method", bErrorResp);
		btnResponse = this.createButton(group, "doResponse method", bResponse);
	}
	
	private Button createButton(Composite parent, String text, boolean select){
		Button btn = new Button(parent, SWT.CHECK | SWT.LEFT);
		btn.setLayoutData(new GridData());
		btn.setSelection(false);
		btn.setText(text);
		btn.setSelection(select);
		btn.addSelectionListener(this.selectionListener);
		return btn;
		
	}
	
	private SelectionListener selectionListener = new SelectionAdapter(){
    	public void widgetSelected(SelectionEvent event) {
    		checkSelection();
	    }
    };

	private AddSip289InitParams next289Page;
    
    private void checkSelection(){
    	bConstructors = btnConstructors.getSelection();
		bAbstractMethods = btnAbstractMethods.getSelection();
		bInit = btnInit.getSelection();
		bDestroy = btnDestroy.getSelection();
		bService = btnService.getSelection();
		
		bInvite = btnDoInvite.getSelection();
		bAck = btnDoAck.getSelection();
		bOptions = btnDoOptions.getSelection();
		bBye = btnDoBye.getSelection();
		bCancel = btnDoCancel.getSelection();
		bRegister = btnDoRegister.getSelection();
		bSubscribe = btnDoSubscribe.getSelection();
		bNotify = btnDoNotify.getSelection();
		bMessage = btnDoMessage.getSelection();
		bInfo = btnDoInfo.getSelection();
		bPrack = btnDoPrack.getSelection();
		bRequest = btnRequest.getSelection();
		
		b1xx = btnDo1xx.getSelection();
		b2xx = btnDo2xx.getSelection();
		b3xx = btnDo3xx.getSelection();
		bErrorResp = btnErrorResp.getSelection();
		bResponse = btnResponse.getSelection();
    }
    
    //added by reeta
    public void setNextSipPage(AddSipMappingAndInitParams page){
    	nextPage=page;
     }
    
    public void setNextSip289Page(AddSip289InitParams page){
    	next289Page=page;
     }
    
	private Button btnConstructors;
	private boolean bConstructors = true;
	private Button btnAbstractMethods;
	private boolean bAbstractMethods = true;
	
	private Button btnInit;
	private boolean bInit = false;
	private Button btnDestroy;
	private boolean bDestroy = false;
	private Button btnService;
	private boolean bService = false;
	
	private Button btnDoInvite;
	private boolean bInvite = false;
	private Button btnDoAck;
	private boolean bAck = false;
	private Button btnDoOptions;
	private boolean bOptions = false;
	private Button btnDoBye;
	private boolean bBye = false;
	private Button btnDoCancel;
	private boolean bCancel = false;
	private Button btnDoRegister;
	private boolean bRegister = false;
	private Button btnDoSubscribe;
	private boolean bSubscribe = false;
	private Button btnDoNotify;
	private boolean bNotify = false;
	private Button btnDoMessage;
	private boolean bMessage = false;
	private Button btnDoInfo;
	private boolean bInfo = false;
	private Button btnDoPrack;
	private boolean bPrack = false;
	private Button btnRequest;
	private boolean bRequest = false;
	
	private Button btnDo1xx;
	private boolean b1xx = false;
	private Button btnDo2xx;
	private boolean b2xx = false;
	private Button btnDo3xx;
	private boolean b3xx = false;
	private Button btnErrorResp;
	private boolean bErrorResp = false;
	private Button btnResponse;
	private boolean bResponse = false;
	@Override
	protected void AddFieldToDescriptor(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}
}
