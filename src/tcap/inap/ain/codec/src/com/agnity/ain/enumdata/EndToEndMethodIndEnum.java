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
 * Enum for End to End Method Indicator 
 * @author Mriganka
 *
 */
public enum EndToEndMethodIndEnum {
	/**
	 * 0-no end-to-end method available (only link-by-link method available)
	 * 1-pass-along method available (national use)
	 * 2-SCCP method available
	 * 3-pass-along and SCCP methods available (national use)
	 */
	NO_END_METHOD(0), PASS_ALONG_METHOD(1), SCCP_METHOD(2), PASS_ALONG_SCCP_METHOD(3);
	private int code;
	private EndToEndMethodIndEnum(int c) {
		code = c;
	}
	
	public int getCode() {
		return code;
	}
	public static EndToEndMethodIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return NO_END_METHOD; }
			case 1: { return PASS_ALONG_METHOD; }
			case 2: { return SCCP_METHOD; }
			case 3: { return PASS_ALONG_SCCP_METHOD; }
			default: { return null; }
		}
	}
}
