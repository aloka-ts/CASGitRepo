package com.genband.isup.enumdata;

/**
 * Enum for Event Indicator
 * @author vgoel
 *
 */
public enum RepresentativeAreaCodeEnum {

	/**
	 *  0-Disabled
	 *  1-Enabled
	 */
	
	DISABLED(0), ENABLED(1);
	 
	private int code;

	private RepresentativeAreaCodeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static RepresentativeAreaCodeEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	DISABLED	; }
		case 1: { return 	ENABLED	; }
		default: { return null; }
		}
	}
}
