package com.genband.isup.enumdata;

/**
 * Enum for MLPP user indicator
 * @author vgoel
 *
 */
public enum MLPPUserIndEnum {

	/**
	 *  0-no indication
	 *  1-MLPP user
	 */
	
	NO_INDICATION(0), MLPP_USER(1);
	 
	private int code;

	private MLPPUserIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static MLPPUserIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NO_INDICATION	; }
		case 1: { return 	MLPP_USER	; }
		default: { return null; }
		}
	}
}
