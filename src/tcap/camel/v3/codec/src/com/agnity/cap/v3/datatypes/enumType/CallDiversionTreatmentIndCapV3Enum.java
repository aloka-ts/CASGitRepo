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
 *callDiversionTreatmentIndicator [2] OCTET STRING (SIZE(1))    OPTIONAL, 
-- callDiversionAllowed   'xxxx xx01'B 
-- callDiversionNotAllowed  'xxxx xx10'B 
-- if absent from Connect or ContinueWithArgument, 
-- then CAMEL service does not affect call diversion treatment 
 */
public enum CallDiversionTreatmentIndCapV3Enum {

	 CALL_DIVERSION_ALLOWED(1), CALL_DIVERSION_NOT_ALLOWED(2);
		
		private int code;
		
		private CallDiversionTreatmentIndCapV3Enum(int c){
			this.code=c;
		}
		
		public int getCode() {
			return code;
		}
		
		public static CallDiversionTreatmentIndCapV3Enum getValue(int tag){
			switch (tag) {
			case 1: return CALL_DIVERSION_ALLOWED; 
			case 2: return CALL_DIVERSION_NOT_ALLOWED;
			default: return null;
			}
		}
}
