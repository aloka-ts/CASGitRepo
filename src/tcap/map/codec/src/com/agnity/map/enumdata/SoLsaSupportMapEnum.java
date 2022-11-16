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
 * Enum to represent if MS supports SoLSA
 * 0 The ME does not support SoLSA.
 * 1 The ME supports SoLSA. 
 * @author sanjay
 *
 */
public enum SoLsaSupportMapEnum {
	SOLSA_NOT_SUPPORTED(0),
	SOLSA_SUPPORTED(1);

	private int code;
	public int getCode() {
		return this.code;
	}
	
	private SoLsaSupportMapEnum(int code) {
		this.code = code;
	}
	
	public static SoLsaSupportMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return SOLSA_NOT_SUPPORTED;
			case 1: return SOLSA_SUPPORTED;
			default: return null;
		}
	}
	
	

}
