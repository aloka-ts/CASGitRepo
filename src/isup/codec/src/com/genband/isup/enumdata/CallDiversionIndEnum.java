package com.genband.isup.enumdata;

/**
 * Enum for Call diversion may occur indicator
 * @author vgoel
 *
 */
public enum CallDiversionIndEnum {

	/**
	 *  0-no indication
	 *  1-call diversion may occur
	 */
	
	NO_INDICATION(0), CALL_DIVERSION(1);
	 
	private int code;

	private CallDiversionIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static CallDiversionIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NO_INDICATION	; }
		case 1: { return 	CALL_DIVERSION	; }
		default: { return null; }
		}
	}
}
