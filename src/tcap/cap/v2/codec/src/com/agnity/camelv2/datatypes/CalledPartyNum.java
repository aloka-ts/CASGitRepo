package com.agnity.camelv2.datatypes;

import org.apache.log4j.Logger;

import com.agnity.camelv2.enumdata.IntNtwrkNumEnum;
import com.agnity.camelv2.enumdata.NatureOfAdrsEnum;
import com.agnity.camelv2.enumdata.NumPlanEnum;
import com.agnity.camelv2.exceptions.InvalidInputException;
import com.agnity.camelv2.util.Util;

/**
 * This class have parameters for Called Party Number.
 * @author nkumar
 *
 */

public class CalledPartyNum extends AdrsSignalDataType{
	
	
	/**
	 * @see NatureOfAdrsEnum
	 */
	NatureOfAdrsEnum natureOfAdrs ; 
	/**
	 * @see NumPlanEnum
	 */
	NumPlanEnum numPlan ;
	/**
	 * @see IntNtwrkNumEnum
	 */
	IntNtwrkNumEnum intNtwrkNum ;
	
	private static Logger logger = Logger.getLogger(CalledPartyNum.class);	 
	
	
	/**
	 * This function will encode called party number. AddrsSignal is mandatory parameter.
	 * Other parameters will be assigned default values if not provided.
	 * NatureOfAdrsEnum: spare,NumPlanEnum: spare,IntNtwrkNumEnum:routing to internal network number allowed
	 * @param addrSignal
	 * @param natureOfNumberEnum
	 * @param numberingPlanEnum
	 * @param intNtwrkNumEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeCaldParty(String addrSignal, NatureOfAdrsEnum natureOfNumberEnum, NumPlanEnum numberingPlanEnum, IntNtwrkNumEnum intNtwrkNumEnum) throws InvalidInputException {
		logger.info("encodeCaldParty:Enter");
		byte[] bcdDigits = AdrsSignalDataType.encodeAdrsSignal(addrSignal);
		int seqLength = 2 + bcdDigits.length;

		int i = 0;
		byte[] myParms = new byte[seqLength];
		
		int natureOfAdrs;
		if(natureOfNumberEnum == null){
			logger.info("encodeCaldParty:Assining default value spare of natureOfAdrs");
			natureOfAdrs = 0;
		}else {
			natureOfAdrs = natureOfNumberEnum.getCode();
		}
		int numberingPlan ;
		if(numberingPlanEnum == null){
			logger.info("encodeCaldParty:Assining default value spare of numberingPlan");
			numberingPlan = 0 ;
		}else {
			numberingPlan = numberingPlanEnum.getCode();
		}
		int intNtwrkNum ;
		if(intNtwrkNumEnum == null){
			// Assigning default value routing to internal network number allowed
			logger.info("encodeCaldParty:Assining default value routing to internal network number allowed of intNtwrkNum");
			intNtwrkNum = 0 ;
		}else {
			intNtwrkNum = intNtwrkNumEnum.getCode();
		}
		// If even no. then set 8th bit 0 otherwise 1
		if (addrSignal.length() % 2 == 0) {
			myParms[i++] = (byte) ((0 << 7) | natureOfAdrs);
		} else {
			myParms[i++] = (byte) ((1 << 7) | natureOfAdrs);
		}
		// Setting 2nd byte for numbering plan and Internal Network Number indicator (INN ind.)
		myParms[i++] = (byte) ( (intNtwrkNum << 7) | (numberingPlan << 4));
		
		for (int j = 0; j < bcdDigits.length; j++) {
			myParms[i++] = bcdDigits[j];
		}
		if(logger.isDebugEnabled())
		logger.debug("encodeCaldParty:Encoded Called Party Num: "+ Util.formatBytes(myParms));
		logger.info("encodeCaldParty:Exit");
		return myParms;
	}
	
	public NatureOfAdrsEnum getNatureOfAdrs() {
		return natureOfAdrs;
	}

	public NumPlanEnum getNumPlan() {
		return numPlan;
	}

	public IntNtwrkNumEnum getIntNtwrkNum() {
		return intNtwrkNum;
	}
	
	

	public void setNatureOfAdrs(NatureOfAdrsEnum natureOfAdrs) {
		this.natureOfAdrs = natureOfAdrs;
	}

	public void setNumPlan(NumPlanEnum numPlan) {
		this.numPlan = numPlan;
	}

	public void setIntNtwrkNum(IntNtwrkNumEnum intNtwrkNum) {
		this.intNtwrkNum = intNtwrkNum;
	}

	/**
	 * This function will decode Called Party Number.
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static CalledPartyNum decodeCaldParty(byte[] data,boolean calledPartyBCD) throws InvalidInputException{
		logger.info("decodeCaldParty:Enter");
		if(logger.isDebugEnabled())
		logger.debug("decodeCaldParty: Input--> data:" + Util.formatBytes(data));
		if(data == null){
			logger.error("decodeCaldParty: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		CalledPartyNum cldParty = new CalledPartyNum();
		int parity = (data[0] >> 7) & 0x1;
		int natureOfAdrs = data[0] & 0x7f ;
		cldParty.natureOfAdrs = NatureOfAdrsEnum.fromInt(natureOfAdrs);
		if(data.length >= 1){
			int intNtwrkNum = (data[1] >> 7) & 0x1;
			cldParty.intNtwrkNum = IntNtwrkNumEnum.fromInt(intNtwrkNum);
			
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			cldParty.numPlan = NumPlanEnum.fromInt(numberingPlan);
			
			// in case of CalledPartyBCD need to fetch 
			// num plan from 0th octet
			if(calledPartyBCD==true){
				intNtwrkNum = (data[0] >> 4) & 0x7;
				if(intNtwrkNum == 0|| intNtwrkNum ==1){
					cldParty.intNtwrkNum = IntNtwrkNumEnum.fromInt(intNtwrkNum);
				}else{
					cldParty.intNtwrkNum= IntNtwrkNumEnum.fromInt(0);
				}
				
				numberingPlan = data[0] & 0x0f ; ;
				cldParty.numPlan = NumPlanEnum.fromInt(numberingPlan);
			}
		}
		if(data.length > 2){
			//here we can check,Is CalledPartyBCDNumberPresent or not.
			//If present then we will take offset 1 for it.
			if(calledPartyBCD==true)
			{
				cldParty.addrSignal = AdrsSignalDataType.decodeAdrsSignal(data, 1 , 0);
			}
			else
			{
				cldParty.addrSignal = AdrsSignalDataType.decodeAdrsSignal(data, 2 , parity);
			}
		}	
		logger.debug("decodeCaldParty: Output<--" + cldParty.toString());
		logger.info("decodeCaldParty:Exit");
		return cldParty ;
	}
	
	public String toString(){
		
		String obj = "addrSignal:"+ addrSignal + " ,natureOfAdrs:"+ natureOfAdrs + " ,intNtwrkNum:" + intNtwrkNum +
									
									" ,numPlan:" + numPlan ;
		return obj ;
	}
	
}
