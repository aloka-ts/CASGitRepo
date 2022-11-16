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
 ****/package com.agnity.cap.v2.datatypes.enumType;

import com.agnity.cap.v2.util.DataType;

public enum CodingStandardCapV2Enum {
   /*
	Bits 
	8 7 
	0 0 standardized coding as described in ITU-T Rec. Q.931 
	0 1 reserved for other international standards 
	1 0 national standard 
	1 1 standard defined for the GSM PLMNS as described below
	*/
	
	ITU_T_COADING(0), RESRV_FOR_INTERNATIONAL_USE(1), NATIONAL_STANDARD(2),DEFINED_STANDARD_GSM_PLMNS(3);
	
	private int code;
	private CodingStandardCapV2Enum(int code){
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static CodingStandardCapV2Enum getValue(int tag){
		switch (tag) {
		case 0: return ITU_T_COADING; 
		case 1: return RESRV_FOR_INTERNATIONAL_USE; 
		case 2: return NATIONAL_STANDARD; 
		case 3: return DEFINED_STANDARD_GSM_PLMNS; 
		default: return null;
		}
	}
}
