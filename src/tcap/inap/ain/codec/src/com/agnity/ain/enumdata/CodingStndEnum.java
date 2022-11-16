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
 * Enum for Coding Standard
 * @author nishantsharma
 *
 */
public enum CodingStndEnum {
	/**
	 *  0-CCITT Standard
	 *  3-Network-Specific 
	 */
	CCITT_STANDARDIZED_CODING(0), NETWORK_SPECIFIC(3);
	private int code;
	private CodingStndEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static CodingStndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	CCITT_STANDARDIZED_CODING	; }
		case 3: { return 	NETWORK_SPECIFIC	; }
		default: { return null; }
		}
	}
}
