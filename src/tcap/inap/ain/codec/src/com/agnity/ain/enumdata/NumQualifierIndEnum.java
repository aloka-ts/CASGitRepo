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
 * Enum for Number Qualifier Indicator
 * @author Mriganka
 *
 */
public enum NumQualifierIndEnum {
	/**
	 * 0-reserved (dialled digits) (national use)
	 * 1-additional called number (national use)
	 * 2-reserved (supplemental user provided CALLING number failed network screening) (national use)
	 * 3-reserved (supplemental user provided CALLING number not screened) (national use)
	 * 4-reserved (redirecting terminating number) (national use)
	 * 5-additional connected number
	 * 6-additional CALLING party number
	 * 7-reserved for additional original called number
	 * 8-reserved for additional redirecting number
	 * 9-reserved for additional redirection number
	 * 10-reserved (used in 1992 version)
	 * 11->127-spare
	 * 128->254-reserved for national use
	 * 255-reserved for expansion
	 */
	RESERVED(0),ADD_CALLED_NO(1), ADD_CONNECTED_NO(5), ADD_CALLING_NO(6), SPARE(11) ;
	private int code;
	
	private NumQualifierIndEnum(int c) {
		code = c;
	}
	public int getCode() {
		return code;
	}
	
	public static NumQualifierIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return RESERVED; }
			case 1: { return ADD_CALLED_NO; }
			case 5: { return ADD_CONNECTED_NO; }
			case 6: { return ADD_CALLING_NO; }
			case 11: { return SPARE; }
			default: { return null; }
		}
	}
}
