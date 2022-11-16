/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/
package com.agnity.cap.v2.datatypes;

import org.apache.log4j.Logger;

import com.agnity.cap.v2.datatypes.enumType.AddressPresentationRestricatedIndicatorCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.NatureOfAddressIndicatorCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.NumberingOfPlanIndicatorCapV2Enum;
import com.agnity.cap.v2.datatypes.enumType.OddEvenIndicatorCapV2Enum;
import com.agnity.cap.v2.exceptions.InvalidInputException;

/**
 * Ref- Q.763e 
 * @author rnarayan
 * 
 */
public class OriginalCalledPartyIdCapV2 {

	private OddEvenIndicatorCapV2Enum oddEvenInd;
	private NatureOfAddressIndicatorCapV2Enum natureAddInd;
	private NumberingOfPlanIndicatorCapV2Enum numPlanInd;
	private AddressPresentationRestricatedIndicatorCapV2Enum addPreResInd;
	private String addressSignal;
	private static Logger logger = Logger.getLogger(OriginalCalledPartyIdCapV2.class);
	
	
	/**
	 * 
	 * @return oddEvenInd
	 */
	public OddEvenIndicatorCapV2Enum getOddEvenInd() {
		return oddEvenInd;
	}

	/**
	 * set oddEvenInd
	 * @param oddEvenInd
	 */
	public void setOddEvenInd(OddEvenIndicatorCapV2Enum oddEvenInd) {
		this.oddEvenInd = oddEvenInd;
	}

	/**
	 * 
	 * @return NatureOfAddressIndicator
	 */
	public NatureOfAddressIndicatorCapV2Enum getNatureAddInd() {
		return natureAddInd;
	}

	/**
	 * set NatureOfAddressIndicator
	 * @param natureAddInd
	 */
	public void setNatureAddInd(NatureOfAddressIndicatorCapV2Enum natureAddInd) {
		this.natureAddInd = natureAddInd;
	}

	/**
	 * 
	 * @return NumberingOfPlanIndicator
	 */
	public NumberingOfPlanIndicatorCapV2Enum getNumPlanInd() {
		return numPlanInd;
	}

	/**
	 * set NumberingOfPlanIndicator
	 * @param numPlanInd
	 */
	public void setNumPlanInd(NumberingOfPlanIndicatorCapV2Enum numPlanInd) {
		this.numPlanInd = numPlanInd;
	}

	/**
	 * 
	 * @return AddressPresentationRestricatedIndicator
	 */
	public AddressPresentationRestricatedIndicatorCapV2Enum getAddPreResInd() {
		return addPreResInd;
	}

	/**
	 * set AddressPresentationRestricatedIndicator
	 * @param addPreResInd
	 */
	public void setAddPreResInd(
			AddressPresentationRestricatedIndicatorCapV2Enum addPreResInd) {
		this.addPreResInd = addPreResInd;
	}

	/**
	 * 
	 * @return addressSignal
	 */
	public String getAddressSignal() {
		return addressSignal;
	}

	/**
	 * set addressSignal
	 * @param addressSignal
	 */
	public void setAddressSignal(String addressSignal) {
		this.addressSignal = addressSignal;
	}

	/**
	 * decode OriginalCalledPartyID byte array into non asn object OriginalCalledPartyIDCapv2
	 * @param bytes
	 * @return OriginalCalledPartyIDCapV2 Object
	 * @throws InvalidInputException
	 */
	public static OriginalCalledPartyIdCapV2 decode(byte[] bytes)  throws InvalidInputException{
		OriginalCalledPartyIdCapV2 ocpId = null;
		if(logger.isDebugEnabled()){
			logger.debug("OriginalCalledPartyID bytes length is "+bytes.length);
		}
			ocpId = new OriginalCalledPartyIdCapV2();
			ocpId.oddEvenInd = OddEvenIndicatorCapV2Enum.getValue((bytes[0]>>7)& 0x01);
			ocpId.natureAddInd = NatureOfAddressIndicatorCapV2Enum.getValue(bytes[0]&0x7f);
			ocpId.numPlanInd = NumberingOfPlanIndicatorCapV2Enum.getValue((bytes[1]>>4)&0x07);
			ocpId.addPreResInd = AddressPresentationRestricatedIndicatorCapV2Enum.getValue((bytes[1]>>2)&0x03);
	        ocpId.addressSignal = AddressSignalCapV2.decode(bytes, 2);
			if(logger.isDebugEnabled()){
				logger.debug("OriginalCalledPartyID decoded successfuly.");
			}
		return ocpId;
	}
	
	/**
	 * encode OriginalCalledPartyIDCapV2 non asn object into byte array of OriginalCalledPartyID
	 * @param ocpId
	 * @return byte array of OriginalCalledPartyID
	 * @throws InvalidInputException
	 */
	public static byte[] encode(OriginalCalledPartyIdCapV2 ocpId)  throws InvalidInputException{
		byte[] bytes=null;
			byte b0 = (byte)((ocpId.oddEvenInd.getCode()<<7)+ocpId.natureAddInd.getCode());
			byte b1 = (byte) (((ocpId.numPlanInd.getCode()<<2)+ocpId.addPreResInd.getCode())<<2);
			byte[] addresSignal = AddressSignalCapV2.encode(ocpId.addressSignal);
			int bytesLength = addresSignal.length+2;
			bytes = new byte[bytesLength];
			bytes[0] = b0;
			bytes[1] = b1;
			for(int i=2;i<bytesLength;i++){
				bytes[i] = addresSignal[i-2];
			}
		return bytes;
	}
}
