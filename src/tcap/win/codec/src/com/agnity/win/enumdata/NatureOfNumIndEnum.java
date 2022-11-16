package com.agnity.win.enumdata;

/**
 * Enum for Nature of Number - Number Indicator
 * 
 * @author Rajeev Arya
 * 
 */
public enum NatureOfNumIndEnum {
	/*
	 * Bits H G F E D C B A - Meaning 0 - National. 1 - International.
	 */
	NATIONAL(0), INTERNATIONAL(1);

	private int code;

	private NatureOfNumIndEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static NatureOfNumIndEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return NATIONAL;
		}
		case 1: {
			return INTERNATIONAL;
		}
		default: {
			return null;
		}
		}
	}
}
