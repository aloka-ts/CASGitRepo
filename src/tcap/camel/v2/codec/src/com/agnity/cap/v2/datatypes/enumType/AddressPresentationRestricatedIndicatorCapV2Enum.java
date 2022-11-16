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

public enum AddressPresentationRestricatedIndicatorCapV2Enum {
 
	PRESENTATION_ALLOWED(0), PRESENTATION_RESTRICTED(1), ADDRESS_NOT_AVAILABLE(2),
	RESERVED(3);
	
	private int code;
	private AddressPresentationRestricatedIndicatorCapV2Enum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static AddressPresentationRestricatedIndicatorCapV2Enum getValue(int tag){
		switch (tag) {
		case 0: return PRESENTATION_ALLOWED; 
		case 1: return PRESENTATION_RESTRICTED; 
		case 2: return ADDRESS_NOT_AVAILABLE; 
		case 3: return RESERVED; 
		default: return null;
		}
	}
}
