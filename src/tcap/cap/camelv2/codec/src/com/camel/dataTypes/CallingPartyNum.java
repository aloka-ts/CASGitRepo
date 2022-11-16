package com.camel.dataTypes;

import org.apache.log4j.Logger;

import com.camel.enumData.AdrsPrsntRestdEnum;
import com.camel.enumData.NatureOfAdrsEnum;
import com.camel.enumData.NumINcomplteEnum;
import com.camel.enumData.NumPlanEnum;
import com.camel.enumData.ScreeningIndEnum;
import com.camel.exceptions.InvalidInputException;
import com.camel.util.Util;

/**
 * This class have parameters for calling party Number.
 * @author nkumar
 *
 */
public class CallingPartyNum extends AdrsSignalDataType{
	
	/**
	 * @see
	 */
	NatureOfAdrsEnum natureOfAdrs; 
	/**
	 * @see NumPlanEnum
	 */
	NumPlanEnum numPlan; 
	/**
	 * @see AdrsPrsntRestdEnum
	 */
	AdrsPrsntRestdEnum adrsPresntRestd; 
	/**
	 * @see ScreeningIndEnum
	 */
	ScreeningIndEnum screening ; 
	/**
	 * @see NumINcomplteEnum
	 */
	NumINcomplteEnum numIncomplte;
	
