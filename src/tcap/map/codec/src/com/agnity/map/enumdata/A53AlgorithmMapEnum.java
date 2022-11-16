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
 * Enum to represent if A5/3 Encryption algorithm support is available 
 * in an MS or not 
 * @author sanjay
 *
 */
public enum A53AlgorithmMapEnum {
	ALGO_NOT_AVAILABLE(0),
	ALGO_AVAILABLE(1);
	
	private int code;
	public int getCode() {
		return this.code;
	}
	
	private A53AlgorithmMapEnum(int code) {
		this.code = code;
	}
	
	public static A53AlgorithmMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return ALGO_NOT_AVAILABLE;
			case 1: return ALGO_AVAILABLE;
			default: return null;
		}
	}
}
