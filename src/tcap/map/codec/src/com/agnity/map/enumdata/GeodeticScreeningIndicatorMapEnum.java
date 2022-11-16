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
 * 0 0 user provided, not verified
 * 0 1 user provided, verified and passed
 * 1 0 user provided, verified and failed
 * 1 1 network provided
 */

public enum GeodeticScreeningIndicatorMapEnum {

	USER_PROVIDED_NOT_VERIFIED(0), USER_PROVIDED_VERIFIED_AND_PASSED (1),
	USER_PROVIDED_VERIFIED_AND_FAILED(2), NETWORK_PROVIDED(3);
	
	int code;
	private GeodeticScreeningIndicatorMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static GeodeticScreeningIndicatorMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return USER_PROVIDED_NOT_VERIFIED;
			case 1: return USER_PROVIDED_VERIFIED_AND_PASSED;
			case 2: return USER_PROVIDED_VERIFIED_AND_FAILED;
			case 3: return NETWORK_PROVIDED;
			default: return null;
		}
	}
}
