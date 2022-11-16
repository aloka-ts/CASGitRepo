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
 *callCompletionTreatmentIndicator  [2] OCTET STRING (SIZE(1))  OPTIONAL, 
-- acceptCallCompletionServiceRequest  'xxxx xx01'B, 
-- rejectCallCompletionServiceRequest  'xxxx xx10'B 
-- if absent from Connect or ContinueWithArgument, 
-- then CAMEL service does not affect call completion treatment 
 */
public enum CallCompletionTreatmentIndCapV3Enum {

   ACCEPT_CALL_COMPLETION_SERVICE_REQ(1), REJECT_CALL_COMPLETION_SERVICE_REQ(2);
	
	private int code;
	
	private CallCompletionTreatmentIndCapV3Enum(int c){
		this.code=c;
	}
	
	public int getCode() {
		return code;
	}
	
	public static CallCompletionTreatmentIndCapV3Enum getValue(int tag){
		switch (tag) {
		case 1: return ACCEPT_CALL_COMPLETION_SERVICE_REQ; 
		case 2: return REJECT_CALL_COMPLETION_SERVICE_REQ;
		default: return null;
		}
	}
}
