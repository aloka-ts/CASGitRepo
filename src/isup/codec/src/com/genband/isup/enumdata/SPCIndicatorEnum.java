package com.genband.isup.enumdata;

/**
 * Enum for SPCIndicator
 * @author vgoel
 *
 */

public enum SPCIndicatorEnum {
	
	/**
	 * 0-SPC Not Present
	 * 1-SPC Present
	 */
	
	SPC_NOT_PRESENT(0), SPC_PRESENT(1);
	
	private int code;

	private SPCIndicatorEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static SPCIndicatorEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPC_NOT_PRESENT; }
			case 1: { return SPC_PRESENT; }
			default: { return null; }
		}
	}
}
