package com.agnity.simulator.handlers.impl;

import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.component.InvokeIndEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.agnity.simulator.InapIsupSimServlet;
import com.agnity.simulator.callflowadaptor.element.Node;
import com.agnity.simulator.callflowadaptor.element.child.FieldElem;
import com.agnity.simulator.callflowadaptor.element.child.SubFieldElem;
import com.agnity.simulator.callflowadaptor.element.type.TcapNode;
import com.agnity.simulator.callflowadaptor.element.type.tcapsubtype.ErbNode;
import com.agnity.simulator.domainobjects.SimCallProcessingBuffer;
import com.agnity.simulator.domainobjects.Variable;
import com.agnity.simulator.handlers.AbstractHandler;
import com.agnity.simulator.handlers.Handler;
import com.agnity.simulator.utils.Constants;
import com.agnity.simulator.utils.Helper;
import com.genband.inap.asngenerated.Cause;
import com.genband.inap.asngenerated.EventReportBCSMArg;
import com.genband.inap.asngenerated.EventSpecificInformationBCSM;
import com.genband.inap.asngenerated.EventTypeBCSM;
import com.genband.inap.asngenerated.MiscCallInfo;
import com.genband.inap.asngenerated.EventTypeBCSM.EnumType;
import com.genband.inap.asngenerated.MiscCallInfo.DpAssignmentEnumType;
import com.genband.inap.asngenerated.MiscCallInfo.MessageTypeEnumType;
import com.genband.inap.asngenerated.Integer4;
import com.genband.inap.asngenerated.LegID;
import com.genband.inap.asngenerated.LegType;
import com.genband.inap.enumdata.CauseValEnum;
import com.genband.inap.enumdata.CodingStndEnum;
import com.genband.inap.enumdata.LocationEnum;
import com.genband.inap.exceptions.InvalidInputException;
import com.genband.inap.operations.InapOpCodes;
import com.genband.inap.operations.InapOperationsCoding;
import com.genband.inap.util.Util;

public class ErbHandler extends AbstractHandler {

	private static Logger logger = Logger.getLogger(ErbHandler.class);
	private static Handler handler;

	private static final String ERB_FIELD_EVENT_TYPE_BCSM = "eventTypeBCSM".toLowerCase();
	private static final String ERB_FIELD_EVENT_SPEC_INFO_BCSM = "eventSpecificInformationBCSM".toLowerCase();
	private static final String ERB_FIELD_LEG_ID = "legId".toLowerCase();
	private static final String ERB_FIELD_MISC_CALL_INFO = "MiscCallInfo".toLowerCase();

	private static final String ERB_FIELD_VAL_OCALLED_PARTY_BUSY_SPECIFIC_INFO = "oCalledPartyBusySpecificInfo".toLowerCase();
	private static final String ERB_FIELD_VAL_ODISC_SPECIFIC_INFO = "oDisconnectSpecificInfo".toLowerCase();
	private static final String ERB_FIELD_VAL_OABANDON = "oAbandon".toLowerCase();
//	private static final String ERB_SUB_FIELD_CAUSE = "cause".toLowerCase();	
	private static final String ERB_SUB_FIELD_CONNECT_TIME = "connectTime".toLowerCase();

	private static final String ERB_SUB_FIELD_CAUSE_LOCATION_ENUM = "Cause.LocationEnum".toLowerCase();
	private static final String ERB_SUB_FIELD_CAUSE_CODING_STND_ENUM = "Cause.CodingStndEnum".toLowerCase();
	private static final String ERB_SUB_FIELD_CAUSE_AUSE_VALUE_ENUM = "Cause.CauseValEnum".toLowerCase();
	
	private static final String ERB_SUB_FIELD_MESSAGE_TYPE_ENUM_TYPE = "messageType".toLowerCase();
	private static final String ERB_SUB_FIELD_DP_ASSIGNMENT_ENUM_TYPE = "dpAssignment".toLowerCase();

	private static final int ERB_CLASS = 2;

