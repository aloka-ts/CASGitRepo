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
 * Enum for Charge Indicator
 * @author Mriganka
 *
 */
public enum ChargeIndicatorEnum {
	/**
	 *  0-no indication
	 *  1-no charge
	 *  2-charge 
	 *  3-spare 
	 */
	NO_INDICATION(0), NO_CHARGE(1), CHARGE(2), SPARE(3);
	private int code;
	private ChargeIndicatorEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static ChargeIndicatorEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NO_INDICATION	; }		case 1: { return 	NO_CHARGE	; }
		case 2: { return 	CHARGE	; }
		case 3: { return 	SPARE	; }
		default: { return null; }
		}
	}
}
