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

public enum ParityInfoCapV3Enum {

	/**
	 * 0- Odd
	 * 2- Even
	 * 3- NONE
	 * 4- Forced to 0
	 * 5- Forced to 1
	 */
	ODD(0), EVEN(2), NONE(3), FORCED_0(4), FORCED_1(5);
	
	private int code;

	private ParityInfoCapV3Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ParityInfoCapV3Enum getValue(int num) {
		switch (num) {
		case 0: { return 	ODD	; }
		case 2: { return 	EVEN	; }
		case 3: { return 	NONE	; }
		case 4: { return 	FORCED_0	; }
		case 5: { return 	FORCED_1	; }		
		default: { return null; }
		}
	}
}
