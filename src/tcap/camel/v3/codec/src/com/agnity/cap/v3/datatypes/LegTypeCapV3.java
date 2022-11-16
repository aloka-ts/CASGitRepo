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

import com.agnity.cap.v3.datatypes.enumType.LegTypeCapV3Enum;
import com.agnity.cap.v3.exceptions.InvalidInputException;

/**
 * 
 * @author rnarayan
 * ref: ETSI TS 101 046 V7.1.0 
 */
public class LegTypeCapV3 {
 
	private LegTypeCapV3Enum legType;
	
	public void setLegType(LegTypeCapV3Enum legType) {
		this.legType = legType;
	}
	
	public LegTypeCapV3Enum getLegType() {
		return legType;
	}
	
	public static LegTypeCapV3 decode(byte[] bytes) throws InvalidInputException {
		LegTypeCapV3 lt = new LegTypeCapV3();
		lt.legType = LegTypeCapV3Enum.getValue(bytes[0]& 0x03);
		return lt;
		
	}
	
	public static byte[] encode(LegTypeCapV3 lt) throws InvalidInputException{
	    byte[] bytes = null;
	    int ltCode =  lt.legType.getCode();
	    byte b = (byte)(ltCode&0x03);
	    bytes = new byte[]{b};
	    return bytes;
	}
	
	
}

