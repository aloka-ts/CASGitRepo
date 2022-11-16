package com.agnity.inapitutcs2.enumdata;

/**
 * Enum for ISDN Access Indicator
 * @author Mriganka
 *
 */

public enum ISDNAccessIndEnum {

	/**
	 * 0-originating access non-ISDN
	 * 1-originating access ISDN
	 */
	
	NON_ISDN(0), ISDN(1);
	
	private int code;

	private ISDNAccessIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ISDNAccessIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return NON_ISDN; }
			case 1: { return ISDN; }
			default: { return null; }
		}
	}
}
