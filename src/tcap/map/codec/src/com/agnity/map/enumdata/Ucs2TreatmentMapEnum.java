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
 * UCS2 treatment preference by MS.
 * This information field indicates the likely treatment by the mobile
 * station of UCS2 encoded character strings.

 * @author sanjay
 *
 */
public enum Ucs2TreatmentMapEnum {
	
	DEFAULT_PREFERRED (0),
	NO_PREFERENCE(1);

	private int code;
	public int getCode() {
		return this.code;
	}
	
	private Ucs2TreatmentMapEnum(int code) {
		this.code = code;
	}
	
	public static Ucs2TreatmentMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return DEFAULT_PREFERRED;
			case 1: return NO_PREFERENCE;
			default: return null;
		}
	}
	
	

}
