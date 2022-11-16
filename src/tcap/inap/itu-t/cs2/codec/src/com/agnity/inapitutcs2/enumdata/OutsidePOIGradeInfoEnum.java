package com.agnity.inapitutcs2.enumdata;

/**
 * Enum for outside POI-grade information
 * @author Mriganka
 *
 */

public enum OutsidePOIGradeInfoEnum {
	
	/**
	 * 0-no indication
	 * 1-level 1
	 * 2-level 2
	 * 3-spare
	 */
	
	NO_INDICATION(0), LEVEL_1(1), LEVEL_2(2), SPARE(3);
	
	private int code;

	private OutsidePOIGradeInfoEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static OutsidePOIGradeInfoEnum fromInt(int num) {
		switch (num) {
			case 0: { return NO_INDICATION; }
			case 1: { return LEVEL_1; }
			case 2: { return LEVEL_2; }
			case 3: { return SPARE; }
			default: { return null; }
		}
	}
}
