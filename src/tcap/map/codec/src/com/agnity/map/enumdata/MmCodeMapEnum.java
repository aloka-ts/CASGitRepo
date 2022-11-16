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

/**
 * 
 * @author sanjay
 *
 */
public enum MmCodeMapEnum {
	// CS domain MM events
	LU_IN_SAME_VLR (0x00),
	LU_TO_OTHER_VLR(0x01),
	IMSI_ATTACH(0x02),
	MS_INITIATED_IMSI_DETACH(0x03),
	NW_INITIATED_IMSI_DETACH(0x40),
	
	// PS domain MM Events
	RA_UPDATE_IN_SAME_SGSN(0x80),
	RA_UPDATE_TO_OTHER_SGSN_UPDATE_FROM_NEW_SGSN(0x81),
	RA_UPDATE_TO_OTHER_SGSN_DISCONN_BY_DETACH(0x82),
	GPRS_ATTACH(0x83),
	MS_INITIATED_GPRS_DETACH(0x84),
	NW_INITIATED_GPRS_DETACH(0x85),
	NW_INITIATED_TRF_TO_MS_NOT_REACHABLE_FOR_PAGING(0x86);
	
	private int code;
	
	private MmCodeMapEnum(int code) {
		this.code = code;
	}
	
	public int getCode() {
		return this.code;
	}
	
	public static MmCodeMapEnum getValue(int tag) {
		switch(tag) {
		case 0x00: return LU_IN_SAME_VLR;
		case 0x01: return LU_TO_OTHER_VLR;
		case 0x02: return LU_TO_OTHER_VLR;
		case 0x03: return MS_INITIATED_IMSI_DETACH;
		case 0x40: return NW_INITIATED_IMSI_DETACH;
		case 0x80: return RA_UPDATE_IN_SAME_SGSN;
		case 0x81: return RA_UPDATE_TO_OTHER_SGSN_UPDATE_FROM_NEW_SGSN;
		case 0x82: return RA_UPDATE_TO_OTHER_SGSN_DISCONN_BY_DETACH;
		case 0x83: return GPRS_ATTACH;
		case 0x84: return MS_INITIATED_GPRS_DETACH;
		case 0x85: return NW_INITIATED_GPRS_DETACH;
		case 0x86: return NW_INITIATED_TRF_TO_MS_NOT_REACHABLE_FOR_PAGING;
		default: return null;
			
		}
	}
}
