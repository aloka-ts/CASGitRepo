package com.genband.isup.enumdata;


/**
 * Enum for Nature of address indicator
 * @author vgoel
 *
 */
public enum NatureOfAddEnum {

	/**
	 * 0-spare
	 * 1-subscriber number (national use)
	 * 2-unknown (national use)
	 * 3-national (significant) number (national use)
	 * 4-international number
	 * 6->111- spare
	 * 112->126-reserved for national use as per ITUT
	 * Following as per T11.113 section 3.6 being used in ANSI ISUP
	 * 113 - subscriber number,operator requested
	 * 114 -national number, operator requested 
	 * 115 - international number, operator requested
	 * 116 - no number present, operator requested
	 * 117 - no number present, cut through call to carrier
	 * 118 - 950+ call from local exchange carrier public station, 
	 *       hotel/motel, non-exchnage access end office
	 * 119 - Test line test code 
	 * 
	 * 126 Network Specific Number
	 * 127-spare
	 */
	SPARE(0), SUBS_NO(1), UNKNOWN(2), NATIONAL_NO(3), INTER_NO(4), SUBS_NO_OPERATOR_REQ(113), NATIONAL_NO_OPERATOR_REQ(114), 
	INTER_NO_OPERATOR_REQ(115), NO_NUM_PRSNT_OPERATOR_REQ(116), NO_NUM_PRSNT_CUT_TRHOUGH(117), LOCAL_EXCHANGE_CARRIER(118), 
	TEST_LINE_TEST_CODE(119), NETWORK_SPEC_NO(126);

	private int code;

	private NatureOfAddEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static NatureOfAddEnum fromInt(int num) {
		switch (num) {
		case 0:   { return SPARE;       }
		case 1:   { return SUBS_NO;     }
		case 2:   { return UNKNOWN;     }
		case 3:   { return NATIONAL_NO; }
		case 4:   { return INTER_NO;    }
		case 113: { return SUBS_NO_OPERATOR_REQ;       }
		case 114: { return NATIONAL_NO_OPERATOR_REQ;   }
		case 115: { return INTER_NO_OPERATOR_REQ;      }
		case 116: { return NO_NUM_PRSNT_OPERATOR_REQ; }
		case 117: { return NO_NUM_PRSNT_CUT_TRHOUGH;   }
		case 118: { return LOCAL_EXCHANGE_CARRIER;     }
		case 119: { return TEST_LINE_TEST_CODE;        }
		case 126: { return NETWORK_SPEC_NO;            }
		default:  { return null;       }
		}
	}
}
