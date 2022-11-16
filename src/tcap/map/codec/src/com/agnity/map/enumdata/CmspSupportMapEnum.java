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
 * Enum to represent CMSP is supported or not 
 * @author sanjay
 *
 */

public enum CmspSupportMapEnum {
	CMSP_NOT_SUPPORTED(0),
	CMSP_SUPPORTED(1);

	
	private int code;
	public int getCode() {
		return this.code;
	}
	
	private CmspSupportMapEnum(int code) {
		this.code = code;
	}
	
	public static CmspSupportMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return CMSP_NOT_SUPPORTED;
			case 1: return CMSP_SUPPORTED;
			default: return null;
		}
	}
}
