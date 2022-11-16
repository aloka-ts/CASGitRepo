package com.camel.CAPMsg;

import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;

import asnGenerated.BCSM_Failure;
import asnGenerated.CallInformationReportArg;
import asnGenerated.CallInformationRequestArg;
import asnGenerated.CallSegmentFailure;
import asnGenerated.Cause;
import asnGenerated.EntityReleasedArg;
import asnGenerated.LegID;
import asnGenerated.LegType;
import asnGenerated.RequestedInformation;
import asnGenerated.RequestedInformationList;
import asnGenerated.RequestedInformationType;
import asnGenerated.RequestedInformationTypeList;
import asnGenerated.SendingSideID;

import com.camel.dataTypes.CauseDataType;
import com.camel.util.NonAsnArg;
import com.camel.util.Util;

/**
 * This class have methods to process the CAP operations
 * Call Information Request(CIRq) and Call Information Report(CIRp)
 * @author nkumar
 *
 */
public class SasCapCIR {

	//Instance of logger
	private static Logger logger = Logger.getLogger(SasCapCIR.class);
	
	/**
	 * This function will encode the Call Information Request argument and
	 * set the invokeReqEvent in the msgs.
	 * @param source
	 * @param cpb
	 * @param msgs
	 * @param invokeId
	 * @throws Exception 
	 */
	public static void encodeCIRq(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: encodeCIRq:Enter");
		if( !cpb.isCallInfoRequestDataForLeg1Present()){
			logger.info(Util.toString(cpb.dlgId) + "::: encodeCIRq: Setting default Value in the callInfoRequestDataForLeg1");
			cpb.callInfoRequestDataForLeg1 = new CallInformationRequestArg();
			setDefaultValuesForCIRq(cpb, cpb.callInfoRequestDataForLeg1, 1);
		}
		if( !cpb.isCallInfoRequestDataForLeg2Present()){
			logger.info(Util.toString(cpb.dlgId) + "::: encodeCIRq: Setting default Value in the callInfoRequestDataForLeg2");
			cpb.callInfoRequestDataForLeg2 = new CallInformationRequestArg();
			setDefaultValuesForCIRq(cpb, cpb.callInfoRequestDataForLeg2, 2);
		}
		makeInvokeReqEvent(source,cpb.callInfoRequestDataForLeg1,  cpb, invokeId, msgs);
		makeInvokeReqEvent(source,cpb.callInfoRequestDataForLeg2,  cpb, ++invokeId, msgs);
		logger.info(Util.toString(cpb.dlgId) + "::: encodeCIRq:Exit");
	}
	
	public static void processCIRp(int dilgId, SasCapCallProcessBuffer cpb, byte[] parms, IDecoder decoder) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: processCIRp:Enter");
		InputStream ins = new ByteArrayInputStream(parms);
		CallInformationReportArg callInfoRpt;
		try {
			logger.info(Util.toString(dilgId) + "::: processCIRp:decoding the CallInformationReportArg");
			callInfoRpt = decoder.decode(ins, CallInformationReportArg.class);
			logger.info(Util.toString(dilgId) + "::: processCIRp:Successfully decoded the CallInformationReportArg");
		} catch (Exception e) {
			logger.error(Util.toString(dilgId) + "::: processCIRp:" , e);
			throw e ;
		}
		cpb.callInfoRptDataType = new SasCapCallInfoRptDataType();
		if(callInfoRpt.isLegIDPresent()){
			cpb.callInfoRptDataType.legId =  Util.formatBytes(callInfoRpt.getLegID().getReceivingSideID().getValue());
			logger.debug(Util.toString(cpb.dlgId) + "::: processCIRp:legId: " + cpb.callInfoRptDataType.legId);
		}
		RequestedInformationList reqInfoList = callInfoRpt.getRequestedInformationList();
		ArrayList<RequestedInformation> list = (ArrayList<RequestedInformation>)reqInfoList.getValue();
		cpb.callInfoRptDataType.reqInfoList = new ArrayList<SasCapReqInfoDataType>();
		for(int k=0 ; k < list.size() ; k++){
			SasCapReqInfoDataType reqInfoData = new SasCapReqInfoDataType();
			RequestedInformation reqInfo = list.get(k);
			reqInfoData.reqInfoType = reqInfo.getRequestedInformationType();
			if(reqInfoData.reqInfoType.getValue() == RequestedInformationType.EnumType.callAttemptElapsedTime){
				reqInfoData.callAttemptElapsedTime = reqInfo.getRequestedInformationValue().getCallAttemptElapsedTimeValue();
				logger.debug(Util.toString(cpb.dlgId) + "::: processCIRp:callAttemptElapsedTime: " + reqInfoData.callAttemptElapsedTime);
			}
			else if(reqInfoData.reqInfoType.getValue() == RequestedInformationType.EnumType.callConnectedElapsedTime){
				reqInfoData.callConnectedElapsedTimeValue = reqInfo.getRequestedInformationValue().getCallConnectedElapsedTimeValue().getValue();
				logger.debug(Util.toString(cpb.dlgId) + "::: processCIRp:callConnectedElapsedTimeValue: " + reqInfoData.callConnectedElapsedTimeValue);
			}
			else if(reqInfoData.reqInfoType.getValue() == RequestedInformationType.EnumType.callStopTime){
				byte[] dateTime = reqInfo.getRequestedInformationValue().getCallStopTimeValue().getValue();
				reqInfoData.dateTime = NonAsnArg.TbcdStringDecoder(dateTime, 0);
				logger.debug(Util.toString(cpb.dlgId) + "::: processCIRp:dateTime: " + reqInfoData.dateTime);
			}
			else if(reqInfoData.reqInfoType.getValue() == RequestedInformationType.EnumType.releaseCause){
				reqInfoData.cause = CauseDataType.decodeCauseVal(reqInfo.getRequestedInformationValue().getReleaseCauseValue().getValue());
				logger.debug(Util.toString(cpb.dlgId) + "::: processCIRp:Release Cause: " + reqInfoData.cause);
			}
			
			cpb.callInfoRptDataType.reqInfoList.add(reqInfoData);			
		}
		
