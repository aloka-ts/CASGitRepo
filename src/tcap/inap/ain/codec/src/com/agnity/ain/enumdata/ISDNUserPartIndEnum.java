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
 * Enum for ISDn User Part Indicator 
 * @author Mriganka
 *
 */
public enum ISDNUserPartIndEnum {
	/**
	 * 0-ISDN user part not used all the way
	 * 1-ISDN user part used all the way
	 */
	ISDN_USER_PART_NOT_USED(0), ISDN_USER_PART_USED(1);
	private int code;
	private ISDNUserPartIndEnum(int c) {		code = c;
	}
	public int getCode() {		return code;
	}
	public static ISDNUserPartIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return ISDN_USER_PART_NOT_USED; }
			case 1: { return ISDN_USER_PART_USED; }
			default: { return null; }
		}
	}
}
