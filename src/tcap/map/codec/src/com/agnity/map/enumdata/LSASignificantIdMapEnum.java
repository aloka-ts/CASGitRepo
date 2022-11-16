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

public enum LSASignificantIdMapEnum {
	/*
	 * 0 Plmn significant number
     * 1 universal LSA 
	 */
	PLMN_SIGNIFICANT_NUMBER(0), UNIVERSAL_SLA(1);
	
	private int code;
	    
	private LSASignificantIdMapEnum(int code) {
		this.code=code;
	}

	public int getCode() {
		return code;
	}

	public static LSASignificantIdMapEnum getValue(int tag) {
		switch(tag) {
			case 0 : return PLMN_SIGNIFICANT_NUMBER;
			case 1 : return UNIVERSAL_SLA;
	        default: return null;
	    }
	}
	
}
