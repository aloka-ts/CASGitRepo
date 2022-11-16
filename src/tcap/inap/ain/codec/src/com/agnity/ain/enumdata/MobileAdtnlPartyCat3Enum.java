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
 * Enum for Mobile Additional Party's Category 3
 * @author Mriganka
 *
 */
public enum MobileAdtnlPartyCat3Enum {
	/**
	 * 0->255-reserved for national use
	 */
	RESERVED(0);
	private int code;
	private MobileAdtnlPartyCat3Enum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static MobileAdtnlPartyCat3Enum fromInt(int num) {		switch (num) {
			case 0: { return RESERVED; }
			default: { return null; }
		}
	}
}
