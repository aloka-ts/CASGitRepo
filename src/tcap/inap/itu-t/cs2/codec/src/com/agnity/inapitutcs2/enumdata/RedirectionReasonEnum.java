package com.agnity.inapitutcs2.enumdata;

/**
 * Enum for RedirectionReason value
 * @author Mriganka
 *
 */

public enum RedirectionReasonEnum {
	
	/**
	 * 0-Spare
	 * 1-Reserved for national use
	 * 126-Roaming
	 */
	
	SPARE(0), RESERVERD(1), ROAMING(126);
	
	private int code;

	private RedirectionReasonEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static RedirectionReasonEnum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return RESERVERD; }
			case 126: { return ROAMING; }
			default: { return null; }
		}
	}
}
