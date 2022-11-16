/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.agnity.ain.enumdata;
/**
 * Enum for National/International Call Indicator 
 * @author Mriganka
 *
 */
public enum NatIntNatCallIndEnum {
	/**
	 * 0-call to be treated as a national call
	 * 1-call to be treated as a international call
	 */	NATIONAL_CALL(0), INTERNATIONAL_CALL(1);
	private int code;
	
	private NatIntNatCallIndEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static NatIntNatCallIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return NATIONAL_CALL; }
			case 1: { return INTERNATIONAL_CALL; }
			default: { return null; }
		}
	}	
}
