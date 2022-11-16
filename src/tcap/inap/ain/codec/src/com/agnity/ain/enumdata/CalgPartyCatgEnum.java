/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.agnity.ain.enumdata;
/**
 * Enum for Calling Party Category
 * @author Mriganka
 *
 */
public enum CalgPartyCatgEnum {
	/**
	 *  0-calling party�s category unknown at this time (national use)
	 *  1-operator, language French
	 *  2-operator, language English
	 *  3-operator, language German
	 *  4-operator, language Russian
	 *  5-operator, language Spanish
	 *  6,7,8-(available to Administrations for selection a particular language by mutual agreement
	 *  9-reserved (see Recommendation Q.104) (Note) (national use)
	 *  10-ordinary calling subscriber
	 *  11-calling subscriber with priority
	 *  12-data call (voice band data)
	 *  13-test call
	 *  14-spare
	 *  15-payphone
	 *  16->223-spare
	 *  223-254-reserved for national use
	 *  255-spare
	 */
	UNKNOWN(0), OPRT_FRENCH(1), OPRT_ENG(2),OPRT_GERMAN(3),OPRT_RUSSIAN(4),OPRT_SPANISH(5),ORD_SUBSR(10),SUBSR_PROTY(11),
	DATA_CALL(12),TEST_CALL(13), SPARE(14), PAYPHONE(15);
	private int code;
	private CalgPartyCatgEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static CalgPartyCatgEnum fromInt(int num) {
		switch (num) {
			case 0: { return UNKNOWN; }
			case 1: { return OPRT_FRENCH; }
			case 2: { return OPRT_ENG; }
			case 3: { return OPRT_GERMAN; }
			case 4: { return OPRT_RUSSIAN; }
			case 5: { return OPRT_SPANISH; }
			case 10: { return ORD_SUBSR; }
			case 11: { return SUBSR_PROTY; }
			case 12: { return DATA_CALL; }
			case 13: { return TEST_CALL; }
			case 14: { return SPARE; }
			case 15: { return PAYPHONE; }
			default: { return null; }
		}
	}
}
