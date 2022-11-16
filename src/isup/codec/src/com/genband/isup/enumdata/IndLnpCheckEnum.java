package com.genband.isup.enumdata;

/**
 * Enum for Event Indicator
 * @author vgoel
 *
 */
public enum IndLnpCheckEnum {

	/**
	 *  0-Not Checked
	 *  1-Checked
	 */
	
	NOT_CHECKED(0), CHECKED(1);
	 
	private int code;

	private IndLnpCheckEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static IndLnpCheckEnum fromInt(int num) {
		switch (num) {
		case 0: { return 	NOT_CHECKED	; }
		case 1: { return 	CHECKED	; }
		default: { return null; }
		}
	}
}
