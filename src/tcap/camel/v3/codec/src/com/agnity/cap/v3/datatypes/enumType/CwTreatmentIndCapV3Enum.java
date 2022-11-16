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
 *cwTreatmentIndicator        [51] OCTET STRING (SIZE(1))       OPTIONAL, 
-- applicable to InitialDP, Connect and ContinueWithArgument 
-- acceptCw 'xxxx xx01'B 
-- rejectCw 'xxxx xx10'B 
-- if absent from Connect or ContinueWithArgument, 
-- then CAMEL service does not affect call waiting treatment 
 */
public enum CwTreatmentIndCapV3Enum {
	
	ACCEPT_CW(1), REJECT_CW(2);
		
		private int code;
		
		private CwTreatmentIndCapV3Enum(int c){
			this.code=c;
		}
		
		public int getCode() {
			return code;
		}
		
		public static CwTreatmentIndCapV3Enum getValue(int tag){
			switch (tag) {
			case 1: return ACCEPT_CW; 
			case 2: return REJECT_CW;
			default: return null;
			}
		}
}
