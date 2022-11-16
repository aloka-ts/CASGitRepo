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
package com.agnity.cap.v3.datatypes.enumType;


public enum INNIndicatorCapV3Enum {
	/*
	 * 0 routing to internal network number allowed 
     * 1 routing to internal network number not allowed 
	 */
	INN_ALLOWED(0),INN_NOT_ALLOWED(1);
	
	private int code;
	private INNIndicatorCapV3Enum(int code){
		this.code=code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static INNIndicatorCapV3Enum getValue(int tag){
		switch (tag) {
		case 0: return INN_ALLOWED; 
		case 1: return INN_NOT_ALLOWED; 
		default: return null;
		}
	}
}
