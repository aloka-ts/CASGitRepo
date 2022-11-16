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
 * ETSI TS 124 080 V9.2.0 (2010-06)
 * 0 - PHASE 1
 * 1 - capability of handling of ellipsis notation and phase 2 error handling 
 * @author sanjay
 *
 */
public enum SsScreeningIndicatorMapEnum {
	PHASE1(0), ELLIPSIS_NOTATION_PHASE2_ERROR_HDLING(2);
	
	private int code;
	
	private SsScreeningIndicatorMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static SsScreeningIndicatorMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return PHASE1;
			case 1: return ELLIPSIS_NOTATION_PHASE2_ERROR_HDLING;
			default: return null;
		}
	}
}