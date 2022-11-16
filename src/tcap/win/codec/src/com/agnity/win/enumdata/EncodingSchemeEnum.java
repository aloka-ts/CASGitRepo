package com.agnity.win.enumdata;

/**
 * This class defines the Encoding Scheme of NonASNDigitsType based on the
 * definition provided in TIA-EIA-41-D, section 6.5.3.2
 * 
 * @author rarya
 */

public enum EncodingSchemeEnum {

	/*
	 * Encoding (octet 3, bits A-D) H G F E D C B A Value Meaning 0 0 0 0 0 Not
	 * used. 0 0 0 1 1 BCD (see Digit definition below). 0 0 1 0 2 IA5. The
	 * International Alphabet 5 as defined in ITU-T Rec. T.50. (also known as
	 * the International Reference Alphabet (IRA)). 0 0 1 1 3 Octet string. This
	 * is used for IP and SS7 addresses. X X X X - Other values are reserved.
	 */
	SPARE(0), BCD(1), IA5(2), OCTET_STRING(3);

	private int code;

	private EncodingSchemeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static EncodingSchemeEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return SPARE;
		}
		case 1: {
			return BCD;
		}
		case 2: {
			return IA5;
		}
		case 3: {
			return OCTET_STRING;
		}
		default: {
			return null;
		}
		}
	}
}
