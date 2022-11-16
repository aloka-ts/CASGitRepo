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
 *
 */

public enum RevisionLevelIndicatorMapEnum {

	R99_OR_LATER_NOT_SUPPORTED(0),
	R99_OR_LATER_SUPPORTED(1);
	
	private int code; 
	
	private RevisionLevelIndicatorMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static RevisionLevelIndicatorMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return R99_OR_LATER_NOT_SUPPORTED;
			case 1: return R99_OR_LATER_SUPPORTED;
			default: return null;
		}
	}
}