	public static synchronized Handler getInstance() {
		if (handler == null) {
			synchronized (ErbHandler.class) {
				if (handler == null) {
					handler = new ErbHandler();
				}
			}
		}
		return handler;
	}

	private ErbHandler() {

	}

	@Override
	protected boolean processNode(Node node, SimCallProcessingBuffer simCpb) {
		if (logger.isInfoEnabled())
			logger.info("Inside ErbHandler processNode()");

		if (!(node.getType().equals(Constants.ERB))) {
			logger.error("Invalid Handler for node type::[" + node.getType()
					+ "]");
			return false;
		}
		ErbNode erbNode = (ErbNode) node;
		List<Node> subElements = node.getSubElements();
		Iterator<Node> subElemIterator = subElements.iterator();

		EventReportBCSMArg erbArg = new EventReportBCSMArg();

		Node subElem = null;
		FieldElem fieldElem = null;
		Map<String, Variable> varMap = simCpb.getVariableMap();

		// adding variables to CPB
		while (subElemIterator.hasNext()) {
			subElem = subElemIterator.next();

			if (subElem.getType().equals(Constants.FIELD)) {
				fieldElem = (FieldElem) subElem;
				String fieldName = fieldElem.getFieldType();
				if (fieldName.equals(ERB_FIELD_EVENT_TYPE_BCSM)) {
					// Event Type BCSM field
					// Possible values origAttemptAuthorized, analysedInformation, oCalledPartyBusy, oNoAnswer, oAnswer, oDisconnect, oAbandon
					String value = fieldElem.getValue(varMap);
					EventTypeBCSM eventTypeBcsm = new EventTypeBCSM();
					eventTypeBcsm.setValue(EnumType.valueOf(value));
					erbArg.setEventTypeBCSM(eventTypeBcsm);
				} else if (fieldName.equals(
						ERB_FIELD_EVENT_SPEC_INFO_BCSM)) {
					EventSpecificInformationBCSM evt = parseEvtSpecInfoBCSM(subElem,  varMap);

					if(evt != null)
						erbArg.setEventSpecificInformationBCSM(evt);

				} else if(fieldName.equals(ERB_FIELD_LEG_ID)) {
					String value = fieldElem.getValue(varMap);
										
					int legId = 2;
					if(value != null){
						legId = Integer.parseInt(value);
					}
					
					LegID legIdField = new LegID();
					byte[] legType = new byte[]{(byte) legId};
										
					legIdField.selectReceivingSideID(new LegType(legType));
					
					erbArg.setLegID(legIdField);
				}else if(fieldName.equals(ERB_FIELD_MISC_CALL_INFO)) {
					MiscCallInfo mci = new MiscCallInfo();
					MessageTypeEnumType mtet;
					DpAssignmentEnumType daet;
					Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
					String value = subFieldElems.get(ERB_SUB_FIELD_MESSAGE_TYPE_ENUM_TYPE).getValue(varMap);
					mtet = new MessageTypeEnumType();
					mtet.setValue(com.genband.inap.asngenerated.MiscCallInfo.MessageTypeEnumType.EnumType.valueOf(value));
					mci.setMessageType(mtet);
					SubFieldElem dpassignment = subFieldElems.get(ERB_SUB_FIELD_DP_ASSIGNMENT_ENUM_TYPE);
					if(dpassignment!=null){
						value = dpassignment.getValue(varMap);
						daet = new DpAssignmentEnumType();
						daet.setValue(com.genband.inap.asngenerated.MiscCallInfo.DpAssignmentEnumType.EnumType.valueOf(value));
						mci.setDpAssignment(daet);
					}
					erbArg.setMiscCallInfo(mci);
				}
			}// complete if subelem is field
		}// complete while

		//getting EventReportBCSM byte array
		LinkedList<byte[]> encode = null;

		LinkedList<Object> objLL = new LinkedList<Object>();
		LinkedList<String> opCode = new LinkedList<String>();
		objLL.add(erbArg);
		opCode.add(InapOpCodes.ERB);
		try {
			encode = InapOperationsCoding.encodeOperations(objLL, opCode);
		} catch (Exception e) {
			logger.error("error enconcoding ERB to byte array",e);
			return false;
		}
		byte[] erb = encode.get(0);
		if(logger.isDebugEnabled())
			logger.debug("ErbHandler EventReportBCSM Generated:["+Util.formatBytes(erb)+"]");

		// generate idp component req event
		byte[] ErbOpCode = { 0x18 };
		Operation requestOp = new Operation(Operation.OPERATIONTYPE_LOCAL,
				ErbOpCode);

		InvokeReqEvent ire = new InvokeReqEvent(InapIsupSimServlet.getInstance(), simCpb.getDialogId(),
				requestOp);
		ire.setInvokeId(simCpb.incrementAndGetInvokeId());
		ire.setParameters(new Parameters(
				Parameters.PARAMETERTYPE_SEQUENCE, erb));
		ire.setClassType(ERB_CLASS);
		if (logger.isDebugEnabled())
			logger
			.debug("ErbHandler processNode()-->reqEvent created, sending component["
					+ ire + "]");
		// sending componentr
		try {
			Helper.sendComponent(ire, simCpb);
		} catch (ParameterNotSetException e) {
			logger.error("ParameterNotSetException sending ERB component", e);
		}
		if (logger.isDebugEnabled())
			logger.debug("IdpHandler processNode()-->component send");
		// if last message generate dialog
		if (erbNode.isLastMessage()) {
			if (logger.isDebugEnabled())
				logger
				.debug("ErbHandler processNode()-->last message sending dialog also creating dialog");
			DialogueReqEvent dialogEvent = Helper.createDialogReqEvent(InapIsupSimServlet.getInstance(),
					erbNode.getDialogAs(), simCpb);
			try {
				if (logger.isDebugEnabled())
					logger
					.debug("ErbHandler processNode()-->sending created dialog ["
							+ dialogEvent + "]");
				Helper.sendDialogue(dialogEvent, simCpb);
			} catch (MandatoryParameterNotSetException e) {
				logger.error(
						"Mandatory param excpetion sending Dialog on IDP::"
						+ erbNode.getDialogAs(), e);
			} catch (IOException e) {
				logger.error("IOException excpetion sending Dialog on IDP::"
						+ erbNode.getDialogAs(), e);
			}
		}

		if (logger.isInfoEnabled())
			logger.info("Leaving erbHandler processNode() with status true");
		return true;

	}

