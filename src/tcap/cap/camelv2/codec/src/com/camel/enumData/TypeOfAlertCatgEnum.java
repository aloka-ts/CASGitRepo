package com.camel.enumData;

/**
 * This enum represents alertingCategory for
 * alerting pattern.
 * @author nkumar
 *
 */
public enum TypeOfAlertCatgEnum {

	/**
	 * 0-category1
	 * 1-category2
	 * 2-category3
	 * 3-category4
	 * 4-category5
	 */
	CATEGORY1(0), CATEGORY2(1), CATEGORY3(2), CATEGORY4(3),CATEGORY5(4) ;
	
	private int code;

	private TypeOfAlertCatgEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static TypeOfAlertCatgEnum fromInt(int num) {
		switch (num) {
			case 0: { return CATEGORY1; }
			case 1: { return CATEGORY2; }
			case 2: { return CATEGORY3; }
			case 3: { return CATEGORY4; }
			case 4: { return CATEGORY5; }
			default: { return null; }
		}
	}
}
