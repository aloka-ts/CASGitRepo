/*******************************************************************************
 *   Copyright (c) 2011 Agnity, Inc. All rights reserved.
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
package com.genband.isup.enumdata;

// This class defines Redirecting Indicator used as CBA bit of Redirection Information 
// as defined in 3.45. 
// Octet format
//          8	7	6	5	4	3	2	1
// Octet-1	H	G	F	E	D	C	B	A
// Octet-2	P	O	N	M	L	K	J	I
// bits CBA:	redirecting indicator
// 0 0 0	no redirection (national use)                                                                                     #
// 0 0 1	call rerouted (national use)                                                                                       #
// 0 1 0	call rerouted, all redirection information presentation restricted (national use)       #
// 0 1 1	call diverted
// 1 0 0	call diverted, all redirection information presentation restricted
// 1 0 1	call rerouted, redirection number presentation restricted (national use)                  #
// 1 1 0	call diversion, redirection number presentation restricted (national use)                 #
// 1 1 1	spare
 


public enum RedirectingIndicatorEnum {

	NO_REDIRECTION(0), CALL_RE_ROUTED(1), CALL_RE_ROUTED_ALL_PRES_RESTRICTED(2), CALL_DIVERTED(3), 
	CALL_DIVERTED_ALL_PRES_RESTRICTED(4), CALL_RE_ROUTED_REDIRECTION_PRES_RESTRICTED(5),
	CALL_DIVERSION_REDIRECTION_PRES_RESTRICTED(6), SPARE(7);

	private int code;

	private RedirectingIndicatorEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static RedirectingIndicatorEnum fromInt(int num) {
		switch (num) {
		case 0: { return NO_REDIRECTION; }
		case 1: { return CALL_RE_ROUTED; }
		case 2: { return CALL_RE_ROUTED_ALL_PRES_RESTRICTED; }
		case 3: { return CALL_DIVERTED; }
		case 4: { return CALL_DIVERTED_ALL_PRES_RESTRICTED; }
		case 5: { return CALL_RE_ROUTED_REDIRECTION_PRES_RESTRICTED; }
		case 6: { return CALL_DIVERSION_REDIRECTION_PRES_RESTRICTED; }
		case 7: { return SPARE; }
		default: { return null; }
		}
	}
}