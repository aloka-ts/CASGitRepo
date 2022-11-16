package com.agnity.camelv2.enumdata;

/**
 * This enum represents Number Incomplete
 * indiactor.
 * @author nkumar
 *
 */
public enum NumINcomplteEnum {

	COMPLETE(0), INCOMPLETE(1);
	
	private int code;

	private NumINcomplteEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static NumINcomplteEnum fromInt(int num) {
		switch (num) {
			case 0: { return COMPLETE; }
			case 1: { return INCOMPLETE; }
			default: { return null; }
		}
	}
}
