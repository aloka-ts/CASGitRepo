/****
 * Copyright (c) 2013 Agnity, Inc. All rights reserved.
 * 
 * This is proprietary source code of Agnity, Inc.
 * 
 * Agnity, Inc. retains all intellectual property rights associated with this
 * source code. Use is subject to license terms.
 * 
 * This source code contains trade secrets owned by Agnity, Inc. Confidentiality
 * of this computer program must be maintained at all times, unless explicitly
 * authorized by Agnity, Inc.
 ****/
package com.agnity.cap.v2.datatypes.enumType;
/**
 * 
 * @author rnarayan
 *
 */
public enum AdditionalLayer3ProtocolInfoCapV2Enum {
	/**
	 * 12-Internet protocol
	 * 15-Point-to-point protocol
	 */
	INTERNET_PROTOCOL(12), POINT_TO_POINT_PROTOCOL(15);
	
	private int code;

	private AdditionalLayer3ProtocolInfoCapV2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static AdditionalLayer3ProtocolInfoCapV2Enum getValue(int num) {
		switch (num) {
		case 12: { return 	INTERNET_PROTOCOL	; }
		case 15: { return 	POINT_TO_POINT_PROTOCOL	; }
		default: { return null; }
		}
	}
}
