package com.genband.isup.enumdata;

/**
 * Enum for Type Of Subaddress
 * @author vgoel
 *
 */
public enum TypeOfSubaddress {

	/**
	 * 0-NSAP (ITU-T Rec. X.213 [23] and ISO/IEC 8348 Add.2 [24])
	 * 1-Reserved
	 * 2-user Specified
	 */
	NSAP(0), RESERVED(1), USER_SPECIFIED(2);
	
	private int code;

	private TypeOfSubaddress(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static TypeOfSubaddress fromInt(int num) {
		switch (num) {
			case 0: { return NSAP; }
			case 1: { return RESERVED; }
			case 2: { return USER_SPECIFIED; }
			default: { return null; }
		}
	}
}
