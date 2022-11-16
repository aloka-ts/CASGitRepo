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
/*
 * ref: 3GPP TS 29.002 [13]
Alerting Pattern value (octet 3) 
Bits 
4 3 2 1 
 
0 0 0 0 alerting pattern 1 
0 0 0 1 alerting pattern 2 
0 0 1 0 alerting pattern 3 
 
0 1 0 0 alerting pattern 5 
0 1 0 1 alerting pattern 6 
0 1 1 0 alerting pattern 7 
0 1 1 1 alerting pattern 8 
1 0 0 0 alerting pattern 9 
 
all other values are reserved 

 */
public enum AlertingPatternCapV3Enum {

	ALERTING_PATTERN_1(0),ALERTING_PATTERN_2(1),ALERTING_PATTERN_3(2),
	ALERTING_PATTERN_5(4),ALERTING_PATTERN_6(5),ALERTING_PATTERN_7(6),
	ALERTING_PATTERN_8(7),ALERTING_PATTERN_9(8);
	
	private int code;
	private AlertingPatternCapV3Enum(int code) {
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static AlertingPatternCapV3Enum getValue(int tag){
		switch (tag) {
		case 0: return ALERTING_PATTERN_1; 
		case 1: return ALERTING_PATTERN_2; 
		case 2: return ALERTING_PATTERN_3; 
		case 4: return ALERTING_PATTERN_5; 
		case 5: return ALERTING_PATTERN_6; 
		case 6: return ALERTING_PATTERN_7; 
		case 7: return ALERTING_PATTERN_8; 
		case 8: return ALERTING_PATTERN_9; 
		default: return null;
		}
	}
}
