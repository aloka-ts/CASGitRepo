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

import com.agnity.cap.v2.util.DataType;

public enum InformationTransferCapabilityCapV2Enum {
     /*
	 Bits
     54321
     00000 - Speech
     01000 - Unrestricted digital information
     01001 - Restricted digital information
     10000 - 3.1 kHz audio
     10001 - Unrestricted digital information with tones/announcements (Note 2)
     11000 - Video
     */
	
	SPEECH(0),UNRESTRICTED_DIGITAL_INFORMATION(8), RESTRICTED_DIGITAL_INFORMATION(9),
	KHZ_3_1_AUDIO(16), UNRESTRICTED_DIGITAL_INFORMATION_WITH_TONES_OR_ANNOUNCEMENT(17),
	VIDEO(24);
	
	private int code=0;
	
	private InformationTransferCapabilityCapV2Enum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static InformationTransferCapabilityCapV2Enum getValue(int tag){
		switch (tag) {
		case 0: return SPEECH; 
		case 8: return UNRESTRICTED_DIGITAL_INFORMATION; 
		case 9: return RESTRICTED_DIGITAL_INFORMATION; 
		case 16: return KHZ_3_1_AUDIO; 
		case 17: return UNRESTRICTED_DIGITAL_INFORMATION_WITH_TONES_OR_ANNOUNCEMENT; 
		case 24: return VIDEO; 
		default: return null;
		}
	}
}
