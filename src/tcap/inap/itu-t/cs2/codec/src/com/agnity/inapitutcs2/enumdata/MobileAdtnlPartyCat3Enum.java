package com.agnity.inapitutcs2.enumdata;


/**
 * Enum for Mobile Additional Party's Category 3
 * @author Mriganka
 *
 */

public enum MobileAdtnlPartyCat3Enum {

	/**
	 * 0->255-reserved for national use
	 */
	RESERVED(0);
	
	private int code;

	private MobileAdtnlPartyCat3Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static MobileAdtnlPartyCat3Enum fromInt(int num) {
		switch (num) {
			case 0: { return RESERVED; }
			default: { return null; }
		}
	}
}
