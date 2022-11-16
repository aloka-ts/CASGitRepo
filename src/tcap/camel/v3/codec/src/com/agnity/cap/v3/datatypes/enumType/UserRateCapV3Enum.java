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
public enum UserRateCapV3Enum {
 

	/**
	 * '_' (except first) will be replaced by '.' in enum values
	 * 
	 * 0- Unspecified
	 * 1- 0.6 kbit/s
	 * 2- 1.2 kbit/s
	 * 3- 2.4 kbit/s
	 * 4- 3.6 kbit/s
	 * 5- 4.8 kbit/s
	 * 6- 7.2 kbit/s
	 * 7- 8 kbit/s
	 * 8- 9.6 kbit/s
	 * 9- 14.4 kbit/s
	 * 10- 16 kbit/s
	 * 11- 19.2 kbit/s
	 * 12- 32 kbit/s
	 * 13- 38.4 kbit/s
	 * 14- 48 kbit/s
	 * 15- 56 kbit/s
	 * 18- 57.6 kbit/s
	 * 19- 28.8 kbit/s
	 * 20- 24 kbit/s
	 * 21- 0.1345 kbit/s
	 * 22- 0.100 kbit/s
	 * 23- 0.075/1.26 kbit/s
	 * 24- 1.2/0.075 kbit/s
	 * 25- 0.050 kbit/s
	 * 26- 0.075 kbit/s
	 * 27- 0110 kbit/s
	 * 28- 0.150 kbit/s
	 * 29- 0.200 kbit/s
	 * 30- 0.300 kbit/s
	 * 31- 12 kbit/s
	 */
	
	UNSPECIFIED(0), KBITS_0_6(1), KBITS_1_2(2), KBITS_2_4(3), KBITS_3_6(4), KBITS_4_8(5), KBITS_7_2(6), KBITS_8(7), KBITS_9_6(8), KBITS_14_4(9), 
	KBITS_16(10), KBITS_19_2(11), KBITS_32(12), KBITS_38_4(13), KBITS_48(14), KBITS_56(15), KBITS_57_6(18), KBITS_28_8(19), KBITS_24(20), 
	KBITS_0_1345(21), KBITS_0_100(22), KBITS_0_075_1_2(23), KBITS_1_2_0_075(24), KBITS_0_05(25), KBITS_0_75(26), KBITS_0_110(27), KBITS_0_150(28), 
	KBITS_0_200(29), KBITS_0_300(30), KBITS_12(31);
	
	private int code;

	private UserRateCapV3Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static UserRateCapV3Enum getValue(int num) {
		switch (num) {
		case 0: { return 	UNSPECIFIED	; }
		case 1: { return 	KBITS_0_6	; }
		case 2: { return 	KBITS_1_2	; }
		case 3: { return 	KBITS_2_4	; }
		case 4: { return 	KBITS_3_6	; }
		case 5: { return 	KBITS_4_8	; }
		case 6: { return 	KBITS_7_2	; }
		case 7: { return 	KBITS_8	; }
		case 8: { return 	KBITS_9_6	; }
		case 9: { return 	KBITS_14_4	; }
		case 10: { return 	KBITS_16	; }
		case 11: { return 	KBITS_19_2	; }
		case 12: { return 	KBITS_32	; }
		case 13: { return 	KBITS_38_4	; }
		case 14: { return 	KBITS_48	; }
		case 15: { return 	KBITS_56	; }
		case 18: { return 	KBITS_57_6	; }
		case 19: { return 	KBITS_28_8	; }
		case 20: { return 	KBITS_24	; }
		case 21: { return 	KBITS_0_1345	; }
		case 22: { return 	KBITS_0_100	; }
		case 23: { return 	KBITS_0_075_1_2	; }
		case 24: { return 	KBITS_1_2_0_075	; }
		case 25: { return 	KBITS_0_05	; }
		case 26: { return 	KBITS_0_75	; }
		case 27: { return 	KBITS_0_110	; }
		case 28: { return 	KBITS_0_150	; }
		case 29: { return 	KBITS_0_200	; }
		case 30: { return 	KBITS_0_300	; }
		case 31: { return 	KBITS_12	; }		
		default: { return null; }
		}
	}
	
}
