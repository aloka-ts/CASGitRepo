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
 * This Enum indicates the support of SRVCC to GERAN/UTRAN
 *  
 * @author sanjay
 *
 */
public enum SrvccToGeranOrUtranCapMapEnum {
	NOT_SUPPORTED(0),
	SUPPORTED(1);
	
	private int code;
	public int getCode() {
		return this.code;
	}
	
	private SrvccToGeranOrUtranCapMapEnum(int code) {
		this.code = code;
	}
	
	public static SrvccToGeranOrUtranCapMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return NOT_SUPPORTED;
			case 1: return SUPPORTED;
			default: return null;
		}
	}
}
