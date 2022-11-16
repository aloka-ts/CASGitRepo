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
 * This Enum indicates if MS has MultipSlot Capability 
 * enabled or not
 *  
 * @author sanjay
 *
 */
public enum MultislotCapabilityMapEnum {
	NOT_PRESENT(0),
	PRESENT(1);
	
	private int code;
	public int getCode() {
		return this.code;
	}
	
	private MultislotCapabilityMapEnum(int code) {
		this.code = code;
	}
	
	public static MultislotCapabilityMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return NOT_PRESENT;
			case 1: return PRESENT;
			default: return null;
		}
	}
}
