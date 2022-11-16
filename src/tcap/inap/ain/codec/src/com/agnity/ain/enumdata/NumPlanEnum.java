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
 * Enum for numbering plan
 * @author nishantsharma
 *
 */
public enum NumPlanEnum {
	/**
	 * 0-spare
	 * 1-ISDN (Telephony) numbering plan (Recommendation E.164)
	 * 2->4-reserved for national use
	 * 5- Private Numbering Plan
	 * 6-reserved for national use
	 * 7-reserved for national use
	 */
	SPARE(0), ISDN_NP(1), PRIVATE_NP(5), TELEPHONY_NP(2);
	private int code;
	private NumPlanEnum(int c) {		code = c;
	}
	public int getCode() {
		return code;
	}
	public static NumPlanEnum fromInt(int num) {
		switch (num) {			case 0: { return SPARE; }
			case 1: { return ISDN_NP; }
			case 5: { return PRIVATE_NP; }			case 2:{ return TELEPHONY_NP; }	
			default: { return null; }
		}
	}
}