		logger.info(Util.toString(cpb.dlgId) + "::: processCIRp:Exit");
	}
	
	public static void processEntityReleased(int dilgId, SasCapCallProcessBuffer cpb, byte[] parms, IDecoder decoder) throws Exception{
		String method  = "processEntityReleased" ;
		logger.info(Util.toString(cpb.dlgId) + "::: " + method + ":Enter");
		InputStream ins = new ByteArrayInputStream(parms);
		EntityReleasedArg entityReleasedArg;
		try {
			logger.info(Util.toString(dilgId) + "::: "+ method +":decoding the EntityReleasedArg");
			entityReleasedArg = decoder.decode(ins, EntityReleasedArg.class);
			logger.info(Util.toString(dilgId) + "::: "+ method +":successfully decoded the EntityReleasedArg");
		} catch (Exception e) {
			logger.error(Util.toString(dilgId) + ":::" + method + ":" , e);
			throw e ;
		}
		
		cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
		
		if(entityReleasedArg.isBCSM_FailureSelected()) {
			logger.info(Util.toString(cpb.dlgId) + "::: " + method + ":BCSM_FailureSelected");
			BCSM_Failure bcsmFailure = entityReleasedArg.getBCSM_Failure();
			if(bcsmFailure.isLegIDPresent()){
				logger.info(Util.toString(cpb.dlgId) + "::: " + method + ":LegIDPresent");
				LegID legId = bcsmFailure.getLegID();
				String legType = null;
				if(legId.isReceivingSideIDSelected()){
					logger.info(Util.toString(cpb.dlgId) + "::: " + method + ":ReceivingSideIDSelected");
					 legType = Util.formatBytes(legId.getReceivingSideID().getValue());
				}else if(legId.isSendingSideIDSelected()){
					logger.info(Util.toString(cpb.dlgId) + "::: " + method + ":SendingSideIDSelected");
					 legType = Util.formatBytes(legId.getReceivingSideID().getValue());
				}
				if(legType != null){
					logger.debug(Util.toString(cpb.dlgId) + "::: " + method + ":legType in bcsmFailure: " + legType);
					if(legType.equalsIgnoreCase("0x01")){
						cpb.stateInfo.setCurrState(SasCapCallStateEnum.O_DISCONNECT_LEG1);
					}else if(legType.equalsIgnoreCase("0x02")){
						cpb.stateInfo.setCurrState(SasCapCallStateEnum.O_DISCONNECT_LEG2);
					}
				}
			}
			if(bcsmFailure.isCausePresent()){
				logger.info(Util.toString(cpb.dlgId) + "::: " + method + ":CausePresent");
				CauseDataType causeDataType = CauseDataType.decodeCauseVal(bcsmFailure.getCause().getValue());
				logger.debug(Util.toString(cpb.dlgId) + "::: " + method + ":Cause value:" + causeDataType);		
			}			
		}
		else if(entityReleasedArg.isCallSegmentFailureSelected()){
			logger.info(Util.toString(cpb.dlgId) + "::: " + method + ":CallSegmentFailureSelected");
			CallSegmentFailure callSegmentFailure = entityReleasedArg.getCallSegmentFailure();
			if(callSegmentFailure.isCallSegmentIDPresent()){
				logger.info(Util.toString(cpb.dlgId) + "::: " + method + ":CallSegmentIDPresent");
				Integer callSegmentId = callSegmentFailure.getCallSegmentID().getValue();
				logger.debug(Util.toString(cpb.dlgId) + "::: " + method + ":CallSegmentIDPresent:" + callSegmentId);
				if(callSegmentId == 1){
					cpb.stateInfo.setCurrState(SasCapCallStateEnum.O_DISCONNECT_LEG1);
				}else if(callSegmentId == 2){
					cpb.stateInfo.setCurrState(SasCapCallStateEnum.O_DISCONNECT_LEG2);
				}else {
					cpb.stateInfo.setCurrState(SasCapCallStateEnum.O_DISCONNECT_LEG1);
				}
			}
			if(callSegmentFailure.isCausePresent()){
				logger.info(Util.toString(cpb.dlgId) + "::: " + method + ":isCausePresent");
				CauseDataType causeDataType = CauseDataType.decodeCauseVal(callSegmentFailure.getCause().getValue());
				logger.debug(Util.toString(cpb.dlgId) + "::: " + method + ":Cause value:" + causeDataType);	
			}
		}
		logger.info(Util.toString(cpb.dlgId) + "::: " + method + ":Exit");
	}
	
	
	private static void makeInvokeReqEvent(Object source, CallInformationRequestArg callInfoArg, SasCapCallProcessBuffer cpb, int invokeId, SasCapMsgsToSend msgs) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: makeInvokeReqEvent:Enter");
		try {
			IEncoder<CallInformationRequestArg> encoder = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			logger.info(Util.toString(cpb.dlgId) + "::: makeInvokeReqEvent:Encoding the CallInformationRequestArg");
			encoder.encode(callInfoArg, outputStream);
			byte[] encodedCIRq = outputStream.toByteArray();
			
			if(logger.isDebugEnabled()){
			logger.debug(Util.toString(cpb.dlgId) + "::: makeInvokeReqEvent:Encoded CallInformationRequestArg: " + Util.formatBytes(encodedCIRq));
			logger.debug(Util.toString(cpb.dlgId) + "::: makeInvokeReqEvent:length of encoded CallInformationRequestArg: " + encodedCIRq.length);
			}
			
			byte[] callInfoOpCode =  { CAPOpcode.CALL_INFORMATION_REQUEST };
			Operation callInfoOp = new Operation(Operation.OPERATIONTYPE_LOCAL, callInfoOpCode);
			InvokeReqEvent ire = new InvokeReqEvent(source, cpb.dlgId, callInfoOp);
			ire.setInvokeId(invokeId);
			ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedCIRq));
			ire.setClassType(CAPOpcode.CALL_INFORMATION_REQ_CLASS);
			List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
			list.add(ire);
			msgs.setCompReqEvents(list);
			} catch(Exception e){
				logger.error(Util.toString(cpb.dlgId) + "::: makeInvokeReqEvent:Exception" ,e);
				throw e ;
			}
		logger.info(Util.toString(cpb.dlgId) + "::: makeInvokeReqEvent:Exit");
	}
	
	private static void setDefaultValuesForCIRq(SasCapCallProcessBuffer cpb, CallInformationRequestArg callInfoArg, int leg) {
		logger.info(Util.toString(cpb.dlgId) + "::: setDefaultValuesForCIRq:Enter");
		RequestedInformationTypeList  reqInfoTypeList = new RequestedInformationTypeList() ;
		ArrayList<RequestedInformationType> reqList = new ArrayList<RequestedInformationType>();
		
		logger.info(Util.toString(cpb.dlgId) + "::: setDefaultValuesForCIRq value:callAttemptElapsedTime");
		RequestedInformationType reqType1 = new RequestedInformationType();		
		reqType1.setValue(RequestedInformationType.EnumType.callAttemptElapsedTime);
		reqList.add(reqType1);
		
		logger.info(Util.toString(cpb.dlgId) + "::: setDefaultValuesForCIRq callConnectedElapsedTime");
		RequestedInformationType reqType2 = new RequestedInformationType();		
		reqType2.setValue(RequestedInformationType.EnumType.callConnectedElapsedTime);
		reqList.add(reqType2);
		
		logger.info(Util.toString(cpb.dlgId) + "::: setDefaultValuesForCIRq callStopTime");
		RequestedInformationType reqType3 = new RequestedInformationType();		
		reqType3.setValue(RequestedInformationType.EnumType.callStopTime);
		reqList.add(reqType3);
		
		logger.info(Util.toString(cpb.dlgId) + "::: setDefaultValuesForCIRq callStopTime");
		RequestedInformationType reqType4 = new RequestedInformationType();		
		reqType4.setValue(RequestedInformationType.EnumType.releaseCause);	
		reqList.add(reqType4);
		
		reqInfoTypeList.setValue(reqList);
		
		callInfoArg.setRequestedInformationTypeList(reqInfoTypeList);
		
		SendingSideID sendingSideID = new SendingSideID();
		logger.info(Util.toString(cpb.dlgId) + "::: setDefaultValuesForCIRq value:legId 1");
		if(leg == 1){
			byte[] legType = {(byte)0x01 } ;
			sendingSideID.selectSendingSideID(new LegType(legType));
		}else if(leg == 2){
			byte[] legType = {(byte)0x02 } ;
			sendingSideID.selectSendingSideID(new LegType(legType));
		}
		callInfoArg.setLegID(sendingSideID);
		logger.info(Util.toString(cpb.dlgId) + "::: setDefaultValuesForCIRq:Exit");
	}
}
