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
 * Bits  
 * 7 6
 * 0 0 Reserved for GSM phase 1
 * 0 1 Used by GSM phase 2 mobile stations
 * 1 0 Used by mobile stations supporting R99 or later versions of the protocol
 * 1 1 Reserved for future use. 
 * 
 * If the network receives a revision level specified as 'reserved
 * for future use', then it shall use the highest revision level supported by the network.
 * @author sanjay
 *
 */
public enum RevisionLevelMapEnum {
	RESERVED_FOR_GSM_PHASE1(0),
	USED_BY_GSM_PHASE2_MS(1),
	USED_BY_MS_R99_OR_LATER(2),
	RESERVED_FOR_FUTURE_USE(3);
	
	private int code;
	private RevisionLevelMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static RevisionLevelMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return RESERVED_FOR_GSM_PHASE1;
			case 1: return USED_BY_GSM_PHASE2_MS;
			case 2: return USED_BY_MS_R99_OR_LATER;
			case 3: return RESERVED_FOR_FUTURE_USE;
			default: return null;
		}
	}
}