	private static Logger logger = Logger.getLogger(CallingPartyNum.class);	 
	 
	
	/**
	 * This function will encode the calling party number.AddrsSignal is mandatory parameter.
	 * Other parameters will be assigned default values if not provided.
	 * NatureOfAdrsEnum:spare, NumPlanEnum:spare,adrsPresntRestdEnum:Spare,
	 * screeningEnum:"user provided, not verified"
	 * @param addrSignal
	 * @param natureOfAdrsEnum
	 * @param numberingPlanEnum
	 * @param adrsPresntRestdEnum
	 * @param screeningEnum
	 * @param numIncomplteEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeCalgParty(String addrSignal, NatureOfAdrsEnum natureOfAdrsEnum, NumPlanEnum numberingPlanEnum, 
											AdrsPrsntRestdEnum adrsPresntRestdEnum, ScreeningIndEnum screeningEnum , NumINcomplteEnum numIncomplteEnum) throws InvalidInputException {
	
		logger.info("encodeCalgParty:Enter");
		byte[] bcdDigits = AdrsSignalDataType.encodeAdrsSignal(addrSignal);
		int seqLength = 2 + bcdDigits.length;
		int i = 0;
		byte[] myParms = new byte[seqLength];
		int natureOfAdrs;
		if(natureOfAdrsEnum == null){
			// Assigning default value -Spare
			natureOfAdrs = 0;
		}else {
			natureOfAdrs = natureOfAdrsEnum.getCode();
		}
		int numberingPlan ;
		if(numberingPlanEnum == null){
			// Assigning default value -Spare
			numberingPlan = 0 ;
		}else {
			numberingPlan = numberingPlanEnum.getCode();
		}
		int adrsPresntRestd ;
		if(adrsPresntRestdEnum == null){
			//Assigning default value -Spare
			adrsPresntRestd = 3;
		}else{
			adrsPresntRestd = adrsPresntRestdEnum.getCode();
		}
		int screening ;
		if(screeningEnum == null){
			//Assigning default value -"user provided, not verified"
			screening = 0;
		}else{
			screening = screeningEnum.getCode();
		}	
		int numIncomplte ;
		if(numIncomplteEnum == null){
			//Assigning default value -complete
			numIncomplte = 0;
		}else{
			numIncomplte = numIncomplteEnum.getCode();
		}	
		
		//If address presentation restricted indicator indicates
		//address not available(2), octets 3 to n are omitted, 1st byte are coded with
		//0's, and the subfield f) is coded with 11.
		if(adrsPresntRestd == 2){
			myParms[i++]= (byte)(0x0);
			myParms[i++]= (byte)((adrsPresntRestd << 2) | 0x03);
		}else {	
			// If even no. of Address signal then set 8th bit 0 otherwise 1
			if (addrSignal.length() % 2 == 0) {
				myParms[i++] = (byte) ((0 << 7) | natureOfAdrs);
			} else {
				myParms[i++] = (byte) ((1 << 7) | natureOfAdrs);
			}
			myParms[i++] = (byte)((numIncomplte << 7) | (numberingPlan << 4) | (adrsPresntRestd << 2) | screening);
		}
		
		for (int j = 0; j < bcdDigits.length; j++) {
			myParms[i++] = bcdDigits[j];
		}
		if(logger.isDebugEnabled())
		logger.debug("encodeCalgParty:Encoded Calling Party: " + Util.formatBytes(myParms));
		logger.info("encodeCalgParty:Exit");
		return myParms;
	}
	
	/**
	 * This function will decode calling Party number.
	 * @param data
	 * @return object of CallingPartyNum
	 * @throws InvalidInputException
	 */
	public static CallingPartyNum decodeCalgParty(byte[] data) throws InvalidInputException{
		logger.info("decodeCalgParty:Enter");
		if(logger.isDebugEnabled())
		logger.debug("decodeCalgParty: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeCalgParty: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		CallingPartyNum clgParty = new CallingPartyNum();
		int parity = (data[0] >> 7) & 0x1;
		int natureOfAdrs = data[0] & 0x7f ;
		clgParty.natureOfAdrs = NatureOfAdrsEnum.fromInt(natureOfAdrs);
		if(data.length >=1){
			int numIncomplete = (data[1] >> 7) & 0x1;
			clgParty.numIncomplte = NumINcomplteEnum.fromInt(numIncomplete);
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			clgParty.numPlan = NumPlanEnum.fromInt(numberingPlan);
			int adrsPresntRestd = (data[1] >> 2) & 0x3 ;
			clgParty.adrsPresntRestd = AdrsPrsntRestdEnum.fromInt(adrsPresntRestd);
			int screening = data[1] &0x3 ;
			clgParty.screening = ScreeningIndEnum.fromInt(screening);
		}
		if(data.length > 2){
			clgParty.addrSignal = AdrsSignalDataType.decodeAdrsSignal(data, 2 , parity);
		}		
		logger.debug("decodeCalgParty: Output<--" + clgParty.toString());
		logger.info("decodeCalgParty:Exit");
		return clgParty ;
	}
	
	public NatureOfAdrsEnum getNatureOfAdrs() {
		return natureOfAdrs;
	}

	public NumPlanEnum getNumPlan() {
		return numPlan;
	}

	public AdrsPrsntRestdEnum getAdrsPresntRestd() {
		return adrsPresntRestd;
	}

	public ScreeningIndEnum getScreening() {
		return screening;
	}

	public NumINcomplteEnum getNumIncomplte() {
		return numIncomplte;
	}
	
	

	public void setNatureOfAdrs(NatureOfAdrsEnum natureOfAdrs) {
		this.natureOfAdrs = natureOfAdrs;
	}

	public void setNumPlan(NumPlanEnum numPlan) {
		this.numPlan = numPlan;
	}

	public void setAdrsPresntRestd(AdrsPrsntRestdEnum adrsPresntRestd) {
		this.adrsPresntRestd = adrsPresntRestd;
	}

	public void setScreening(ScreeningIndEnum screening) {
		this.screening = screening;
	}

	public void setNumIncomplte(NumINcomplteEnum numIncomplte) {
		this.numIncomplte = numIncomplte;
	}

	public String toString(){
		
		String obj = "addrSignal:"+ addrSignal + " ,natureOfAdrs:"+ natureOfAdrs + " ,numIncomplte:" + numIncomplte +
									
									" ,numPlan:" + numPlan + " ,adrsPresntRestd:" + adrsPresntRestd + " ,screening:" + screening ;
		return obj ;
	}

}
