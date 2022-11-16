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
package com.agnity.cap.v3.datatypes.enumType;

public enum InbandOutbandNegotiationCapV3Enum {


	/**
	 * 0-Negotiation is done with user information messages on a temporary signaling connection
	 * 1-Negotiation is done in-band using logical link zero
	 */
	USER_INFO(0), IN_BAND(1);
	
	private int code;

	private InbandOutbandNegotiationCapV3Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static InbandOutbandNegotiationCapV3Enum getValue(int num) {
		switch (num) {
		case 0: { return 	USER_INFO	; }
		case 1: { return 	IN_BAND	; }
		default: { return null; }
		}
	}
}
