package com.genband.isup.enumdata;

/**
 * Enum for EncodingScheme 
 * @author vgoel
 *
 */

public enum EncodingSchemeEnum {

	/**
	 * 0-BCD even: (even number of digits)
	 * 1-BCD odd: (odd number of digits)
	 * 2-IA5 character
	 * 3-binary coded
	 * 4-spare
	 * 5-spare
	 * 6-spare
	 * 7-spare
	 */
	BCD_EVEN(0), BCD_ODD(1), IA5_CHAR(2), BINARY_CODED(3), SPARE(4);
	
	private int code;

	private EncodingSchemeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static EncodingSchemeEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	BCD_EVEN	; }
		case 1: { return 	BCD_ODD	; }
		case 2: { return 	IA5_CHAR	; }
		case 3: { return 	BINARY_CODED	; }
		case 4: { return 	SPARE	; }
		default: { return null; }
		}
	}
	
}
