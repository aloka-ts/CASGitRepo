package com.genband.isup.enumdata;

/**
 * Enum for Event Indicator
 * @author vgoel
 *
 */
public enum TerminationBypassIndEnum {

	/**
	 *  0-None
	 *  1-BypassConnection
	 */
	
	NONE(0), BYPASS_CONNECTION(1);
	 
	private int code;

	private TerminationBypassIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static TerminationBypassIndEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NONE	; }
		case 1: { return 	BYPASS_CONNECTION	; }
		default: { return null; }
		}
	}
}
