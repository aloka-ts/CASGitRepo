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
 * This class is used in case of nature of number for calling parameter in AINDigits
 */
public enum CalgNatOfNumEnum
{
	/**
	 * 0-Unknown or Not Applicable,default
	 * 1-Unique Subscriber number
	 * 2-spare,reserved for national use
	 * 3-Unique National number 
	 * 4-unique International number
	 * 5->112 spare 
	 * 113-Non-unique subscriber number
	 * 114-spare,reserved for national use
	 * 115-Non-unique National number 
	 * 116-Non-unique International number 
	 * 117-spare 
	 * 118-spare 
	 * 119-Test Line test code
	 * 120->126 Reserved for Network Specific use
	 * 127-Reserved  
	 */

	NOT_APPLICABLE(0), UNIQUE_SUBS_NUM(1), SPARE(2),UNIQUE_NAT_NUM(3),UNIQUE_INTER_NAT_NUM(4),NON_UNIQUE_SUBS_NUM(113),
	NON_UNIQUE_NAT_NUM(115),NON_UNIQUE_INTER_NAT_NUM(116),TEST_CODE(119),RESERVED(127);
	private int code;
	private CalgNatOfNumEnum(int c){
		code = c;
	}
	public int getCode(){
		return code;
	}
	public static CalgNatOfNumEnum fromInt(int num){
		switch (num){
		case 0: { return NOT_APPLICABLE; }
		case 1: { return UNIQUE_SUBS_NUM; }
		case 2: { return SPARE; }
		case 3: { return UNIQUE_NAT_NUM; }
		case 4: { return UNIQUE_INTER_NAT_NUM; }
		case 113: { return NON_UNIQUE_SUBS_NUM; }
		case 115: { return NON_UNIQUE_NAT_NUM; }
		case 116: { return NON_UNIQUE_INTER_NAT_NUM; }
		case 119: { return TEST_CODE; }
		case 127: { return RESERVED; }
		default: { return null; }
		}
	}
}
