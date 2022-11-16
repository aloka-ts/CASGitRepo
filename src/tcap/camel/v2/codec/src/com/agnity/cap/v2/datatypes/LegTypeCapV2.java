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

import com.agnity.cap.v2.datatypes.enumType.LegTypeCapV2Enum;
import com.agnity.cap.v2.exceptions.InvalidInputException;

/**
 * 
 * @author rnarayan
 * ref: ETSI TS 101 046 V7.1.0 
 */
public class LegTypeCapV2 {
 
	private LegTypeCapV2Enum legType;
	
	public void setLegType(LegTypeCapV2Enum legType) {
		this.legType = legType;
	}
	
	public LegTypeCapV2Enum getLegType() {
		return legType;
	}
	
	public static LegTypeCapV2 decode(byte[] bytes) throws InvalidInputException {
		LegTypeCapV2 lt = new LegTypeCapV2();
		lt.legType = LegTypeCapV2Enum.getValue(bytes[0]& 0x03);
		return lt;
		
	}
	
	public static byte[] encode(LegTypeCapV2 lt) throws InvalidInputException{
	    byte[] bytes = null;
	    int ltCode =  lt.legType.getCode();
	    byte b = (byte)(ltCode&0x03);
	    bytes = new byte[]{b};
	    return bytes;
	}
	
	
}

