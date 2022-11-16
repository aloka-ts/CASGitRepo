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
 * Enum for SCCP method indicator
 * @author Mriganka
 *
 */
public enum SCCPMethodIndENum {
	/**
	 * 0-no indication
	 * 1-connectionless method available (national use)
	 * 2-connection oriented method available
	 * 3-connectionless and connection oriented methods available (national use)
	 */
	NO_INDICATION(0), CONNECTIONLESS_METHOD(1), CONNECTIONORIENTED_METHOD(2), COMNNECTIONLESS_CONNECTIONORIENTED_METHOD(3);
	private int code;
	private SCCPMethodIndENum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	public static SCCPMethodIndENum fromInt(int num) {
		switch (num) {
		case 0: { return NO_INDICATION; }
		case 1: { return CONNECTIONLESS_METHOD; }
		case 2: { return CONNECTIONORIENTED_METHOD; }
		case 3: { return COMNNECTIONLESS_CONNECTIONORIENTED_METHOD; }
		default: { return null; }
		}
	}
}
