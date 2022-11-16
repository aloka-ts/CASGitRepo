package com.genband.inap.enumdata;

/**
 * Enum for Digit Category
 * @author vgoel
 *
 */

public enum DigitCatEnum {

	/**
	 * 0: Reserved for account code
	 * 1: Reserved for authentication code
	 * 2: Reserved for private network travelling class mark
	 * 3: Reserved for business communication group identifier
	 * 4: Reserved for domestic use
	 * 30: Correlation ID
	 * 31: Reserved for enhancement
	 */
	RESERVED_ACCOUNT_CODE(0), RESERVED_AUTHENTICATION_CODE(1), RESERVED_PRIVATE_NW(2), RESERVED_BUSINESS_COMM(3), 
	RESERVED_DOMESTIC_USE(4), CORRELATION_ID(30), RESERVED_ENHANCEMENT(31);

	private int code;

	private DigitCatEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static DigitCatEnum fromInt(int num) {
		switch (num) {
			case 0: { return RESERVED_ACCOUNT_CODE; }
			case 1: { return RESERVED_AUTHENTICATION_CODE; }
			case 2: { return RESERVED_PRIVATE_NW; }
			case 3: { return RESERVED_BUSINESS_COMM; }
			case 4: { return RESERVED_DOMESTIC_USE; }
			case 30: { return CORRELATION_ID; }
			case 31: { return RESERVED_ENHANCEMENT; }
			default: { return null; }
		}
	}
}
