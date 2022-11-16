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
 * @author sony
 *
 */
public enum CallTreatIndicatorEnum 
{
	NOT_USED(0),NO_OVERFLOW_RETURN(1),OVERFLOW(2),OFF_HOOK_NO_OVERFLOW_RETURN(3),OFF_HOOK_OVERFLOW(4),RINGBACK_NO_OVERFLOW_RETURN(5),
	RINGBACK_OVERFLOW_RETURN(6),RETURN(7),OFF_HOOK_RETURN(8),RINGBACK_RETURN(9);
	private int code;
	private CallTreatIndicatorEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static CallTreatIndicatorEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NOT_USED	; }
		case 1: { return 	NO_OVERFLOW_RETURN	; }
		case 2: { return 	OVERFLOW	; }
		case 3: { return 	OFF_HOOK_NO_OVERFLOW_RETURN	; }
		case 4: { return 	OFF_HOOK_OVERFLOW	; }
		case 5: { return 	RINGBACK_NO_OVERFLOW_RETURN	; }
		case 6: { return 	RINGBACK_OVERFLOW_RETURN	; }
		case 7: { return 	RETURN	; }
		case 8: { return 	OFF_HOOK_RETURN	; }
		case 9: { return 	RINGBACK_RETURN	; }
		default: { return null; }
		}
	}
}
