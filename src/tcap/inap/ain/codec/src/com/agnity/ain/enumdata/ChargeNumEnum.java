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
 * This Class is used for Charge number in case of AINDigits
 */
public enum ChargeNumEnum 
{
	/**
	 * 0-Spare 
	 * 1-ANI of the calling party;Subscriber number
	 * 2-ANI not available otr not provided
	 * 3-ANI of the calling party; national number 
	 * 4-Spare 
	 * 5-ANI of the called party included;Subscriber number 
	 * 6-ANI of the called party;Not included
	 * 7-ANI of the called party included;national number
	 * 8->119-Spare 
	 * 120->126-Reserved for Network Specific use	 
	 * 127-Reserved  
	 */

	SPARE(0), ANI_CLG_SUBS_NUM(1), ANI_NOT_PROVIDED(2),ANI_CLG_NAT_NUM(3),ANI_CALLED_SUBS_NUM(5),
	ANI_CALLED_NOT_INCLUDED(6),ANI_CALLED_INCLUDED_NAT_NUM(7),RESERVED(127);
	private int code;
	private ChargeNumEnum(int c){
		code = c;
	}
	public int getCode(){
		return code;
	}
	public static ChargeNumEnum fromInt(int num){
		switch (num){
		case 0: { return SPARE; }
		case 1: { return ANI_CLG_SUBS_NUM; }
		case 2: { return ANI_NOT_PROVIDED; }
		case 3: { return ANI_CLG_NAT_NUM; }
		case 5: { return ANI_CALLED_SUBS_NUM; }
		case 6: { return ANI_CALLED_NOT_INCLUDED; }
		case 7: { return ANI_CALLED_INCLUDED_NAT_NUM; }
		case 127: { return RESERVED; }
		default: { return null; }
		}
	}
}
