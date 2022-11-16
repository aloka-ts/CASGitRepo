package com.camel.CAPMsg;

import jain.protocol.ss7.tcap.ComponentReqEvent;
import jain.protocol.ss7.tcap.component.InvokeReqEvent;
import jain.protocol.ss7.tcap.component.Operation;
import jain.protocol.ss7.tcap.component.Parameters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.log4j.Logger;
import org.bn.CoderFactory;
import org.bn.IDecoder;
import org.bn.IEncoder;

import asnGenerated.AChBillingChargingCharacteristics;
import asnGenerated.ApplyChargingArg;
import asnGenerated.ApplyChargingReportArg;
import asnGenerated.CAMEL_AChBillingChargingCharacteristics;
import asnGenerated.CAMEL_CallResult;
import asnGenerated.CAMEL_FCIBillingChargingCharacteristics;
import asnGenerated.FCIBillingChargingCharacteristics;
import asnGenerated.FurnishChargingInformationArg;
import asnGenerated.LegType;
import asnGenerated.SendingSideID;
import asnGenerated.TimeIfTariffSwitch;
import asnGenerated.TimeInformation;
import asnGenerated.CAMEL_AChBillingChargingCharacteristics.TimeDurationChargingSequenceType;
import asnGenerated.CAMEL_CallResult.TimeDurationChargingResultSequenceType;
import asnGenerated.CAMEL_FCIBillingChargingCharacteristics.FCIBCCCAMELsequence1SequenceType;

import com.camel.exceptions.InvalidInputException;
import com.camel.util.Util;

/**
 * This class have methods to set SasCapCallProcessBuffer
 * using the parameters coming from network for ACR and methods to encode 
 * CAP ACH operation.
 * @author nkumar
 *
 */
public class SasCapACR {

	//Instance of logger
	private static Logger logger = Logger.getLogger(SasCapACR.class);
	
	/**
	 * This function will process the Apply Charging Report and decode the params..
	 * @param dilgId
	 * @param cpb
	 * @param parms
	 * @param decoder
	 * @throws Exception
	 */
	public static void processACR(int dilgId, SasCapCallProcessBuffer cpb, byte[] parms, IDecoder decoder) throws Exception{
		logger.info(Util.toString(dilgId) + "::: processACR:Enter");
		InputStream ins = new ByteArrayInputStream(parms);
		ApplyChargingReportArg acrArg ;
		try {
			logger.info(Util.toString(dilgId) + "::: processACR:before totalTimeDuration:" + cpb.totalTimeDuration);
			//cpb.totalTimeDuration = cpb.totalTimeDuration - cpb.maxCallPeriodDuration ;
			logger.info(Util.toString(dilgId) + "::: processACR:after totalTimeDuration:" + cpb.totalTimeDuration);
			logger.info(Util.toString(dilgId) + "::: processACR:decoding the ApplyChargingReportArg");
			acrArg = decoder.decode(ins, ApplyChargingReportArg.class);
			InputStream ins1 = new ByteArrayInputStream(acrArg.getValue().getValue());
			logger.info(Util.toString(dilgId) + "::: processACR:decoding the CAMEL_CallResult");
			cpb.camelCallResult= decoder.decode(ins1, CAMEL_CallResult.class);
			TimeDurationChargingResultSequenceType time = cpb.camelCallResult.getTimeDurationChargingResult();
			if(time != null)
				logger.debug(Util.toString(dilgId) + "::: processACR:State of Call Active:"+ time.getCallActive());
			TimeInformation timeInfo = time.getTimeInformation() ;
			if(timeInfo.isTimeIfNoTariffSwitchSelected()){
				logger.debug(Util.toString(dilgId) + "::: processACR:isTimeIfNoTariffSwitchSelected value:"+ timeInfo.getTimeIfNoTariffSwitch().getValue());
			}
			if(timeInfo.isTimeIfTariffSwitchSelected()){
				TimeIfTariffSwitch timeIfTariff = timeInfo.getTimeIfTariffSwitch() ;
				if(timeIfTariff.isTariffSwitchIntervalPresent())
					logger.debug(Util.toString(dilgId) + "::: processACR:isTariffSwitchIntervalPresent value:"+ timeIfTariff.getTariffSwitchInterval());
				logger.debug(Util.toString(dilgId) + "::: processACR:getTimeSinceTariffSwitch value:"+ timeIfTariff.getTimeSinceTariffSwitch());
			}
			
		} catch (Exception e) {
			logger.error(Util.toString(dilgId) + "::: processACR:" , e);
			throw e ;
		}
		logger.info(Util.toString(dilgId) + "::: processACR:Exit");		
	}
	
