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
 */
public enum OfferedCamel4CsiMapEnum {

	O_CSI(0), D_CSI(1), VT_CSI(2), T_CSI(3), MT_SMS_CSI(4), MG_CSI(5), 
	PSI_ENHANCEMENTS(6);
	
	private int code;
	
	public int getCode(){
		return this.code;
	}
	
	private OfferedCamel4CsiMapEnum(int code) {
		this.code =  code;
	}
	
	public static OfferedCamel4CsiMapEnum getValue(int tag) {
		switch(tag){
		case 0: return O_CSI;
		case 1: return D_CSI;
		case 2: return VT_CSI;
		case 3: return T_CSI;
		case 4: return MT_SMS_CSI;
		case 5: return MG_CSI;
		case 6: return PSI_ENHANCEMENTS;
		default: return null;
		}
	}
}
