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
 * For Redirection Information(for Originating Redirecting reason and Redirecting reason fields )
 *
 */
public enum OrigRedirectReasonEnum{
	/**
	 * 0-Unknown or Not Applicable,default
	 * 1-User busy
	 * 2-No reply
	 * 3-Unconditional
	 * 4->15 spare 
	 */

	NOT_APPLICABLE(0), USER_BUSY(1), NO_REPLY(2),UNCONDITIONAL(3);
	private int code;
	private OrigRedirectReasonEnum(int c){
		code = c;
	}
	
	public int getCode(){
		return code;
	}
	
	public static OrigRedirectReasonEnum fromInt(int num) {
		switch (num) 
		{
		case 0: { return NOT_APPLICABLE; }
		case 1: { return USER_BUSY; }
		case 2: { return NO_REPLY; }
		case 3: { return UNCONDITIONAL; }
		default: { return null; }
		}
	}
}
