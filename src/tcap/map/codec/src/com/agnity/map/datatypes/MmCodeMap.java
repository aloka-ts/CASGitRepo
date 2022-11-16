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

import com.agnity.map.enumdata.MmCodeMapEnum;
import com.agnity.map.exceptions.InvalidInputException;

/**
 * Class to represent Mobility Management Event Codes
 * @author sanjay
 *
 */
public class MmCodeMap {

	private static Logger logger = Logger.getLogger(MmCodeMap.class);
	private MmCodeMapEnum code;
	
	public void setMmCode(MmCodeMapEnum code) {
		this.code = code;
	}
	
	public MmCodeMapEnum getMmCode() {
		return this.code;
	}

	public static MmCodeMap decode(byte[] bytes) throws InvalidInputException{
	    if(logger.isDebugEnabled()){
	        logger.debug("in decode. MmCodeMap byte array length::"+bytes.length);
	    }
	    MmCodeMap retObj = new MmCodeMap();
	    retObj.code = MmCodeMapEnum.getValue(bytes[0]);

	    if(logger.isDebugEnabled()){
	        logger.debug("MmCodeMap non asn decoded successfully.");
	    }
	    return retObj;
	}

	public static byte[] encode(MmCodeMap mmCodeObj) throws InvalidInputException {
		   byte[] bytes = null;
	       byte b0 = (byte)mmCodeObj.code.getCode();
	       bytes = new byte[]{b0};
	       
	       if(logger.isDebugEnabled()){
	           logger.debug("MmCodeMap non asn encoded successfully. byte array length:"+bytes.length);
	       }
	       return bytes;
	}
	
	
}
