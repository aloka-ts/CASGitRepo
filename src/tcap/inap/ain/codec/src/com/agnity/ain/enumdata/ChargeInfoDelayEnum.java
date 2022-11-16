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
 * Enum for Charge Information DelayEnum
 * @author Mriganka
 *
 */
public enum ChargeInfoDelayEnum {
	/**
	 *  0-Spare
	 *  1->128-reserved for national use 
	 *  253-charging rate transfer 
	 *  254-terminating charge area information 	 */
	SPARE(0), NATIONAL_USE(1), CHARGING_RATE_TRANSFER(253), TERMINATING_CHARGE_AREA_INFO(254);
	private int code;
	private ChargeInfoDelayEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static ChargeInfoDelayEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	SPARE	; }
		case 1: { return 	NATIONAL_USE	; }
		case 253: { return 	CHARGING_RATE_TRANSFER	; }
		case 254: { return 	TERMINATING_CHARGE_AREA_INFO	; }
		default: { return null; }
		}
	}
}
