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

public enum FlowControlOnRxCapV2Enum {

	/**
	 * 0-Can not accept data with flow control mechanism
	 * 1-Can accept data with flow control mechanism
	 */
	CAN_NOT_ACCEPT(0), CAN_ACCEPT(1);
	
	private int code;

	private FlowControlOnRxCapV2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static FlowControlOnRxCapV2Enum getValue(int num) {
		switch (num) {
		case 0: { return 	CAN_NOT_ACCEPT	; }
		case 1: { return 	CAN_ACCEPT	; }
		default: { return null; }
		}
	}
}
