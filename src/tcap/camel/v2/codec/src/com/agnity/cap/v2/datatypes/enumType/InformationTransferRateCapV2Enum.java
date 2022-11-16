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


public enum InformationTransferRateCapV2Enum {
	
	/**
	 * 0- This code shall be used for packet-mode calls
	 * 16- 64 kbit/s
	 * 17- 2*64 kbit/s
	 * 19- 384 kbit/s
	 * 21- 1536 kbit/s
	 * 23- 1920 kbit/s
	 * 24- Multirate
	 */
	PACKET_MODE_CALLS(0), KBITS_64(16), KBITS_2_64(17), KBITS_384(19), KBITS_1536(21), KBITS_1920(23), MULTIRATE(24);
	
	private int code;

	private InformationTransferRateCapV2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static InformationTransferRateCapV2Enum getValue(int num) {
		switch (num) {
		case 0: { return 	PACKET_MODE_CALLS	; }
		case 16: { return 	KBITS_64	; }
		case 17: { return 	KBITS_2_64	; }
		case 19: { return 	KBITS_384	; }
		case 21: { return 	KBITS_1536	; }
		case 23: { return 	KBITS_1920	; }
		case 24: { return 	MULTIRATE	; }
		default: { return null; }
		}
	}
}
