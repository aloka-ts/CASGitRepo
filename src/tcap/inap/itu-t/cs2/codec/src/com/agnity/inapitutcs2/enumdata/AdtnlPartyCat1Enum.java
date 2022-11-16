package com.agnity.inapitutcs2.enumdata;


/**
 * Enum for Additional Party's Category 1
 * @author Mriganka
 *
 */

public enum AdtnlPartyCat1Enum {

	/**
	 * 0-spare
	 * 1-train public telephone
	 * 2-pink public telephone
	 */
	SPARE(0), TRAIN_PUBLIC_TELEPHONE(1), PINK_PUBLIC_TELEPHONE(2);
	
	private int code;

	private AdtnlPartyCat1Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static AdtnlPartyCat1Enum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return TRAIN_PUBLIC_TELEPHONE; }
			case 2: { return PINK_PUBLIC_TELEPHONE; }
			default: { return null; }
		}
	}
}
