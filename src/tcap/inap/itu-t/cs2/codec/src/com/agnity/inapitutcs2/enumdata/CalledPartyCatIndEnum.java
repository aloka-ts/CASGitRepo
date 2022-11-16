package com.agnity.inapitutcs2.enumdata;

/**
 * Enum for Called party's category indicator
 * @author Mriganka
 *
 */

public enum CalledPartyCatIndEnum {

	/**
	 *  0-no indication
	 *  1-ordinary subscriber
	 *  2-Payphone
	 *  3-spare 
	 */
	NO_INDICATION(0), ORDINARY_SUBSCRIBER(1), PAYPHONE(2), SPARE(3);
	 
	private int code;

	private CalledPartyCatIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static CalledPartyCatIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NO_INDICATION	; }
		case 1: { return 	ORDINARY_SUBSCRIBER	; }
		case 2: { return 	PAYPHONE	; }
		case 3: { return 	SPARE	; }
		default: { return null; }
		}
	}
}
