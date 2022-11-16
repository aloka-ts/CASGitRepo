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
public enum ClgPrsntRestIndEnum 
{
	/**
	 * 0-Presentation allowed  
	 * 1-Presentation Restricted (default)	 
	 * 2-Number unavailable	 
	 * 3-Reserved	 
	 */

	PRESENT_ALLWD(0), PRESENT_RESTRICT(1), NUM_UNAVALIABLE(2),RESERVED(3);	
	private int code;
	private ClgPrsntRestIndEnum(int c){
		code = c;
	}
	public int getCode(){
		return code;
	}
	public static ClgPrsntRestIndEnum fromInt(int num){
		switch (num){
			case 0: { return PRESENT_ALLWD; }
			case 1: { return PRESENT_RESTRICT; }
			case 2: { return NUM_UNAVALIABLE; }
			case 3: { return RESERVED; }
			default: { return null; }

		}
	}
}
