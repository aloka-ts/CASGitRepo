/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
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


package com.agnity.map.datatypes;

import org.apache.log4j.Logger;

import com.agnity.map.enumdata.SupplementaryServicesMapEnum;
import com.agnity.map.exceptions.InvalidInputException;
import com.agnity.map.util.Util;

/**
 * 
 * @author sanjay
 *
 * This type is used to represent the code identifying a single
 * supplementary service, a group of supplementary services, or
 * all supplementary services. The services and abbreviations
 * used are defined in TS 3GPP TS 22.004 [5]. The internal structure is
 * defined as follows:
 * 
 * bits 87654321: group (bits 8765), and specific service
 * (bits 4321)

 */
public class SsCodeMap {
	
	private static Logger logger = Logger.getLogger(SsCodeMap.class);

	private SupplementaryServicesMapEnum ssCode;
	
	public SsCodeMap(SupplementaryServicesMapEnum sscode) {
		this.ssCode = sscode;
	}

	public SsCodeMap() {
        }
	
	/**
	 * 
	 * @return Supplementary Service Code Enum
	 */
	
	public SupplementaryServicesMapEnum getSsCode() {
		return this.ssCode;
	}
	
	/**
	 * @param code
	 */
	public void setSsCode(SupplementaryServicesMapEnum code) {
		this.ssCode = code;
	}
	
	/**
	 * decode SsCodeMap bytes array in non-asn object of
	 *  SsCodeMap
	 * @param bytes
	 * @return SsCodeMap object
	 */
	public static SsCodeMap decode(byte[] bytes) throws InvalidInputException{
		
		if(logger.isDebugEnabled()){
			logger.debug("In decode, SsCodeMap bytes array length::"+bytes.length);
		}
		
		SsCodeMap ssCodeObj = new SsCodeMap(SupplementaryServicesMapEnum.getValue(bytes[0]));
		
		if(logger.isDebugEnabled()){
			logger.debug("SsCodeMap non asn succesfully decoded.");
		}
		return ssCodeObj;
	}	
	
	/**
	 * encode SsCodeMap non asn into byte array of 
	 * SsCodeMap
	 * @param SsCodeMap object
	 * @return byte array of SsCodeMap
	 */
	public static byte[] encode(SsCodeMap ssCodeObj) throws InvalidInputException{
		byte[] bytes= null;
		
		byte b0 = (byte)ssCodeObj.ssCode.getCode();
		bytes = new byte[]{b0};
		
		if(logger.isDebugEnabled()){
			logger.debug("SsCodeMap non asn succesfully encoded. byte array length:"+bytes.length);
		}
		return bytes;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SsCodeMap [ssCode=" + ssCode + "]";
	}
	
	

}
