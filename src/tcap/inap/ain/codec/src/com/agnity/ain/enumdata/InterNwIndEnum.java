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
 * Enum for Interworking indicator
 * @author Mriganka
 *
 */
public enum InterNwIndEnum {
	/**
	 * 0-no interworking encountered (No. 7 signalling all the way)
	 * 1-interworking encountered	 
	 */
	NO_INTER_NW_ENC(0), INTER_NW_ENC(1);
	private int code;
	
	private InterNwIndEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static InterNwIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return NO_INTER_NW_ENC; }
			case 1: { return INTER_NW_ENC; }
			default: { return null; }
		}
	}
}
