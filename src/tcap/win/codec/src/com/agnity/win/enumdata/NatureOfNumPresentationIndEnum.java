package com.agnity.win.enumdata;

/**
 * Enum for Nature of Number - Presentation Indicator
 * 
 * @author Rajeev Arya
 * 
 */

public enum NatureOfNumPresentationIndEnum {
	/*
	 * Bits H G F E D C B A - Meaning 0 - Presentation Allowed. 1 - Presentation
	 * Restricted .
	 */
	PRES_ALLOW(0), PRES_RESTRICTED(1);

	private int code;

	private NatureOfNumPresentationIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static NatureOfNumPresentationIndEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return PRES_ALLOW;
		}
		case 1: {
			return PRES_RESTRICTED;
		}
		default: {
			return null;
		}
		}
	}
}
