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
 * Enum for ISDN Access Indicator
 * @author Mriganka
 *
 */
public enum ISDNAccessIndEnum {
	/**
	 * 0-originating access non-ISDN
	 * 1-originating access ISDN
	 */
	NON_ISDN(0), ISDN(1);
	private int code;
	
	private ISDNAccessIndEnum(int c) {
		code = c;
	}
	
	public int getCode() {		return code;
	}
	public static ISDNAccessIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return NON_ISDN; }
			case 1: { return ISDN; }
			default: { return null; }
		}
	}
}
