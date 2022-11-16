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

public enum RateAdaptionHeaderCapV3Enum {
	/**
	 * 0-Rate adaption header not included
	 * 1-Rate adaption header included
	 */
	NOT_INCLUDED(0), INCLUDED(1);
	
	private int code;

	private RateAdaptionHeaderCapV3Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static RateAdaptionHeaderCapV3Enum getValue(int num) {
		switch (num) {
		case 0: { return 	NOT_INCLUDED	; }
		case 1: { return 	INCLUDED	; }
		default: { return null; }
		}
	}

}
