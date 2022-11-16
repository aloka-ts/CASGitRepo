package com.genband.inap.enumdata;

/**
 * Enum for SSNIndicator
 * @author vgoel
 *
 */

public enum SSNIndicatorEnum {
	
	/**
	 * 0-SSN Not Present
	 * 1-SSN Present
	 */
	
	SSN_NOT_PRESENT(0), SSN_PRESENT(1);
	
	private int code;

	private SSNIndicatorEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static SSNIndicatorEnum fromInt(int num) {
		switch (num) {
			case 0: { return SSN_NOT_PRESENT; }
			case 1: { return SSN_PRESENT; }
			default: { return null; }
		}
	}
}
