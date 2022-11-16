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
public enum NegotiationCapV2Enum {


	/**
	 * 0-In-band negotiation not possible
	 * 1-In-band negotiation possible
	 */
	INBAND_NOT_POSSIBLE(0), INBAND_POSSIBLE(1);
	
	private int code;

	private NegotiationCapV2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NegotiationCapV2Enum getValue(int num) {
		switch (num) {
		case 0: { return 	INBAND_NOT_POSSIBLE	; }
		case 1: { return 	INBAND_POSSIBLE	; }
		default: { return null; }
		}
	}
}
