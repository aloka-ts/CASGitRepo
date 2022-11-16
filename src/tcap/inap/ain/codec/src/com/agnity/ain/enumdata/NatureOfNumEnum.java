package com.agnity.ain.enumdata;

public enum NatureOfNumEnum {

	NATIONAL_NPR(0),
	INTERNATIONAL_NPR(1),
	NATIONAL_PR(2),
	INTERNATIONAL_PR(3);

	private int code;

	private NatureOfNumEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}

	public static NatureOfNumEnum fromInt(int num) {
		switch (num) {
		case 0: {
			return NATIONAL_NPR;
		}
		case 1: {
			return INTERNATIONAL_NPR;
		}
		case 2: {
			return NATIONAL_PR;
		}
		case 3: {
			return INTERNATIONAL_PR;
		}
		default: {
			return NATIONAL_NPR;
		}
		}
	}
}
