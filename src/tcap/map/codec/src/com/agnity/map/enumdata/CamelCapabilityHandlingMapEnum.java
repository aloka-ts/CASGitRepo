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
 * Enum to represent if A5/1 Encryption algorithm support is available 
 * in an MS or not 
 * @author sanjay
 *
 */
public enum CamelCapabilityHandlingMapEnum {
	CAMEL_PHASE1(1),
	CAMEL_PHASE2(2),
	CAMEL_PHASE3(3),
	CAMEL_PHASE4(4);
	
	private int code;
	public int getCode() {
		return this.code;
	}
	
	private CamelCapabilityHandlingMapEnum(int code) {
		this.code = code;
	}
	
	public static CamelCapabilityHandlingMapEnum getValue(int tag) {
		switch(tag) {
			case 1: return CAMEL_PHASE1;
			case 2: return CAMEL_PHASE2;
			case 3: return CAMEL_PHASE3;
			case 4: return CAMEL_PHASE4;
			default: return null;
		}
	}
}
