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

public enum NicOnTxCapV2Enum {
	/**
	 * 0-Not required to send data with network independent clock
	 * 1-Required to send data with network independent clock
	 */
	NOT_REQUIRED(0), REQUIRED(1);
	
	private int code;

	private NicOnTxCapV2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NicOnTxCapV2Enum getValue(int num) {
		switch (num) {
		case 0: { return 	NOT_REQUIRED	; }
		case 1: { return 	REQUIRED	; }
		default: { return null; }
		}
	}


}
