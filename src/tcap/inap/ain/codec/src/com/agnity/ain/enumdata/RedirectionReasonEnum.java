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
 * Enum for RedirectionReason value
 * @author Mriganka
 *
 */
public enum RedirectionReasonEnum {
	/**
	 * 0-Spare
	 * 1-Reserved for national use
	 * 126-Roaming
	 */
	SPARE(0), RESERVERD(1), ROAMING(126);
	private int code;
	private RedirectionReasonEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static RedirectionReasonEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return RESERVERD; }
			case 126: { return ROAMING; }
			default: { return null; }
		}
	}
}
