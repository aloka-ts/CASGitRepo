package com.genband.inap.enumdata;

/**
 * Enum for Information discrimination indicator
 * @author vgoel
 *
 */

public enum InfoDiscriminationIndiEnum {
	
	/**
	 * 0-MA Code
	 * 1-CA Code
	 * 2->127-spare	 
	 */
	MA_CODE(0), CA_CODE(1), SPARE(2);
	
	private int code;

	private InfoDiscriminationIndiEnum(int c) {
		code = c;
	}

	public int getCode() {
		return code;
	}
	
	public static InfoDiscriminationIndiEnum fromInt(int num) {
		switch (num) {
			case 0: { return MA_CODE; }
			case 1: { return CA_CODE; }
			case 2: { return SPARE; }
			default: { return null; }
		}
	}
	

}
