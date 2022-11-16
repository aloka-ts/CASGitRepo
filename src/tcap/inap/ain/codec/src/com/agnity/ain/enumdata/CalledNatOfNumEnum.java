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
 * This class is used in case of nature of number for called parameter in AINDigits
 */
public enum CalledNatOfNumEnum 
{
	
	/**
	 * 0-Not Applicable
	 * 1-Subscriber number
	 * 2-spare,reserved for national use
	 * 3-National number 
	 * 4-International number
	 * 5->112 spare 
	 * 113-subscriber number,operator requested(0+ call)
	 * 114-national number,operator requested(0+ call)
	 * 115-International number,operator requested(0+ call)
	 * 116-No address present,operator requested
	 * 117-No address present,cut through call to carrier
	 * 118-950 + call form local exchange carrier public station or hotel line or non EAEO
	 * 119-Test Line test code
	 * 120->126 Reserved for Network Specific use
	 */

	NOT_APPLICABLE(0), SUBS_NUM(1), SPARE(2),NAT_NUM(3),INTER_NAT_NUM(4),SUBS_NUM_OPERTR_REQ(113),NAT_NUM_OPERTR_REQ(114),
	INTER_NAT_NUM_OPERTR_REQ(115),NO_ADDR_PRSNT_OPERTR_REQ(116),NO_ADDR_PRSNT_CUT(117),CALL_LOCAL_EXCHANGE(118),TEST_CODE(119);
	private int code;
	private CalledNatOfNumEnum(int c){
		code = c;
	}
	public int getCode(){
		return code;
	}
	public static CalledNatOfNumEnum fromInt(int num){
		switch (num){
		case 0: { return NOT_APPLICABLE; }
		case 1: { return SUBS_NUM; }
		case 2: { return SPARE; }
		case 3: { return NAT_NUM; }
		case 4: { return INTER_NAT_NUM; }
		case 113: { return SUBS_NUM_OPERTR_REQ; }
		case 114: { return NAT_NUM_OPERTR_REQ; }
		case 115: { return INTER_NAT_NUM_OPERTR_REQ; }
		case 116: { return NO_ADDR_PRSNT_OPERTR_REQ; }
		case 117: { return NO_ADDR_PRSNT_CUT; }
		case 118: { return CALL_LOCAL_EXCHANGE; }
		case 119: { return TEST_CODE; }
		default: { return null; }
		}
	}
}
