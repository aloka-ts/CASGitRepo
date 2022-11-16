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

public enum MultipleFrameCapV3Enum {

	/**
	 * 0-Multiple frame establishment not supported
	 * 1-Multiple frame establishment supported
	 */
	NOT_SUPPORTED(0), SUPPORTED(1);
	
	private int code;

	private MultipleFrameCapV3Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static MultipleFrameCapV3Enum getValue(int num) {
		switch (num) {
		case 0: { return 	NOT_SUPPORTED	; }
		case 1: { return 	SUPPORTED	; }
		default: { return null; }
		}
	}
}
