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
 * Enum for End to End Information Indicator 
 * @author Mriganka
 *
 */
public enum EndToEndInfoIndEnum {
	/**
	 * 0-no end-to-end information available
	 * 1-end-to-end information available
	 */
	NO_END_INFO_AVAILABLE(0), END_INFO_AVAILABLE(1);
	private int code;
	private EndToEndInfoIndEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static EndToEndInfoIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return NO_END_INFO_AVAILABLE; }
			case 1: { return END_INFO_AVAILABLE; }
			default: { return null; }
		}
	}
}
