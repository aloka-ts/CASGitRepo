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
 * Enum for SPCIndicator
 * @author Mriganka
 *
 */
public enum SPCIndicatorEnum {
	/**
	 * 0-SPC Not Present
	 * 1-SPC Present
	 */
	SPC_NOT_PRESENT(0), SPC_PRESENT(1);	
	private int code;
	private SPCIndicatorEnum(int c) {
		code = c;
	}
	
	public int getCode() {		return code;
	}
	public static SPCIndicatorEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPC_NOT_PRESENT; }
			case 1: { return SPC_PRESENT; }
			default: { return null; }
		}
	}
}
