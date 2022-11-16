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

/**
 * 
 * @author rnarayan
 * 
 * ref- 3GPP TS 29.060
 *PDP Type Organisation    Value (Decimal) 
 *           ETSI           0 
 *           IETF           1 
 *      All other values are reserved 
 *
 */
public enum PDPTypeOrgCapV3Enum {
  
	ETSI(0),IETF(1);
	
	private int code;
	
	private PDPTypeOrgCapV3Enum(int code) {
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static PDPTypeOrgCapV3Enum getValue(int tag){
		switch (tag) {
		  case 0: return ETSI;
		  case 1: return IETF;
		  default: return null;
		}
	}
}
