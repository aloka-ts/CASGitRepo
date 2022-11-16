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
package com.agnity.cap.v2.datatypes.enumType;

/**
 * ref: ETSI TS 101 046 V7.1.0 (2000-07)
 * @author rnarayan
 * '3D'H – Decimal value 61 - Cellular Service (Type 1)
   '3E'H – Decimal value 62 - Cellular Service (Type 2)
   '3F'H – Decimal value 63 - Cellular Service (roaming)
 */
public enum NaOliInfoCapV2Enum {

	TYPE_1(61),TYPE_2(62), ROAMING(63);
	
	private int code;
	
	private NaOliInfoCapV2Enum(int code) {
		// TODO Auto-generated constructor stub
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static NaOliInfoCapV2Enum getValue(int tag){
		
		switch (tag) {
		case 61: return TYPE_1;
		case 62: return TYPE_2;
		case 63: return ROAMING;
		default: return null;
		}
	}
}
