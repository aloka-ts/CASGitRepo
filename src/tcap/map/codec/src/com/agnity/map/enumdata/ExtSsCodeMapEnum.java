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
 * 
 * bits 4321: Used to convey the "P bit","R bit","A bit" and "Q bit",
 * representing supplementary service state information
 * as defined in TS 3GPP TS 23.011 [22]
 * bit 4: "Q bit"
 * bit 3: "P bit"
 * bit 2: "R bit"
 * bit 1: "A bit"
 *
 */
public enum ExtSsCodeMapEnum {
	Q_BIT(4), P_BIT(3), R_BIT(2), A_BIT(1);
	
	private int code;
	
	public int getCode(){
		return code;
	}
	
	private ExtSsCodeMapEnum(int code) {
		this.code = code;
	}
	
	public static ExtSsCodeMapEnum getValue(int tag) {
		switch(tag){
			case 1: return A_BIT;
			case 2: return R_BIT;
			case 3: return P_BIT;
			case 4: return Q_BIT;
			default: return null;
		}
	}
}
