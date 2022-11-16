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

public enum RequestedCAMELSubscriptionInfoMapEnum {
	O_CSI (0), 
	T_CSI (1), 
	VT_CSI (2), 
	TIF_CSI (3), 
	GPRS_CSI (4),
	MO_SMS_CSI(5),
	SS_CSI(6),
	M_CSI(7),
	D_CSI(8);
	
	
	private int code;
	
	private RequestedCAMELSubscriptionInfoMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static RequestedCAMELSubscriptionInfoMapEnum getValue(int tag) {
		switch(tag){
			case 0: return O_CSI;
			case 1: return T_CSI;
			case 2: return VT_CSI;
			case 3: return TIF_CSI;
			case 4: return GPRS_CSI;
			case 5: return MO_SMS_CSI;
			case 6: return SS_CSI;
			case 7: return M_CSI;
			case 8: return D_CSI;
			default: return null;
		}
	}

}