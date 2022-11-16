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

public enum TransferModeCapV3Enum {
	
	/**
	 * 0-Circuit mode
	 * 2-Packet mode
	 */
	CIRCUIT_MODE(0), PACKET_MODE(2);
	
	private int code;

	private TransferModeCapV3Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static TransferModeCapV3Enum getValue(int num) {
		switch (num) {
		case 0: { return 	CIRCUIT_MODE	; }
		case 2: { return 	PACKET_MODE	; }
		default: { return null; }
		}
	}
}
