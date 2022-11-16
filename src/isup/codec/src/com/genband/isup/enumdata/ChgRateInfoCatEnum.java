package com.genband.isup.enumdata;

/**
 * Enum for Charging Rate Information Category
 * @author vgoel
 *
 */

public enum ChgRateInfoCatEnum {

	/**
	 * 0-spare
	 * 1-reserved
	 * 124-flexible charging rate indication (payphone)
	 * 125-flexible charging rate indication (ordinary calling subscriber)
	 * 126-no flexible charging rate information
	 */
	SPARE(0), RESERVED(1), PAYPHONE(124), ORDINARY_CALLING_SUBS(125), NO_RATE_INFO(126);
	
	private int code;

	private ChgRateInfoCatEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ChgRateInfoCatEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return RESERVED; }
			case 124: { return PAYPHONE; }
			case 125: { return ORDINARY_CALLING_SUBS; }
			case 126: { return NO_RATE_INFO; }
			default: { return null; }
		}
	}
}