	/**
	 * This function will encode the Apply Charging Arg.
	 * @param source
	 * @param cpb
	 * @param msgs
	 * @throws Exception
	 */
	public static void encodeAC(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: encodeAC:Enter");
		AChBillingChargingCharacteristics aChBillingCharging = new AChBillingChargingCharacteristics();
		CAMEL_AChBillingChargingCharacteristics camelAcCharging = new CAMEL_AChBillingChargingCharacteristics();
		TimeDurationChargingSequenceType timeDuration = new TimeDurationChargingSequenceType();
		if(!cpb.ismaxCallPeriodDurationPresent()){
			throw new InvalidInputException("encodeAC:maxCallPeriodDuration is null");
		}
		logger.debug(Util.toString(cpb.dlgId) + "::: encodeAC:maxCallPeriodDuration:"+ cpb.maxCallPeriodDuration);
		timeDuration.setMaxCallPeriodDuration(cpb.maxCallPeriodDuration);
		if(cpb.isTariifSwitchIntervalPresent()){
		logger.debug(Util.toString(cpb.dlgId) + "::: encodeAC:tariifSwitchInterval:"+ cpb.tariifSwitchInterval);
		timeDuration.setTariffSwitchInterval(cpb.tariifSwitchInterval);
		}
		if(cpb.isReleaseIfDurationExcedPresent()){
		logger.debug(Util.toString(cpb.dlgId) + "::: encodeAC:releaseIfDurationExced:"+ cpb.releaseIfDurationExced);
		timeDuration.setReleaseIfdurationExceeded(cpb.releaseIfDurationExced);
		}
		if(cpb.isAudibleIndicatorPresent()){
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeAC:isAudibleIndicatorPresent:"+ cpb.audibleIndicator);
			timeDuration.setTone(cpb.audibleIndicator);
		}
		camelAcCharging.selectTimeDurationCharging(timeDuration);
		IEncoder<CAMEL_AChBillingChargingCharacteristics> encoder;
		
		try {
			encoder = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			logger.info(Util.toString(cpb.dlgId) + "::: encodeAC:Encoding the CAMEL_AChBillingChargingCharacteristics");
			encoder.encode(camelAcCharging, outputStream);
			byte[] data = outputStream.toByteArray();
			aChBillingCharging.setValue(data);
			
			ApplyChargingArg ac = new ApplyChargingArg();
			ac.setAChBillingChargingCharacteristics(aChBillingCharging);
			byte[] legType ;
			if(cpb.partyToCharge.equalsIgnoreCase("01")){
				legType = new byte[]{0x01};
			}else {
				legType = new byte[]{0x02};
			}
			if(logger.isDebugEnabled())
				logger.debug(Util.toString(cpb.dlgId) + "::: encodeAC:partyToCharge:"+ Util.formatBytes(legType));
			SendingSideID sending = new SendingSideID();
			sending.selectSendingSideID(new LegType(legType));
			ac.setPartyToCharge(sending);
			IEncoder<ApplyChargingArg> encoder1 = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
			logger.info(Util.toString(cpb.dlgId) + "::: encodeAC:Encoding the ApplyChargingArg");
			encoder1.encode(ac, outputStream1);
			byte[] encodededData = outputStream.toByteArray();
			int len = encodededData.length ;
			byte[] newEncodedData = new byte[len + 4];
			newEncodedData[0] = 0x30 ;
			byte[] b = Util.asciToHex(Integer.toHexString(len + 2 ));
			newEncodedData[1] = b[0];
			newEncodedData[2] = (byte)0x80 ;
			byte[] b1 = Util.asciToHex(Integer.toHexString(len));
			newEncodedData[3] = b1[0] ;
			
			for(int k=0 ; k < encodededData.length ; k++){
				newEncodedData[k+4] = encodededData[k];
			}
			
			if(logger.isDebugEnabled()){
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeAC:Encoded ApplyChargingArg: " + Util.formatBytes(encodededData));
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeAC:length of encoded ApplyChargingArg: " + encodededData.length);
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeAC:New Encoded ApplyChargingArg: " + Util.formatBytes(newEncodedData));
			logger.debug(Util.toString(cpb.dlgId) + "::: encodeAC:new length of encoded ApplyChargingArg: " + newEncodedData.length);
			}
			
			byte[] applyChargingOpCode =  { CAPOpcode.APPLY_CHARGING };
			Operation acOp = new Operation(Operation.OPERATIONTYPE_LOCAL, applyChargingOpCode);
			InvokeReqEvent ire = new InvokeReqEvent(source, cpb.dlgId, acOp);
			//TODO- without removing two octets
			ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, newEncodedData));
			ire.setInvokeId(invokeId);
			ire.setClassType(CAPOpcode.APLLY_CHARGING_CLASS);
			List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
			list.add(ire);
			msgs.setCompReqEvents(list);
			logger.info(Util.toString(cpb.dlgId) + "::: encodeAC:Exit");
		} catch(Exception e){
			logger.error(Util.toString(cpb.dlgId) + "::: encodeAC:Exception:" , e);
			throw e ;
		}
			
	}
	
	/**
	 * This function will encode the Furnish Charging Information Arg.
	 * @param source
	 * @param cpb
	 * @param msgs
	 * @throws Exception
	 */
	public static void encodeFCI(Object source, SasCapCallProcessBuffer cpb, SasCapMsgsToSend msgs, int invokeId) throws Exception{
		logger.info(Util.toString(cpb.dlgId) + "::: encodeFCI:Enter");
		//TODO encoding of freeformatdata
		if(cpb.partyToCharge == null || cpb.freeFormatData == null){
			throw new InvalidInputException("Either partyToCharge or freeFormatData of buffer is null");
		}
		FurnishChargingInformationArg fciArg = new FurnishChargingInformationArg();
		FCIBillingChargingCharacteristics fciChargingChar = new FCIBillingChargingCharacteristics();
		CAMEL_FCIBillingChargingCharacteristics camelFciChargingChar = new CAMEL_FCIBillingChargingCharacteristics();
		byte[] legType ;
		logger.debug(Util.toString(cpb.dlgId) + "::: encodeFCI:partyToCharge:" + cpb.partyToCharge);
		if(cpb.partyToCharge.equalsIgnoreCase("01")){
			legType = new byte[]{0x01};
		}else {
			legType = new byte[]{0x02};
		}		
		SendingSideID sending = new SendingSideID();
		sending.selectSendingSideID(new LegType(legType));
		FCIBCCCAMELsequence1SequenceType fciBccCamelSeq = new FCIBCCCAMELsequence1SequenceType();
		fciBccCamelSeq.setPartyToCharge(sending);
		fciBccCamelSeq.setFreeFormatData(cpb.freeFormatData);
		camelFciChargingChar.selectFCIBCCCAMELsequence1(fciBccCamelSeq);
		
		IEncoder<CAMEL_FCIBillingChargingCharacteristics> encoder;
		
		try {
			encoder = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			logger.info(Util.toString(cpb.dlgId) + "::: encodeFCI:Encoding the CAMEL_FCIBillingChargingCharacteristics");
			encoder.encode(camelFciChargingChar, outputStream);
			logger.info(Util.toString(cpb.dlgId) + "::: encodeFCI:successfully encoded the CAMEL_FCIBillingChargingCharacteristics");
			byte[] data = outputStream.toByteArray();
			
			fciChargingChar.setValue(data);
			fciArg.setValue(fciChargingChar);
			
			IEncoder<FurnishChargingInformationArg> encoder1;
			encoder1 = CoderFactory.getInstance().newEncoder("BER");
			ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
			logger.info(Util.toString(cpb.dlgId) + "::: encodeFCI:Encoding the FurnishChargingInformationArg");
			encoder1.encode(fciArg, outputStream1);
			logger.info(Util.toString(cpb.dlgId) + "::: encodeFCI:successfully encoded the FurnishChargingInformationArg");
			byte[] encodedData = outputStream.toByteArray();
			
			if(logger.isDebugEnabled()){
				logger.debug(Util.toString(cpb.dlgId) + "::: encodeAC:Encoded ApplyChargingArg: " + Util.formatBytes(encodedData));
				logger.debug(Util.toString(cpb.dlgId) + "::: encodeAC:length of encoded ApplyChargingArg: " + encodedData.length);
				}
				
				byte[] furnishChargingOpCode =  { CAPOpcode.FURNISH_CHARGING_INFORMATION };
				Operation fciOp = new Operation(Operation.OPERATIONTYPE_LOCAL, furnishChargingOpCode);
				InvokeReqEvent ire = new InvokeReqEvent(source, cpb.dlgId, fciOp);
				ire.setParameters(new Parameters(Parameters.PARAMETERTYPE_SEQUENCE, encodedData));
				ire.setInvokeId(invokeId);
				ire.setClassType(CAPOpcode.CLASS_TWO);
				List<ComponentReqEvent> list = msgs.getCompReqEvents() ;
				list.add(ire);
				msgs.setCompReqEvents(list);
		} catch(Exception e){
			throw e ;
		}
		logger.info(Util.toString(cpb.dlgId) + "::: encodeFCI:Exit");
	}
}
