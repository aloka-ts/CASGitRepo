package com.agnity.inapitutcs2.enumdata;

/**
 * Enum for Number Incomplete Indiactor
 * @author Mriganka
 *
 */

public enum NumIncmpltEnum {

	/**
	 * 0-Complete
	 * 1-Incomplete
	 */
	COMPLETE(0), INCOMPLETE(1);
	
	private int code;

	private NumIncmpltEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NumIncmpltEnum fromInt(int num) {
		switch (num) {
			case 0: { return COMPLETE; }
			case 1: { return INCOMPLETE; }
			default: { return null; }
		}
	}
}
