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
/**
 * ref-3GPP TS 29.078 V4.9.0
 * @author rnarayan
 *conferenceTreatmentIndicator    [1] OCTET STRING (SIZE(1))  OPTIONAL, 
  -- acceptConferenceRequest  'xxxx xx01'B 
  -- rejectConferenceRequest  'xxxx xx10'B 
  -- if absent from Connect or ContinueWithArgument, 
  -- then CAMEL service does not affect conference treatement 
 */
public enum ConferenceTreatmentIndCapV3Enum {

	ACCEPT_CONFERENCE_REQ(1), REJECT_CONFERENCE_REQ(2);
	
	private int code;
	
	private ConferenceTreatmentIndCapV3Enum(int c){
		this.code=c;
	}
	
	public int getCode() {
		return code;
	}
	
	public static ConferenceTreatmentIndCapV3Enum getValue(int tag){
		switch (tag) {
		case 1: return ACCEPT_CONFERENCE_REQ; 
		case 2: return REJECT_CONFERENCE_REQ;
		default: return null;
		}
	}
	
}
