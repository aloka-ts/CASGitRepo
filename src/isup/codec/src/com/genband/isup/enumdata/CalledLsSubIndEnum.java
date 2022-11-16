package com.genband.isup.enumdata;

/**
 * Enum for Event Indicator
 * @author vgoel
 *
 */
public enum CalledLsSubIndEnum {

	/**
	 *  0-NONE
	 *  1-Termination_LS
	 */
	
	NONE(0), TERMINATION_LS(1);
	 
	private int code;

	private CalledLsSubIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static CalledLsSubIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NONE	; }
		case 1: { return 	TERMINATION_LS	; }
		default: { return null; }
		}
	}
}
