package com.agnity.win.enumdata;

/**
 * Enum for Nature of Number - Number Availability Indicator
 * 
 * @author Rajeev Arya
 * 
 */
public enum NatureOfNumAvailIndEnum {
	/*
	 * Bits H G F E D C B A - Meaning 0 - Number is vailable 1 - Number is not
	 * available
	 */
	NUM_AVAILABLE(0), NUM_UNAVAILABLE(1);

	private int code;

	private NatureOfNumAvailIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static NatureOfNumAvailIndEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return NUM_AVAILABLE;
		}
		case 1: {
			return NUM_UNAVAILABLE;
		}
		default: {
			return null;
		}
		}
	}
}
