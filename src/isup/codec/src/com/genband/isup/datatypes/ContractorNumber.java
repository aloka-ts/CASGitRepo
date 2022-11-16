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
import com.genband.isup.enumdata.ContractorNatureOfAddrEnum;
import com.genband.isup.enumdata.ContractorNumPlanEnum;
import com.genband.isup.exceptions.InvalidInputException;
import com.genband.isup.util.Util;


/**
 * Used for encoding and decoding of ContractorNumber
 * @author vgoel
 *
 */
public class ContractorNumber extends AddressSignal {

	private static Logger logger = Logger.getLogger(ContractorNumber.class);	 

	/**
	 * @see ContractorNatureOfAddrEnum
	 */
	ContractorNatureOfAddrEnum natureOfAdrs ; 
	/**
	 * @see ContractorContractorNumPlanEnum
	 */
	ContractorNumPlanEnum numPlan ;

	/**
	 * This function will encode contractor number. AddrsSignal is mandatory parameter.
	 * Other parameters will be assigned default values if not provided.
	 * NatureOfAdrsEnum: spare,ContractorNumPlanEnum: spare,IntNtwrkNumEnum:routing to internal network number allowed
	 * @param addrSignal
	 * @param natureOfNumberEnum
	 * @param numberingPlanEnum
	 * @param intNtwrkNumEnum
	 * @return encoded data byte[]
	 * @throws InvalidInputException 
	 */
	public static byte[] encodeContractorNumber(String addrSignal, ContractorNatureOfAddrEnum natureOfNumberEnum, 
			ContractorNumPlanEnum numberingPlanEnum) throws InvalidInputException {

		logger.info("encodeContractorNumber:Enter");
		if (logger.isInfoEnabled()) {
			logger.info("encodeTtcContractorNum:Enter");
		}
		byte[] bcdDigits = AddressSignal.encodeAdrsSignal(addrSignal);
		int seqLength = 2 + bcdDigits.length;

		int i = 0;
		byte[] myParms = new byte[seqLength];

		int natureOfAdrs;
		if(natureOfNumberEnum == null){
			if (logger.isInfoEnabled()) {
				logger.info("encodeContractorNumber:Assigning default value spare of natureOfAdrs");
			}
			natureOfAdrs = 0;
		}else {
			natureOfAdrs = natureOfNumberEnum.getCode();
		}
		int numberingPlan ;
		if(numberingPlanEnum == null){
			if (logger.isInfoEnabled()) {
				logger.info("encodeContractorNumber:Assigning default value spare of numberingPlan");
			}
			numberingPlan = 0 ;
		}else {
			numberingPlan = numberingPlanEnum.getCode();
		}

		// If even no. then set 8th bit 0 otherwise 1
		if (addrSignal.length() % 2 == 0) {
			myParms[i++] = (byte) ((0 << 7) | natureOfAdrs);
		} else {
			myParms[i++] = (byte) ((1 << 7) | natureOfAdrs);
		}
		// Setting 2nd byte for numbering plan and Internal Network Number indicator (INN ind.)
		myParms[i++] = (byte) (numberingPlan << 4);

		for (int j = 0; j < bcdDigits.length; j++) {
			myParms[i++] = bcdDigits[j];
		}

		if(logger.isDebugEnabled())
			logger.debug("encodeContractorNumber:Encoded Called Party Num: "+ Util.formatBytes(myParms));

		logger.info("encodeContractorNumber:Exit");
		return myParms;
	}

	/**
	 * This function will decode contractor.
	 * @param data
	 * @return decoded data String
	 * @throws InvalidInputException 
	 */
	public static ContractorNumber decodeContractorNumber(byte[] data) throws InvalidInputException{

		logger.info("decodeContractorNumber:Enter");

		if(logger.isDebugEnabled())
			logger.debug("decodeContractorNumber: Input--> data:" + Util.formatBytes(data));

		if(data == null){
			logger.error("decodeContractorNumber: InvalidInputException(data is null)");
			throw new InvalidInputException("data is null");
		}

		ContractorNumber contractorNum = new ContractorNumber();
		int parity = (data[0] >> 7) & 0x1;
		int natureOfAdrs = data[0] & 0x7f ;
		
		contractorNum.natureOfAdrs = ContractorNatureOfAddrEnum.fromInt(natureOfAdrs);
		if(data.length >= 1){
			int numberingPlan = (data[1] >> 4) & 0x7 ;
			contractorNum.numPlan = ContractorNumPlanEnum.fromInt(numberingPlan);
		}
		
		if(data.length > 2){
			contractorNum.addrSignal = AddressSignal.decodeAdrsSignal(data, 2 , parity);			
		}	

		if (logger.isInfoEnabled()) {
			logger.info("decodeTtcContractorNum:Exit");
		}

		logger.debug("decodeContractorNumber: Output<--" + contractorNum.toString());
		if (logger.isInfoEnabled()) {
			logger.info("decodeContractorNumber:Exit");
		}
		return contractorNum ;
	}

	public String toString(){

		String obj = "addrSignal:"+ addrSignal + " ,natureOfAdrs:"+ natureOfAdrs + " ,numPlan:" + numPlan ;
		return obj ;
	}

	public ContractorNatureOfAddrEnum getNatureOfAdrs() {
		return natureOfAdrs;
	}

	public void setNatureOfAdrs(ContractorNatureOfAddrEnum natureOfAdrs) {
		this.natureOfAdrs = natureOfAdrs;
	}

	public ContractorNumPlanEnum getNumPlan() {
		return numPlan;
	}

	public void setNumPlan(ContractorNumPlanEnum numPlan) {
		this.numPlan = numPlan;
	}

}

