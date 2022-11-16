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

public enum UserInfoLayer2ProtocolCapV2Enum {

	/**
	 * 2- Recommendation Q.921
	 * 6- Recommendation X.25
	 * 12- LAN logical link control
	 */
	
	RECOMMEND_Q_921(2), RECOMMEND_X_25(6), LAN_LOGICAL_LINK_CONTROL(12);
	
	private int code;

	private UserInfoLayer2ProtocolCapV2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static UserInfoLayer2ProtocolCapV2Enum getValue(int num) {
		switch (num) {
		case 2: { return 	RECOMMEND_Q_921	; }
		case 6: { return 	RECOMMEND_X_25	; }
		case 12: { return 	LAN_LOGICAL_LINK_CONTROL	; }
		default: { return null; }
		}
	}
}
