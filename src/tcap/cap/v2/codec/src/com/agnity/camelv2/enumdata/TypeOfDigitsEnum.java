package com.agnity.camelv2.enumdata;

public enum TypeOfDigitsEnum {
	
	/**
	 * 0-reserved for account code
	 * 1-reserved for authorisation code
	 * 2-reserved for private networking travelling class mark
	 * 3-reserved for business communication group identity
	 * 4-30-reserved for national use
	 * 31-reserved for extension
	 */
	RESERVED_ACCOUNT_CODE(0), RESERVED_AUTH_CODE(1), RESERVED_PRIVATE_NW_TRAVEL_CLASS(2), RESERVED_BUSINESS_COMM_GUP_ID(3);
	
	private int code;

	private TypeOfDigitsEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static TypeOfDigitsEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	RESERVED_ACCOUNT_CODE	; }
		case 1: { return 	RESERVED_AUTH_CODE	; }
		case 2: { return 	RESERVED_PRIVATE_NW_TRAVEL_CLASS	; }
		case 3: { return 	RESERVED_BUSINESS_COMM_GUP_ID	; }
		default: { return null; }
		}
	}
	
}