package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.IntNwNumEnum;
import com.genband.isup.enumdata.NatureOfAddEnum;
import com.genband.isup.enumdata.NumPlanEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;


/**
 * Used for encoding and decoding of CalledPartyNum
 * @author vgoel
 *
 */
public class CalledPartyNum extends AddressSignal {

	private static Logger logger = Logger.getLogger(CalledPartyNum.class);	 

	/**
	 * @see NatureOfAddEnum
	 */
	NatureOfAddEnum natureOfAdrs ; 
	/**
	 * @see NumPlanEnum
	 */
	NumPlanEnum numPlan ;
	/**
	 * @see IntNwNumEnum
	 */
	IntNwNumEnum intNtwrkNum ;

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
	public static byte[] encodeCaldParty(String addrSignal, NatureOfAddEnum natureOfNumberEnum, NumPlanEnum numberingPlanEnum, IntNwNumEnum intNtwrkNumEnum) throws InvalidInputException {
		logger.info("encodeCaldParty:Enter");
		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(addrSignal);
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

	/**
	 * This function will decode Called Party Number.
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static CalledPartyNum decodeCaldParty(byte[] data) throws InvalidInputException{
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
		cldParty.natureOfAdrs = NatureOfAddEnum.fromInt(natureOfAdrs);
		if(data.length >= 1){
			int intNtwrkNum = (data[1] >> 7) & 0x1;
			cldParty.intNtwrkNum = IntNwNumEnum.fromInt(intNtwrkNum);
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			cldParty.numPlan = NumPlanEnum.fromInt(numberingPlan);
		}
		if(data.length > 2){
			cldParty.addrSignal = AddressSignal.decodeAdrsSignal(data, 2 , parity);			
		}	
		if(logger.isDebugEnabled())
			logger.debug("decodeCaldParty: Output<--" + cldParty.toString());
		logger.info("decodeCaldParty:Exit");
		return cldParty ;
	}

	public String toString(){

		String obj = "addrSignal:"+ addrSignal + " ,natureOfAdrs:"+ natureOfAdrs + " ,intNtwrkNum:" + intNtwrkNum + " ,numPlan:" + numPlan ;
		return obj ;
	}

	public NatureOfAddEnum getNatureOfAdrs() {
		return natureOfAdrs;
	}

	public void setNatureOfAdrs(NatureOfAddEnum natureOfAdrs) {
		this.natureOfAdrs = natureOfAdrs;
	}

	public NumPlanEnum getNumPlan() {
		return numPlan;
	}

	public void setNumPlan(NumPlanEnum numPlan) {
		this.numPlan = numPlan;
	}

	public IntNwNumEnum getIntNtwrkNum() {
		return intNtwrkNum;
	}

	public void setIntNtwrkNum(IntNwNumEnum intNtwrkNum) {
		this.intNtwrkNum = intNtwrkNum;
	}
}
