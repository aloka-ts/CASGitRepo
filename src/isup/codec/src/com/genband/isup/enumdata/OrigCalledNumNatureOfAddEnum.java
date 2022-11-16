/*******************************************************************************
 *   Copyright (c) 2011 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.genband.isup.enumdata;

// This class implements Nature of Address defined for Original Called Number 
// as per TTC-JT-Q763, section 3.39 and as defined below. 
// 0 0 0 0 0 0 0	spare
// 0 0 0 0 0 0 1	subscriber number (national use)
// 0 0 0 0 0 1 0	unknown (national use)
// 0 0 0 0 0 1 1	national (significant) number (national use)
// 0 0 0 0 1 0 0	international number
//  0 0 0 0 1 0 1
//         to
// 1 1 0 1 1 1 1	spare
// 1 1 1 0 0 0 0
//        to
// 1 1 1 1 1 1 0	reserved for national use
// 1 1 1 1 1 1 1	spare

public enum OrigCalledNumNatureOfAddEnum {

	/**
	 * 0-spare
	 * 1-subscriber number (national use)
	 * 2-unknown (national use)
	 * 3-national (significant) number (national use)
	 * 4-international number
	 * Spare remaining
	 */
	SPARE(0), SUBS_NO(1), UNKNOWN(2), NATIONAL_NO(3), INTER_NO(4);

	private int code;

	private OrigCalledNumNatureOfAddEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static OrigCalledNumNatureOfAddEnum fromInt(int num) {
		switch (num) {
		case 0: { return SPARE; }
		case 1: { return SUBS_NO; }
		case 2: { return UNKNOWN; }
		case 3: { return NATIONAL_NO; }
		case 4: { return INTER_NO; }
		default: { return null; }
		}
	}
}
