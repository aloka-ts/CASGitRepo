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

package com.agnity.map.enumdata;

/**
 * 3GPP TS 24.008 V9.3.0 (2010-06), page 486
 * 
 * This field indicates the access technology
 * type to be associated with the following access capabilities. 
 * @author sanjay
 *
 */
public enum AccessTechnologyTypeMapEnum {
	GSM_P(0),
	GSM_E(1), // note that GSM E covers GSM P
	GSM_R(2), // note that GSM R covers GSM E and GSM P
	GSM_1800(3),
	GSM_1900(4),
	GSM_450(5),
	GSM_480(6),
	GSM_850(7),
	GSM_750(8),
	GSM_T_380(9),
	GSM_T_410(10),
	NOT_USED(11),  //This value was allocated in an earlier version of the protocol and shall not be used.
	GSM_710(12),
	GSM_T_810(13),
	ADDL_ACC_TECH(15); //  Indicates the presence of a list of Additional access technologies
	
	private int code;
	private AccessTechnologyTypeMapEnum(int code) {
		this.code = code;
	}
	public int getCode() {
		return this.code;
	}
	public static AccessTechnologyTypeMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return GSM_P;
			case 1: return GSM_E;
			case 2: return GSM_R;
			case 3: return GSM_1800;
			case 4: return GSM_1900;
			case 5: return GSM_450;
			case 6: return GSM_480;
			case 7: return GSM_850;
			case 8: return GSM_750;
			case 9: return GSM_T_380;
			case 10: return GSM_T_410;
			case 11: return NOT_USED;
			case 12: return GSM_710;
			case 13: return GSM_T_810;
			case 15: return ADDL_ACC_TECH;
			default: return null;
		}
	}
	
}
