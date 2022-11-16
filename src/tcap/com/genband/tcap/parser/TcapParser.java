package com.genband.tcap.parser;
import jain.MandatoryParameterNotSetException;
import jain.ParameterNotSetException;
import jain.protocol.ss7.SccpUserAddress;
import jain.protocol.ss7.SignalingPointCode;
import jain.protocol.ss7.SubSystemAddress;
import jain.protocol.ss7.sccp.StateIndEvent;
import jain.protocol.ss7.sccp.StateReqEvent;
import jain.protocol.ss7.sccp.management.NPCStateIndEvent;
import jain.protocol.ss7.sccp.management.NStateIndEvent;
import jain.protocol.ss7.sccp.management.NStateReqEvent;
import jain.protocol.ss7.tcap.ComponentIndEvent;
import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.DialogueIndEvent;
import jain.protocol.ss7.tcap.DialogueReqEvent;
import jain.protocol.ss7.tcap.JainTcapProvider;
import jain.protocol.ss7.tcap.TcapConstants;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;
import jain.protocol.ss7.tcap.dialogue.BeginReqEvent;
import jain.protocol.ss7.tcap.dialogue.EndReqEvent;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;


public class TcapParser {

	private static Logger logger = Logger.getLogger(TcapParser.class);	 

	public static int isProtocolSccpAnsi = 0;

	
	/**
	 * This function will decode the TCAP dialogue+component(s) message buffers
	 * @param buffer
	 * @param provider
	 * @return TcapType
	 * @throws TcapContentReaderException
	 * @throws MandatoryParameterNotSetException
	 */
	public static TcapType parse(byte[] buffer, JainTcapProvider provider) throws TcapContentReaderException, MandatoryParameterNotSetException
	{	
		if (logger.isInfoEnabled()) {
			logger.info("parse():Enter parse()");
		}
		if(logger.isDebugEnabled())
			logger.debug("Enter parse(): buffer: " + Util.formatBytes(buffer));
		
		DlgCompLenAttributes attr = TcapUtil.getLength(buffer, 1);		//length starts from buffer[1]
		int dlgLen = attr.length;		
		int noOfLenBytes = attr.noOfLenBytes;			
		
		TcapType tcapType = new TcapType();
		if (logger.isInfoEnabled()) {
			logger.info("parse():before parsing dialogues");
		}
		DialogueIndEvent dlgevent = parseAndPrepareDialogue(buffer, dlgLen, noOfLenBytes, provider);
		if (logger.isInfoEnabled()) {
			logger.info("parse():after parsing dialogues");
		}
		tcapType.setDialogueIndEvent(dlgevent);

		int dlgId = -1 ;
		if(dlgevent.isDialogueIdPresent()){
			dlgId = dlgevent.getDialogueId() ;
		}
		
		//components
		List<ComponentIndEvent> compList = new ArrayList<ComponentIndEvent>();
		int compIndex = dlgLen+noOfLenBytes+1;		//buffer index that points to start of components (COMPONENT_TYPE tag) 
		while(compIndex < buffer.length)
		{
			DlgCompLenAttributes attrC = TcapUtil.getLength(buffer, ++compIndex);
			int compLen = attrC.length;
			int noOfLenBytesC = attrC.noOfLenBytes;
			
			boolean lastComp = false;
			if(compIndex+noOfLenBytesC+compLen == buffer.length)
				lastComp = true ;
			if (logger.isInfoEnabled()) {
				logger.info("parse():before parsing comps");
			}
			ComponentIndEvent comp = parseAndPrepareComponent(buffer, compLen, compIndex+noOfLenBytesC, lastComp, dlgId, provider) ;
			if (logger.isInfoEnabled()) {
				logger.info("parse():after parsing comps");
			}
			if(comp != null)
				compList.add(comp) ;
			
			compIndex += noOfLenBytesC+compLen;			
		}
		
		tcapType.setComponentIndEvent(compList);
		if (logger.isInfoEnabled()) {
			logger.info("parse():Exit parse()");
		}
		return tcapType ;
	}
	
	
	/**
	 * This function will decode TCAP SCCP management messages (SSN State, PC State, Configuration)
	 * @param buffer
	 * @param provider
	 * @return TcapType
	 * @throws TcapContentReaderException
	 * @throws ParameterNotSetException 
	 * @throws ParameterNotSetException
	 */
	public static TcapType parseSCCPMgmtMsg(byte[] buffer, JainTcapProvider provider) throws TcapContentReaderException, ParameterNotSetException
	{
		if (logger.isInfoEnabled()) {
			logger.info("parseSCCPMgmtMsg():Enter parseSCCPMgmtMsg()");
		}
		if(logger.isDebugEnabled())
			logger.debug("Enter parseSCCPMgmtMsg(): buffer: " + Util.formatBytes(buffer));
		
		TcapType tcapType = new TcapType();	
		StateIndEvent stateIndEvent = null;
		
		DlgCompLenAttributes attrib = TcapUtil.getLength(buffer, 1);		//length starts from buffer[1]	
		int dlgLen = attrib.length;		
		int noOfLenBytes = attrib.noOfLenBytes;
		
		LinkedList<SccpUserAddress> origSua = new LinkedList<SccpUserAddress>();
		int userStatus = -1;
		byte[] destPC = null;
		byte[] origPC = null;
		int protocolVar = -1;
		
		int bufferIndex = noOfLenBytes+1;
		int msgType = buffer[bufferIndex++];
				
		while(bufferIndex < buffer.length)
		{
			int tag = buffer[bufferIndex];

			switch(tag) {
	
				case TagsConstant.USER_STATUS :
				{
					userStatus = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.USER_STATUS_LEN);
					bufferIndex += TagsConstant.USER_STATUS_LEN;
					break ;
				}					
				case TagsConstant.ORIG_SUA :
				{
					DlgCompLenAttributes attr = TcapUtil.getLength(buffer, ++bufferIndex);
					bufferIndex += attr.noOfLenBytes;
					origSua.add(TcapUtil.makeSccpUserAdrs(buffer, bufferIndex, attr.length));
					bufferIndex += attr.length;
					break ;
				}
				case TagsConstant.DEST_SUA :
				{
					DlgCompLenAttributes attr = TcapUtil.getLength(buffer, ++bufferIndex);
					bufferIndex += attr.noOfLenBytes;
					//destSua.add(TcapUtil.makeSccpUserAdrs(buffer, bufferIndex, attr.length)); //No need to parse DEST_SUA (if present, just ignore it: Bug 8161)
					bufferIndex += attr.length;
					break ;
				}	
				case TagsConstant.PROTOCOL_VARIANT :
				{
					protocolVar = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.PROTOCOL_VARIANT_LEN);
					bufferIndex += TagsConstant.PROTOCOL_VARIANT_LEN;
					break ;
				}
				case TagsConstant.AFFECTED_DPC :
				{
					destPC = new byte[TagsConstant.AFFECTED_DPC_LEN];
					System.arraycopy(buffer, ++bufferIndex, destPC, 0, TagsConstant.AFFECTED_DPC_LEN);
					bufferIndex += TagsConstant.AFFECTED_DPC_LEN;
					break ;
				}
				case TagsConstant.OWN_PC :
				{
					origPC = new byte[TagsConstant.OWN_PC_LEN];
					System.arraycopy(buffer, ++bufferIndex, origPC, 0, TagsConstant.OWN_PC_LEN);
					bufferIndex += TagsConstant.OWN_PC_LEN;
					break ;
				}
				default:
					throw new TcapContentReaderException("Unknown tag " + tag);

			}// end of dlg
		}
		
		if(msgType == TagsConstant.SSN_STATE_IND_MSG) {		//there will be only 1 entry in lists
			if (logger.isInfoEnabled()) {
				logger.info("parseSCCPMgmtMsg():SSN State Ind Msg");			
			}
			stateIndEvent = new NStateIndEvent(provider, origSua.get(0), userStatus);			
		}
		else if(msgType == TagsConstant.PC_STATE_IND_MSG) {
			if (logger.isInfoEnabled()) {
				logger.info("parseSCCPMgmtMsg():PC State Ind Msg");
			}
			SignalingPointCode affectedPointCode = null;
			SignalingPointCode ownPointCode = null;
			if(destPC != null)
				affectedPointCode = TcapUtil.makePointCode(destPC, protocolVar);
			if(origPC != null)
				ownPointCode = TcapUtil.makePointCode(origPC, protocolVar);;
			
			stateIndEvent = new NPCStateIndEvent(provider, affectedPointCode, ownPointCode, userStatus);
		}
		else if(msgType == TagsConstant.CONF_MSG) {
			if (logger.isInfoEnabled()) {
				logger.info("parseSCCPMgmtMsg():Conf Msg");
			}
			ConfigurationMsgDataType configMsg = new ConfigurationMsgDataType();
			configMsg.setOrigSua(origSua);
			tcapType.setConfigMsg(configMsg);
		}
				
		if(stateIndEvent != null)
			tcapType.setStateIndEvent(stateIndEvent);
		if (logger.isInfoEnabled()) {
			logger.info("Decoded Message: " + tcapType.toString());
			logger.info("parseSCCPMgmtMsg():Exit parseSCCPMgmtMsg()");
		}
		return tcapType;
	}
	

	//Used to decode the component
	private static ComponentIndEvent parseAndPrepareComponent(byte[] buffer, int compLen, int compValueOffset, boolean lastComp, int dlgId, JainTcapProvider provider) throws TcapContentReaderException
	{
		if (logger.isInfoEnabled()) {
			logger.info("parseAndPrepareComponent():Enter parseAndPrepareComponent()");
		}
		byte[] opCode = null;
		int opType = -1;
		int classType = -1 ;
		int compType = -1 ;
		int invokeId = -1 ;
		int linkedId = -1 ;
		int paramIdentifier = -1 ;
		byte[] param = null ;
		byte[] errorCode = null ;
		int errorType = -1 ;
		int problemType = -1 ;
		int problemCode = -1 ;
		int rejectType = -1 ;
		int incRejectSrc = -1 ;
		int incLast = -1 ;
		int incNotLast = -1 ;
		boolean isLast = false;
		

		int bufferIndex = compValueOffset;
		compType = buffer[bufferIndex++];
		while(bufferIndex < compValueOffset+compLen)
		{		
			int tag = buffer[bufferIndex];			
			switch(tag){
	
				/*case TagsConstant.COMPONENT_TYPE: {
					dlgId = getIntValue(buffer, ++bufferIndex, TagsConstant.COMPONENT_TYPE_LEN);
					bufferIndex += TagsConstant.COMPONENT_TYPE_LEN;
					break ;
				}*/
				case TagsConstant.OPERATION_TYPE: {
					opType = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.OPERATION_TYPE_LEN);
					bufferIndex += TagsConstant.OPERATION_TYPE_LEN;
					break ;
				}
				case TagsConstant.OPERATION_CODE: {
					DlgCompLenAttributes attr = TcapUtil.getLength(buffer, ++bufferIndex);
					opCode = new byte[attr.length];
					bufferIndex += attr.noOfLenBytes;
					System.arraycopy(buffer, bufferIndex, opCode, 0, attr.length);										
					bufferIndex += attr.length;
					break ;
				}
				case TagsConstant.PARAM_IDENTIFIER: {
					paramIdentifier = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.PARAM_IDENTIFIER_LEN);
					bufferIndex += TagsConstant.PARAM_IDENTIFIER_LEN;
					break ;
				}
				case TagsConstant.PARAM: {
					DlgCompLenAttributes attr = TcapUtil.getLength(buffer, ++bufferIndex);
					param = new byte[attr.length];
					bufferIndex += attr.noOfLenBytes;
					System.arraycopy(buffer, bufferIndex, param, 0, attr.length);										
					bufferIndex += attr.length;
					break ;
				}
				case TagsConstant.CLASS_TYPE: {
					classType = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.CLASS_TYPE_LEN);
					bufferIndex += TagsConstant.CLASS_TYPE_LEN;
					break ;
				}
				case TagsConstant.INVOKE_ID: {
					invokeId = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.INVOKE_ID_LEN);
					bufferIndex += TagsConstant.INVOKE_ID_LEN;
					break ;
				}
				case TagsConstant.LINKED_ID: {
					linkedId = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.LINKED_ID_LEN);
					bufferIndex += TagsConstant.LINKED_ID_LEN;
					break ;
				}
				case TagsConstant.PROBLEM_TYPE: {
					problemType = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.PROBLEM_TYPE_LEN);
					bufferIndex += TagsConstant.PROBLEM_TYPE_LEN;
					break ;
				}
				case TagsConstant.PROBLEM_CODE: {
					problemCode = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.PROBLEM_CODE_LEN);
					bufferIndex += TagsConstant.PROBLEM_CODE_LEN;
					break ;
				}
				case TagsConstant.REJECT_TYPE: {
					rejectType = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.PROBLEM_TYPE_LEN);
					bufferIndex += TagsConstant.PROBLEM_TYPE_LEN;
					break ;
				}
				case TagsConstant.ERROR_TYPE: {
					errorType = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.PROBLEM_TYPE_LEN);
					bufferIndex += TagsConstant.PROBLEM_TYPE_LEN;
					break ;
				}
				case TagsConstant.ERROR_CODE: {
					DlgCompLenAttributes attr = TcapUtil.getLength(buffer, ++bufferIndex);
					errorCode = new byte[attr.length];
					bufferIndex += attr.noOfLenBytes;
					System.arraycopy(buffer, bufferIndex, errorCode, 0, attr.length);										
					bufferIndex += attr.length;
					break ;
				}
				case TagsConstant.INC_REJECT_SOURCE: {
					incRejectSrc = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.INC_REJECT_SOURCE_LEN);
					bufferIndex += TagsConstant.INC_REJECT_SOURCE_LEN;
					break ;
				}
				case TagsConstant.INC_LAST: {
					incLast = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.INC_LAST_LEN);
					if(incLast == 1) 
						isLast=true;
					bufferIndex += TagsConstant.INC_LAST_LEN;
					break ;
				}
				case TagsConstant.INC_NOT_LAST: {
					incNotLast = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.INC_NOT_LAST_LEN);
					bufferIndex += TagsConstant.INC_NOT_LAST_LEN;
					break ;
				}
				default:
					throw new TcapContentReaderException("Unknown tag: " + tag);
			}

		}//end of component
		
		Operation operation = null ;
		if(opType != -1 && opCode != null){
			operation = new Operation(opType, opCode);
		}
		Parameters params = null ;
		if(paramIdentifier != -1 && param != null){
			params = new Parameters(paramIdentifier, param);
		}
		ComponentIndEvent compIndEvent = ComponentIndFactory.prepareCompIndEvent(compType, classType, lastComp, dlgId, operation, params, invokeId, linkedId,
					problemType, problemCode, incRejectSrc, errorType, errorCode, isLast, provider);
		if (logger.isInfoEnabled()) {
			logger.info("parseAndPrepareComponent():Exit parseAndPrepareComponent()");
		}
		return compIndEvent ;
	}

	//Used to decode the dialogus
	private static DialogueIndEvent parseAndPrepareDialogue(byte[] buffer, int dlgLen, int noOfLenBytes, JainTcapProvider provider) throws TcapContentReaderException
	{
		if (logger.isInfoEnabled()) {
			logger.info("parseAndPrepareDialogue():Enter parseAndPrepareDialogue()");
		}
		SccpUserAddress destSua = null;
		SccpUserAddress origSua = null;
		int ansiType = -1;
		int reportCause = -1 ;
		int abortCause = -1 ;
		int appContextType = -1;
		byte[] appCtxName = null;
		byte[] pc = null ;

		byte[] abortInfo = null;
		int dlgId = -1 ;
		int dlgType = -1 ;
		int qos = -1;
		SignalingPointCode	dpc=null;
		SignalingPointCode	opc=null;
		
		boolean compPresent = false;
		
		int returnOption = -1;

		int sequenceControl = -1;
		int messagePriority = -1;
				
		if(buffer.length > dlgLen+noOfLenBytes+1)		// 1 for dlg tag
			compPresent = true;
				
		int bufferIndex = noOfLenBytes+1;		// 1 for dlg tag		
		dlgType = buffer[bufferIndex++];
		
		while(bufferIndex < dlgLen+noOfLenBytes+1)
		{
			int tag = buffer[bufferIndex];

			switch(tag) {
	
				case TagsConstant.DLGID :
				{					
					dlgId = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.DLGID_LEN);
					bufferIndex += TagsConstant.DLGID_LEN;
					break ;
				}						
				case TagsConstant.QOS :
				{
					qos = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.QOS_LEN);
					bufferIndex += TagsConstant.QOS_LEN;
					break ;
				}
				case TagsConstant.REPORT_CAUSE :
				{
					reportCause = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.REPORT_CAUSE_LEN);
					bufferIndex += TagsConstant.REPORT_CAUSE_LEN;
					break ;
				}
				case TagsConstant.ABORT_CAUSE :
				{
					abortCause = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.ABORT_CAUSE_LEN);
					bufferIndex += TagsConstant.ABORT_CAUSE_LEN;
					break ;
				}
				case TagsConstant.APP_CONTEXT_NAME :
				{
					DlgCompLenAttributes attr = TcapUtil.getLength(buffer, ++bufferIndex);
					appCtxName = new byte[attr.length];
					bufferIndex += attr.noOfLenBytes;
					System.arraycopy(buffer, bufferIndex, appCtxName, 0, attr.length);										
					bufferIndex += attr.length;
					break ;
				}
				case TagsConstant.ABORT_INFO :
				{
					DlgCompLenAttributes attr = TcapUtil.getLength(buffer, ++bufferIndex);
					abortInfo = new byte[attr.length];
					bufferIndex += attr.noOfLenBytes;
					System.arraycopy(buffer, bufferIndex, abortInfo, 0, attr.length);										
					bufferIndex += attr.length;
					break ;
				}
				case TagsConstant.APP_CONTEXT_NAME_TYPE :
				{
					appContextType = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.APP_CONTEXT_NAME_TYPE_LEN);
					bufferIndex += TagsConstant.APP_CONTEXT_NAME_TYPE_LEN;
					break ;
				}
				case TagsConstant.ORIG_SUA :
				{
					DlgCompLenAttributes attr = TcapUtil.getLength(buffer, ++bufferIndex);
					bufferIndex += attr.noOfLenBytes;
					origSua = TcapUtil.makeSccpUserAdrs(buffer, bufferIndex, attr.length);
					bufferIndex += attr.length;
					break ;
				}
				case TagsConstant.DEST_SUA :
				{
					DlgCompLenAttributes attr = TcapUtil.getLength(buffer, ++bufferIndex);
					bufferIndex += attr.noOfLenBytes;
					destSua = TcapUtil.makeSccpUserAdrs(buffer, bufferIndex, attr.length);
					bufferIndex += attr.length;
					break ;
				}
				case TagsConstant.ALLOWED_PERMISSION:
				{
					//TODO: fix this case after discussion with yogesh.
					ansiType = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.ALLOWED_PERMISSION_LEN);
					bufferIndex += TagsConstant.ALLOWED_PERMISSION_LEN;
					
					break ;
				}
				case TagsConstant.MTP3_DPC:
				{ 
					logger.debug("Found MTP3 dpc tag in request ");
				pc = new byte[TagsConstant.PC_LEN];
				System.arraycopy(buffer, ++bufferIndex, pc, 0, TagsConstant.PC_LEN);
				bufferIndex += TagsConstant.PC_LEN;
				dpc=new SignalingPointCode((pc[3]&0xFF), (pc[2]& 0xFF), (pc[1]& 0xFF));//new SignalingPointCode((pc[3]), (pc[2]), pc[1]);
				logger.debug("Dpcc :- "+ dpc);
				 pc=null;
				break ;
				}
				case TagsConstant.MPT3_OPC:
				{  
					logger.debug("Found MTP3 opc tag in request ");
				pc = new byte[TagsConstant.PC_LEN];
				System.arraycopy(buffer, ++bufferIndex, pc, 0, TagsConstant.PC_LEN);
				bufferIndex += TagsConstant.PC_LEN;
				opc=new SignalingPointCode((pc[3]&0xFF), (pc[2]& 0xFF), (pc[1]& 0xFF));
				logger.debug("opc :- "+ opc);
				pc=null;
				break ;
				}
				case TagsConstant.RETURN_OPTION:
				{
					returnOption = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.ALLOWED_PERMISSION_LEN);
					bufferIndex += TagsConstant.ALLOWED_PERMISSION_LEN;
					break ;
				}
				
				case TagsConstant.SEQUENCE_CONTROL:
				{
					sequenceControl = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.ALLOWED_PERMISSION_LEN);
					bufferIndex += TagsConstant.ALLOWED_PERMISSION_LEN;
					break ;
				}
				
				case TagsConstant.MESSAGE_PRIORITY:
				{
					messagePriority = TcapUtil.getIntValue(buffer, ++bufferIndex, TagsConstant.ALLOWED_PERMISSION_LEN);
					bufferIndex += TagsConstant.ALLOWED_PERMISSION_LEN;
					break ;
				}
				
				default:
					throw new TcapContentReaderException("Unknown tag : " + tag);

			}// end of dlg
		}
		
		DialogueIndEvent dlgEvent = DialogueIndFactory.prepareDlgIndEvent(dlgType,ansiType, dlgId, origSua, destSua, (byte)qos, 
				reportCause, abortCause, abortInfo, compPresent, appContextType,appCtxName,provider);
		dlgEvent.setMtp3Dpc(dpc);
		dlgEvent.setMtp3Opc(opc);
		
		if(logger.isDebugEnabled()) {
			logger.debug("checking and setting --> return Option: "+ returnOption +" sequenceControl: "+ sequenceControl +" messagePriority: "+ messagePriority);
		}
		
		if(returnOption != -1) {
			dlgEvent.setM_returnOption(returnOption);
		}
		
		if(sequenceControl != -1) {
			dlgEvent.setM_sequenceControl(sequenceControl);
		}
		
		if(messagePriority != -1) {
			dlgEvent.setM_messagePriority(messagePriority);
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("parseAndPrepareDialogue():Exit parseAndPrepareDialogue()");
		}
		return dlgEvent ;
	}
	
	
	/**
	 * This function will encode the TCAP dialogue+component(s) message objects
	 * @param dlg
	 * @param comps
	 * @param relay 
	 * @return byte[]
	 * @throws TcapContentWriterException 
	 * @throws ParameterNotSetException
	 */
	public static byte[] encode(DialogueReqEvent dlg, List<ComponentReqEvent> comps,JainTcapProvider provider, boolean relay) throws TcapContentWriterException
	{
		if (logger.isInfoEnabled()) {
			logger.info("encode():Enter encode()");
		}
		if(logger.isDebugEnabled())
			logger.debug("Enter encode(): dlg: " + dlg + " ,comps: " + comps +" relay "+relay);
		
		
		byte[] out = null ;
		try {
		List<Byte> outList = new LinkedList<Byte>();
		
		if(dlg.getPrimitiveType() == TcapConstants.PRIMITIVE_BEGIN) {
			BeginReqEvent beginEvent = (BeginReqEvent)dlg;
			if (relay) {
				outList.add((byte) TagsConstant.MRS_RELAY);
				byte rlay = (byte) 1;
				outList.add(rlay);
				if(logger.isDebugEnabled())
					logger.debug("Enter outlist after relay tag to 1 is "+ outList);
			}else{
				if(logger.isDebugEnabled())
					logger.debug("Enter outlist after relay tag to zero"+ outList);
				outList.add((byte) TagsConstant.MRS_RELAY);
				byte rlay = (byte) 0;
				outList.add(rlay);
			}
			;
			//adding orig_sua
		
		}
		
		if(logger.isDebugEnabled())
			logger.debug("add dialog type to  "+ outList);
		//for dlg
		//adding dlgtype, length will be added later
		outList.add((byte)TagsConstant.DLGTYPE);
		if(dlg.getPrimitiveType() == jain.protocol.ss7.tcap.TcapConstants.PRIMITIVE_END) {
			EndReqEvent endReq = (EndReqEvent)dlg;
			if(endReq.isTerminationPresent() && endReq.getTermination() == jain.protocol.ss7.tcap.dialogue.DialogueConstants.TC_PRE_ARRANGED_END) {
				outList.add((byte)jain.protocol.ss7.tcap.TcapConstants.PRIMITIVE_END_PRE_ARRANGED);				
			} else {
				outList.add((byte)dlg.getPrimitiveType());
			}
		} else {		
			outList.add((byte)dlg.getPrimitiveType());
		}
		
		
		//adding dlgId
		outList.add((byte)TagsConstant.DLGID);
		byte[] dlgIdBytes = TcapUtil.encodeInteger(dlg.getDialogueId());
		for(int i=0; i<dlgIdBytes.length; i++)
			outList.add(dlgIdBytes[i]);
		
	
		/*
		 * Add TC-Corr-id for Ansi WIN 
		 */
		Integer tcCorrId = null;
		
		if(provider!=null)
			tcCorrId=provider
				.getTCCorrelationId(dlg.getDialogueId());

		if (tcCorrId != null) {
			outList.add((byte) TagsConstant.TC_CORR_ID);
			byte[] tcCorrIdBytes = TcapUtil.encodeInteger(tcCorrId
					.intValue());
			for (int i = 0; i < tcCorrIdBytes.length; i++)
				outList.add(tcCorrIdBytes[i]);
		}
		
		//adding QoS
		if(dlg.isQualityOfServicePresent()){
			outList.add((byte)TagsConstant.QOS);
			outList.add((byte)dlg.getQualityOfService());
		}
		
//		if(dlg.getPrimitiveType() == TcapConstants.PRIMITIVE_BEGIN) {
//			//adding mtp3
//				BeginReqEvent beginEvent = (BeginReqEvent)dlg;
			if (dlg.getMtp3Dpc() != null) {
				logger.debug("adding MTP3_DPC in dialog");
				SignalingPointCode spc = dlg.getMtp3Dpc();
				outList.add((byte) TagsConstant.MTP3_DPC);
				outList.add((byte)0x00);
				outList.add((byte) spc.getZone());
				outList.add((byte) spc.getCluster());
				outList.add((byte) spc.getMember());
			}
			
			
			if (dlg.getMtp3Opc() != null) {
				logger.debug("adding MTP3_OPC in dialog");
				SignalingPointCode spc = dlg.getMtp3Opc();
				outList.add((byte) TagsConstant.MPT3_OPC);
				outList.add((byte)0x00);
				outList.add((byte) spc.getZone());
				outList.add((byte) spc.getCluster());
				outList.add((byte) spc.getMember());
			}
		//	}
		
			if(dlg.getPrimitiveType() == jain.protocol.ss7.tcap.TcapConstants.PRIMITIVE_END) {
				//adding protocol version
				EndReqEvent endReq = (EndReqEvent)dlg;
				//checking protocolVersin is set as 0 & 1
				//by default it will be zero 
				if(endReq.getProtocolVersion() == 0 || endReq.getProtocolVersion() == 1) {
					logger.debug("adding protocol version with value " + endReq.getProtocolVersion());
					outList.add((byte) TagsConstant.PROTOCOL_VERSION);
					outList.add((byte) endReq.getProtocolVersion());
				}
			}
			
			//adding returnOption, sequenceControl & messagePriority
			if (dlg.getReturnOption() != null) {
				logger.debug("adding ReturnOption in dialog");
				outList.add((byte) TagsConstant.RETURN_OPTION);
				outList.add((Integer.valueOf(dlg.getReturnOption())).byteValue());
			}
			
			if (dlg.getSequenceControl() != null) {
				logger.debug("adding SequenceControl in dialog");
				outList.add((byte) TagsConstant.SEQUENCE_CONTROL);
				outList.add((Integer.valueOf(dlg.getSequenceControl())).byteValue());
			}
			
			if (dlg.getMessagePriority() != null) {
				logger.debug("adding MessagePriority in dialog");
				outList.add((byte) TagsConstant.MESSAGE_PRIORITY);
				outList.add((Integer.valueOf(dlg.getMessagePriority())).byteValue());
			}
			
		//adding dlg specific params
		outList = DialogueReqFactory.encodeDialogue(dlg, outList,relay);
		
		if (logger.isInfoEnabled()) {
			logger.info("encode():encodeLength now of outlist " + outList +" size "+ outList.size());
		}
		//adding length of dlg
		byte[] dlgLen=null;
			if (dlg.getPrimitiveType() == TcapConstants.PRIMITIVE_BEGIN) {
				dlgLen = TcapUtil.encodeLength(outList.size() - 3);
			} else {
				dlgLen = TcapUtil.encodeLength(outList.size() - 1);
				// -1 for dlgType tag
			}
		
		if (logger.isInfoEnabled()) {
			logger.info("encode():encodeLength dialog length " + dlgLen +" size is "+ dlgLen.length);
		}
		for(int i=0; i<dlgLen.length; i++){
			
			if (logger.isInfoEnabled()) {
				logger.info("encode():add length after "+ i+1+"  length "+ dlgLen[i]);
			}
				if (dlg.getPrimitiveType() == TcapConstants.PRIMITIVE_BEGIN) {
					outList.add(i + 3, dlgLen[i]);
				} else {
					outList.add(i+1, dlgLen[i]);
				}
		}//length will start from byte[1]	
		if (logger.isInfoEnabled()) {
			logger.info("encode():after encoding dlg");
		}
		//for components

		if(comps != null && !comps.isEmpty())
		{
			for(ComponentReqEvent comp : comps) {
				List<Byte> compList = new LinkedList<Byte>();
				//adding compType, length will be added later
				compList.add((byte)TagsConstant.COMPONENT_TYPE);
				compList.add((byte)comp.getPrimitiveType());
				
				//adding invokeId {
				if(comp.isInvokeIdPresent()){
					compList.add((byte)TagsConstant.INVOKE_ID);
					compList.add((byte)comp.getInvokeId());
				}
				
				//adding comp specific params
				compList = ComponentReqFactory.encodeComponent(comp, compList);
				//adding length of comp
				byte[] compLen = TcapUtil.encodeLength(compList.size()-1);		//-1 for compType tag
				for(int i=0; i<compLen.length; i++)
					compList.add(i+1, compLen[i]);		//length will start from byte[1]

				//adding comp in dlg list
				outList.addAll(compList);
				if (logger.isInfoEnabled()) {
					logger.info("encode():after encoding comp");
				}
			}
		}
			
		outList.add((byte)13);
		outList.add((byte)10);
		
		
		out = new byte[outList.size()];
		for(int i=0; i<outList.size(); i++)
			out[i] = outList.get(i);
		} catch(Exception e){
			throw new TcapContentWriterException(e);
		}
		if (logger.isInfoEnabled()) {
			logger.info("Encode Bytes: " + Util.formatBytes(out));
			logger.info("encode(): exit encode()");
		}
		return out;
	}
	
	
	/**
	 * This function will encode the TCAP SCCP Management Messages
	 * @param event
	 * @param typeOfMsg (11 for SSN State Indication, 12 for PC State Indication, 13 for Configuration)
	 * @return byte[]
	 * @throws ParameterNotSetException 
	 * @throws MandatoryParameterNotSetException 
	 */
	public static byte[] encodeSCCPMgmtMsg(StateReqEvent event, int typeOfMsg) throws MandatoryParameterNotSetException, ParameterNotSetException
	{
		if (logger.isInfoEnabled()) {
			logger.info("encodeSCCPMgmtMsg():Enter encodeSCCPMgmtMsg()");
		}
		if(logger.isDebugEnabled())
			logger.debug("Enter encodeSCCPMgmtMsg(): event: " + event);
		
		List<Byte> outList = new LinkedList<Byte>();		
		
		if(typeOfMsg == TagsConstant.SSN_STATE_IND_MSG) {
			//adding msgtype, length will be added later
			outList.add((byte)TagsConstant.INC_SCCP_MGMT_MSG);		
			outList.add((byte)TagsConstant.SSN_STATE_IND_MSG);
			
			NStateReqEvent stateInd = (NStateReqEvent)event;
			//adding user status
			outList.add((byte)TagsConstant.USER_STATUS);		
			outList.add((byte)stateInd.getUserStatus());
			//adding affected user
			outList.add((byte)TagsConstant.ORIG_SUA);		//affected user tag is same as orig_sua
			byte[] origAdd = TcapUtil.encodeSCCPUserAdd(stateInd.getAffectedUser(), false);
			byte[] origAddLen = TcapUtil.encodeLength(origAdd.length);
			for(int i=0; i<origAddLen.length; i++)
				outList.add(origAddLen[i]);
			for(int i=0; i<origAdd.length; i++)
				outList.add(origAdd[i]);
			
			//adding length of msg
			byte[] len = TcapUtil.encodeLength(outList.size()-1);		//-1 for msgType tag
			for(int i=0; i<len.length; i++)
				outList.add(i+1, len[i]);		//length will start from byte[1]
		}

		outList.add((byte)13);
		outList.add((byte)10);
		
		byte[] out = new byte[outList.size()];
		for(int i=0; i<outList.size(); i++)
			out[i] = outList.get(i);
		if (logger.isInfoEnabled()) {
			logger.info("Encode Bytes: " + Util.formatBytes(out));
			logger.info("encodeSCCPMgmtMsg(): exit encodeSCCPMgmtMsg()");
		}
		return out;
	}	

	public static int getIsProtocolSccpAnsi() {
		return isProtocolSccpAnsi;
	}


	public static void setIsProtocolSccpAnsi(int isProtocolSccpAnsi) {
		TcapParser.isProtocolSccpAnsi = isProtocolSccpAnsi;
		TcapUtil.setIsProtocolSccpAnsi(isProtocolSccpAnsi);
		if(logger.isDebugEnabled()){
			logger.debug("Setting value of SCCP protocol as:" + isProtocolSccpAnsi);
		}
	}
}
