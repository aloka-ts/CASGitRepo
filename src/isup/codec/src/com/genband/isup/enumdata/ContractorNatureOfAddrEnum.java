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

// b) Nature of address indicator								           *
// 0 0 0 0 0 0 0     spare									           *	
// 0 0 0 0 0 0 1     subscriber number							           *
// 0 0 0 0 0 1 0     spare									           *
// 0 0 0 0 0 1 1     national number								           *
// 0 0 0 0 0 1 0 0 										           *
//        to         	 spare									           *
// 1 1 1 1 1 1 0 1 	

public enum ContractorNatureOfAddrEnum {
	
	/**
	 * 0-spare
	 * 1-subscriber number (national use)
	 * 2-spare
	 * 3-national (significant) number (national use)
	 * remaining spare
	 */
	SPARE(0), SUBS_NO(1), UNKNOWN(2), NATIONAL_NO(3);

	private int code;

	private ContractorNatureOfAddrEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ContractorNatureOfAddrEnum fromInt(int num) {
		switch (num) {
			case 0:  { return SPARE;   }
			case 1:  { return SUBS_NO; }
			case 2:  { return UNKNOWN; }
			case 3:  { return NATIONAL_NO; }
			default: { return null; }
		}
	}
}
