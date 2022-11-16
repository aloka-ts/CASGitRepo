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


public enum HighLayerCharacteristicsIdentCapV3Enum {

	/*
	 * Bits
		7 6 5 4 3 2 1
		0 0 0 0 0 0 1 Telephony
		0 0 0 0 1 0 0 Facsimile Group 2/3 (Recommendation F.182 [68])
		0 1 0 0 0 0 1 Facsimile Group 4 Class I (Recommendation F.184 [69])
		0 1 0 0 1 0 0 Facsimile service Group 4, Classes II ad III (Recommendation F.184)
		0 1 0 1 0 0 0 (Note 7)
		0 1 1 0 0 0 1 (Note 7)
		0 1 1 0 0 1 0 Syntax based Videotex (Recommendation F.300 [73] and T.102 [74])
		0 1 1 0 0 1 1 International Videotex interworking via gateways or interworking units
		(Recommendation F.300 and T.101 [75])
		0 1 1 0 1 0 1 Telex service (Recommendation F.60 [76])
		0 1 1 1 0 0 0 Message Handling Systems (MHS) (X.400-series Recommendation [77])
		1 0 0 0 0 0 1 OSI application (Note 6) (X.200-series Recommendations [78])
		1 0 0 0 0 1 0 FTAM application (ISO 8571)
		1 0 1 1 1 1 0 Reserved for maintenance (Note 8)
		1 0 1 1 1 1 1 Reserved for management (Note 8)
		1 1 0 0 0 0 0 Videotelephony (Recommendations F.720 [91] and F.721 [79]) and F.731 profile 1a)
		(Note 9)
		1 1 0 0 0 0 1 Videoconferencing Recommendation F.702 [94] and F.731 [97] Profile 1b (Note 9)
		1 1 0 0 0 1 0 Audiographic conferencing Recommendations F.702 [94] and F.731 [97] (including at
		least profile 2a2 and optionally 2a1, 2a3, 2b1, 2b2, and 2bc) (Notes 9 and 10)
		1 1 0 0 0 1 1
		through Reserved for audiovisual service (F.700-series Recommendations [80])
		1 1 0 0 1 1 1
		1 1 0 1 0 0 0 Multimedia services F.700-series Recommendations [80] (Note 9)
		1 1 0 0 0 1 1
		through Reserved for audiovisual services (F.700-series Recommendations [80])
		1 1 0 1 1 1 1
		1 1 1 1 1 1 1 Reserved
		All other values are reserved
		Extended high layer characteristics identification (octet 4a for maintenance or management)
		
		NOTE 5 – The coding above applies in case of "Coding standard" = "ITU-T Standard" and "Presentation
		method of protocol profile" = "High layer protocol profile".
		NOTE 6 – Further compatibility checking will be executed by the OSI high layer protocol.
		NOTE 7 – Codepoints are added only to those services for which ITU-T Recommendations are available.
		See also the I.241-series of Recommendations [34].
		NOTE 8 – When this coding is included, octet 4 may be followed by octet 4a.
		NOTE 9 – When this coding is used, octet 4 may be followed by octet 4a.
		NOTE 10 – The multimedia services identified by this codepoint mush have a mandatory common core
		functionality of speech that will ensure a minimum capability to communicate.
		NOTE 11 – This codepoint was previously allocated for an F.200-series Recommendation that has been
		deleted.
	 */
	
	TELEPHONY(1),FACSIMILE_GROUP_2_3(4),FACSIMILE_GROUP_4_class_I(33),FACSIMILE_SERVICE_GROUP_4_class_II_III(36),
    SYNTAX_BASED_VIDEOTEX(50), INTERNATIONAL_VIDEOTEX(51), TELEX_SERVICE(53), MESSAGE_HANDLING_SYATEM(56),
    OSI_APP(65);
	
	int code;
	
	private HighLayerCharacteristicsIdentCapV3Enum(int code){
		this.code = code;
	}
	
	public int getCode() {
		return code;
	}
	
	public static  HighLayerCharacteristicsIdentCapV3Enum getValue(int tag){
		switch (tag) {
		case 1: return TELEPHONY;
		case 4: return FACSIMILE_GROUP_2_3;
		case 33: return FACSIMILE_GROUP_4_class_I;
		case 36: return FACSIMILE_SERVICE_GROUP_4_class_II_III;
		case 50: return SYNTAX_BASED_VIDEOTEX;
		case 51: return INTERNATIONAL_VIDEOTEX;
		case 53: return TELEX_SERVICE;
		case 56: return MESSAGE_HANDLING_SYATEM;
		default: return null;

		}
	}

}
