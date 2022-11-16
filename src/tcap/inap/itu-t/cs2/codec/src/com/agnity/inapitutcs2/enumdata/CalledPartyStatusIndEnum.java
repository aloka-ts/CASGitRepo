package com.agnity.inapitutcs2.enumdata;

/**
 * Enum for Called party's status indicator
 * @author Mriganka
 *
 */

public enum CalledPartyStatusIndEnum {

	/**
	 *  0-no indication
	 *  1-subscriber free
	 *  2-connect when free (national use)
	 *  3-spare 
	 */
	NO_INDICATION(0), SUBSCRIBER_FREE(1), CONNECT_WHEN_FREE(2), SPARE(3);
	 
	private int code;

	private CalledPartyStatusIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static CalledPartyStatusIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NO_INDICATION	; }
		case 1: { return 	SUBSCRIBER_FREE	; }
		case 2: { return 	CONNECT_WHEN_FREE	; }
		case 3: { return 	SPARE	; }
		default: { return null; }
		}
	}
}
