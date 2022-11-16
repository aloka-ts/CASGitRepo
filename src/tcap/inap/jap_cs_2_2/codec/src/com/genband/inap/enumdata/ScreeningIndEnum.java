package com.genband.inap.enumdata;

/**
 * Enum for Screening indicator
 * @author vgoel
 *
 */

public enum ScreeningIndEnum {

	/**
	 * 0-"user provided, not verified"
	 * 1-user provided, verified and passed
	 * 2-user provided, verified and failed
	 * 3-network provided
	 * 17 - AssistingSSPIPRoutingAddress (specific to SBTM)
	 */
	USER_PROVD_NOT_VERFD(0), USER_PROVD(1), USER_PROVD_FAILED(2), NETWORK_PROVD(3), ASSIST_SSPIP_ROUTE_ADDR(17);
	
	private int code;

	private ScreeningIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ScreeningIndEnum fromInt(int num) {
		switch (num) {
			case 0: { return USER_PROVD_NOT_VERFD; }
			case 1: { return USER_PROVD; }
			case 2: { return USER_PROVD_FAILED; }
			case 3: { return NETWORK_PROVD; }
			case 17: { return ASSIST_SSPIP_ROUTE_ADDR; }
			default: { return null; }
		}
	}
}
