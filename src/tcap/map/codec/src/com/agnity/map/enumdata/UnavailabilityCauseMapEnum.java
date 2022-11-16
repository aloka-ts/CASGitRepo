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
 * @author sanjay
 *
 */
public enum UnavailabilityCauseMapEnum {
	
	BEARERSERVICENOTPROVISIONED (1), 
	TELESERVICENOTPROVISIONED (2), 
	ABSENTSUBSCRIBER (3), 
	BUSYSUBSCRIBER (4), 
	CALLBARRED (5), 
	CUG_REJECT (6);

	
	private int code;
	public int getCode() {
		return this.code;
	}
	
	private UnavailabilityCauseMapEnum(int code) {
		this.code = code;
	}
	
	public static UnavailabilityCauseMapEnum getValue(int tag) {
		switch(tag) {
			case 1: return BEARERSERVICENOTPROVISIONED;
			case 2: return TELESERVICENOTPROVISIONED;
			case 3: return ABSENTSUBSCRIBER;
			case 4: return BUSYSUBSCRIBER;
			case 5: return CALLBARRED;
			case 6: return CUG_REJECT;
			default: return null;
		}
	}
}
