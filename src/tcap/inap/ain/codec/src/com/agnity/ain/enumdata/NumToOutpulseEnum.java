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
/**
 * 
 */
package com.agnity.ain.enumdata;

/**
 * @author nishantsharma
 *
 */
public enum NumToOutpulseEnum 
{
	OUTPULSE_NUM(0),NORMAL_ROUTING_NUM(1);
	
	private int code;

	private NumToOutpulseEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static NumToOutpulseEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	OUTPULSE_NUM	; }
		case 1: { return 	NORMAL_ROUTING_NUM	; }
		default: { return null; }
		}
	}	
}
