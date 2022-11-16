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

package com.agnity.map.enumdata;

/**
 * 
 * @author sanjay
 * 3GPP TS 29.060
 * 
 * closed mode (0) decimal
 * Hybrid mode (1)
 * Reserved    3, 4
 * 
 *
 */
public enum AccessModeMapEnum {
	
	CLOSED(0), HYBRID(1), RESERVED_2(2), RESERVED_3(3);
	
	int code;
	
	private AccessModeMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static AccessModeMapEnum getValue(int tag){
		switch (tag) {
		case 0: return CLOSED; 
		case 1: return HYBRID; 
		case 2: return RESERVED_2; 
		case 3: return RESERVED_3;
		default: return null;
		}
	}

}
