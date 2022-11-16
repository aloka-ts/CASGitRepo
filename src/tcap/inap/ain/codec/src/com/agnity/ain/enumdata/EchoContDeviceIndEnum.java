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
 * Enum for Echo Control Device Indicator
 * @author Mriganka
 *
 */
public enum EchoContDeviceIndEnum {
	/**
	 * 0-incoming echo control device not included
	 * 1-incoming echo control device included
	 */
	DEVICE_NOT_INCLUDED(0), DEVICE_INCLUDED(1);
	private int code;
	private EchoContDeviceIndEnum(int c) {
		code = c;
	}
	public int getCode() {		return code;
	}
	public static EchoContDeviceIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return DEVICE_NOT_INCLUDED; }
			case 1: { return DEVICE_INCLUDED; }
			default: { return null; }
		}
	}
}
