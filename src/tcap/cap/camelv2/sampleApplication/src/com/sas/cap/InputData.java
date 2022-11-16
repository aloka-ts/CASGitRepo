package com.sas.cap;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import asnGenerated.AudibleIndicator;
import asnGenerated.BCSMEvent;
import asnGenerated.BothwayThroughConnectionInd;
import asnGenerated.CallSegmentID;
import asnGenerated.ErrorTreatment;
import asnGenerated.EventTypeBCSM;
import asnGenerated.Integer4;
import asnGenerated.LegID;
import asnGenerated.LegType;
import asnGenerated.MonitorMode;
import asnGenerated.Tone;
import asnGenerated.EventTypeBCSM.EnumType;

import com.camel.CAPMsg.SasCapCallProcessBuffer;
import com.camel.CAPMsg.SasCapInbandInfoDataType;
import com.camel.CAPMsg.SasCapMsgIdEnum;
import com.camel.CAPMsg.SasCapVariableMsgDataType;
import com.camel.CAPMsg.SasCapVariablePartDataType;
import com.camel.dataTypes.CalledPartyNum;
import com.camel.dataTypes.GenericDigitsDataType;

import com.camel.CAPMsg.*;
import com.camel.enumData.*;


/**
 * 
 * @author nkumar
 *
 */
public class InputData {
	
	private static Logger logger = Logger.getLogger(InputData.class);
	
