package com.agnity.win.enumdata;

public enum PreferredLanguageEnum {

	/*
	 * 
	 * Preferred Language (octet 1) Bits HG FE DC BA Value Meaning
	 * 
	 * 00 00 00 00 0 Unspecified. 00 00 00 01 1 English 00 00 00 10 2 French 00
	 * 00 00 11 3 Spanish 00 00 01 00 4 German 00 00 01 01 5 Portuguese
	 */

	UNSPECIFIED(0), ENGLISH(1), FRENCH(2), SPANISH(3), GERMAN(4), PORTUGESE(5);

	private int code;

	private PreferredLanguageEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static PreferredLanguageEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return UNSPECIFIED;
		}
		case 1: {
			return ENGLISH;
		}
		case 2: {
			return FRENCH;
		}
		case 3: {
			return SPANISH;
		}
		case 4: {
			return GERMAN;
		}
		case 5: {
			return PORTUGESE;
		}
		default: {
			return null;
		}
		}
	}

}
