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
/**
 * 
 */
package com.agnity.ain.enumdata;

/**
 * @author nishantsharma
 *
 */
public enum ACGSmsControlIndEnum {
	/**
	 * 0->  No SMS Initiated controls encountered
	 * 1->  SMS Initiated controls encountered
	 */
	NO_SMS_INITIATED_CTRL(0),SMS_INITIATED_CTRL(1);
	private int code;
	private ACGSmsControlIndEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	
	public static ACGSmsControlIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return NO_SMS_INITIATED_CTRL; }
		case 1: { return SMS_INITIATED_CTRL; }
		default: { return null; }
		}
	}
}
