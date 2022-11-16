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
public enum CarrierFormatSelectionEnum {
	/**
	 * 0-No Indication
	 * 1-Selected carrier identification code pre-subscribed and not input by calling party
	 * 2-Selected carrier identification code pre-subscribed and input by calling party
	 * 3-Selected carrier identification code pre-subscribed and not indication of whether input  by calling party
	 * 4-Selected carrier identification code not pre-subscribed and input by calling party
	 * 5->254 spare 	 
	 * 255-Reserved  
	 */
	NO_INDICATION(0),PRE_SUBS_NO_INPUT(1),PRE_SUBS_INPUT(2),PRE_SUBS_NO_IND_INPUT(3),NOT_PRE_SUBS_INPUT(4),RESERVED(255);
	private int code;
	private CarrierFormatSelectionEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	public static CarrierFormatSelectionEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NO_INDICATION	; }
		case 1: { return 	PRE_SUBS_NO_INPUT	; }
		case 2: { return 	PRE_SUBS_INPUT	; }
		case 3: { return 	PRE_SUBS_NO_IND_INPUT	; }
		case 4: { return 	NOT_PRE_SUBS_INPUT	; }
		case 255: { return 	RESERVED	; }
		default: { return null; }
		}
	}
}
