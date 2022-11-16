package com.genband.isup.enumdata;

/**
 * Enum for Charged Party Type
 * @author vgoel
 *
 */

public enum ChargedPartyTypeEnum {

	/**
	 * 0-Calling party
	 * 1-spare
	 */
	CALLING_PARTY(0), SPARE(1);
	
	private int code;

	private ChargedPartyTypeEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ChargedPartyTypeEnum fromInt(int num) {
		switch (num) {
			case 0: { return CALLING_PARTY; }
			case 1: { return SPARE; }
			default: { return null; }
		}
	}
}
