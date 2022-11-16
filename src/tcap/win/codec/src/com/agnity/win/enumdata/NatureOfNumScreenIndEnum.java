package com.agnity.win.enumdata;

/*
 * Enum for Nature of Number - Screening Indicator
 * @author Rajeev Arya
 *
 */
public enum NatureOfNumScreenIndEnum {
	/*
	 * Bits H G F E D C B A - Meaning 0 0 - User Provided, not screened 0 1 -
	 * User Provided, screening passed 1 0 - User provided Screening failed 1 1
	 * - Network Provided
	 */
	USER_PROVIDED_NOT_SCREENED(0), USER_PROVIDED_SCREEN_PASSED(1), USER_PROVIDED_SCREEN_FAILED(
			2), NETWORK_PROVIDED(3);

	private int code;

	private NatureOfNumScreenIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static NatureOfNumScreenIndEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return USER_PROVIDED_NOT_SCREENED;
		}
		case 1: {
			return USER_PROVIDED_SCREEN_PASSED;
		}
		case 2: {
			return USER_PROVIDED_SCREEN_FAILED;
		}
		case 3: {
			return NETWORK_PROVIDED;
		}
		default: {
			return null;
		}
		}
	}
}
