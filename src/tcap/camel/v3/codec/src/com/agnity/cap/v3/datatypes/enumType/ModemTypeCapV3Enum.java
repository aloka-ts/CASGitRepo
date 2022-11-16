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

public enum ModemTypeCapV3Enum {

	/**
	 * 0- National use
	 * 17- Recommendation V.21
	 * 18- Recommendation V.22
	 * 19- Recommendation V.22 bis
	 * 20- Recommendation V.23
	 * 21- Recommendation V.26
	 * 22- Recommendation V.26 bis
	 * 23- Recommendation V.26 ter
	 * 24- Recommendation V.27
	 * 25- Recommendation V.27 bis
	 * 26- Recommendation V.27 ter
	 * 27- Recommendation V.29
	 * 29- Recommendation V.32
	 * 30- Recommendation V.34
	 * 48- User specified
	 */
	NATIONAL_USE(0), RECOMMEND_V_21(17), RECOMMEND_V_22(18), RECOMMEND_V_22_BIS(19), RECOMMEND_V_23(20), RECOMMEND_V_26(21), RECOMMEND_V_26_BIS(22)
	, RECOMMEND_V_26_TER(23), RECOMMEND_V_27(24), RECOMMEND_V_27_BIS(25), RECOMMEND_V_27_TER(26), RECOMMEND_V_29(27), RECOMMEND_V_32(29), RECOMMEND_V_34(30)
	, USER_SPECIFIED(48);
	
	private int code;

	private ModemTypeCapV3Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ModemTypeCapV3Enum getValue(int num) {
		switch (num) {
		case 0: { return 	NATIONAL_USE	; }
		case 17: { return 	RECOMMEND_V_21	; }
		case 18: { return 	RECOMMEND_V_22	; }
		case 19: { return 	RECOMMEND_V_22_BIS	; }
		case 20: { return 	RECOMMEND_V_23	; }
		case 21: { return 	RECOMMEND_V_26	; }
		case 22: { return 	RECOMMEND_V_26_BIS	; }
		case 23: { return 	RECOMMEND_V_26_TER	; }
		case 25: { return 	RECOMMEND_V_27	; }
		case 26: { return 	RECOMMEND_V_27_BIS	; }
		case 27: { return 	RECOMMEND_V_27_TER	; }
		case 29: { return 	RECOMMEND_V_29	; }
		case 30: { return 	RECOMMEND_V_32	; }
		case 48: { return 	RECOMMEND_V_34	; }		
		default: { return null; }
		}
	}

}
