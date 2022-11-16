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


public enum CallingPartyCategoryCapV2Enum {

	/*
	The following codes are used in the calling party's category parameter field. 
	0 0 0 0 0 0 0 0 calling party's category unknown at this time (national use) 
	0 0 0 0 0 0 0 1 operator, language French 
	0 0 0 0 0 0 1 0 operator, language English 
	0 0 0 0 0 0 1 1 operator, language German 
	0 0 0 0 0 1 0 0 operator, language Russian 
	0 0 0 0 0 1 0 1 operator, language Spanish 
	----------------
	0 0 0 0 0 1 1 0
	0 0 0 0 0 1 1 1
	0 0 0 0 1 0 0 0(available to Administrations for selection a particular language by mutual agreement) 
	---------------
	0 0 0 0 1 0 0 1 reserved (see ITU-T Recommendation Q.104) (Note) (national use) 
	0 0 0 0 1 0 1 0 ordinary calling subscriber 
	0 0 0 0 1 0 1 1 calling subscriber with priority 
	 
	0 0 0 0 1 1 0 0 data call (voice band data) 
	0 0 0 0 1 1 0 1 test call 
	0 0 0 0 1 1 1 0 spare 
	0 0 0 0 1 1 1 1 payphone
	---------------- 
	0 0 0 1 0 0 0 0
	to
	1 1 0 1 1 1 1 1 spare
	---------------- 
	----------------
	1 1 1 0 0 0 0 0
	to
	1 1 1 1 1 1 1 0 reserved for national use 
	-----------------
	1 1 1 1 1 1 1 1 spare 
	*/
	
	UNKNOWN(0), FRENCH_OPER_LANG(1), ENGLISH__OPER_LANG(2),GERMAN_OPER_LANG(3),
	RUSSIAN_OPER_LANG(4),SPANISH_OPER_LANG(5), RESERVED(9), ORDINARY_CALLING_SUBSCRIBER(10),
	CALLING_SUBSCRIBER_PRIORITY(11), DATA_CALL(12), TEST_CALL(13), SPARE(14), PAYPHONE(15);
	
	private int code;
	private CallingPartyCategoryCapV2Enum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static CallingPartyCategoryCapV2Enum getValue(int tag) {
	     switch (tag) {
		case 0: return UNKNOWN;
		case 1: return FRENCH_OPER_LANG;
		case 2: return ENGLISH__OPER_LANG;
		case 3: return GERMAN_OPER_LANG;
		case 4: return RUSSIAN_OPER_LANG;
		case 5: return SPANISH_OPER_LANG;
		case 9: return RESERVED;
		case 10: return ORDINARY_CALLING_SUBSCRIBER;
		case 11: return CALLING_SUBSCRIBER_PRIORITY;
		case 12: return DATA_CALL;
		case 13: return TEST_CALL;
		case 14: return SPARE;
		case 15: return PAYPHONE;			
		default: return null;
		}

	}
	

}
