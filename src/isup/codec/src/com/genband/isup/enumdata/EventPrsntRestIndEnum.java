package com.genband.isup.enumdata;

/**
 * Enum for Event presentation restricted indicator
 * @author vgoel
 *
 */
public enum EventPrsntRestIndEnum {

	/**
	 *  0-no indication
	 *  1-presentation restricted
	 */
	
	NO_INDICATION(0), PRESENTATION_RESTRICTED(1);
	 
	private int code;

	private EventPrsntRestIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static EventPrsntRestIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NO_INDICATION	; }
		case 1: { return 	PRESENTATION_RESTRICTED	; }
		default: { return null; }
		}
	}
}
