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

// This class defined OriginalRedirectionReason used as HGFE bit of Redirection Information 
// as defined in 3.45. 
// Octet format
//          8	7	6	5	4	3	2	1
// Octet-1	H	G	F	E	D	C	B	A
// Octet-2	P	O	N	M	L	K	J	I
// bit    HGFE:	original redirection reason
// 0 0 0 0	unknown/not available
// 0 0 0 1	user busy (national use)
// 0 0 1 0	no reply (national use)
// 0 0 1 1	unconditional (national use)
// 0 1 0 0 call diversion
// 0 1 0 1	Response call deflection in a shake 
// 0 1 1 0	Mobile terminal called restricted    
// remaining SPare


public enum OriginalRedirectionReasonEnum {

	UNKNOWN(0), USER_BUSY(1), NO_REPLY(2), UNCONDITIONAL(3), CALL_DIVERSION(4), CALL_DEFLECTION(5),
	MOBILE_TERMINAL_CALL_RESTRICTED(6), SPARE_7(7), SPARE_8(8), SPARE_9(9), SPARE_10(10),SPARE_11(11),
	SPARE_12(12), SPARE_13(13), SPARE_14(14), SPARE_15(15);

	private int code;

	private OriginalRedirectionReasonEnum (int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static OriginalRedirectionReasonEnum fromInt(int num) {
		switch (num) {
		case 0: { return UNKNOWN;         }
		case 1: { return USER_BUSY;       }
		case 2: { return NO_REPLY;        }
		case 3: { return UNCONDITIONAL;   }
		case 4: { return CALL_DIVERSION;  }
		case 5: { return CALL_DEFLECTION; }
		case 6: { return MOBILE_TERMINAL_CALL_RESTRICTED; }
		case 7: { return SPARE_7;   }
		case 8: { return SPARE_8;   }
		case 9: { return SPARE_9;   }
		case 10: { return SPARE_10; }
		case 11: { return SPARE_11; }
		case 12: { return SPARE_12; }
		case 13: { return SPARE_13; }
		case 14: { return SPARE_14; }
		case 15: { return SPARE_15; }
		default: { return null;     }
		}
	}
}
