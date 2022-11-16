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

package com.agnity.map.enumdata;

public enum SpecificCSIWithdrawMapEnum {
	O_CSI (0),
	SS_CSI (1),
	TIF_CSI (2),
	D_CSI (3),
	VT_CSI (4),
	MO_SMS_CSI (5),
	M_CSI (6),
	GPRS_CSI (7),
	T_CSI (8),
	MT_SMS_CSI (9),
	MG_CSI (10),
	O_IM_CSI (11),
	D_IM_CSI (12),
	VT_IM_CSI(13);
	
	int code;
	private SpecificCSIWithdrawMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static SpecificCSIWithdrawMapEnum getValue(int tag) {
		switch(tag) {
			case 0: return O_CSI;
			case 1: return SS_CSI;
			case 2: return TIF_CSI;
			case 3: return D_CSI;
			case 4: return VT_CSI;
			case 5: return MO_SMS_CSI;
			case 6: return M_CSI;
			case 7: return GPRS_CSI;
			case 8: return T_CSI;
			case 9: return MT_SMS_CSI;
			case 10: return MG_CSI;
			case 11: return O_IM_CSI;
			case 12: return D_IM_CSI;
			case 13: return VT_IM_CSI;
			default: return null;
		}
	}
}
