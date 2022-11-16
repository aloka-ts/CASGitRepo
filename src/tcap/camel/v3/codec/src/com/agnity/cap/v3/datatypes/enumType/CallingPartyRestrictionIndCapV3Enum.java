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
 *callingPartyRestrictionIndicator [4] OCTET STRING (SIZE(1))   OPTIONAL, 
-- noINImpact        'xxxx xx01'B 
-- presentationRestricted  'xxxx xx10'B 
-- if absent from Connect or ContinueWithArgument, 
-- then CAMEL service does not affect calling party restriction treatment 
 */
public enum CallingPartyRestrictionIndCapV3Enum {
	
	NO_IN_IMPACT(1), PRESENTATION_RESTRICTED(2);
		
		private int code;
		
		private CallingPartyRestrictionIndCapV3Enum(int c){
			this.code=c;
		}
		
		public int getCode() {
			return code;
		}
		
		public static CallingPartyRestrictionIndCapV3Enum getValue(int tag){
			switch (tag) {
			case 1: return NO_IN_IMPACT; 
			case 2: return PRESENTATION_RESTRICTED;
			default: return null;
			}
		}
}