	private EventSpecificInformationBCSM parseEvtSpecInfoBCSM(Node node,  Map<String, Variable> varMap) {
		if(logger.isInfoEnabled())
			logger.info("Inside ErbHandler parseEvtSpecInfoBCSM()");

		String fieldValue =null; 
		FieldElem fieldElem =((FieldElem) node);
		if(node.getType().equals(Constants.FIELD)){
			fieldValue = ((FieldElem) node).getValue(varMap);
		}else
			return null; // error scenario

		EventSpecificInformationBCSM evt = new EventSpecificInformationBCSM();

		
		// Possible values in EventSpecificInformationBCSM
		EventSpecificInformationBCSM.OAbandonSequenceType oAbandon = null;
		EventSpecificInformationBCSM.OCalledPartyBusySpecificInfoSequenceType oCdBusy = null;
		EventSpecificInformationBCSM.ODisconnectSpecificInfoSequenceType oDisc = null;

		// Not considering ONoAnswerSpecificInfo and OAnswerSpecific Info 
		// as standard doesn't specify any values for these fields 
		if(fieldValue.toLowerCase().equals(ERB_FIELD_VAL_OCALLED_PARTY_BUSY_SPECIFIC_INFO)) {
			oCdBusy = new EventSpecificInformationBCSM.OCalledPartyBusySpecificInfoSequenceType();

			Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
			Cause cause = getEncodeCause(subFieldElems,varMap);
			if(cause!=null){
				oCdBusy.setBusyCause(cause);
				//setting in event
				evt.selectOCalledPartyBusySpecificInfo(oCdBusy);
				if (logger.isDebugEnabled())
					logger.debug("ErbHandler parseEvtSpecInfoBCSM()-->OCalledPartyBusySpecificInfo is Set");
			}else{
				evt = null;
				if (logger.isDebugEnabled())
					logger.debug("ErbHandler parseEvtSpecInfoBCSM()-->Making EventSpecificInformationBCSM as null");
				return null;
			}
		} else if(fieldValue.toLowerCase().equals(ERB_FIELD_VAL_ODISC_SPECIFIC_INFO)) {
			oDisc = new EventSpecificInformationBCSM.ODisconnectSpecificInfoSequenceType();
			Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
			Cause cause = getEncodeCause(subFieldElems,varMap);
			if(cause!=null){
				oDisc.setReleaseCause(cause);
				//connect time
				String connectTimeStr =subFieldElems.get(ERB_SUB_FIELD_CONNECT_TIME).getValue(varMap);
				Integer4 connectTime = new Integer4(Integer.parseInt(connectTimeStr));
				oDisc.setConnectTime(connectTime);
				//setting event
				evt.selectODisconnectSpecificInfo(oDisc);
				if (logger.isDebugEnabled())
					logger.debug("ErbHandler parseEvtSpecInfoBCSM()-->ODisconnectSpecificInfo is Set");
			}else{
				evt = null;
				if (logger.isDebugEnabled())
					logger.debug("ErbHandler parseEvtSpecInfoBCSM()-->Making EventSpecificInformationBCSM as null");
				return null;
			}
		} else if(fieldValue.toLowerCase().equals(ERB_FIELD_VAL_OABANDON)) {
			oAbandon = new EventSpecificInformationBCSM.OAbandonSequenceType();
			Map<String, SubFieldElem> subFieldElems=fieldElem.getSubFieldElements();
			Cause cause = getEncodeCause(subFieldElems,varMap);
			if(cause!=null){
				oAbandon.setAbandonCause(cause);
				evt.selectOAbandon(oAbandon);
				if (logger.isDebugEnabled())
					logger.debug("ErbHandler parseEvtSpecInfoBCSM()-->OAbandon is Set");
			}else{
				evt = null;
				if (logger.isDebugEnabled())
					logger.debug("ErbHandler parseEvtSpecInfoBCSM()-->Making EventSpecificInformationBCSM as null");
				return null;
			}


		}else{
			evt = null;
			if (logger.isDebugEnabled())
				logger.debug("ErbHandler parseEvtSpecInfoBCSM()-->Making EventSpecificInformationBCSM as null");
			return null;
		}
		if(logger.isInfoEnabled())
			logger.info("Leaving ErbHandler parseEventSpecificInformationBCSM() with event::"+evt);
		return evt;
	}

