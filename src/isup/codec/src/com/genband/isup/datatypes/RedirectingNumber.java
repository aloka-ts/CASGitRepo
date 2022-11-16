
/*******************************************************************************
 *   Copyright (c) 2011 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.genband.isup.datatypes;

import org.apache.log4j.Logger;

import com.genband.isup.enumdata.AddPrsntRestEnum;
import com.genband.isup.enumdata.NatureOfAddEnum;
import com.genband.isup.enumdata.NumPlanEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

// This class implements the Redirecting Number encoding and decoding function
// according to TTC-JT-Q763, section 3.44 document. The format is defined below. 

//a)	Odd/even indicator
//see 3.9 a)
//b)	Nature of address indicator
//see 3.10 b)
//c)	Numbering plan indicator
//see 3.9 d)
//d)	Address presentation restricted indicator
//see 3.10 e)
//e)	Address signal
//see 3.10 g)
//f)	Filler
//see 3.9 f).



public class RedirectingNumber extends AddressSignal {

	private static Logger logger = Logger.getLogger(RedirectingNumber.class);	 

	/**
	 * @see NatureOfAddEnum
	 */
	NatureOfAddEnum natureOfAdrs;
	/**
	 * @see NumPlanEnum
	 */
	NumPlanEnum numPlan ;
	/**
	 * @see AddPrsntRestEnum
	 */
	AddPrsntRestEnum adrsPresntRestd; 

	/**
	 * This function will encode original called  number. AddrsSignal is mandatory parameter.
	 * Other parameters will be assigned default values if not provided.
	 * NatureOfAddEnum: spare,NumPlanEnum: spare,AddPrsntRestEnum: address presentation allowed
	 * @param addrSignal
	 * @param natureOfNumberEnum
	 * @param numberingPlanEnum
	 * @param adrsPresntRes
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeRedirectingNumber(String addrSignal, NatureOfAddEnum natureOfNumberEnum, 
			NumPlanEnum numberingPlanEnum, AddPrsntRestEnum adrsPresntRes) throws InvalidInputException {

		logger.info("encodeRedirectingNumber:Enter");

		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(addrSignal);
		int seqLength = 2 + bcdDigits.length;

		int i = 0;
		byte[] myParms = new byte[seqLength];

		int natureOfAdrs;
		if(natureOfNumberEnum == null){
			logger.error("encodeRedirectingNumber:Assining default value spare of NatureOfAddEnum");
			natureOfAdrs = 0;
		}else {
			natureOfAdrs = natureOfNumberEnum.getCode();
		}
		int numberingPlan ;
		if(numberingPlanEnum == null){
			logger.error("encodeRedirectingNumber:Assining default value spare of numberingPlan");
			numberingPlan = 0 ;
		}else {
			numberingPlan = numberingPlanEnum.getCode();
		}
		int adrsPrsntRes = 0 ;
		if(adrsPresntRes == null){
			logger.error("encodeRedirectingNumber:Assigning Default Address Presentation Indicator");
			adrsPrsntRes = 0 ;
		}else {
			adrsPrsntRes = adrsPresntRes.getCode();
		}
		// If even no. then set 8th bit 0 otherwise 1
		if (addrSignal.length() % 2 == 0) {
			myParms[i++] = (byte) ((0 << 7) | natureOfAdrs);
		} else {
			myParms[i++] = (byte) ((1 << 7) | natureOfAdrs);
		}
		// Setting 2nd byte for numbering plan and Internal Network Number indicator (INN ind.)
		myParms[i++] = (byte) ((numberingPlan << 4) | (adrsPrsntRes << 2));

		for (int j = 0; j < bcdDigits.length; j++) {
			myParms[i++] = bcdDigits[j];
		}
		if(logger.isDebugEnabled())
			logger.debug("encodeRedirectingNumber:Encoded Redirecting Num: "+ Util.formatBytes(myParms));
		logger.info("encodeRedirectingNumber:Exit");
		return myParms;
	}

	/**
	 * This function will decode Redirecting Number
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static RedirectingNumber decodeRedirectingNumber(byte[] data) throws InvalidInputException{

		logger.info("decodeRedirectingNumber:Enter");
		
		if(logger.isDebugEnabled())
			logger.debug("decodeRedirectingNumber: Input--> data:" + Util.formatBytes(data));
		
		if(data == null){
			logger.error("decodeRedirectingNumber: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		RedirectingNumber redirectingNum = new RedirectingNumber();

		// Extract Parity and Nature of address from first octet
		int parity = (data[0] >> 7) & 0x1;
		int natureOfAdrs = data[0] & 0x7f ;
		redirectingNum.natureOfAdrs = NatureOfAddEnum.fromInt(natureOfAdrs);

		// Extract Numbering Plan and Address presentation restriction Indicator
		// from second octet
		if(data.length >= 1){
			int adrsPrsntRes = (data[1] >> 2) & 0x3;
			redirectingNum.adrsPresntRestd = AddPrsntRestEnum.fromInt(adrsPrsntRes);
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			redirectingNum.numPlan = NumPlanEnum.fromInt(numberingPlan);
		}

		// Extract address signal i.e., number from remaining octet
		if(data.length > 2){
			redirectingNum.addrSignal = AddressSignal.decodeAdrsSignal(data, 2 , parity);			
		}	
		logger.debug("decodeRedirectingNumber: Output<--" + redirectingNum.toString());
		logger.info("decodeRedirectingNumber:Exit");
		return redirectingNum ;
	}

	public String toString(){

		String obj = "addrSignal:"+ addrSignal + " ,natureOfAdrs:"+ natureOfAdrs + 
				" ,adrsPresntRestd:" + adrsPresntRestd + " ,numPlan:" + numPlan ;
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

	public AddPrsntRestEnum getAdrsPresntRestd() {
		return adrsPresntRestd;
	}

	public void setAdrsPresntRestd(AddPrsntRestEnum adrsPresntRestd) {
		this.adrsPresntRestd = adrsPresntRestd;
	}


}
