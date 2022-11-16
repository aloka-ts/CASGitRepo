package com.camel.CAPMsg;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.bn.IDecoder;

import asnGenerated.EventReportBCSMArg;
import asnGenerated.EventSpecificInformationBCSM;
import asnGenerated.EventTypeBCSM;

import com.camel.dataTypes.CauseDataType;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.Util;

/**
 * This class have methods to set SasCapCallProcessBuffer
 * using the parameters of EventReportBcsm.
 * @author nkumar
 *
 */
public class SasCapEventReport {

	//Instance of logger
	private static Logger logger = Logger.getLogger(SasCapEventReport.class);

	/**
	 * This function will use parameters of EventReportBcsmArg and set them in SasCapCallProcessBuffer.
	 * @param dlgId
	 * @param parms
	 * @param cpb
	 * @param decoder 
	 * @throws InvalidInputException
	 */
	public static void setEventReprtParamsOfCpb(int dlgId, byte[] parms, SasCapCallProcessBuffer cpb, IDecoder decoder) throws InvalidInputException, Exception{
		logger.info(Util.toString(dlgId) + "::: setEventReprtParams:Enter");
		InputStream ins = new ByteArrayInputStream(parms);
		EventReportBCSMArg eventReport = null;
		try {
			eventReport = decoder.decode(ins, EventReportBCSMArg.class);
		} catch (Exception e) {
			logger.error(Util.toString(dlgId) + "::: setEventReprtParamsOfCpb:" , e);
			throw e ;
		}
		cpb.eventType = eventReport.getEventTypeBCSM() ;
		logger.debug(Util.toString(dlgId) + "::: setEventReprtParams:event type:"+ cpb.eventType.getValue());
		if(eventReport.isEventSpecificInformationBCSMPresent()){
			logger.info(Util.toString(dlgId) + "::: setEventReprtParams:EventSpecificInformationBCSMPresent");
			EventSpecificInformationBCSM eventSpecific = eventReport.getEventSpecificInformationBCSM();
			if(eventSpecific.isRouteSelectFailureSpecificInfoSelected()){
				logger.info(Util.toString(dlgId) + "::: setEventReprtParams:RouteSelectFailureSpecificInfoSelected");
				cpb.cause = CauseDataType.decodeCauseVal(eventSpecific.getRouteSelectFailureSpecificInfo().getFailureCause().getValue());
				logger.debug(Util.toString(dlgId) + "::: setEventReprtParams:Cause: " +cpb.cause);
			}
			else if(eventSpecific.isOCalledPartyBusySpecificInfoSelected()){
				logger.info(Util.toString(dlgId) + "::: setEventReprtParams:OCalledPartyBusySpecificInfoSelected");
				cpb.cause = CauseDataType.decodeCauseVal(eventSpecific.getOCalledPartyBusySpecificInfo().getBusyCause().getValue());
				logger.debug(Util.toString(dlgId) + "::: setEventReprtParams:Cause: " +cpb.cause);
			}
			else if(eventSpecific.isODisconnectSpecificInfoSelected()){
				logger.info(Util.toString(dlgId) + "::: setEventReprtParams:ODisconnectSpecificInfoSelected");
				cpb.cause = CauseDataType.decodeCauseVal(eventSpecific.getODisconnectSpecificInfo().getReleaseCause().getValue());
				logger.debug(Util.toString(dlgId) + "::: setEventReprtParams:Cause: " +cpb.cause);
			}
			else if(eventSpecific.isTBusySpecificInfoSelected()){
				logger.info(Util.toString(dlgId) + "::: setEventReprtParams:TBusySpecificInfoSelected");
				cpb.cause = CauseDataType.decodeCauseVal(eventSpecific.getTBusySpecificInfo().getBusyCause().getValue());
				logger.debug(Util.toString(dlgId) + "::: setEventReprtParams:Cause: " +cpb.cause);
			}
			else if(eventSpecific.isTDisconnectSpecificInfoSelected()){
				logger.info(Util.toString(dlgId) + "::: setEventReprtParams:TDisconnectSpecificInfoSelected");
				cpb.cause = CauseDataType.decodeCauseVal(eventSpecific.getTDisconnectSpecificInfo().getReleaseCause().getValue());
				logger.debug(Util.toString(dlgId) + "::: setEventReprtParams:Cause: " +cpb.cause);
			}
		}
		if(cpb.eventType.getValue() == EventTypeBCSM.EnumType.oAbandon){
			logger.info(Util.toString(dlgId) + "::: setEventReprtParamsOfCpb:oAbandon: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.O_ABANDON);
			if(!checkLegIdPresent(dlgId, cpb, eventReport)){
				cpb.legType = "0x01" ;
			}
			
		}else if(cpb.eventType.getValue() == EventTypeBCSM.EnumType.oAnswer){
			logger.info(Util.toString(dlgId) + "::: setEventReprtParamsOfCpb:oAnswer: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.CONNECTED);
			if(!checkLegIdPresent(dlgId, cpb, eventReport)){
				cpb.legType = "0x02" ;
			}
			
		}else if(cpb.eventType.getValue() == EventTypeBCSM.EnumType.oCalledPartyBusy){
			logger.info(Util.toString(dlgId) + "::: setEventReprtParamsOfCpb:oCalledPartyBusy: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.O_BUSY);
			if(!checkLegIdPresent(dlgId, cpb, eventReport)){
				cpb.legType = "0x02" ;
			}
			
		}else if(cpb.eventType.getValue() == EventTypeBCSM.EnumType.oDisconnect){
			logger.info(Util.toString(dlgId) + "::: setEventReprtParamsOfCpb:oDisconnect: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			checkLegIdPresent(dlgId, cpb, eventReport);
			if(cpb.legType.equalsIgnoreCase("0x01")){
				cpb.stateInfo.setCurrState(SasCapCallStateEnum.O_DISCONNECT_LEG1);
			}else if(cpb.legType.equalsIgnoreCase("0x02")){
				cpb.stateInfo.setCurrState(SasCapCallStateEnum.O_DISCONNECT_LEG2);
			}
			
		}else if(cpb.eventType.getValue() == EventTypeBCSM.EnumType.oNoAnswer){
			logger.info(Util.toString(dlgId) + "::: setEventReprtParamsOfCpb:oNoAnswer: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.O_NOANSWER);
			if(!checkLegIdPresent(dlgId, cpb, eventReport)){
				cpb.legType = "0x02" ;
			}
		}else if(cpb.eventType.getValue() == EventTypeBCSM.EnumType.tAbandon){
			logger.info(Util.toString(dlgId) + "::: setEventReprtParamsOfCpb:tAbandon: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.T_ABANDON);
			if(!checkLegIdPresent(dlgId, cpb, eventReport)){
				cpb.legType = "0x01" ;
			}
			
		}else if(cpb.eventType.getValue() == EventTypeBCSM.EnumType.tAnswer){
			logger.info(Util.toString(dlgId) + "::: setEventReprtParamsOfCpb:tAnswer: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.CONNECTED);
			if(!checkLegIdPresent(dlgId, cpb, eventReport)){
				cpb.legType = "0x02" ;
			}
			
		}else if(cpb.eventType.getValue() == EventTypeBCSM.EnumType.tBusy){
			logger.info(Util.toString(dlgId) + "::: setEventReprtParamsOfCpb:tBusy: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.T_BUSY);
			if(!checkLegIdPresent(dlgId, cpb, eventReport)){
				cpb.legType = "0x02" ;
			}
			
		}else if(cpb.eventType.getValue() == EventTypeBCSM.EnumType.tDisconnect){
			logger.info(Util.toString(dlgId) + "::: setEventReprtParamsOfCpb:tDisconnect: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			checkLegIdPresent(dlgId, cpb, eventReport);
			if(cpb.legType.equalsIgnoreCase("0x01")){
				cpb.stateInfo.setCurrState(SasCapCallStateEnum.T_DISCONNECT_LEG1);
			}else if(cpb.legType.equalsIgnoreCase("0x02")){
				cpb.stateInfo.setCurrState(SasCapCallStateEnum.T_DISCONNECT_LEG2);
			}
			
		}else if(cpb.eventType.getValue() == EventTypeBCSM.EnumType.tNoAnswer){
			logger.info(Util.toString(dlgId) + "::: setEventReprtParamsOfCpb:tNoAnswer: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.T_NOANSWER);
			if(!checkLegIdPresent(dlgId, cpb, eventReport)){
				cpb.legType = "0x02" ;
			}
		}else if(cpb.eventType.getValue() == EventTypeBCSM.EnumType.routeSelectFailure){
			logger.info(Util.toString(dlgId) + "::: setEventReprtParamsOfCpb:routeSelectFailure: Current state:" + cpb.stateInfo.getCurrState());
			cpb.stateInfo.setPrevState(cpb.stateInfo.getCurrState());
			cpb.stateInfo.setCurrState(SasCapCallStateEnum.ROUTE_SELECT_FAILURE);
			if(!checkLegIdPresent(dlgId, cpb, eventReport)){
				cpb.legType = "0x01" ;
			}
		}
		cpb.miscCallInfo = eventReport.getMiscCallInfo();
		logger.debug(Util.toString(dlgId) + "::: setEventReprtParams:miscCallInfo:" + cpb.miscCallInfo.getMessageType().getValue());
		logger.info(Util.toString(dlgId) + "::: setEventReprtParams:Exit");
	}
	
	private static boolean checkLegIdPresent(int dlgId, SasCapCallProcessBuffer cpb, EventReportBCSMArg eventReport){
		if(eventReport.isLegIDPresent()){
			logger.info(Util.toString(dlgId) + "::: checkLegIdPresent:legId present");
			cpb.legType = Util.formatBytes(eventReport.getLegID().getReceivingSideID().getValue());
			logger.debug(Util.toString(dlgId) + "::: checkLegIdPresent:legType:"+ cpb.legType);
			return true ;
		}
		return false ;
	}
}
