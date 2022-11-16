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
 * 3GPP TS 24.008 V9.3.0 (2010-06) 
 * 
 * 0 0 0 class 1
 * 0 0 1 class 2
 * 0 1 0 class 3
 * 0 1 1 class 4
 * 1 0 0 class 5 
 * 
 * @author sanjay
 *
 */
public enum RfPowerCapabilityMapEnum {
	CLASS1(0), CLASS2(1), CLASS3(2), CLASS4(3), CLASS5(4);

	private int code;
	
	private RfPowerCapabilityMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static RfPowerCapabilityMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return CLASS1;
			case 1: return CLASS2;
			case 2: return CLASS3;
			case 3: return CLASS4;
			case 4: return CLASS5;
			default: return null;
		}
	}

}
