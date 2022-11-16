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
package com.agnity.cap.v3.datatypes.enumType;

public enum InterpretationCapV3Enum {
	
	/*
	 *  Bits
		5 4 3
		1 0 0 First (primary or only) high layer characteristics identification (in octet 4) to be used in the 
		call
		All other values are reserved.
		NOTE 2 – "Interpretation" indicates how the "High layer characteristics identification" (in octet 4) should
		be interpreted.
	 */
	
	FIRST_HIGH_LAYER_CHAR_IDEN(4);
	
	int code;
	
	private InterpretationCapV3Enum(int code){
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static InterpretationCapV3Enum getValue(int tag){
		switch (tag) {
		case 4: return FIRST_HIGH_LAYER_CHAR_IDEN;
		default:return null;
		}
	}

}
