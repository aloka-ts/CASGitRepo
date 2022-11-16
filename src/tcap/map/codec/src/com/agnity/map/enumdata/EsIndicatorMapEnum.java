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
 * 3GPP TS 24.008 V9.3.0 (2010-06), page 383
 * 
 * Enum for Controlled Early Class Mark Sending option.
 * A value of 0 means ES is not implemented
 * A value of 1 means ES is implemented
 * @author sanjay
 *
 */
public enum EsIndicatorMapEnum {
	ES_NOT_IMPLEMENTED(0),
	ES_IMPLEMENTED(1);
	
	private int code;
	private EsIndicatorMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static EsIndicatorMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return ES_NOT_IMPLEMENTED;
			case 1: return ES_IMPLEMENTED;
			default: return null;
		}
	}
	
}
