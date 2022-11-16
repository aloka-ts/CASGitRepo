package com.agnity.camelv2.datatypes;

import org.apache.log4j.Logger;

import com.agnity.camelv2.enumdata.AdrsPrsntRestdEnum;
import com.agnity.camelv2.enumdata.NatureOfAdrsEnum;
import com.agnity.camelv2.enumdata.NumINcomplteEnum;
import com.agnity.camelv2.enumdata.NumPlanEnum;
import com.agnity.camelv2.enumdata.ScreeningIndEnum;
import com.agnity.camelv2.exceptions.InvalidInputException;
import com.agnity.camelv2.util.Util;

/**
 * This class have parameters for Orignal Called Party ID. 
 * @author nkumar
 *
 */

public class PartyId extends CallingPartyNum{
	
	private static Logger logger = Logger.getLogger(PartyId.class);	 
	
	/**
	 * This function will encode the Redirecting Party Id and Original CalledParty Id..AddrsSignal is mandatory parameter.
	 * Other parameters will be assigned default values if not provided.
	 * NatureOfAdrsEnum: spare,NumPlanEnum: spare,adrsPresntRestdEnum:Spare
	 * @param addrSignal
	 * @param natureOfAdrsEnum
	 * @param numberingPlanEnum
	 * @param adrsPresntRestdEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodePartyId(String addrSignal, NatureOfAdrsEnum natureOfAdrsEnum, NumPlanEnum numberingPlanEnum, AdrsPrsntRestdEnum adrsPresntRestdEnum) throws InvalidInputException {
		
		logger.info("encodePartyId:Enter");
		// No need of ScreeningIndEnum and NumINcomplteEnum in encoding of Original Called party number
		byte[] data = CallingPartyNum.encodeCalgParty(addrSignal, natureOfAdrsEnum, numberingPlanEnum, adrsPresntRestdEnum, ScreeningIndEnum.USER_PROVD_NOT_VERFD, NumINcomplteEnum.COMPLETE);
		logger.info("encodePartyId:Exit");
		return data ;
	
	}
	
	/**
	 * This function will decode Redirecting Party Id and Original CalledParty Id.
	 * @param data
	 * @return object of PartyId
	 * @throws InvalidInputException
	 */
	public static PartyId decodePartyId(byte[] data) throws InvalidInputException{
		logger.info("decodePartyId:Enter");
		if(logger.isDebugEnabled())
		logger.debug("decodePartyId: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodePartyId: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		PartyId orgCldParty = new PartyId();
		int parity = (data[0] >> 7) & 0x1;
		int natureOfAdrs = data[0] & 0x7f ;
		orgCldParty.natureOfAdrs = NatureOfAdrsEnum.fromInt(natureOfAdrs);
		orgCldParty.numIncomplte = null;
		if(data.length > 1){
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			orgCldParty.numPlan = NumPlanEnum.fromInt(numberingPlan);
			int adrsPresntRestd = (data[1] >> 2) & 0x3 ;
			orgCldParty.adrsPresntRestd = AdrsPrsntRestdEnum.fromInt(adrsPresntRestd);
		}		
		orgCldParty.screening = null ;
		if(data.length > 2){
			orgCldParty.addrSignal = AdrsSignalDataType.decodeAdrsSignal(data, 2 , parity);
		}
		logger.info("decodePartyId:Output<--" + orgCldParty.toString());
		logger.info("decodePartyId:Exit");
		return orgCldParty ;
	}
	
	public String toString(){
		
		String obj = "addrSignal:"+ addrSignal + " ,natureOfAdrs:"+ natureOfAdrs + " ,numIncomplte:" + numIncomplte +
									
									" ,numPlan:" + numPlan + " ,adrsPresntRestd:" + adrsPresntRestd + " ,screening:" + screening ;
		return obj ;
	}
}
