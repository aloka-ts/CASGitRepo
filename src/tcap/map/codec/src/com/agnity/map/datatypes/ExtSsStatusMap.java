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

import com.agnity.map.enumdata.ExtSsCodeMapEnum;
import com.agnity.map.exceptions.InvalidInputException;

/**
 * 
 * @author sanjay
 *
 *Ext-SS-Status ::= OCTET STRING (SIZE (1..5))
 * 
 * OCTET 1:
 * 
 * bits 8765: 0000 (unused)
 * bits 4321: Used to convey the "P bit","R bit","A bit" and "Q bit",
 * representing supplementary service state information
 * as defined in TS 3GPP TS 23.011 [22]
 * 
 * 
 * bit 4: "Q bit"
 * bit 3: "P bit"
 * bit 2: "R bit"
 * bit 1: "A bit"     
 * 
 * OCTETS 2-5: reserved for future use. They shall be discarded if
 * received and not understood.

 */
public class ExtSsStatusMap {
	
	private static Logger logger = Logger.getLogger(ExtSsStatusMap.class);

	private byte[] codeData;
	
	/**
	 * Set Ext SS Bit in the Code
	 * @param bit
	 */
	
	public void setExtSsCode(ExtSsCodeMapEnum bit){
		this.codeData[0] |= 0x01 << (bit.getCode() - 1);
	}
	
	/**
	 * Get Activation Indicator
	 * @return
	 */
	
	public boolean getBitA() {
		return (this.codeData[0] & 0x01) > 0;
	}
	
	/**
	 * Get Registration Indicator
	 * @return
	 */
	public boolean getBitR() {
		return (this.codeData[0] & 0x02) > 0;
	}
	
	/**
	 * Get Provision Indicator
	 * @return
	 */
	public boolean getBitP() {
		return (this.codeData[0] & 0x04) > 0;
	}
	
	/**
	 * Get Bit representing Quiescent/Operative Ind
	 * @return
	 */
	public boolean getBitQ() {
		return (this.codeData[0] & 0x08) > 0;
	}
	
	
	/**
	 * decode ExtSsCodeMap bytes array in non-asn object of
	 *  ExtSsCodeMap
	 * @param bytes
	 * @return ExtSsCodeMap object
	 */
	public static ExtSsStatusMap decode(byte[] bytes) throws InvalidInputException{
		if(logger.isDebugEnabled()){
			logger.debug("In decode, ExtSsCodeMap bytes array length::"+bytes.length);
		}
		
		ExtSsStatusMap code = new ExtSsStatusMap();
		
		code.codeData = new byte[]{bytes[0]};
		
		if(logger.isDebugEnabled()){
			logger.debug("ExtSsCodeMap non asn succesfully decoded.");
		}
		return code;
	}

	/**
	 * encode ExtSsCodeMap non asn into byte array of 
	 * ExtSsCodeMap
	 * @param ExtSsStatusMap object
	 * @return byte array of ExtSsCodeMap
	 */
	public static byte[] encode(ExtSsStatusMap extCode) throws InvalidInputException{
		byte[] bytes= null;
		
		bytes = extCode.codeData.clone();
		
		if(logger.isDebugEnabled()){
			logger.debug("SsCodeMap non asn succesfully encoded. byte array length:"+bytes.length);
		}
		return bytes;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Bit A(Activation Indicator) = ").append(getBitA()).append("\n");
		sb.append("Bit P(Provision Indicator)  = ").append(getBitP()).append("\n");
		sb.append("Bit Q(Quiescent/Operative Indicator) = ").append(getBitQ()).append("\n");
		sb.append("Bit R(Registration Indicator) = ").append(getBitR()).append("\n");
		return sb.toString();
	}
}
