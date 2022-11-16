package com.agnity.map.enumdata;

/**
 * This enum represent  type of Pattern
 * for Alerting Pattern.
 * @author nkumar
 *
 */
public enum TypeOfPatrnEnum {

	/**
	 * 0-level
	 * 1-category
	 */
	LEVEL(0), CATEGORY(1) ;
	
	private int code;

	private TypeOfPatrnEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static TypeOfPatrnEnum fromInt(int num) {
		switch (num) {
			case 0: { return LEVEL; }
			case 1: { return CATEGORY; }
			default: { return null; }
		}
	}
}
