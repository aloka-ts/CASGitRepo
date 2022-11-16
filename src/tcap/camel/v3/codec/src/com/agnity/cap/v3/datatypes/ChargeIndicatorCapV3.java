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
package com.agnity.cap.v3.datatypes;

import java.util.Arrays;

import org.apache.log4j.Logger;

import com.agnity.cap.v3.datatypes.enumType.ChargeIndicatorCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;
import com.agnity.cap.v3.util.CapFunctions;

/**
 * 
 * @author rnarayan
 * ref: Q763
 */
public class ChargeIndicatorCapV3 {

	private ChargeIndicatorCapV3Enum chargeIndicator;
	private static Logger logger = Logger.getLogger(ChargeIndicatorCapV3.class);

	/**
	 * 
	 * @return ChargeIndicatorCapV3Enum value
	 */
	public ChargeIndicatorCapV3Enum getChargeIndicator() {
		return chargeIndicator;
	}
	
	/**
	 * set chargeIndicator
	 * @param ChargeIndicatorCapV3Enum type
	 */
	public void setChargeIndicator(ChargeIndicatorCapV3Enum chargeIndicator) {
		this.chargeIndicator = chargeIndicator;
	}
	
	/**
	 * decode charge Indicator
	 * @param bytes
	 * @return object of ChargeIndicatorCapV3
	 * @throws InvalidInputException
	 */
	public static ChargeIndicatorCapV3 decode(byte[] bytes)  throws InvalidInputException{
		if(logger.isDebugEnabled()){
			logger.debug("ChargeIndicator bytes length ::"+bytes.length);
		}
		ChargeIndicatorCapV3 ci = new ChargeIndicatorCapV3();
		ci.chargeIndicator = ChargeIndicatorCapV3Enum.getValue(bytes[0]& 0x03);
		if(logger.isDebugEnabled()){
			logger.debug("ChargeIndicator successfully decoded.");
		}
		return ci;
	}
	
	/**
	 * encode charge Indicator
	 * @param ci
	 * @return byte array of chargeIndicator
	 * @throws InvalidInputException
	 */
	public static byte[] encode(ChargeIndicatorCapV3 ci)  throws InvalidInputException{
		byte[] bytes = null;
		int chargeIndicator = ci.chargeIndicator.getCode();
		byte b1 = (byte)(chargeIndicator&0x03);
		bytes = new byte[]{b1};
		
		if(logger.isDebugEnabled()){
			logger.debug("ChargeIndicator successfully encoded");
		}
		return bytes;
	}
	
	
	
	//for test
	public static void main(String[] args) throws InvalidInputException {
		byte bytes[] = CapFunctions.hexStringToByteArray("01");
		byte exBytes[] = encode(decode(bytes));
		System.out.println(Arrays.equals(bytes, exBytes));
	}
	
}
