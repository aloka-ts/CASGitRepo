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
package com.agnity.cap.v2.datatypes.enumType;

public enum ExtentionIndicatorCapV2Enum {

	NEXT_OCTET(0),LAST_OCTET(1);
	
	private int code;
	private ExtentionIndicatorCapV2Enum(int code){
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static ExtentionIndicatorCapV2Enum getValue(int tag){
		switch (tag) {
		case 0: return NEXT_OCTET; 
		case 1: return LAST_OCTET; 
		default: return null;
		}
	}
}
