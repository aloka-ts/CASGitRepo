package com.camel.CAPMsg;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.bn.IDecoder;

import asnGenerated.Ext_BasicServiceCode;
import asnGenerated.InitialDPArg;
import asnGenerated.LocationInformation;
import asnGenerated.TimeAndTimezone;
import asnGenerated.EventTypeBCSM.EnumType;

import com.camel.dataTypes.AdrsSignalDataType;
import com.camel.dataTypes.AdrsStringDataType;
import com.camel.dataTypes.BearerCapabilityDataType;
import com.camel.dataTypes.CalledPartyNum;
import com.camel.dataTypes.CallingPartyNum;
import com.camel.dataTypes.CellIdFixedLenDataType;
import com.camel.dataTypes.IPSSCapabilitiesDataType;
import com.camel.dataTypes.ImsiDataType;
import com.camel.dataTypes.LAIFixedLenDataType;
import com.camel.dataTypes.LocationNum;
import com.camel.dataTypes.TimeAndTimeZoneDataType;
import com.camel.enumData.CalgPartyCatgEnum;
import com.camel.enumData.ExtBearerServiceCodeEnum;
import com.camel.enumData.ExtTeleServiceCodeEnum;
import com.camel.enumData.NatureOfAdrsEnum;
import com.camel.enumData.NatureOfAdrsStringEnum;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.NonAsnArg;
import com.camel.util.Util;


/**
 * This class have methods to set SasCapCallProcessBuffer
 * using the parameters coming from the network for InitialDp.
 * @author nkumar
 *
 */
public class SasCapIdp {

