package com.genband.isup.enumdata;

/**
 * Enum for Additional Party's Category 1
 * @author rarya
 *
 */

public enum AdditionalPartyCat1Enum {
	
	/**
	 * 1- Train public telephone
	 * 2- pink public telephone
	 */
	
	SPARE(0), TRAIN_PUBLIC_TEL(1), PINK_PUBLIC_TEL(2);
	
	private int code;

	private AdditionalPartyCat1Enum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static AdditionalPartyCat1Enum fromInt(int num) {
		switch (num) {
			case 0: { return SPARE; }
			case 1: { return TRAIN_PUBLIC_TEL; }
			case 2: { return PINK_PUBLIC_TEL;  }
			default: { return SPARE; }
		}
	}
}

