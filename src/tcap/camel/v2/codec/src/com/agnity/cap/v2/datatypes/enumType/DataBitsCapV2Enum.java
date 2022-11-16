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

public enum DataBitsCapV2Enum {

	/**
	 * 0- Not used
	 * 1- 5 bit
	 * 2- 7 bits
	 * 3- 8 bits
	 */
	NOT_USED(0), BIT_5(1), BIT_7(2), BIT_8(3);
	
	private int code;

	private DataBitsCapV2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static DataBitsCapV2Enum getValue(int num) {
		switch (num) {
		case 0: { return 	NOT_USED	; }
		case 1: { return 	BIT_5	; }
		case 2: { return 	BIT_7	; }
		case 3: { return 	BIT_8	; }
		default: { return null; }
		}
	}
}
