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
 * Enum to represent if an MS has PS (pseudo-synchronization capability)
 * @author sanjay
 *
 */
public enum PsCapabilityMapEnum {
	PS_CAP_NOT_PRESENT(0),
	PS_CAP_PRESENT(1);

	private int code;
	public int getCode() {
		return this.code;
	}
	
	private PsCapabilityMapEnum(int code) {
		this.code = code;
	}
	
	public static PsCapabilityMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return PS_CAP_NOT_PRESENT;
			case 1: return PS_CAP_PRESENT;
			default: return null;
		}
	}
}
