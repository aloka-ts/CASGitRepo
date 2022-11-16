package com.genband.inap.enumdata;

/**
 * Enum for National/International Call Indicator 
 * @author vgoel
 *
 */

public enum NatIntNatCallIndEnum {

	/**
	 * 0-call to be treated as a national call
	 * 1-call to be treated as a international call
	 */
	NATIONAL_CALL(0), INTERNATIONAL_CALL(1);
	
	private int code;

	private NatIntNatCallIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NatIntNatCallIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return NATIONAL_CALL; }
			case 1: { return INTERNATIONAL_CALL; }
			default: { return null; }
		}
	}
	
}
