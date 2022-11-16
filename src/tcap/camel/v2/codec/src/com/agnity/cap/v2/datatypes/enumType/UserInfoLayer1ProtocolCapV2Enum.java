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

public enum UserInfoLayer1ProtocolCapV2Enum {
	
	/**
	 * 1- ITU-T standardized rate adaption V.110, I.460 and X.30
	 * 2- Recommendation G.711[10] u-law
	 * 3- Recommendation G.711 A-law
	 * 4- Recommendation G.721 and I.460
	 * 5- Recommendation H.221 and H.242
	 * 6- Recommendation H.223 and H.245
	 * 7- Non-ITU-T standardized rate adaption
	 * 8- ITU-T standardized rate adaption V.120
	 * 9- ITU-T standardized rate adaption X.31
	 */
	ITUT_V110_I460_X30(1), RECOMMEND_G711_U(2), RECOMMEND_G711_A(3), RECOMMEND_G721(4), RECOMMEND_H221_H242(5), RECOMMEND_H223_H245(6), 
	NON_ITUT_STD(7), ITUT_V120(8), ITUT_X31(9);
	
	private int code;

	private UserInfoLayer1ProtocolCapV2Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static UserInfoLayer1ProtocolCapV2Enum getValue(int num) {
		switch (num) {
		case 1: { return 	ITUT_V110_I460_X30	; }
		case 2: { return 	RECOMMEND_G711_U	; }
		case 3: { return 	RECOMMEND_G711_A	; }
		case 4: { return 	RECOMMEND_G721	; }
		case 5: { return 	RECOMMEND_H221_H242	; }
		case 6: { return 	RECOMMEND_H223_H245	; }
		case 7: { return 	NON_ITUT_STD	; }
		case 8: { return 	ITUT_V120	; }
		case 9: { return 	ITUT_X31	; }
		default: { return null; }
		}
	}
}
