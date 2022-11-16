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
 * Enum for Additional Party's Category Name
 * @author Mriganka
 *
 */
public enum AdtnlPartyCatNameEnum {
	/**
	 * 0-spare
	 * 1->128-reserved for national use
	 * 251-mobile additional party's category 3
	 * 252-mobile additional party's category 2
	 * 253-mobile additional party's category 1
	 * 254-PSTN additional party's category 1
	 */
	SPARE(0), RESERVED(1), MOBILE_CATEGORY_3(251) , MOBILE_CATEGORY_2(252), MOBILE_CATEGORY_1(253), PSTN_CATEGORY_1(254);
	private int code;
	private AdtnlPartyCatNameEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}		public static AdtnlPartyCatNameEnum fromInt(int num) {
		switch (num) {			case 0: { return SPARE; }
			case 1: { return RESERVED; }
			case 251: { return MOBILE_CATEGORY_3; }
			case 252: { return MOBILE_CATEGORY_2; }
			case 253: { return MOBILE_CATEGORY_1; }
			case 254: { return PSTN_CATEGORY_1; }
			default: { return null; }
		}
	}
}
