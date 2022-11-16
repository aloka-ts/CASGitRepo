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
 */
public enum CallTypeCriteriaMapEnum {
	FORWARDED(0),
	NOT_FORWARDED(1);
	
	private int code;
	public int getCode() {
		return this.code;
	}
	
	private CallTypeCriteriaMapEnum(int code) {
		this.code = code;
	}
	
	public static CallTypeCriteriaMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return FORWARDED;
			case 1: return NOT_FORWARDED;
			default: return null;
		}
	}
}
