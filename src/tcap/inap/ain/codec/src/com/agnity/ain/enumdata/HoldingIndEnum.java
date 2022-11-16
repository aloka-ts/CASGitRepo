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
 * Enum for Holding Indicators
 * @author Mriganka
 *
 */
public enum HoldingIndEnum {
	/**
	 *  0-Holding not requested
	 *  1-Holding requested
	 */
	HOLDING_NOT_REQUESTED(0), HOLDING_REQUESTED(1);
	private int code;
	private HoldingIndEnum(int c) {
		code = c;
	}
	
	public int getCode() {
		return code;
	}
	public static HoldingIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	HOLDING_NOT_REQUESTED	; }
		case 1: { return 	HOLDING_REQUESTED	; }
		default: { return null; }
		}
	}
}
