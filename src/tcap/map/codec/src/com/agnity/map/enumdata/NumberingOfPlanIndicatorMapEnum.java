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

import com.agnity.map.util.DataType;

public enum NumberingOfPlanIndicatorMapEnum {
	/*
	    0 0 0 spare 
		0 0 1 ISDN (Telephony) numbering plan (ITU-T Recommendation E.164) 
		0 1 0 spare 
		0 1 1 Data numbering plan (ITU-T Recommendation X.121) (national use) 
		1 0 0 Telex numbering plan (ITU-T Recommendation F.69) (national use) 
		1 0 1 reserved for national use 
		1 1 0 reserved for national use 
		1 1 1 spare  
	 */
	
	
	SPARE_0(0),NUMBERING_PLAN_ISDN(1), SPARE_2(2),
	NUMBERING_PLAN_DATA(3),NUMBERING_PLAN_TELEX(4),
	NUMBERING_PLAN_RESERVED_FOR_PVT_USE(5),NUMBERING_PLAN_RESERVED_FOR_NATL_USE(6),
	SPARE_7(7);
	
	private int code;
	private NumberingOfPlanIndicatorMapEnum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
   public static NumberingOfPlanIndicatorMapEnum getValue(int tag){
		switch (tag) {
		case 0: return SPARE_0; 
		case 1: return NUMBERING_PLAN_ISDN; 
		case 2: return SPARE_2; 
		case 3: return NUMBERING_PLAN_DATA;
		case 4: return NUMBERING_PLAN_TELEX; 
		case 5: return NUMBERING_PLAN_RESERVED_FOR_PVT_USE; 
		case 6: return NUMBERING_PLAN_RESERVED_FOR_NATL_USE;
		case 7: return SPARE_7;
		default: return null;
		}
	}
}
