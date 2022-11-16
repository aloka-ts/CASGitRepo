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

import com.agnity.cap.v2.util.DataType;

public enum NIIndicatorCapV2Enum {
	COMPLETE(0), INCOMPLETE(1);
	
	private int code;
	private NIIndicatorCapV2Enum(int code){
		this.code = code;
	}
	public int getCode() {
		return code;
	}
	
	
	public static NIIndicatorCapV2Enum getValue(int tag){
		switch (tag) {
		case 0: return COMPLETE; 
		case 1: return INCOMPLETE; 
		default: return null;
		}
	}

}
