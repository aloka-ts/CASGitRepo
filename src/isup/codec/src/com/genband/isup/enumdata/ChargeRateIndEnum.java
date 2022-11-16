package com.genband.isup.enumdata;

/**
 * Enum for Charge Rate Indicator
 * @author vgoel
 *
 */

public enum ChargeRateIndEnum {

	/**
	 * 0-spare
	 * 1-en-bloc metering + seconds/10 yen
	 * 2-no charge/rate information
	 * 3-communications fee MBI + information fee rate "seconds/10 yen" + information fee non-charging time
	 * 4- communications fee MBI + information fee metering + information fee non-charging time
	 * 5-communications fee MBI
	 * 6-(initial) en-bloc metering + "seconds/10 yen" (general + public)
	 */
	SPARE(0), SECONDS_10_YEN(1), NO_RATE_INFO(2), INFO_FEE_RATE_SECONDS_10_YEN(3), INFO_FEE_METER(4), COMM_FEE_MBI(5), SECONDS_10_YEN_GENERAL_PUBLIC(6);
	
	private int code;

	private ChargeRateIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ChargeRateIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return SECONDS_10_YEN; }
			case 2: { return NO_RATE_INFO; }
			case 3: { return INFO_FEE_RATE_SECONDS_10_YEN; }
			case 4: { return INFO_FEE_METER; }
			case 5: { return COMM_FEE_MBI; }
			case 6: { return SECONDS_10_YEN_GENERAL_PUBLIC; }
			default: { return null; }
		}
	}
}
