package com.genband.isup.enumdata;

/**
 * Enum for ISDN user part preference indicator
 * @author vgoel
 *
 */

public enum ISDNUserPartPrefIndEnum {
	
	/**
	 * 0-ISDN user part preferred all the way
	 * 1-ISDN user part not required all the way
	 * 2-ISDN user part required all the way
	 * 3-spare
	 */
	
	ISDN_PREFERRED(0), ISDN_NOT_REQUIRED(1), ISDN_REQUIRED(2), SPARE(3);
	
	private int code;

	private ISDNUserPartPrefIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ISDNUserPartPrefIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return ISDN_PREFERRED; }
			case 1: { return ISDN_NOT_REQUIRED; }
			case 2: { return ISDN_REQUIRED; }
			case 3: { return SPARE; }
			default: { return null; }
		}
	}
}
