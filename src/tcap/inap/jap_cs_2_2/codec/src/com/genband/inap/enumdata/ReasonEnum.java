package com.genband.inap.enumdata;

/**
 * Enum for Reason value
 * @author vgoel
 *
 */

public enum ReasonEnum {
	
	/**
	 * 0-Not used
	 * 1-Application timer expired
	 * 2-Abnormal procedure
	 * 3-Other related resources released
	 * 128->255-Network specific area
	 */
	
	NOT_USED(0), APP_TIMER_EXPIRED(1), ABNORMAL_PROCEDURE(2), OTHER_RESOURCE_RELEASED(3), NW_SPECIFIC_AREA(128);
	
	private int code;

	private ReasonEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static ReasonEnum fromInt(int num) {
		switch (num) {
			case 0: { return NOT_USED; }
			case 1: { return APP_TIMER_EXPIRED; }
			case 2: { return ABNORMAL_PROCEDURE; }
			case 3: { return OTHER_RESOURCE_RELEASED; }
			case 128: { return NW_SPECIFIC_AREA; }
			default: { return null; }
		}
	}
}
