/*******************************************************************************
 *   Copyright (c) 2014 Agnity, Inc. All rights reserved.
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
package com.agnity.ain.enumdata;
/**
 * Enum for Mobile Additional Party's Category 1
 * @author Mriganka
 *
 */
public enum MobileAdtnlPartyCat1Enum {
	/**
	 * 0-spare
	 * 1-mobile (automobile and portable phone service)
	 * 2-mobile (maritime telephone service)	 * 3-mobile (in-flight telephone service)
	 * 4-mobile (pager)	
	 */
	
	SPARE(0), AUTO_PORTABLE_PHONE(1), MARITIME_TELEPHONE(2), INFLIGHT_TELEPHONE(3), PAGER(4), PHS_SRV(5);
	private int code;
	
	private MobileAdtnlPartyCat1Enum(int c){
		code = c;
	}
	
	public int getCode() {
		return code;
	}
	public static MobileAdtnlPartyCat1Enum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }			case 1: { return AUTO_PORTABLE_PHONE; }
			case 2: { return MARITIME_TELEPHONE; }
			case 3: { return INFLIGHT_TELEPHONE; }
			case 4: { return PAGER; }
			case 5: { return PHS_SRV; }
			default: { return null; }
		}
	}
}
