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
import com.genband.isup.enumdata.IntNwNumEnum;
import com.genband.isup.enumdata.NumPlanEnum;
import com.genband.isup.enumdata.OrigCalledNumNatureOfAddEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;

// This class implements the Original Called Number encoding and decoding fucntion
// according to TTC-JT-Q763, section 3.39 document. The format is defined below. 

// 8	7	6	5	4	3	2	1
// 1	Odd/ even	Nature of address indicator
// 2	Spare	Numbering plan ind.	Address presentation restricted indicator	Spare
// 3
// 2nd address signal	1st address signal
// .	Address information  *
// (Number digit)     *
//	
//n	Filler (if necessary)	nth address signal
// Odd/even indicator: see 3.9 a).
// b)	Nature of address indicator:
// 0 0 0 0 0 0 0	spare
// 0 0 0 0 0 0 1	subscriber number (national use)
// 0 0 0 0 0 1 0	unknown (national use)
// 0 0 0 0 0 1 1	national (significant) number (national use)
// 0 0 0 0 1 0 0	international number
// 0 0 0 0 1 0 1
//         to
// 1 1 0 1 1 1 1	spare
// 1 1 1 0 0 0 0
//        to
// 1 1 1 1 1 1 0	reserved for national use
// 1 1 1 1 1 1 1	spare
// c)	Numbering plan indicator: see 3.9 d).
// d)	Address presentation restricted indicator: see 3.10 e).
// e)	Address signal: see 3.10 g).
// f)	Filler: see 3.9 f).


public class OriginalCalledNumber extends AddressSignal {

	private static Logger logger = Logger.getLogger(OriginalCalledNumber.class);	 

	/**
	 * @see OrigCalledNumNatureOfAddEnum
	 */
	OrigCalledNumNatureOfAddEnum natureOfAdrs ; 
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
	 * OrigCalledNumNatureOfAddEnum: spare,NumPlanEnum: spare,AddPrsntRestEnum: address presentation allowed
	 * @param addrSignal
	 * @param natureOfNumberEnum
	 * @param numberingPlanEnum
	 * @param adrsPresntRes
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeOrignalCalledNum(String addrSignal, OrigCalledNumNatureOfAddEnum natureOfNumberEnum, 
			NumPlanEnum numberingPlanEnum, AddPrsntRestEnum adrsPresntRes) throws InvalidInputException {

		logger.info("encodeOrignalCalledNum:Enter");

		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(addrSignal);
		int seqLength = 2 + bcdDigits.length;

		int i = 0;
		byte[] myParms = new byte[seqLength];

		int natureOfAdrs;
		if(natureOfNumberEnum == null){
			logger.error("encodeOrignalCalledNum:Assining default value spare of OrigCalledNumNatureOfAddEnum");
			natureOfAdrs = 0;
		}else {
			natureOfAdrs = natureOfNumberEnum.getCode();
		}
		int numberingPlan ;
		if(numberingPlanEnum == null){
			logger.error("encodeOrignalCalledNum:Assining default value spare of numberingPlan");
			numberingPlan = 0 ;
		}else {
			numberingPlan = numberingPlanEnum.getCode();
		}
		int adrsPrsntRes = 0 ;
		if(adrsPresntRes == null){
			logger.error("encodeOrignalCalledNum:Assigning Default Address Presentation Indicator");
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
			logger.debug("encodeOrignalCalledNum:Encoded Original Called Num: "+ Util.formatBytes(myParms));
		logger.info("encodeOrignalCalledNum:Exit");
		return myParms;
	}

	/**
	 * This function will decode Original Called Number
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static OriginalCalledNumber decodeOriginalCalledNumber(byte[] data) throws InvalidInputException{
		
		logger.info("decodeOriginalCalledNumber:Enter");
		
		if(logger.isDebugEnabled())
			logger.debug("decodeOriginalCalledNumber: Input--> data:" + Util.formatBytes(data));
		
		if(data == null){
			logger.error("decodeOriginalCalledNumber: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}
		
		OriginalCalledNumber origCldPty = new OriginalCalledNumber();

		// Extract Parity and Nature of address from first octet
		int parity = (data[0] >> 7) & 0x1;
		int natureOfAdrs = data[0] & 0x7f ;
		origCldPty.natureOfAdrs = OrigCalledNumNatureOfAddEnum.fromInt(natureOfAdrs);

		// Extract Numbering Plan and Address presentation restriction Indicator
		// from second octet
		if(data.length >= 1){
			int adrsPrsntRes = (data[1] >> 2) & 0x3;
			origCldPty.adrsPresntRestd = AddPrsntRestEnum.fromInt(adrsPrsntRes);
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			origCldPty.numPlan = NumPlanEnum.fromInt(numberingPlan);
		}

		// Extract address signal i.e., number from remaining octet
		if(data.length > 2){
			origCldPty.addrSignal = AddressSignal.decodeAdrsSignal(data, 2 , parity);			
		}	
		logger.debug("decodeOriginalCalledNumber: Output<--" + origCldPty.toString());
		logger.info("decodeOriginalCalledNumber:Exit");
		return origCldPty ;
	}

	public String toString(){

		String obj = "addrSignal:"+ addrSignal + " ,natureOfAdrs:"+ natureOfAdrs + 
				" ,adrsPresntRestd:" + adrsPresntRestd + " ,numPlan:" + numPlan ;
		return obj ;
	}

	public OrigCalledNumNatureOfAddEnum getNatureOfAdrs() {
		return natureOfAdrs;
	}

	public void setNatureOfAdrs(OrigCalledNumNatureOfAddEnum natureOfAdrs) {
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
