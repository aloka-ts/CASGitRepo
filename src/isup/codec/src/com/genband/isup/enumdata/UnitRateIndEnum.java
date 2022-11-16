package com.genband.isup.enumdata;

/**
 * Enum for Unit Rate Indicator
 * @author vgoel
 *
 */

public enum UnitRateIndEnum {

	/**
	 * 0-spare
	 * 1-reserved
	 * 252-100 yen
	 * 253-10 yen
	 * 254-no indication
	 */
	SPARE(0), RESERVED(1), YEN_100(252), YEN_10(253), NO_INDICATION(254);
	
	private int code;

	private UnitRateIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static UnitRateIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return RESERVED; }
			case 252: { return YEN_100; }
			case 253: { return YEN_10; }
			case 254: { return NO_INDICATION; }
			default: { return null; }
		}
	}
}
