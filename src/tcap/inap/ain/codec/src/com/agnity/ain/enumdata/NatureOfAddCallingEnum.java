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
 * Enum for Nature of address indicator
 * @author Nishant
 *
 */
public enum NatureOfAddCallingEnum {
	/**
	 * 0-spare
	 * 1-subscriber number (national use)
	 * 2-spare (national use)
	 * 3-national (significant) number (national use)
	 * 4-international number
	 * 5->112-reserved for national use	 	 * 113->non-unique subscriber number	 	 * 114-spare	 	 * 115->non-unique national (significant)number	 	 * 116->non-unique international number	 	 * 117->118-spare	 	 * 119-Test line test code   
	 * 120->126 Reserved for network specific use
	 * 127-spare
	 */
	SPARE(0), SUBS_NO(1), UNKNOWN(2), NATIONAL_NO(3), INTER_NO(4),NON_SUBS_NO(113),NON_UNIQUE_NATIONAL_NO(115),NON_UNIQUE_INTER_NO(116),TEST_CODE(119);
	private int code;
	private NatureOfAddCallingEnum(int c) {
		code = c;
	}

	public int getCode() {		return code;
	}
	public static NatureOfAddCallingEnum fromInt(int num) {
		switch (num) {
		case 0: { return SPARE; }
		case 1: { return SUBS_NO; }
		case 2: { return UNKNOWN; }
		case 3: { return NATIONAL_NO; }
		case 4: { return INTER_NO; }
		case 113: { return NON_SUBS_NO; }
		case 115: { return NON_UNIQUE_NATIONAL_NO;}
		case 116: { return NON_UNIQUE_INTER_NO; }				case 119: { return TEST_CODE; }
		default: { return null; }
		}
	}
}
