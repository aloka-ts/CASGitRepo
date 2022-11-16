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
 * Enum to represent if LCS VA capability are supported in an MS or not
 * @author sanjay
 *
 */
public enum LcsVaCapabilityMapEnum {
	LCS_VA_NOT_SUPPORTED(0),
	LCS_VA_SUPPORTED(1);

	private int code;
	public int getCode() {
		return this.code;
	}
	
	private LcsVaCapabilityMapEnum(int code) {
		this.code = code;
	}
	
	public static LcsVaCapabilityMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return LCS_VA_NOT_SUPPORTED;
			case 1: return LCS_VA_SUPPORTED;
			default: return null;
		}
	}

}
