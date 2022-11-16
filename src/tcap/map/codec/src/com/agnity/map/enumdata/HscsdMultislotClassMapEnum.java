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
 * This Enum indicates the multislot class supported by an MS 
 * enabled or not
 *  
 * @author sanjay
 *
 */
public enum HscsdMultislotClassMapEnum {
	CLASS1(0),
	PRESENT(1);
	
	private int code;
	public int getCode() {
		return this.code;
	}
	
	private HscsdMultislotClassMapEnum(int code) {
		this.code = code;
	}
	
	public static HscsdMultislotClassMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return CLASS1;
			case 1: return PRESENT;
			default: return null;
		}
	}
}
