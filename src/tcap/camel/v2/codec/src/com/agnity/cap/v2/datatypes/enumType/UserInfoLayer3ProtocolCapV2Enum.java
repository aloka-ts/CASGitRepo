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

public enum UserInfoLayer3ProtocolCapV2Enum {

	/**
	 * 2- Recommendation Q.931
	 * 6- Recommendation X.25, packet layer
	 * 11- ISO?IEC TR 9577
	 */
	
	RECOMMEND_Q_931(2), RECOMMEND_X_25_PACKET_LAYER(6), ISO_IEC_TR_9577(12);
	
	private int code;

	private UserInfoLayer3ProtocolCapV2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static UserInfoLayer3ProtocolCapV2Enum getValue(int num) {
		switch (num) {
		case 2: { return 	RECOMMEND_Q_931	; }
		case 6: { return 	RECOMMEND_X_25_PACKET_LAYER	; }
		case 11: { return 	ISO_IEC_TR_9577	; }
		default: { return null; }
		}
	}
}
