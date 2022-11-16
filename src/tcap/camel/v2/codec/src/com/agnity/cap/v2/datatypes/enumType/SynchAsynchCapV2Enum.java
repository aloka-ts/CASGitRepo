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

import java.lang.annotation.RetentionPolicy;

/**
 * 
 * @author rnarayan
 *
 */
public enum SynchAsynchCapV2Enum {

	/**
	 * 0-Synchronous data
	 * 1-Asynchronous data
	 */
	SYNCH_DATA(0), ASYNCH_DATA(1);
	
	private int code;

	private SynchAsynchCapV2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static SynchAsynchCapV2Enum getValue(int num) {
		switch (num) {
		case 0: { return 	SYNCH_DATA	; }
		case 1: { return 	ASYNCH_DATA	; }
		default: { return null; }
		}
	}
}
