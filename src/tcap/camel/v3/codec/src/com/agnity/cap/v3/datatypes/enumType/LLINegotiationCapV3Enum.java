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

public enum LLINegotiationCapV3Enum {

	/**
	 * 0-Default LLI=256 only
	 * 1-Full protocol negotiation
	 */
	DEFAULT(0), FULL(1);
	
	private int code;

	private LLINegotiationCapV3Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static LLINegotiationCapV3Enum getValue(int num) {
		switch (num) {
		case 0: { return 	DEFAULT	; }
		case 1: { return 	FULL	; }
		default: { return null; }
		}
	}
}
