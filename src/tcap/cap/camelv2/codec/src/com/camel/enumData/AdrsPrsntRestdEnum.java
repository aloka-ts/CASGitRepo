package com.camel.enumData;

/**
 * This enum represent the Address Presentation Restricted
 * indicator.
 * @author nkumar
 *
 */

public enum AdrsPrsntRestdEnum {
	
	/**
	 * 0-presentation allowed
	 * 1-presentation restricted
	 * 2-address not available
	 * 3-spare
	 */
	PRSNT_ALLWD(0), PRSNT_RESTD(1), ADRS_NOT_AVAIL(2) , SPARE(3);
	
	private int code;

	private AdrsPrsntRestdEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static AdrsPrsntRestdEnum fromInt(int num) {
		switch (num) {
			case 0: { return PRSNT_ALLWD; }
			case 1: { return PRSNT_RESTD; }
			case 2: { return ADRS_NOT_AVAIL; }
			case 3: { return SPARE; }
			default: { return null; }
		}
	}
}
