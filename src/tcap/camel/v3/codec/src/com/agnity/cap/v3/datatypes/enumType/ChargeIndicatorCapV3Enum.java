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
 * ref: Q763
 * bits
   B A Charge indicator 
   0 0 no indication
   0 1 no charge
   1 0 charge
   1 1 spare
 */
public enum ChargeIndicatorCapV3Enum {

	NO_INDICATION(0),NO_CHARGE(1),CHARGE(2),SPARE(3);
	
	int code;
	private ChargeIndicatorCapV3Enum(int code) {
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	
	public static ChargeIndicatorCapV3Enum getValue(int tag){
		switch (tag) {
		case 0: return NO_INDICATION;
		case 1: return NO_CHARGE;
		case 2: return CHARGE;
		case 3: return SPARE;
		default:return null;
		}
	}
	
}
