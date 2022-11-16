package com.camel.CAPMsg;

import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IEncoder;

import asnGenerated.CalledPartyNumber;
import asnGenerated.CollectedDigits;
import asnGenerated.CollectedInfo;
import asnGenerated.ConnectToResourceArg;
import asnGenerated.Digits;
import asnGenerated.ErrorTreatment;
import asnGenerated.IPRoutingAddress;
import asnGenerated.InbandInfo;
import asnGenerated.InformationToSend;
import asnGenerated.Integer4;
import asnGenerated.MessageID;
import asnGenerated.PlayAnnouncementArg;
import asnGenerated.PromptAndCollectUserInformationArg;
import asnGenerated.ServiceInteractionIndicatorsTwo;
import asnGenerated.VariablePart;
import asnGenerated.ConnectToResourceArg.ResourceAddressChoiceType;
import asnGenerated.MessageID.VariableMessageSequenceType;

import com.camel.dataTypes.AdrsSignalDataType;
import com.camel.dataTypes.CalledPartyNum;
import com.camel.dataTypes.GenericDigitsDataType;
import com.camel.enumData.EncodingSchemeEnum;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.NonAsnArg;
import com.camel.util.Util;

/**
 * This class have methods to encode PC and PA using the 
 * parameters of SasCapCallProcessBuffer.
 * @author nkumar
 *
 */
public class SasCapPC {

	//Instance of logger
	private static Logger logger = Logger.getLogger(SasCapPC.class);