	//Instance of logger
	private static Logger logger = Logger.getLogger(SasCapIdp.class);	 
	/**
	 *  This function will decode Idp and set them in SasCapCallProcessBuffer.
	 * @param dilgId dialogue identifier
	 * @param parms byte[] received from the network
	 * @param cpb object of SasCapCallProcessBuffer
	 * @param decoder IDecoder
	 * @throws InvalidInputException,Exception 
	 */
	public static void processIdp(int dilgId, SasCapCallProcessBuffer cpb, byte[] parms, IDecoder decoder) throws InvalidInputException,Exception{
		logger.info(Util.toString(dilgId) + "::: processIdp:Enter");
		
		/*byte [] newparms  = new byte[parms.length-2];
		for(int k= 0 ; k < parms.length -2 ; k++){
			newparms[k] = parms[k];
		}
		newparms[1] -=2 ;
		
		logger.debug("byte array after modification:" + Util.formatBytes(newparms));*/
		InputStream ins = new ByteArrayInputStream(parms);
		InitialDPArg idp;
		try {
			logger.info(Util.toString(dilgId) + "::: processIdp:decoding the IDP");
			logger.debug(Util.toString(dilgId) + "::: processIdp:decoder" + decoder);
			idp = decoder.decode(ins, InitialDPArg.class);
			logger.info(Util.toString(dilgId) + "::: processIdp:Successfully decoded the IDP");
		} catch (Exception e) {
			logger.error(Util.toString(dilgId) + "::: processIdp:" , e);
			throw e ;
		}
		cpb.serviceKey = idp.getServiceKey().getValue().getValue();
		logger.debug(Util.toString(dilgId) + "::: processIdp: Value of service key in the IDP: "+ cpb.serviceKey);
		if(idp.isCalledPartyNumberPresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp:decoding the CalledPartyNum from the Idp");
			cpb.calledPartyNum = CalledPartyNum.decodeCaldParty(idp.getCalledPartyNumber().getValue()) ;
			if(cpb.calledPartyNum.getNatureOfAdrs() == NatureOfAdrsEnum.NATIONAL_NO){
				cpb.cldNumRTCH = "0".concat(cpb.calledPartyNum.getAddrSignal());
			}else if(cpb.calledPartyNum.getNatureOfAdrs() == NatureOfAdrsEnum.INTER_NO){
				cpb.cldNumRTCH = "00".concat(cpb.calledPartyNum.getAddrSignal());
			}else {
				cpb.cldNumRTCH = cpb.calledPartyNum.getAddrSignal();
			}
			logger.debug(Util.toString(dilgId) + "::: processIdp:nature of Adrs calledPartyNum" + cpb.calledPartyNum.getNatureOfAdrs());
			logger.debug(Util.toString(dilgId) + "::: processIdp:translated cldNum" + cpb.cldNumRTCH);
			logger.info(Util.toString(dilgId) + "::: processIdp:successfully decoded the CalledPartyNum from the Idp");
		}
		if(idp.isCallingPartyNumberPresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp:decoding the CallingPartyNum from the Idp");
			cpb.callingPartyNum = CallingPartyNum.decodeCalgParty(idp.getCallingPartyNumber().getValue());
			
			if(cpb.callingPartyNum.getNatureOfAdrs() == NatureOfAdrsEnum.NATIONAL_NO){
				cpb.clgNumRTCH = "0".concat(cpb.callingPartyNum.getAddrSignal());
			}else if(cpb.callingPartyNum.getNatureOfAdrs() == NatureOfAdrsEnum.INTER_NO){
				cpb.clgNumRTCH = "00".concat(cpb.callingPartyNum.getAddrSignal());
			}else {
				cpb.clgNumRTCH = cpb.callingPartyNum.getAddrSignal();
			}
			logger.debug(Util.toString(dilgId) + "::: processIdp:nature of Adrs callingPartyNum" + cpb.callingPartyNum.getNatureOfAdrs());
			logger.debug(Util.toString(dilgId) + "::: processIdp:translated callingPartyNum" + cpb.clgNumRTCH);
			logger.info(Util.toString(dilgId) + "::: processIdp:successfully decoded the CallingPartyNum from the Idp");
		}
		if(idp.isIMSIPresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp:decoding the IMSI from the Idp");
			cpb.imsi = ImsiDataType.decodeImsi(idp.getIMSI().getValue().getValue());
			cpb.imsiForCDR = cpb.imsi.getMobileCountryCode() + "," + cpb.imsi.getMobileNetworkCode()+ "," + cpb.imsi.getLocationAreaCode();
			logger.info(Util.toString(dilgId) + "::: processIdp:successfully decoded the IMSI from the Idp");
		}
		if(idp.isLocationNumberPresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp: LocationNumber");
			cpb.locationNum = LocationNum.decodeLocationNum(idp.getLocationNumber().getValue());
			logger.info(Util.toString(dilgId) + "::: processIdp:successfully decoded the LocationNum from the Idp");
		}
		if(idp.isIPSSPCapabilitiesPresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp: IPSSPCapabilitiesPresent");
			cpb.ipSSPCapabilitiesDataType = IPSSCapabilitiesDataType.decodeIpSS(idp.getIPSSPCapabilities().getValue());
			logger.info(Util.toString(dilgId) + "::: processIdp:successfully decoded the IPSSPCapabilities from the Idp");
		}
		if(idp.isLocationInformationPresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp: LocationInformationPresent");
			LocationInformation locInfo = idp.getLocationInformation();
			if(locInfo.isAgeOfLocationInformationPresent()){
				cpb.ageOfLocationInfo = locInfo.getAgeOfLocationInformation().getValue();
				logger.debug(Util.toString(dilgId) + "::: processIdp:ageOfLocationInfo: "+ cpb.ageOfLocationInfo);
			}
			if(locInfo.isLocationNumberPresent()){
				logger.info(Util.toString(dilgId) + "::: processIdp:decoding the LocationNum from the Idp");
				cpb.locationNum = LocationNum.decodeLocationNum(locInfo.getLocationNumber().getValue());
				logger.info(Util.toString(dilgId) + "::: processIdp:successfully decoded the LocationNum from the Idp");
			}
			if(locInfo.isCellGlobalIdOrServiceAreaIdOrLAIPresent()){
				if(locInfo.getCellGlobalIdOrServiceAreaIdOrLAI().isCellGlobalIdOrServiceAreaIdFixedLengthSelected()){
					logger.info(Util.toString(dilgId) + "::: processIdp:decoding the CellGlobalIdOrServiceAreaIdFixedLength from the Idp");
					cpb.cellIdFixedLenDataType = CellIdFixedLenDataType.decodeCellId(locInfo.getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getValue());
					logger.info(Util.toString(dilgId) + "::: processIdp:successfully decoded the CellGlobalIdOrServiceAreaIdFixedLength from the Idp and value is:"+cpb.cellIdFixedLenDataType);
					if(cpb.imsi != null){
						if(cpb.imsi.getMobileCountryCode().equalsIgnoreCase(cpb.cellIdFixedLenDataType.getMobileCountryCode())
								&& cpb.imsi.getMobileNetworkCode().equalsIgnoreCase(cpb.cellIdFixedLenDataType.getMobileNetworkCode())){
							logger.info(Util.toString(dilgId) + "::: processIdp:Subscriber is in home network");
							cpb.roamingVal = 0 ;
						}else{
							logger.info(Util.toString(dilgId) + "::: processIdp:Subscriber is roamed");
							cpb.roamingVal = 1 ;
						}
					}
				}
				else if(locInfo.getCellGlobalIdOrServiceAreaIdOrLAI().isLaiFixedLengthSelected()){
					logger.info(Util.toString(dilgId) + "::: processIdp:decoding the LAIFixedLenDataType from the Idp");
					cpb.laiFixedLenDataType = LAIFixedLenDataType.decodeLAI(locInfo.getCellGlobalIdOrServiceAreaIdOrLAI().getLaiFixedLength().getValue());
					logger.info(Util.toString(dilgId) + "::: processIdp:successfully decoding the LAIFixedLenDataType from the Idp and value is:"+cpb.laiFixedLenDataType);
					if(cpb.imsi != null){
						if(cpb.imsi.getMobileCountryCode().equalsIgnoreCase(cpb.laiFixedLenDataType.getMobileCountryCode())
								&& cpb.imsi.getMobileNetworkCode().equalsIgnoreCase(cpb.laiFixedLenDataType.getMobileNetworkCode())){
							logger.info(Util.toString(dilgId) + "::: processIdp:Subscriber is in home network");
							cpb.roamingVal = 0 ;
						}else{
							logger.info(Util.toString(dilgId) + "::: processIdp:Subscriber is roamed");
							cpb.roamingVal = 1 ;
						}
					}
				}
			}
			if(locInfo.isVlr_numberPresent()){
				logger.info(Util.toString(dilgId) + "::: processIdp:decoding the Vlr_number from the Idp");
				cpb.vlrNumber = AdrsStringDataType.decodeAdrsString(locInfo.getVlr_number().getValue().getValue());
				logger.info(Util.toString(dilgId) + "::: processIdp:successfully decoded the Vlr_number from the Idp");
			}
			//Change for CDR
			if(cpb.ageOfLocationInfo != null){
				cpb.locationInfoForCDR = cpb.ageOfLocationInfo + "," ;
			}else{
				cpb.locationInfoForCDR = "," ;
			}
			
			if(cpb.cellIdFixedLenDataType != null) {
				CellIdFixedLenDataType cellId = cpb.cellIdFixedLenDataType ;
				cpb.locationInfoForCDR += cellId.getMobileCountryCode() + "," + cellId.getMobileNetworkCode()+ "," + cellId.getLocationAreaCode()+"," + cellId.getCellIdentity()+ ",";
			}else if(cpb.laiFixedLenDataType != null){
				LAIFixedLenDataType lai = cpb.laiFixedLenDataType ;
				cpb.locationInfoForCDR += lai.getMobileCountryCode() + "," + lai.getMobileNetworkCode()+ "," + lai.getLocationAreaCode()+", ," ;
			}else{
				cpb.locationInfoForCDR += ", , , ," ;
			}
			
			if(cpb.vlrNumber != null){
				cpb.locationInfoForCDR += cpb.vlrNumber.getAdrs() + ",";
			}else{
				cpb.locationInfoForCDR += "," ;
			}
			
			if(cpb.locationNum != null){
				cpb.locationInfoForCDR += cpb.locationNum.getAddrSignal();
			}
		}
		if(idp.isCallingPartysCategoryPresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp: CallingPartysCategoryPresent");
			 cpb.calgPartyCatg = NonAsnArg.decodeCalgPartyCatg(idp.getCallingPartysCategory().getValue());
		}
		if(idp.isMscAddressPresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp: MscAddressPresent");
			cpb.mscAdrs = AdrsStringDataType.decodeAdrsString(idp.getMscAddress().getValue().getValue());
		}
		if(idp.isEventTypeBCSMPresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp: EventTypeBCSMPresent");
			cpb.eventTypeBCSM_Idp = idp.getEventTypeBCSM();
			if(cpb.eventTypeBCSM_Idp.getValue() == EnumType.collectedInfo || cpb.eventTypeBCSM_Idp.getValue() == EnumType.routeSelectFailure){
				logger.info(Util.toString(dilgId) + "::: processIdp:type of call is MO:" +  cpb.eventTypeBCSM_Idp.getValue());
				cpb.callType = SasCapCallTypeEnum.MOBILE_ORIGIN ;
			}
			else if(cpb.eventTypeBCSM_Idp.getValue() == EnumType.termAttemptAuthorized || cpb.eventTypeBCSM_Idp.getValue() == EnumType.tBusy ||
					cpb.eventTypeBCSM_Idp.getValue() == EnumType.tNoAnswer ){
				logger.info(Util.toString(dilgId) + "::: processIdp:type of call is MT:" +  cpb.eventTypeBCSM_Idp.getValue());
				cpb.callType = SasCapCallTypeEnum.MOBILE_TERM ;
			}
			logger.debug(Util.toString(dilgId) + "::: processIdp:eventTypeBCSM: " + cpb.eventTypeBCSM_Idp.getValue());
		}
		if(idp.isCallReferenceNumberPresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp: CallReferenceNumberPresent");
			byte[] callRefNum = idp.getCallReferenceNumber().getValue();
			cpb.callRefNum = AdrsSignalDataType.decodeAdrsSignal(callRefNum, 0, callRefNum.length%2);
			logger.debug(Util.toString(dilgId) + "::: processIdp:CallReferenceNumber in string: " + cpb.callRefNum);
		}
		if(idp.isExt_basicServiceCodePresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp: Ext_basicServiceCodePresent");
			Ext_BasicServiceCode ext_BasicServiceCode = idp.getExt_basicServiceCode() ;
			if(ext_BasicServiceCode.isExt_BearerServiceSelected()){
				logger.info(Util.toString(dilgId) + "::: processIdp: Ext_BearerServiceSelected");
				byte[] bearerServiceCode = ext_BasicServiceCode.getExt_BearerService().getValue() ;
				int berarerCode ;
				if(bearerServiceCode[0] < 0){
					berarerCode = 256 + bearerServiceCode[0] ;
				}else{
					berarerCode = bearerServiceCode[0] ;
				}
				ExtBearerServiceCodeEnum bearerSrvCodeEnum  = ExtBearerServiceCodeEnum.fromInt(berarerCode);
				logger.debug(Util.toString(dilgId) + "::: processIdp: Ext_Bearerservice:" + bearerSrvCodeEnum);
			}else if(ext_BasicServiceCode.isExt_TeleserviceSelected()){
				logger.info(Util.toString(dilgId) + "::: processIdp: Ext_TeleserviceSelected");
				byte[] telServiceCode = ext_BasicServiceCode.getExt_Teleservice().getValue() ;
				int teleCode ;
				if(telServiceCode[0] < 0){
					teleCode = 256 + telServiceCode[0] ;
				}else{
					teleCode = telServiceCode[0] ;
				}
				ExtTeleServiceCodeEnum teleSrvCodeEnum  = ExtTeleServiceCodeEnum.fromInt(teleCode);
				logger.debug(Util.toString(dilgId) + "::: processIdp: Ext_Teleservice:" + teleSrvCodeEnum);
			}
		}
		if(idp.isTimeAndTimezonePresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp: TimeAndTimezonePresent");
			TimeAndTimezone timeAndTimezone = idp.getTimeAndTimezone();
			 TimeAndTimeZoneDataType timeZoneDataType = TimeAndTimeZoneDataType.deocdeTimeAndTimeZone(timeAndTimezone.getValue()) ;
			logger.debug(Util.toString(dilgId) + "::: processIdp:time value:" + timeZoneDataType.getTime());
			logger.debug(Util.toString(dilgId) + "::: processIdp:timeZone value:" + timeZoneDataType.getTimeZone());
		}
		if(idp.isBearerCapabilityPresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp: BearerCapabilityPresent");
			BearerCapabilityDataType beType = BearerCapabilityDataType.decodeBearerCapability(idp.getBearerCapability().getBearerCap());
		}
		if(idp.isCalledPartyBCDNumberPresent()){
			logger.info(Util.toString(dilgId) + "::: processIdp: CalledPartyBCDNumberPresent");
			AdrsStringDataType calledPartyBcdNum = AdrsStringDataType.decodeAdrsString(idp.getCalledPartyBCDNumber().getValue());
			cpb.calledPartyBCDNumber = calledPartyBcdNum ;
			
			if(cpb.calledPartyBCDNumber.getNatureOfAdrs() == NatureOfAdrsStringEnum.NATIONAL_NO){
				cpb.cldNumRTCH = "0".concat(cpb.calledPartyBCDNumber.getAdrs());
			}else if(cpb.calledPartyBCDNumber.getNatureOfAdrs() == NatureOfAdrsStringEnum.INTER_NO){
				cpb.cldNumRTCH = "00".concat(cpb.calledPartyBCDNumber.getAdrs());
			}else {
				cpb.cldNumRTCH = cpb.calledPartyBCDNumber.getAdrs();
			}
			logger.debug(Util.toString(dilgId) + "::: processIdp:nature of Adrs calledPartyBCDNumber" + cpb.calledPartyBCDNumber.getNatureOfAdrs());
			logger.debug(Util.toString(dilgId) + "::: processIdp:translated cldNum" + cpb.cldNumRTCH);
			logger.debug(Util.toString(dilgId) + "::: processIdp: CalledPartyBCDNumber:" + calledPartyBcdNum);
		}
		logger.info(Util.toString(dilgId) + "::: processIdp:Exit");
	}
	
	

}
