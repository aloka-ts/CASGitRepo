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

import com.agnity.map.util.DataType;

public enum ScreeningIndicatorMapEnum {

	RESERVED_0(0), USER_PROVIDED(1), RESERVED_2(2), NETWORK_PROVIDED(3);
	
	private int code;
	private ScreeningIndicatorMapEnum(int code){
		this.code= code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static ScreeningIndicatorMapEnum getValue(int tag){
		switch (tag) {
		case 0: return RESERVED_0; 
		case 1: return USER_PROVIDED; 
		case 2: return RESERVED_2; 
		case 3: return NETWORK_PROVIDED; 
		default: return null;
		}
	}
}
