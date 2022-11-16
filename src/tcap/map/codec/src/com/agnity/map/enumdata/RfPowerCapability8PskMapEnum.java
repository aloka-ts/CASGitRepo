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
 * This field indicates the radio capability for 8-PSK modulation. 
 * The following coding is used:
 * 
 * 0 0 8PSK modulation not supported for uplink
 * 0 1 Power class E1
 * 1 0 Power class E2
 * 1 1 Power class E3 
 * 
 * @author sanjay
 *
 */
public enum RfPowerCapability8PskMapEnum {
	/*
	 * For uplink value 0 indicated RESERVED
	 */
	NOT_SUPPORTED(0), CLASS_E1(1), CLASS_E2(2), CLASS_E3(3);

	private int code;
	
	private RfPowerCapability8PskMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static RfPowerCapability8PskMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return NOT_SUPPORTED;
			case 1: return CLASS_E1;
			case 2: return CLASS_E2;
			case 3: return CLASS_E3;
			default: return null;
		}
	}

}
