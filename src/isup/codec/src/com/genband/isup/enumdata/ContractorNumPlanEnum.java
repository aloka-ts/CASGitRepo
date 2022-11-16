/*******************************************************************************
 *   Copyright (c) 2011 Agnity, Inc. All rights reserved.
 *   
 *   This is proprietary source code of Agnity, Inc. 
 *   
 *   Agnity, Inc. retains all intellectual property rights associated 
 *   with this source code. Use is subject to license terms.
 *   
 *   This source code contains trade secrets owned by Agnity, Inc.
 *   Confidentiality of this computer program must be maintained at 
 *   all times, unless explicitly authorized by Agnity, Inc.
 *******************************************************************************/
package com.genband.isup.enumdata;

// This class defines number plan for contractor number as defined 
// TTC-JT Q763, section 3.106
// c) Numbering plan indicator						                       	           *
// 0 0 0    spare										           *
// 0 0 1    ISDN (telephony) numbering plan (Recommendation E.164 (E.163))	                       *
// 0 1 0 											           *
//    to       spare										           *
// 1 1 1										                        *  


public enum ContractorNumPlanEnum {

	/**
	 * 0-spare
	 * 1-ISDN 
	 * Spare remaining
	 */
	SPARE(0), ISDN(1);

	private int code;

	private ContractorNumPlanEnum (int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static ContractorNumPlanEnum fromInt(int num) {
		switch (num) {
		case 0: { return SPARE; }
		case 1: { return ISDN; }
		default: { return null; }
		}
	}
}
