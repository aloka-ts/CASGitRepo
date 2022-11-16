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
 * Enum to represent if an MS supports Class Mark 3 options 
 * @author sanjay
 *
 */
public enum Cm3OptionsMapEnum {
	CM3_OPTIONS_NOT_SUPPORTED(0),
	CM3_OPTIONS_SUPPORTED(1);

	private int code;
	public int getCode() {
		return this.code;
	}
	
	private Cm3OptionsMapEnum(int code) {
		this.code = code;
	}
	
	public static Cm3OptionsMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return CM3_OPTIONS_NOT_SUPPORTED;
			case 1: return CM3_OPTIONS_SUPPORTED;
			default: return null;
		}
	}
	
	
	
}