	private Cause getEncodeCause(Map<String, SubFieldElem> subFieldElems,Map<String, Variable> varMap){

		String locationEnum =subFieldElems.get(ERB_SUB_FIELD_CAUSE_LOCATION_ENUM).getValue(varMap);
		String codingStdEnum = subFieldElems.get(ERB_SUB_FIELD_CAUSE_CODING_STND_ENUM).getValue(varMap);
		String causeValueEnum = subFieldElems.get(ERB_SUB_FIELD_CAUSE_AUSE_VALUE_ENUM).getValue(varMap);
		Cause cause = new Cause();
		byte[] causeVal;
		try {
			causeVal = com.genband.inap.datatypes.Cause.encodeCauseVal(LocationEnum.valueOf(locationEnum), 
					CodingStndEnum.valueOf(codingStdEnum), CauseValEnum.valueOf(causeValueEnum));
		} catch (InvalidInputException e) {
			logger.error("Error encoding cause in oabondon::return with null");
			return null;
		}
		cause.setValue(causeVal);

		return cause;
	}

	@Override
	protected boolean processRecievedMessage(Node node,
			SimCallProcessingBuffer simCpb, Object message) {

		if(logger.isDebugEnabled())
			logger.debug("processRecievedMessage() for ErbHandler");

		List<Node> subElements =node.getSubElements();
		if(subElements.size() == 0){
			if(logger.isDebugEnabled())
				logger.debug("ErbHandler processRecievedMessage()->No subelemnt present returning from handler");
			return true;
		}

		Node subElem =null;
		//		SetElem setElem = null;
		//		Map<String, Variable> varMap = simCpb.getVariableMap();
		//		Variable var = null;

		Iterator<Node> subElemIterator = subElements.iterator();

		//parsing ERB message
		InvokeIndEvent invokeIndEvent = (InvokeIndEvent)message; 
		byte[] parmsErb;
		EventReportBCSMArg erbARg = null;

		try{
			parmsErb =invokeIndEvent.getParameters().getParameter();
			if(logger.isDebugEnabled())
				logger.debug("processRecievedMessage() for ErbHandler--> starting first level decoding on ERB bytes:: "+Util.formatBytes(parmsErb));

			erbARg = (EventReportBCSMArg)InapOperationsCoding.decodeOperation(parmsErb, invokeIndEvent);
		}catch(Exception e){
			logger.error("Decode Failed ex::",e);
			return false;
		}

		if(erbARg == null){
			logger.error("erb is null");
			return false;
		}

		while (subElemIterator.hasNext()) {
			subElem = subElemIterator.next();
			//only set subelem needs to be hanled..
			if(subElem.getType().equals(Constants.SET)){
				//				setElem =(SetElem) subElem;

				//				String varName = setElem.getVarName();
				//				var =varMap.get(varName);
				//				if(var == null){
				//					var = new Variable();
				//					var.setVarName(varName);
				//				}
				//				String varVal = null;
				//				
				//				
				//				
				//				//finally storing variable
				//				var.setVarValue(varVal);
				//				simCpb.addVariable(var);

			}//end if check for set elem
		}//end while loop on subelem

		if(logger.isDebugEnabled())
			logger.debug("Leave processRecievedMessage() for ErbHandler");
		return true;
	}

