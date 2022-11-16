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
import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.eclipse.swt.widgets.Composite;


public class BPMPHServiceInterfacePage extends BPClassCreationPage{
	
public static final String LINE_DELIMITER = "\n";
	
	private static final String[] NULL_PARAMS = new String[0];
	private static final String VOID_TYPE = "void".intern();
//	private static final String RESOURCE_EXCEPTION = "com.baypackets.ase.resource.ResourceException".intern();
	private static final String EVENT_HANDLER_IF = "com.agnity.ph.common.ServiceInterface".intern();
	private static final String EVENT = "com.agnity.mphdata.common.Event".intern();
	private static final String CALLDATA = "com.agnity.mphdata.common.CallData".intern();
	
	private static final String ACTION="com.agnity.mphdata.common.Action[]";

	private static final String RETURNTYPE= ACTION;
	private static final String[] EXCEPTION_TYPES = new String[0] ;

	private static final String[] PROCESS_EVENT_PARAM_TYPES = new String[] {EVENT,CALLDATA};
	private static final String[] PROCESS_EVENT_NAMES = new String[] {"event".intern(),"callData".intern()};
	
	
	String content="String origLegCallId=(String) callData.get(CallDataAttribute.P_ORIG_LEG_CALL_ID);"
		
			+"\n PhoneNumber dialedDigits = null;"
			+"\n PhoneNumber callingParty = null;"
			+"\n PhoneNumber calledParty = null;"
		+"\n  if (CallDataAttribute.P_LEG1.equals(event.getLeg())) {"
		+"\n   LegData legData = (LegData) callData.get(CallDataAttribute.valueOf(event.getLeg()));"		
		+"\n    dialedDigits = (PhoneNumber)legData.get(LegDataAttributes.P_DIALED_DIGITS);"
		+"\n    callingParty=(PhoneNumber) legData.get(LegDataAttributes.P_CALLING_PARTY);"
	    +"\n    calledParty =(PhoneNumber)legData.get(LegDataAttributes.P_CALLED_PARTY);"
		+"\n};"
		
		+"\nAction[] actionArr=null;"
		
		+"\nswitch(event.getEventType()){"
		
		+"\ncase EVENT_INITIAL:" +"\nif (logger.isInfoEnabled()) {"
				+"\nlogger.info(\"Entering initial event received \");"
			+"\n}"
			
			+"\nbreak;"
			
		+"\ncase EVENT_SUCCESS:"
			+"\nif (logger.isInfoEnabled()) {"
				+"\nlogger.info(\"success response received\");"
			+"\n}"
			
			+"\nbreak;"
			
		+"\ncase EVENT_DISCONNECT:" +"\nif (logger.isInfoEnabled()) {"
				+"\nlogger.info(\"Disconnect event received\");"
			+"\n}"
			
			+"\nbreak;"
			
		+"\ncase EVENT_FAILURE:"
			+"\nif (logger.isInfoEnabled()) {"
				+"\nlogger.info(\"failure event received received\");"
			+"\n}"
			
			+"\nbreak;"	
         +"\ndefault:"
	            +"\nbreak;"
             +"\n}"
		+"\nreturn actionArr;";
	

	public String typeName=null;
	
	public BPMPHServiceInterfacePage() {
		setTitle("New Service Interface");
		setDescription("Creates a New Service Interface Impl Class");
	}
	
	protected void createTypeMembers(IType type, ImportsManager imports, IProgressMonitor monitor) throws CoreException {
	
		
//		public Action[] processEvent(Event event, CallData callData);
//
//		public String getServletName();
//
//		public String getApplicationName();
//
//		public String[] getServiceCdr(CallData callData);

		imports.addImport("org.apache.log4j.Logger");
		imports.addImport("com.agnity.mphdata.common.Action");
		imports.addImport("com.agnity.mphdata.common.CallData");
		imports.addImport("com.agnity.mphdata.common.Event");
		imports.addImport("com.agnity.mphdata.common.LegData");
		imports.addImport("com.agnity.ph.common.enums.CallDataAttribute");
		imports.addImport("com.agnity.ph.common.enums.LegDataAttributes");
		imports.addImport("com.agnity.ph.common.ServiceInterface");
		imports.addImport("com.agnity.mphdata.common.PhoneNumber");
		     super.createTypeMembers(type, imports, monitor);
			super.createMethod(type, imports, monitor, "processEvent", PROCESS_EVENT_PARAM_TYPES, PROCESS_EVENT_NAMES, EXCEPTION_TYPES, RETURNTYPE, content);
			
			super.createMethod(type, imports, monitor, "getServletName", NULL_PARAMS, NULL_PARAMS, EXCEPTION_TYPES, "String", "return null;");
			super.createMethod(type, imports, monitor, "getApplicationName", NULL_PARAMS, NULL_PARAMS, EXCEPTION_TYPES, "String","return null;");
			super.createMethod(type, imports, monitor, "getServiceCdr", new String[]{CALLDATA}, new String[]{"callData"}, EXCEPTION_TYPES, "String[]","return null;");
	
		if (monitor != null) {
			monitor.done();
		}	
	}

	protected void createCustomControls(Composite composite, int nColumns) {
		
		ArrayList interfaces = new ArrayList();
		interfaces.add(EVENT_HANDLER_IF);
		setSuperClass("java.lang.Object", true);
		this.setSuperInterfaces(interfaces, false);
		
	}
	
	public void AddFieldToDescriptor(IProgressMonitor monitor) {
		// if(bTimerListener){
		
	}
	

}
