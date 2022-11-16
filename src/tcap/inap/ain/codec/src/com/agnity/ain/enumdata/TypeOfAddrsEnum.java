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
 * Enum for Type of address indicator
 * @author Nishant
 *
 */
public enum TypeOfAddrsEnum 
{
	DAILED_NUM(0),DESTINATION_NO(1),SUP_USR_PRO_CALLING_ADDR_failed(2),SUP_USR_PRO_CALLING_ADDR_not_screen(3),
	COMPLETION_NO(4),PORTED_DAILLED_NO(192), CAIN_ALTERNATE_OUTPULSE_NO(96), CAIN_SECOND_ALT_OUTPULSE_NO(97),
	CAIN_OVERFLOW_ROUTING_NO(98);

	private int code;

	private TypeOfAddrsEnum(int c){
		code = c;
	}

	public int getCode(){
		return code;
	}

	public static TypeOfAddrsEnum fromInt(int num) {
		switch (num){
		case 0: { return DAILED_NUM; }
		case 1: { return DESTINATION_NO; }
		case 2: { return SUP_USR_PRO_CALLING_ADDR_failed; }
		case 3: { return SUP_USR_PRO_CALLING_ADDR_not_screen; }
		case 4: { return COMPLETION_NO; }
		case 192: { return PORTED_DAILLED_NO; }
		case 96:{ return CAIN_ALTERNATE_OUTPULSE_NO;}
		case 97: { return CAIN_SECOND_ALT_OUTPULSE_NO; }
		case 98: { return CAIN_OVERFLOW_ROUTING_NO; }
		default: { return null; }
		}
	}
}