	@Override
	public boolean validateMessage(Node node, Object message, SimCallProcessingBuffer simCpb) {

		if(logger.isDebugEnabled())
			logger.debug("validateMessage() for ErbHandler");

		if(!(message instanceof InvokeIndEvent)){
			if(logger.isDebugEnabled())
				logger.debug("Not and InvokeIndEvent message");
			return false;
		}

		if(!( node.getType().equals(Constants.ERB) ) ){
			if(logger.isDebugEnabled())
				logger.debug("Not a ERB Node");
			return false;
		}

		if(!(node.getAction().toLowerCase().equals(Constants.RECIEVE_ACTION.toLowerCase()))){
			if(logger.isDebugEnabled())
				logger.debug("Not a Recieve Action Node");
			return false;
		}
		TcapNode tcapNode = (TcapNode) node;

		int dialogType = simCpb.getLastDialoguePrimitive();
		int dialogId = simCpb.getDialogId();

		InvokeIndEvent receivedInvoke = (InvokeIndEvent)message; 
		Operation opr;
		byte[] opCode ;
		String opCodeStr= null ;
		boolean isValid= false;
		try {
			opr = receivedInvoke.getOperation();
			opCode = opr.getOperationCode();
			opCodeStr = Util.formatBytes(opCode);
			if( (opCodeStr.toLowerCase().equals(tcapNode.getOpCodeString().toLowerCase())) && (dialogType== tcapNode.getDialogType()) ){
				isValid= true;
			}	
			if(logger.isDebugEnabled())
				logger.debug("ErbHandler validateMessage() isValid::["+isValid+"]  Expected opcode::["+tcapNode.getOpCodeString()+
						"] Actual Opcode::["+opCodeStr+"] Expected DialogType::["+ tcapNode.getDialogType()+ 
						"] Actual DialogType::["+dialogType+"]");

		} catch (MandatoryParameterNotSetException e) {
			if(logger.isDebugEnabled())
				logger.debug("MandatoryParameterNotSetException for dialog ID::" +dialogId, e);
			isValid = false;
		} 

		return isValid;
	}

}
