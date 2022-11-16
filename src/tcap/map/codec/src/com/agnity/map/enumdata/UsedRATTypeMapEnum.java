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

public enum UsedRATTypeMapEnum {

	UTRAN(0), GERAN(1), GAN(2),I_HSPA_EVOLUTION(3), E_UTRAN(4);
	
	private int code;
	
	private UsedRATTypeMapEnum(int code){
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static UsedRATTypeMapEnum getValue(int tag){
		switch (tag) {
		case 0: return UTRAN;
		case 1: return GERAN;
		case 2: return GAN;
		case 3: return I_HSPA_EVOLUTION;
		case 4: return E_UTRAN;
		default: return null;
		}
	}
}
