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

public enum AddlRequestedCAMELSubscriptionInfoMapEnum {
	MT_SMS_CSI (0), 
	MG_CSI (1), 
	O_IM_CSI (2), 
	D_IM_CSI (3), 
	VT_IM_CSI (4); 

	
	private int code;
	
	private AddlRequestedCAMELSubscriptionInfoMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static AddlRequestedCAMELSubscriptionInfoMapEnum getValue(int tag) {
		switch(tag){
			case 0: return MT_SMS_CSI;
			case 1: return MG_CSI;
			case 2: return O_IM_CSI;
			case 3: return D_IM_CSI;
			case 4: return VT_IM_CSI;
			default: return null;
		}
	}

}