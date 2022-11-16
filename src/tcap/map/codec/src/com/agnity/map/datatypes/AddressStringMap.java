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

package com.agnity.map.datatypes;


import org.apache.log4j.Logger;

import com.agnity.map.enumdata.ExtentionMapEnum;
import com.agnity.map.enumdata.NatureOfAddressIndicatorMapEnum;
import com.agnity.map.enumdata.NatureOfAddressMapEnum;
import com.agnity.map.enumdata.NumberPlanMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.MapFunctions;
/**
 * 
 * @author sanjay
 * mscAddress non-asn decoding & encoding
 */
public class AddressStringMap {

	 protected ExtentionMapEnum extention;
	 protected NatureOfAddressMapEnum natureOfAddr;
	 protected NumberPlanMapEnum numberPlan;
	 protected String addressDigits;
	 protected int countryCode;

	 private static Logger logger = Logger.getLogger(AddressStringMap.class);
	 
	 /**
	 * 
	 */
	public AddressStringMap() {
	}

	 /**
	 * @throws InvalidInputException 
	 * 
	 */
	public AddressStringMap(byte[] address) throws InvalidInputException {
		decode(address);
	}

	
	/**
	  * 
	  * @return ExtentionCapV2Enum
	  */
	 public ExtentionMapEnum getExtention() {
		return extention;
	}

	/**
	 * 
	 * @return NatureOfAddressIndicatorMapEnum
	 */
	public NatureOfAddressMapEnum getNai() {
		return natureOfAddr;
	}
    
	/**
	 * 
	 * @return NumberPlanMapEnum
	 */
	public NumberPlanMapEnum getNumberPlan() {
		return numberPlan;
	}

	/**
	 * 
	 * @return decode hex string of addressDigits
	 */
	public String getAddressDigits() {
		return addressDigits;
	}
	
    /**
     * 
     * @return countryCode
     */
    public int getCountryCode() {
            return countryCode;
    }


	
	/**
	 * 
	 * @param extention
	 */
	public void setExtention(ExtentionMapEnum extention) {
		this.extention = extention;
	}

	/**
	 * 
	 * @param natureOfAddr
	 */
	public void setNatureOfNumber(NatureOfAddressMapEnum natureOfAddr) {
		this.natureOfAddr = natureOfAddr;
	}

	/**
	 * 
	 * @param numberPlan
	 */
	public void setNumberPlan(NumberPlanMapEnum numberPlan) {
		this.numberPlan = numberPlan;
	}

	/**
	 * 
	 * @param addressDigits
	 */
	public void setAddressDigits(String addressDigits) {
		this.addressDigits = addressDigits;
	}

   
	/**
	 * decode AddressString byte array into non-asn object of AddressStringMap
	 * @param byte array of mscAddress
	 * @return void
	 */
	public void decode(byte[] bytes)  throws InvalidInputException{
		this.extention = ExtentionMapEnum.getValue((bytes[0]>>7)&0x01);
		this.natureOfAddr = NatureOfAddressMapEnum.getValue((bytes[0]&0x7F)>>4);
		this.numberPlan = NumberPlanMapEnum.getValue(bytes[0]&0x0F);
		this.addressDigits = MapFunctions.decodeNumber(bytes,1);
		this.countryCode = Integer.parseInt(MapFunctions.decodeNumber(new byte[]{bytes[0],bytes[1]},1));

		if(logger.isDebugEnabled()){
			logger.debug("Address non-asn successfully decoded.");
		}
	}
	
	/**
	 * encode non-asn AddressStringMap object into byte array
	 * @return byteArray of address
	 */
	public byte[] encode()  throws InvalidInputException{
		byte[] bytes = null;
		int ext = this.extention.getCode();
		int natOfAddr = this.natureOfAddr.getCode();
		int numPlan = this.numberPlan.getCode();
		String adddigits = this.addressDigits; 

		if(logger.isDebugEnabled()){
			logger.debug("Encoding : " + this);
		}
		
		byte b0 = (byte)((((ext<<3)+natOfAddr)<<4)+numPlan);
		
		adddigits = MapFunctions.encodenNumber(adddigits);
		byte[] adddigitsBytes = MapFunctions.hexStringToByteArray(adddigits);
		int totalLength = adddigitsBytes.length+1; 
		bytes = new byte[totalLength];
		bytes[0]=b0;
		for(int i=0;i<(totalLength-1);i++){
			bytes[i+1] = adddigitsBytes[i];
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("Address byte array encoded successfully");
			logger.debug("Address byte array encoded length::"+bytes.length);
		}
			
		return bytes;
	}
	
	@Override
	public String toString() {
		String bl = "\n";
		return  "Extention "+ extention+bl+
		        "NatureOfNumber "+ natureOfAddr+bl+
		        "NumberPlan "+ numberPlan+bl+
		       "addressDigits "+addressDigits+bl;
	}
}