	static void setArgsForCTR(SasCapCallProcessBuffer buffer, Properties prop){
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForCTR:Enter");
		String natureOfAdrs = prop.getProperty("NatureOfAdrsEnum");
		String numPlan = prop.getProperty("NumPlanEnum");
		String intNtwrk = prop.getProperty("IntNtwrkNumEnum");
		String adrSignal = prop.getProperty("addrSignal");
		String bothwayPath = prop.getProperty("bothwayPathRequired");
		String callSegmentIdForCTR = prop.getProperty("CallSegmentIdForCTR");
		if(logger.isDebugEnabled()){
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForCTR:NatureOfAdrsEnum:" + natureOfAdrs);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForCTR:NumPlanEnum:" + numPlan);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForCTR:IntNtwrkNumEnum:" + intNtwrk);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForCTR:addrSignal:" + adrSignal);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForCTR:bothwayPath:" + bothwayPath);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForCTR:CallSegmentIdForCTR:" + callSegmentIdForCTR);
			
		}
		if(natureOfAdrs != null && numPlan != null && intNtwrk != null && adrSignal != null ){
			CalledPartyNum cldNum = new CalledPartyNum() ;
			cldNum.setAddrSignal(adrSignal);
			cldNum.setIntNtwrkNum(IntNtwrkNumEnum.fromInt(Integer.parseInt(intNtwrk)));
			cldNum.setNatureOfAdrs(NatureOfAdrsEnum.fromInt(Integer.parseInt(natureOfAdrs)));
			cldNum.setNumPlan(NumPlanEnum.fromInt(Integer.parseInt(numPlan)));
			buffer.ipRoutingAdrs = cldNum ;
		}
		if(callSegmentIdForCTR != null){
			buffer.callSegmentID = new CallSegmentID(Integer.parseInt(callSegmentIdForCTR));
		}
				
		if(bothwayPath != null) {
			buffer.bothwayThroughConnectionInd = new BothwayThroughConnectionInd();
			if(bothwayPath.equalsIgnoreCase("true"))
			buffer.bothwayThroughConnectionInd.setValue(asnGenerated.BothwayThroughConnectionInd.EnumType.bothwayPathRequired);
			else
				buffer.bothwayThroughConnectionInd.setValue(asnGenerated.BothwayThroughConnectionInd.EnumType.bothwayPathNotRequired);
		}
		
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForCTR:Exit");
	}
	
	static void setArgsForRRBCSM(SasCapCallProcessBuffer buffer, Properties prop){
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForRRBCSM:Enter");
		String o_bcsmEvent = prop.getProperty(Constants.O_BCSMEVENT);
		buffer.bcsmEventList = new ArrayList<BCSMEvent>();
		String[] bcsmEventList = o_bcsmEvent.split(":");
		for(int i = 0 ; i < bcsmEventList.length ; i++) {
			BCSMEvent bcsmEvent = new BCSMEvent() ;
			String[] bcsmEventStr = bcsmEventList[i].split(",");
			EventTypeBCSM event = new EventTypeBCSM();
			if(bcsmEventStr[0].equalsIgnoreCase("oAbandon")){				
				event.setValue(EnumType.oAbandon);								
			}else if(bcsmEventStr[0].equalsIgnoreCase("oAnswer")){
				event.setValue(EnumType.oAnswer);				
			}else if(bcsmEventStr[0].equalsIgnoreCase("oNoAnswer")){
				event.setValue(EnumType.oNoAnswer);				
			}else if(bcsmEventStr[0].equalsIgnoreCase("oCalledPartyBusy")){
				event.setValue(EnumType.oCalledPartyBusy);				
			}else if(bcsmEventStr[0].equalsIgnoreCase("oDisconnect")){
				event.setValue(EnumType.oDisconnect);				
			}else if(bcsmEventStr[0].equalsIgnoreCase("routeSelectFailure")){
				event.setValue(EnumType.routeSelectFailure);				
			}
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForRRBCSM:setEventTypeBCSM:" + event.getValue());
			bcsmEvent.setEventTypeBCSM(event);
			
			MonitorMode md = new MonitorMode();
			if(bcsmEventStr[1].equalsIgnoreCase("interrupted")){	
				md.setValue(MonitorMode.EnumType.interrupted);
			}else if(bcsmEventStr[1].equalsIgnoreCase("notifyAndContinue")){
				md.setValue(MonitorMode.EnumType.notifyAndContinue);
			}else if(bcsmEventStr[1].equalsIgnoreCase("transparent")){
				md.setValue(MonitorMode.EnumType.transparent);
			}
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForRRBCSM:MonitorMode:" + md.getValue());
			bcsmEvent.setMonitorMode(md);
			
			LegID legID = new LegID();
			if(bcsmEventStr[2].equalsIgnoreCase("legID1")){				
				byte[] legtype = {0x01 } ;
				legID.selectSendingSideID(new LegType(legtype));				
			}else if(bcsmEventStr[2].equalsIgnoreCase("legID2")){				
				byte[] legtype = {0x02 } ;
				legID.selectSendingSideID(new LegType(legtype));				
			}
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForRRBCSM:setLegID:" + legID.getSendingSideID().getValue());
			bcsmEvent.setLegID(legID);
			buffer.bcsmEventList.add(bcsmEvent);
		}
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForRRBCSM:Exit");
	}
	
	static void setArgsForRRBCSMForTEvents(SasCapCallProcessBuffer buffer, Properties prop){
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForRRBCSM:Enter");
		String t_bcsmEvent = prop.getProperty(Constants.T_BCSMEVENT);
		buffer.bcsmEventList = new ArrayList<BCSMEvent>();
		String[] bcsmEventList = t_bcsmEvent.split(":");
		for(int i = 0 ; i < bcsmEventList.length ; i++) {
			BCSMEvent bcsmEvent = new BCSMEvent() ;
			String[] bcsmEventStr = bcsmEventList[i].split(",");
			EventTypeBCSM event = new EventTypeBCSM();
			if(bcsmEventStr[0].equalsIgnoreCase("tAbandon")){				
				event.setValue(EnumType.tAbandon);								
			}else if(bcsmEventStr[0].equalsIgnoreCase("tAnswer")){
				event.setValue(EnumType.tAnswer);				
			}else if(bcsmEventStr[0].equalsIgnoreCase("tNoAnswer")){
				event.setValue(EnumType.tNoAnswer);				
			}else if(bcsmEventStr[0].equalsIgnoreCase("tBusy")){
				event.setValue(EnumType.tBusy);				
			}else if(bcsmEventStr[0].equalsIgnoreCase("tDisconnect")){
				event.setValue(EnumType.tDisconnect);				
			}else if(bcsmEventStr[0].equalsIgnoreCase("routeSelectFailure")){
				event.setValue(EnumType.routeSelectFailure);				
			}
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForRRBCSM:setEventTypeBCSM:" + event.getValue());
			bcsmEvent.setEventTypeBCSM(event);
			
			MonitorMode md = new MonitorMode();
			if(bcsmEventStr[1].equalsIgnoreCase("interrupted")){	
				md.setValue(MonitorMode.EnumType.interrupted);
			}else if(bcsmEventStr[1].equalsIgnoreCase("notifyAndContinue")){
				md.setValue(MonitorMode.EnumType.notifyAndContinue);
			}else if(bcsmEventStr[1].equalsIgnoreCase("transparent")){
				md.setValue(MonitorMode.EnumType.transparent);
			}
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForRRBCSM:MonitorMode:" + md.getValue());
			bcsmEvent.setMonitorMode(md);
			
			LegID legID = new LegID();
			if(bcsmEventStr[2].equalsIgnoreCase("legID1")){				
				byte[] legtype = {0x01 } ;
				legID.selectSendingSideID(new LegType(legtype));				
			}else if(bcsmEventStr[2].equalsIgnoreCase("legID2")){				
				byte[] legtype = {0x02 } ;
				legID.selectSendingSideID(new LegType(legtype));				
			}
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForRRBCSM:setLegID:" + legID.getSendingSideID().getValue());
			bcsmEvent.setLegID(legID);
			buffer.bcsmEventList.add(bcsmEvent);
		}
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForRRBCSM:Exit");
	}
	
	static void setArgsForPlay(SasCapCallProcessBuffer buffer, Properties prop){
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:Enter");
		String disconnectFromIPForbidden = prop.getProperty("DisconnectFromIPForbidden");
		String requestAnnouncementComplete = prop.getProperty("RequestAnnouncementComplete");
		String requestAnnouncementStarted = prop.getProperty("RequestAnnouncementStarted");
		String toneId = prop.getProperty("play_toneId");
		String play_tone_duration = prop.getProperty("play_tone_duration");
		String resetTimer = prop.getProperty("resetTimer");
		String resetTimerDuration = prop.getProperty("resetTimerDuration");
		
		String InformationToSend = prop.getProperty("InformationToSend");
		String elementaryMessageID = prop.getProperty("elementaryMessageID");
		String elementaryMessageIDs = prop.getProperty("elementaryMessageIDs");
		String variableMsgId = prop.getProperty("variableMsgId");
		String time = prop.getProperty("time");
		String date = prop.getProperty("date");
		String price = prop.getProperty("price");
		String value = prop.getProperty("value");
		String encodingSchemeEnum = prop.getProperty("encodingSchemeEnum");
		String digits = prop.getProperty("digits");
		String numberOfRepetitions = prop.getProperty("numberOfRepetitions");
		String duration = prop.getProperty("duration");
		String interval = prop.getProperty("interval");
		String callSegmentIDForPlay = prop.getProperty("CallSegmentIDForPlay");
		
		if(logger.isDebugEnabled()){
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:disconnectFromIPForbidden:" + disconnectFromIPForbidden);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:RequestAnnouncementComplete:" + requestAnnouncementComplete);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:requestAnnouncementStarted:" + requestAnnouncementStarted);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:play_toneId:" + toneId);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:play_tone_duration:" + play_tone_duration);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:resetTimerDuration:" + resetTimerDuration);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:resetTimer:" + resetTimer);
			
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:InformationToSend:" + InformationToSend);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:elementaryMessageID:" + elementaryMessageID);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:elementaryMessageIDs:" + elementaryMessageIDs);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:variableMsgId:" + variableMsgId);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:time:" + time);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:date:" + date);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:price:" + price);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:value:" + value);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:encodingSchemeEnum:" + encodingSchemeEnum);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:digits:" + digits);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:numberOfRepetitions:" + numberOfRepetitions);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:duration:" + duration);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:interval:" + interval);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:callSegmentIDForPlay:" + callSegmentIDForPlay);
		}
		if(disconnectFromIPForbidden != null){
			buffer.disconnectFromIPForbidden = (disconnectFromIPForbidden.equalsIgnoreCase("false")? false : true);
		}
		if(requestAnnouncementComplete != null){
			buffer.requestAnnouncementComplete = (requestAnnouncementComplete.equalsIgnoreCase("false")? false : true);
		}
		if(requestAnnouncementStarted != null){
			buffer.requestAnnouncementStarted = (requestAnnouncementStarted.equalsIgnoreCase("true")? true : false);
		}
		if(callSegmentIDForPlay != null){
			CallSegmentID callSegmentID = new CallSegmentID(Integer.parseInt(callSegmentIDForPlay));
			buffer.callSegmentID = callSegmentID ;
		}
		/*if(toneId != null ){
			buffer.informationtoSendEnum = SasCapInformationtoSendEnum.TONE ;			
			buffer.tone = new Tone();
			buffer.tone.setToneID(new Integer4(Integer.parseInt(toneId)));
			if(play_tone_duration != null)
				buffer.tone.setDuration(new Integer4(Integer.parseInt(play_tone_duration)));
		}*/
		if(resetTimerDuration != null){
			buffer.resetTimerDuration = Integer.parseInt(resetTimerDuration);
		}
		if(resetTimer != null){
			buffer.resetTimer = new Boolean(resetTimer);
		}
		
		if("INBAND_INFO".equalsIgnoreCase(InformationToSend)){
			buffer.informationtoSendEnum = SasCapInformationtoSendEnum.INBAND_INFO ;		
			buffer.inbandInfoDataType = new SasCapInbandInfoDataType() ;
			if(numberOfRepetitions !=null){
				buffer.inbandInfoDataType.numberOfRepetitions = Integer.parseInt(numberOfRepetitions);
			}
			if(duration != null){
				buffer.inbandInfoDataType.duration = Integer.parseInt(duration);
			}
			if(interval != null){
				buffer.inbandInfoDataType.interval = Integer.parseInt(interval);
			}
			
			if(elementaryMessageID != null){
				buffer.inbandInfoDataType.msgId = SasCapMsgIdEnum.ELEMENTRY_MSG_ID ;
				buffer.elementryMsgId = Integer.parseInt(elementaryMessageID);
			}else if(elementaryMessageIDs != null){
				buffer.inbandInfoDataType.msgId = SasCapMsgIdEnum.ELEMENTRY_MSG_ID_LIST ;
				buffer.elementryMsgIdList = new ArrayList<Integer>();
				String[] elementryList = elementaryMessageIDs.split(",");
				for(int k=0 ; k<elementryList.length; k++){
					buffer.elementryMsgIdList.add(Integer.parseInt(elementryList[k]));
				}				
			}else if(variableMsgId != null){
				buffer.inbandInfoDataType.msgId = SasCapMsgIdEnum.VARIABLEMSG ;
				buffer.variableMsg = new SasCapVariableMsgDataType();
				buffer.variableMsg.elementryMsgId = Integer.parseInt(variableMsgId);
				List<SasCapVariablePartDataType> list = new ArrayList<SasCapVariablePartDataType>();
				if(value != null){
					SasCapVariablePartDataType varPartData =  new SasCapVariablePartDataType() ;
					varPartData.value = Integer.parseInt(value) ;
					list.add(varPartData);
				}
				if(time != null){
					SasCapVariablePartDataType varPartData =  new SasCapVariablePartDataType() ;
					varPartData.time = time ;
					list.add(varPartData);
				}
				if(date != null){
					SasCapVariablePartDataType varPartData =  new SasCapVariablePartDataType() ;
					varPartData.date = date ;
					list.add(varPartData);
				}
				if(price != null){
					SasCapVariablePartDataType varPartData =  new SasCapVariablePartDataType() ;
					varPartData.price = price ;
					list.add(varPartData);
				}
				if(encodingSchemeEnum != null && digits != null){
					SasCapVariablePartDataType varPartData =  new SasCapVariablePartDataType() ;
					GenericDigitsDataType number = new GenericDigitsDataType();
					number.digits = digits ;
					number.encodingSchemeEnum = EncodingSchemeEnum.fromInt(Integer.parseInt(encodingSchemeEnum));
					varPartData.number = number ;
					list.add(varPartData);
				}
				buffer.variableMsg.variablePartList = list ;
			}
		}else if("TONE".equalsIgnoreCase(InformationToSend)){
			buffer.informationtoSendEnum = SasCapInformationtoSendEnum.TONE ;			
			buffer.tone = new Tone();
			if(toneId !=null)
				buffer.tone.setToneID(new Integer4(Integer.parseInt(toneId)));
			if(play_tone_duration != null)
				buffer.tone.setDuration(new Integer4(Integer.parseInt(play_tone_duration)));
		}
		
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForPlay:Exit");
	}
	
	static void setArgsForPromptCollect(SasCapCallProcessBuffer buffer, Properties prop){
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:Enter");
		String minimumNbOfDigits = prop.getProperty("minimumNbOfDigits");
		String maximumNbOfDigits = prop.getProperty("maximumNbOfDigits");
		String endOfReplyDigit     = prop.getProperty("endOfReplyDigit");
		String cancelDigit 			= prop.getProperty("cancelDigit");
		String firstDigitTimeOut	= prop.getProperty("firstDigitTimeOut");
		String interDigitTimeOut	= prop.getProperty("interDigitTimeOut");
		String interruptableAnnInd = prop.getProperty("interruptableAnnInd");
		String toneId = prop.getProperty("toneId");
		String tone_duration = prop.getProperty("tone_duration");
		String requestAnnouncementStarted = prop.getProperty("RequestAnnouncementStarted");
		String InformationToSend = prop.getProperty("InformationToSend");
		String elementaryMessageID = prop.getProperty("elementaryMessageID");
		String elementaryMessageIDs = prop.getProperty("elementaryMessageIDs");
		String variableMsgId = prop.getProperty("variableMsgId");
		String time = prop.getProperty("time");
		String date = prop.getProperty("date");
		String price = prop.getProperty("price");
		String value = prop.getProperty("value");
		String encodingSchemeEnum = prop.getProperty("encodingSchemeEnum");
		String digits = prop.getProperty("digits");
		String numberOfRepetitions = prop.getProperty("numberOfRepetitions");
		String duration = prop.getProperty("duration");
		String interval = prop.getProperty("interval");
		String errorTreatment = prop.getProperty("errorTreatment");
		String voiceInformation = prop.getProperty("voiceInformation");
		String voiceBack = prop.getProperty("voiceBack");
		String callSegmentIDForPlay = prop.getProperty("CallSegmentIDForPlay");
		if(logger.isDebugEnabled()){
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:minimumNbOfDigits:" + minimumNbOfDigits);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:maximumNbOfDigits:" + maximumNbOfDigits);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:interruptableAnnInd:" + interruptableAnnInd);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:toneId:" + toneId);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:tone_duration:" + tone_duration);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:InformationToSend:" + InformationToSend);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:elementaryMessageID:" + elementaryMessageID);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:elementaryMessageIDs:" + elementaryMessageIDs);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:variableMsgId:" + variableMsgId);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:time:" + time);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:date:" + date);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:price:" + price);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:value:" + value);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:encodingSchemeEnum:" + encodingSchemeEnum);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:digits:" + digits);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:numberOfRepetitions:" + numberOfRepetitions);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:duration:" + duration);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:interval:" + interval);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:errorTreatment:" + errorTreatment);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:voiceInformation:" + voiceInformation);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:voiceBack:" + voiceBack);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:requestAnnouncementStarted:" + requestAnnouncementStarted);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:CallSegmentIDForPlay:" + callSegmentIDForPlay);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:endOfReplyDigit:" + endOfReplyDigit);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:cancelDigit:" + cancelDigit);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:firstDigitTimeOut:" + firstDigitTimeOut);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:interDigitTimeOut:" + interDigitTimeOut);
		}
		if(minimumNbOfDigits != null){
			buffer.collectedDigits.maximumNbOfDigits = Integer.parseInt(minimumNbOfDigits);
		}
		if(maximumNbOfDigits != null){
			buffer.collectedDigits.maximumNbOfDigits = Integer.parseInt(maximumNbOfDigits);
		}
		if(endOfReplyDigit != null)
		{
			buffer.collectedDigits.endOfReplyDigit = endOfReplyDigit;
		}		
		if(cancelDigit != null)
		{
			buffer.collectedDigits.cancelDigit = cancelDigit;
		}		
		if(firstDigitTimeOut != null)
		{
			buffer.collectedDigits.firstDigitTimeOut = Integer.parseInt(firstDigitTimeOut); 
		}
		if (interDigitTimeOut != null)
		{
			buffer.collectedDigits.interDigitTimeOut = Integer.parseInt(interDigitTimeOut);
		}
		if(interruptableAnnInd != null){
			buffer.collectedDigits.interruptableAnnInd = interruptableAnnInd.equalsIgnoreCase("false")? false : true ;
		}
		if(requestAnnouncementStarted != null)
			buffer.requestAnnouncementStarted = requestAnnouncementStarted.equalsIgnoreCase("false")? false : true ;
		
		if(callSegmentIDForPlay != null){
			CallSegmentID callSegmentID = new CallSegmentID(Integer.parseInt(callSegmentIDForPlay));
			buffer.callSegmentID = callSegmentID ;
		}
		
		if(errorTreatment != null){
			if(errorTreatment.equalsIgnoreCase("1")){
				buffer.collectedDigits.errorTreatment = ErrorTreatment.EnumType.stdErrorAndInfo ;
			}else if(errorTreatment.equalsIgnoreCase("2")){
				buffer.collectedDigits.errorTreatment = ErrorTreatment.EnumType.help ;
			}else if(errorTreatment.equalsIgnoreCase("3")){
				buffer.collectedDigits.errorTreatment = ErrorTreatment.EnumType.repeatPrompt ;
			}
		}
		if(voiceBack != null){
			buffer.collectedDigits.voiceBack = new Boolean(voiceBack);
		}
		
		if(voiceInformation != null){
			buffer.collectedDigits.voiceInformation = new Boolean(voiceInformation);
		}
		if("INBAND_INFO".equalsIgnoreCase(InformationToSend)){
			buffer.informationtoSendEnum = SasCapInformationtoSendEnum.INBAND_INFO ;		
			buffer.inbandInfoDataType = new SasCapInbandInfoDataType() ;
			if(numberOfRepetitions !=null){
				buffer.inbandInfoDataType.numberOfRepetitions = Integer.parseInt(numberOfRepetitions);
			}
			if(duration != null){
				buffer.inbandInfoDataType.duration = Integer.parseInt(duration);
			}
			if(interval != null){
				buffer.inbandInfoDataType.interval = Integer.parseInt(interval);
			}
			
			if(elementaryMessageID != null){
				buffer.inbandInfoDataType.msgId = SasCapMsgIdEnum.ELEMENTRY_MSG_ID ;
				buffer.elementryMsgId = Integer.parseInt(elementaryMessageID);
			}else if(elementaryMessageIDs != null){
				buffer.inbandInfoDataType.msgId = SasCapMsgIdEnum.ELEMENTRY_MSG_ID_LIST ;
				buffer.elementryMsgIdList = new ArrayList<Integer>();
				String[] elementryList = elementaryMessageIDs.split(",");
				for(int k=0 ; k<elementryList.length; k++){
					buffer.elementryMsgIdList.add(Integer.parseInt(elementryList[k]));
				}				
			}else if(variableMsgId != null){
				buffer.inbandInfoDataType.msgId = SasCapMsgIdEnum.VARIABLEMSG ;
				buffer.variableMsg = new SasCapVariableMsgDataType();
				buffer.variableMsg.elementryMsgId = Integer.parseInt(variableMsgId);
				List<SasCapVariablePartDataType> list = new ArrayList<SasCapVariablePartDataType>();
				if(value != null){
					SasCapVariablePartDataType varPartData =  new SasCapVariablePartDataType() ;
					varPartData.value = Integer.parseInt(value) ;
					list.add(varPartData);
				}
				if(time != null){
					SasCapVariablePartDataType varPartData =  new SasCapVariablePartDataType() ;
					varPartData.time = time ;
					list.add(varPartData);
				}
				if(date != null){
					SasCapVariablePartDataType varPartData =  new SasCapVariablePartDataType() ;
					varPartData.date = date ;
					list.add(varPartData);
				}
				if(price != null){
					SasCapVariablePartDataType varPartData =  new SasCapVariablePartDataType() ;
					varPartData.price = price ;
					list.add(varPartData);
				}
				if(encodingSchemeEnum != null && digits != null){
					SasCapVariablePartDataType varPartData =  new SasCapVariablePartDataType() ;
					GenericDigitsDataType number = new GenericDigitsDataType();
					number.digits = digits ;
					number.encodingSchemeEnum = EncodingSchemeEnum.fromInt(Integer.parseInt(encodingSchemeEnum));
					varPartData.number = number ;
					list.add(varPartData);
				}
				buffer.variableMsg.variablePartList = list ;
			}
		}else if("TONE".equalsIgnoreCase(InformationToSend)){
			buffer.informationtoSendEnum = SasCapInformationtoSendEnum.TONE ;			
			buffer.tone = new Tone();
			if(toneId !=null)
				buffer.tone.setToneID(new Integer4(Integer.parseInt(toneId)));
			if(tone_duration != null)
				buffer.tone.setDuration(new Integer4(Integer.parseInt(tone_duration)));
		}
		
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForPromptCollect:Exit");
	}
	
	static void setArgsForConnect(SasCapCallProcessBuffer buffer, Properties prop){
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForConnect:Enter");
		String bcsmReport = prop.getProperty("requestReportBcsmConnect");
		if (null == bcsmReport || "".equals(bcsmReport)) {
			bcsmReport = "false";
		}
		buffer.requestReportBcsm = Boolean.parseBoolean(bcsmReport);
		String natureOfAdrs = prop.getProperty("dest_NatureOfAdrsEnum");
		String numPlan = prop.getProperty("dest_NumPlanEnum");
		String intNtwrk = prop.getProperty("dest_IntNtwrkNumEnum");
		String adrSignal = prop.getProperty("dest_addrSignal");
		String callInfoRequest = prop.getProperty("callInfoRequest");
		if(logger.isDebugEnabled()){
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForConnect:NatureOfAdrsEnum:" + natureOfAdrs);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForConnect:NumPlanEnum:" + numPlan);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForConnect:IntNtwrkNumEnum:" + intNtwrk);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForConnect:addrSignal:" + adrSignal);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForConnect:callInfoRequest:" + callInfoRequest);
		}
		if(natureOfAdrs != null && numPlan != null && intNtwrk != null && adrSignal != null ){
			CalledPartyNum cldNum = new CalledPartyNum() ;
			cldNum.setAddrSignal(adrSignal);
			cldNum.setIntNtwrkNum(IntNtwrkNumEnum.fromInt(Integer.parseInt(intNtwrk)));
			cldNum.setNatureOfAdrs(NatureOfAdrsEnum.fromInt(Integer.parseInt(natureOfAdrs)));
			cldNum.setNumPlan(NumPlanEnum.fromInt(Integer.parseInt(numPlan)));
			ArrayList<CalledPartyNum> list = new ArrayList<CalledPartyNum>();
			list.add(cldNum);
			buffer.destRoutingAdd = list ;
		}
		if(callInfoRequest != null){
			buffer.callInfoRequest = callInfoRequest.equalsIgnoreCase("true")? true : false ;
		}		
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForConnect:Exit");
	}
	
	static void setArgsForApplyCharging(SasCapCallProcessBuffer buffer, Properties prop){
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForApplyCharging:Enter");
		String totalTimeDuration = null;
		String maxCallPeriodDuration = null;
		String releaseIfDurationExced  = null;
		String tariifSwitchInterval  = null;
		if(!buffer.isTotalTimeDurationPresent())
			totalTimeDuration = prop.getProperty("totalTimeDuration");
		if(!buffer.ismaxCallPeriodDurationPresent())
			maxCallPeriodDuration = prop.getProperty("maxCallPeriodDuration");
		if(!buffer.isReleaseIfDurationExcedPresent())
			releaseIfDurationExced = prop.getProperty("releaseIfDurationExced");
		if(!buffer.isTariifSwitchIntervalPresent()) 
			tariifSwitchInterval = prop.getProperty("tariifSwitchInterval");
		
		String partyToCharge = prop.getProperty("partyToCharge");
		String tone = prop.getProperty("tone");
		
		if(totalTimeDuration != null){
			buffer.totalTimeDuration = Integer.parseInt(totalTimeDuration.trim());
		}
		if(maxCallPeriodDuration != null){
			buffer.maxCallPeriodDuration = Integer.parseInt(maxCallPeriodDuration.trim());
		}
		if(releaseIfDurationExced != null){
			buffer.releaseIfDurationExced = releaseIfDurationExced.equalsIgnoreCase("false") ? false : true;
		}
		if(tariifSwitchInterval != null){
			buffer.tariifSwitchInterval = Integer.parseInt(tariifSwitchInterval.trim());
		}
		if(partyToCharge != null){
			buffer.partyToCharge = partyToCharge ;
		}
		
		if(tone != null){
			buffer.audibleIndicator = new Boolean(tone);
		}
		if(logger.isDebugEnabled()){
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForApplyCharging:totalTimeDuration:" + buffer.totalTimeDuration);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForApplyCharging:maxCallPeriodDuration:" + buffer.maxCallPeriodDuration);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForApplyCharging:releaseIfDurationExced:" + buffer.releaseIfDurationExced);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForApplyCharging:tariifSwitchInterval:" + buffer.tariifSwitchInterval);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForApplyCharging:partyToCharge:" + buffer.partyToCharge);
			logger.debug("Dilaogue Id:" + buffer.dlgId + "::setArgsForApplyCharging:tone:" + tone);
		}
		logger.info("Dilaogue Id:" + buffer.dlgId + "::setArgsForApplyCharging:Exit");
	}
	
}
