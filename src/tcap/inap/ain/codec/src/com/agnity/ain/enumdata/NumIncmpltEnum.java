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
 * Enum for Number Incomplete Indiactor
 * @author Mriganka
 *
 */
public enum NumIncmpltEnum {
	/**
	 * 0-Complete
	 * 1-Incomplete
	 */
	COMPLETE(0), INCOMPLETE(1);
	private int code;
	private NumIncmpltEnum(int c) {
		code = c;
	}
	
	public int getCode() {
		return code;
	}
	public static NumIncmpltEnum fromInt(int num) {
		switch (num) {
			case 0: { return COMPLETE; }
			case 1: { return INCOMPLETE; }
			default: { return null; }
		}
	}
}
