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
 * Enum for SSNIndicator
 * @author Mriganka
 *
 */
public enum SSNIndicatorEnum {
	/**
	 * 0-SSN Not Present
	 * 1-SSN Present
	 */
	SSN_NOT_PRESENT(0), SSN_PRESENT(1);
	private int code;
	private SSNIndicatorEnum(int c) {		code = c;
	}	public int getCode() {
		return code;
	}
	public static SSNIndicatorEnum fromInt(int num) {
		switch (num) {
			case 0: { return SSN_NOT_PRESENT; }
			case 1: { return SSN_PRESENT; }
			default: { return null; }
		}
	}
}
