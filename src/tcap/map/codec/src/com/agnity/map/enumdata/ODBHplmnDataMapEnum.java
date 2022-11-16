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
 * 
 * @author sanjay
 *
 */
public enum ODBHplmnDataMapEnum {
	PLMN_SPECIFIC_BARRING_TYPE_1(0),
	PLMN_SPECIFIC_BARRING_TYPE_2(1),
	PLMN_SPECIFIC_BARRING_TYPE_3(2),
	PLMN_SPECIFIC_BARRING_TYPE_4(3);
	
	private int code;
	
	private ODBHplmnDataMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static ODBHplmnDataMapEnum getValue(int tag) {
		switch(tag) {
		case 0: return PLMN_SPECIFIC_BARRING_TYPE_1;
		case 1: return PLMN_SPECIFIC_BARRING_TYPE_2;
		case 2: return PLMN_SPECIFIC_BARRING_TYPE_3;
		case 3: return PLMN_SPECIFIC_BARRING_TYPE_4;
		default: return null;
		
		}
	}

}
