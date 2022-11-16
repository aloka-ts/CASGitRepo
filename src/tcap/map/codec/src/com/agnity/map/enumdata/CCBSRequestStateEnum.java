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
 * 3GPP TS 29.060
 * 
 * closed mode (0) decimal
 * Hybrid mode (1)
 * Reserved    3, 4
 * 
 *
 */
public enum CCBSRequestStateEnum {
	
	REQUEST (0),
	RECALL (1),
	ACTIVE (2),
	COMPLETED (3),
	SUSPENDED (4),
	FROZEN (5),
	DELETED (6);
	
	int code;
	
	private CCBSRequestStateEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static CCBSRequestStateEnum getValue(int tag){
		switch (tag) {
		case 0: return REQUEST; 
		case 1: return RECALL; 
		case 2: return ACTIVE; 
		case 3: return COMPLETED;
		case 4: return SUSPENDED;  
		case 5: return FROZEN; 
		case 6: return DELETED;

		default: return null;
		}
	}

}
