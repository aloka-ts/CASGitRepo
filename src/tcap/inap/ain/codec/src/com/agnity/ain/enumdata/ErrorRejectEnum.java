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
 * Enum for Error or Reject Operation 
 * @author Mriganka
 *
 */
public enum ErrorRejectEnum{
	/**	 * 0-Error	 * 1-Reject	 */
	ERROR(0), REJECT(1);
	
	private int code;
	
	private ErrorRejectEnum(int c) {
		code = c;
	}
	public int getCode() {		return code;	}
	
	public static ErrorRejectEnum fromInt(int num) {
		switch (num) {
			case 0: { return ERROR; }
			case 1: { return REJECT; }
			default: { return null; }
		}
	}
}
