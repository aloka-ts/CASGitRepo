package com.agnity.inapitutcs2.enumdata;


/**
 * Enum for Mobile Additional Party's Category 1
 * @author Mriganka
 *
 */

public enum MobileAdtnlPartyCat1Enum {

	/**
	 * 0-spare
	 * 1-mobile (automobile and portable phone service)
	 * 2-mobile (maritime telephone service)
	 * 3-mobile (in-flight telephone service)
	 * 4-mobile (pager)	
	 */
	SPARE(0), AUTO_PORTABLE_PHONE(1), MARITIME_TELEPHONE(2), INFLIGHT_TELEPHONE(3), PAGER(4), PHS_SRV(5);
	
	private int code;

	private MobileAdtnlPartyCat1Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static MobileAdtnlPartyCat1Enum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return AUTO_PORTABLE_PHONE; }
			case 2: { return MARITIME_TELEPHONE; }
			case 3: { return INFLIGHT_TELEPHONE; }
			case 4: { return PAGER; }
			case 5: { return PHS_SRV; }
			default: { return null; }
		}
	}
}
