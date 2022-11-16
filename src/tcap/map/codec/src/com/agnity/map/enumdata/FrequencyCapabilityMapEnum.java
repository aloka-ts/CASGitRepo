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
 * FC Frequency Capability
 * 0 - The MS does not support the E-GSM or R-GSM band 
 * 1 - The MS does support the E-GSM or R-GSM
 * @author sanjay
 *
 */
public enum FrequencyCapabilityMapEnum {
	EGSM_OR_RGSM_NOT_SUPPORTED(0),
	EGSM_OR_RGSM_SUPPORTED(1);
	
	private int code;
	public int getCode() {
		return this.code;
	}
	
	private FrequencyCapabilityMapEnum(int code) {
		this.code = code;
	}
	
	public static FrequencyCapabilityMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return EGSM_OR_RGSM_NOT_SUPPORTED;
			case 1: return EGSM_OR_RGSM_SUPPORTED;
			default: return null;
		}
	}

}
