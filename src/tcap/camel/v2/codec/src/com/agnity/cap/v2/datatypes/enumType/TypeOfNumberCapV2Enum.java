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

public enum TypeOfNumberCapV2Enum {
    /*
	Bits 
	7 6 5 
	0 0 0 unknown (Note 2) 
	0 0 1 international number (Note 3, Note 5) 
	0 1 0 national number (Note 3) 
	0 1 1 network specific number (Note 4) 
	1 0 0 dedicated access, short code 
	1 0 1 reserved 
	1 1 0 reserved 
	1 1 1 reserved for extension 
    */
	
	UNKNOWN(0),INTERNATIONAL_NO(1),NATIONAL_NO(2),NETWORK_SPECIFIC_NO(3),DEDICATED_ACCESS(4);
	
	private int code;
	private TypeOfNumberCapV2Enum(int code){
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static TypeOfNumberCapV2Enum getValue(int tag){
		switch (tag) {
		case 0: return UNKNOWN;
		case 1:	return INTERNATIONAL_NO;
		case 2: return NATIONAL_NO;
		case 3: return NETWORK_SPECIFIC_NO;
		case 4: return DEDICATED_ACCESS;
		default: return UNKNOWN	;	
			}
		
	}
}
