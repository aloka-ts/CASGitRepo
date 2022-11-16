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
 * Enum for Digit Category
 * @author nishantsharma
 *
 */
public enum DigitCatEnum {
	/**
	 * 0: Reserved for account code
	 * 1: Reserved for authentication code
	 * 2: Reserved for private network travelling class mark
	 * 3: spare
	 * 4: OriginatingGUBB
	 * 5: RedirectingGUBB 	 * 6: TerminatingGUBB
	 * 7->14 : Spare	  	 * 15: bill-to-number	  	 * 16->30:reserved for network specific use  	 * 31: reserved for extension
	 */
	RESERVED_ACCOUNT_CODE(0), RESERVED_AUTHENTICATION_CODE(1), RESERVED_PRIVATE_NW(2), SPARE(3), 
	ORIGINATINGUBB(4),REDIRECTINGGUBB(5), TERMINATINGGUBB(6), BILL_TO_NUMBER(15),RESERVED_FOR_EXTENSION(31);
	private int code;
	private DigitCatEnum(int c){
		code = c;
	}
	public int getCode(){
		return code;
	}
	public static DigitCatEnum fromInt(int num){
		switch (num){
			case 0: { return RESERVED_ACCOUNT_CODE; }
			case 1: { return RESERVED_AUTHENTICATION_CODE; }
			case 2: { return RESERVED_PRIVATE_NW; }
			case 3: { return SPARE; }
			case 4: { return ORIGINATINGUBB; }
			case 5: { return REDIRECTINGGUBB; }
			case 6: { return TERMINATINGGUBB; }
			case 15: { return BILL_TO_NUMBER; }
			case 31: { return RESERVED_FOR_EXTENSION; }
			default: { return null; }
		}
	}
}
