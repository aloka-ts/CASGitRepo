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
 *holdTreatmentIndicator        [50] OCTET STRING (SIZE(1
-- applicable to InitialDP, Connect and ContinueWithArgument 
-- acceptHoldRequest  'xxxx xx01'B 
-- rejectHoldRequest  'xxxx xx10'B 
-- if absent from Connect or ContinueWithArgument, 
-- then CAMEL service does not affect call hold treatment 
 */
public enum HoldTreatmentIndCapV3Enum {
	
	ACCEPT_HOLD_REQ(1), REJECT_HOLD_REQ(2);
		
		private int code;
		
		private HoldTreatmentIndCapV3Enum(int c){
			this.code=c;
		}
		
		public int getCode() {
			return code;
		}
		
		public static HoldTreatmentIndCapV3Enum getValue(int tag){
			switch (tag) {
			case 1: return ACCEPT_HOLD_REQ; 
			case 2: return REJECT_HOLD_REQ;
			default: return null;
			}
		}
}
