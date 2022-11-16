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
 * Enum for Internal Network Number indicator 
 * @author Mriganka
 *
 */
public enum IntNwNumEnum {
	/**
	 * 0-routing to internal network number allowed
	 * 1-routing to internal network number not allowed
	 */
	ROUTING_ALLWD(0), ROUTING_NOT_ALLWD(1);
	private int code;
	
	private IntNwNumEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static IntNwNumEnum fromInt(int num) {
		switch (num) {			case 0: { return ROUTING_ALLWD; }
			case 1: { return ROUTING_NOT_ALLWD; }
			default: { return null; }
		}
	}
}
