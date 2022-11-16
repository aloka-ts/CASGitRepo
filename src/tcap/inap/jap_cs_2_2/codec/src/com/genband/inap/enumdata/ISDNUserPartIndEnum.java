package com.genband.inap.enumdata;

/**
 * Enum for ISDn User Part Indicator 
 * @author vgoel
 *
 */

public enum ISDNUserPartIndEnum {

	/**
	 * 0-ISDN user part not used all the way
	 * 1-ISDN user part used all the way
	 */
	ISDN_USER_PART_NOT_USED(0), ISDN_USER_PART_USED(1);
	
	private int code;

	private ISDNUserPartIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ISDNUserPartIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return ISDN_USER_PART_NOT_USED; }
			case 1: { return ISDN_USER_PART_USED; }
			default: { return null; }
		}
	}
	
}
