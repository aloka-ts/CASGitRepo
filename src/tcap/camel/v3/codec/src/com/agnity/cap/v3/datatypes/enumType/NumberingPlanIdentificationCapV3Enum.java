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

public enum NumberingPlanIdentificationCapV3Enum {

	/* ref: TS 24.008
	Bits 
	4 3 2 1 
	0 0 0 0 unknown 
	0 0 0 1 ISDN/telephony numbering plan (Rec. E.164/E.163) 
	0 0 1 1 data numbering plan (Recommendation X.121) 
	0 1 0 0 telex numbering plan (Recommendation F.69) 
	1 0 0 0 national numbering plan 
	1 0 0 1 private numbering plan 
	1 0 1 1 reserved for CTS (see 3GPP TS 44.056 [91]) 
	1 1 1 1 reserved for extension
	*/
	UNKNOWN(0),ISDN_TELEPHONY_NUMBERING(1),DATA_NUMBERING_PLAN(3),TELEX_NUMBERING_PLAN(4),
	NATIONAL_NUMBERING_PLAN(8), PRIVATE_NUMBERING_PLAN(9),RESERVED_FOR_CTS(11), 
	RESERVED_FOR_EXTENSION(15);
	
	
	private int code;
	private NumberingPlanIdentificationCapV3Enum(int code){
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	
	   public static NumberingPlanIdentificationCapV3Enum getValue(int tag){
		   switch (tag) {
			case 0: return UNKNOWN;
			case 1:return ISDN_TELEPHONY_NUMBERING;
			case 3: return DATA_NUMBERING_PLAN;
			case 4: return TELEX_NUMBERING_PLAN;
			case 8: return NATIONAL_NUMBERING_PLAN;
			case 9: return PRIVATE_NUMBERING_PLAN;
			case 11: return RESERVED_FOR_CTS;
			case 15: return RESERVED_FOR_EXTENSION;
			default: return null;
			}
		}
}
