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
 * Enum for Address Presentation restricted indicator
 * @author Nishant
 *
 */
public enum AddPrsntRestEnum {
	/**
	 * 0-presentation allowed
	 * 1-presentation restricted
	 * 2-spare
	 * 3-spare
	 */
	PRSNT_ALLWD(0), PRSNT_RESTD(1), SPARE(2);	private int code;
	private AddPrsntRestEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static AddPrsntRestEnum fromInt(int num) {
		switch (num) {
		case 0: { return PRSNT_ALLWD; }
		case 1: { return PRSNT_RESTD; }
		case 3: { return SPARE; }
		default: { return null; }
		}
	}
}
