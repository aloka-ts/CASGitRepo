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
 * 3GPP TS 29.060
 * 
 * CSG membership 0 decimal
 * Non CSG membership 1 
 * 
 *
 */

public enum CmiMapEnum {
	CSG_MEMBERSHIP(0), NON_CSG_MEMBERSHIP(1);
	
	private int code;
	
	private CmiMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static CmiMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return CSG_MEMBERSHIP;
			case 1: return NON_CSG_MEMBERSHIP;
			default: return null;
		}
		
	}
}
