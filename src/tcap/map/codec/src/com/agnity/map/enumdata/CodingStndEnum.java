package com.agnity.map.enumdata;

/**
 * Enum for Coding Standard
 * @author vgoel
 *
 */

public enum CodingStndEnum {

	/**
	 *  0-ITU-T standardized coding
	 *  1-ISO/IEC standard 
	 *  2-national standard 
	 *  3-standard specific to identified location 
	 */
	ITUT_STANDARDIZED_CODING(0), ISO_IEC_STANDARD(1), NATIONAL_STANDARD(2), STANDARD_SPECIFIC_IDENTIFIED_LOCATION(3);
	 
	private int code;

	private CodingStndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static CodingStndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	ITUT_STANDARDIZED_CODING	; }
		case 1: { return 	ISO_IEC_STANDARD	; }
		case 2: { return 	NATIONAL_STANDARD	; }
		case 3: { return 	STANDARD_SPECIFIC_IDENTIFIED_LOCATION	; }
		default: { return null; }
		}
	}
}
