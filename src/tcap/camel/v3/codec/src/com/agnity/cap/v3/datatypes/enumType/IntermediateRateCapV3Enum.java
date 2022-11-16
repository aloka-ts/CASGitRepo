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
/**
 * 
 * @author rnarayan
 *
 */
public enum IntermediateRateCapV3Enum {
	/**
	 * 0- Not used
	 * 1- 8 kbit/s
	 * 2- 16 kbit/s
	 * 3- 32 kbit/s
	 */
	NOT_USED(0), KBITS_8(1), KBITS_16(2), KBITS_32(3);
	
	private int code;

	private IntermediateRateCapV3Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static IntermediateRateCapV3Enum getValue(int num) {
		switch (num) {
		case 0: { return 	NOT_USED	; }
		case 1: { return 	KBITS_8	; }
		case 2: { return 	KBITS_16	; }
		case 3: { return 	KBITS_32	; }
		default: { return null; }
		}
	}
}
