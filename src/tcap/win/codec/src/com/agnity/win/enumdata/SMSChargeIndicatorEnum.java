package com.agnity.win.enumdata;

public enum SMSChargeIndicatorEnum {

	/*
           * SMSChargeIndicator(octet 1)
         * 	  Bits      HG FE DC BA   Value    Meaning 
		 * 				00 00 00 00 	0 		Not Used
		 * 				00 00 00 01 	1 		No Charge
		 * 				00 00 00 10 	2 		Charge Original Originator
		 * 				00 00 00 11 	3 		Charge Original Destination
		 				XX XX XX XX				Other values are reserved.
		 */
	
	NOT_USED(0), NO_CHARGE(1), CHARGE_ORIGINAL_ORIGINATOR(2), CHARGE_ORIGINAL_DESTINATION(3);

	private int code;

	private SMSChargeIndicatorEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static SMSChargeIndicatorEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return NOT_USED;
		}
		case 1: {
			return NO_CHARGE;
		}
		case 2: {
			return CHARGE_ORIGINAL_ORIGINATOR;
		}
		case 3: {
			return CHARGE_ORIGINAL_DESTINATION;
		}
		default: {
			return null;
		}
		}
	}

}
