package com.agnity.win.enumdata;

/**
 * This class defines the Type Of NonASNDigitsType based on the definition provided
 * in TIA-EIA-41-D, section 6.5.3.2
 * 
 * @author rarya
 */
public enum TypeOfDigitsEnum {
	/*
	 * Type of NonASNDigitsType (octet 1, bits A-H) H G F E D C B A Value Meaning 0
	 * 0 0 0 0 0 0 0 0 Not Used. 0 0 0 0 0 0 0 1 1 Dialed Number or Called Party
	 * Number. 0 0 0 0 0 0 1 0 2 Calling Party Number. 0 0 0 0 0 0 1 1 3 Caller
	 * Interaction. These are the digits dialed by a user in response to a
	 * prompt (not used in this Standard). 0 0 0 0 0 1 0 0 4 Routing Number.
	 * This number is used to steer a call towards its ultimate destination. 0 0
	 * 0 0 0 1 0 1 5 Billing Number. This is the number to use for ANI, Charge
	 * Number or other recording purposes. 0 0 0 0 0 1 1 0 6 Destination Number.
	 * This is the network address of the called party. 0 0 0 0 0 1 1 1 7 LATA
	 * (not used in this Standard). 0 0 0 0 1 0 0 0 8 Carrier. In North America
	 * the three, four, or five digits represent an interexchange or
	 * international carrier. X X X X X X X X - Other values are reserved.
	 */
	SPARE(0), DIALED_NUM(1), CALLING_PTY_NUM(2), CALLER_INTERACTION(3), ROUTING_NUM(
			4), BILLING_NUM(5), DESTINATION_NUM(6), LATA(7), CARRIER(8);

	private int code;

	private TypeOfDigitsEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static TypeOfDigitsEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SPARE;
		}
		case 1: {
			return DIALED_NUM;
		}
		case 2: {
			return CALLING_PTY_NUM;
		}
		case 3: {
			return CALLER_INTERACTION;
		}
		case 4: {
			return ROUTING_NUM;
		}
		case 5: {
			return BILLING_NUM;
		}
		case 6: {
			return DESTINATION_NUM;
		}
		case 7: {
			return LATA;
		}
		case 8: {
			return CARRIER;
		}
		default: {
			return null;
		}
		}
	}
}
