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
public enum SupportedCamelPhasesMapEnum {
	
	PHASE_1(0),   
	PHASE_2(1),
	PHASE_3(2),
	PHASE_4(3);
	
	private int code;
	private SupportedCamelPhasesMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public SupportedCamelPhasesMapEnum getValue(int tag) {
		switch(tag) {
		case 0: return PHASE_1;
		case 1: return PHASE_2;
		case 2: return PHASE_3;
		case 3: return PHASE_4;
		default: return null;
		}
	}

}