	/**
	 * This function will encode the Prompt and Collect UserInformation Arg.
	 * @param source
	 * @param cpb
	 * @param msgs
	 * @param invokeId
	 * @throws Exception
	 */
	public static void encodePC(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: encodePC:Enter");
		PromptAndCollectUserInformationArg pcArg = new PromptAndCollectUserInformationArg();
		logger.info(Util.toString(cpb.dlgId) + "::: encodePC:Calling setCollectedInfoParam");
		setCollectedInfoParam(cpb, pcArg);

		//setting the default value
		pcArg.setDisconnectFromIPForbidden(cpb.disconnectFromIPForbidden);
		logger.info(Util.toString(cpb.dlgId) + "::: encodePC:setDisconnectFromIPForbidden:" + pcArg.getDisconnectFromIPForbidden());

		if(cpb.isRequestAnnouncementStartedPresent()){
			pcArg.setRequestAnnouncementStartedNotification(cpb.requestAnnouncementStarted);
			logger.info(Util.toString(cpb.dlgId) + "::: encodePC:getRequestAnnouncementStartedNotification:" + pcArg.getRequestAnnouncementStartedNotification());
		}

		if(cpb.isInformationtoSendEnumPresent()){
			logger.info(Util.toString(cpb.dlgId) + "::: encodePC:Calling setInfotoSendParam");
			pcArg.setInformationToSend(setInfotoSendParam(cpb));
		}

		if(cpb.isCallSegmentIDPresent()){
			pcArg.setCallSegmentID(cpb.callSegmentID);
		}
		IEncoder<PromptAndCollectUserInformationArg> encoder;
		try {
			encoder = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			logger.info(Util.toString(cpb.dlgId) + "::: encodePC:encoding the PromptAndCollectUserInformationArg");
			encoder.encode(pcArg, outputStream);
			logger.info(Util.toString(cpb.dlgId) + "::: encodePC:successfully encoded the PromptAndCollectUserInformationArg");
			byte[] encodedData = outputStream.toByteArray();

			if(logger.isDebugEnabled()){
				logger.debug(Util.toString(cpb.dlgId) + "::: encodePC:Encoded PromptAndCollectUserInformationArg: " + Util.formatBytes(encodedData));
				logger.debug(Util.toString(cpb.dlgId) + "::: encodePC:length of encoded PromptAndCollectUserInformationArg: " + encodedData.length);
			}

			byte[] promptCollectOpCode =  { CAPOpcode.PROMPT_COLLECT };
			Operation pcOp = new Operation(Operation.OPERATIONTYPE_LOCAL, promptCollectOpCode);
			InvokeReqEvent ire = new InvokeReqEvent(source, cpb.dlgId, pcOp);
			ire.setInvokeId(invokeId);
			ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedData));
			ire.setClassType(CAPOpcode.PROMPT_COLLECT_CLASS);
			List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
			list.add(ire);
			msgs.setCompReqEvents(list);
		}catch(Exception e){
			logger.error(Util.toString(cpb.dlgId) + "::: encodePC:Exception:" , e);
			throw e ;
		}
		logger.info(Util.toString(cpb.dlgId) + "::: encodePC:Exit");	
	}

	private static void setCollectedInfoParam(SasCapCallProcessBuffer cpb, PromptAndCollectUserInformationArg pcArg) throws InvalidInputException {
		logger.info(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:Enter");	
		CollectedInfo clctdInfo = new CollectedInfo();
		//setting the collected digits
		CollectedDigits cltdDigits = new CollectedDigits();
		if(! cpb.isCollectedDigitsPresent()){
			logger.error(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:CollectedDigits parameter of CPB is null.It is mandatory parameter");
			throw new InvalidInputException("CollectedDigits parameter of CPB is null");
		}
		SasCapClctdDigitsDataType cldDigitsDataType = cpb.collectedDigits ;
		//change
		if(cldDigitsDataType.errorTreatment !=null){
			ErrorTreatment et = new ErrorTreatment();
			et.setValue(cldDigitsDataType.errorTreatment);
			cltdDigits.setErrorTreatment(et);

			logger.info(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:setting the errorTreatment:"+ et.getValue());
		}

		if(cldDigitsDataType.isFirstDigitTimeOutPresent()){
			logger.debug(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:FirstDigitTimeOutPresent:" + cldDigitsDataType.firstDigitTimeOut);	
			cltdDigits.setFirstDigitTimeOut(cldDigitsDataType.firstDigitTimeOut);
		}
		if(cldDigitsDataType.isInterDigitTimeOutPresent()){
			logger.debug(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:interDigitTimeOut:" + cldDigitsDataType.interDigitTimeOut);
			cltdDigits.setInterDigitTimeOut(cldDigitsDataType.interDigitTimeOut);
		}
		cltdDigits.setMinimumNbOfDigits(cldDigitsDataType.minimumNbOfDigits);
		logger.debug(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:MinimumNbOfDigits:" + cldDigitsDataType.minimumNbOfDigits);

		if(! cldDigitsDataType.isMaximumNbOfDigitsPresent()){
			logger.error(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:MaximumNbOfDigits parameter of collectedDigits instance of SasCapClctdDigitsDataType is null.It is mandatory parameter");
			throw new InvalidInputException("MaximumNbOfDigits parameter of collectedDigits instance of SasCapClctdDigitsDataType is null");
		}
		cltdDigits.setMaximumNbOfDigits(cldDigitsDataType.maximumNbOfDigits);
		logger.debug(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:maximumNbOfDigits:" + cldDigitsDataType.maximumNbOfDigits);

		cltdDigits.setInterruptableAnnInd(cldDigitsDataType.interruptableAnnInd);

		logger.debug(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:interruptableAnnInd:" + cldDigitsDataType.interruptableAnnInd);
		//change
		if(cldDigitsDataType.voiceBack != null){
			cltdDigits.setVoiceBack(cldDigitsDataType.voiceBack);
			logger.debug(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:voiceBack:" + cldDigitsDataType.voiceBack);
		}
		//change
		if(cldDigitsDataType.voiceInformation != null){
			cltdDigits.setVoiceInformation(cldDigitsDataType.voiceInformation);
			logger.debug(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:voiceInformation:" + cldDigitsDataType.voiceInformation);
		}
		if(cldDigitsDataType.isCancelDigitPresent()){
			logger.debug(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:cancelDigit:" + cldDigitsDataType.cancelDigit);
			byte[] data = NonAsnArg.collectedDigitsEncoder(cldDigitsDataType.cancelDigit);
			cltdDigits.setCancelDigit(data);
		}
		if(cldDigitsDataType.isEndOfReplyDigitPresent()){
			logger.debug(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:endOfReplyDigit:" + cldDigitsDataType.endOfReplyDigit);
			byte[] data = NonAsnArg.collectedDigitsEncoder(cldDigitsDataType.endOfReplyDigit);
			cltdDigits.setEndOfReplyDigit(data);
		}
		if(cldDigitsDataType.isStartDigitPresent()){
			logger.debug(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:startDigit:" + cldDigitsDataType.startDigit);
			byte[] data = NonAsnArg.collectedDigitsEncoder(cldDigitsDataType.startDigit);
			cltdDigits.setStartDigit(data);
		}
		logger.info(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:setting the collected digits");
		clctdInfo.selectCollectedDigits(cltdDigits);
		pcArg.setCollectedInfo(clctdInfo);
		logger.info(Util.toString(cpb.dlgId) + "::: setCollectedInfoParam:Exit");	
	}

	private static InformationToSend setInfotoSendParam(SasCapCallProcessBuffer cpb ) throws InvalidInputException{
		logger.info(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:Enter");	
		InformationToSend infoSend = new InformationToSend();

		if(cpb.informationtoSendEnum == SasCapInformationtoSendEnum.INBAND_INFO){
			logger.info(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:informationtoSendEnum is INBAND_INFO ");	
			InbandInfo inBand = new InbandInfo() ;
			if(! cpb.isInbandInfoDataTypePresent()){
				logger.error(Util.toString(cpb.dlgId) + "::: encodePC:inbandInfoDataType parameter of CPB is null.");
				throw new InvalidInputException("InbandInfoDataType parameter of CPB is null");
			}
			SasCapInbandInfoDataType inBandDataType = cpb.inbandInfoDataType ;
			if(!inBandDataType.isMsgIdPresent()){
				logger.error(Util.toString(cpb.dlgId) + "::: encodePC:MsgId parameter of inbandInfoDataType of CPB is null.");
				throw new InvalidInputException("MsgId parameter of inbandInfoDataType of cpb is null");
			}
			if(inBandDataType.isDurationPresent()){
				logger.debug(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:duration:" + inBandDataType.duration);	
				inBand.setDuration(inBandDataType.duration);
			}
			if(inBandDataType.isIntervalPresent()){
				logger.debug(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:interval:" + inBandDataType.interval);	
				inBand.setInterval(inBandDataType.interval);
			}
			if(inBandDataType.isNumberOfRepetitionsPresent()){
				logger.debug(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:numberOfRepetitions:" + inBandDataType.numberOfRepetitions);	
				inBand.setNumberOfRepetitions(inBandDataType.numberOfRepetitions);
			}
			MessageID msgId = new MessageID();
			if(inBandDataType.msgId == SasCapMsgIdEnum.ELEMENTRY_MSG_ID){
				logger.info(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:ELEMENTRY_MSG_ID");	
				if(! cpb.isElementryMsgIdPresent()){
					logger.error(Util.toString(cpb.dlgId) + "::: encodePC:ElementryMsgId parameter of CPB is null.");
					throw new InvalidInputException("ElementryMsgId parameter of cpb is null");
				}
				logger.debug(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:cpb.elementryMsgId:" + cpb.elementryMsgId);
				msgId.selectElementaryMessageID(new Integer4(cpb.elementryMsgId));
				inBand.setMessageID(msgId);
			}else if(inBandDataType.msgId == SasCapMsgIdEnum.ELEMENTRY_MSG_ID_LIST){
				logger.info(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:ELEMENTRY_MSG_ID_LIST");	
				if(! cpb.isElementryMsgIdListPresent() || cpb.elementryMsgIdList.isEmpty()){
					logger.error(Util.toString(cpb.dlgId) + "::: encodePC:ElementryMsgIdList parameter of CPB is null.");
					throw new InvalidInputException("ElementryMsgIdList parameter of cpb is null");
				}
				ArrayList<Integer4> list = new ArrayList<Integer4>();
				for(int k= 0; k < cpb.elementryMsgIdList.size(); k++){
					list.add(new Integer4(cpb.elementryMsgIdList.get(k)));
				}
				logger.debug(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:cpb.elementryMsgId list:" + list);
				msgId.selectElementaryMessageIDs(list);
				inBand.setMessageID(msgId);
			}else if(inBandDataType.msgId == SasCapMsgIdEnum.VARIABLEMSG){
				logger.info(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:VARIABLEMSG");	
				if(! cpb.isVariableMsgPresent()){
					logger.error(Util.toString(cpb.dlgId) + "::: encodePC:VariableMsg parameter of CPB is null.");
					throw new InvalidInputException("VariableMsg parameter of cpb is null");
				}
				VariableMessageSequenceType varMsg = new VariableMessageSequenceType();
				SasCapVariableMsgDataType varMsgDataType = cpb.variableMsg ;
				logger.debug(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:varMsgDataType.elementryMsgId:" + varMsgDataType.elementryMsgId);
				varMsg.setElementaryMessageID(new Integer4(varMsgDataType.elementryMsgId));

				if(varMsgDataType.variablePartList == null || varMsgDataType.variablePartList.isEmpty()){
					logger.error(Util.toString(cpb.dlgId) + "::: encodePC:variablePartList parameter of VARIABLEMSG is null.");
					throw new InvalidInputException("variablePartList parameter of VARIABLEMSG is null");
				}
				ArrayList<VariablePart> varPartList = new ArrayList<VariablePart>();
				for(int k=0 ; k < varMsgDataType.variablePartList.size() ; k++){
					SasCapVariablePartDataType varPartDataType = varMsgDataType.variablePartList.get(k);
					VariablePart varPart = new VariablePart();
					if(varPartDataType.isDatePresent()){
						logger.debug(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:isDatePresent:" + varPartDataType.date);
						byte[] data = AdrsSignalDataType.encodeAdrsSignal(varPartDataType.date);				
						varPart.selectDate(data);
						varPartList.add(varPart);
					}else if(varPartDataType.isNumberPresent()){
						if(varPartDataType.number.digits == null){
							logger.error(Util.toString(cpb.dlgId) + "::: encodePC:digits parameter of number of varPartDataType is null.");
							throw new InvalidInputException("digits parameter of number of varPartDataType is null");
						}
						logger.debug(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:isNumberPresent:digits:"+varPartDataType.number.digits);
						int encodingScheme = 0 ;
						if(varPartDataType.number.digits.length() %2 != 0){
							encodingScheme = 1 ;
						}
						varPartDataType.number.encodingSchemeEnum = EncodingSchemeEnum.fromInt(encodingScheme);
						logger.debug(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:isNumberPresent:encodingSchemeEnum:"+varPartDataType.number.encodingSchemeEnum);
						
						byte[] data = GenericDigitsDataType.encodeGenericDigits(varPartDataType.number.encodingSchemeEnum, varPartDataType.number.digits);
						Digits dig = new Digits(data);
						varPart.selectNumber(dig);
						varPartList.add(varPart);
					}else if(varPartDataType.isPricePresent()){
						logger.debug(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:isPricePresent:" + varPartDataType.price);
						byte[] data = AdrsSignalDataType.encodeAdrsSignal(varPartDataType.price);				
						logger.debug(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:isPricePresent..length of encoding price:" + data.length);
						varPart.selectPrice(data);
						varPartList.add(varPart);
					}else if(varPartDataType.isTimePresent()){
						logger.debug(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:isTimePresent:" + varPartDataType.time);
						byte[] data = AdrsSignalDataType.encodeAdrsSignal(varPartDataType.time);				
						varPart.selectTime(data);
						varPartList.add(varPart);
					}else if(varPartDataType.isValuePresent()){
						logger.debug(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:isValuePresent:" + varPartDataType.value);
						varPart.selectInteger(new Integer4(varPartDataType.value));
						varPartList.add(varPart);
					}
				}
				logger.info(Util.toString(cpb.dlgId) + "::: setInfotoSendParam: setVariableParts");	
				varMsg.setVariableParts(varPartList);
				msgId.selectVariableMessage(varMsg);
				inBand.setMessageID(msgId);
				logger.info(Util.toString(cpb.dlgId) + "::: setInfotoSendParam: setMessageID");	
			}
			infoSend.selectInbandInfo(inBand);
		}else if(cpb.informationtoSendEnum == SasCapInformationtoSendEnum.TONE){
			logger.info(Util.toString(cpb.dlgId) + "::: setInfotoSendParam: informationtoSendEnum is Tone");	
			if(cpb.tone == null) {
				logger.error(Util.toString(cpb.dlgId) + "::: encodePC:tone parameter of cpb is null.");
				throw new InvalidInputException("tone parameter of cpb is null");
			}
			infoSend.selectTone(cpb.tone);
			logger.info(Util.toString(cpb.dlgId) + "::: setInfotoSendParam: informationtoSendEnum is Tone and tone duration is:"+ cpb.tone.getDuration());
			logger.info(Util.toString(cpb.dlgId) + "::: setInfotoSendParam: informationtoSendEnum is Tone and tone Id is:"+ cpb.tone.getToneID());
		}

		logger.info(Util.toString(cpb.dlgId) + "::: setInfotoSendParam:Exit");	
		return infoSend ;
	}

	/**
	 * This function will encode ConnectToResource Arg.
	 * @param source
	 * @param cpb
	 * @param msgs
	 * @param invokeId
	 * @throws Exception
	 */
	public static void encodeCTR(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: encodeCTR:Enter");	
		ConnectToResourceArg ctrArg = new ConnectToResourceArg();
		ResourceAddressChoiceType rsChoiceType = new ResourceAddressChoiceType();
		if(!cpb.isIpRoutingAdrsPresent()){
			//logger.error(Util.toString(cpb.dlgId) + "::: encodeCTR:IpRoutingAdrs parameter of cpb is null.");
			//throw new InvalidInputException("IpRoutingAdrs parameter of cpb is null");
			logger.info(Util.toString(cpb.dlgId) + "::: encodeCTR:selecting resourceAdrs none");	
			rsChoiceType.selectNone();
		}else {
			CalledPartyNum cldNum = cpb.ipRoutingAdrs ;
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeCTR:ipRoutingAdrs: "+ cldNum);
			byte[] data = CalledPartyNum.encodeCaldParty(cldNum.getAddrSignal(), cldNum.getNatureOfAdrs(), cldNum.getNumPlan(), cldNum.getIntNtwrkNum());

			IPRoutingAddress ipAddress = new IPRoutingAddress();
			ipAddress.setValue(new CalledPartyNumber(data));
			rsChoiceType.selectIpRoutingAddress(ipAddress);
		}

		ctrArg.setResourceAddress(rsChoiceType);

		if(cpb.isbothwayThroughConnectionIndPresent()){
			logger.info(Util.toString(cpb.dlgId) + "::: encodeCTR:bothwayThroughConnectionInd present");
			ServiceInteractionIndicatorsTwo srvIndicatorsTwo = new ServiceInteractionIndicatorsTwo();
			srvIndicatorsTwo.setBothwayThroughConnectionInd(cpb.bothwayThroughConnectionInd);
			ctrArg.setServiceInteractionIndicatorsTwo(srvIndicatorsTwo);
		}
		if(cpb.isCalSegmentIDForCTRPresent()){
			logger.info(Util.toString(cpb.dlgId) + "::: encodeCTR:CalSegmentIDForCTR present:" + cpb.calSegmentIDForCTR);
			ctrArg.setCallSegmentID(cpb.calSegmentIDForCTR);
		}

		IEncoder<ConnectToResourceArg> encoder;
		try {
			encoder = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			logger.info(Util.toString(cpb.dlgId) + "::: encodeCTR:encoding the ConnectToResourceArg");
			encoder.encode(ctrArg, outputStream);
			logger.info(Util.toString(cpb.dlgId) + "::: encodeCTR:successfully encoded the ConnectToResourceArg");
			byte[] encodedData = outputStream.toByteArray();			
			int len = encodedData.length ;

			if(logger.isDebugEnabled()){
				logger.debug(Util.toString(cpb.dlgId) + "::: encodeCTR:Encoded ConnectToResourceArg: " + Util.formatBytes(encodedData));
				logger.debug(Util.toString(cpb.dlgId) + "::: encodeCTR:length of encoded ConnectToResourceArg: " + len);
			}

			byte[] ctrOpCode =  { CAPOpcode.CONNECT_TO_RESOURCE };
			Operation pcOp = new Operation(Operation.OPERATIONTYPE_LOCAL, ctrOpCode);
			InvokeReqEvent ire = new InvokeReqEvent(source, cpb.dlgId, pcOp);
			ire.setInvokeId(invokeId);
			ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedData));
			ire.setClassType(CAPOpcode.CONNECT_TO_RESOURCE_CLASS);
			List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
			list.add(ire);
			msgs.setCompReqEvents(list);
		}catch(Exception e){
			logger.error(Util.toString(cpb.dlgId) + "::: encodeCTR:Exception:" , e);
			throw e ;
		}
		logger.info(Util.toString(cpb.dlgId) + "::: encodeCTR:Exit");	
	}

	/**
	 * This function will encode the play announcement argument.
	 * @param source
	 * @param cpb
	 * @param msgs
	 * @param invokeId
	 * @throws Exception
	 */
	public static void encodePA(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: encodePA:Enter");
		PlayAnnouncementArg paArg = new PlayAnnouncementArg();

		//setting the default value
		paArg.setDisconnectFromIPForbidden(cpb.disconnectFromIPForbidden);
		logger.info(Util.toString(cpb.dlgId) + "::: encodePA:getDisconnectFromIPForbidden:" + paArg.getDisconnectFromIPForbidden());
		//setting the default value
		if(cpb.isRequestAnnouncementCompletePresent()){
			paArg.setRequestAnnouncementComplete(cpb.requestAnnouncementComplete);
			logger.info(Util.toString(cpb.dlgId) + "::: encodePA:getRequestAnnouncementComplete:" + paArg.getRequestAnnouncementComplete());
		}
		if(cpb.isRequestAnnouncementStartedPresent()){
			logger.info(Util.toString(cpb.dlgId) + "::: encodePA:cpb.requestAnnouncementStarted:" + cpb.requestAnnouncementStarted);
			paArg.setRequestAnnouncementStartedNotification(cpb.requestAnnouncementStarted);
		}


		if(! cpb.isInformationtoSendEnumPresent()){
			logger.error(Util.toString(cpb.dlgId) + "::: encodePA:InformationtoSendEnum parameter of CPB is null.It is mandatory parameter for PA");
			throw new InvalidInputException("InformationtoSendEnum parameter of CPB is null");			
		}

		logger.info(Util.toString(cpb.dlgId) + "::: encodePA:Calling setInfotoSendParam");		
		paArg.setInformationToSend(setInfotoSendParam(cpb));
		if(cpb.isCallSegmentIDPresent()){
			paArg.setCallSegmentID(cpb.callSegmentID);
		}

		IEncoder<PlayAnnouncementArg> encoder;
		try {
			encoder = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			logger.info(Util.toString(cpb.dlgId) + "::: encodePA:encoding the PromptAndCollectUserInformationArg");
			encoder.encode(paArg, outputStream);
			logger.info(Util.toString(cpb.dlgId) + "::: encodePA:successfully encoded the PromptAndCollectUserInformationArg");
			byte[] encodedData = outputStream.toByteArray();
			int len = encodedData.length ;

			if(logger.isDebugEnabled()){
				logger.debug(Util.toString(cpb.dlgId) + "::: encodePA:Encoded PlayAnnouncementArg: " + Util.formatBytes(encodedData));
				logger.debug(Util.toString(cpb.dlgId) + "::: encodePA:length of encoded PlayAnnouncementArg: " + len);
			}

			byte[] paOpCode =  { CAPOpcode.PLAY_ANNOUNCEMENT };
			Operation paOp = new Operation(Operation.OPERATIONTYPE_LOCAL, paOpCode);
			InvokeReqEvent ire = new InvokeReqEvent(source, cpb.dlgId, paOp);
			ire.setInvokeId(invokeId);
			//ire.setLastInvokeEvent(true);
			ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedData));
			ire.setClassType(CAPOpcode.PLAY_ANNOUNCEMENT_CLASS);
			List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
			list.add(ire);
			msgs.setCompReqEvents(list);
		}catch(Exception e){
			logger.error(Util.toString(cpb.dlgId) + "::: encodePA:Exception:" , e);
			throw e ;
		}
		logger.info(Util.toString(cpb.dlgId) + "::: encodePA:Exit");	
	}
}
